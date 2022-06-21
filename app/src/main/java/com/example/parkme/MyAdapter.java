package com.example.parkme;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{
    ArrayList<Model> mList;
    Context context;
    public MyAdapter(Context context,ArrayList<Model> mList)
    {
        this.mList=mList;
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.items,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    Model model = mList.get(position);
    holder.vehicle.setText(model.getVehicle());
    holder.pmode.setText(model.getPayment());
    holder.status.setText(model.getBookingStatus());
    holder.datetime.setText(model.getDateTime());
    holder.id.setText(model.getId());
        if(holder.status.getText().equals("Confirmed"))
        {
            holder.status.setText("Confirmed");
            holder.status.setTextColor(Color.rgb(0,153,0));
            holder.btn.setBackgroundColor(Color.rgb(0,153,0));
           holder.btn.setText("CONFIRMED");
           holder.cancelbtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView vehicle, datetime, status, pmode, id;
        Button btn, cancelbtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicle = itemView.findViewById(R.id.vehicle);
            datetime = itemView.findViewById(R.id.datetime);
            status = itemView.findViewById(R.id.status);
            pmode = itemView.findViewById(R.id.paymentMode);
            btn = itemView.findViewById(R.id.status_upadte);
            id = itemView.findViewById(R.id.bookingId);
            cancelbtn = itemView.findViewById(R.id.cancel);
            btn.setTag(10);
            cancelbtn.setTag(20);
            cancelbtn.setBackgroundColor(Color.rgb(237, 0, 8));


            btn.setOnClickListener(this);
            cancelbtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //
            if (view.getId() == btn.getId()) {
                if (status.getText().equals("Pending")) {
                    status.setText("Confirmed");
                    status.setTextColor(Color.rgb(0, 153, 0));
                    btn.setBackgroundColor(Color.rgb(0, 153, 0));
                    btn.setText("CONFIRMED");
                    cancelbtn.setVisibility(View.GONE);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());
                    db.child("BookingStatus").setValue("Confirmed");
                    DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("Owners").child(FirebaseAuth.getInstance().getUid().toString());
                    boolean[] ck = {false};
                    if (vehicle.getText().equals("Two Wheeler")) {
                        dbs.child("Slots").child("Two").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!ck[0]) {
                                    int x = Integer.parseInt(snapshot.getValue().toString());
                                    dbs.child("Slots").child("Two").setValue(x - 1);
                                    ck[0] = true;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                }
                    boolean[] lk={false};
                    Log.d("vipulxx",vehicle.getText().toString());
                    DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("Owners").child(FirebaseAuth.getInstance().getUid().toString());
                    if (vehicle.getText().equals("FourWheeler")) {
                        Log.d("vipulxx","changed");
                        dbf.child("Slots").child("Four").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!lk[0]) {

                                    int x = Integer.parseInt(snapshot.getValue().toString());
                                    dbf.child("Slots").child("Four").setValue(x - 1);
                                    lk[0] = true;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
                if (view.getId() == cancelbtn.getId()) {
                    //Log.d("vipulx",cancelbtn.getText().toString());
                    //Log.d("vipulx","Clicked");
                    // cancelbtn.setText("CANCELED");
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());
                    db.child("BookingStatus").setValue("Canceled");

                }
            }
        }
    }

}
