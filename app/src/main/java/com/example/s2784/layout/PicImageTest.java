package com.example.s2784.layout;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class PicImageTest extends AppCompatActivity {

    private Button btn_choosePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_image_test);

        btn_choosePic = (Button)findViewById(R.id.button);
        btn_choosePic.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if(uri != null) {
                Log.d("imgt", uri.getPath());

                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageView imageView = (ImageView)findViewById(R.id.imgv);
                    imageView.setImageBitmap(bitmap);


                } catch (FileNotFoundException e) {
                    Log.d("imgt", e.getMessage(), e);
                }

            } else {
                Log.d("imgt", "uri is null!");
            }


        }
    }
}
