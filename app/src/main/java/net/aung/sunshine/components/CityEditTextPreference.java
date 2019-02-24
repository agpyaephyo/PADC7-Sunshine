package net.aung.sunshine.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import net.aung.sunshine.R;
import net.aung.sunshine.activities.SettingsActivity;

/**
 * Created by aung on 2/21/16.
 */
public class CityEditTextPreference extends EditTextPreference {

    private static final int DEFAULT_MINIMUM_CITY_LENGTH = 3;

    private int mMinLength;

    public CityEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CityEditTextPreference, 0, 0);

        try {
            mMinLength = array.getInteger(R.styleable.CityEditTextPreference_minLength, DEFAULT_MINIMUM_CITY_LENGTH);
        } finally {
            array.recycle();
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            setWidgetLayoutResource(R.layout.pref_current_location);
        }

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        View currentLocation = view.findViewById(R.id.current_location);
        if (currentLocation != null) {
            currentLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "Woo!", Toast.LENGTH_LONG).show();
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Activity settingActivity = (Activity) getContext();
                    try {
                        settingActivity.startActivityForResult(
                                builder.build(settingActivity), SettingsActivity.PLACE_PICKER_REQUEST_CODE);
                    } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {

                    }
                }
            });
        }

        return view;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText et = getEditText();
        et.setSelection(et.getText().toString().length());
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog dialog = getDialog();
                if (dialog instanceof AlertDialog) {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(!(s.length() < mMinLength));
                }
            }
        });
    }
}
