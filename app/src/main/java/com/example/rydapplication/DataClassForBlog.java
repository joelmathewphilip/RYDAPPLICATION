package com.example.rydapplication;

public class DataClassForBlog {
String type,heading,content,date,profile_photo_url,profile_name,id,blog_id;

DataClassForBlog(String type,String heading,String content,String date,String profile_photo_url,String profile_name,String id,String blog_id)
{
    this.type=type;
    this.heading=heading;
    this.content=content;
    this.date=date;
    this.profile_photo_url=profile_photo_url;
    this.profile_name=profile_name;
    this.id=id;
    this.blog_id=blog_id;
}

    public String getType() {
        return type;
    }

    public String getHeading() {
        return heading;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public String getId() {
        return id;
    }

    public String getBlog_id() {
        return blog_id;
    }
}
