package com.adida.chatapp.firebase_manager;

import android.content.Context;

import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    private static FirebaseManager instance;
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    private FirebaseManager(){}

    public void createNewUser(String email, Context context) {
        User user = new User();
        user.email = email;
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(context).getUuid()).setValue(user);
    }

    public void updateUser(User user, Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(context).getUuid()).setValue(user);
    }


    public void setState(boolean state, Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.state).child(SharePref.getInstance(context).getUuid()).setValue(state);
    }
}
