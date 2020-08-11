package com.android.celltechmobileservicesapp.json;


import java.io.Serializable;
import java.util.List;

public class Params implements Serializable {
    private List<Algorithm> algorithms;

    public List<Algorithm> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<Algorithm> value) {
        this.algorithms = value;
    }
}
