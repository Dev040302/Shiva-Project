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

public class Driver_Reg extends AppCompatActivity {

    EditText Demail,Dname,Dphone,Dpassword,Dlicence,Dexperince,Daddress;
    Button Dregister;
    FirebaseAuth DrDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reg);

        Demail=(EditText)findViewById(R.id.email);
        Dname=(EditText)findViewById(R.id.name);
        Dphone=(EditText)findViewById(R.id.number);
        Dpassword=(EditText)findViewById(R.id.password);
        Dlicence=(EditText)findViewById(R.id.licence);

        Daddress=(EditText)findViewById(R.id.address);
        Dregister=(Button)findViewById(R.id.signupbtn);

        DrDB=FirebaseAuth.getInstance();

        Dregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Dname.getText().toString().trim();
                String email=Demail.getText().toString().trim();
                String phone =Dphone.getText().toString().trim();
                String address=Daddress.getText().toString().trim();
                String licence=Dlicence.getText().toString().trim();

                String password=Dpassword.getText().toString().trim();

                if(name.isEmpty()){
                    Dname.setError("name is required");
                    Dname.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    Demail.setError("Enter email");
                    Demail.requestFocus();
                    return;
                }
                if(phone.isEmpty()){
                    Dphone.setError("Enter the phone number");
                    Dphone.requestFocus();
                    return;
                }
                if(address.isEmpty()){
                    Daddress.setError("Enter the address");
                    Daddress.requestFocus();
                    return;
                }
                if(licence.isEmpty()){
                    Dlicence.setError("Enter the licence number");
                    Dlicence.requestFocus();
                    return;
                }
                if(licence.length() != 15 ){
                    Dlicence.setError("Enter correct licence number");
                    Dlicence.requestFocus();
                    return;
                }




                if(password.isEmpty()){
                    Dpassword.setError("Enter the password");
                    Dpassword.requestFocus();
                    return;
                }

                DrDB.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    Driver_Users user = new Driver_Users(name,email,phone,address,licence,password);

                                    FirebaseDatabase.getInstance().getReference("Users").child("Drivers")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(Driver_Reg.this,"Registered successfully",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(Driver_Reg.this,Driver_Bookings.class));

                                            }
                                            else {
                                                Toast.makeText(Driver_Reg.this,"failed to register",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(Driver_Reg.this,"failed to register",Toast.LENGTH_LONG).show();
                                }

                            }
                        });

            }
        });


    }

}