package com.BestofallPhotography.PhotoFrameFlowerPF;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.BestofallPhotography.PhotoFrameFlowerPF.adapter.FrameAdapter;
import com.BestofallPhotography.PhotoFrameFlowerPF.touchview.MultiTouchListener;
import com.BestofallPhotography.PhotoFrameFlowerPF.touchview.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class FrameActivity extends AppCompatActivity implements FrameAdapter.FrameOnClickListener {

    private RelativeLayout frameLayout;
    private RecyclerView recyclerView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        // Create the InterstitialAd and set the adUnitId.
        MobileAds.initialize(this,Data.adMobAppId);// update the id with your adMob app id
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Data.interstitialAd);// update the id with your InterstitialAd ads id
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(Data.testDevice)// update the id with your device id
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                createImage();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);


        TouchImageView imageView = findViewById(R.id.mainImgview);
        imageView.setImageURI(getIntent().getData());
        imageView.setOnTouchListener(new MultiTouchListener());
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setEnabled(false);
        frameLayout.setBackgroundResource(R.drawable.frame_a);

        FrameAdapter adapter = new FrameAdapter(Data.frameId, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_frame:
                    changeFrameStatus();
                    return true;
                case R.id.navigation_save:
                    showInterstitial();
                    return true;
                case R.id.navigation_more:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id="+Data.developerID)));
                    return true;

            }
            return false;
        }
    };

    @Override
    public void onFrameSelected(int id) {
        frameLayout.setBackgroundResource(Data.frameId[id]);
    }


    public void changeFrameStatus() {
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            createImage();
        }
    }

    private void createImage() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .addTestDevice(Data.testDevice)// update the id with your device id
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build());

        }
        OutputStream output;

        Calendar cal = Calendar.getInstance();

        RelativeLayout captureLayout = findViewById(R.id.mainLayout);

        Bitmap bitmap = Bitmap.createBitmap(captureLayout.getWidth(), captureLayout.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        captureLayout.draw(canvas);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/"+ Data.folderName +"/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String mImageName = "image" + cal.getTimeInMillis() + ".jpg";
        // Create a name for the saved image
        File file = new File(dir, mImageName);

        MediaScannerConnection.scanFile(this, new String[]{file.getPath()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // now visible in gallery
                    }
                });


        try {

            output = new FileOutputStream(file);
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String savePath = dir.getAbsolutePath() + "/" + mImageName;
        Intent intent = new Intent(FrameActivity.this, SaveActivity.class);
        intent.putExtra("path", savePath);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

}
