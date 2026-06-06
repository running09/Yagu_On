package com.example.myapplication.data;

public interface AppCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}
