package com.example.testapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.camera2.CameraManager;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button btnCamera = (Button) findViewById(R.id.button_camera);


        imageView = (ImageView) findViewById(R.id.image_view);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Toast.makeText(CameraActivity.this, "Testing 123", Toast.LENGTH_SHORT).show();
                //startActivityForResult(intent, 0);
                if (printCameraIDs()<0) {
                    Toast.makeText(CameraActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CameraActivity.this, "PASS", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //get list of camera IDs, print out each one
    public int printCameraIDs() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

            assert cameraManager != null;
            for (String cameraId : cameraManager.getCameraIdList()) {
                Toast.makeText(CameraActivity.this, "Trying Camera #"+cameraId, Toast.LENGTH_SHORT).show();
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
