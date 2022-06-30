package com.example.parkme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        holder.datetime.setText(sfd.format(new Date(Long.parseLong(model.getDateTime()))));
    holder.id.setText(model.getId());
        if(holder.status.getText().equals("CheckedOut"))
        {
     holder.cancelbtn.setVisibility(View.GONE);
     holder.btn.setText("Details");
     holder.btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent = new Intent(context,BillPayment.class);
             intent.putExtra("Id",model.getId());
             context.startActivity(intent);
         }
     });
        }
        if(holder.status.getText().equals("Canceled"))
        {
            holder.cancelbtn.setVisibility(View.GONE);
            holder.checkin.setVisibility(View.GONE);
            holder.btn.setVisibility(View.GONE);
        }
        if(holder.status.getText().equals("Confirmed"))
        {
            holder.status.setTextColor(Color.rgb(246,190,0));
           holder.btn.setVisibility(View.GONE);
           holder.cancelbtn.setVisibility(View.GONE);
           holder.txt.setVisibility(View.VISIBLE);
           holder.checkin.setVisibility(View.VISIBLE);

        }
        if(holder.status.getText().equals("Checked In"))
        {
            holder.status.setTextColor(Color.rgb(0,153,0));
            holder.btn.setVisibility(View.GONE);
            holder.cancelbtn.setVisibility(View.GONE);
            holder.checkin.setText("CheckOut");
            holder.checkin.setVisibility(View.VISIBLE);
        }
        if(holder.checkin.getText().toString().equals("CheckOut"))
        {
            holder.checkin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,BillPayment.class);
                    intent.putExtra("Id",model.getId());
                    com.google.firebase.Timestamp timestamp=com.google.firebase.Timestamp.now();
                    final boolean[] ck = {true};
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(model.getId());
                    long ti=timestamp.getSeconds()*1000;
                    db.child("outTime").setValue(ti);
                    db.child("BookingStatus").setValue("CheckedOut");
                    db.child("inTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tm=snapshot.getValue().toString();
                            if(ck[0])
                            {
                                long takenTime = ti-Long.parseLong(tm);
                                db.child("duration").setValue(takenTime);
                                ck[0] =false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    holder.checkin.setText("Details");
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView vehicle, datetime, status, pmode, id;
        Button btn, cancelbtn,checkin;
        EditText otpText;
        TextInputLayout txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicle = itemView.findViewById(R.id.vehicle);
            datetime = itemView.findViewById(R.id.datetime);
            status = itemView.findViewById(R.id.status);
            pmode = itemView.findViewById(R.id.paymentMode);
            btn = itemView.findViewById(R.id.status_upadte);
            id = itemView.findViewById(R.id.bookingId);
            cancelbtn = itemView.findViewById(R.id.cancel);
            checkin = itemView.findViewById(R.id.checkIn);
            otpText = itemView.findViewById(R.id.edit_text);
            txt = itemView.findViewById(R.id.filledTextField);
            btn.setTag(10);
            cancelbtn.setTag(20);
            checkin.setTag(30);
            cancelbtn.setBackgroundColor(Color.rgb(237, 0, 8));
            btn.setOnClickListener(this);
            cancelbtn.setOnClickListener(this);
            checkin.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //
            if (view.getId() == btn.getId()&&btn.getText().toString().equals("CONFIRM")) {
                if (status.getText().equals("Pending")) {
                    status.setText("Confirmed");
                    status.setTextColor(Color.rgb(0, 153, 0));
                    btn.setBackgroundColor(Color.rgb(0, 153, 0));
                    cancelbtn.setVisibility(View.GONE);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());
                    db.child("BookingStatus").setValue("Confirmed");
                    DatabaseReference dbs = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid().toString());
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
                    DatabaseReference dbf = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid().toString());
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

                btn.setVisibility(View.GONE);
                checkin.setVisibility(View.VISIBLE);
                txt.setVisibility(View.VISIBLE);
                otpText.setVisibility(View.VISIBLE);
            }
            if (view.getId() == cancelbtn.getId()) {
                //Log.d("vipulx",cancelbtn.getText().toString());
                //Log.d("vipulx","Clicked");
                // cancelbtn.setText("CANCELED");
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());
                db.child("BookingStatus").setValue("Canceled");

            }
            if(view.getId()==checkin.getId()&&checkin.getText().toString().equals("CheckIn"))
            {
                String otp = otpText.getText().toString();
                if(otp.length()!=6)
                {
                    Toast.makeText(view.getContext(),"Please Enter 6 digit OTP",Toast.LENGTH_SHORT).show();
                }else
                {
                    DatabaseReference db= FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());

                    db.child("otp").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(otp.equals(snapshot.getValue().toString()))
                                {
                                    db.child("checkedIn").setValue(true);
                                    db.child("inTime").setValue(com.google.firebase.Timestamp.now().getSeconds()*1000);
                                    db.child("BookingStatus").setValue("Checked In");
                                    otpText.getText().clear();
                                    txt.setVisibility(View.GONE);
                                    otpText.setVisibility(View.GONE);
                                    checkin.setText("CHECKOUT");
                                    Log.d("vipulu","Ok");
                                }else
                                {
                                    Toast.makeText(view.getContext(),"The OTP entered is incorrect!",Toast.LENGTH_SHORT).show();
                                }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

        }
    }

}
