package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.common.io.LineReader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Driver_Bookings extends AppCompatActivity {

    private RecyclerView Rc;
    orderAdapter adapter;
    DatabaseReference mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bookings);

        mbase = FirebaseDatabase.getInstance().getReference("CustomerRequirement");

        Rc=findViewById(R.id.recyclerview);

        Rc.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<orders> option = new FirebaseRecyclerOptions.Builder<orders>().setQuery(mbase,orders.class).build();

        adapter = new orderAdapter(option);

        Rc.setAdapter(adapter);
    }

    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }


    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }


}