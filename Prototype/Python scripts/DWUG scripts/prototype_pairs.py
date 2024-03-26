import os
import csv
from pathlib import Path

datasets = ['dwug_en']
input_path = '../../../DWUG/'
output_path = 'data_output'
Path(output_path).mkdir(parents=True, exist_ok=True)

undesired_judgments = ['0.0']
undesired_annotators = []
undesired_keys = ['identifier_system', 'project', 'lang', 'user']


def write_csv(file_path, data, keys):
    with open(file_path, 'w', newline='') as f:
        w = csv.DictWriter(f, keys, delimiter='\t', quoting=csv.QUOTE_NONE, escapechar=' ', extrasaction='ignore',
                           lineterminator='\n')
        w.writeheader()
        w.writerows(data)


def read_and_filter_csv(file_path, undesired_keys, undesired_judgments, undesired_annotators):
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
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
            filtered_rows.append(row_out)
        return filtered_rows


def generate_random_pairs(uses, judgments):
    pairs = []
    uses_dict = {use['identifier']: use for use in uses}

    # For each judgment, look up the corresponding uses by its identifiers
    for judgment in judgments:
        use1 = uses_dict.get(judgment['identifier1'])
        use2 = uses_dict.get(judgment['identifier2'])
        if use1 is not None and use2 is not None:
            pair = {
                'lemma': use1['lemma'],  # Assuming that the lemma is the same for both uses
                'identifier1': use1['identifier'],
                'identifier2': use2['identifier'],
                'context1': use1['context'],
                'context2': use2['context'],
                'indexes_target_token1': use1['indexes_target_token'],
                'indexes_target_token2': use2['indexes_target_token'],
                'judgment': judgment['judgment'],
            }
            pairs.append(pair)
        else:
            print('Could not find use for judgment', judgment)

    return pairs


def remove_judgments_from_pairs(pairs):
    for pair in pairs:
        pair.pop('judgment', None)

    return pairs


for dataset in datasets:
    dataset_path = os.path.join(input_path, dataset, 'data')
    for p in Path(dataset_path).glob('*/'):
        print(p)
        lemma_path = str(p).split('/')[-1].replace('-', '_')
        uses_file_path = os.path.join(str(p), 'uses.csv')
        judgments_file_path = os.path.join(str(p), 'judgments.csv')
        pairs_file_path = os.path.join(str(p), 'pairs.csv')

        lemma = os.path.basename(lemma_path)


        uses = read_and_filter_csv(uses_file_path, undesired_keys, undesired_judgments, undesired_annotators)
        judgments = read_and_filter_csv(judgments_file_path, [], undesired_judgments, undesired_annotators)
        pairs_header = read_and_filter_csv(pairs_file_path, [], undesired_judgments,
                                           undesired_annotators)[0].keys() if pairs_file_path else []

        pairs = generate_random_pairs(uses, judgments)

        output_path_lemma = os.path.join(output_path, dataset, 'data', lemma)
        Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

        print('output path:', output_path_lemma)




        write_csv(os.path.join(output_path_lemma, 'judgments.csv'), judgments, judgments[0].keys() if judgments else [])
        write_csv(os.path.join(output_path_lemma, 'uses.csv'), uses, uses[0].keys() if uses else [])
        write_csv(os.path.join(output_path_lemma, 'pairs.csv'), pairs)
        write_csv(os.path.join(output_path_lemma, 'pairs_clean.csv'), remove_judgments_from_pairs(pairs), pairs_header)
