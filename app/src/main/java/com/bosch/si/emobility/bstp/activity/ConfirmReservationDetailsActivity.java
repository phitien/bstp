package com.bosch.si.emobility.bstp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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

/**
 * Created by SSY1SGP on 26/1/16.
 */
public class ConfirmReservationDetailsActivity extends Activity {

    ConfirmReservationComponent confirmReservationComponent;

    String fromDateTime;
    String toDateTime;

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

        Intent intent = getIntent();

        fromDateTime = intent.getStringExtra(Constants.FROM_DATE_TIME_INTENT_DATA_KEY);
        toDateTime = intent.getStringExtra(Constants.TO_DATE_TIME_INTENT_DATA_KEY);
        parkingLocation = (ParkingLocation)intent.getSerializableExtra(Constants.PARKING_LOCATION_INTENT_DATA_KEY);

        confirmReservationComponent = new ConfirmReservationComponent(this);

        confirmReservationComponent.setParkingLocation(parkingLocation);
        confirmReservationComponent.setFromDateTime(fromDateTime);
        confirmReservationComponent.setToDateTime(toDateTime);

        driver = DataManager.getInstance().getCurrentDriver();

        if (driver == null){

            final ProgressDialog mDialog = new ProgressDialog(ConfirmReservationDetailsActivity.this);
                                                                mDialog.setMessage("Please wait...");
                                                                mDialog.setCancelable(false);
                                                                mDialog.show();

            GetDriverInfoService driverInfoService = new GetDriverInfoService();
            driverInfoService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    Driver driver = new Gson().fromJson(service.getResponseString(), Driver.class);
                    if (driver != null){
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

                    if (driver != null){
                        confirmReservationComponent.setDriver(driver);
                        confirmReservationComponent.populateData();
                    }
                    else {
                        //cancel the intent
                        Utils.Notifier.notify("Unable to get driver details");
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
            });
        }
        else {
            confirmReservationComponent.setDriver(driver);
            confirmReservationComponent.populateData();
        }
    }

    public void onCancelConfirmReserveButtonClicked(View view){
        //dismiss the view
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void onConfirmReserveButtonClicked(View view){
        //reserve the space

        Utils.Indicator.setDialogTitle("Please wait...");
        Utils.Indicator.show();

        ReserveParkingService reserveParkingService = new ReserveParkingService();
        reserveParkingService.driverId = driver.getDriverId();
        reserveParkingService.parkingId = parkingLocation.getParkingId();
        reserveParkingService.additionalInfo = "";
        reserveParkingService.startTime = fromDateTime;
        reserveParkingService.endTime = toDateTime;
        reserveParkingService.vehicleId = confirmReservationComponent.getTruck().getVehicleId();
        reserveParkingService.parkingId = Constants.PAYMENT_MODE_CREDIT;

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
                    Utils.Notifier.notify("Succesfully reserved the parking space!");
                }
                else {
                    Utils.Notifier.notify("Unable to reserve parking space!");
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }
}
