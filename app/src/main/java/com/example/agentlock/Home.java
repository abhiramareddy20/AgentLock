package com.example.agentlock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Home extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    private  FirebaseAuth mAuth;
    private DatabaseReference myref;
    private StorageReference PostImagesRef;
    private Uri ImageUri;

    Button b1,Update;
    private static final int gallery = 1;
    ImageButton profileImage;
    private String user;
    private EditText uNmae,E_mail,aadhar;
    private TextView number;
    private ProgressDialog loadingBar;
    private  String saveCurrentDate,saveCurrentTime,postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ().getUid ();
        myref = FirebaseDatabase.getInstance ().getReference ("Loksmiths_Profile").child (user);
        PostImagesRef = FirebaseStorage.getInstance ().getReference ().child ("Loksmih_Profile_Images");

        Update = (Button) findViewById (R.id.Update_Profile);
        aadhar =(EditText)findViewById (R.id.adhar);
        profileImage = (ImageButton) findViewById (R.id.agentImage);
        number = (TextView) findViewById (R.id.phone);
        uNmae = (EditText) findViewById (R.id.name);
        E_mail = (EditText) findViewById (R.id.email);
        loadingBar = new ProgressDialog (this);


        final String phone = getIntent ().getStringExtra ("Mobile");
        number.setText (phone);


        Update.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                String userName = uNmae.getText ().toString ().trim ();
                String mail = E_mail.getText ().toString ().trim ();
                String mobile = phone;
                String KYC = aadhar.getText ().toString ().trim ();

                if (TextUtils.isEmpty (userName)) {
                    Toast.makeText (Home.this, "Plaese fill all the details", Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (TextUtils.isEmpty (mail)) {
                    Toast.makeText (Home.this, "Please Fill all the Details", Toast.LENGTH_SHORT).show ();
                    return;
                }
                if(TextUtils.isEmpty (KYC))
                {
                    Toast.makeText (Home.this, "Please Fill all the details", Toast.LENGTH_SHORT).show ();
                    return;
                }

                    Loksmith_details loksmith_details = new Loksmith_details (userName, mail, mobile,KYC);
                    FirebaseUser user = mAuth.getCurrentUser ();
                    myref.child (user.getUid ()).setValue (loksmith_details);

                    Intent i = new Intent (Home.this,Jobs.class);
                    startActivity (i);
                    Toast.makeText (Home.this, "Profile Updated", Toast.LENGTH_SHORT).show ();
                }



        });

        profileImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                Intent Galleryintent = new Intent ();
                Galleryintent.setAction (Intent.ACTION_GET_CONTENT);
                Galleryintent.setType ("image/*");
                startActivityForResult (Galleryintent, gallery);
            }
        });

        myref.child ("image").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ()){
                    String image = dataSnapshot.getValue ().toString ();
                    Glide.with (Home.this)
                            .load (image)
                            .into (profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode==gallery && resultCode==RESULT_OK && data!=null){
            loadingBar.setTitle ("Upload");
            loadingBar.setMessage ("Please Wait while image to upload...");
            loadingBar.setCanceledOnTouchOutside (false);
            loadingBar.show ();

            Uri ImageUri = data.getData();


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult (data);

            if (resultCode == RESULT_OK) {


                Uri resultUri = result.getUri ();


                final StorageReference filePath = PostImagesRef.child (user + ".jpg");

                filePath.putFile (resultUri).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   filePath.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                       @Override
                       public void onSuccess(Uri uri) {

                           final String downloadUrl = uri.toString ();
                           myref.child ("image").setValue (downloadUrl)
                                   .addOnCompleteListener (new OnCompleteListener<Void> () {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful ())
                                        {
                                            Toast.makeText (Home.this, "Image saved to database", Toast.LENGTH_SHORT).show ();
                                            loadingBar.dismiss ();
                                        }
                                        else
                                        {
                                            String message = task.getException ().getMessage ();
                                            Toast.makeText (Home.this, "Error Occured", Toast.LENGTH_SHORT).show ();
                                            loadingBar.dismiss ();
                                        }


                                       }
                                   });
                       }
                   });
                    }
                });
            }
        }


    }





}
