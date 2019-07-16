package com.jagtar.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class StartActivity extends AppCompatActivity {
    // MARK: Debug info
    private final String TAG = "JSK";
    // MARK: Particle Publish / Subscribe variables
    private long subscriptionId;

    String done = "1";
    TextView result;
    String storeFirstSequence = "";
    String storeSecondSequence = "";
    String reverseCode = "96311";
    String easyButton;
    String hardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);

        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFS, MODE_PRIVATE);
        easyButton = prefs.getString("easy", "");
        hardButton = prefs.getString("hard", "");
    }

    //calling this function to get data from Particle
    public void getFromDevice(String eventDD) {

        if (MainActivity.mDevice == null) {
            Log.d(TAG, "Cannot find device");
            return;
        }

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents(
                        eventDD,  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                Log.i(TAG, "Received event with payload: " + event.dataPayload);
                                //   ss = (event.dataPayload).toString();

                                runOnUiThread(new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (easyButton.equals("true") && hardButton.equals("false")) {
                                            if (eventDD.equals("firstseq")) {
                                                storeFirstSequence = "";
                                                storeFirstSequence = event.dataPayload;
                                            }
                                            if (eventDD.equals("secondseq")) {
                                                storeSecondSequence = "";
                                                storeSecondSequence = event.dataPayload;
                                            }
                                            if (!storeFirstSequence.equals("") && !storeSecondSequence.equals("")) {
                                                if (storeFirstSequence.equals(storeSecondSequence))
                                                    result.setText("You Win");
                                                else {
                                                    result.setText("You Lose");
                                                }
                                            }
                                        }
                                        if (easyButton.equals("false") && hardButton.equals("true")) {
                                            {
                                                if (eventDD.equals("secondseq")) {
                                                    storeSecondSequence = "";
                                                    storeSecondSequence = event.dataPayload;
                                                }
                                                if (!storeSecondSequence.equals("") && !reverseCode.equals("")) {
                                                    if (storeSecondSequence.equals(reverseCode))
                                                        result.setText("You Win");
                                                    else {
                                                        result.setText("You Lose");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }));
                            }

                            public void onEventError(Exception e) {
                                Log.e(TAG, "Event error: ", e);
                            }
                        });
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });

    }

    //calling this function to sent data to Particle
    public void sendToDevice(String commandToSend, String funName) {
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                // 2. build a list and put the r,g,b into the list
                List<String> functionParameters = new ArrayList<String>();
                functionParameters.add(commandToSend);

                // 3. send the command to the particle
                try {
                    MainActivity.mDevice.callFunction(funName, functionParameters);

                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    e.printStackTrace();
                }

                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Sent colors command to device.");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    //MY Game Logic
    public void gameEnd(View view) {
        sendToDevice(done, "endGame");
           getFromDevice("secondseq");
            getFromDevice("firstseq");
    }

    public void gameRestart(View view) {
        if (easyButton.equals("true") && hardButton.equals("false"))
        {
            sendToDevice("1", "restart");
        }
        else if (easyButton.equals("false") && hardButton.equals("true"))
        {
            sendToDevice("2", "restart");
        }
        storeSecondSequence = "";
        storeFirstSequence = "";
        result.setText("");
    }
}
