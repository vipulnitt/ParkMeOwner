package com.example.parkme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ownerDetails extends AppCompatActivity {
   public EditText ownerName,ownerContact,ownerEmail;
   public Button submit;
   public ImageButton upload;
   private static final int PICK_FILE_REQUEST = 1;
   private Uri fileuri;
   private StorageReference storageRef;
   private DatabaseReference databaseRef;
   private FirebaseAuth firebaseAuth;
   String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_details);
        ownerName = findViewById(R.id.ownerName);
        ownerContact = findViewById(R.id.contact);
        ownerEmail = findViewById(R.id.owneremail);
        submit = findViewById(R.id.submit);
        upload = findViewById(R.id.imageButton);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();
        storageRef = FirebaseStorage.getInstance().getReference(userid);
        databaseRef = FirebaseDatabase.getInstance().getReference(userid);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
openFileChooser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oName = ownerName.getText().toString();
                String oContact = ownerContact.getText().toString();
                String oEmail = ownerEmail.getText().toString();
                databaseRef.child("ownerName").setValue(oName);
                databaseRef.child("ownerContact").setValue(oContact);
                databaseRef.child("ownerEmail").setValue(oEmail);
                userid = Objects.requireNonNull(firebaseAuth.getCurrentUser().getUid());
                if(fileuri==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Upload Documents!",Toast.LENGTH_SHORT).show();
                }else
                {
                    String filename = userid+System.currentTimeMillis()+"."+getFileExtension(fileuri);
                    uploadFile(filename);
                    startActivity(new Intent(ownerDetails.this,baseActivity.class));
                    finish();
                }


            }
        });

    }
    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_FILE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
fileuri = data.getData();
            Picasso.get().load(fileuri).into(upload);
        }
    }
    private void uploadFile(String fileName)
    {
   if(fileuri!=null)
   {
StorageReference storageReference = storageRef.child(fileName);
storageReference.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
     String uploadId = databaseRef.push().getKey();
     storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
         @Override
         public void onSuccess(Uri uri) {
             Log.d("vipull",""+uri);
             databaseRef.child("Document").setValue(uri.toString());
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

}