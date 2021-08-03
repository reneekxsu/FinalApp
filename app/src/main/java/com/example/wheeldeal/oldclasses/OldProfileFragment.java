package com.example.wheeldeal.oldclasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.LoginActivity;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class OldProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    Button btnLogout;
    Button btnViewCars;
    TextView tvProfileUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.old_fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnViewCars = view.findViewById(R.id.btnViewCars);
        tvProfileUser = view.findViewById(R.id.tvProfileUser);
        tvProfileUser.setText(ParseUser.getCurrentUser().getUsername());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked logout button");
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent i = new Intent(getView().getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        btnViewCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked car view button");
                Intent i = new Intent(getView().getContext(), UserCarFeedActivity.class);
                startActivity(i);
//                getActivity().startActivityForResult(i, 111);
            }
        });
    }

}