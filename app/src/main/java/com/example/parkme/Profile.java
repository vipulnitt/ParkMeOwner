package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class Profile extends AppCompatActivity {
  TextView name,contact,email;
  ImageView interior,exterior;
    private GoogleMap map;
    private DrawerLayout drawerLayout;
    SupportMapFragment mapFragment;
    String[] lat=new String[1];
    String[] lon=new String[1];
    private FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name= findViewById(R.id.profilename);
        contact = findViewById(R.id.profilecontact);
        email = findViewById(R.id.profileemail);
        interior = findViewById(R.id.Interior);
        exterior = findViewById(R.id.Exterior);
        //drawerLayout= findViewById(R.id.maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        String id = FirebaseAuth.getInstance().getUid().toString();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("ownerName").getValue().toString());
                contact.setText(snapshot.child("ownerContact").getValue().toString());
                email.setText(snapshot.child("ownerEmail").getValue().toString());
                String inurl= snapshot.child("Interior").getValue().toString();
                String exurl = snapshot.child("Exterior").getValue().toString();
                Picasso.get()
                        .load(inurl)
                        .resize(100,100)
                        .into(interior);
              Picasso.get()
                      .load(exurl)
                      .resize(100,100)
                      .into(exterior);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}