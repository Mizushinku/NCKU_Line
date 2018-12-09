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
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
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
                ContentResolver cr = this.getContentResolver();
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(cr.openInputStream(uri), null,options);
                    int reqWidth = 688, reqHeight = 387;
                    int inSampleSize = 1;
                    if(options.outWidth > reqWidth || options.outHeight > reqHeight) {
                        final int heightRatio = Math.round((float)options.outHeight / (float)reqHeight);
                        final int widthRatio = Math.round((float)options.outWidth / (float)reqWidth);
                        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                    }
                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false;

                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    TextView textView = findViewById(R.id.tv_img_size);
                    String size_tv = "Size : " + (baos.toByteArray().length/1024) + "KB";
                    textView.setText(size_tv);
                    textView.setTextSize(40);
                    Bitmap cmpBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                    ImageView imageView = findViewById(R.id.imgv);
                    imageView.setImageBitmap(cmpBitmap);
                } catch (FileNotFoundException e) {
                    Log.d("imgt", e.getMessage(), e);
                }
            } else {
                Log.d("imgt", "uri is null!");
            }


        }
    }
}
