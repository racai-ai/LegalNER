#!/bin/sh

source /data/LegalNERo/venv_pharmaconer/bin/activate

/data/LegalNERo/venv_pharmaconer/bin/python3 ner_server_nolegal.py  \
    --parameters_filepath=/data/LegalNERo/PharmaCoNER-Tagger/trained_models/no_legal_CoRoLa+MARCELL_1_1/parameters.ini \
    --pretrained_model_folder=/data/LegalNERo/PharmaCoNER-Tagger/trained_models/no_legal_CoRoLa+MARCELL_1_1/ \
    --use_pretrained_model=True \
    --train_model=False

