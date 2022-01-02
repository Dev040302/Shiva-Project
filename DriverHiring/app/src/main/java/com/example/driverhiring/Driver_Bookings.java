package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.common.io.LineReader;
import com.google.firebase.auth.FirebaseAuth;
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
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bookings);

        getSupportActionBar().hide();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_menu_driver);
        View headerview = navigationView.getHeaderView(0);
        TextView email = headerview.findViewById(R.id.nav_mail);
        TextView name = headerview.findViewById(R.id.nav_name);

        FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nametxt= snapshot.child("name").getValue(String.class);
                String emailtxt= snapshot.child("email").getValue(String.class);

                name.setText(nametxt);
                email.setText(emailtxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {

                    case R.id.nav_profile:
                        intent = new Intent(Driver_Bookings.this,View_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Driver_Bookings.this, Edit_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Driver_Bookings.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Driver_Bookings.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        intent = new Intent(Driver_Bookings.this, History.class);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    default:
                        return true;

                }


                return true;
            }
        });

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