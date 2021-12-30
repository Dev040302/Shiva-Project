package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.driverhiring.databinding.ActivityMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    Double Slan, Slong, Elan, Elong;
    EditText fromtxt,totxt,fromdate,todate,time;
    Button request;
    Marker st,sp;
    String start,stop;
    LatLng startpos,stoppos;
    int count=1;
    Calendar myCalendar=Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromtxt = findViewById(R.id.from12345678);
        totxt = findViewById(R.id.to);
        fromdate = findViewById(R.id.fromdate);
        todate = findViewById(R.id.todate);
        time = findViewById(R.id.time);
        request = findViewById(R.id.req_btn);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Map.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        time.setText(hourOfDay + ":" + minutes);
                    }
                }, 0, 0, false);

                timePickerDialog.show();
            }
        });


                fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);
                        String myFormat="dd/MM/yy";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                        fromdate.setText(dateFormat.format(myCalendar.getTime()));

                    }
                };

                new DatePickerDialog(Map.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);
                        String myFormat="dd/MM/yy";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                        todate.setText(dateFormat.format(myCalendar.getTime()));

                    }
                };

                new DatePickerDialog(Map.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fromdate.getText().toString().isEmpty()){
                    fromdate.setError("Date is required");
                    return;
                }
                if(todate.getText().toString().isEmpty()){
                    todate.setError("Date is required");
                    return;
                }
                if(fromtxt.getText().toString().isEmpty()){
                    fromtxt.setError("Place is required");
                    return;
                }
                if(totxt.getText().toString().isEmpty()){
                    totxt.setError("Place is required");
                    return;
                }
                if(time.getText().toString().isEmpty()){
                    time.setError("Date is required");
                    return;
                }

                orders customer = new orders(fromdate.getText().toString().trim(), todate.getText().toString().trim(), time.getText().toString().trim(), fromtxt.getText().toString().trim(), totxt.getText().toString().trim(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                saveData(customer);


                Toast.makeText(Map.this, "Request has been send to the driver", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void saveData(orders customer) {

        DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference("CustomerRequirement")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.setValue(customer);
        myRef.child("Start").setValue(startpos);
        myRef.child("Stop").setValue(stoppos);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng erode = new LatLng(11.3410, 77.7172);

        st = mMap.addMarker(new MarkerOptions()
                .position(erode)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                .title("Start Position")
                .draggable(true));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                if(marker.equals(st)){
                    Slan = marker.getPosition().latitude;
                    Slong = marker.getPosition().longitude;

                    st.setPosition(new LatLng(Slan, Slong));
                    startpos = new LatLng(Slan,Slong);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(startpos));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(Slan,Slong,1);
                        Address obj = addresses.get(0);
                        start= obj.getLocality();


                        fromtxt.setText(start);

                        if(count == 1) {
                            sp = mMap.addMarker(new MarkerOptions()
                                    .position(erode)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                                    .draggable(true));

                            count=2;
                        }

                    } catch (IOException e) {

                    }
                }
                else if(marker.equals(sp)){
                    Elan = marker.getPosition().latitude;
                    Elong = marker.getPosition().longitude;

                    sp.setPosition(new LatLng(Elan, Elong));
                    stoppos = new LatLng(Elan,Elong);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(stoppos));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(Elan,Elong,1);
                        Address obj = addresses.get(0);
                        stop= obj.getLocality();


                        totxt.setText(stop);



                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(erode));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}