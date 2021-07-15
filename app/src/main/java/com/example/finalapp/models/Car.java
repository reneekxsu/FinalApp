package com.example.finalapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Car")
public class Car extends ParseObject {
    // keys: description, author, image, rate

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_OWNER = "author";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATE = "rate";
    public static final String KEY_MODEL = "model";

    // description getter and setter
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    // car owner getter and setter
    public ParseUser getOwner(){
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser owner){
        put(KEY_OWNER,owner);
    }

    // image getter and setter
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE,image);
    }

    // rate getter and setter
    public String getRate(){
        return getString(KEY_RATE);
    }

    public void setRate(String rate){
        put(KEY_RATE,rate);
    }

    // model getter and setter
    public String getModel() {
        return getString(KEY_MODEL);
    }

    public void setModel(String model) {
        put(KEY_MODEL, model);
    }
}
