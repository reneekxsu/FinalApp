package com.example.wheeldeal.utils;

public class FormClient {
    public FormClient(){}
    public boolean isEntryEmpty(String name, String make, String model, String year, String price, String passengerCount,
                         String sizeType, String description, String address){
        return (name.isEmpty() || make.isEmpty() || model.isEmpty() || year.isEmpty() || price.isEmpty() || passengerCount.isEmpty()
                || sizeType.isEmpty() || description.isEmpty() || address.isEmpty());
    }
}
