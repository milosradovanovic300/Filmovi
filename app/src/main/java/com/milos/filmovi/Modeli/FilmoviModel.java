package com.milos.filmovi.Modeli;

public class FilmoviModel {

    public String id, naziv, slika, link, reditelj, glumci, opis, audio, ocenaimdb, kategorija, trajanje;
    public int broj;


    public FilmoviModel(String id, String naziv, String slika, String link, String reditelj, String glumci, String opis, String audio, String ocenaimdb, String kategorija, String trajanje, int broj)
    {
        this.id = id;
        this.naziv = naziv;
        this.slika = slika;
        this.link = link;
        this.reditelj = reditelj;
        this.glumci = glumci;
        this.opis = opis;
        this.audio = audio;
        this.ocenaimdb = ocenaimdb;
        this.kategorija = kategorija;
        this.trajanje = trajanje;
        this.broj = broj;
    }
    public FilmoviModel(){}
    public String getId()
    {
        return id;
    }
    public String getNaziv()
    {
        return naziv;
    }
    public String getSlika()
    {
        return slika;
    }
    public String getLink()
    {
        return link;
    }
    public String getReditelj()
    {
        return reditelj;
    }
    public String getGlumci()
    {
        return glumci;
    }
    public String getOpis()
    {
        return opis;
    }
    public String getAudio()
    {
        return audio;
    }
    public String getOcenaimdb()
    {
        return ocenaimdb;
    }
    public String getKategorija()
    {
        return kategorija;
    }
    public String getTrajanje()
    {
        return trajanje;
    }

    public int getBroj(){return broj;}


}
