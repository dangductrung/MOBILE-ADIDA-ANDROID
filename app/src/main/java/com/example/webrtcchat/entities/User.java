package com.example.webrtcchat.entities;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String phone;
    public String email;
    public int countChatMessage;
    public int countCreateConnection;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
        name = "";
        phone = "";
        email = "";
        countChatMessage = 0;
        countCreateConnection = 0;
    }

    public User(String name, String phone, String email, int countChatMessage, int countCreateConnection) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.countChatMessage = countChatMessage;
        this.countCreateConnection = countCreateConnection;
    }
}