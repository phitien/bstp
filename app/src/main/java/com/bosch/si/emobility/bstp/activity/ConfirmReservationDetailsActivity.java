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
import com.bosch.si.emobility.bstp.service.GetDriverInfoService;
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
//            finish();
        } else if (event.getType() == Constants.EventType.DIALOG_YES.toString()) {
            //dismiss the view
            finish();
        } else if (event.getType() == Constants.EventType.DIALOG_NO.toString()) {
            //do nothing
        } else {
            super.onEventMainThread(event);
        }
    }

    public void onCancelConfirmReserveButtonClicked(View view) {
        Utils.Notifier.yesNoDialog(getString(R.string.cancel_reservation_confirm_message));
    }

    public void onConfirmReserveButtonClicked(View view) {
        //reserve the space
        loadDriverInfo(new DriverLoaderCallback() {
            @Override
            public void beforeLoad() {
                Utils.Indicator.show();
            }

            @Override
            public void afterLoaded(Driver driver) {
                reserve(driver);
            }

            @Override
            public void loadFailed() {
                Utils.Notifier.alert(getString(R.string.unable_to_get_driver_details));
            }
        });
    }

    private void reserve(Driver driver) {
        Utils.Indicator.show();

        ReserveParkingService service = new ReserveParkingService();
        service.driverId = driver.getDriverId();
        service.parkingId = parkingLocation.getParkingId();
        service.additionalInfo = getString(R.string.bstp_app);
        service.startTime = Utils.getRestfulFormattedDatetime(startTime);
        service.endTime = Utils.getRestfulFormattedDatetime(toTime);
        service.vehicleId = confirmReservationComponent.getSelectedTruck();
        service.paymentMode = Constants.PAYMENT_MODE_CREDIT;
        service.parkingLocationName = parkingLocation.getLocationTitle();

        service.executeAsync(new ServiceCallback() {

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
        });
    }
}
