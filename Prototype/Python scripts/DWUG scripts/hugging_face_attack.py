import os
import csv
from pathlib import Path

use_pairs_input_path = 'data_output/dwug_en/data/afternoon_nn/pairs_clean.csv'
output_path = 'data_output/attack_pairs'
Path(output_path).mkdir(parents=True, exist_ok=True)


def load_pairs(file_path):
    pairs = []
    with open(file_path, encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter='\t', quoting=csv.QUOTE_NONE, strict=True)
        for row in reader:
            pairs.append(row)
    return pairs


pairs = load_pairs(use_pairs_input_path)
