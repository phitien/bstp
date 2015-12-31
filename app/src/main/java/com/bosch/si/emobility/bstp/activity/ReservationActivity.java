package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.View;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.ux.ReservationViewHolder;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.google.android.gms.maps.model.LatLng;

public class ReservationActivity extends Activity {

    @Override
    public int layoutResID() {
        return R.layout.activity_reservation;
    }

    @Override
    protected void setup() {
        super.setup();
        populateData();
    }

    public void onRouteToLocationClicked(View view) {
        Location myLocation = Utils.getMyLocation(this);
        LatLng destLatLng = new LatLng(37.423156, -122.084917);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        startActivity(intent);
    }

    private void populateData() {
        ReservationViewHolder viewHolder = new ReservationViewHolder(this.findViewById(android.R.id.content));
        viewHolder.populateData(DataManager.getInstance().getCurrentTransaction());
    }
}
