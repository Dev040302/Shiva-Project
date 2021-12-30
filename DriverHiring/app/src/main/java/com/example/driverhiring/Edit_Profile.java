package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Edit_Profile extends AppCompatActivity {
    
    String Type,Uid;
    Intent intent;

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

        Intent i=getIntent();
        Type = i.getStringExtra("Type");
        Uid = i.getStringExtra("uid");

        EditText Pname=(EditText)findViewById(R.id.name);
        EditText Pphone=(EditText)findViewById(R.id.number);
        EditText Plicence=(EditText)findViewById(R.id.licence);
        EditText Paddress=(EditText)findViewById(R.id.address);
        Button btn = findViewById(R.id.updatebtn);

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = Pname.getText().toString().trim();
                String phone =Pphone.getText().toString().trim();
                String address=Paddress.getText().toString().trim();
                String licence=Plicence.getText().toString().trim();

                if(name.isEmpty()){
                    Pname.setError("name is required");
                    Pname.requestFocus();
                    return;
                }

                if(phone.isEmpty()){
                    Pphone.setError("Enter the phone number");
                    Pphone.requestFocus();
                    return;
                }
                if(address.isEmpty()){
                    Paddress.setError("Enter the address");
                    Paddress.requestFocus();
                    return;
                }
                if(Type=="Drivers") {
                    if (licence.isEmpty()) {
                        Plicence.setError("Enter the licence number");
                        Plicence.requestFocus();
                        return;
                    }
                    if (licence.length() != 15) {
                        Plicence.setError("Enter correct licence number");
                        Plicence.requestFocus();
                        return;
                    }
                }

                DatabaseReference Ref = FirebaseDatabase.getInstance().getReference("Users").child(Type).child(Uid);
                Ref.child("name").setValue(name);
                Ref.child("phone").setValue(phone);
                Ref.child("address").setValue(address);

                if (Type=="Drivers"){
                    Ref.child("licence").setValue(licence);
                }


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
                        intent = new Intent(Edit_Profile.this,View_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Edit_Profile.this, Edit_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Edit_Profile.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email.getText())
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
                        intent = new Intent(Edit_Profile.this, History.class);
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