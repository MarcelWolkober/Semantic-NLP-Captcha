# import scripts from parent folder
import sys
sys.path.append('../')
from pathlib import Path
import csv

# Preprocess annotated data
datasets = ['dwug_en']

# Make output directory
input_path = '../../DWUG/'
output_path = 'data_output'
Path(output_path).mkdir(parents=True, exist_ok=True)

# unwanted metrics
undesired_annotators = []
undesired_judgments = ['0.0']

for dataset in datasets:
    for p in Path(input_path + '/' + dataset + '/data').glob('*/'):
        print(p)
        lemma = str(p).split('/')[-1].replace('-', '_')  # rename lemma folders in English data
        with open(str(p) + '/' + 'uses.csv', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
            uses = [row for row in reader]
        with open(str(p) + '/' + 'judgments.csv', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
            judgments = [row for row in reader]
        with open(str(p) + '/' + 'pairs.csv', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
            pairs = [row for row in reader]



        #-----------ADD FILTERS HERE-----------------------






        #filter unwanted keys and write data
        uses_out = []
        for row in uses:
            row_out = {key: val for key, val in row.items() if
                       key not in ['identifier_system', 'project', 'lang', 'user']}
            uses_out.append(row_out)




        judgments_out = []
        for row in judgments:
            if row['judgment'] in undesired_judgments:
                print('skipped judgment with rating 0')
                continue
            if row['annotator'] in undesired_annotators:  # filter out undesired annotators
                continue
            row_out = {key: val for key, val in row.items()}
            if row_out['comment'] in ['', 'comment']:  # clean the comment column
                row_out['comment'] = ' '
            if row_out['annotator'] == 'AndreaMariaC':  # merge annotators with multiple's names
                row_out['annotator'] = 'amariac810'
            judgments_out.append(row_out)

        # Continue if word was not annotated
        if judgments_out == []:
            continue







        output_path_lemma = output_path + '/' + dataset + '/data/' + lemma + '/'
        Path(output_path_lemma).mkdir(parents=True, exist_ok=True)

        with open(output_path_lemma + '/' + 'judgments.csv', 'w') as f:
            w = csv.DictWriter(f, judgments_out[0].keys(), delimiter='\t', quoting=csv.QUOTE_NONE, escapechar=' ',
                               extrasaction='ignore', lineterminator='\n')
            w.writeheader()
            w.writerows(judgments_out)

        with open(output_path_lemma + '/' + 'uses.csv', 'w') as f:
            w = csv.DictWriter(f, uses_out[0].keys(), delimiter='\t', quoting=csv.QUOTE_NONE, escapechar=' ',
                               extrasaction='ignore', lineterminator='\n')
            w.writeheader()
            w.writerows(uses_out)
