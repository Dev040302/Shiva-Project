package com.example.actingdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AdminLogin extends AppCompatActivity {

    EditText emailtxt,passwordtxt;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailtxt=findViewById(R.id.id);
        passwordtxt=findViewById(R.id.pass);
        login=findViewById(R.id.customer_login_btn);
        ProgressDialog loadingBar = new ProgressDialog(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String email = emailtxt.getText().toString();
                String password = passwordtxt.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(AdminLogin.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(AdminLogin.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();

                    String a="12345678",b="12345678";
                    if(email.equals("12345678") && password.equals("12345678")){
                        Intent intent = new Intent(AdminLogin.this, Admin.class);
                        startActivity(intent);}
                    else{
                        Toast.makeText(AdminLogin.this, "Wrong Credietials", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }



                }
            }
        });

    }
}