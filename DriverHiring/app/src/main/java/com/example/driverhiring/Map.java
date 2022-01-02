package com.example.driverhiring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    Intent intent;


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

        getSupportActionBar().hide();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_menu_driver);
        View headerview = navigationView.getHeaderView(0);
        TextView email = headerview.findViewById(R.id.nav_mail);
        TextView name = headerview.findViewById(R.id.nav_name);

        FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nametxt= snapshot.child("name").getValue(String.class);
                String emailtxt= snapshot.child("email").getValue(String.class);

                name.setText(nametxt);
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
                        intent = new Intent(Map.this,View_Profile.class);
                        intent.putExtra("Type","Customers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Map.this, Edit_Profile.class);
                        intent.putExtra("Type","Customers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Map.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Map.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        intent = new Intent(Map.this, History.class);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    default:
                        return true;

                }


                return true;
            }
        });


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