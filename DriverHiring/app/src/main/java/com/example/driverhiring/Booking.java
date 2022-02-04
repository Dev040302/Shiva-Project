package com.example.driverhiring;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.driverhiring.databinding.ActivityBookingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Booking extends AppCompatActivity implements OnMapReadyCallback,PaymentStatusListener{

    int type=0,done=0,set=0;
    GoogleMap mMap;
    LatLng start,stop,driver;
    Marker startmar,stopmar,drivermar;
    String driid,driname,drinumber;
    TextView name,number;
    Intent intent;
    DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name=findViewById(R.id.Name);
        number=findViewById(R.id.phone);

        getSupportActionBar().hide();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_menu_driver);
        View headerview = navigationView.getHeaderView(0);
        TextView email = headerview.findViewById(R.id.nav_mail);
        TextView name1 = headerview.findViewById(R.id.nav_name);

        FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nametxt= snapshot.child("name").getValue(String.class);
                String emailtxt= snapshot.child("email").getValue(String.class);

                name1.setText(nametxt);
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
                        intent = new Intent(Booking.this,View_Profile.class);
                        intent.putExtra("Type","Customers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Booking.this, Edit_Profile.class);
                        intent.putExtra("Type","Customers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Booking.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Booking.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        intent = new Intent(Booking.this, History.class);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    default:
                        return true;

                }


                return true;
            }
        });

        if(type == 0){

            FirebaseDatabase.getInstance().getReference("Pair").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    driid= snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(String.class);
                    Toast.makeText(Booking.this, driid, Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(driid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            driname = snapshot.child("name").getValue(String.class);
                            drinumber = snapshot.child("phone").getValue(String.class);

                            name.setText(driname);
                            number.setText(drinumber);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref=FirebaseDatabase.getInstance().getReference("CurrentGoing").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(set == 0 && snapshot.child("Payment").child("amount").exists()){

                        set=1;

                        DataSnapshot pay = snapshot.child("Payment");
                        
                        AlertDialog.Builder alert = new AlertDialog.Builder(Booking.this);
                        View mview = getLayoutInflater().inflate(R.layout.customer_dialogue_box,null);
                        TextView amount=mview.findViewById(R.id.bill_amount);
                        Button cashbtn = mview.findViewById(R.id.btn_cash);
                        Button upibtn = mview.findViewById(R.id.btn_upi);
                        amount.setText(pay.child("amount").getValue(String.class));
                        alert.setView(mview);
                        final AlertDialog alertDialog = alert.create();
                        alertDialog.setCanceledOnTouchOutside(false);

                        DatabaseReference payment=ref.child("Payment");

                        cashbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                                payment.child("payment").setValue("cash");

                            }
                        });

                        upibtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                payment.child("payment").setValue("upi");
                                String upi = pay.child("upi").getValue(String.class);
                                String amount = pay.child("amount").getValue(String.class);

                                PayAmount(upi,new Double(amount));

                            }
                        });

                        alertDialog.show();

                    }

                    if(done==0){
                        Double a = snapshot.child("Start").child("latitude").getValue(Double.class);
                        Double b = snapshot.child("Start").child("longitude").getValue(Double.class);

                        start = new LatLng(a,b);

                        Double c = snapshot.child("Stop").child("latitude").getValue(Double.class);
                        Double d = snapshot.child("Stop").child("longitude").getValue(Double.class);

                        stop = new LatLng(c,d);

                        startmar = mMap.addMarker(new MarkerOptions()
                                .position(start)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                        stopmar = mMap.addMarker(new MarkerOptions()
                                .position(stop)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        drivermar = mMap.addMarker(new MarkerOptions()
                                .position(start)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));

                        done=1;

                    }

                    Double a = snapshot.child("Drivers'Position").child("latitude").getValue(Double.class);
                    Double b = snapshot.child("Drivers'Position").child("longitude").getValue(Double.class);

                    driver= new LatLng(a,b);

                    drivermar.setPosition(driver);

                    LatLngBounds llb;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(driver).include(start).include(stop);
                    llb = builder.build();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,1));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void PayAmount(String upi, Double amount) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        String transcId = df.format(c);

        makePayment(amount.toString(), upi, driname,"Payment for Driver", transcId);

    }

    private void makePayment(String amount, String upi, String name, String desc, String transactionId) {
        // on below line we are calling an easy payment method and passing
        // all parameters to it such as upi id,name, description and others.
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                // on below line we are adding upi id.
                .setPayeeVpa(upi)
                // on below line we are setting name to which we are making oayment.
                .setPayeeName(name)
                // on below line we are passing transaction id.
                .setTransactionId(transactionId)
                // on below line we are passing transaction ref id.
                .setTransactionRefId(transactionId)
                // on below line we are adding description to payment.
                .setDescription(desc)
                // on below line we are passing amount which is being paid.
                .setAmount(amount)
                // on below line we are calling a build method to build this ui.
                .build();
        // on below line we are calling a start
        // payment method to start a payment.
        easyUpiPayment.startPayment();
        // on below line we are calling a set payment
        // status listener method to call other payment methods.
        easyUpiPayment.setPaymentStatusListener(this);
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        // on below line we are getting details about transaction when completed.
        String transcDetails = transactionDetails.getStatus().toString() + "\n" + "Transaction ID : " + transactionDetails.getTransactionId();

    }

    @Override
    public void onTransactionSuccess() {
        // this method is called when transaction is successful and we are displaying a toast message.
        Toast.makeText(this, "Transaction successfully completed..", Toast.LENGTH_SHORT).show();
        ref.child("Payment").child("payment").setValue("done");

    }

    @Override
    public void onTransactionSubmitted() {
        // this method is called when transaction is done
        // but it may be successful or failure.
        Log.e("TAG", "TRANSACTION SUBMIT");
    }

    @Override
    public void onTransactionFailed() {
        // this method is called when transaction is failure.
        Toast.makeText(this, "Failed to complete transaction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionCancelled() {
        // this method is called when transaction is cancelled.
        Toast.makeText(this, "Transaction cancelled..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppNotFound() {
        // this method is called when the users device is not having any app installed for making payment.
        Toast.makeText(this, "No app found for making transaction..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}