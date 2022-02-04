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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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

public class Driver_Ride extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    String cusid,cusname,cusnumber,startpos,endpos,date;
    String rupee;
    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;
    Marker drivermar,startmar,stopmar;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    DatabaseReference ref;

    LatLng start,stop,driver;

    TextView name,phone;
    Button btn;

    int value=1,done=0,got=1;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);

        getSupportActionBar().hide();

        Intent i = getIntent();
        cusid = i.getStringExtra("uuid");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name=findViewById(R.id.name);
        phone=findViewById(R.id.number);
        btn=findViewById(R.id.btn);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_menu_driver);
        View headerview = navigationView.getHeaderView(0);
        TextView email1 = headerview.findViewById(R.id.nav_mail);
        TextView name1 = headerview.findViewById(R.id.nav_name);

        FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nametxt= snapshot.child("name").getValue(String.class);
                String emailtxt= snapshot.child("email").getValue(String.class);

                name1.setText(nametxt);
                email1.setText(emailtxt);
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
                        intent = new Intent(Driver_Ride.this,View_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_edit_profile:
                        intent = new Intent(Driver_Ride.this, Edit_Profile.class);
                        intent.putExtra("Type","Drivers");
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case R.id.nav_Logout:

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Driver_Ride.this,MainMenu.class));
                        break;

                    case R.id.nav_change_password:

                        FirebaseAuth.getInstance().sendPasswordResetEmail((String) email1.getText())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Driver_Ride.this, "Reset Password Email Has been Send To Your Mail ID", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                    case R.id.nav_History:
                        intent = new Intent(Driver_Ride.this, History.class);
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    default:
                        return true;

                }


                return true;
            }
        });



        DatabaseReference cus = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(cusid);

        cus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cusname = snapshot.child("name").getValue(String.class);
                cusnumber = snapshot.child("phone").getValue(String.class);
                name.setText("Name :- "+ cusname);
                phone.setText("Number :-"+ cusnumber);
                Toast.makeText(Driver_Ride.this, cusname + " " + cusnumber, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref=FirebaseDatabase.getInstance().getReference("CurrentGoing").child(cusid);

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

                else if(value == 2){

                    AlertDialog.Builder alert = new AlertDialog.Builder(Driver_Ride.this);
                    View mview = getLayoutInflater().inflate(R.layout.driver_dialog_box,null);
                    EditText upi = mview.findViewById(R.id.txt_upi);
                    EditText amount = mview.findViewById(R.id.txt_amount);
                    Button btn = mview.findViewById(R.id.btn);
                    alert.setView(mview);
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(false);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference payment=ref.child("Payment");

                            payment.child("upi").setValue(upi.getText().toString());
                            payment.child("amount").setValue(amount.getText().toString());
                            rupee = amount.getText().toString();
                            payment.child("payment").setValue("None");
                            alertDialog.dismiss();

                        }
                    });

                    alertDialog.show();

                }
            }
        });

        ref.child("Payment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    String result = snapshot.child("payment").getValue(String.class);

                    Toast.makeText(Driver_Ride.this, result, Toast.LENGTH_SHORT).show();

                    if (result == "upi") {

                        Toast.makeText(Driver_Ride.this, "Customer is Paying Using UPI", Toast.LENGTH_SHORT).show();

                    }

                    else if (result == "cash") {

                        Toast.makeText(Driver_Ride.this, "Customer is Paying Cash", Toast.LENGTH_SHORT).show();
                        Historys amount = new Historys(startpos, endpos, date, rupee);

                        FirebaseDatabase.getInstance().getReference("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(amount);
                    }

                    else if (result == "done") {

                        Toast.makeText(Driver_Ride.this, "Payment successful", Toast.LENGTH_SHORT).show();



                        Historys amount = new Historys(startpos, endpos, date, rupee);

                        FirebaseDatabase.getInstance().getReference("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(amount);

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(got == 1){
                    startpos = snapshot.child("start_place").getValue(String.class);
                    endpos = snapshot.child("end_place").getValue(String.class);
                    date = snapshot.child("todate").getValue(String.class);

                    Double a = snapshot.child("Start").child("latitude").getValue(Double.class);
                    Double b = snapshot.child("Start").child("longitude").getValue(Double.class);

                    Double c = snapshot.child("Stop").child("latitude").getValue(Double.class);
                    Double d = snapshot.child("Stop").child("longitude").getValue(Double.class);

                    stop = new LatLng(a,b);

                    start = new LatLng(a,b);
                    driver = new LatLng(a,b);
                    stop = new LatLng(c,d);

                    startmar = mMap.addMarker(new MarkerOptions()
                            .position(start).title("Start Position")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    drivermar = mMap.addMarker(new MarkerOptions()
                            .position(start).title("Driver")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));

                    done=1;
                    got=2;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("Driver's Position").setValue(driver);
        name.setText("Name :- "+ cusname);
        phone.setText("Number :-"+ cusnumber);



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