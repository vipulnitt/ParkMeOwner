package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private ConnectivityManager manager;
    private NetworkInfo networkInfo;
    private GoogleMap map;
    private Geocoder geocoder;
    FirebaseFirestore firestore;
    Bitmap imagebitmap,bitmapback;
    DrawerLayout drawerLayout;
    private double selectedLat,selectedLong;
    private List<Address> addresses;
    String address;

    private EditText propertyName,selectAddress,pincode,city;
    private ImageView uploadpic,selectLocation,uploadpicback;
    private Button addadrs,next;
    private  int check=0;
    private Uri fileuriIn,fileuriEx;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    String userid;
    private TextView textView5,textView6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        drawerLayout= findViewById(R.id.maplayout);
        propertyName = findViewById(R.id.propertyName);
        selectAddress = findViewById(R.id.address);
        uploadpicback = findViewById(R.id.imageView2);
        pincode = findViewById(R.id.pinCode);
        city = findViewById(R.id.city);
        next = findViewById(R.id.next);
        selectLocation = findViewById(R.id.locbtn);
        uploadpic = findViewById(R.id.imageView);
        textView5 = findViewById(R.id.textView5);
        textView6=findViewById(R.id.textView6);
        addadrs = findViewById(R.id.addloc);
        addadrs.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference("Owners");
        databaseRef = FirebaseDatabase.getInstance().getReference(userid);

        firestore = FirebaseFirestore.getInstance();
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.setVisibility(View.VISIBLE);
                addadrs.setVisibility(View.VISIBLE);
                uploadpic.setVisibility(View.GONE);
                textView5.setVisibility(View.GONE);
                textView6.setVisibility(View.GONE);
                uploadpicback.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
            }
        });
        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=1;
                Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takepic,1);
                } catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        uploadpicback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=2;
                Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takepic,1);
                } catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        client = LocationServices.getFusedLocationProviderClient(this);
        permission();
        addadrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city.setText(addresses.get(0).getSubAdminArea());
                selectAddress.setText(address);
                pincode.setText(addresses.get(0).getPostalCode());
                drawerLayout.setVisibility(View.GONE);
                addadrs.setVisibility(View.GONE);
                uploadpic.setVisibility(View.VISIBLE);
                uploadpicback.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                textView5.setVisibility(View.VISIBLE);
                textView6.setVisibility(View.VISIBLE);

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(fileuriEx==null)
               {
                   Toast.makeText(getApplicationContext(),"Please Click Exterior Picture!",Toast.LENGTH_SHORT).show();
               }else if(fileuriIn==null)
               {
                   Toast.makeText(getApplicationContext(),"Please Click Interior Picture!",Toast.LENGTH_SHORT).show();
               }else if(selectedLat==0||selectedLong==0)
               {
                   Toast.makeText(getApplicationContext(),"Please Location On Map!",Toast.LENGTH_SHORT).show();
               }else if(propertyName==null)
               {
                   Toast.makeText(getApplicationContext(),"Please Fill property Name!",Toast.LENGTH_SHORT).show();
               }else
               {
                   Log.d("xyzzz",""+selectedLat);
                   String filename =userid+"Exterior"+"."+getFileExtension(fileuriEx);
                   uploadFile(filename,fileuriEx,0);
                   filename = userid+"Interior"+"."+getFileExtension(fileuriIn);
                   uploadFile(filename,fileuriIn,1);
                   DocumentReference db = firestore.collection("parking").document(userid);
                   String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(selectedLat, selectedLong));
                   Map<String, Object> updates = new HashMap<>();
                   updates.put("geohash", hash);
                   updates.put("lat", selectedLat);
                   updates.put("lng", selectedLong);
                   updates.put("userid", userid);
                   // updates.put("Id",userid);
                   db.set(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           Log.d("vipulpatel","Success");
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Log.d("vipulpatel",e.getMessage());
                       }
                   });

                   databaseRef.child("pname").setValue(propertyName.getText().toString());
                   databaseRef.child("address").setValue(selectAddress.getText().toString());
                   databaseRef.child("pinCode").setValue(pincode.getText().toString());
                   databaseRef.child("City").setValue(city.getText().toString());
                   databaseRef.child("Longitude").setValue(selectedLong);
                   databaseRef.child("Latitude").setValue(selectedLat);
                   databaseRef.child("Slots").child("Two").setValue(0);
                   databaseRef.child("Slots").child("Four").setValue(0);
                   startActivity(new Intent(getApplicationContext(),ownerDetails.class));
               }

            }
        });
    }

    public void permission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
getCurrentLocation();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Current Location!");
                            selectedLat = latLng.latitude;
                            selectedLong = latLng.longitude;
                            getAddress(latLng.longitude,latLng.latitude);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                            googleMap.addMarker(markerOptions).showInfoWindow();
                            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull LatLng latLng) {
                                    checkConnectivity();
                                    if(networkInfo.isConnected()&&networkInfo.isAvailable())
                                    {
                                      selectedLat = latLng.latitude;
                                      selectedLong = latLng.longitude;
                                      getAddress(selectedLong,selectedLat);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Please Check Internet Connectivity!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

            }
        });
    }
private void checkConnectivity()
{
    manager = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
    networkInfo = manager.getActiveNetworkInfo();
}
private  void getAddress(double longitude,double latitude)
{
    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
    if(latitude!=0)
    {
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if(addresses!=null)
    {
        address = addresses.get(0).getAddressLine(0);
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String Pincode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        String district = addresses.get(0).getSubAdminArea();
        if(address!=null)
        {
            map.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(latitude,longitude);
            markerOptions.position(latLng).title(address);
            map.addMarker(markerOptions).showInfoWindow();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extra = data.getExtras();
        if(check==1) {
            imagebitmap = (Bitmap) extra.get("data");
            fileuriEx = getImageUri(getApplicationContext(),imagebitmap);
            //uploadpic.setImageBitmap(Bitmap.createScaledBitmap(imagebitmap, 100, 100, false));
            Picasso.get().load(fileuriEx)
                    .resize(100,100)
                    .into(uploadpic);
            check=0;
        }
        if(check==2)
        {
            bitmapback = (Bitmap) extra.get("data");
            fileuriIn = getImageUri(getApplicationContext(),bitmapback);
           // uploadpicback.setImageBitmap(Bitmap.createScaledBitmap(bitmapback,100,100,false));
            Picasso.get()
                    .load(fileuriIn)
                    .resize(100,100)
                    .into(uploadpicback);
            check=0;
        }
    }
    private void uploadFile(String fileName,Uri fileuri,int x)
    {
        Log.d("vipulc",""+fileuri);
        if(fileuri!=null)
        {
            StorageReference storageReference = storageRef.child(fileName);
            storageReference.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String uploadId = databaseRef.push().getKey();
                 //   databaseRef.child(uploadId).setValue(taskSnapshot.getStorage().getDownloadUrl().toString());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(x==0)
                                databaseRef.child("Interior").setValue(uri.toString());
                            if(x==1)
                                databaseRef.child("Exterior").setValue(uri.toString());
                            Log.d("vipull",""+uri);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please Upload Document!", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title"+ now, null);
        return Uri.parse(path);
    }
}