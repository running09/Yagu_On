package com.example.myapplication.data.firebase;

import android.net.Uri;

import com.example.myapplication.data.AppCallback;
import com.example.myapplication.data.ListCallback;
import com.example.myapplication.data.PostRepository;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebasePostRepository implements PostRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseUserRepository userRepository = new FirebaseUserRepository();

    @Override
    public void getPostsByTeam(String teamId, ListCallback<Post> callback) {
        db.collection("posts")
                .whereEqualTo("teamId", teamId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Post> posts = new ArrayList<>();
                    snapshot.forEach(document -> {
                        Post post = document.toObject(Post.class);
                        post.id = document.getId();
                        posts.add(post);
                    });
                    callback.onSuccess(posts);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    @Override
    public void createPost(String teamId, String caption, Uri mediaUri, String mediaType, AppCallback<Post> callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("로그인이 필요합니다.");
            return;
        }

        userRepository.getCurrentUser(new AppCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                uploadAndSavePost(user.getUid(), profile.nickname, teamId, caption, mediaUri, mediaType, callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private void uploadAndSavePost(String uid, String nickname, String teamId, String caption, Uri mediaUri,
                                   String mediaType, AppCallback<Post> callback) {
        String extension = "video".equals(mediaType) ? ".mp4" : ".jpg";
        String path = "posts/" + uid + "/" + System.currentTimeMillis() + extension;
        StorageReference reference = storage.getReference().child(path);

        reference.putFile(mediaUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> savePost(uid, nickname, teamId, caption, mediaType, uri.toString(), callback))
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    private void savePost(String uid, String nickname, String teamId, String caption, String mediaType,
                          String mediaUrl, AppCallback<Post> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("authorUid", uid);
        data.put("authorNickname", nickname);
        data.put("teamId", teamId);
        data.put("caption", caption);
        data.put("mediaType", mediaType);
        data.put("mediaUrl", mediaUrl);
        data.put("thumbnailUrl", "");
        data.put("likeCount", 0);
        data.put("commentCount", 0);
        data.put("createdAt", FieldValue.serverTimestamp());

        db.collection("posts").add(data)
                .addOnSuccessListener(document -> {
                    Post post = new Post();
                    post.id = document.getId();
                    post.authorUid = uid;
                    post.authorNickname = nickname;
                    post.teamId = teamId;
                    post.caption = caption;
                    post.mediaType = mediaType;
                    post.mediaUrl = mediaUrl;
                    callback.onSuccess(post);
                })
                .addOnFailureListener(e -> callback.onError(message(e)));
    }

    private String message(Exception e) {
        return e.getMessage() == null ? "게시글 작업에 실패했습니다." : e.getMessage();
    }
}
