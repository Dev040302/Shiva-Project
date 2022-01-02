package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseUser;

public class Driver_Login extends AppCompatActivity {

    EditText Ce,Cp;
    Button Cl,Cs;
    FirebaseAuth clog;
    String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        Ce=(EditText)findViewById(R.id.emailedittxt);
        Cp=(EditText)findViewById(R.id.passwordedittxt);
        Cl=(Button)findViewById(R.id.loginbtn);
        Cs=(Button)findViewById(R.id.signupbtn);
        clog = FirebaseAuth.getInstance();


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

                clog.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()){
                                startActivity(new Intent(Driver_Login.this,Driver_Bookings.class));


                            }
                            else{
                                user.sendEmailVerification();
                                Toast.makeText(Driver_Login.this, "Your Email is not verified yet\n Verification mail has been send again", Toast.LENGTH_LONG).show();
                            }
                            startActivity(new Intent(Driver_Login.this,Driver_Bookings.class));

                        }
                        else
                        {
                            Toast.makeText(Driver_Login.this, "failed to login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        Cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Driver_Login.this,Driver_Reg.class));
            }
        });

    }

}
