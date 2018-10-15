# Naive Bayes Spam Classifier

System to classify .eml files using naive bayes algorithm.

See http://www.math.kit.edu/ianm4/~ritterbusch/seite/spam/de

The system has already been trained using spam and ham mail.
The data is stored in a sqlite database under /data.db

This database is being used to classify further .eml files.
The calibration is done using more .eml tests files which brings the alpha value to 0.0008.
This value may be adjusted accordingly.

## Usage

There are several command for the application using command line arguments.

The first argument always sets the file or directory depending on the command:

* reset - Resets the database configured in Database.java
* classify - Outputs the probability of the .eml file to be a spam
* test - Runs tests over a directory of .eml files and tells the precision
* trainHam - Trains a directory of .eml as ham ! Always make sure to run a reset before training the system
* trainSpam -  Trains a directory of .eml as spam ! Always make sure to run a reset before training the system