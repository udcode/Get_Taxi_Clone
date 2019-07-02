package com.avi_ud.gettaxi1.model.backend;

public interface IAction<T> {
    void onSuccess(T obj);
    void onFailure(Exception exception);
}
