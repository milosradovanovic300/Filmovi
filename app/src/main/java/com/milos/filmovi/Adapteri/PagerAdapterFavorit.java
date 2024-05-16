package com.milos.filmovi.Adapteri;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.milos.filmovi.FragmentFilmovi;

public class PagerAdapterFavorit extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentFilmovi fragmentFilmovi;
    public PagerAdapterFavorit(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putBoolean("serije", false);
        switch (position) {
            case 0:

                fragmentFilmovi = new FragmentFilmovi();
                fragmentFilmovi.setArguments(bundle);
                fragmentFilmovi.title = "Favoriti";
                return fragmentFilmovi;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}