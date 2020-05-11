package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        nameTextView.setText(name);
        numberTextView.setText(String.format("#%03d", number));
    }


}
