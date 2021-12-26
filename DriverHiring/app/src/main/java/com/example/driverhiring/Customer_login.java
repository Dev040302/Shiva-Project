package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.driverhiring.databinding.ActivityCustomerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Customer_login extends AppCompatActivity {

    EditText Ce,Cp;
    Button Cl,Cs;
    FirebaseAuth clog;
    String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCustomerBinding binding  = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Ce=(EditText)findViewById(R.id.emailedittxt);
        Cp=(EditText)findViewById(R.id.passwordedittxt);
        Cl=(Button)findViewById(R.id.loginbtn);
        Cs=(Button)findViewById(R.id.signupbtn);
        clog = FirebaseAuth.getInstance();

        Cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Customer_login.this,Customer_Reg.class));
            }
        });


        Cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    email = Ce.getText().toString().trim();
                    password = Cp.getText().toString().trim();
                    if (email.isEmpty()){
                        Ce.setError("Enter email");
                        Ce.requestFocus();
                        return;
                    }

                    if (password.isEmpty()){
                        Cp.setError("Enter password");
                        Cp.requestFocus();
                        return;
                    }
                }catch (Exception e){

                }

                String email = Ce.getText().toString().trim();
                String password = Cp.getText().toString().trim();

                if (email.isEmpty()){
                    Ce.setError("Enter email");
                    Ce.requestFocus();
                    return;
                }

                if (password.isEmpty()){
                    Cp.setError("Enter password");
                    Cp.requestFocus();
                    return;
                }

                clog.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(Customer_login.this,start.class));
                        }
                        else
                        {
                            Toast.makeText(Customer_login.this, "failed to login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}