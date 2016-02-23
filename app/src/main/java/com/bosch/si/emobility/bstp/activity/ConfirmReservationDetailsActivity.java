package com.bosch.si.emobility.bstp.activity;

import android.app.ProgressDialog;
import android.view.View;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.ConfirmReservationComponent;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
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

    ParkingTransaction parkingTransaction;

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

        if (driver == null) {

            final ProgressDialog mDialog = new ProgressDialog(ConfirmReservationDetailsActivity.this);
            mDialog.setMessage(getString(R.string.please_wait));
            mDialog.setCancelable(false);
            mDialog.show();

            GetDriverInfoService driverInfoService = new GetDriverInfoService();
            driverInfoService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    Driver driver = new Gson().fromJson(service.getResponseString(), Driver.class);
                    if (driver != null) {
                        DataManager.getInstance().setCurrentDriver(driver);
                    }
                }

                @Override
                public void failure(IService service) {

                }

                @Override
                public void onPostExecute(IService service) {
                    super.onPostExecute(service);
                    mDialog.dismiss();
                    driver = DataManager.getInstance().getCurrentDriver();

                    if (driver != null) {
                        confirmReservationComponent.setDriver(driver);
                        confirmReservationComponent.populateData();
                    } else {
                        //cancel the intent
                        Utils.Notifier.notify(getString(R.string.unable_to_get_driver_details));
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
            });
        } else {
            confirmReservationComponent.setDriver(driver);
            confirmReservationComponent.populateData();
        }
    }

    public void onCancelConfirmReserveButtonClicked(View view) {
        //dismiss the view
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void onConfirmReserveButtonClicked(View view) {
        //reserve the space

        Utils.Indicator.setDialogTitle(getString(R.string.please_wait));
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
                parkingTransaction = new Gson().fromJson(service.getResponseString(), ParkingTransaction.class);
            }

            @Override
            public void failure(IService service) {
            }

            @Override
            public void onPostExecute(IService service) {
                super.onPostExecute(service);
                Utils.Indicator.hide();
                if (parkingTransaction != null) {
                    Utils.Notifier.notify(getString(R.string.reserved_successfully));
                } else {
                    Utils.Notifier.notify(getString(R.string.reserved_unsuccessfully));
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
