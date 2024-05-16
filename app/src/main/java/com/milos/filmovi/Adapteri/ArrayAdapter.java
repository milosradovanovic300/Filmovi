package com.milos.filmovi.Adapteri;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.milos.filmovi.Modeli.FilmoviModel;
import com.milos.filmovi.R;

import java.util.List;

/**
 * Created by Milos Radovanovic on 9/14/2017.
 */

public class ArrayAdapter extends BaseAdapter {


    private Context context;

    private List<FilmoviModel> filmoviModels;

    public ArrayAdapter(Context context, List<FilmoviModel> filmoviModels)
    {
        this.context = context;
        this.filmoviModels = filmoviModels;
    }
    @Override
    public int getCount() {
        return filmoviModels.size();
    }

    @Override
    public Object getItem(int i) {
        return filmoviModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    ImageView imageView;
     @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.array_adapter, viewGroup, false);
        ((TextView) view.findViewById(R.id.tvNaziv)).setText(filmoviModels.get(i).naziv);
        ((TextView) view.findViewById(R.id.tvImdb)).setText(" " + filmoviModels.get(i).ocenaimdb + "/10");
        String redni = String.valueOf(i+1);
        ((TextView) view.findViewById(R.id.tvNumber)).setText(redni+".");
         imageView = (ImageView) view.findViewById(R.id.ivSlika);
         //Glide.with(context).load(filmoviModels.get(i).slika).into(imageView);

         imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return view;
    }


}
