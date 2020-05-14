package com.example.testapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.display.DisplayManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class RunTestActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private int failure;
    private Button endButton;
    private String component;

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        component=getIntent().getStringExtra("component");

        Button btnTestName = (Button) findViewById(R.id.button_camera);
        btnTestName.setText("Test " + component);

        imageView = (ImageView) findViewById(R.id.image_view);
        endButton = (Button) findViewById(R.id.end_button);


        btnTestName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check component type, run test
                assert component != null;
                ExampleAsyncTask testsTask = new ExampleAsyncTask(RunTestActivity.this);
                testsTask.execute(component);
            }
        });
    }

    public void getI2cDevices() {
    }

    private class ExampleAsyncTask extends AsyncTask<String, Void, Integer> {
        Context context;
        public ExampleAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            RunTestActivity runTestActivity = (RunTestActivity) this.context;
            Log.d("HELLO", "I am back thread");
            switch (strings[0]) {
                case "Camera": {runTestActivity.cameraTest(); break;}
                case "Display": {runTestActivity.displayTest(); break;}
                case "Audio Outputs": {Log.d("HELLO", "Audio"); runTestActivity.audioOutTest(); break;}
                case "Vibrator": {runTestActivity.vibrateTest(); break;}
                case "Bluetooth": {runTestActivity.bluetoothTest(); break;}
                case "Battery Info": {runTestActivity.getBatteryInfo(); break;}
                case "Ambient light/prox sensor": {runTestActivity.proxSensorTest(); break;}
                case "I2C Device List": {runTestActivity.getI2cDevices(); break;}
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            endButton.setVisibility(View.VISIBLE);
            ((Button) (endButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RunTestActivity.this.finish();
                }
            });
        }
    }


    public void proxSensorTest() {
        if (checkProxSensor()<0) {
            Toaster.customToast("FAIL", RunTestActivity.this);
        }
        else {
            Toaster.customToast("PASS", RunTestActivity.this);
        }
    }

    public int checkProxSensor() {
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);

        //nothing found, something's wrong, error
        if (sensors.isEmpty()) {
            return -1;
        }

        Toaster.customToast(String.format("%d Prox Sensors Found", sensors.size()), RunTestActivity.this);

        int index=0;
        for (Sensor thisSensor : sensors) {
            Toaster.customToast(String.format("Sensor #%d: ", index++) + thisSensor.getName(), RunTestActivity.this);
        }
        return 0;
    }

    public void getBatteryInfo() {
    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        float scale = -1;
        float level = -1;
        float voltage = -1;
        float temp = -1;
        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            temp = (float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
            voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) /1000;
            Toaster.customToast("Charge level is "+level+"/"+scale+", temp is "+temp+"C, voltage is "+voltage+"V", RunTestActivity.this);
        }
    };
    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    registerReceiver(batteryReceiver, filter);
    }

    public void bluetoothTest() {
        if (checkBluetooth()<0) {
            Toaster.customToast("FAIL", RunTestActivity.this);
        }
    }

    public int checkBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //device doesn't support bluetooth
            return -1;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RunTestActivity.this, "Device supports Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });


        //Run 2 different tests: first, try to get the human-readable name of the Bluetooth adapter
        final String name = bluetoothAdapter.getName();

        if (name==null) {
            return -1;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RunTestActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });

        //the second test we wanna run is we'll try to enable bluetooth, and check the result code

        //make sure Bluetooth starts off
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //enabling bluetooth failed
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toaster.customToast("FAIL", RunTestActivity.this);
                return;
            }
            Toaster.customToast("PASS", RunTestActivity.this);
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }

    public void vibrateTest() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        assert vibrator != null;
        if (!vibrator.hasVibrator()) {
            Toaster.customToast("No vibrator found", RunTestActivity.this);
            return;
        }

        //pulse a vibration
        Toaster.customToast("Found vibrator, vibrating...", RunTestActivity.this);
        vibrator.vibrate(500);
        Toaster.customToast("PASS", RunTestActivity.this);
    }

    //get audio outputs and display them
    public void audioOutTest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toaster.customToast("Device API too old to get audio devices", RunTestActivity.this);
            return;
        }
        checkAudioOutputs();
    }

    //get list of audio devices, print them out, play beep
    public int checkAudioOutputs() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //get array of audio device informations
        assert audioManager != null;
        final AudioDeviceInfo[] deviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        if (deviceInfos.length == 0) {
            //no output devices found, so FAIL
            Toaster.customToast("FAIL: NO OUTPUT DEVICE FOUND", RunTestActivity.this);
        }

        Log.d("HELLO", "Toasting");
        Toaster.customToast(String.format("%d audio devices found: ", deviceInfos.length), RunTestActivity.this);
        int index=0;

        //create list of audio output devices connected
        List<String> devices = new ArrayList<>();


        //otherwise we'll go through array and print out human-readable IDs of the devices
        for (AudioDeviceInfo thisInfo : deviceInfos) {
            String deviceName = thisInfo.getProductName().toString();
            Toaster.customToast(String.format("Device #%d: ", index++)+deviceName+String.format(" of type %d", thisInfo.getId()), RunTestActivity.this);
            devices.add(deviceName);
        }

        //create string of all audio devices
        final String joinedList = String.join(", ", devices);

        //post an alert dialog pop-up portal to the playSounds() sequence to the main thread MessageQueue,
        //a few seconds out (based on how many devices and the toast duration)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(RunTestActivity.this)
                        .setTitle("Found audio output devices")
                        .setMessage("Found these devices: "+joinedList)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Test all", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //run through the sound tests
                                playSounds();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Dismiss", null)
                        .show();
            }
        }, 2000*(deviceInfos.length +1));

        return 0;
    }

    class ToneRunnable implements Runnable {
        int which;
        String text;

        ToneRunnable(int which, String text) {
            this.which=which;
            this.text=text;
        }

        @Override
        public void run() {
           Toaster.customToast(text, RunTestActivity.this);
            final AudioTrack soundAtSpecificFrequency = generateTone(500, 6000, which);
            soundAtSpecificFrequency.play();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundAtSpecificFrequency.pause();
                }
            }, 2000);
        }
    }

    public void playSounds() {
        //run audible test of L and R main stereo, then phone speaker. Finally we passed the test

        //post up the three tests to the MessageQueue to be run by looper
        new Handler(Looper.getMainLooper()).post(new ToneRunnable(0, "Main stereo left"));
        new Handler(Looper.getMainLooper()).postDelayed(new ToneRunnable(1, "Main stereo right"), 2000);
        new Handler(Looper.getMainLooper()).postDelayed(new ToneRunnable(2, "Phone speaker"), 4000);

        //post up a PASS toast
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toaster.customToast("PASS", RunTestActivity.this);
            }
        }, 6000);
    }

    private AudioTrack generateTone(double freqHz, int durationMs, int which)
    {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for (int i = 0; i < count; i += 2) {

            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);

            if (which == 2) {
                samples[i] = sample;
                samples[i+1] = sample;
            } else if (which == 0) { //only play on left
                samples[i] = sample;
                samples[i + 1] = 0;
            } else { //only play on right
                samples[i] = 0;
                samples[i + 1] = sample;
            }
        }
        AudioTrack track;
        if (which==2) {
            track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
            AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
            assert audioManager != null;
            audioManager.setSpeakerphoneOn(false);
        }

        else {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                    count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        }

        track.write(samples, 0, count);
        return track;
    }

    //run the test on all connected displays
    public void displayTest() {
        if (printDisplayIDs()<0) {
            Toaster.customToast("FAIL", RunTestActivity.this);
        }
        else {
            Toaster.customToast("PASS", RunTestActivity.this);
        }
    }

    public int printDisplayIDs() {
            //get a display manager object
            DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);

            assert displayManager != null;

            Display[] displays = displayManager.getDisplays();

            //if there are no connected displays, problem
            if (displays.length == 0) {
                return -1;
            }

            //else go through displays and try to get display information
            for (Display thisDisplay : displays) {
                int displayId = thisDisplay.getDisplayId();
                Toaster.customToast("Trying Display #" + String.format("%d", displayId), RunTestActivity.this);

                //run a getDisplay check on the displayId, which will return Display object on success, or "null if there is no valid display with the given id"
                //https://developer.android.com/reference/android/hardware/display/DisplayManager#getDisplay(int)
                Display result = displayManager.getDisplay(displayId);
                if (result == null) {
                    return -1;
                }
            }
        return 0;
    }

    //run the test on all cameras
    public void cameraTest() {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); -- this is a way to take a picture, which would probably work for the test, but I went with something more base
        //startActivityForResult(intent, 0);
        if (printCameraIDs()<0) {
            Toaster.customToast("FAIL", RunTestActivity.this);
        }
        else {
            Toaster.customToast("PASS", RunTestActivity.this);
        }
    }

    //get list of camera IDs, print out each one
    public int printCameraIDs() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

            assert cameraManager != null;
            for (String cameraId : cameraManager.getCameraIdList()) {
                Toaster.customToast("Trying Camera #"+cameraId, RunTestActivity.this);
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            }
            return 0;
        }
        catch(CameraAccessException|IllegalArgumentException e) {
            return -1;
        }
    }


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");

        imageView.setImageBitmap(bitmap);
    }

     */
}
