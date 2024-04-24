# Create new env ( CONDA INIT NEEDED BEFORE )
conda config --set auto_activate_base false
conda create --name nlp_captcha2

# Install pip
conda install -n nlp_captcha2 pip
conda install -n nlp_captcha2 git pip


conda activate nlp_captcha2

#Install packages
pip install setuptools
pip install wheel

pip install numpy
pip install pandas
pip install krippendorff
pip install git+https://github.com/pierluigic/xl-lexeme