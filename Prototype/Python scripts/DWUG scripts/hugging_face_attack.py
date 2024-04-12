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
Path(output_path_pairs).mkdir(parents=True, exist_ok=True)


def load_csv_file(file_path):
    data = []
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
        for row in reader:
            data.append(row)
    return data


def analyse_pair(pair):
    # Get the context
    lemma = pair['lemma']
    context1 = pair['context1']
    context2 = pair['context2']

    start_index1 = context1.find(lemma)
    start_index2 = context2.find(lemma)

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


def write_file(file_path, data, keys):
    with open(file_path, 'w') as f:
        header = '\t'.join(keys)
        f.write(header)
        f.write('\n')
        for row in data:
            line = '\t'.join(row.values())
            f.write(line)
            f.write('\n')


def analyze_and_write_pairs_challenges(pairs):
    print('Generating cosine similarity for ',  len(pairs), ' pairs ...')
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
    reference_usage = filter(lambda x: x['identifier'] == list_challenge['reference_usage'], uses)
    print('reference_usage:', reference_usage)
    return 0


def analyze_and_write_list_challenges(challenges, uses, judgments):
    print('Analysing list challenges...')
    keys = ['lemma', 'reference_usage', 'reference_usage_context', 'reference_usage_indexes_target_token',
            'reference_usage_derived_indexes', 'cosine_similarity']
    data = []
    count = 1
    for list_challenge in challenges:
        print('for list_challenge ', count, ' of ', len(list_challenge))
        count += 1
        cosine_similarity = analyze_list_challenge(list_challenge, uses)
        list_challenge['cosine_similarity'] = cosine_similarity.item()
        data.append(list_challenge)
    write_file(os.path.join(output_path_list_challenge, 'list_challenge_attack.csv'), data, keys)
    print('List challenge generated. Save to:', output_path_list_challenge + '/list_challenge.csv')


# uses = load_csv_file('data_output/dwug_en/data/attack_nn/uses.csv')
# judgments = load_csv_file('data_output/dwug_en/data/attack_nn/judgments.csv')
# list_challenges = load_csv_file('data_output/dwug_en/data/attack_nn/list_challenge.csv')

# analyze_list_challenge(list_challenges[0], uses)

pairs = load_csv_file(use_pairs_input_path)
analyze_and_write_pairs_challenges(pairs)
