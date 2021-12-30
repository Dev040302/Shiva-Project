package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Edit_Profile extends AppCompatActivity {

    MenuItem history;
    String Type,Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerview = navigationView.getHeaderView(0);
        TextView email = headerview.findViewById(R.id.nav_mail);
        TextView name = headerview.findViewById(R.id.nav_name);

        FirebaseAuth auth = FirebaseAuth.getInstance();


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
                        Intent intent = new Intent(Edit_Profile.this,View_Profile.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Edit_Profile.this, Edit_Profile.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:
                        FirebaseAuth.getInstance().signOut();break;
                    case R.id.nav_change_password:

                        auth.sendPasswordResetEmail((String) email.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Edit_Profile.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        Toast.makeText(Edit_Profile.this, "History",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;

                }


                return true;
            }
        });


    }
}