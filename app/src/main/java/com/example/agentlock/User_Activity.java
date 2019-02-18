package com.example.agentlock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.TimeUnit;

public class User_Activity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 600000;
    private static final int gallery = 1;
    Button otp, end, upload;
    ImageView img;
    private ProgressDialog loadingBar;
    private String user;
    private  FirebaseAuth mAuth;
    private DatabaseReference myref;
    private StorageReference PostImagesRef;
    AlertDialog.Builder alert;

    private Chronometer chronometer1;
    public boolean running;
    private long pauseChronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user_);


        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ().getUid ();
        myref = FirebaseDatabase.getInstance ().getReference ("Loksmiths_Profile").child (user);


        img = (ImageView)findViewById (R.id.imageView2);
        upload = (Button)findViewById (R.id.button);
        otp = (Button)findViewById (R.id.Submit);
        end = (Button)findViewById (R.id.End);
        loadingBar = new ProgressDialog (this);
        alert = new AlertDialog.Builder (this);
        chronometer1 = (Chronometer)findViewById (R.id.chronometer);

        otp.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startChronometer();

            }
        });


        end.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                stopChronometer();
                long minutes = TimeUnit.MILLISECONDS.toMinutes(pauseChronometer);

                //Chronometer_Time chronometer_time = new Chronometer_Time (pauseChronometer);
                FirebaseUser user = mAuth.getCurrentUser ();
                myref.child ("time").setValue (minutes);

            }
        });


        upload.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                Intent Galleryintent = new Intent ();
                Galleryintent.setAction (Intent.ACTION_GET_CONTENT);
                Galleryintent.setType ("image/*");
                startActivityForResult (Galleryintent, gallery);

            }
        });



        myref.child ("Camera_image").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists ()){
                    String image = dataSnapshot.getValue ().toString ();
                    Glide.with (User_Activity.this)
                            .load (image)
                            .into (img);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void startChronometer(){
        if(!running)
        {
            chronometer1.setBase (SystemClock.elapsedRealtime () - pauseChronometer);
            chronometer1.start ();
            running = true;
        }
    }
    public void stopChronometer()
    {
        if(running)
        {
            chronometer1.stop ();
            pauseChronometer = SystemClock.elapsedRealtime () - chronometer1.getBase ();
            running  = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode==gallery && resultCode==RESULT_OK && data!=null){
            loadingBar.setTitle ("Upload");
            loadingBar.setMessage ("Please Wait while image to upload...");
            loadingBar.setCanceledOnTouchOutside (false);
            loadingBar.show ();

            //Uri ImageUri = data.getData();


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
                                myref.child ("Camera_image").setValue (downloadUrl)
                                        .addOnCompleteListener (new OnCompleteListener<Void> () {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful ())
                                                {
                                                    Toast.makeText (User_Activity.this, "Image saved to database", Toast.LENGTH_SHORT).show ();
                                                    loadingBar.dismiss ();
                                                }
                                                else
                                                {
                                                    String message = task.getException ().getMessage ();
                                                    Toast.makeText (User_Activity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show ();
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
