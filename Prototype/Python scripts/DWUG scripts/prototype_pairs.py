import os
import csv
from pathlib import Path

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
    with open(file_path, 'w', newline='') as f:
        w = csv.DictWriter(f, keys, delimiter='\t', quoting=csv.QUOTE_NONE, escapechar=' ', extrasaction='ignore',
                           lineterminator='\n')
        w.writeheader()
        w.writerows(data)


def read_csv(file_path):
    return read_and_filter_csv(file_path, [], [], [])

#filter später
def read_and_filter_csv(file_path, undesired_keys, undesired_judgments, undesired_annotators):
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
        #header = reader.__next__()

        #print('row:', header.keys())

        rows = [row for row in reader]
        filtered_rows = []
        for row in rows:
            if 'judgment' in row and row['judgment'] in undesired_judgments:
                continue
            if 'annotator' in row and row['annotator'] in undesired_annotators:
                continue
            row_out = {key: val for key, val in row.items() if key not in undesired_keys}
            if 'comment' in row_out and row_out['comment'] in ['', 'comment']:
                row_out['comment'] = ' '
            if 'annotator' in row_out and row_out['annotator'] == 'AndreaMariaC':
                row_out['annotator'] = 'amariac810'
            if 'context' in row:
                lemma = row['lemma'].split('_')[0]
                row_out['lemma'] = lemma
                row_out['derived_indexes'] = derive_indexes(lemma, row['context'])

            filtered_rows.append(row_out)
        return filtered_rows


def generate_pairs(uses, judgments):
    pairs = []
    uses_dict = {use['identifier']: use for use in uses}

    # For each judgment, look up the corresponding uses by its identifiers
    for judgment in judgments:
        use1 = uses_dict.get(judgment['identifier1'])
        use2 = uses_dict.get(judgment['identifier2'])
        if use1 is not None and use2 is not None:
            pair = {
                'lemma': use1['lemma'],
                'identifier1': use1['identifier'],
                'identifier2': use2['identifier'],
                'context1': use1['context'],
                'context2': use2['context'],
                'indexes_target_token1': use1['indexes_target_token'],
                'derived_indexes1': use1['derived_indexes'],
                'indexes_target_token2': use2['indexes_target_token'],
                'derived_indexes2': use2['derived_indexes'],
                'judgment': judgment['judgment'],
            }
            pairs.append(pair)
        else:
            print('Could not find use for judgment', judgment)

    return pairs


def generate_and_print_list_challenge():
    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            judgments_file_path = os.path.join(str(p), 'judgments.csv')

            lemma = os.path.basename(lemma_path)

            uses = read_and_filter_csv(uses_file_path, undesired_keys, undesired_judgments, undesired_annotators)
            judgments = read_and_filter_csv(judgments_file_path, [], undesired_judgments, undesired_annotators)
            print('Generating list of challenges ...')

            local_challenges = []

            for usage in uses:
                local_list_challenge = find_practical_reference_usages(usage, judgments)
                if local_list_challenge['identifier3'] is not None:
                    local_challenges.append(local_list_challenge)

            list_challenge_header = local_challenges[0].keys() if local_challenges else []
            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'list_challenges.csv'), local_challenges, list_challenge_header)


def find_practical_reference_usages(usage, judgments):
    judgment_to_have = ['1.0', '2.0', '3.0', '4.0']
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
    i = 1
    for judgment in judgments:
        if judgment['identifier1'] == usage['identifier']:
            if judgment['judgment'] in judgment_to_have:
                list_challenge['identifier' + str(i)] = judgment['identifier2']
                list_challenge['judgment' + str(i)] = judgment['judgment']
                judgment_to_have.remove(judgment['judgment'])
                i += 1
                if len(judgment_to_have) == 0:
                    break

    return list_challenge


def remove_judgments_from_pairs(pairs):
    for pair in pairs:
        pair.pop('judgment', None)

    return pairs


def derive_indexes(lemma, context):
    start_index = context.find(lemma)
    #print('derived_lemma: ', context[start_index:start_index + len(lemma)])
    return [start_index, start_index + len(lemma)]


def generate_and_print_set_of_pairs():
    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            judgments_file_path = os.path.join(str(p), 'judgments.csv')
            pairs_file_path = os.path.join(str(p), 'pairs.csv')

            lemma = os.path.basename(lemma_path)

            uses = read_and_filter_csv(uses_file_path, undesired_keys, undesired_judgments, undesired_annotators)
            judgments = read_and_filter_csv(judgments_file_path, [], undesired_judgments, undesired_annotators)
            pairs_header = ['lemma', 'identifier1', 'identifier2', 'context1', 'context2', 'indexes_target_token1',
                            'derived_indexes1',
                            'indexes_target_token2', 'derived_indexes2', 'judgment']

            pairs = generate_pairs(uses, judgments)

            output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
            Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

            write_csv(os.path.join(output_path_lemma, 'judgments.csv'), judgments,
                      judgments[0].keys() if judgments else [])
            write_csv(os.path.join(output_path_lemma, 'uses.csv'), uses, uses[0].keys() if uses else [])
            write_csv(os.path.join(output_path_lemma, 'pairs.csv'), pairs, pairs_header)
            write_csv(os.path.join(output_path_lemma, 'pairs_clean.csv'), remove_judgments_from_pairs(pairs),
                      pairs_header)


def analyse_judgment_cosine_correlation(path):
    pairs = read_csv(path)
    data = []
    lemma = pairs[0]['lemma']
    file_name = lemma + '_jcc.csv'
    output_path_local = output_path + '/judgment_cosine_correlation/' + lemma
    Path(output_path_local).mkdir(parents=True, exist_ok=True)
    for pair in pairs:
        judgment = pair['judgment']
        cosine_similarity = pair['cosine_similarity']
        data.append({'lemma': lemma, 'judgment': judgment, 'cosine_similarity': cosine_similarity})

    write_csv(os.path.join(output_path_local, file_name), data, ['judgment', 'cosine_similarity'])


generate_and_print_list_challenge()

#analyse_judgment_cosine_correlation('data_output/attack_pairs/pairs_attack.csv')
#generate_pairs()
