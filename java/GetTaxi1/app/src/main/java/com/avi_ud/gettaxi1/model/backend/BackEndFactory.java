package com.avi_ud.gettaxi1.model.backend;

import com.avi_ud.gettaxi1.model.datasource.FirebaseDBManager;

public class BackEndFactory {
    public static BackEnd backEnd = new FirebaseDBManager();
}
