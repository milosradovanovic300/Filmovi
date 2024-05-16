/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milos.filmovi.Adapteri;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.milos.filmovi.Modeli.FilmoviModel;
import com.milos.filmovi.OpisActivity;
import com.milos.filmovi.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<Object> mRecyclerViewItems;
    private boolean serije, domace_serije;

    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems, boolean serije, boolean domace_serije) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
        this.serije = serije;
        this.domace_serije = domace_serije;
    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNaziv, tvImdb, tvNumber;
        ImageView ivSlika;
        List<Object> adModelList;
        Context ctx;

        public AdViewHolder(@NonNull View itemView, Context ctx, List<Object> adModelList) {

            super(itemView);
            this.adModelList = adModelList;
            this.ctx=ctx;
            itemView.setOnClickListener(this);

            ivSlika = itemView.findViewById(R.id.ivSlika);
            tvNaziv = itemView.findViewById(R.id.tvNaziv);
            tvImdb = itemView.findViewById(R.id.tvImdb);
            tvNumber = itemView.findViewById(R.id.tvNumber);

        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            FilmoviModel filmoviModel = (FilmoviModel) adModelList.get(position);
            Intent intent;
            intent = new Intent(this.ctx, OpisActivity.class);
            intent.putExtra("id", filmoviModel.getId());
            intent.putExtra("naziv", filmoviModel.getNaziv());
            intent.putExtra("slika", filmoviModel.getSlika());
            intent.putExtra("url", filmoviModel.getLink());
            intent.putExtra("reditelj", filmoviModel.getReditelj());
            intent.putExtra("glumci", filmoviModel.getGlumci());
            intent.putExtra("opis", filmoviModel.getOpis());
            intent.putExtra("audio", filmoviModel.getAudio());
            intent.putExtra("ocenaimdb", filmoviModel.getOcenaimdb());
            intent.putExtra("kategorija", filmoviModel.getKategorija());
            intent.putExtra("trajanje", filmoviModel.getTrajanje());
            this.ctx.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.array_adapter, viewGroup, false);

            return new AdViewHolder(menuItemLayoutView, mContext, mRecyclerViewItems);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            AdViewHolder menuItemHolder = (AdViewHolder) holder;
            FilmoviModel menuItem = (FilmoviModel) mRecyclerViewItems.get(position);

            Glide.with(mContext).load(menuItem.getSlika()).apply(new RequestOptions().override(600, 200).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(menuItemHolder.ivSlika);

            menuItemHolder.ivSlika.setScaleType(ImageView.ScaleType.FIT_XY);
            menuItemHolder.tvNaziv.setText(menuItem.getNaziv());
            menuItemHolder.tvImdb.setText(menuItem.getOcenaimdb());
            menuItemHolder.tvNumber.setText("" + menuItem.getBroj());
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        AdViewHolder menuItemHolder = (AdViewHolder) holder;
        {
            try{
                Glide.with(mContext).clear(menuItemHolder.ivSlika);
            } catch (IllegalArgumentException e){
                // do nothing
            }
        }

    }

}
