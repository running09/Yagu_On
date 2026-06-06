package com.example.myapplication.data;

import android.net.Uri;

import com.example.myapplication.model.Post;

public interface PostRepository {
    void getPostsByTeam(String teamId, ListCallback<Post> callback);

    void createPost(String teamId, String caption, Uri mediaUri, String mediaType, AppCallback<Post> callback);
}
