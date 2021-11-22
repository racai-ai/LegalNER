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

### Citing
ACL Anthology URL: https://aclanthology.org/2021.nllp-1.2/

Citation:
```
Păiș, Vasile and Mitrofan, Maria and Gasan, Carol Luca and Coneschi, Vlad and Ianov, Alexandru. Named Entity Recognition in the Romanian Legal Domain. In Proceedings of the Natural Legal Language Processing Workshop 2021. Association for Computational Linguistics, Punta Cana, Dominican Republic, pp. 9--18, nov 2021, https://aclanthology.org/2021.nllp-1.2
```

Bibtex:
```
@inproceedings{pais-etal-2021-named,
    title = "Named Entity Recognition in the {R}omanian Legal Domain",
    author = "Păiș, Vasile  and
      Mitrofan, Maria  and
      Gasan, Carol Luca  and
      Coneschi, Vlad  and
      Ianov, Alexandru",
    booktitle = "Proceedings of the Natural Legal Language Processing Workshop 2021",
    month = nov,
    year = "2021",
    address = "Punta Cana, Dominican Republic",
    publisher = "Association for Computational Linguistics",
    url = "https://aclanthology.org/2021.nllp-1.2",
    pages = "9--18",
}
```
### Dataset
The LegalNERo dataset is available here: https://doi.org/10.5281/zenodo.4772094

Dataset page on PapersWithCode: https://paperswithcode.com/dataset/legalnero
