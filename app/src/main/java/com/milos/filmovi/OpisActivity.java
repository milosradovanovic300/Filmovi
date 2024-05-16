package com.milos.filmovi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import com.milos.filmovi.Modeli.Favorit;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;


public class OpisActivity extends AppCompatActivity  {

    private String id, slika, opis, link, kategorija, naziv, reditelj, glumci, audio, imdb, min;

    private ImageView ivOpis;
    private TextView tvNaslov,  tvOpis, tvKategorija, tvTitle, tvReditelj, tvGlumci, tvAudio, tvImdb, tvMin;
    private Button  btnPodeli, btnPrijavi;
    private boolean clicked = false;
    private ListaFavorita listaFavorita;
    private RelativeLayout rlKomentari, rlDodajKomentar;

    private Toolbar toolbar;

    private ProgressDialog pd;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opis);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);





        id = getIntent().getStringExtra("id");
        slika = getIntent().getStringExtra("slika");
        opis = getIntent().getStringExtra("opis");
        link = getIntent().getStringExtra("url");
        kategorija = getIntent().getStringExtra("kategorija");
        naziv = getIntent().getStringExtra("naziv");
        reditelj = getIntent().getStringExtra("reditelj");
        glumci = getIntent().getStringExtra("glumci");
        audio = getIntent().getStringExtra("audio");
        imdb = getIntent().getStringExtra("ocenaimdb");
        min = getIntent().getStringExtra("trajanje");


        pd = new ProgressDialog(this);
        pd.dismiss();
        pd.setMessage("Ucitavanje...");




        ivOpis = (ImageView) findViewById(R.id.ivOpis);
        tvOpis = (TextView) findViewById(R.id.tvOpis);
        tvKategorija = (TextView) findViewById(R.id.tvKategorija);
        tvReditelj = (TextView) findViewById(R.id.tvReditelj);
        tvGlumci = (TextView) findViewById(R.id.tvGlume);
        tvAudio = (TextView) findViewById(R.id.tvAudio);
        tvImdb = (TextView) findViewById(R.id.tvImdb);
        tvMin = (TextView) findViewById(R.id.tvMin);
        rlKomentari = findViewById(R.id.rlKomentari);
        rlKomentari.setVisibility(View.GONE);
        rlDodajKomentar = findViewById(R.id.rlDodajKomentar);
        tvNaslov = findViewById(R.id.tvNaslov);
        btnPodeli = findViewById(R.id.btnPodeli);
        btnPrijavi = findViewById(R.id.btnPrijavi);

        toolbar = (Toolbar) findViewById(R.id.toolbar);





        btnPrijavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"neispravanlink@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Prijava neispravnog linka za  " + naziv );

                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }
            }
        });

        btnPodeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClicked();
            }
        });

        Glide.with(OpisActivity.this).load(slika).apply(new RequestOptions().override(1920, 1080).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into(ivOpis);
        tvOpis.setText(opis);
        tvNaslov.setText(naziv);
        tvKategorija.setText(kategorija);
        tvReditelj.setText(reditelj);
        tvAudio.setText(audio);
        tvGlumci.setText(glumci);
        tvImdb.setText(imdb);
        tvMin.setText(min);
        ivOpis.setScaleType(ImageView.ScaleType.FIT_XY);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getBackground().setAlpha(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ivOpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(mInterstitialAd!=null)
                    mInterstitialAd.show(OpisActivity.this);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.load(this,"ca-app-pub-4699102351411089/2647395869", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.

                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Intent intent = new Intent(OpisActivity.this, WatchMovie.class);
                                intent.putExtra("url", link);
                                startActivity(intent);
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.

                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.

                            }
                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        mInterstitialAd = null;
                        Intent intent = new Intent(OpisActivity.this, WatchMovie.class);
                        intent.putExtra("url", link);
                        startActivity(intent);
                    }
                });

                loadAd();
    /*    mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4699102351411089/2647395869");
*/
        /*mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                if(pd!=null)
                    try{
                        pd.dismiss();
                    } catch (IllegalArgumentException e){
                        // do nothing
                    }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                pd.dismiss();

                Intent intent = new Intent(OpisActivity.this, WatchMovie.class);
                intent.putExtra("url", link);
                startActivity(intent);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                pd.dismiss();

                Intent intent = new Intent(OpisActivity.this, WatchMovie.class);
                intent.putExtra("url", link);
                startActivity(intent);
            }
        });*/
    }
    private InterstitialAd mInterstitialAd;

    private void loadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.

                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                pd.dismiss();
                                Intent intent = new Intent(OpisActivity.this, WatchMovie.class);
                                intent.putExtra("url", link);
                                startActivity(intent);
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.

                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.

                            }
                        });

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        mInterstitialAd = null;
                    }
                });

    }
    @Override
    public void onResume()
    {
        super.onResume();
        loadAd();
    }

   @Override
   public void onDestroy()
   {
       super.onDestroy();
   }
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed()
    {
        if(pd.isShowing())
        {
            pd.dismiss();
        }
        else if(rlKomentari.getVisibility()==View.VISIBLE)
        {
            rlKomentari.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
        else
        {
            super.onBackPressed();
            this.finish();
        }
    }

    private void onShareClicked() {
        String permLink = "https://gledaj.page.link/Zi7X" + "?id=" + id
                + "&id=" + id
                + "&tip=filmovi"
                + "&naziv=" + naziv

                + "&kategorija=" + kategorija
                + "&reditelj=" + reditelj
                + "&glumci=" + glumci
                + "&opis=" + opis
                + "&audio=" + audio
                + "&imdb=" + imdb
                + "&min=" + min
                + "&slika=" + slika
                + "&link=" + link;

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(permLink))
                .setDomainUriPrefix("https://gledaj.page.link")
                .setAndroidParameters(new
                        DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(naziv)
                                .setDescription(opis)
                                .setImageUrl(Uri.parse(slika))
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            shareLink(shortLink);

                        } else {
                            // Error
                            // ...
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
        // [END create_short_link]*/
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }
    public void shareLink(Uri myDynamicLink) {
        // [START ddl_share_link]
        Intent sendIntent = new Intent();
        String msg =""+myDynamicLink;
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        // [END ddl_share_link]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity1, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent i= new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }
}
