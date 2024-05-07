import os
import csv
from pathlib import Path
from WordTransformer import WordTransformer, InputExample
from sentence_transformers import util
import krippendorff
from scipy.optimize import minimize, OptimizeResult
import numpy as np
from matplotlib import pyplot as plt

print('loading model...')
model = None  # WordTransformer('pierluigic/xl-lexeme')
print('model loaded')

datasets = ['dwug_en']
input_path = 'data_output'
aggregated_judgments_path = 'data_output/dwug_en/data/attack_nn/judgments.csv'
use_pairs_input_path = 'data_output/dwug_en/data/attack_nn/pairs_challenge.csv'
output_path_pairs = 'data_output/attacked_pairs'
Path(output_path_pairs).mkdir(parents=True, exist_ok=True)

output_path_list_challenge = 'data_output/attacked_list_challenge'
Path(output_path_list_challenge).mkdir(parents=True, exist_ok=True)

output_path_objective_function_data = 'data_output/objective_function'
Path(output_path_objective_function_data).mkdir(parents=True, exist_ok=True)

output_path_objective_function_plot = 'data_output/objective_function_plot'
Path(output_path_objective_function_plot).mkdir(parents=True, exist_ok=True)

mapping_params = [0.32892136, 0.56140356, 0.74925642]  # [0.18143242, 0.572626, 0.77368778]
current_dataset = 'dwug_en'
current_mode = 'Nelder-Mead'


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


def redo_mapping_co_sim_to_label_for_file(path):
    pairs = load_csv_file(path)
    for pair in pairs:
        pair['mapped_label'] = mapping_co_sim_to_label_predict(pair['cosine_similarity'])
    write_file(path, pairs, pairs[0].keys())


def attack_and_write_pairs_challenges(pairs, name):
    number_of_pairs = len(pairs)
    print('Generating cosine similarity for ', number_of_pairs, ' pairs ...')
    if pairs.__len__() == 0:
        return
    data = []
    count = 1

    # Initial call to print 0% progress
    # printProgressBar(0, number_of_pairs, prefix='Progress:', suffix='Complete', length=50)

    for pair in pairs:
        if str(pair['context1']).__len__() > 512 or pair['context2'].__len__() > 512:
            print('Context too long, skipping pair')
            continue

        lemma = pair['lemma']

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
        print('generating pair', count, ' of ', number_of_pairs)
        # printProgressBar(count, number_of_pairs, prefix='Progress:', suffix='Complete', length=50)

        data.append(output_file)

    write_file(os.path.join(output_path_pairs, 'attacked_pairs_' + name + '.csv'), data, data[0].keys())
    print('Cosine similarity generated. Save to:', output_path_pairs + '/attacked_pairs_' + name + '.csv')


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


def callback(intermediate_result: OptimizeResult):
    global current_mode

    path = os.path.join(output_path_objective_function_data, current_mode + '_objective_function_fitting_data.csv')
    with open(path, "a", encoding="utf-8") as f:
        line = '\t'.join([str(v) for v in [intermediate_result.x.tolist(), -1 * intermediate_result.fun]])
        f.write(line)
        f.write('\n')

    #    print('Current mapping:', intermediate_result.x, 'Value of the objective function:', -1 * intermediate_result.fun,          'Method: Nelder-Mead')


def objective_function_to_minimize(mapping, labels, cos_sim):
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


# Minimizes krippendorff alpha with mapping params as input
def mapping_co_sim_to_label_fit(list_train_data, method='Nelder-Mead', initial_mapping=None):
    if initial_mapping is None:
        initial_mapping = [0.25, 0.5, 0.75]
    global current_mode
    current_mode = method

    # Save minimization data for plotting
    path = os.path.join(output_path_objective_function_data, method + '_objective_function_fitting_data.csv')
    with open(path, "w", encoding="utf-8") as f:
        header = '\t'.join(['current_mapping', 'objective_function_value'])
        f.write(header)
        f.write('\n')

    # Initial guess for the mapping parameters
    # initial_mapping =

    # Labels and cosine similarities
    labels = np.array([float(pair['judgment']) for pair in list_train_data])
    cos_sim = np.array([float(pair['cosine_similarity']) for pair in list_train_data])

    # Find the mapping parameters that maximize the Krippendorff's alpha coefficient
    result = minimize(objective_function_to_minimize, initial_mapping, args=(labels, cos_sim), method=method,
                      callback=callback)
    # todo sanity checks

    initial_mapping_str = '_'.join([str(value) for value in initial_mapping])
    new_path = os.path.join(output_path_objective_function_data,
                            method + '_' + initial_mapping_str + '_objective_function_fitting_data.csv')
    try:
        os.rename(path, new_path)
    except FileExistsError:
        os.remove(new_path)
        os.rename(path, new_path)

    global mapping_params
    mapping_params = np.sort(result.x)
    # Print the optimal mapping parameters
    print('Minimization successful:', result.success, 'with method:', method, 'and initial mapping:', initial_mapping)
    print('Optimal mapping parameters:', result.x, 'with value of the objective function:', -1 * result.fun)
    print(' ')


def mapping_co_sim_to_label_predict(cos_sim):
    mapped = np.digitize(float(cos_sim), bins=mapping_params)
    mapped += 1
    return mapped


def generate_krippendorff_coefficient(pairs_mapped):
    labels = [float(pair['judgment']) for pair in pairs_mapped]
    mapped_labels = [float(pair['mapped_label']) for pair in pairs_mapped]

    krippendorff_coefficient = krippendorff.alpha(reliability_data=[labels, mapped_labels],
                                                  level_of_measurement='ordinal')

    print('Krippendorff coefficient:', krippendorff_coefficient)
    return krippendorff_coefficient


def test_different_methods_and_init_mappings():
    mappings = [[0.32892136, 0.56140356, 0.74925642], [0.18143242, 0.572626, 0.77368778], [0.25, 0.5, 0.75],
                [0.25246081, 0.4095611, 0.68803579], [0.1, 0.5, 0.9], [0.1, 0.3, 0.7], [0.2, 0.4, 0.8]]
    for i in range(1, 10):
        d = 0.1 * i
        temp_mapping = [d, d + (d / (2 * 9)), d + (d / 9)]
        mappings.append(temp_mapping)
    print(mappings)
    for method in ['Nelder-Mead', 'Powell']:
        for mapping in mappings:
            try:
                mapping_co_sim_to_label_fit(load_csv_file('data_output/attacked_pairs/attacked_pairs_dwug_de.csv'),
                                            method=method, initial_mapping=mapping)
            except Exception:
                print('Error in method:', method, 'with mapping:', mapping)
                continue


def plot_objective_function_values(mapping_params, function_values, name):
    plt.plot(mapping_params, function_values)
    plt.xlabel('Mapping parameters')
    plt.ylabel('Objective function value')
    plt.title('Objective function value for different mapping parameters')
    plt.savefig(os.path.join(output_path_objective_function_plot, name + '_plot.png'))
    plt.close()


def plot_all(path):
    file_names = os.listdir(path)
    for file_name in file_names:
        if file_name.__contains__('objective_function_fitting'):
            data = load_csv_file(os.path.join(path, file_name))
            file_name = file_name.replace('.csv', '')
            mapping_params = [[float(e) for e in
                               mapping['current_mapping'].removeprefix('[').removesuffix(']').replace(' ', '').split(
                                   ',')] for mapping in data]
            mapping_params_index_1 = [mapping[0] for mapping in mapping_params]
            mapping_params_index_2 = [mapping[1] for mapping in mapping_params]
            mapping_params_index_3 = [mapping[2] for mapping in mapping_params]

            mapping_params_strings = [mapping['current_mapping'] for mapping in data]
            function_values = [float(e['objective_function_value']) for e in data]
            plot_objective_function_values(mapping_params_index_1, function_values, file_name + '_index_1')
            plot_objective_function_values(mapping_params_index_2, function_values, file_name + '_index_2')
            plot_objective_function_values(mapping_params_index_3, function_values, file_name + '_index_3')


test_different_methods_and_init_mappings()

# plot_all(output_path_objective_function_data)

# redo_mapping_co_sim_to_label_for_file('data_output/attacked_pairs/attacked_pairs_dwug_en.csv')

# generate_krippendorff_coefficient(load_csv_file('data_output/attacked_pairs/attacked_pairs_dwug_en.csv'))

# write_list_challenges_for_datasets()

# pairs = load_csv_file(use_pairs_input_path)
# attack_and_write_pairs_challenges(pairs)
# analyze_and_write_pairs_challenges(pairs)

# attack_and_write_pairs_challenges(load_csv_file('data_output/pairs_whole_dataset/strict_dwug_de_pairs_challenge.csv'),                                  'dwug_de')
# attack_and_write_pairs_challenges(load_csv_file('data_output/pairs_whole_dataset/strict_dwug_en_pairs_challenge.csv'),                                  'dwug_en')

#  except Exception:         print('Error in method:', method)         continue

# print( mapping_co_sim_to_label_predict(load_csv_file('data_output/attacked_pairs/attacked_pairs_attack.csv')))
