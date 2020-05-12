package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

public class RunTestActivity extends AppCompatActivity {
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
                }
            }
        });
    }

    //run the test on all connected displays
    public void displayTest() {
        if (printDisplayIDs()<0) {
            Toast.makeText(RunTestActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RunTestActivity.this, "PASS", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RunTestActivity.this, "Trying Display #" + String.format("%d", displayId), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(RunTestActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RunTestActivity.this, "PASS", Toast.LENGTH_SHORT).show();
        }
    }

    //get list of camera IDs, print out each one
    public int printCameraIDs() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

            assert cameraManager != null;
            for (String cameraId : cameraManager.getCameraIdList()) {
                Toast.makeText(RunTestActivity.this, "Trying Camera #"+cameraId, Toast.LENGTH_SHORT).show();
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
