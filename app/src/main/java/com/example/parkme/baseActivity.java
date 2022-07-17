package com.example.parkme;

import static com.example.parkme.R.string.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class baseActivity extends AppCompatActivity {
   ActionBarDrawerToggle actionBarDrawerToggle;
   DrawerLayout drawerLayout;
   private TextView textView,twoSlot,fourSlot;
   String userid;
   FirebaseAuth firebaseAuth;
   Button button,locationbtn;
   boolean ck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        drawerLayout = findViewById(R.id.dl);
        textView = findViewById(R.id.textView7);
        twoSlot = findViewById(R.id.twoSlot);
        fourSlot = findViewById(R.id.fourSlot);
        button = findViewById(R.id.button);
        locationbtn = findViewById(R.id.MyLocation);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, Open, Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        firebaseAuth = FirebaseAuth.getInstance();
        userid =  firebaseAuth.getCurrentUser().getUid();
     //   SharedPreferences preferences =  getApplicationContext().getSharedPreferences("bCounter",0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.slot)
                {
                    startActivity(new Intent(baseActivity.this,SlotUpdate.class));
                }
                if(id==R.id.home) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                if(id==R.id.profile){
                   startActivity(new Intent(getApplicationContext(),Profile.class));
                }
                if(id==R.id.addpark){
                    startActivity(new Intent(baseActivity.this,AddActivity.class));
                }
                if(id==R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }

                return true;
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startActivity(new Intent(getApplicationContext(),Booking.class));
          }
      });
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()==0)
                {
                    finish();
                    startActivity(new Intent(baseActivity.this,AddActivity.class));

                }
                String pname,City,Address;
                pname=snapshot.child("pname").getValue().toString();
                City = snapshot.child("City").getValue().toString();
                Address = snapshot.child("address").getValue().toString();
                textView.setText("Parking Name:"+pname+"\nCity:"+City+"\nAddress:"+Address);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference referenceS = FirebaseDatabase.getInstance().getReference(userid).child("Slots");
        referenceS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                  if(snapshot.getChildrenCount()!=0)
                  {
                      twoSlot.setText(snapshot.child("Two").getValue().toString());
                      fourSlot.setText(snapshot.child("Four").getValue().toString());
                  }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("OwnerBooking").child(userid);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences gt = getApplicationContext().getSharedPreferences("bCounter",0);
                if(gt.getInt("c",0)!=snapshot.getChildrenCount())
                {
                    SharedPreferences.Editor editor = gt.edit();
                    editor.putInt("c",(int)snapshot.getChildrenCount());
                    editor.apply();
                     notification();
                }
                Log.d("vipulx",""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ck=false;

                DatabaseReference db = FirebaseDatabase.getInstance().getReference(userid);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!ck)
                        {
                            String lon=snapshot.child("Longitude").getValue().toString();
                            String lat=snapshot.child("Latitude").getValue().toString();
                            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " (" + "Parking" + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            ck=true;
                            startActivity(intent);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }
    void notification()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("nid","nid", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"nid")
                .setContentText("Alert!")
                .setSmallIcon(R.drawable.notification)
                .setAutoCancel(true)
                .setContentText("New Booking!");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }
}