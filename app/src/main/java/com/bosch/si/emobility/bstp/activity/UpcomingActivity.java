package com.bosch.si.emobility.bstp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.component.ux.ReservationViewHolder;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingTransaction;
import com.bosch.si.emobility.bstp.service.GetUpcomingReservationsService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpcomingActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listViewUpcoming;
    TextView emptyView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public int layoutResID() {
        return R.layout.activity_upcoming;
    }

    @Override
    protected void setup() {
        super.setup();

        headerComponent.setDisableSearch(true);

        listViewUpcoming = (ListView) findViewById(R.id.listViewUpcoming);
        listViewUpcoming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDetailActivity(position);
            }
        });

        emptyView = (TextView) findViewById(R.id.noReservationsInfoLabel);
        listViewUpcoming.setEmptyView(emptyView);
    }

    private void openDetailActivity(int position) {
        DataManager.getInstance().setCurrentTransaction(DataManager.getInstance().getFilteredTransactions(Constants.TRANSANCTION_STATUS_RESERVATION).get(position));
        Intent intent = new Intent(UpcomingActivity.this, ReservationActivity.class);
        startActivity(intent);
    }

    private void populateData() {

        Utils.Indicator.show();

        GetUpcomingReservationsService service = new GetUpcomingReservationsService();
        Date fromDate = Utils.getNextDateByAddingHours(0);
        Date toDate = Utils.getNextDateByAddingHours(24 * 365);

        service.fromDate = Utils.getUTCDatetime(fromDate);
        service.toDate = Utils.getUTCDatetime(toDate);
        service.searchTerm = "";//TODO: to clarify requirements

        service.executeAsync(
                new ServiceCallback() {
                    @Override
                    public void success(IService service) {
                        final String responseString = service.getResponseString();
                        Gson gson = new Gson();
                        DataManager.getInstance().setTransactions(
                                (List<ParkingTransaction>) gson.fromJson(
                                        responseString,
                                        new TypeToken<ArrayList<ParkingTransaction>>() {
                                        }.getType()));
                        UpcomingAdapter adapter = new UpcomingAdapter(UpcomingActivity.this);
                        listViewUpcoming.setAdapter(adapter);
                    }

                    @Override
                    public void failure(IService service) {
                        service.getResponseCode();
                    }

                    @Override
                    public void onPostExecute(IService service) {
                        Utils.Indicator.hide();
                    }
                }
        );
    }


    public void onSearchButtonClicked(View view) {
    }

    @Override
    public void onRefresh() {
//        populateData();
    }

    private class UpcomingAdapter extends ArrayAdapter<ParkingTransaction> {

        public UpcomingAdapter(Context context) {
            super(context, R.layout.upcoming_item, DataManager.getInstance().getFilteredTransactions(Constants.TRANSANCTION_STATUS_RESERVATION));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ReservationViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = getLayoutInflater().inflate(R.layout.upcoming_item, parent, false);

                holder = new ReservationViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ReservationViewHolder) convertView.getTag();
            }
            holder.populateData(getItem(position));
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateData();
    }

}
