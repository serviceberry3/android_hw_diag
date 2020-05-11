package com.example.testapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;


//RecyclerView class is a generic class
public class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.TestsViewHolder> {
    public static class TestsViewHolder extends RecyclerView.ViewHolder { //needs to be public because using it in declaration above
        public LinearLayout containerView;
        public TextView textView;
        TestsViewHolder(View view) {
            //use original
            super(view);
            //create fields for the two things we added IDs to: layout and textview

            //take view that's passed in from recyclerview, convert it into something we can use

            //under the hood, Gradle automatically generates unique IDs for all the string IDs we gave stuff, puts in R
            containerView=view.findViewById(R.id.pokdex_row); //integer that represents the container
            textView = view.findViewById(R.id.pokedex_row_text_view);

            //add event handler that can be executed when row is tapped
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //cast the returned object to a hardware test
                    HardwareComponentTest current = (HardwareComponentTest)containerView.getTag();

                    //create Intent, specifying what class we want to instantiate
                    Intent intent = new Intent(v.getContext(), HardwareTestActivity.class);
                    intent.putExtra("name", current.getName());
                    intent.putExtra("number", current.getNumber());

                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    //need some data to start
    private List<HardwareComponentTest> tests= Arrays.asList(
            new HardwareComponentTest("Camera", 1),
            new HardwareComponentTest("Sensor", 2),
            new HardwareComponentTest("NFC", 3)
    );

    //override some methods from recyclerview adapter

    @NonNull
    @Override
    public TestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get our layout file, go from layout to a view. Inflate: go from XML file to a Java View. R.layout is auto generated for us
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row, parent, false);
        //now we've converted XML file into Java View object in memory

        //return a new view holder containing this view
        return new TestsViewHolder(view);
    }

    //second method: onbind--called whenever a view scrolls into screen and we say we need to set the values inside of this row
    //set the different properties of the view we created

    //go from this model to our view (a controller goes from a model to a view)
    @Override
    public void onBindViewHolder(@NonNull TestsViewHolder holder, int position) {
        //grab element out of array to display data
        HardwareComponentTest current = tests.get(position);

        //take name of the test, set that to be text of row
        holder.textView.setText(current.getName());

        //pass along the test to the viewholder
        holder.containerView.setTag(current); //now viewholder has access to current test
    }

    //method for how many rows to display

    @Override
    public int getItemCount() {
        return tests.size();
    }
}
