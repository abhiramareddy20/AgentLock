package com.example.agentlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Jobs extends AppCompatActivity {

    List<Jobss> productList;

    //the recyclerview
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_jobs);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);




//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager (this));

        //initializing the productlist
        productList = new ArrayList<> ();


        //adding some items to our list
        productList.add(
                new Jobss (
                        1,
                        "Abhi",
                        1,
                        20000,
                        R.drawable.upload222));

        productList.add(
                new Jobss (
                        1,
                        "Nikhil",
                        2,
                        50000,
                        R.drawable.agentprofile));

        productList.add(
                new Jobss (
                        1,
                        "Rahul",
                        1,
                        10000,
                        R.drawable.helmet));

        //creating recyclerview adapter
        Jobs_Adapter adapter = new Jobs_Adapter (this, productList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
        Log.e ("Dta  ","1");



    }
}
