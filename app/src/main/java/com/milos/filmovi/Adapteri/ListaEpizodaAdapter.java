package com.milos.filmovi.Adapteri;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.milos.filmovi.R;
import com.milos.filmovi.Modeli.SezoneModel;
import com.milos.filmovi.WatchMovie;

import java.util.ArrayList;
import java.util.List;

public class ListaEpizodaAdapter extends RecyclerView.Adapter<ListaEpizodaAdapter.AdViewHolder>  {

    private Context mCtx;
    private List<SezoneModel> adModelList;
    public String pretraga = "";
    private ProgressDialog pd;

    public ListaEpizodaAdapter(Context mCtx, List<SezoneModel> adModelList) {
        this.mCtx = mCtx;
        this.adModelList = adModelList;
        pd = new ProgressDialog(mCtx);
        pd.dismiss();
        pd.setMessage("Ucitavanje...");

    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_ads, viewGroup, false);
        return new AdViewHolder(view, mCtx, adModelList);
    }


    @Override
    public void onBindViewHolder(@NonNull AdViewHolder adViewHolder, int i) {

        SezoneModel adModel = adModelList.get(i);
        int epizoda = i+1;
        adViewHolder.textViewTitle.setText("Epizoda " + epizoda);
        adViewHolder.textViewCity.setText(adModel.getNaziv());
    }


    @Override
    public int getItemCount() {
        return adModelList.size();
    }



    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewCity;
        List<SezoneModel> adModelList = new ArrayList<>();
        Context ctx;

        private InterstitialAd mInterstitialAd;


        public AdViewHolder(@NonNull View itemView, Context ctx, List<SezoneModel> adModelList) {

            super(itemView);
            this.adModelList = adModelList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);



            textViewTitle = itemView.findViewById(R.id.textViewListItemAdsTitle);
            textViewCity = itemView.findViewById(R.id.textViewListItemAdsCity);


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(ctx,"ca-app-pub-3940256099942544/1033173712", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                   // Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                //    Log.d(TAG, "Ad dismissed fullscreen content.");
                    pd.dismiss();
                    int position = getAdapterPosition();
                    SezoneModel adModel = adModelList.get(position);
                    Intent intent = new Intent(ctx, WatchMovie.class);
                    intent.putExtra("url", adModel.getLink());
                    ctx.startActivity(intent);
                    mInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                   // Log.e(TAG, "Ad failed to show fullscreen content.");
                    pd.dismiss();
                    int position = getAdapterPosition();
                    SezoneModel adModel = adModelList.get(position);
                    Intent intent = new Intent(ctx, WatchMovie.class);
                    intent.putExtra("url", adModel.getLink());
                    ctx.startActivity(intent);
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                  //  Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    //Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        }
        @Override
        public void onClick(View view) {

            pd.show();
            if (mInterstitialAd != null) {
                //mInterstitialAd.show();
            } else {

            }
        }
    }
}