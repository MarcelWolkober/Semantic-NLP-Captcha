# Create new env ( RUN FILE AS 'source install_packages' on linux or DELETE ROW 'conda init' and run 'conda init' or 'conda init bash' beforehand in separete shell on Windows)
conda init
conda config --set auto_activate_base false
conda create --name nlp_captcha

# Install pip
conda install -n nlp_captcha pip
conda install -n nlp_captcha git pip


#conda activate nlp_captcha

#Install packages
pip install setuptools
pip install wheel

pip install numpy
pip install pandas
pip install matplotlib
pip install krippendorff
pip install git+https://github.com/pierluigic/xl-lexeme
