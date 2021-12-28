package com.example.driverhiring;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Customer_Booking extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String cusid,cusname,cusnumber;
    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;
    Marker drivermar,startmar,stopmar;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    DatabaseReference ref;

    LatLng start,stop,driver;

    TextView name,phone;
    Button btn;


    int value=1,done=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_booking);

        Intent intent = getIntent();
        cusid = intent.getStringExtra("uuid");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name=findViewById(R.id.name);
        phone=findViewById(R.id.number);
        btn=findViewById(R.id.btn);



        DatabaseReference cus = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(cusid);

        cus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cusname = snapshot.child("name").getValue(String.class);
                cusnumber = snapshot.child("phone").getValue(String.class);
                name.setText("Name :- "+ cusname);
                phone.setText("Number :-"+ cusnumber);
                Toast.makeText(Customer_Booking.this, cusname + " " + cusnumber, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref=FirebaseDatabase.getInstance().getReference("CurrentGoing").child(cusid);



        ref.child("Start").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double a = snapshot.child("latitude").getValue(Double.class);
                Double b = snapshot.child("longitude").getValue(Double.class);

                start = new LatLng(a,b);
                driver = new LatLng(a,b);
                stop = new LatLng(a,b);

                startmar = mMap.addMarker(new MarkerOptions()
                        .position(start).title("Start Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                drivermar = mMap.addMarker(new MarkerOptions()
                        .position(start).title("Driver")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                done=1;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("Stop").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double a = snapshot.child("latitude").getValue(Double.class);
                Double b = snapshot.child("longitude").getValue(Double.class);

                stop = new LatLng(a,b);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ref.child("Driver's Position").setValue(driver);
        name.setText("Name :- "+ cusname);
        phone.setText("Number :-"+ cusnumber);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(value == 1){
                    value =2;
                    btn.setText("End Trip");

                    stopmar = mMap.addMarker(new MarkerOptions().position(stop)
                            .title("Destination")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }
        });



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        driver = new LatLng(location.getLatitude(),location.getLongitude());

        ref.child("Driver's Position").setValue(driver);

        if(done==1){
            drivermar.setPosition(driver);

            LatLngBounds llb;

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(driver).include(start).include(stop);
            llb = builder.build();

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,1));
        }





        //mMap.moveCamera(CameraUpdateFactory.newLatLng(driver));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }
}