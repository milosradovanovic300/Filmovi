package com.milos.filmovi.Modeli;

public class SezoneModel {

    public String naziv, link, slika;


    public SezoneModel(String naziv, String link, String slika)
    {
        this.naziv = naziv;
        this.link = link;
        this.slika = slika;
    }
    public SezoneModel(){}
    public String getNaziv()
    {
        return naziv;
    }
    public String getLink()
    {
        return link;
    }
    public String getSlika(){return slika;}
}
