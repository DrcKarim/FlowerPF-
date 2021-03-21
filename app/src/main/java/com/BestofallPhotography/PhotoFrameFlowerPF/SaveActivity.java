package com.BestofallPhotography.PhotoFrameFlowerPF;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;

public class SaveActivity extends AppCompatActivity {
    String path;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        Toast.makeText(this, "Image saved successfully", Toast.LENGTH_LONG).show();

        path = getIntent().getStringExtra("path");

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.fromFile(new File(path)));

        // AdMob Banner Ads Implementation
        MobileAds.initialize(this,Data.adMobAppId);// update the id with your adMob app id
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(Data.testDevice)
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Data.interstitialAd);// update the id with your InterstitialAd ads id
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(Data.testDevice)// update the id with your device id
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                createNew();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share){
            try {

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                File media = new File(path);
                Uri screenshotUri = Uri.fromFile(media);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Sharing app not found!", Toast.LENGTH_LONG).show();

            }
        }
        if(item.getItemId() == R.id.action_open_new){
            showInterstitial();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            createNew();
        }
    }

    private void createNew(){
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .addTestDevice(Data.testDevice)// update the id with your device id
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build());

        }
        Intent intent = new Intent(SaveActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openNew(View view) {
        showInterstitial();
    }
}
