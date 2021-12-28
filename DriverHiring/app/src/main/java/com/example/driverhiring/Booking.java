package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import com.example.driverhiring.databinding.ActivityBookingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Booking extends AppCompatActivity implements OnMapReadyCallback{

    int type=0,done=0;
    GoogleMap mMap;
    LatLng start,stop,driver;
    Marker startmar,stopmar,drivermar;
    String driid,driname,drinumber;
    TextView name,number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name=findViewById(R.id.Name);
        number=findViewById(R.id.phone);

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



            FirebaseDatabase.getInstance().getReference("CurrentGoing").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(done==0){
                        Double a = snapshot.child("Start").child("latitude").getValue(Double.class);
                        Double b = snapshot.child("Start").child("longitude").getValue(Double.class);

                        start = new LatLng(a,b);

                        Double c = snapshot.child("Stop").child("latitude").getValue(Double.class);
                        Double d = snapshot.child("Stop").child("longitude").getValue(Double.class);

                        stop = new LatLng(c,d);

                        startmar = mMap.addMarker(new MarkerOptions()
                                .position(start)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        stopmar = mMap.addMarker(new MarkerOptions()
                                .position(stop)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        drivermar = mMap.addMarker(new MarkerOptions()
                                .position(start)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                        done=1;

                    }

                    Double a = snapshot.child("Driver's Position").child("latitude").getValue(Double.class);
                    Double b = snapshot.child("Driver's Position").child("longitude").getValue(Double.class);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}