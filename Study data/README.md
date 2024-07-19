# Folder structure

The study data is divided into two parts: the _study challenges_ and the _study evaluation_

## Study challenges

This part contains the data for the study challenges.

They consist of their individual parts _usage pair_, _pair challenge_, _list challenge_, and the combination of pair and list challenge called _combined challenge_.

Their structure is a CSV file with a header and a _tab_ as the column separation. The current Captcha needs this header and separator to read the challenges correctly.

## Study evaluation

### Results

This folder contains all the unfiltered data of the study participant results. 

They are structured as CSV files with headers and _tab_ as operator. The connection of the files is done by the same name in the header. E.g.:

The folder "study-results.csv" refers to the files "pair-challenge-results.csv", "list-challenge-results.csv", and "user-feedback.csv" with their different names in the header.


### Data

This folder contains all the **filtered** data, evaluated for one metric at once.

The file "study_evaluation.csv" contains the overall participant evaluation, and the other subfolders categorize the data.

All the CSV files have a header and use a _tab_.

### Grouped Data

Generally, the same as the data folder, but with evaluation for more than one metric.


### Multi box plots

Here all of the generated box plots can be found. They are categorized into the box plots showing the overall results and into the ones showing the results for individual challenges.
