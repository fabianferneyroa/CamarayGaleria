package com.veterinaria.camarastorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PictureActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int TAKE_PICTURE = 101;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 200;

    private ImageView ivFoto;
    private Button btnFoto, btnGuardar;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        ivFoto = findViewById(R.id.ivFoto);
        btnFoto = findViewById(R.id.btnFoto);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verificarPermisoCamara();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verificarPermisosAlmacenamiento();

            }
        });

    }


    private void verificarPermisoCamara(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){

            tomarFoto();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
        }

        }else{

            tomarFoto();

        }

    }

    private void tomarFoto(){

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null){

            startActivityForResult(intent,TAKE_PICTURE);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_PICTURE){

            if(resultCode== Activity.RESULT_OK && data!=null){

                bitmap=(Bitmap) data.getExtras().get("data");
                ivFoto.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSION_CAMERA){

            if(permissions.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 tomarFoto();

            }else if(requestCode==REQUEST_PERMISSION_WRITE_STORAGE){

                if (permissions.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                    guardarFoto();

                }

            }

        }
    }


    public void guardarFoto(){

        OutputStream outputStream=null;
        File file=null;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            ContentValues values = new ContentValues();
            ContentResolver resolver= getContentResolver();
            String filename=System.currentTimeMillis()+"image";

            values.put(MediaStore.Images.Media.DISPLAY_NAME,filename);
            values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/AppStorage");
            values.put(MediaStore.Images.Media.IS_PENDING,1);

            Uri collection= MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri= resolver.insert(collection,values);

            try {
                outputStream= resolver.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING,0);
            resolver.update(imageUri,values,null,null);

        }
        else{

            String imageDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            String fileName=System.currentTimeMillis()+"jpeg";
            file = new File(imageDir,fileName);

            try {
                outputStream= new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

        if(saved){
            Toast.makeText(PictureActivity.this, "Imagen Guardada", Toast.LENGTH_SHORT).show();

        }
        if (outputStream!=null){

            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(file!=null){

            MediaScannerConnection.scanFile(this,new String[]{file.toString()},null,null);
        }

    }


    public void verificarPermisosAlmacenamiento(){


            if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.M){

                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                    guardarFoto();

                }else{

                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_WRITE_STORAGE);
                }

            }
            else{
                guardarFoto();

            }




    }


}