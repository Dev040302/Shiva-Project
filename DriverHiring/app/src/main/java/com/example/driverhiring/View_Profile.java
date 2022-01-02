package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

public class View_Profile extends AppCompatActivity {
    
    String Type,Uid;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerview = navigationView.getHeaderView(0);
        TextView email = headerview.findViewById(R.id.nav_mail);
        TextView name = headerview.findViewById(R.id.nav_name);


        FirebaseAuth auth = FirebaseAuth.getInstance();

        TextView mail=findViewById(R.id.mail);
        TextView name1=findViewById(R.id.name);
        TextView phone=findViewById(R.id.number);
        TextView address=findViewById(R.id.address);
        TextView password=findViewById(R.id.password);
        TextView licence=findViewById(R.id.licence);
        
        Intent i=getIntent();
        Type = i.getStringExtra("Type");
        Uid = i.getStringExtra("uid");
        
        if(Type == "Drivers"){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navigation_menu_driver);
        }
        else{
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navigation_menu_customer);
        }


        FirebaseDatabase.getInstance().getReference("Users").child(Type).child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nametxt= snapshot.child("name").getValue(String.class);
                String emailtxt= snapshot.child("email").getValue(String.class);


                name.setText(nametxt);
                email.setText(emailtxt);
                mail.setText("Mail : "+emailtxt);
                name1.setText("Name : "+nametxt);

                phone.setText("Phone : "+snapshot.child("phone").getValue(String.class));
                address.setText("Address : "+snapshot.child("address").getValue(String.class));
                password.setText("Password : "+snapshot.child("password").getValue(String.class));

                if(Type != "Drivers"){
                    licence.setText("Licence : "+snapshot.child("licence").getValue(String.class));
                }

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
                        intent = new Intent(View_Profile.this,View_Profile.class);
                        intent.putExtra("Type",Type);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(View_Profile.this, Edit_Profile.class);
                        intent.putExtra("Type",Type);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(View_Profile.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(View_Profile.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        intent = new Intent(View_Profile.this, History.class);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    default:
                        return true;

                }


                return true;
            }
        });
    }


}