package com.example.myapplication.data;

import java.util.List;

public interface ListCallback<T> {
    void onSuccess(List<T> results);

    void onError(String message);
}
