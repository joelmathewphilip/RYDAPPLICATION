package com.example.rydapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class block_post extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<DataClassForBlog> dataClassForBlog=new ArrayList<>();
    AdapterClassForBlog adapterClassForBlog;
    String name,photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_post);

        getSupportActionBar().setTitle("My Blogs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference=FirebaseDatabase.getInstance().getReference();


        get_data_firebase();


        Toast.makeText(this,getIntent().getStringExtra("ID"),Toast.LENGTH_SHORT).show();

        recyclerView=findViewById(R.id.recycler_view_blog_post);
        adapterClassForBlog=new AdapterClassForBlog(this,dataClassForBlog);
        recyclerView.setAdapter(adapterClassForBlog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blogs_add,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_blog:
                Intent intent = new Intent(block_post.this, Blog_Add.class);
                intent.putExtra("ID",getIntent().getStringExtra("ID"));
                intent.putExtra("Name",name);
                intent.putExtra("Photo",photo);
                startActivity(intent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

        public void get_data_firebase()
        {
            dataClassForBlog.clear();
            databaseReference=FirebaseDatabase.getInstance().getReference().child(getIntent().getStringExtra("ID"));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataClassForBlog.clear();
                    String name=dataSnapshot.child("Name").getValue().toString();
                    String photo=dataSnapshot.child("Photo").getValue().toString();
                    for(DataSnapshot item:dataSnapshot.child("Blogs").getChildren())
                    {
                        String heading=item.child("heading").getValue().toString();
                        String blog_id=item.getKey().toString();
                        String brief_content=item.child("brief_content").getValue().toString();
                        String type=item.child("type").getValue().toString();
                        String date=item.child("date").getValue().toString();
                        dataClassForBlog.add(new DataClassForBlog(type,heading,brief_content,date,photo,name,getIntent().getStringExtra("ID"),blog_id));
                        //Toast.makeText(getApplicationContext(),type+" "+heading+" "+brief_content+" "+date+" "+photo+" "+name+" "+getIntent().getStringExtra("ID"),Toast.LENGTH_LONG).show();
                    }
                    adapterClassForBlog.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    @Override
    protected void onResume() {
        super.onResume();
        get_data_firebase();
    }
}


