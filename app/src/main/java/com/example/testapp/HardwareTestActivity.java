package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HardwareTestActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;

    //to get the values, use intent to pass data from one activity to another

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //take some text that's passed into it and display it on the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_test);

        //get extra data that was passed along as intent
        String name = getIntent().getStringExtra("name");
        int number = getIntent().getIntExtra("number", 0);

        //set contents of the textview
        nameTextView = findViewById(R.id.test_name);
        numberTextView = findViewById(R.id.test_number);

        nameTextView.setText("Click here for "+name+" test.");
        numberTextView.setText(String.format("#%03d", number));


        //listen for if button clicked
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getIntent().getStringExtra("name");
                //when the test name is clicked, run the camera activity
                Intent intent = new Intent(v.getContext(), CameraActivity.class);

                //store the component name in intent
                intent.putExtra("component", nameTextView.getText());

                //run camera
                if (name.equals("Camera")) {
                    startActivity(intent);
                }
                //Toast.makeText(getApplicationContext(), "Testing 123", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
