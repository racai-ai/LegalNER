#!/usr/bin/env python
# -*- coding: utf-8 -*-

from neuroner import neuromodel
#nn = neuromodel.NeuroNER(train_model=False, use_pretrained_model=True, pretrained_model_folder="./trained_models/legalnero_legal_per_loc_org_time")
nn = neuromodel.NeuroNER(train_model=False, use_pretrained_model=True, pretrained_model_folder="./trained_models/legalnero_per_loc_org_time")

nn.fit()


from flask import Flask,request,jsonify

app = Flask(__name__)

@app.route("/api/v1.0/ner", methods=["GET","POST"])
def ner():
    if request.method=="POST":
        textf=request.files['text']
        text=textf.read().decode('utf-8',errors='ignore')
    else:
        text=request.args.get("text")
        text=text.encode("latin1",errors='ignore').decode("utf8",errors='ignore')

    r=nn.predict(text=text)
    return jsonify({'status':'OK','result':r})

app.run(threaded=False, debug=False, host="127.0.0.1", port=5102)
