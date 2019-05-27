package com.example.s2784.layout;

import com.example.s2784.layout.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenImage  extends Activity {


    @SuppressLint({"NewApi", "WrongViewCast"})


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_full);

        //Change status color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.ncku_red));
        }
        //Change status color

        //Bundle extras = getIntent().getExtras();
        //Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");

        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        Bitmap bmp=null;
        if(bytes!=null)
        {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
//        else
//        {
//            bmp = BitmapFactory.decodeByteArray(bytes, 0, 0);
//        }

        final ImageView imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        imgDisplay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    imgDisplay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    imgDisplay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                Drawable drawable = imgDisplay.getDrawable();
                Rect rectDrawable = drawable.getBounds();
                float leftOffset = (imgDisplay.getMeasuredWidth() - rectDrawable.width()) / 2f;
                float topOffset = (imgDisplay.getMeasuredHeight() - rectDrawable.height()) / 2f;

                Matrix matrix = imgDisplay.getImageMatrix();
                matrix.postTranslate(leftOffset, topOffset);
                imgDisplay.setImageMatrix(matrix);
                imgDisplay.invalidate();
            }
        });

        imgDisplay.setOnTouchListener(new MulitPointTouchListener ());

        imgDisplay.setImageBitmap(bmp);

    }
}
