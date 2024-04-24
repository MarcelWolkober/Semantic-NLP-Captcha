import os
import csv
from pathlib import Path
from WordTransformer import WordTransformer, InputExample
from sentence_transformers import util
import krippendorff
from scipy.optimize import minimize
import numpy as np

print('loading model...')
model = WordTransformer('pierluigic/xl-lexeme')
print('model loaded')

datasets = ['dwug_en']
input_path = 'data_output'
aggregated_judgments_path = 'data_output/dwug_en/data/attack_nn/judgments.csv'
use_pairs_input_path = 'data_output/dwug_en/data/attack_nn/pairs_challenge.csv'
output_path_pairs = 'data_output/attacked_pairs'
Path(output_path_pairs).mkdir(parents=True, exist_ok=True)

output_path_list_challenge = 'data_output/attacked_list_challenge'
Path(output_path_list_challenge).mkdir(parents=True, exist_ok=True)

mapping_params = [0.25246081, 0.48095611, 0.68803579]


def load_csv_file(file_path):
    data = []
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
        for row in reader:
            data.append(row)
    return data


def write_file(file_path, data, keys):
    with open(file_path, 'w', encoding="utf-8") as f:
        header = '\t'.join(keys)
        f.write(header)
        f.write('\n')
        for row in data:
            row = {k: ('' if v is None else v) for k, v in row.items()}
            line = '\t'.join([str(v) for v in row.values()])
            f.write(line)
            f.write('\n')


# Print iterations progress
def printProgressBar(iteration, total, prefix='', suffix='', decimals=1, length=100, fill='█', printEnd="\r"):
    """
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. "\r", "\r\n") (Str)
    """
    percent = ("{0:." + str(decimals) + "f}").format(100 * (iteration / float(total)))
    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print(f'\r{prefix} |{bar}| {percent}% {suffix}', end=printEnd)
    # Print New Line on Complete
    if iteration == total:
        print()


def attack_pair(pair):
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


def attack_and_write_pairs_challenges(pairs):
    number_of_pairs = len(pairs)
    print('Generating cosine similarity for ', number_of_pairs, ' pairs ...')
    if pairs.__len__() == 0:
        return
    data = []
    count = 1
    lemma = pairs[0]['lemma']

    # Initial call to print 0% progress
    # printProgressBar(0, number_of_pairs, prefix='Progress:', suffix='Complete', length=50)

    for pair in pairs:
        if str(pair['context1']).__len__() > 512 or pair['context2'].__len__() > 512:
            print('Context too long, skipping pair')
            continue

        cosine_similarity = attack_pair(pair)
        mapped_label = mapping_co_sim_to_label_predict(cosine_similarity.item())
        output_file = {
            'lemma': lemma,
            'identifier1': pair['identifier1'],
            'identifier2': pair['identifier2'],
            'judgment': pair['judgment'],
            'mapped_label': mapped_label,
            'cosine_similarity': str(cosine_similarity.item())

        }
        count += 1
        print('generating pairs', count, ' of ', number_of_pairs)
        # printProgressBar(count, number_of_pairs, prefix='Progress:', suffix='Complete', length=50)

        data.append(output_file)

    write_file(os.path.join(output_path_pairs, 'attacked_pairs_' + lemma + '.csv'), data, data[0].keys())
    print('Cosine similarity generated. Save to:', output_path_pairs + '/attacked_pairs_' + lemma + '.csv')


def attack_random_challenge(challenge, uses, value):
    list_challenge = attack_list_challenge(challenge, uses)
    usage_to_return = list_challenge['order'][4 - int(value) - 1]
    print(usage_to_return)


def attack_list_challenge(list_challenge, uses):
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
        'usage1': usage1['identifier'],
        'cosine_similarity1': 0.0,
        'usage2': usage2['identifier'],
        'cosine_similarity2': 0.0,
        'usage3': usage3['identifier'],
        'cosine_similarity3': 0.0,
        'usage4': usage4['identifier'],
        'cosine_similarity4': 0.0,
        'order': None,
        'given_order': list_challenge['order']
    }
    count = 1
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
        pair_cosine_similarity = attack_pair(pair)
        ranking['cosine_similarity' + str(count)] = pair_cosine_similarity.item()
        count += 1
        if count > 4:
            break

    # Create a list of tuples (usage, cosine_similarity)
    usage_cosine_list = [(ranking[f'usage{i}'], ranking[f'cosine_similarity{i}']) for i in range(1, 5)]

    # Remove key-value pairs where the value is 'missing_usage'
    usage_cosine_list_new = [t for t in usage_cosine_list if t.__str__().find('missing_usage') == -1]
    # usage_cosine_list_new = [ v for t in usage_cosine_list if t[1] != '\'missing_usage\'']

    # Sort the list based on cosine similarity in descending order
    sorted_list = sorted(usage_cosine_list_new, key=lambda x: x[1], reverse=True)

    # Extract the ordered usage keys
    ordered_usages = [item[0] for item in sorted_list]

    ranking['order'] = ordered_usages

    return ranking


def attack_and_write_list_challenges(challenges, uses):
    lemma = challenges[0]['lemma']
    print('Analysing list challenges for lemma ', lemma, ' ...')
    data = []
    count = 1
    for list_challenge in challenges:
        print('for list_challenge ', count, ' of ', len(challenges))
        count += 1
        attack_random_challenge(list_challenge, uses, 4.0)
        ranking = attack_list_challenge(list_challenge, uses)
        data.append(ranking)

    write_file(os.path.join(output_path_list_challenge, 'attacked_list_challenge_' + lemma + '.csv'),
               data, data[0].keys() if data else [])
    print('Attacked list challenge generated. Save to:',
          output_path_list_challenge + '/attacked_list_challenge_' + lemma + '.csv')


def write_list_challenges_for_datasets():
    for dataset in datasets:
        dataset_path = os.path.join(input_path, dataset, 'data')
        for p in Path(dataset_path).glob('*/'):
            lemma_path = str(p).split('/')[-1].replace('-', '_')
            uses_file_path = os.path.join(str(p), 'uses.csv')
            list_challenges_file_path = os.path.join(str(p), 'list_challenges_filtered.csv')

            lemma = os.path.basename(lemma_path)

            uses = load_csv_file(uses_file_path)
            list_challenges = load_csv_file(list_challenges_file_path)

            attack_and_write_list_challenges(list_challenges, uses)


def to_minimize(mapping, labels, cos_sim):
    # Sort the mapping parameters
    mapping_sorted = np.sort(mapping)

    # Apply the mapping to the cosine similarities
    cos_sim_mapped = np.digitize(cos_sim, bins=mapping_sorted)
    cos_sim_mapped = [value + 1 for value in cos_sim_mapped]

    # Compute the Krippendorff's alpha coefficient
    alpha = krippendorff.alpha(reliability_data=[labels, cos_sim_mapped],
                               level_of_measurement='ordinal')

    # Return the negative of the alpha coefficient
    return -alpha


# liste mit label und cos_sim
def mapping_co_sim_to_label_fit(list_train_data):
    # nach Krippendorff maximum iterieren mit deutsche gold daten

    # Initial guess for the mapping parameters
    initial_mapping = np.array([0.24216072, 0.48696087, 0.64023089])  # #[0.25246081, 0.4095611, 0.68803579]

    # Labels and cosine similarities
    labels = np.array([float(pair['label']) for pair in list_train_data])
    cos_sim = np.array([float(pair['cosine_similarity']) for pair in list_train_data])

    # Find the mapping parameters that maximize the Krippendorff's alpha coefficient
    result = minimize(to_minimize, initial_mapping, args=(labels, cos_sim), method='Nelder-Mead')

    mapping_params = np.sort(result.x)
    # Print the optimal mapping parameters
    print('Optimal mapping parameters:', result.x)


def mapping_co_sim_to_label_predict(cos_sim):
    mapped = np.digitize(float(cos_sim), bins=[0.24216072, 0.48696087, 0.64023089])
    mapped += 1
    return mapped


def generate_krippendorff_coefficient(pairs_mapped):
    labels = [float(pair['judgment']) for pair in pairs_mapped]
    mapped_labels = [float(pair['mapped_label']) for pair in pairs_mapped]

    krippendorff_coefficient = krippendorff.alpha(reliability_data=[labels, mapped_labels],
                                                  level_of_measurement='ordinal')

    print('Krippendorff coefficient:', krippendorff_coefficient)
    return krippendorff_coefficient


# generate_krippendorff_coefficient(load_csv_file('data_output/attacked_pairs/attacked_pairs_attack.csv'))

# write_list_challenges_for_datasets()

# pairs = load_csv_file(use_pairs_input_path)
# attack_and_write_pairs_challenges(pairs)
# analyze_and_write_pairs_challenges(pairs)

attack_and_write_pairs_challenges(load_csv_file('data_output/pairs_whole_dataset/dwug_de_pairs_challenge.csv'))

# mapping_co_sim_to_label_fit(    load_csv_file('data_output/judgment_cosine_correlation/abbauen_0.7351388199302153_PC_Spearman.csv'))

# print( mapping_co_sim_to_label_predict(load_csv_file('data_output/attacked_pairs/attacked_pairs_attack.csv')))
