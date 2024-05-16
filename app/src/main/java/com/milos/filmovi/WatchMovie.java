package com.milos.filmovi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class WatchMovie extends AppCompatActivity {
    private boolean checkIsTelevision() {
        int uiMode = this.getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION;
    }
    private WebView mWebView;
    private String link;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_watch_movie);
        mWebView = findViewById(R.id.webview);


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.getSettings().setSupportZoom(false);

        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.setBackgroundColor(0x01000000);

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);
        link = getIntent().getStringExtra("url");

      /*  File file = null;
        try {
            file = File.createTempFile("test.html", null, this.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }*/



        if(savedInstanceState==null) {
            final ProgressDialog pd = ProgressDialog.show(this, "", "Ucitavanje...",true);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null && url.startsWith(url)) {

                        return true;
                    } else {
                        return false;
                    }
                }
                @Override
                public void onPageFinished(WebView view, String url)
                {
                    if(pd!=null && pd.isShowing())
                    {
                        try{
                            pd.dismiss();
                        } catch (IllegalArgumentException e){
                            // do nothing
                        }
                    }

                }
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(WatchMovie.this, "Error:" + description, Toast.LENGTH_SHORT).show();

                }

            });


            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int height = 0;
                    int width = 0;
                    if (mWebView.getWidth() > 1920) {
                        height = (int) (mWebView.getHeight() / 2.25f);
                        width = (int) (mWebView.getWidth() / 2.25f);
                    } else if (mWebView.getWidth() > 640) {
                        height = (int) (mWebView.getHeight());
                        width = (int) (mWebView.getWidth());
                    } else {
                        height = (int) (mWebView.getHeight() * 1.25f);
                        width = (int) (mWebView.getWidth() * 1.25f);
                    }



                    String playVideo1 = "<html><body><iframe src="+link+" scrolling=\"no\" frameborder=\"0\" width="+width+" height="+height+" allowfullscreen=\"true\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\"></iframe></body></html>";


                    if(height>width)
                    {
                        playVideo1 = "<html><body><iframe src="+link+" scrolling=\"no\" frameborder=\"0\" width="+height+" height="+width+" allowfullscreen=\"true\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\"></iframe></body></html>";

                    }

                  //  playVideo1 = "<div id=\"trailer\"><iframe src=\""+ link +" scrolling=\"no\" frameborder=\"0\" width=\"728\" height=\"410\" allowfullscreen=\"true\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\"></iframe></div>";
                    mWebView.loadDataWithBaseURL(null, playVideo1,"text/html; charset=UTF-8", null, null);

                }
            }, 250);

        }

    }

        @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState); // output would be a WebBackForwardList
        super.onSaveInstanceState(outState);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
