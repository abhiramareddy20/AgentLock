package com.example.agentlock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class User_Activity extends AppCompatActivity {


    Button otp, end, upload;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user_);

        img = (ImageView)findViewById (R.id.imageView2);
        upload = (Button)findViewById (R.id.button);
        otp = (Button)findViewById (R.id.Submit);
        end = (Button)findViewById (R.id.End);

        otp.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Toast.makeText (User_Activity.this, "Please enter OTP", Toast.LENGTH_SHORT).show ();
            }
        });

        end.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Toast.makeText (User_Activity.this, "Service Ended", Toast.LENGTH_SHORT).show ();
            }
        });

        upload.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult (intent,0);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        try {

            Bitmap bitmap = (Bitmap) data.getExtras ().get ("data");
            img.setImageBitmap (bitmap);
        }
        catch (Exception e){
            e.printStackTrace ();
        }

    }
}
