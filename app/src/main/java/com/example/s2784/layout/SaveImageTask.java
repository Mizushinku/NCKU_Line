package com.example.s2784.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SaveImageTask extends AsyncTask<Bitmap, Void, File> {
    private final Context mcontext;
    
    public SaveImageTask(Context context){
        this.mcontext = context;
    }

    @Override
    protected File doInBackground(Bitmap... bitmaps) {
        Bitmap image = bitmaps[0];
        return saveBitmapToFile(mcontext,image);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        onSaveComplete(file);
    }

    public static File saveBitmapToFile(Context ctx, Bitmap bitmap){
        File pictureFile = getOutputMediaFile(ctx);
        if(pictureFile == null){
            Log.d("IMG","Error creating media file, please check permission");
            return null;
        }

        try{
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.close();
        }catch (FileNotFoundException e){
            Log.d("IMG","File not found");
        }catch (IOException e){
            Log.d("IMG","Error accessing file");
        }
        return pictureFile;
    }


    public static File getOutputMediaFile(Context context){

        String state = Environment.getExternalStorageState();
        if (! Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("IMG","getOutputMediaFile: Environment storage not writable");
            return null;
        }

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory
                                (Environment.DIRECTORY_PICTURES), "NCKU_Line");


        // Create the storage directory if it does not exist

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        String mImageName = timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void onSaveComplete(File filePath) {
        if(filePath == null){
            Log.d("IMG", "onSaveComplete: file dir error");
            return;
        }

        Toast saveCompleteMsg =
                Toast.makeText(mcontext, "COMPLETE", Toast.LENGTH_SHORT);
        saveCompleteMsg.show();

        Log.d("IMG", "onSaveComplete: save image in "+filePath);

        MediaScannerConnection.scanFile(
                mcontext,
                new String[]{filePath.getAbsolutePath()},
                null,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        Log.d("IMG", "onMediaScannerConnected");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("IMG", "onScanCompleted");
                    }
                }
        );

    }
}
