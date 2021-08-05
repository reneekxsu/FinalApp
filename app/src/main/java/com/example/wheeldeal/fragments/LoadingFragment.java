package com.example.wheeldeal.fragments;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.CarculatorActivity;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class LoadingFragment extends Fragment {

    private TextView tvYourName;
    private TextView tvEstimate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvYourName = view.findViewById(R.id.tvYourName);
        tvYourName.setText(ParseUser.getCurrentUser().getUsername() + "!");
        tvEstimate = view.findViewById(R.id.tvToCarculator);
        ImageView exchangeDealLogoImageView = (ImageView) getActivity().findViewById(R.id.logo);
        Drawable drawable = exchangeDealLogoImageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        tvEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), CarculatorActivity.class);
                intent.putExtra("make", "");
                intent.putExtra("model", "");
                intent.putExtra("year", "");
                intent.putExtra("passengers", "");
                intent.putExtra("carFlag", false);
                startActivity(intent);
            }
        });
    }
}