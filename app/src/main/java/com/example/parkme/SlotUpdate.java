package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SlotUpdate extends AppCompatActivity {
    ImageButton up2Button,down2Button,up4Button,down4Button;
    TextView twoCount,fourCount;
    int twoSlots=0,fourSlots=0;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String userid;
    Button updateSlot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_update);
        twoCount = findViewById(R.id.twoCount);
        fourCount= findViewById(R.id.fourCount);
        up2Button = findViewById(R.id.up2Button);
        down2Button = findViewById(R.id.down2Button);
        up4Button = findViewById(R.id.up4Button);
        down4Button=findViewById(R.id.down4Button);
        updateSlot = findViewById(R.id.updateSlot);
        firebaseAuth = FirebaseAuth.getInstance();
        userid= firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Owners");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Owners").child(userid).child("Slots");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] arr= new String[2];
                int i=0;
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    arr[i]=snapshot1.getValue().toString();
                    i++;
                }
                if(i==0)
                {
                    twoCount.setText("0");
                    fourCount.setText("0");
                }else
                {
                    fourSlots=Integer.parseInt(arr[0]);
                    fourCount.setText(arr[0]);
                    twoSlots=Integer.parseInt(arr[1]);
                    twoCount.setText(arr[1]);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        up2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoSlots++;
                twoCount.setText(""+twoSlots);
            }
        });
        down2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoSlots--;
                twoCount.setText(""+twoSlots);
            }
        });
        up4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fourSlots++;
                fourCount.setText(""+fourSlots);
            }
        });
        down4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fourSlots--;
                fourCount.setText(""+fourSlots);
            }
        });
        updateSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(userid).child("Slots").child("Four").setValue(""+fourSlots);
                databaseReference.child(userid).child("Slots").child("Two").setValue(""+twoSlots);
                finish();

            }
        });
    }
}