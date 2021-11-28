package com.veterinaria.camarastorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class HomeActivity extends AppCompatActivity {

    TextView txvProveedor, txvEmail;
    Button btnSalir, btnGaleria,btnAbrirPicture;

    FirebaseAuth auth= FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref= storage.getReference();


    private static  final int GALLERY_INTENT=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txvProveedor=findViewById(R.id.txvProveedor);
        txvEmail=findViewById(R.id.txvEmail);
        btnSalir=findViewById(R.id.btnSalir);
        btnGaleria=findViewById(R.id.btnGaleria);
        btnAbrirPicture=findViewById(R.id.btnAbrirPicture);

        FirebaseUser user=auth.getCurrentUser();

        txvProveedor.setText(user.getProviderId());
        txvEmail.setText(user.getEmail());


        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();

                Intent inte= new Intent(HomeActivity.this,MainActivity.class);
                startActivity(inte);
                finish();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imagen= new Intent(Intent.ACTION_PICK);
                imagen.setType("image/*");
                startActivityForResult(imagen,GALLERY_INTENT);
            }
        });

        btnAbrirPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,PictureActivity.class));

            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){

            Uri uri=data.getData();
            StorageReference filePath= ref.child("fotos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(HomeActivity.this, "Foto  cargada", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}