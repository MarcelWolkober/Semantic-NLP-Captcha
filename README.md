# Semantic NLU CAPTCHA
This Repository provides the prototype created for my bachelor's thesis at IMS, University Stuttgart.

Current features are only to conduct a study.

## Description
![Prototype-Overview](https://github.com/MarcelWolkober/Semantic-NLP-Captcha/blob/main/Prototype-overview.png)

It consists of two components that run in Docker Containers.

The data folder is the standard location for reading data (currently, the study challenges).

The description of the study data is in its [README](https://github.com/MarcelWolkober/Semantic-NLP-Captcha/blob/dev/Study%20data/README.md).

## Getting Started

### Dependencies

Docker: For the starting of the system as a whole.

Node + npm: To run the Captcha-Frontend without Docker.

Java SDK >= 21: To run the Captcha without Docker.

### Installing

Download and unzip. 

### Executing program

In the prototype folder, open a terminal and run:

```
docker compose up 
```
On Linux, you might need to use _sudo_.

The Captcha-Front is reachable at [http://localhost:8080/study](http://localhost:8080/study)

The Captcha is not supposed to be accessed directly, but is reachable at [http://localhost:8081](http://localhost:8081)


## Authors

[Marcel Wolkober](mailto:st163937@stud.uni-suttgart.de)
