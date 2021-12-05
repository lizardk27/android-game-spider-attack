package com.pontus.openglspider;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
/*
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
*/
import java.util.concurrent.Executor;


public class MainActivity extends Activity {

    //A ProgressDialog object
    private ProgressDialog progressDialog;
    // Our OpenGL Surfaceview
    private GLSurfaceView glSurfaceView;
    // decor view
    private View mDecorView;
    private Intent music;
/*
    private AdView adView;
    InterstitialAd mInterstitialAd;

    private boolean isAdShowing = false;
    private boolean isAdLoaded = false;
    private boolean isAdFailed = false;

    private boolean isBannerShowing = false;
    private boolean isBannerLoaded = false;
    private boolean isBannerFailed = false;

*/
    private boolean isFirstPause = true;
    private boolean isConnected = false;

    LoadViewTask myLoadViewTask;


    private boolean isMusicPlaying = false;
    private boolean mIsBound = false;
    private MusicService mServ;

    private ServiceConnection Scon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super
        super.onCreate(savedInstanceState);

        doBindService();
        // set initial screen orientation
        initActivityScreenOrientPortrait();

        // hide UI features
        hideSystemUI();

        // We create our Surfaceview for our OpenGL here.
        glSurfaceView = new GLSurf(this);

        // Set our view.
        setContentView(R.layout.activity_main);

        // Retrieve our Relative layout from our main layout we just set to our view.
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);

        // Attach our surfaceview to our relative layout from our main layout.
        RelativeLayout.LayoutParams glParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        // Create and load the AdView.

        /*
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-2154554492998378/2129526442");
        adView.setAdSize(AdSize.SMART_BANNER);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2154554492998378/5082992843");

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                isBannerShowing = false;

                System.out.println("Exception: banner closed");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                isBannerFailed = true;
                System.out.println("Exception: banner failed to load");
            }

            @Override
            public void onAdLoaded() {
                isBannerLoaded = true;

                System.out.println("Exception: banner is loaded");
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                isAdShowing = false;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                isAdFailed = true;
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
                isAdLoaded = true;

            }
        });


        // Add adView to the bottom of the screen.
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        layout.addView(glSurfaceView, glParams);
        layout.addView(adView, adParams);


        //Initialize a LoadViewTask object and call the execute() method
        launchLoadViewTask();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        */

    }

/*
    public void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void hideBanner() {
        adView.setVisibility(View.GONE);
    }

    public void showBanner() {

        if (isBannerFailed) {
            return;
        }

        System.out.println("Exception: IN SHOW banner ");
        adView.setVisibility(View.VISIBLE);
        adView.loadAd(new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        adView.setVisibility(View.VISIBLE);

    }
*/

    public void doBindService() {

        try {
            music = new Intent();
            music.setClass(this, MusicService.class);
            bindService(music, Scon, Context.BIND_AUTO_CREATE);
            mIsBound = true;
        } catch (Exception exp) {
            System.out.println("Exception. Could not bind music service");

        }
    }


    public void doUnbindService() {
        try {
            if (mIsBound) {
                unbindService(Scon);
                mIsBound = false;
            }
        } catch (Exception exp) {

            System.out.println("Exception. Could not UN-Bind music service");
            // exp.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stopService(music);
            mServ.stopMusic();
            doUnbindService();
        } catch (Exception ex) {

            System.out.println("Exception. Could not Stop and or unbind music service: " + ex.getMessage());
            //ex.printStackTrace();
        }

    }



    @Override
    protected void onPause() {
            super.onPause();

        glSurfaceView.onPause();
        showSystemUI();


            if (isConnected) {

            try {
           /*     if (!isAdFailed && (mInterstitialAd.isLoaded() & !isAdShowing)) {
                    System.out.println("Exception. Counter mInterstitialAd is Loaded -  SHOWING AD!");
                    mInterstitialAd.show();
                    isAdShowing = true;
                }
                if (!isBannerFailed) {
                    showBanner();
                }*/
                if (!isMusicPlaying) {
                    playMusic();
                }
            } catch (Exception exp) {

                System.out.println("Exception.  on Pause: " + exp.getMessage());

            }


        }// END IF IS CONNECTED
    }




        /*
         CODE TO WRITE TO CACHE READY TO IMPLEMENT WHEN NEEDED
         JUST REMOVE COMMENTS
            - - - - - - - - - - FROM HERE - - - - - - - - - -

        String textToCache = "-" + String.valueOf(((GLSurf) glSurfaceView).getSpiderAttackRenderer().getLevel()) + "-" +
                "-" + String.valueOf(((GLSurf) glSurfaceView).getSpiderAttackRenderer().getScore()) + "-" +
                "-" + String.valueOf(((GLSurf) glSurfaceView).getSpiderAttackRenderer().getBugsReachedSpider()) + "-" +
                "-" + String.valueOf(((GLSurf) glSurfaceView).getSpiderAttackRenderer().getLivesLeft()) + "-";

        boolean success = CacheFileHandler.writeAllCachedText(this, "spiderCacheFile.txt", textToCache);

        if (success) {
            //System.out.println("CACHE WRITE OK");

        } else {
            //System.out.println("COULD NOT WRITE TO CACHE");

        }
            - - - - - - - - - - TO HERE - - - - - - - - - -
        */


    public void playMusic() {

        try {
            if (!isMusicPlaying && mIsBound) {

                startService(music);
            }
        } catch (Exception exp) {

            System.out.println("Exception.  Starting Music: " + exp.getMessage());
        }
        try {
            if (mServ != null) {
                mServ.playMusic();
                mServ.setVolumeLevel(0.5f);
                isMusicPlaying = true;
            }
        } catch (Exception exp) {

            System.out.println("Exception.  Starting Music: " + exp.getMessage());
        }

        try {
            if (!mIsBound) {
                doBindService();
            }


        } catch (Exception exp) {
            System.out.println("Exception.  on Play Music: " + exp.getMessage());
        }

    }


    public void setMusicVolume(float volume) {
        try {
            if (volume < 0 || volume > 1) {
                mServ.setVolumeLevel(.5f);
                throw new Exception("Music Volume Exception: volume level should be between 0 and 1");
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            //  e.printStackTrace();

            System.out.println("Exception. music volume exception:" + e.getMessage());
            return;
        }
        mServ.setVolumeLevel(volume);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //System.out.println("MAIN ACTIVITY ON RESUME");
        hideSystemUI();
        //requestNewInterstitial();
        glSurfaceView.onResume();
        if (!mIsBound && mServ != null) {
            mServ.resumeMusic();
            mIsBound = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {

        initActivityScreenOrientPortrait();
        hideSystemUI();
    }

    private boolean isRendererInitialized() {
        if (glSurfaceView != null && ((GLSurf) glSurfaceView).getSpiderAttackRenderer() != null) {
            return ((GLSurf) glSurfaceView).getSpiderAttackRenderer().isSurfaceCreated();
        } else {
            return false;
        }

    }

    // manage initial screen settings
    private void initActivityScreenOrientPortrait() {

        // 	Avoid screen rotations
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set window fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Test if it is VISUAL in portrait mode by simply checking it's size
        boolean bIsVisualPortrait = (metrics.heightPixels >= metrics.widthPixels);
        if (!bIsVisualPortrait) {
            // Swap the orientation to match the VISUAL portrait mode
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            //IF visually on portrait, lock screen orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
     /*
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Spider vs Bugs", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://pontusdd.com"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("http://pontusdd.com/spidervbugs")
        );
        AppIndex.AppIndexApi.start(client, viewAction);

        if (mInterstitialAd.isLoaded()) {
            System.out.println("Exception. IN START GOOGLE GEN: Counter mInterstitialAd is Loaded -  SHOWING AD!");
            mInterstitialAd.show();
            isAdShowing = true;
        }

      */
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://pontusdd.com"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("http://pontusdd.com/spidervbugs")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        */

    }
    public void checkInternetConnection(){

        if(isConnected){
            return;
        }

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            isConnected = true;
            isFirstPause= false;
            ((GLSurf)glSurfaceView).getSpiderAttackRenderer().showPlayButton(true);
            ((GLSurf)glSurfaceView).getSpiderAttackRenderer().showConnectioMessagge();
            System.out.println("Connection OK... continue");
            //requestNewInterstitial();
            launchLoadViewTask();
        } else {
            System.out.println("Connection NOT CONNECTED show message ... . .");
            ((GLSurf)glSurfaceView).getSpiderAttackRenderer().showPlayButton(false);
            ((GLSurf)glSurfaceView).getSpiderAttackRenderer().showConnectioMessagge();
        }

    }

    public void launchLoadViewTask(){
        myLoadViewTask = null;
        myLoadViewTask = new LoadViewTask();
        myLoadViewTask.execute();
    }

    public void startAgain(){
        this.onCreate(null);
    }

    // CLASS CONSTRUCTOR
    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Spider Attack™ by Pontus",
                    "Please wait. Loading...  \n" +
                            " All content (including but not limited to: music, sounds and images)" +
                            " is copyrighted by Pontus", false, false);
        }

        //The code to be executed in a old_background thread.
        @Override
        protected Void doInBackground(Void... params) {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a old_background
             * thread must be placed.
             */
            try {
                //Get the current thread's token
                synchronized (this) {

                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;

//
//                    while (!isRendererInitialized() & !isAdLoaded) {
                    while (counter < 18) {
                        // Wait 500 milliseconds
                        this.wait(500);
                        counter++;
                        publishProgress(counter);
                    }
                }
            } catch (Exception e) {

                System.out.println("Exception.  in LoadViewTask doInBackground method: " + e.getMessage());
                //e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
            System.out.println("Exception. Counter value:" + values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            //close the progress dialog
            progressDialog.dismiss();

           checkInternetConnection();

/*
            if (mInterstitialAd.isLoaded() && isConnected) {
                System.out.println("Exception. Counter mInterstitialAd is Loaded -  SHOWING AD!");
                mInterstitialAd.show();
                isAdShowing = true;
            }

 */
            try {
                playMusic();
            } catch (Exception ex) {

                System.out.println("Exception. in  mInterstitialAd is playing music" + ex.getMessage());
            }
        }
    }
}