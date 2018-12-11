package com.example.s2784.layout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
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
    private Context context;
    private TextView textView;
    private ImageView imageView;
    int imgsize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_image_test);

        context = this.getApplicationContext();
        textView = findViewById(R.id.tv_img_size);
        imageView = findViewById(R.id.imgv);

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
                new SendingImg().execute(uri);
            } else {
                Log.d("imgt", "uri is null!");
            }


        }
    }

    private class SendingImg extends AsyncTask<Uri, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar_img).setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Uri...params) {
            Uri uri = params[0];
            if(uri != null) {
                ContentResolver cr = context.getContentResolver();
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
                    imgsize = baos.toByteArray().length/1024;
                    Bitmap cmpBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
                    return cmpBitmap;
                } catch (FileNotFoundException e) {
                    Log.d("imgt", e.getMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            super.onPostExecute(img);
            String size_tv = "Size : " + imgsize + "KB";
            textView.setText(size_tv);
            textView.setTextSize(40);

            imageView.setImageBitmap(img);

            findViewById(R.id.progressBar_img).setVisibility(View.GONE);
        }
    }
}
