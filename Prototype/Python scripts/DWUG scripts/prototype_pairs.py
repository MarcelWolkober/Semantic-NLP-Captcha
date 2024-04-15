import os
import csv
from pathlib import Path
import pandas as pd
import numpy as np
from scipy import stats
from IPython.display import display

datasets = ['dwug_en']
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
    with open(file_path, 'w') as f:
        header = '\t'.join(keys)
        f.write(header)
        f.write('\n')
        for row in data:
            row = {k: ('' if v is None else v) for k, v in row.items()}
            values = row.values()
            line = '\t'.join(values)
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
    pairs = []
    uses_dict = {use['identifier']: use for use in uses}
    lemma = uses[0]['lemma']

    df = judgments_df_filtered.groupby(['lemma']).get_group((lemma + '_nn',))

    for i in df.index:

        judgment_mean = str(df.loc[i][4:13].mean())
        # print('Mean judgment:', judgment_mean)

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
                'judgment': judgment_mean,
            }
            pairs.append(pair)
        else:
            print('Could not find use for judgment', df.loc[i])

    return pairs


def generate_and_write_pairs_challenge():
    print('Generating and writing pairs for challenge ...')

    judgments_aggregated = aggregate_judgments_df(load_judgments_df_from_source_datasets())
    judgments_filtered = filter_aggregated_judgments(judgments_aggregated)

    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')

            lemma = os.path.basename(lemma_path)

            pairs = generate_pairs(load_csv(uses_file_path, undesired_keys), judgments_filtered)

            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'pairs_challenge.csv'), pairs, pairs[0].keys() if pairs else [])


def generate_and_write_list_challenge():
    print('Generating and writing lists for challenge ...')

    judgments_aggregated = aggregate_judgments_df(load_judgments_df_from_source_datasets())
    judgments = filter_aggregated_judgments(judgments_aggregated)

    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            judgments_file_path = os.path.join(str(p), 'judgments.csv')

            lemma = os.path.basename(lemma_path)

            uses = load_csv(uses_file_path, undesired_keys)

            print('Generating list of challenges ...')

            local_challenges = []

            for usage in uses:
                local_list_challenge = find_practical_reference_usages(usage, judgments)
                if local_list_challenge['identifier3'] is not None:
                    local_challenges.append(local_list_challenge)

            if local_challenges.__len__() == 0:
                print('No list challenges possible for lemma ', lemma)
                continue

            list_challenge_header = local_challenges[0].keys() if local_challenges else []
            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'list_challenges_filtered.csv'), local_challenges,
                      list_challenge_header)


def find_practical_reference_usages(usage, judgments_df):
    judgment_to_have = [1.0, 2.0, 3.0, 4.0]
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
        'judgment4': None
    }

    lemma = usage['lemma']
    df = judgments_df.groupby(['lemma']).get_group((lemma + '_nn',))
    index = 1

    # For each judgment, check if usage is fitting
    for i in df.index:
        judgment = df.loc[i]
        # print('Processing judgment', judgment)

        judgment_mean = judgment[4:13].mean()
        if judgment_mean not in judgment_to_have:
            continue
        # print('Mean judgment:', judgment_mean)

        if usage['identifier'] in [judgment['identifier1'], judgment['identifier2']]:
            referenced_usage = judgment['identifier2'] if usage['identifier'] == judgment['identifier1'] else \
                judgment['identifier1']

            list_challenge['identifier' + str(index)] = referenced_usage
            list_challenge['judgment' + str(index)] = str(judgment_mean)
            judgment_to_have.remove(judgment_mean)
            index += 1
            if len(judgment_to_have) == 0:
                break

    return list_challenge


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
    # display(df_judgments)
    # Display a sample to validate
    # display(df_judgments.sample(n=10))
    return df_judgments


def load_and_write_uses_judgments():
    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            judgments_file_path = os.path.join(str(p), 'judgments.csv')

            lemma = os.path.basename(lemma_path)

            uses = load_csv(uses_file_path, undesired_keys)
            judgments = load_csv(judgments_file_path, [])

            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'judgments.csv'), judgments,
                      judgments[0].keys() if judgments else [])
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
        # display(judgments_annotator)
        df_judgments_pair_vs_ann = pd.concat([df_judgments_pair_vs_ann, judgments_annotator])

    # Discard duplicates
    df_judgments_pair_vs_ann_aggregated = df_judgments_pair_vs_ann.groupby(
        ['identifier1', 'identifier2']).first().reset_index()
    return df_judgments_pair_vs_ann_aggregated


def filter_aggregated_judgments(df):
    df_new = df.copy()

    for i in df.index:
        # Filter out pairs with 0 judgment
        if 0 in df.loc[i][4:13].values:
            df_new.drop(index=i, inplace=True)
            # print('Dropped pair with 0 judgment', df.loc[i])

    return df_new


def analyse_judgment_cosine_correlation_spearman(path):
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

        cosine_similarity = pair['cosine_similarity']
        cosine_similarities.append(cosine_similarity)

        data.append({'lemma': lemma, 'judgment': judgment, 'cosine_similarity': cosine_similarity})

    # Calculate spearman correlation
    spearman_correlation = stats.spearmanr(judgments, cosine_similarities, nan_policy='omit')
    print('Spearman correlation:', spearman_correlation)

    file_name = lemma + '_' + str(spearman_correlation.statistic) + '_jcc.csv'
    output_path_file = os.path.join(output_path_folder, file_name)
    write_csv(output_path_file, data, ['judgment', 'cosine_similarity'])

    # generate_and_print_list_challenge()

    # analyse_judgment_cosine_correlation_spearman('data_output/attacked_pairs/attacked_pairs_attack.csv')
    # generate_and_print_set_of_pairs()

    # write_csv('data_output/attack_pairs/pairs_attack_.csv', aggregate_judgments_df(), aggregate_judgments_df().keys())
    # generate_and_write_pairs_challenge()


generate_and_write_list_challenge()
# generate_and_write_pairs_challenge()
