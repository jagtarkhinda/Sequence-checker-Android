package com.jagtar.memorygame;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;


public class MainActivity extends Activity {


    // MARK: Particle Account Info
    private final String PARTICLE_USERNAME = "jsk5755@gmail.com";
    private final String PARTICLE_PASSWORD = "Alpha123";

    // MARK: Particle device-specific info
    private final String DEVICE_ID = "22001d000447363333343435";

    private long subscriptionId;
    // MARK: Particle device
    public static ParticleDevice mDevice;

    public static final String MY_PREFS = "MY_PREFS" ;
    EditText name;
    TextView show;
    TextView conn;
    TextView diff;
    String namel = "";
    Button easy;
    Button hard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        show = (TextView) findViewById(R.id.showName);
        name = (EditText) findViewById(R.id.name);
        conn = (TextView) findViewById(R.id.conn);
        easy = (Button) findViewById(R.id.easy);
        hard = (Button) findViewById(R.id.hard);
        diff = (TextView) findViewById(R.id.diff);

        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this.getApplicationContext());

        // 2. Setup your device variable
        getDeviceFromCloud();

        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFS, MODE_PRIVATE);
        namel = prefs.getString("username", "");

        easy.setVisibility(View.INVISIBLE);
        hard.setVisibility(View.INVISIBLE);
        diff.setVisibility(View.INVISIBLE);
        if(!TextUtils.isEmpty(namel)) {
            name.setVisibility(View.GONE);
            show.setText("Welcome " + namel);
            name.setText(namel);
        }
    }

    public void easyButton(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString("username", name.getText().toString());
        editor.putString("easy", "true");
        editor.putString("hard", "false");
        editor.apply();

        Intent mainAc = new Intent(this,StartActivity.class);
        startActivity(mainAc);

    }

    public void hardButton(View view) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString("username", name.getText().toString());
        editor.putString("easy", "false");
        editor.putString("hard", "true");
        editor.apply();

        Intent mainAc = new Intent(this,StartActivity.class);
        startActivity(mainAc);

    }

    /**
     * Custom function to connect to the Particle Cloud and get the device
     */
    public void getDeviceFromCloud() {
        // This function runs in the background
        // It tries to connect to the Particle Cloud and get your device
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                mDevice = particleCloud.getDevice(DEVICE_ID);
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                conn.setText("Connected");
                easy.setVisibility(View.VISIBLE);
                hard.setVisibility(View.VISIBLE);
                diff.setVisibility(View.VISIBLE);
                Log.d("connection", "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d("connection", exception.getBestMessage());
            }
        });
    }
}