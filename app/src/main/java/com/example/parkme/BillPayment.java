package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class BillPayment extends AppCompatActivity {
   private String id;
   private String time;
   TextView txtpname,txttime,txtvehicle,txtinAt,txtoutAt,txtmode,txtid,txtamount,txtduration,txtprice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payment);
        txtpname = findViewById(R.id.pname);
        txtid=findViewById(R.id.bookingId);
        txttime = findViewById(R.id.datetime);
        txtvehicle = findViewById(R.id.vehicle);
        txtinAt = findViewById(R.id.inTime);
        txtoutAt = findViewById(R.id.outTime);
        txtduration = findViewById(R.id.duration);
        txtamount= findViewById(R.id.amount);;
        txtmode =findViewById(R.id.mode);
        txtprice= findViewById(R.id.price);
        id= getIntent().getStringExtra("Id");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id);

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtid.setText(id);
                String dt=snapshot.child("DateTime").getValue().toString();
                txttime.setText(sfd.format(new Date(Long.parseLong(dt))));
                txtvehicle.setText(snapshot.child("Vehicle").getValue().toString());
                txtinAt.setText(sfd.format(new Date(Long.parseLong(snapshot.child("inTime").getValue().toString()))));
                txtoutAt.setText(sfd.format(new Date(Long.parseLong(snapshot.child("outTime").getValue().toString()))));
               txtmode.setText(snapshot.child("Payment").getValue().toString());
                Long dur = Long.parseLong(snapshot.child("duration").getValue().toString());
               // Log.d("vipulxyz",snapshot.child("duration").toString();
                 int price =Integer.parseInt(snapshot.child("Price").getValue().toString());
                double hrs=((double) (dur/1000)/3600);
                int amount= (int) (hrs*price);
               txtduration.setText(""+hrs);
               txtamount.setText(""+amount);
               txtprice.setText(""+price);
                DatabaseReference dr= FirebaseDatabase.getInstance().getReference(snapshot.child("OwnerId").getValue().toString());
                dr.child("pname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txtpname.setText(snapshot.getValue().toString());
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
    }
}