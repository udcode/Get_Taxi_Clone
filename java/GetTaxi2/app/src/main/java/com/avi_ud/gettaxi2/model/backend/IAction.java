package com.avi_ud.gettaxi2.model.backend;

public interface IAction<T> {
    void onSuccess(T obj);
    void onFailure(Exception exception);
    void onPreExecute();
    void onPostExecute();
}
