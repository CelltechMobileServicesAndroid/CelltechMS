package com.android.celltechmobileservicesapp.json;

import java.io.Serializable;

public class Algorithm implements Serializable {
    private String name;
    private String run;
    private String duration;

    public Algorithm() {
        this.name = "";
        this.run = "";
        this.duration = "";
    }

    public Algorithm(String name, String run, String duration) {
        this.name = name;
        this.run = run;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String value) {
        this.run = value;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
