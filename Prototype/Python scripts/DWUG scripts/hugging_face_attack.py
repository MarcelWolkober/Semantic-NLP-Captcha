import os
import csv
from pathlib import Path
from WordTransformer import WordTransformer, InputExample
from sentence_transformers import util

print('loading model...')
model = WordTransformer('pierluigic/xl-lexeme')

print('model loaded')
use_pairs_input_path = 'data_output/dwug_en/data/attack_nn/pairs_challenge.csv'
output_path_pairs = 'data_output/attacked_pairs'
Path(output_path_pairs).mkdir(parents=True, exist_ok=True)

output_path_list_challenge = 'data_output/attacked_list_challenge'
Path(output_path_list_challenge).mkdir(parents=True, exist_ok=True)


def load_csv_file(file_path):
    data = []
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
        for row in reader:
            data.append(row)
    return data


def write_file(file_path, data, keys):
    with open(file_path, 'w') as f:
        header = '\t'.join(keys)
        f.write(header)
        f.write('\n')
        for row in data:
            row = {k: ('' if v is None else v) for k, v in row.items()}
            line = '\t'.join([str(v) for v in row.values()])
            f.write(line)
            f.write('\n')


def analyse_pair(pair):
    # Get the context
    context1 = pair['context1']
    context2 = pair['context2']

    # Get the target word and its position
    target_position1 = [int(pair['indexes_target_token1'].split(':')[0]),
                        int(pair['indexes_target_token1'].split(':')[1])]
    target_position2 = [int(pair['indexes_target_token2'].split(':')[0]),
                        int(pair['indexes_target_token2'].split(':')[1])]

    # Create an InputExample
    input_example1 = InputExample(texts=context1, positions=target_position1)
    input_example2 = InputExample(texts=context2, positions=target_position2)

    # Get the embedding of the target word
    target_embedding1 = model.encode(input_example1, convert_to_tensor=True)
    target_embedding2 = model.encode(input_example2, convert_to_tensor=True)

    cosine_similarity = util.cos_sim(target_embedding1, target_embedding2)

    return cosine_similarity


def analyze_and_write_pairs_challenges(pairs):
    print('Generating cosine similarity for ', len(pairs), ' pairs ...')
    keys = ['lemma', 'identifier1', 'identifier2', 'judgment', 'cosine_similarity']
    data = []
    count = 1
    lemma = pairs[0]['lemma']

    for pair in pairs:
        print('blabla')
        print('for pair ', count, ' of ', len(pairs), end='\r')
        count += 1
        cosine_similarity = analyse_pair(pair)
        output_file = {
            'lemma': lemma,
            'identifier1': pair['identifier1'],
            'identifier2': pair['identifier2'],
            'judgment': pair['judgment'],
            'cosine_similarity': str(cosine_similarity.item())
        }

        data.append(output_file)

    write_file(os.path.join(output_path_pairs, 'attacked_pairs_' + lemma + '.csv'), data, keys)
    print('Cosine similarity generated. Save to:', output_path_pairs + '/attacked_pairs_' + lemma + '.csv')


def analyze_list_challenge(list_challenge, uses):
    lemma = list_challenge['lemma']
    usage_missing_usage = {
        'lemma': lemma,
        'identifier': 'missing_usage',
        'judgment': 0.0,
    }
    reference_usage = list(filter(lambda x: x['identifier'] == list_challenge['identifier_ref'], uses))[0]
    usage1_filter = list(filter(lambda x: x['identifier'] == list_challenge['identifier1'], uses))
    usage2_filter = list(filter(lambda x: x['identifier'] == list_challenge['identifier2'], uses))
    usage3_filter = list(filter(lambda x: x['identifier'] == list_challenge['identifier3'], uses))
    usage4_filter = list(filter(lambda x: x['identifier'] == list_challenge['identifier4'], uses))

    usage1 = usage_missing_usage if usage1_filter.__len__() < 1 else usage1_filter[0]
    usage2 = usage_missing_usage if usage2_filter.__len__() < 1 else usage2_filter[0]
    usage3 = usage_missing_usage if usage3_filter.__len__() < 1 else usage3_filter[0]
    usage4 = usage_missing_usage if usage4_filter.__len__() < 1 else usage4_filter[0]

    usages = [usage1, usage2, usage3, usage4]
    ranking = {
        'reference_usage': reference_usage['identifier'],
        usage1['identifier']: None,
        usage2['identifier']: None,
        usage3['identifier']: None,
        usage4['identifier']: None,
        'order': None
    }

    for usage in usages:
        if usage in [None, usage_missing_usage]:
            continue

        pair = {
            'lemma': usage['lemma'],
            'identifier1': reference_usage['identifier'],
            'identifier2': usage['identifier'],
            'context1': reference_usage['context'],
            'context2': usage['context'],
            'indexes_target_token1': reference_usage['indexes_target_token'],
            'indexes_target_token2': usage['indexes_target_token'],
            'judgment': '0.0',
        }
        # print(pair)
        pair_cosine_similarity = analyse_pair(pair)
        ranking[usage['identifier']] = pair_cosine_similarity.item()

    ranking['order'] = sorted(ranking, reverse=True)
    for usage in usages:
        ranking[usage['identifier']] = str(ranking[usage['identifier']])
    # print(ranking)

    return ranking


def analyze_and_write_list_challenges(challenges, uses):
    print('Analysing list challenges...')
    lemma = challenges[0]['lemma']
    data = []
    count = 1
    for list_challenge in challenges:
        print('for list_challenge ', count, ' of ', len(challenges))
        count += 1
        ranking = analyze_list_challenge(list_challenge, uses)
        data.append(ranking)
        if count == 3:
            break

    write_file(os.path.join(output_path_list_challenge, 'attacked_list_challenge_' + lemma + '.csv'),
               data, data[0].keys() if data else [])
    print('List challenge generated. Save to:',
          output_path_list_challenge + '/attacked_list_challenge_' + lemma + '.csv')


uses = load_csv_file('data_output/dwug_en/data/attack_nn/uses.csv')
judgments = load_csv_file('data_output/dwug_en/data/attack_nn/judgments.csv')
list_challenges = load_csv_file('data_output/dwug_en/data/attack_nn/list_challenges_filtered.csv')

analyze_and_write_list_challenges(list_challenges, uses)

# pairs = load_csv_file(use_pairs_input_path)
# analyze_and_write_pairs_challenges(pairs)
