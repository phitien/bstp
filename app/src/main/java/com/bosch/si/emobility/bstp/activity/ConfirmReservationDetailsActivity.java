package com.bosch.si.emobility.bstp.activity;

import android.view.View;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.ConfirmReservationComponent;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Event;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.Driver;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;
import com.bosch.si.emobility.bstp.service.ReserveParkingService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by SSY1SGP on 26/1/16.
 */
public class ConfirmReservationDetailsActivity extends Activity {

    ConfirmReservationComponent confirmReservationComponent;

    Date startTime;
    Date toTime;

    ParkingLocation parkingLocation;
    Driver driver;

    @Override
    public int layoutResID() {
        return R.layout.activity_confirm_reservation_details;
    }

    @Override
    protected void setup() {
        super.setup();

        headerComponent.setDisableSearch(true);

        DataManager dataManager = DataManager.getInstance();

        startTime = dataManager.getStartTime();
        toTime = dataManager.getEndTime();
        parkingLocation = dataManager.getCurrentParkingLocation();

        confirmReservationComponent = new ConfirmReservationComponent(this);

        confirmReservationComponent.setParkingLocation(parkingLocation);
        confirmReservationComponent.setFromDate(startTime);
        confirmReservationComponent.setToDate(toTime);

        driver = DataManager.getInstance().getCurrentDriver();

        confirmReservationComponent.setDriver(driver);
        confirmReservationComponent.populateData();

    }

    @Override
    public void onEventMainThread(Event event) {
        if (event.getType() == Constants.EventType.ALERT_DIALOG_HIDE.toString()) {
            finish();
        } else {
            super.onEventMainThread(event);
        }
    }

    public void onCancelConfirmReserveButtonClicked(View view) {
        //dismiss the view
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void onConfirmReserveButtonClicked(View view) {
        //reserve the space

        Utils.Indicator.show();

        ReserveParkingService reserveParkingService = new ReserveParkingService();
        reserveParkingService.driverId = driver.getDriverId();
        reserveParkingService.parkingId = parkingLocation.getParkingId();
        reserveParkingService.additionalInfo = getString(R.string.bstp_app);
        reserveParkingService.startTime = Utils.getRestfulFormattedDatetime(startTime);
        reserveParkingService.endTime = Utils.getRestfulFormattedDatetime(toTime);
        reserveParkingService.vehicleId = confirmReservationComponent.getSelectedTruck();
        reserveParkingService.paymentMode = Constants.PAYMENT_MODE_CREDIT;
        reserveParkingService.parkingLocationName = parkingLocation.getLocationTitle();

        reserveParkingService.executeAsync(new ServiceCallback() {

            @Override
            public void success(IService service) {
                ParkingTransaction parkingTransaction = new Gson().fromJson(service.getResponseString(), ParkingTransaction.class);
                if (parkingTransaction != null) {
                    Utils.Notifier.notify(getString(R.string.reserved_successfully));
                    finish();
                } else {
                    Utils.Notifier.alert(getString(R.string.reserved_unsuccessfully));
                }
            }

            @Override
            public void failure(IService service) {
                Utils.Notifier.alert(getString(R.string.reserved_unsuccessfully));
            }

            @Override
            public void onPostExecute(IService service) {
                super.onPostExecute(service);
                Utils.Indicator.hide();
            }
        });
    }
}
