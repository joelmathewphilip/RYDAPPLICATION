package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class blog_individual extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView type_t,heading_t,name_t,date_t,content_t;
    ImageView profile_photo,imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_individual);

        getSupportActionBar().setTitle("Blog Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type_t=findViewById(R.id.ind_type);
        heading_t=findViewById(R.id.ind_heading);
        name_t=findViewById(R.id.profile_name_individual);
        date_t=findViewById(R.id.blog_date_individual);
        content_t=findViewById(R.id.blog_content_individual);

        imageView=findViewById(R.id.blog_photo_individual);
        profile_photo=findViewById(R.id.profile_photo_individual);


        get_data();
    }


    public void get_data()
    {


        databaseReference= FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("ID"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("Name").getValue().toString();
                String photo=dataSnapshot.child("Photo").getValue().toString();
                DataSnapshot dataSnapshot1=dataSnapshot.child("Blogs").child(getIntent().getStringExtra("Blog_id"));


                String type=dataSnapshot1.child("type").getValue().toString();
                String heading=dataSnapshot1.child("heading").getValue().toString();

                String content=dataSnapshot1.child("content").getValue().toString();
                String date=dataSnapshot1.child("date").getValue().toString();

                String image=dataSnapshot1.child("image").getValue().toString();

                type_t.setText(type);
                heading_t.setText(heading);
                name_t.setText(name);
                content_t.setText(content);
                date_t.setText(date);
                Picasso.get().load(photo).into(profile_photo);
                Picasso.get().load(image).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Data load Error",Toast.LENGTH_SHORT).show();
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