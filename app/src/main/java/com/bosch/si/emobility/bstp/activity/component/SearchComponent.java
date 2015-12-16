package com.bosch.si.emobility.bstp.activity.component;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
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

    static Date currentFromDate;
    static Date currentToDate;

    public AutoCompleteTextView getEditTextSearch() {
        return editTextSearch;
    }

    public ImageButton getImageButtonClear() {
        return imageButtonClear;
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
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Calendar calendar = Calendar.getInstance();
        currentFromDate = calendar.getTime();
        currentToDate = new Date(currentFromDate.getTime() + TimeUnit.HOURS.toMillis(1));

        textViewFromDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", currentFromDate));
        textViewFromTime.setText(android.text.format.DateFormat.format("hh:mm:ss", currentFromDate));

        textViewToDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", currentToDate));
        textViewToTime.setText(android.text.format.DateFormat.format("hh:mm:ss", currentToDate));
    }

    private void validateDates() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        if (currentFromDate.getTime() < date.getTime()) {
            currentFromDate = date;
        }
        if (currentToDate.getTime() < currentFromDate.getTime()) {
            currentToDate = new Date(currentFromDate.getTime() + TimeUnit.HOURS.toMillis(1));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date;
        if (currentTextView == textViewFromDate) {
            date = currentFromDate;
        } else {
            date = currentToDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = calendar.getTime();
        if (currentTextView == textViewFromDate) {
            currentFromDate = date;
        } else {
            currentToDate = date;
        }
        validateDates();
        currentTextView.setText(android.text.format.DateFormat.format("yyyy-MM-dd", date));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date date;
        if (currentTextView == textViewFromDate) {
            date = currentFromDate;
        } else {
            date = currentToDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        date = calendar.getTime();
        if (currentTextView == textViewFromDate) {
            currentFromDate = date;
        } else {
            currentToDate = date;
        }
        validateDates();
        currentTextView.setText(android.text.format.DateFormat.format("hh:mm:ss", date));
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Date date = currentTextView == textViewFromDate ? currentFromDate : currentToDate;
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
            Date date = currentTextView == textViewFromDate ? currentFromDate : currentToDate;
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
            final Place place = places.get(0);
            activity.moveCamera(place.getLatLng());
            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id, CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber, websiteUri));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Do nothing
    }
}
