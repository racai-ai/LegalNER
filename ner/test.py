#!/usr/bin/env python
# -*- coding: utf-8 -*-

from pathlib import Path
txt = Path('test3.txt').read_text()

from neuroner import neuromodel
nn = neuromodel.NeuroNER(train_model=False, use_pretrained_model=True, pretrained_model_folder="./trained_models/legalnero_legal_per_loc_org_time")
#nn = neuromodel.NeuroNER(train_model=False, use_pretrained_model=True, pretrained_model_folder="./trained_models/legalnero_per_loc_org_time")

nn.fit()

r=nn.predict(text=txt)
print(r)
