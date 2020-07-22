package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import id.zelory.compressor.Compressor;

public class SetAccountDetails extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    DatabaseReference databaseReference;
    private String type;
    Button submit;
    Uri uri,result_uri;
    String user_name;
    public int GALLERY_PICK=1;
    private byte[] thumb_bytes=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account_details);
        imageView=findViewById(R.id.person_icon);
        editText=findViewById(R.id.user_name_blog);
        submit=findViewById(R.id.submit_account_details);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_image();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().trim().length()==0)
                {
                    editText.setError("Required");
                    editText.requestFocus();
                    return;
                }

                if(result_uri==null)
                {
                    Toast.makeText(getApplicationContext(),"Upload A Suitable Image",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editText.getText().toString().trim().length()!=0 && result_uri!=null)
                {
                    compress_image(result_uri);
                }
            }
        });

        getSupportActionBar().setTitle("Account Details");
        user_name=getIntent().getStringExtra("ID");
        GoogleSignInAccount signInAccount= GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount!=null)
        {
            editText.setText(signInAccount.getDisplayName());
        }
        if(getIntent().getStringExtra("Type").equals("Mobile"))
        {

            editText.setText(user_name);
            Toast.makeText(getApplicationContext(),"Select Profile Image and Name",Toast.LENGTH_SHORT).show();
        }




        //Picasso.get().load(image).into(imageView);

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"You Cannot Go Back",Toast.LENGTH_SHORT).show();
    }

    public void pick_image()
    {
        uri=null;
        Intent galleryintent=new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryintent,"Select Image"),GALLERY_PICK);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK)
        {
            Toast.makeText(getApplicationContext(),"Select The Crop Window",Toast.LENGTH_SHORT).show();
            uri=data.getData();
            type="image";
            CropImage.activity(uri)
                    .setMinCropWindowSize(320,320)
                    .setMinCropResultSize(320,320)
                    .setCropMenuCropButtonTitle("Crop")
                    .start(SetAccountDetails.this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            result_uri=result.getUri();
            imageView.setImageURI(result_uri);

        }

    }

    public void compress_image(Uri image)
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Compressing And Uploading Image");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        File file_path=new File(image.getPath());
        try
        {
            Bitmap bitmap=new Compressor(this).setMaxHeight(500)
                    .setMaxWidth(220)
                    .setQuality(75)
                    .compressToBitmap(file_path);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,65,baos);
            thumb_bytes=baos.toByteArray();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child(user_name).child("Name").setValue(editText.getText().toString().trim());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference thumb_file_path=storageReference.child(user_name).child(user_name+".jpg");
        UploadTask uploadTask=thumb_file_path.putBytes(thumb_bytes);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                thumb_file_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String link=uri.toString();
                        databaseReference.child(user_name).child("Photo").setValue(link);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), block_post.class);
                        intent.putExtra("ID",user_name);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


    }

}