package edu.ewubd.assignment01_abir013;

import android.widget.EditText;

public class Event {
    String key = "";
    String name = "";
    String email = "";
    String phoneHome = "";
    String phoneOffice = "";
    String base64Image = "";



    public Event(String key, String name, String email, String phoneHome, String phoneOffice, String base64Image){
        this.key = key;
        this.name = name;
        this.email = email;
        this.phoneHome = phoneHome;
        this.phoneOffice = phoneOffice;
        this.base64Image = base64Image;
    }
}
