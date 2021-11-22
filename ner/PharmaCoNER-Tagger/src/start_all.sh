#!/bin/sh

source /data/LegalNERo/venv_pharmaconer/bin/activate

/data/LegalNERo/venv_pharmaconer/bin/python3 ner_server_all.py \
    --parameters_filepath=/data/LegalNERo/PharmaCoNER-Tagger/trained_models/all_entities_MARCELL_1_0/parameters.ini \
    --pretrained_model_folder=/data/LegalNERo/PharmaCoNER-Tagger/trained_models/all_entities_MARCELL_1_0/ \
    --use_pretrained_model=True \
    --train_model=False


