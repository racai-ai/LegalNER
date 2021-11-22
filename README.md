# LegalNER
NER in the Romanian Legal domain

### Utils

```
java -cp utils.jar KeepLargestAnn <ann_folder>
```

Will process all the files in <ann_folder> and remove smaller entities embedded into larger ones. Make sure to have a backup of your original files as they will get overwritten.


### NER
For the experiments we used NeuroNER (http://neuroner.com/) and PharmaCoNER-Tagger (https://github.com/TeMU-BSC/PharmaCoNER-Tagger.git).
We employed embeddings models trained on the MARCELL-RO corpus and modified the NER packages to support Romanian language where needed.
Additionally we created server versions of the packages allowing the models to be served online. 

A demo is available in the RELATE platform: https://relate.racai.ro/index.php?path=ner/demo
(on the same demo page pre-trained models and embeddings can be downloaded)
