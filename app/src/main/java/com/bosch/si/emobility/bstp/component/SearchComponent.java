package com.bosch.si.emobility.bstp.component;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.model.SearchCriteria;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class SearchComponent extends Component implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, GoogleApiClient.OnConnectionFailedListener {

    private static SearchComponent ourInstance = new SearchComponent();

    public static SearchComponent getInstance(MapsActivity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private SearchComponent() {
        super();
    }

    static AutoCompleteTextView editTextSearch;
    static ImageButton imageButtonClear;

    TextView textViewFrom;
    static TextView textViewFromDate;
    static TextView textViewFromTime;

    TextView textViewTo;
    static TextView textViewToDate;
    static TextView textViewToTime;

    static TextView currentTextView;

    static Date fromDate;
    static Date toDate;

    static SearchCriteria searchCriteria = new SearchCriteria();

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public AutoCompleteTextView getEditTextSearch() {
        return editTextSearch;
    }

    public ImageButton getImageButtonClear() {
        return imageButtonClear;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    @Override
    public void setActivity(MapsActivity activity) {
        super.setActivity(activity);

        layout = (RelativeLayout) this.activity.findViewById(R.id.searchLayout);

        editTextSearch = (AutoCompleteTextView) this.activity.findViewById(R.id.editTextSearch);
        imageButtonClear = (ImageButton) this.activity.findViewById(R.id.imageButtonClear);

        textViewFrom = (TextView) this.activity.findViewById(R.id.textViewFrom);
        textViewFromDate = (TextView) this.activity.findViewById(R.id.textViewFromDate);
        textViewFromTime = (TextView) this.activity.findViewById(R.id.textViewFromTime);

        textViewFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(textViewFromDate);
            }
        });

        textViewFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(textViewFromTime);
            }
        });

        textViewTo = (TextView) this.activity.findViewById(R.id.textViewTo);
        textViewToDate = (TextView) this.activity.findViewById(R.id.textViewToDate);
        textViewToTime = (TextView) this.activity.findViewById(R.id.textViewToTime);

        textViewToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(textViewToDate);
            }
        });

        textViewToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(textViewToTime);
            }
        });

        Calendar calendar = Calendar.getInstance();
        fromDate = calendar.getTime();
        toDate = new Date(fromDate.getTime() + TimeUnit.SECONDS.toMillis(1));

        setSearchAutoComplete();
    }

    private void openTimePicker(TextView textView) {
        currentTextView = textView;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.activity.getSupportFragmentManager(), "timePicker");
    }

    private void openDatePicker(TextView textView) {
        currentTextView = textView;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.activity.getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        super.setEnabled(enabled, noAnimation);
        hideKeyboard();
        updateComponent();
    }

    @Override
    protected boolean isSlideUp() {
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
    }

    private void validateDates() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        if (fromDate.getTime() < date.getTime()) {
            fromDate = date;
        }
        if (toDate.getTime() < fromDate.getTime()) {
            toDate = new Date(fromDate.getTime() + TimeUnit.HOURS.toMillis(1));
        }
        updateComponent();
    }

    protected void updateComponent() {
        textViewFromDate.setText(DateFormat.format("yyyy-MM-dd", fromDate));
        textViewFromTime.setText(DateFormat.format("hh:mm:ss", fromDate));

        textViewToDate.setText(DateFormat.format("yyyy-MM-dd", toDate));
        textViewToTime.setText(DateFormat.format("hh:mm:ss", toDate));

        searchCriteria.setStartTime(fromDate.toString()).setEndTime(toDate.toString());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date;
        if (currentTextView == textViewFromDate) {
            date = fromDate;
        } else {
            date = toDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = calendar.getTime();
        if (currentTextView == textViewFromDate) {
            fromDate = date;
        } else {
            toDate = date;
        }
        validateDates();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date date;
        if (currentTextView == textViewFromTime) {
            date = fromDate;
        } else {
            date = toDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        date = calendar.getTime();
        if (currentTextView == textViewFromTime) {
            fromDate = date;
        } else {
            toDate = date;
        }
        validateDates();
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Date date = currentTextView == textViewFromDate ? fromDate : toDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), SearchComponent.getInstance(null), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Date date = currentTextView == textViewFromTime ? fromDate : toDate;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), SearchComponent.getInstance(null), hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }

    /*******************
     * GOOGLE SEARCH
     **********************/
    static GoogleApiClient mGoogleApiClient;

    static PlaceAutocompleteAdapter mAdapter;

    public void setSearchAutoComplete() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.activity)
                .enableAutoManage(this.activity, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        editTextSearch.setOnItemClickListener(mAutocompleteClickListener);

        LatLngBounds bounds = this.activity.getCurrentLatLngBounds();
        mAdapter = new PlaceAutocompleteAdapter(this.activity, mGoogleApiClient, bounds, null);
        editTextSearch.setAdapter(mAdapter);
        // Set up the 'clear text' button that clears the text in the autocomplete view
        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            setEnabled(false, false);
            Place place = places.get(0);
            activity.moveCamera(place);
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Do nothing
    }
}
