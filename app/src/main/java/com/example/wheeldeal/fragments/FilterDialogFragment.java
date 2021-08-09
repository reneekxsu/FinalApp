package com.example.wheeldeal.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.wheeldeal.R;
import com.example.wheeldeal.ParseApplication;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterDialogFragment extends DialogFragment{
    public static final String TAG = "FilterDialog";
    private Toolbar toolbar;
    String makes[];
    AppCompatAutoCompleteTextView acMake, acModel;
    HashMap<String, String> hmModelMake;
    HashMap<String, ArrayList> hmMakeModels;

    public static FilterDialogFragment display(FragmentManager fragmentManager){
        FilterDialogFragment filterDialog = new FilterDialogFragment();
        filterDialog.show(fragmentManager, TAG);
        return filterDialog;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.filter_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        Resources res = getResources();
        makes = res.getStringArray(R.array.makes_array);

        hmModelMake = ((ParseApplication) getActivity().getApplication()).getHashMapModelMake();

        hmMakeModels = ((ParseApplication) getActivity().getApplication()).getHashMapMakeModel();


        ArrayList<String> allModels = new ArrayList<String>(((ParseApplication) getActivity().getApplication()).getModels());
        ArrayList<String> allModelsExtra = new ArrayList<String>(((ParseApplication) getActivity().getApplication()).getModels());

        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_singlechoice,
                allModels);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_singlechoice, makes);
        //Find TextView control
        acMake = (AppCompatAutoCompleteTextView) view.findViewById(R.id.etCarMake);
        //Set the number of characters the user must type before the drop down list is shown
        acMake.setThreshold(1);
        //Set the adapter
        acMake.setAdapter(adapter);

        final String[] myMake = new String[1];
        acMake.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myMake[0] = adapter.getItem(position).toString();
                ArrayList<String> models = hmMakeModels.get(myMake[0]);
                modelAdapter.clear();
                modelAdapter.addAll(models);
                modelAdapter.notifyDataSetChanged();
            }
        });

        acMake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myMake[0] = null;
                if (s.toString().length() == 0){
                    modelAdapter.clear();
                    modelAdapter.addAll(allModelsExtra);
                    modelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Log.i(TAG, "adapter set");
        //Find TextView control
        acModel = (AppCompatAutoCompleteTextView) view.findViewById(R.id.etCarculatorModel);
        //Set the number of characters the user must type before the drop down list is shown
        acModel.setThreshold(1);
        //Set the adapter
        acModel.setAdapter(modelAdapter);

        acModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedModel = modelAdapter.getItem(position).toString();
                String make = hmModelMake.get(selectedModel);
                acMake.setText(make);
                Log.i(TAG, "matching make is: " + make);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Filter");
        toolbar.inflateMenu(R.menu.filter_dialog);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent();
            intent.putExtra("Make", acMake.getText().toString());
            intent.putExtra("Model", acModel.getText().toString());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
