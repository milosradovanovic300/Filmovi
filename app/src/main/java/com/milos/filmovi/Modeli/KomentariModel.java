package com.milos.filmovi.Modeli;

public class KomentariModel {
    public String userName, komentar, photoUrl, time;
    public KomentariModel (String photoUrl, String userName, String komentar, String time)
    {
        this.userName = userName;
        this.komentar = komentar;
        this.photoUrl = photoUrl;
        this.time = time;
    }
    public KomentariModel(){}

    public String getUserName()
    {
        return userName;
    }
    public String getKomentar()
    {
        return komentar;
    }
}
