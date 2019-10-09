from generate_gsearch_judgements import generate_gsearch_judgements
from train import train

#
# The main GSearch script.
#
#  1) Generates the judgment list based on the clicks tracking data
#  2) Collects the features and trains the model
#  3) Uploads the data to ES 
#

if __name__ == "__main__":
	generate_gsearch_judgements()
	train()