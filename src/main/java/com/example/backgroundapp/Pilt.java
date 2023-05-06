package com.example.backgroundapp;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pilt {

    private ImageView eelvaade;
    private String tee;
    private String failinimi;

    public Pilt(String tee, String failinimi) {
        setEelvaade(tee);
        this.tee = tee;
        this.failinimi = failinimi;
    }

    public void setEelvaade(String failitee){
        this.eelvaade = new ImageView(new Image(failitee));
        this.eelvaade.setFitWidth(100);
        this.eelvaade.setPreserveRatio(true);
        this.eelvaade.setSmooth(true);
        this.eelvaade.setCache(true);
    }

    public ImageView getEelvaade() {
        return eelvaade;
    }

    public String getTee() {
        return tee;
    }

    public String getNimi() {
        return failinimi;
    }
}