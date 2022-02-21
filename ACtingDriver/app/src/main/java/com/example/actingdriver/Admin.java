package com.example.actingdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Admin extends AppCompatActivity {

    EditText emailtxt,passwordtxt,nametxt,notxt,vehnotxt;
    Button login,history;
    String currentUserId;

    private DatabaseReference driversDatabaseRef;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        emailtxt=findViewById(R.id.customer_email);
        passwordtxt=findViewById(R.id.customer_password);
        nametxt=findViewById(R.id.Driver_name);
        notxt=findViewById(R.id.Driver_phonenumber);
        vehnotxt=findViewById(R.id.driver_car_name);
        login=findViewById(R.id.customer_login_btn);
        ProgressDialog loadingBar = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = emailtxt.getText().toString();
                String password = passwordtxt.getText().toString();
                String name = nametxt.getText().toString();
                String no = notxt.getText().toString();
                String vehno = vehnotxt.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Admin.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(Admin.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(Admin.this, "Please write your name...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(no))
                {
                    Toast.makeText(Admin.this, "Please write your Phone-number...", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(vehno))
                {
                    Toast.makeText(Admin.this, "Please write your Vehical-Number...", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("uid", mAuth.getCurrentUser().getUid());
                                userMap.put("name", name);
                                userMap.put("phone", no);
                                userMap.put("car", vehno);

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
                                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(userMap);



                                Toast.makeText(Admin.this, "Driver Registered", Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(Admin.this, "Please Try Again. Error Occurred, while registering... ", Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}