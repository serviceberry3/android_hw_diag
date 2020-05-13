package com.example.testapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.display.DisplayManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

public class RunTestActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private int failure;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);

        final String component = getIntent().getStringExtra("component");

        Button btnTestName = (Button) findViewById(R.id.button_camera);
        btnTestName.setText("Test " + component);

        imageView = (ImageView) findViewById(R.id.image_view);

        btnTestName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check component type, run test
                assert component != null;
                switch (component) {
                    case "Camera": {cameraTest(); break;}
                    case "Display": {displayTest(); break;}
                    case "Audio Outputs": {audioOutTest(); break;}
                    case "Vibrator": {vibrateTest(); break;}
                    case "Bluetooth": {bluetoothTest(); break;}
                    case "Battery Info": {getBatteryInfo(); break;}
                }
            }
        });
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
        Toast.makeText(RunTestActivity.this, "Device supports Bluetooth", Toast.LENGTH_SHORT).show();

        //Run 2 different tests: first, try to get the human-readable name of the Bluetooth adapter
        String name = bluetoothAdapter.getName();

        if (name==null) {
            return -1;
        }

        Toast.makeText(RunTestActivity.this, name, Toast.LENGTH_SHORT).show();

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
        if (checkAudioOutputs()<0) {
            Toaster.customToast("FAIL", RunTestActivity.this);
        }
        else {
            Toaster.customToast("PASS", RunTestActivity.this);
        }
    }

    //get list of audio devices, print them out, play beep
    public int checkAudioOutputs() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        //get array of audio device informations
        assert audioManager != null;
        AudioDeviceInfo[] deviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        if (deviceInfos.length == 0) {
            //no output devices found, so FAIL
            return -1;
        }

        //otherwise we'll go through array and print out human-readable IDs of the devices
        for (AudioDeviceInfo thisInfo: deviceInfos) {
            Toaster.customToast(thisInfo.getProductName().toString(), RunTestActivity.this);
        }

        Toaster.customToast("Playing three beeps...", RunTestActivity.this);
        //try playing a beep on main speaker
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,500);

        return 0;
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
