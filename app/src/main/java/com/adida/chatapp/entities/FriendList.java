package com.adida.chatapp.entities;

import java.util.ArrayList;

public class FriendList {
    public String uiid;
    public ArrayList<String> listFirendUiid = new ArrayList<String>();

    public FriendList(){
        uiid = "";
        listFirendUiid.clear();
    }

    public FriendList(String uiid, ArrayList<String> listFirendUiid) {
        this.uiid = uiid;
        this.listFirendUiid = listFirendUiid;
    }

    public void addNewContact(String friendUiid) {
        this.listFirendUiid.add(friendUiid);
    }
}
