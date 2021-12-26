package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class Customer_Reg extends AppCompatActivity {

    EditText Cemail,Cname,Cphone,Caddress,Cpassword;
    Button Cregister;
    FirebaseAuth Cusdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_reg);

        Cemail=(EditText)findViewById(R.id.email);
        Cname=(EditText)findViewById(R.id.name);
        Cphone=(EditText)findViewById(R.id.number);
        Caddress=(EditText)findViewById(R.id.address);
        Cpassword=(EditText)findViewById(R.id.password);
        Cregister=(Button)findViewById(R.id.signupbtn);
        Cusdb=FirebaseAuth.getInstance();

        Cregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=Cname.getText().toString().trim();
                String email=Cemail.getText().toString().trim();
                String phone =Cphone.getText().toString().trim();
                String address=Caddress.getText().toString().trim();
                String password=Cpassword.getText().toString().trim();

                if(name.isEmpty()){
                    Cname.setError("name is required");
                    Cname.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    Cemail.setError("Enter email");
                    Cemail.requestFocus();
                    return;
                }
                if(phone.isEmpty()){
                    Cphone.setError("Enter the phone number");
                    Cphone.requestFocus();
                    return;
                }
                if(address.isEmpty()){
                    Caddress.setError("Enter the address");
                    Caddress.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    Cpassword.setError("Enter the password");
                    Cpassword.requestFocus();
                    return;
                }

               Cusdb.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){

                           User user = new User(name,email,phone,address,password);

                           FirebaseDatabase.getInstance().getReference("Users").child("Customers")
                                   .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Toast.makeText(Customer_Reg.this,"Registered successfully",Toast.LENGTH_LONG).show();
                                       startActivity(new Intent(Customer_Reg.this,start.class));

                                   }
                                   else {
                                       Toast.makeText(Customer_Reg.this,"failed to register",Toast.LENGTH_LONG).show();
                                   }

                               }
                           }) ;
                       }
                   }
               });


            }
        });

    }
}