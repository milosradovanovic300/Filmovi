package com.milos.filmovi.Adapteri;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.milos.filmovi.FragmentFilmovi;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentFilmovi fragmentFilmovi;
    boolean serije = false, domaci_filmovi = false, domace_serije = false;
    public PagerAdapter(FragmentManager fm, int NumOfTabs, boolean serije, boolean domaci_filmovi, boolean domace_serije) {
        super(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
        this.serije = serije;
        this.domaci_filmovi = domaci_filmovi;
        this.domace_serije = domace_serije;

    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putBoolean("serije", serije);
        bundle.putBoolean("domaci_filmovi", domaci_filmovi);
        bundle.putBoolean("domace_serije", domace_serije);
        fragmentFilmovi = new FragmentFilmovi();
        fragmentFilmovi.setArguments(bundle);
            switch (position) {
                case 0:
                    fragmentFilmovi.title = "";
                    return fragmentFilmovi;
                case 1:
                    fragmentFilmovi.title = "Action";
                    return fragmentFilmovi;
                //fragmentFilmovi.createList();

                case 2:
                    fragmentFilmovi.title = "Adventure";
                    return fragmentFilmovi;

                case 3:
                    fragmentFilmovi.title = "Animation";
                    return fragmentFilmovi;
                case 4:
                    fragmentFilmovi.title = "Biography";
                    return fragmentFilmovi;
                case 5:
                    fragmentFilmovi.title = "Comedy";
                    return fragmentFilmovi;
                case 6:
                    fragmentFilmovi.title = "Crime";
                    return fragmentFilmovi;
                case 7:
                    fragmentFilmovi.title = "Documentary";
                    return fragmentFilmovi;
                case 8:
                    fragmentFilmovi.title = "Drama";
                    return fragmentFilmovi;
                case 9:
                    fragmentFilmovi.title = "Family";
                    return fragmentFilmovi;
                case 10:
                    fragmentFilmovi.title = "Fantasy";
                    return fragmentFilmovi;
                case 11:
                    fragmentFilmovi.title = "History";
                    return fragmentFilmovi;
                case 12:
                    fragmentFilmovi.title = "Horror";
                    return fragmentFilmovi;
                case 13:
                    fragmentFilmovi.title = "Mystery";
                    return fragmentFilmovi;
                case 14:
                    fragmentFilmovi.title = "Romance";
                    return fragmentFilmovi;
                case 15:
                    fragmentFilmovi.title = "Sci-Fi";
                    return fragmentFilmovi;
                case 16:
                    fragmentFilmovi.title = "Sport";
                    return fragmentFilmovi;
                case 17:
                    fragmentFilmovi.title = "Thriller";
                    return fragmentFilmovi;
                case 18:
                    fragmentFilmovi.title = "War";
                    return fragmentFilmovi;
                case 19:
                    fragmentFilmovi.title = "Western";
                    return fragmentFilmovi;
                default:
                    return null;
            }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}