package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.DetailComponent;
import com.bosch.si.emobility.bstp.component.ux.ReservationViewHolder;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.google.android.gms.maps.model.LatLng;

public class ReservationActivity extends Activity {

    Button reserveButton;
    DetailComponent detailComponent;

    @Override
    public int layoutResID() {
        return R.layout.activity_reservation;
    }

    @Override
    protected void setup() {
        super.setup();

        headerComponent.setDisableSearch(true);

        detailComponent = new DetailComponent(this);
        detailComponent.setParkingLocation(DataManager.getInstance().getCurrentTransaction().getParkingLocation());
        reserveButton = (Button) findViewById(R.id.reserveButton);
        reserveButton.setVisibility(View.GONE);
        populateData();
    }

    public void onRouteToLocationClicked(View view) {

        ParkingLocation parkingLocation = DataManager.getInstance().getCurrentTransaction().getParkingLocation();

        LatLng s = Utils.getMyLocationLatLng(this);
        LatLng d = new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude());//TODO replace this line by parking location position

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",
                        s.latitude, s.longitude,
                        d.latitude, d.longitude)));

        startActivity(intent);
    }

    private void populateData() {
        ReservationViewHolder viewHolder = new ReservationViewHolder(this.findViewById(android.R.id.content));
        viewHolder.populateData(DataManager.getInstance().getCurrentTransaction());
    }

    public void onCancelReservationClicked(View view) {
        //TODO
    }
}
