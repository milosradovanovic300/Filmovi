package com.milos.filmovi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import android.os.Handler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;



import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.milos.filmovi.Adapteri.PagerAdapter;
import com.milos.filmovi.Adapteri.PagerAdapterFavorit;
import com.milos.filmovi.Adapteri.RecyclerViewAdapter;
import com.milos.filmovi.Modeli.FilmoviModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.WindowManager;

import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TabLayout tabLayout;
    private List<FilmoviModel> listaFilmova;
    private RecyclerViewAdapter arrayAdapter;
    private RelativeLayout rlLoading, rlSearch, rlContainer;
    private RecyclerView listView;
    AppUpdateManager appUpdateManager;

    Handler handler;


    private NavigationView navigationView;
    private Menu nav_Menu;
    private ViewPager viewPager;

    private String id, slika, opis, link, kategorija, naziv, reditelj, glumci, audio, imdb, min;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        //new JsonTask().execute("https://drive.google.com/file/d/1guV9un_S2D-MehPGoMoO2-8qiiBNiAmL/view?usp=sharing");
        appUpdateManager = AppUpdateManagerFactory.create(this);
// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            REQUEST_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        handler = new Handler();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Strani Filmovi");
        FirebaseApp.initializeApp(this);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        nav_Menu = navigationView.getMenu();


        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        rlLoading = (RelativeLayout) findViewById(R.id.rlLoading);
        listView =  findViewById(R.id.rvSearch);

      /*  Ion.with(this).load("https://www.filmovi.me/law-order-organized-crime-s01e06-2021").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

                String page = result;

                String opis = page.substring(page.indexOf("<div class=\"opis\">")+1, page.indexOf("</p>"));
                
                System.out.println(opis);

            }
        });*/



        filmoviSelected(false, false, false);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();

                    }
                    if (deepLink != null) {


                        Intent intent = getIntent();
                        Uri data = intent.getData();

                        //IfDeepLink


                        id = data.getQueryParameter("id");
                        naziv = data.getQueryParameter("naziv");
                        kategorija = data.getQueryParameter("kategorija");
                        reditelj = data.getQueryParameter("reditelj");
                        glumci = data.getQueryParameter("glumci");
                        opis = data.getQueryParameter("opis");
                        audio = data.getQueryParameter("audio");
                        imdb = data.getQueryParameter("imdb");
                        min = data.getQueryParameter("min");
                        slika = data.getQueryParameter("slika");
                        link = data.getQueryParameter("link");
                        String tip = data.getQueryParameter("tip");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(MainActivity.this, OpisActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("naziv", naziv);
                                intent.putExtra("slika", slika);
                                intent.putExtra("url", link);
                                intent.putExtra("reditelj", reditelj);
                                intent.putExtra("glumci", glumci);
                                intent.putExtra("opis", opis);
                                intent.putExtra("audio", audio);
                                intent.putExtra("ocenaimdb",imdb);
                                intent.putExtra("kategorija", kategorija);
                                intent.putExtra("trajanje", min);
                                startActivity(intent);


                            }
                        },200);
                    } else {
                    }
                    // [END_EXCLUDE]
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


        /*try {
            listaFilmova = new ArrayList<>();
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray filmovi = obj.getJSONArray(klasa);


            for (int i = 0; i < filmovi.length(); i++) {
                JSONObject c = filmovi.getJSONObject(i);


                String id = c.getString("id");
                String naziv = c.getString("naziv");
                String audio = c.getString("audio");
                String glumci = c.getString("glumci");
                String kategorija = c.getString("kategorija");
                String link = "";
                if(serije==false&&domace_serije==false)
                    link = c.getString("link");
                String ocenaimdb = c.getString("ocenaimdb");
                String opis = c.getString("opis");
                String reditelj = c.getString("reditelj");
                String slika = c.getString("slika");
                String trajanje = c.getString("trajanje");
                // Phone node is JSON Object
                // tmp hash map for single contact
                HashMap<String, String> filmovi_hash_map = new HashMap<>();

                // adding each child node to HashMap key => value

                FilmoviModel filmoviModel = new FilmoviModel(id, naziv, slika, link, reditelj, glumci, opis, audio, ocenaimdb, kategorija, trajanje, i+1);

                listaFilmova.add(filmoviModel);
            }
            Collections.reverse(listaFilmova);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        new getFilmovi(klasa).execute();
        // [END get_deep_link]
    }
    ProgressDialog pd;
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = MainActivity.this.getAssets().open("filmovi-43883-export.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    boolean domace_serije = false, domaci_filmovi = false, serije = false;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            REQUEST_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });


        handler = new Handler();




        Toolbar toolbar = findViewById(R.id.toolbar);
        if(domaci_filmovi==true)
            setTitle("Domaci Filmovi");
        else if(serije==true)
            setTitle("Strane Serije");
        else if (domace_serije==true)
            setTitle("Domace Serije");
        else
            setTitle("Strani Filmovi");
        FirebaseApp.initializeApp(this);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        nav_Menu = navigationView.getMenu();

        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        rlLoading = (RelativeLayout) findViewById(R.id.rlLoading);
        listView =  findViewById(R.id.rvSearch);




        filmoviSelected(serije, domaci_filmovi, domace_serije);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                        }
                        if (deepLink != null) {


                            Intent intent = getIntent();
                            String action = intent.getAction();
                            Uri data = intent.getData();
                            String permLink = deepLink.toString().split("\\?")[0];



                            //deepLink1 = true;


                            id = deepLink.getQueryParameter("id");
                            naziv = deepLink.getQueryParameter("naziv");
                            kategorija = deepLink.getQueryParameter("kategorija");
                            reditelj = deepLink.getQueryParameter("reditelj");
                            glumci = deepLink.getQueryParameter("glumci");
                            opis = deepLink.getQueryParameter("opis");
                            audio = deepLink.getQueryParameter("audio");
                            imdb = deepLink.getQueryParameter("imdb");
                            min = deepLink.getQueryParameter("min");
                            slika = deepLink.getQueryParameter("slika");
                            link = deepLink.getQueryParameter("link");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(MainActivity.this, OpisActivity.class);
                                    intent.putExtra("id", id);
                                    intent.putExtra("naziv", naziv);
                                    intent.putExtra("slika", slika);
                                    intent.putExtra("url", link);
                                    intent.putExtra("reditelj", reditelj);
                                    intent.putExtra("glumci", glumci);
                                    intent.putExtra("opis", opis);
                                    intent.putExtra("audio", audio);
                                    intent.putExtra("ocenaimdb",imdb);
                                    intent.putExtra("kategorija", kategorija);
                                    intent.putExtra("trajanje", min);
                                    startActivity(intent);


                                }
                            },200);
                        } else {
                        }
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        new getFilmovi(klasa).execute();
    }






    private final static int REQUEST_UPDATE = 255,PERMISSION_REQUEST_CODE = 1;


    public void filmoviSelected(boolean serije, boolean domaci_filmovi, boolean domace_serije)
    {

        tabLayout.removeAllTabs();

        favoritiSelected = false;
        tabLayout.addTab(tabLayout.newTab().setText("Novo"));
        tabLayout.addTab(tabLayout.newTab().setText("Akcija"));
        tabLayout.addTab(tabLayout.newTab().setText("Avantura"));
        tabLayout.addTab(tabLayout.newTab().setText("Animirani"));
        tabLayout.addTab(tabLayout.newTab().setText("Biografski"));
        tabLayout.addTab(tabLayout.newTab().setText("Komedija"));
        tabLayout.addTab(tabLayout.newTab().setText("Kriminalisticki"));
        tabLayout.addTab(tabLayout.newTab().setText("Dokumentarni"));
        tabLayout.addTab(tabLayout.newTab().setText("Drama"));
        tabLayout.addTab(tabLayout.newTab().setText("Porodicni"));
        tabLayout.addTab(tabLayout.newTab().setText("Fantazija"));
        tabLayout.addTab(tabLayout.newTab().setText("Istorijski"));
        tabLayout.addTab(tabLayout.newTab().setText("Horor"));
        tabLayout.addTab(tabLayout.newTab().setText("Misterija"));
        tabLayout.addTab(tabLayout.newTab().setText("Romantika"));
        tabLayout.addTab(tabLayout.newTab().setText("SciFi"));
        tabLayout.addTab(tabLayout.newTab().setText("Sportski"));
        tabLayout.addTab(tabLayout.newTab().setText("Triler"));
        tabLayout.addTab(tabLayout.newTab().setText("Ratni"));
        tabLayout.addTab(tabLayout.newTab().setText("Western"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.getTabAt(0).select();
            }
        }, 100);



        PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), serije, domaci_filmovi, domace_serije);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        adapter.notifyDataSetChanged();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    private boolean favoritiSelected = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (favoritiSelected==true)
        {
            navigationView.setCheckedItem(R.id.nav_filmovi);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_filmovi));

        }
        else if (rlSearch!=null&&rlSearch.getVisibility()==View.VISIBLE)
        {
            rlSearch.setVisibility(View.GONE);
            rlContainer.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
            this.finish();
        }
    }



    private List<Object> mRecyclerViewItems = new ArrayList<>();


    private class getFilmovi extends AsyncTask<Void, Void, Void> {

        private String klasa = "";
        public getFilmovi(String klasa)
        {
            this.klasa = klasa;


        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://firebasestorage.googleapis.com/v0/b/filmovi-43883.appspot.com/o/test.json?alt=media&token=7ebc23a1-637e-49af-b7d9-79156e174024";
            String jsonStr = sh.makeServiceCall(url);
            listaFilmova = new ArrayList<>();
            //listaFilmova.clear();

            if (jsonStr != null) {
                try {


                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node

                    JSONArray filmovi = new JSONObject(jsonStr).getJSONArray(klasa);


                    for (int i = 0; i < filmovi.length(); i++) {
                        JSONObject c = filmovi.getJSONObject(i);


                        String id = c.getString("id");
                        String naziv = c.getString("naziv");
                        String audio = c.getString("audio");
                        String glumci = c.getString("glumci");
                        String kategorija = c.getString("kategorija");
                        String link = "";
                        if(serije==false&&domace_serije==false)
                            link = c.getString("link");
                        String ocenaimdb = c.getString("ocenaimdb");
                        String opis = c.getString("opis");
                        String reditelj = c.getString("reditelj");
                        String slika = c.getString("slika");
                        String trajanje = c.getString("trajanje");
                        // Phone node is JSON Object
                        // tmp hash map for single contact
                        HashMap<String, String> filmovi_hash_map = new HashMap<>();

                        // adding each child node to HashMap key => value

                        FilmoviModel filmoviModel = new FilmoviModel(id, naziv, slika, link, reditelj, glumci, opis, audio, ocenaimdb, kategorija, trajanje, i+1);

                        listaFilmova.add(filmoviModel);
                    }
                    Collections.reverse(listaFilmova);

                } catch (final JSONException e) {

                    System.out.println("" + e.getMessage()) ;
                }

            } else {

               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
    private String klasa = "Filmovi";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity1, menu);

        // Creates instance of the manager.

        myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();



        myActionMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(serije==true)
                    klasa = "Serije";
                else if (domaci_filmovi==true)
                    klasa = "DomaciFilmovi";
                else if (domace_serije==true)
                    klasa = "DomaceSerije";
                else
                    klasa = "Filmovi";
                new getFilmovi(klasa).execute();
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {


                rlLoading.setVisibility(View.VISIBLE);

                mRecyclerViewItems.clear();

                arrayAdapter = new RecyclerViewAdapter(MainActivity.this, mRecyclerViewItems, serije, domace_serije);
                listView.setAdapter(arrayAdapter);
                listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                if(listaFilmova!=null&&listaFilmova.size()>0) {
                    for (int i = 0; i < listaFilmova.size(); i++) {
                        if (listaFilmova.get(i).naziv.toLowerCase().contains(newText.toLowerCase())) {
                            FilmoviModel filmoviModel = new FilmoviModel(
                                    listaFilmova.get(i).id,
                                    listaFilmova.get(i).naziv,
                                    listaFilmova.get(i).slika,
                                    listaFilmova.get(i).link,
                                    listaFilmova.get(i).reditelj,
                                    listaFilmova.get(i).glumci,
                                    listaFilmova.get(i).opis,
                                    listaFilmova.get(i).audio,
                                    listaFilmova.get(i).ocenaimdb,
                                    listaFilmova.get(i).kategorija,
                                    listaFilmova.get(i).trajanje, i + 1);
                            mRecyclerViewItems.add(filmoviModel);
                        }
                    }

                    arrayAdapter.notifyDataSetChanged();
                    rlLoading.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                rlSearch.setVisibility(View.VISIBLE);
                rlContainer.setVisibility(View.GONE);
                mRecyclerViewItems.clear();
                if(serije==true)
                    klasa = "Serije";
                else if (domaci_filmovi==true)
                    klasa = "DomaciFilmovi";
                else if (domace_serije==true)
                    klasa = "DomaceSerije";
                else
                    klasa = "Filmovi";
                new getFilmovi(klasa).execute();

                mRecyclerViewItems.clear();
                arrayAdapter = new RecyclerViewAdapter(getApplicationContext(), mRecyclerViewItems, serije, domace_serije);
                listView.setAdapter(arrayAdapter);
                listView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                rlSearch.setVisibility(View.GONE);
                rlContainer.setVisibility(View.VISIBLE);
             }
        });
        return true;
    }
    private void chkStatus() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {

        } else if (mobile.isConnectedOrConnecting ()) {
            Toast.makeText(this, "Ukljuceni su vam mobilni podaci. Da biste izbegli uvecani racun za telefon preporucujemo da filmove gledate iskljucive preko WIFI mreze! ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Nema internet konekcije ", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Da biste nastavili koriscenje aplikacije morate prvo uraditi azuriranje!", Toast.LENGTH_SHORT).show();
                finish();

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }



        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Da biste nastavili koriscenje aplikacije morate prvo dozvoliti pristup skladistu uredjaja!", Toast.LENGTH_SHORT).show();
                finish();

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    private SearchView searchView;
    private MenuItem myActionMenuItem;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_search:
                //startSearch();
                rlSearch.setVisibility(View.VISIBLE);
                rlContainer.setVisibility(View.GONE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();


        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {

                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            REQUEST_UPDATE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_filmovi) {

            domaci_filmovi = false;
            domace_serije = false;
            serije = false;
            filmoviSelected(serije, domaci_filmovi, domace_serije);
            setTitle("Strani Filmovi");

            myActionMenuItem.setVisible(true);
        }
        else if (id == R.id.nav_domaci_filmovi)
        {
            domaci_filmovi = true;
            domace_serije = false;
            serije = false;
            filmoviSelected(serije, domaci_filmovi, domace_serije);
            setTitle("Domaci Filmovi");

            myActionMenuItem.setVisible(true);
        }
       else if (id == R.id.nav_o_aplikaciji) {

            new AlertDialog.Builder(this)
                    .setTitle("O aplikaciji")
                    .setMessage("Ova aplikacija ne vrsi stream videa ili hostuje video snimke. Filmove koje gosti gledaju kod nas na aplikaciji su hostovani na sajtovima kao što su openload, vidbul, bestreams, vidzi, thevideo, streamin itd. Svi video zapisi koji se nalaze na tim sajtovima su verovatno odobreni od strane vlasnika istih. Ova aplikacija nije odgovorna za sadrzaj drugih sajtova." +
                            "\n\n This application  is not responsible for the availability of such third party web sites. The content of such third party web sites is beyond our control. We neither endorse, make any representations nor accept any liability (whether direct or indirect) for such third party web sites or their content, products or services offered at those web sites, or by their sponsoring companies.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Zatvori", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
           // rlOaplikaciji.setVisibility(View.VISIBLE);
        }else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String text = "Besplatno gledanje filmova: https://play.google.com/store/apps/details?id=com.milos.filmovi";
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, text);

            startActivity(Intent.createChooser(intent, "Izaberite"));
        }

       else if (id == R.id.nav_kontakt) {

            new AlertDialog.Builder(this)
                    .setTitle("Kontakt")
                    .setMessage("Za sve primedbe i sugestije pisite nam na mail: filmovidroid@gmail.com")
                    .setPositiveButton("Posalji email", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"filmovidroid@gmail.com"});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Primedbe i sugestije" );

                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {

                            }
                        }
                    })
                    .setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        deleteCache(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
        else
            this.finish();
    }
}
