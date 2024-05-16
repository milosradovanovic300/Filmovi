package com.milos.filmovi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.milos.filmovi.Adapteri.RecyclerViewAdapter;
import com.milos.filmovi.Modeli.Favorit;
import com.milos.filmovi.Modeli.FilmoviModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class FragmentFilmovi extends Fragment {



    private ArrayList<FilmoviModel> listaFilmova, tmpListaFilmova;
    public String title = "";
    private RecyclerView recyclerView;
    private ProgressBar ppLoading;
    private List<Object> mRecyclerViewItems = new ArrayList<>();

    private ProgressDialog pd;

    private ListaFavorita listaFavorita;
    private RecyclerViewAdapter adapter;
    private boolean cekajUcitavanje = false, krajListe = false;

    private int sledeciLimit = 30;

    public boolean serije = false, domaci_filmovi = false, domace_serije = false;

    public FragmentFilmovi(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Ucitavanje...");

        serije = this.getArguments().getBoolean("serije");
        domaci_filmovi = this.getArguments().getBoolean("domaci_filmovi");
        domace_serije = this.getArguments().getBoolean("domace_serije");
        implementScrollListener();
        createList();
        ppLoading = view.findViewById(R.id.ppLoading);
        return view;
    }



    private void implementScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                   // Toast.makeText(getActivity(), "Last", Toast.LENGTH_LONG).show();
                    if(cekajUcitavanje==false&&krajListe==false&&!title.equals("")){
                        if(title.equals("Favoriti"))
                            return;
                        sledeciLimit+=30;
                        cekajUcitavanje = true;
                        updateListView();
                    }
                }
            }
        });
    }

    private void updateListView() {

        pd.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(tmpListaFilmova.size()>=sledeciLimit)
                {
                    for(int i = sledeciLimit-30; i<sledeciLimit; i++)
                    {

                        listaFilmova.add(tmpListaFilmova.get(i));
                        FilmoviModel filmoviModel = new FilmoviModel(
                                tmpListaFilmova.get(i).id,
                                tmpListaFilmova.get(i).naziv,
                                tmpListaFilmova.get(i).slika,
                                tmpListaFilmova.get(i).link,
                                tmpListaFilmova.get(i).reditelj,
                                tmpListaFilmova.get(i).glumci,
                                tmpListaFilmova.get(i).opis,
                                tmpListaFilmova.get(i).audio,
                                tmpListaFilmova.get(i).ocenaimdb,
                                tmpListaFilmova.get(i).kategorija,
                                tmpListaFilmova.get(i).trajanje, i+1);
                        mRecyclerViewItems.add(filmoviModel);
                    }
                    krajListe = false;
                }
                else
                {
                    for(int i = sledeciLimit-30; i<tmpListaFilmova.size(); i++)
                    {

                        listaFilmova.add(tmpListaFilmova.get(i));
                        FilmoviModel filmoviModel = new FilmoviModel(
                                tmpListaFilmova.get(i).id,
                                tmpListaFilmova.get(i).naziv,
                                tmpListaFilmova.get(i).slika,
                                tmpListaFilmova.get(i).link,
                                tmpListaFilmova.get(i).reditelj,
                                tmpListaFilmova.get(i).glumci,
                                tmpListaFilmova.get(i).opis,
                                tmpListaFilmova.get(i).audio,
                                tmpListaFilmova.get(i).ocenaimdb,
                                tmpListaFilmova.get(i).kategorija,
                                tmpListaFilmova.get(i).trajanje, i+1);
                        mRecyclerViewItems.add(filmoviModel);
                    }
                    krajListe = true;
                }

                //NUMBER_OF_ADS+=5;
                //loadNativeAds();
                adapter.notifyDataSetChanged();
                ppLoading.setVisibility(View.GONE);
                cekajUcitavanje = false;
                pd.dismiss();
            }
        }, 1000);
    }

    private String checkStorage() {
        String folderPath = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            folderPath = Environment.getExternalStorageDirectory().getPath() + "/FilmoviFavorites/";

        return folderPath;
    }

    public void createList()
    {

        listaFilmova = new ArrayList<>();
       // arrayAdapter = new ArrayAdapter(getContext(), listaFilmova);

        adapter = new RecyclerViewAdapter(getActivity(), mRecyclerViewItems, serije, domace_serije);

        tmpListaFilmova = new ArrayList<>();
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(serije==true)
            klasa = "Serije";
        else if (domaci_filmovi==true)
            klasa = "DomaciFilmovi";
        else if (domace_serije==true)
            klasa = "DomaceSerije";
        else
            klasa = "Filmovi";

        new getFilmovi().execute();


    }

    private String klasa = "";
    private class getFilmovi extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://firebasestorage.googleapis.com/v0/b/filmovi-43883.appspot.com/o/test.json?alt=media&token=fb2d6890-f9a9-4aaf-851b-9d012a5a6ebc";
            String jsonStr = sh.makeServiceCall(url);
            listaFilmova.clear();

            if (jsonStr != null) {
                try {


                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray filmovi = jsonObj.getJSONArray(klasa);


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

                        FilmoviModel filmoviModel = new FilmoviModel(id, naziv, slika, link, reditelj, glumci, opis, audio, ocenaimdb, kategorija, trajanje, i+1);

                        if(title.equals("Favoriti"))
                        {
                            tmpListaFilmova.add(filmoviModel);
                        }
                        else{
                            if(kategorija!=null&&kategorija.contains(title))
                            {
                                tmpListaFilmova.add(filmoviModel);
                            }
                        }
                    }
                    Collections.reverse(tmpListaFilmova);


                } catch (final JSONException e) {

                    System.out.println("" + e.getMessage()) ;
                }

            } else {

                    System.out.println("This couldn't be more boring");

                       System.out.println("Couldn't get json from server. Check LogCat for possible errors!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


                if(tmpListaFilmova.size()>=sledeciLimit)
                {
                    for(int i = 0; i<sledeciLimit; i++)
                    {
                        listaFilmova.add(tmpListaFilmova.get(i));
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
                                listaFilmova.get(i).trajanje, i+1);
                        mRecyclerViewItems.add(filmoviModel);
                    }
                }
                else
                {
                    int test = 0;
                    for(int i = 0; i<tmpListaFilmova.size(); i++)
                    {

                        listaFilmova.add(tmpListaFilmova.get(i));
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
                                listaFilmova.get(i).trajanje, i+1);
                        mRecyclerViewItems.add(filmoviModel);


                    }
                }




            ppLoading.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of HomeFragment");
        super.onPause();
    }
}
