package com.milos.filmovi.Adapteri;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.milos.filmovi.Modeli.KomentariModel;
import com.milos.filmovi.R;

import java.util.List;

public class ArrayAdapterKomentari extends BaseAdapter {


    private Context context;

    private List<KomentariModel> komentariModels;

    public ArrayAdapterKomentari(Context context, List<KomentariModel> komentariModels)
    {
        this.context = context;
        this.komentariModels = komentariModels;
    }
    @Override
    public int getCount() {
        return komentariModels.size();
    }

    @Override
    public Object getItem(int i) {
        return komentariModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.array_adapter_komentari, viewGroup, false);

        ((TextView) view.findViewById(R.id.tvUserName)).setText(komentariModels.get(i).userName);
        ((TextView) view.findViewById(R.id.tvKomentar)).setText(komentariModels.get(i).komentar);
        ((TextView) view.findViewById(R.id.tvDatum)).setText(komentariModels.get(i).time);

        ImageView imageView = (ImageView) view.findViewById(R.id.ivSlikaUser);
        Glide.with(context).load(komentariModels.get(i).photoUrl).apply(new RequestOptions().circleCrop()).into(imageView);

        return view;
    }


}
