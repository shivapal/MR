from nltk.stem import PorterStemmer 
from nltk.tokenize import word_tokenize 
   
ps = PorterStemmer()

words = ["caresses", "liked", "disabled", "agreed", "retrieved"] 
  
k = map(ps.stem, words)


