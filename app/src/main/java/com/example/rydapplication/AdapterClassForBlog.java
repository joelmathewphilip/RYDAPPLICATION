package com.example.rydapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterClassForBlog extends RecyclerView.Adapter<AdapterClassForBlog.MyViewHolder> {

    ArrayList<DataClassForBlog> apps;
    Context mContext;

    AdapterClassForBlog(Context context, ArrayList<DataClassForBlog> obj)
    {
        mContext=context;
        apps=obj;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.blog_template,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.date.setText(apps.get(position).getDate());
        holder.heading.setText(apps.get(position).getHeading());
        holder.type.setText(apps.get(position).getType());
        holder.brief_content.setText(apps.get(position).getContent());
        holder.name.setText(apps.get(position).getProfile_name());
        Picasso.get().load(apps.get(position).getProfile_photo_url()).into(holder.profile_photo);
        //Picasso.get().load(R.drawable.face_photo).into(holder.profile_photo);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(mContext,blog_individual.class);
                intent1.putExtra("ID",apps.get(position).getId());
                intent1.putExtra("Blog_id",apps.get(position).getBlog_id());
                mContext.startActivity(intent1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type,heading,brief_content,name,date,more;
        ImageView profile_photo;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            type=itemView.findViewById(R.id.type);
            more=itemView.findViewById(R.id.read_more);
            heading=itemView.findViewById(R.id.heading);
            brief_content=itemView.findViewById(R.id.brief_content);
            name=itemView.findViewById(R.id.profile_name);
            date=itemView.findViewById(R.id.blog_date);
            profile_photo=itemView.findViewById(R.id.profile_photo);
        }
    }
}
