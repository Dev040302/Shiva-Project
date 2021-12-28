package com.example.driverhiring;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class orderAdapter extends FirebaseRecyclerAdapter<
        orders, orderAdapter.orderViewholder> {

    Context context;

    public orderAdapter(@NonNull FirebaseRecyclerOptions<orders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull orderViewholder holder, int position, @NonNull orders model) {

        holder.from.setText(model.getStart_place());
        holder.to.setText(model.getEnd_place());
        holder.fromdate.setText(model.getFromdate());
        holder.todate.setText(model.getTodate());
        holder.time.setText(model.getTime());
        holder.customerid = model.getUid();

        DatabaseReference myRef1=FirebaseDatabase.getInstance().getReference("CustomerRequirement");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Toast.makeText(v.getContext(), "Data Changed", Toast.LENGTH_SHORT).show();

                Double a=snapshot.child(holder.customerid).child("Start").child("latitude").getValue(Double.class);
                Double b=snapshot.child(holder.customerid).child("Start").child("longitude").getValue(Double.class);
                Double c=snapshot.child(holder.customerid).child("Stop").child("latitude").getValue(Double.class);
                Double d=snapshot.child(holder.customerid).child("Stop").child("longitude").getValue(Double.class);




                holder.Start=new LatLng(a,b);
                holder.Stop=new LatLng(c,d);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                orders customer = new orders(model.getFromdate(), model.getTodate(),model.getTime(),model.getStart_place(),model.getEnd_place(),model.getUid());

                DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("CurrentGoing").child(model.getUid());
                myRef.setValue(customer);
                myRef.child("Start").setValue(holder.Start);
                myRef.child("Stop").setValue(holder.Stop);
                myRef.child("Driver").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseDatabase.getInstance().getReference("Pair").child(holder.customerid).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Intent intent=new Intent(v.getContext(),Customer_Booking.class);
                intent.putExtra("uuid",holder.customerid);

                v.getContext().startActivity(intent);


            }
        });

    }

    @NonNull
    @Override
    public orderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders, parent, false);
        return new orderAdapter.orderViewholder(view);
    }

    public class orderViewholder extends RecyclerView.ViewHolder {

        TextView from,to,fromdate,todate,time;
        Button btn;
        private DatabaseReference reference;
        private String userID;
        String name,phone,customerid;
        FirebaseFirestore firestore;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        LatLng Start,Stop;


        public orderViewholder(@NonNull View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.fromplace);
            to = itemView.findViewById(R.id.toplace);
            fromdate = itemView.findViewById(R.id.fromdate);
            todate = itemView.findViewById(R.id.todate);
            time = itemView.findViewById(R.id.pickuptime);
            btn = itemView.findViewById(R.id.btn);



            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("DRIVERS");
            userID = user.getUid();
            reference.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     Driver_Users user = snapshot.getValue(Driver_Users.class);
                     if(user != null){
                         name = user.name;
                         phone =user.phone;
                     }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //btn.setOnClickListener(new View.OnClickListener() {
                //@Override
                //public void onClick(View v) {
                    /*final HashMap<String,Object> Details = new HashMap<>();
                    Details.put("Name",name);
                    Details.put("phone",phone);
                    Details.put("uuid",customerid);

                    firestore.collection("ACCEPTANCE").document(auth.getCurrentUser().getUid())//
                            .collection("CURRENTDRIVER").add(Details).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {//
                        @Override//
                        public void onComplete(@NonNull Task<DocumentReference> task) {//

                        }//
                    });//*/



                //}
           // });


        }
    }
}
