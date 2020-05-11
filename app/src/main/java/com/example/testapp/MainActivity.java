package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new TestsAdapter();

        //pass reference to activity
        layoutManager = new LinearLayoutManager(this);

        //set up the RecyclerView
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        //now we've instantiated the recyclerview, set the adapter for it to be adapter we wrote, and so now it knows what data to display

        /* Testing Java Functionality
        //List is an interface, ArrayList uses that interface
        List<TestClass> list = new ArrayList<>();
        list.add(new TestClass("Noah", "student"));
        list.add(new TestClass("Jacob","policeman"));
        list.add(new TestClass("Gideon","fireman"));

        //asList is a static method
        List<String> students = Arrays.asList("Bill", "Bob", "Susan");

        //make Map to map students to a certain TestClass instance
        Map<String, TestClass> assignments = new HashMap<>();

        //generate random number
        Random random = new Random();

        //iterate through students
        for (String student : students) {
            //generate random number, arg is max random number you can generate
            int index = random.nextInt(list.size());

            //assign student
            assignments.put(student, list.get(index));
        }

        //print out the assignments

        //Entry is class defined within map class that reps single pair of keys/values
        for (Map.Entry<String, TestClass> entry: assignments.entrySet()) {
            //print the entry to the log
            TestClass value = entry.getValue();
            Log.d("MYPRINTS",entry.getKey()+ " got " + value.getName() + " with " + value.getValue());
        }

        }
         */
    }
}
