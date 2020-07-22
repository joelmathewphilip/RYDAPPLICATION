package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class Blog_Add extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText heading,type_t,content,brief_content;
    ImageView imageView;
    Button submit;
    String user_name;
    Uri uri,result_uri=null;
    public int GALLERY_PICK=1;
    private byte[] thumb_bytes=null;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog__add);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("rydapplication");
        user_name=getIntent().getStringExtra("ID");
        Toast.makeText(this,user_name,Toast.LENGTH_SHORT).show();

        heading=findViewById(R.id.content_type_heading);
        type_t=findViewById(R.id.content_type_add);
        content=findViewById(R.id.content_type_full_content);
        brief_content=findViewById(R.id.content_type_brief_content);
        imageView=findViewById(R.id.image_for_blog_post);
        submit=findViewById(R.id.submit_button);

        getSupportActionBar().setTitle("Add A Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check_if_empty()==1)
                {
                    compress_image(result_uri);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_image();
            }
        });


    }

    public int check_if_empty()
    {
        if(heading.getText().toString().trim().length()==0)
        {
            heading.setError("Required!!");
            heading.requestFocus();
            return 0;
        }

        if(content.getText().toString().trim().length()==0)
        {
            content.setError("Required!!");
            content.requestFocus();
            return 0;
        }

        if(brief_content.getText().toString().trim().length()==0)
        {
            brief_content.setError("Required!!");
            brief_content.requestFocus();
            return 0;
        }
        if(type_t.getText().toString().trim().length()==0)
        {
            type_t.setError("Required!!");
            type_t.requestFocus();
            return 0;
        }
        if(result_uri==null)
        {
            imageView.requestFocus();
            return 0;
        }
        return 1;


    }






    public void pick_image()
    {
        uri=null;
        Intent galleryintent=new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryintent,"Select Image"),GALLERY_PICK);
    }


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
                    .start(Blog_Add.this);
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

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        final String formattedDate = df.format(c);

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
        long time=System.currentTimeMillis();
        databaseReference= FirebaseDatabase.getInstance().getReference().child(user_name).child("Blogs").child(String.valueOf(time));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference thumb_file_path=storageReference.child(user_name).child("Blogs").child(time+".jpg");
        UploadTask uploadTask=thumb_file_path.putBytes(thumb_bytes);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                thumb_file_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String link=uri.toString();
                        databaseReference.child("heading").setValue(heading.getText().toString());
                        databaseReference.child("type").setValue(type_t.getText().toString());
                        databaseReference.child("date").setValue(formattedDate);
                        databaseReference.child("content").setValue(content.getText().toString());
                        databaseReference.child("brief_content").setValue(brief_content.getText().toString());
                        databaseReference.child("image").setValue(link);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}