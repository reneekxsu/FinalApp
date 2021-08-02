package com.example.wheeldeal.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Car_Model_List")
public class CarModelList extends ParseObject {
    public static final String KEY_MODEL = "Model";
    public static final String KEY_MAKE = "Make";
    public static final String KEY_CATEGORY = "Category";

    public String getModel(){
        return getString(KEY_MODEL);
    }

    public String getMake(){
        return getString(KEY_MAKE);
    }

    public String getCategory(){
        return getString(KEY_CATEGORY);
    }

}
