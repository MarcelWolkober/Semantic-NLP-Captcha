import os
import csv
from pathlib import Path
import pandas as pd
import numpy as np
from scipy import stats
import re
import random

datasets = ['dwug_en', 'dwug_de']
input_path = '../../../DWUG/'
output_path = 'data_output'
Path(output_path).mkdir(parents=True, exist_ok=True)

undesired_judgments = ['0.0']
undesired_annotators = []
undesired_keys = ['identifier_system', 'project', 'lang', 'user', 'pos', 'date', 'grouping', 'description',
                  'indexes_target_sentence',
                  'context_tokenized', 'indexes_target_token_tokenized', 'indexes_target_sentence_tokenized',
                  'context_lemmatized', 'context_pos']


def write_csv(file_path, data, keys):
    with open(file_path, 'w', encoding="utf-8") as f:
        header = '\t'.join(keys)
        f.write(header)
        f.write('\n')
        for row in data:
            row = {k: ('' if v is None else v) for k, v in row.items()}
            line = '\t'.join([str(v) for v in row.values()])
            f.write(line)
            f.write('\n')


def read_csv(file_path):
    return load_csv(file_path, [])


def load_csv(file_path, _undesired_keys):
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)

        rows = [row for row in reader]
        filtered_rows = []
        for row in rows:
            row_out = {key: val for key, val in row.items() if key not in _undesired_keys}
            if 'lemma' in row_out.keys():
                row_out['lemma'] = row_out['lemma'].split('_')[0]
            filtered_rows.append(row_out)
        return filtered_rows


def generate_pairs(uses, judgments_df_filtered):
    if (judgments_df_filtered.empty):
        return []

    pairs = []
    uses_dict = {use['identifier']: use for use in uses}
    lemma = uses[0]['lemma']
    try:
        df = judgments_df_filtered.groupby(['lemma']).get_group((lemma,))
    except KeyError:
        print('Could not find lemma in judgments:', lemma)
        return []

    for i in df.index:

        judgment_median = str(df.loc[i]['median_label'])
        # print('Median judgment:', judgment_median)

        use1 = uses_dict.get(df.loc[i]['identifier1'])
        use2 = uses_dict.get(df.loc[i]['identifier2'])
        if use1 is not None and use2 is not None:
            pair = {
                'lemma': use1['lemma'],
                'identifier1': use1['identifier'],
                'identifier2': use2['identifier'],
                'context1': use1['context'],
                'context2': use2['context'],
                'indexes_target_token1': use1['indexes_target_token'],
                'indexes_target_token2': use2['indexes_target_token'],
                'judgment': judgment_median,
            }
            pairs.append(pair)
        else:
            print('Could not find use for judgment', df.loc[i])

    return pairs


def generate_and_write_pairs_for_dataset(dataset):
    dataset_path = os.path.join(input_path, dataset, 'data')
    print('Generating and writing pairs for whole dataset:', dataset_path, ' ...')

    judgments_aggregated = aggregate_judgments_df(load_judgments_df_from_source_datasets())
    judgments_filtered = filter_aggregated_judgments(judgments_aggregated, False)
    pairs = []

    for p in Path(dataset_path).glob('*/'):
        uses_file_path = os.path.join(str(p), 'uses.csv')

        pairs.extend(generate_pairs(load_csv(uses_file_path, undesired_keys), judgments_filtered))

    pairs[0]['count_dropped_pairs'] = judgments_filtered['count_dropped_pairs'].iloc[0]
    pairs[0]['count_all_pairs'] = judgments_filtered['count_all_pairs'].iloc[0]

    output_path_lemma = os.path.join(output_path, 'pairs_whole_dataset')
    Path(output_path_lemma).mkdir(parents=True, exist_ok=True)
    write_csv(os.path.join(output_path_lemma, dataset + '_pairs_challenge.csv'), pairs,
              pairs[0].keys() if pairs else [])


# Bundles pairs into challenges of #number_of_pairs_per_challenge pairs, TODO: maybe make challenge permutation random
def generate_and_write_pair_challenges(path_to_pairs_file, number_of_pairs_per_challenge=4):
    pairs = read_csv(path_to_pairs_file)
    challenges = []
    header = ['id', 'pair1', 'pair2', 'pair3', 'pair4']
    for i in range(0, pairs.__len__(), number_of_pairs_per_challenge):
        challenge_pairs = pairs[i:i + number_of_pairs_per_challenge]
        challenge = {'string_id': i}
        lemma1 = challenge_pairs[0]['lemma']
        lemma_end = challenge_pairs[len(challenge_pairs) - 1]['lemma']
        if not lemma1 == lemma_end:
            continue
        string_id = ''
        for j in range(1, len(challenge_pairs) + 1):
            local_pair = challenge_pairs[j - 1]
            challenge['usage' + str(j)] = local_pair

            string_id += local_pair['identifier1'] + '-' + local_pair['identifier2'] + '|'

        challenge['string_id'] = string_id
        challenges.append(challenge)

    write_csv(path_to_pairs_file.replace('_pairs', '_pair_challenges_' + str(number_of_pairs_per_challenge)),
              challenges, header)


def generate_and_write_pairs_separate():
    print('Generating and writing pairs for challenge ...')

    judgments_aggregated = aggregate_judgments_df(load_judgments_df_from_source_datasets())
    judgments_filtered = filter_aggregated_judgments(judgments_aggregated, True)

    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')

            lemma = os.path.basename(lemma_path).split('_')[0]

            pairs = generate_pairs(load_csv(uses_file_path, undesired_keys), judgments_filtered)

            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'pairs_challenge.csv'), pairs, pairs[0].keys() if pairs else [])


def join_list_challenges(input_path_folder):
    all_list_challenges = []

    Path(input_path_folder).mkdir(parents=True, exist_ok=True)

    output_path_folder = output_path + '/list_challenges_whole_dataset/'
    Path(output_path_folder).mkdir(parents=True, exist_ok=True)

    file_names = os.listdir(input_path_folder)

    for file_name in file_names:
        all_list_challenges.extend(read_csv(os.path.join(input_path_folder, file_name)))

    write_csv(os.path.join(output_path_folder, 'dwug_en_list_challenge.csv'), all_list_challenges,
              all_list_challenges[0].keys())


# Strict if min 2 annotators,  to_find only used if !strict and to_find is the judgment to find in the list
# joined if whole dataset as one file
def generate_and_write_list_challenge(strict=True, to_find=4.0, joined=True):
    print('Generating and writing lists for challenge, strict =', strict, ' ...')

    judgments_aggregated = aggregate_judgments_df(load_judgments_df_from_source_datasets())
    judgments = filter_aggregated_judgments(judgments_aggregated)

    whole_list_challenges = []

    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')

            lemma = os.path.basename(lemma_path).split('_')[0]

            uses = load_csv(uses_file_path, undesired_keys)

            print('Generating list of challenges for lemma: ', lemma, ' ...')

            local_challenges = []

            for usage in uses:
                local_list_challenge = find_practical_reference_usages(usage, judgments, strict, to_find)
                if local_list_challenge['count'] >= 3:
                    local_challenges.append(local_list_challenge)

            if local_challenges.__len__() == 0:
                print('No list challenges possible for lemma ', lemma)
                continue

            if not joined:
                list_challenge_header = local_challenges[0].keys() if local_challenges else []
                output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
                file_name = 'list_challenges_filtered.csv'
                if not strict:
                    file_name = 'random_challenges_filtered.csv'

                Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

                write_csv(os.path.join(output_path_lemma, file_name), local_challenges, list_challenge_header)
            else:
                whole_list_challenges.extend(local_challenges)

        list_challenge_header = whole_list_challenges[0].keys() if whole_list_challenges else []
        output_path_lemma = os.path.join(output_path, 'list_challenges_whole_dataset')
        Path(output_path_lemma).mkdir(parents=True, exist_ok=True)
        file_name = dataset + '_list_challenges_filtered.csv'
        if not strict:
            file_name = dataset + '_random_challenges_filtered.csv'

        Path(output_path_lemma).mkdir(parents=True, exist_ok=True)
        write_csv(os.path.join(output_path_lemma, file_name), whole_list_challenges, list_challenge_header)
        print('List challenges generated. Save to:', os.path.join(output_path_lemma, file_name))


# Get one usage with 4 and 1 judgment for random challenge, that is when not strict
def find_practical_reference_usages(usage, judgments_df, strict=True, to_find=4.0):
    judgment_to_have = [1.0, 2.0, 3.0, 4.0] if strict else [to_find]
    list_challenge = {
        'lemma': usage['lemma'],
        'identifier_ref': usage['identifier'],
        'identifier1': None,
        'judgment1': None,
        'identifier2': None,
        'judgment2': None,
        'identifier3': None,
        'judgment3': None,
        'identifier4': None,
        'judgment4': None,
        'order': None,
        'to_find': None,
        'count': 0
    }

    local_order = {}

    lemma = usage['lemma']
    df_grouped = judgments_df.groupby(['lemma']).get_group((lemma,))
    df = df_grouped[
        (df_grouped["identifier1"] == usage['identifier']) | (df_grouped["identifier2"] == usage['identifier'])]

    df_indexes_random = list(df.index)
    random.shuffle(df_indexes_random)
    index = 1
    random_order = [1, 2, 3, 4]
    random.shuffle(random_order)
    # For each judgment, check if usage is fitting
    for i in df_indexes_random:
        judgment = df.loc[i]
        # print('Processing judgment', judgment)

        judgment_median = judgment['median_label']
        if strict and judgment_median not in judgment_to_have:
            continue
        elif not strict and judgment_median not in judgment_to_have:
            continue

        referenced_usage = judgment['identifier2'] if usage['identifier'] == judgment['identifier1'] else \
            judgment['identifier1']

        list_challenge['identifier' + str(random_order[index - 1])] = referenced_usage
        list_challenge['judgment' + str(random_order[index - 1])] = str(judgment_median)
        local_order[referenced_usage] = judgment_median
        list_challenge['count'] += 1

        if strict:
            judgment_to_have.remove(judgment_median)
        elif index == 1:
            list_challenge['to_find'] = referenced_usage
            judgment_to_have = [1.0, 2.0, 3.0, 4.0]
            judgment_to_have.remove(to_find)

        index += 1
        if len(judgment_to_have) == 0 or index > 4:
            break

    if strict:
        list_challenge['order'] = [k for k, v in sorted(local_order.items(), key=lambda item: item[1], reverse=True)]

    return list_challenge


def generate_and_write_random_challenge():
    generate_and_write_list_challenge(strict=False, to_find=4.0)


def find_random_reference_usages(usage, judgments_df):
    return find_practical_reference_usages(usage, judgments_df, False)


def remove_judgments_from_pairs(pairs):
    for pair in pairs:
        pair.pop('judgment', None)

    return pairs


def load_judgments_df_from_source_datasets():
    # Load new datasets into data frame
    df_judgments = pd.DataFrame()
    print('Loading judgments from Datasets ...')
    for dataset in datasets:
        for p in Path(input_path + '/' + dataset + '/data').glob('*/judgments.csv'):
            # print(p)
            df = pd.read_csv(p, delimiter='\t', quoting=3, na_filter=False)
            df['dataset'] = dataset
            df_judgments = pd.concat([df_judgments, df])

    return df_judgments


def load_and_write_uses_judgments(judgments_strict_filtered=False):
    df_judgments = pd.DataFrame()
    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            judgments_file_path = os.path.join(str(p), 'judgments.csv')

            lemma = os.path.basename(lemma_path).split('_')[0]

            uses = load_csv(uses_file_path, undesired_keys)
            # df = pd.read_csv(judgments_file_path, delimiter='\t', quoting=3, na_filter=False)
            # df['dataset'] = dataset
            # df_judgments = pd.concat([df_judgments, df])
            #
            # judgments_df_filtered = filter_aggregated_judgments(aggregate_judgments_df(df_judgments=df_judgments),
            #                                                     strict=judgments_strict_filtered)
            #
            # judgments = judgments_df_filtered.groupby(['lemma']).get_group((lemma,))
            #
            # judgments.to_csv(os.path.join(output_path_lemma, 'judgments.csv'), sep='\t', na_rep='',
            #                  quoting=csv.QUOTE_NONE)

            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'uses.csv'), uses, uses[0].keys() if uses else [])


def aggregate_judgments_df(df_judgments):
    # Get all annotators
    annotators = df_judgments.annotator.unique()
    # display(annotators)

    # Get aggregated data as instance versus annotator
    df_judgments[['identifier1', 'identifier2']] = np.sort(df_judgments[['identifier1', 'identifier2']],
                                                           axis=1)  # sort within pairs to be able to aggregate
    df_judgments_pair_vs_ann = pd.DataFrame()
    for annotator in annotators:
        judgments_annotator = df_judgments[df_judgments['annotator'] == annotator][
            ['identifier1', 'identifier2', 'lemma', 'dataset', 'judgment']].rename(columns={'judgment': annotator},
                                                                                   inplace=False)
        df_judgments_pair_vs_ann = pd.concat([df_judgments_pair_vs_ann, judgments_annotator])

    # Discard duplicates
    df = df_judgments_pair_vs_ann.groupby(
        ['identifier1', 'identifier2']).first().reset_index()

    annotator_keys = [key for key in df.keys() if key.startswith('annotator')]
    # print('Annotator keys:', annotator_keys)

    df['labels'] = df[annotator_keys].values.tolist()
    df['median_label'] = df[annotator_keys].median(axis=1)

    return df


def filter_aggregated_judgments(df, strict=False):
    annotator_keys = [key for key in df.keys() if key.startswith('annotator')]
    df_new = df.copy()

    # Filter out '0.0' judgments
    df_new = df_new[~df_new['labels'].apply(lambda x: 0.0 in x)]

    # Filter out disagreements
    df_new = df_new[~df_new['median_label'].apply(lambda x: x not in [1.0, 2.0, 3.0, 4.0])]

    df_new['non_nan_count'] = df_new[annotator_keys].count(axis=1)

    #

    if strict:
        # Drop where only 1 annotator
        df_new = df_new[df_new['non_nan_count'] >= 2]

    # remove '_nn' from lemma
    df_new['lemma'] = df_new['lemma'].apply(lambda x: x.split('_')[0])
    # print(df.keys())

    # Count amount of dropped judgments
    df_new['count_dropped_pairs'] = len(df.index) - len(df_new.index)
    df_new['count_all_pairs'] = len(df.index)

    return df_new


def analyse_judgment_cosine_correlation_spearman(path, write=True):
    pairs = read_csv(path)
    judgments = []
    cosine_similarities = []
    data = []
    lemma = pairs[0]['lemma']

    output_path_folder = output_path + '/judgment_cosine_correlation/'
    Path(output_path_folder).mkdir(parents=True, exist_ok=True)

    for pair in pairs:
        judgment = pair['judgment']
        judgments.append(judgment)
        lemma = pair['lemma']

        cosine_similarity = pair['cosine_similarity']
        cosine_similarities.append(cosine_similarity)

        data.append({'lemma': lemma, 'label': judgment, 'cosine_similarity': cosine_similarity})

    # Calculate spearman correlation
    spearman_correlation = stats.spearmanr(judgments, cosine_similarities, nan_policy='omit')
    data[0]['spearman_correlation'] = spearman_correlation.statistic
    # print('Spearman correlation:', spearman_correlation)
    if write:
        file_name = lemma + '_' + str(spearman_correlation.statistic) + '_PC_Spearman.csv'
        output_path_file = os.path.join(output_path_folder, file_name)
        write_csv(output_path_file, data, data[0].keys())

    return data


def analyse_pairs_mapped(path, write=True):
    pairs = read_csv(path)
    labels = []
    mapped_labels = []
    lemma = pairs[0]['lemma']

    data = []
    hits = 0

    large_difference = 0

    output_path_folder = output_path + '/mapped_labels_analysis/'
    Path(output_path_folder).mkdir(parents=True, exist_ok=True)

    for pair in pairs:
        label = float(pair['judgment']).__round__()
        labels.append(label)
        lemma = pair['lemma']

        mapped_label = int(pair['mapped_label'])
        # mapped_labels.append(mapped_label)

        hit = label == mapped_label
        if hit:
            hits += 1

        difference = abs(label - mapped_label)
        if difference > 1:
            large_difference += 1

        data.append(
            {'lemma': lemma, 'label': label, 'mapped_label': mapped_label, 'hit': hit,
             'difference': difference})

    # Calculate hit percentage
    hit_percentage = hits / pairs.__len__()
    data[0]['hit_percentage'] = hit_percentage

    # Calculate large difference percentage
    large_difference_percentage = large_difference / pairs.__len__()
    data[0]['large_difference_percentage'] = large_difference_percentage

    # print('Spearman correlation:', hit_percentage)

    if write:
        file_name = lemma + '_' + str(hit_percentage) + '_hit_percentage.csv'
        output_path_file = os.path.join(output_path_folder, file_name)
        write_csv(output_path_file, data, data[0].keys())

    return data


def analyse_attacked_pair_challenges(path, write=True):
    pair_challenges = read_csv(path)
    data = []
    challenges_solved_entirely = 0
    challenges_solved_partially = 0
    krippendorff_coefficients = 0.0
    krippendorff_zeros = 0

    for pair_challenge in pair_challenges:

        # noinspection SpellCheckingInspection
        kripp_coeff = float(pair_challenge['krippendorff_coefficient'])
        solved_entirely = False
        solved_partially = False

        krippendorff_coefficients += abs(kripp_coeff)

        if kripp_coeff == 1.0:
            challenges_solved_entirely += 1
            solved_entirely = True
            solved_partially = True
        elif abs(kripp_coeff) >= 0.8:
            challenges_solved_partially += 1
            solved_partially = True
        elif kripp_coeff == 0.0:
            krippendorff_zeros += 1

        data.append({'id': pair_challenge['id'], 'krippendorff_coefficient': kripp_coeff,
                     'solved_entirely': solved_entirely, 'solved_partially': solved_partially})

    percentage_solved_entirely = challenges_solved_entirely / pair_challenges.__len__()
    percentage_solved_partially = challenges_solved_partially / pair_challenges.__len__()
    mean_krippendorff_coefficient = krippendorff_coefficients / pair_challenges.__len__()

    data[0]['percentage_solved_entirely'] = percentage_solved_entirely
    data[0]['percentage_solved_partially'] = percentage_solved_partially
    data[0]['mean_krippendorff_coefficient'] = mean_krippendorff_coefficient
    data[0]['number_of_krippendorff_zeros'] = krippendorff_zeros
    data[0]['number_of_challenges'] = pair_challenges.__len__()


    if write:
        output_path_folder = output_path + '/attacked_pair_challenges_analysis/'
        Path(output_path_folder).mkdir(parents=True, exist_ok=True)

        file_name = str(path).split('/')[str(path).split('/').__len__() - 1].replace('.csv', '_analysed.csv')
        output_path_file = os.path.join(output_path_folder, file_name)
        write_csv(output_path_file, data, data[0].keys())
        print('Attacked list challenge generated. Save to:', output_path_file)

    return data

def analyse_attacked_list_challenges(filepath, write=True, whole_dataset=True):
    list_challenges = read_csv(filepath)
    data = []
    order_set = np.array([], dtype=int)
    given_order_set = np.array([], dtype=int)

    hits = 0
    large_difference = 0
    spearman_correlations = 0.0

    filename = os.path.basename(filepath).split('_')
    lemma = None
    if not whole_dataset:
        lemma = filename[filename.__len__() - 1].split('.')[0]

    print('Analysing list challenge ranking for :', lemma)



    # per list challenge compare the order of the attacker and the true order and calculate spearman correlation
    for list_challenge in list_challenges:
        # noinspection RegExpRedundantEscape
        regex_order = re.sub(r' |\'|\s|\[|\]', '', list_challenge['order'])
        # noinspection RegExpRedundantEscape
        regex_order_given = re.sub(r' |\'|\s|\[|\]', '', list_challenge['given_order'])

        given_order_list = regex_order_given.split(',')
        order_list = regex_order.split(',')

        given_order_list_numbers = list(range(1, given_order_list.__len__() + 1))
        order_list_numbers = []
        for id in order_list:
            derived_index = given_order_list.index(id)
            order_list_numbers.append(derived_index + 1)
        # print(order_list)

        if order_list_numbers.__len__() != given_order_list_numbers.__len__():
            print('Error in list challenge:', list_challenge)
            continue

        error_distance = np.sum(np.abs(np.subtract(given_order_list_numbers, order_list_numbers)))

        #  ---- old -----
        # add reverse order to Set
        # order_set = np.append(order_set, order_list_numbers[::-1])
        # given_order_set = np.append(given_order_set, given_order_list_numbers[::-1])

        spearman_correlation = round(stats.spearmanr(order_list_numbers, given_order_list_numbers).statistic, 4)
        spearman_correlations += spearman_correlation

        if spearman_correlation == 1.0:
            hits += 1
        if spearman_correlation < 0.75:
            large_difference += 1

        id = list_challenge['reference_usage'] + '|' + list_challenge['usage1'] + '|' + list_challenge['usage2'] + '|' + \
             list_challenge['usage3'] + '|' + list_challenge['usage4']

        try:
            lemma = list_challenge['lemma']
        except KeyError:
            pass

        data.append(
            {'id': id, 'lemma': lemma, 'attacker_indexes': order_list_numbers,
             'spearman_correlation': spearman_correlation,
             'error_distance': error_distance,
             'real_order': list_challenge['given_order'], 'attacker_order': list_challenge['order']})

    data[0]['hit_percentage'] = hits / list_challenges.__len__()
    data[0]['large_difference_percentage'] = large_difference / list_challenges.__len__()
    data[0]['mean_spearman_correlation'] = spearman_correlations / list_challenges.__len__()

    if write:
        output_path_folder = output_path + '/attacked_list_challenge_analysis/'
        Path(output_path_folder).mkdir(parents=True, exist_ok=True)

        file_name = str(filepath).split('/')[str(filepath).split('/').__len__() - 1].replace('.csv', '_analysed.csv')
        output_path_file = os.path.join(output_path_folder, file_name)
        write_csv(output_path_file, data, data[0].keys())
        print('Attacked list challenge generated. Save to:', output_path_file)

    return data


def analyse_minimization_methods_and_mappings(path, write_mapping=True):
    file_names = os.listdir(path)

    best_method_with_mapping = {
        'method': None,
        'initial_mapping': None,
        'krippendorff': 0.0}

    output_path_folder = output_path + '/minimization_optimization_analysis/'
    Path(output_path_folder).mkdir(parents=True, exist_ok=True)

    for file_name in file_names:
        elements = file_name.split('_')
        method = elements[0]
        initial_mapping = [float(e) for e in elements[1:4]]
        file_data = read_csv(os.path.join(path, file_name))
        end_row_index = file_data.__len__() - 1
        krippendorff = float(file_data[end_row_index]['objective_function_value'])

        if krippendorff > best_method_with_mapping['krippendorff']:
            best_method_with_mapping['method'] = method
            best_method_with_mapping['initial_mapping'] = initial_mapping
            best_method_with_mapping['krippendorff'] = krippendorff

    print('Best method with mapping:', best_method_with_mapping)

    if best_method_with_mapping['method'] is not None and write_mapping:
        write_csv(os.path.join(output_path_folder, 'best_minimization_method.csv'), [best_method_with_mapping],
                  best_method_with_mapping.keys())
    elif best_method_with_mapping['method'] is not None and not write_mapping:
        return best_method_with_mapping


def analyse_all():
    analyse_minimization_methods_and_mappings('data_output/objective_function/')
    # analyzed_list_challenge_en = analyse_attacked_list_challenges('data_output/attacked_list_challenge/', False) needs fix
    judgment_cosine_relation = analyse_judgment_cosine_correlation_spearman(
        'data_output/attacked_pairs/attacked_pairs_dwug_en.csv')
    list_challenge_ranking = analyse_attacked_list_challenges(
        'data_output/attacked_list_challenge/dwug_en_attacked_list_challenge.csv')
    analyse_pairs_mapped('data_output/attacked_pairs/attacked_pairs_dwug_en.csv')


# analyse_all()

# join_list_challenges()
# generate_and_write_list_challenge(joined=True)

# generate_and_write_random_challenge()


# generate_and_write_pair_challenges('data_output/pairs_whole_dataset/dwug_en_pairs.csv')
# generate_and_write_pair_challenges('data_output/pairs_whole_dataset/dwug_en_pairs.csv', 3)
# generate_and_write_pairs_separate()
# generate_and_write_pairs_for_dataset('dwug_en')
# generate_and_write_pairs_for_dataset('dwug_de')

# analyse_minimization_methods_and_mappings('data_output/objective_function/')

# load_and_write_uses_judgments(True)

# analyse_judgment_cosine_correlation_spearman('data_output/attacked_pairs/attacked_pairs_abbauen.csv')

# analyse_attacked_list_challenges('data_output/attacked_list_challenge/dwug_en_attacked_list_challenge_fixed.csv')
# analyse_attacked_pair_challenges('data_output/attacked_pair_challenges/dwug_en_pair_challenges_3_attacked_fixed.csv')
# analyse_pairs_mapped('data_output/attacked_pairs/attacked_pairs_dwug_en.csv')

# generate_and_print_list_challenge()


# generate_and_print_set_of_pairs()

# write_csv('data_output/attack_pairs/pairs_attack_.csv', aggregate_judgments_df(), aggregate_judgments_df().keys())
