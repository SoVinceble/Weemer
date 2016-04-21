package com.weemer.weemer.weemer;

import com.firebase.client.Firebase;

/**
 * Created by Latiinna on 16/04/07.
 */
public class WeemerApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
