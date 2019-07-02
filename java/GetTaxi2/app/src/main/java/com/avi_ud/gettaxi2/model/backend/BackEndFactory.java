package com.avi_ud.gettaxi2.model.backend;

import com.avi_ud.gettaxi2.model.datasource.FirebaseDBManager;

public class BackEndFactory {
    public static BackEnd backEnd = new FirebaseDBManager();

}
