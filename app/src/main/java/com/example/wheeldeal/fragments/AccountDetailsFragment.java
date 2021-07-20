package com.example.wheeldeal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wheeldeal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class AccountDetailsFragment extends Fragment {

    ParseUser currentUser;
    String email;
    String username;
    String address;
    TextInputEditText etName, etEmail, etAddress;
    Button btnUpdate;
    public static final String TAG = "AccountDetailsFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
        etName = view.findViewById(R.id.etProfileName);
        etEmail = view.findViewById(R.id.etProfileEmail);
        etAddress = view.findViewById(R.id.etProfileAddress);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        fetchUserDetails();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });
    }

    private void saveUserDetails() {
        // Specify which class to query
        email = etEmail.getText().toString();
        address = etAddress.getText().toString();
        currentUser.put("email", email);
        currentUser.put("address", address);
        currentUser.saveInBackground();
        Toast.makeText(getContext(), "Edits to user were saved", Toast.LENGTH_SHORT).show();
    }

    public void fetchUserDetails(){
        // Specify which class to query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // Specify the object id
        query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user");
                } else {
                    email = currentUser.getEmail();
                    Log.i(TAG, "user id: " + currentUser.getObjectId());
                    Log.i(TAG, "user email: " + currentUser.getEmail());
                    username = currentUser.getUsername();
                    address = currentUser.getString("address");
                    etName.setText(username);
                    etEmail.setText(email);
                    etAddress.setText(address);
                }
            }
        });
    }

}