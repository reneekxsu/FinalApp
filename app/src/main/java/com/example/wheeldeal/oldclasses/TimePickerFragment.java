package com.example.wheeldeal.oldclasses;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    // This is the listener for the activity
    private TimePickerFragment.TimePickerFragmentListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        listener.onTimeSet(hourOfDay, minute);
    }

    // This is the method from the TimePicker fragment to implement in the Main Activity
    public interface TimePickerFragmentListener{
        public void onTimeSet(int hour, int minute);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TimePickerFragment.TimePickerFragmentListener) context;
    }
}
