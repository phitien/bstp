package com.bosch.si.emobility.bstp.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.DetailComponent;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Event;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;
import com.bosch.si.emobility.bstp.service.CancelParkingReservationService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.model.LatLng;

public class ReservationActivity extends Activity {

    Button reserveButton;
    DetailComponent detailComponent;

    ParkingTransaction currentTransanction;

    TextView startTime;
    TextView endTime;

    @Override
    public int layoutResID() {
        return R.layout.activity_reservation;
    }

    @Override
    protected void setup() {
        super.setup();

        headerComponent.setDisableSearch(true);
        currentTransanction = DataManager.getInstance().getCurrentTransaction();

        detailComponent = new DetailComponent(this);
        detailComponent.setParkingLocation(DataManager.getInstance().getCurrentTransaction().getParkingLocation());
        reserveButton = (Button) findViewById(R.id.reserveButton);
        reserveButton.setVisibility(View.GONE);

        startTime = (TextView) findViewById(R.id.textViewStartTime);
        endTime = (TextView) findViewById(R.id.textViewEndTime);

        startTime.setText(currentTransanction.getFormattedStartTime());
        endTime.setText(currentTransanction.getFormattedEndTime());

    }

    public void onRouteToLocationClicked(View view) {

        Event.broadcast(Utils.getString(R.string.session_expired), Constants.EventType.SESSION_EXPIRED.toString());

//        ParkingLocation parkingLocation = currentTransanction.getParkingLocation();
//
//        LatLng s = Utils.getMyLocationLatLng(this);
//        LatLng d = new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude());//TODO replace this line by parking location position
//
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",
//                        s.latitude, s.longitude,
//                        d.latitude, d.longitude)));
//
//        startActivity(intent);
    }


    public void onCancelReservationClicked(View view) {

        try {

            Utils.Indicator.show();

            CancelParkingReservationService cancelParkingReservationService = new CancelParkingReservationService();
            cancelParkingReservationService.transactionId = currentTransanction.getTransactionId();
            cancelParkingReservationService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    Utils.Notifier.notify(getString(R.string.reservation_canceled_successfully));
                    DataManager.getInstance().setJustCanceledReservations(true);
                    finish();
                }

                @Override
                public void failure(IService service) {
                    Utils.Notifier.alert(getString(R.string.reservation_canceled_unsuccessfully));
                }

                @Override
                public void onPostExecute(IService service) {
                    super.onPostExecute(service);
                    Utils.Indicator.hide();
                }
            });
        } catch (Exception e) {
            Utils.Notifier.alert(getString(R.string.reservation_canceled_unsuccessfully));
        }
    }
}
