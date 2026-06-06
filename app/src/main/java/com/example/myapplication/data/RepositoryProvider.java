package com.example.myapplication.data;

import com.example.myapplication.data.firebase.FirebaseAuthRepository;
import com.example.myapplication.data.firebase.FirebaseCheerRepository;
import com.example.myapplication.data.firebase.FirebaseGroupRepository;
import com.example.myapplication.data.firebase.FirebaseInningRepository;
import com.example.myapplication.data.firebase.FirebasePostRepository;
import com.example.myapplication.data.firebase.FirebaseSongRepository;
import com.example.myapplication.data.firebase.FirebaseUserRepository;

public final class RepositoryProvider {
    private RepositoryProvider() {
    }

    public static AuthRepository auth() {
        return new FirebaseAuthRepository();
    }

    public static UserRepository users() {
        return new FirebaseUserRepository();
    }

    public static PostRepository posts() {
        return new FirebasePostRepository();
    }

    public static CheerRepository cheers() {
        return new FirebaseCheerRepository();
    }

    public static SongRepository songs() {
        return new FirebaseSongRepository();
    }

    public static GroupRepository groups() {
        return new FirebaseGroupRepository();
    }

    public static InningRepository innings() {
        return new FirebaseInningRepository();
    }
}
