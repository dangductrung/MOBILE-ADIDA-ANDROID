package com.adida.chatapp.entities;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String phone;
    public String email;
    public String countChatMessage;
    public String countCreateConnection;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
        name = "";
        phone = "";
        email = "";
        countChatMessage = "0";
        countCreateConnection = "0";
    }

    public User(String name, String phone, String email, String countChatMessage, String countCreateConnection) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.countChatMessage = countChatMessage;
        this.countCreateConnection = countCreateConnection;
    }
}