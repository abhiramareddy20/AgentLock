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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private EditText uNmae,E_mail;
    private TextView number;
    private ProgressDialog loadingBar;
    private  String saveCurrentDate,saveCurrentTime,postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ().getUid ();
        myref = FirebaseDatabase.getInstance ().getReference ("Loksmiths_Profile");
        PostImagesRef = FirebaseStorage.getInstance ().getReference ().child ("Loksmih_Profile_Images");

        Update = (Button) findViewById (R.id.Update_Profile);
        b1 = (Button) findViewById (R.id.LIC);
        profileImage = (ImageButton) findViewById (R.id.agentImage);
        number = (TextView) findViewById (R.id.phone);
        uNmae = (EditText) findViewById (R.id.name);
        E_mail = (EditText) findViewById (R.id.email);


        final String phone = getIntent ().getStringExtra ("Mobile");
        number.setText (phone);

        Update.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                String userName = uNmae.getText ().toString ().trim ();
                String mail = E_mail.getText ().toString ().trim ();
                String mobile = phone;




                if (TextUtils.isEmpty (userName)) {
                    Toast.makeText (Home.this, "Plaese fill all the details", Toast.LENGTH_SHORT).show ();
                    return;
                }
                if (TextUtils.isEmpty (mail)) {
                    Toast.makeText (Home.this, "Please Enter the Email address", Toast.LENGTH_SHORT).show ();
                    return;
                }







                    Loksmith_details loksmith_details = new Loksmith_details (userName, mail, mobile);
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

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode==gallery && resultCode==RESULT_OK && data!=null){
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                /*loadingBar.setTitle("set profile image");
                loadingBar.setMessage("Please wait your image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
*/
                Uri resultUri = result.getUri();


                StorageReference filePath = PostImagesRef.child(user + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Home.this, " Image uploaded successfully", Toast.LENGTH_SHORT).show();

                            String downloadUrl = task.getResult().getStorage ().getDownloadUrl().toString();

                            myref.child(user).child("image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(Home.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                               // loadingBar.dismiss();

                                            }else{
                                                String message = task.getException().toString();
                                                Toast.makeText(Home.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    });
                        }else{
                            String message = task.getException().toString();
                            Toast.makeText(Home.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }
                });
            }

        }


    }

}
