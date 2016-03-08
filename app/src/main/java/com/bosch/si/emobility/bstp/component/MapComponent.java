package com.bosch.si.emobility.bstp.component;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;
import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Event;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.manager.DataManager;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MapComponent extends Component {

    private LatLngBounds currentCameraBounds;
    private Marker myLocationMarker;

    private LatLng searchingLatLng;
    private Marker searchingMarker;

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<ParkingLocation> parkingLocations = new ArrayList<>();

    private LatLng prevLatLng = null;
    private LatLng currLatLng = null;
    private float zoomLevel = -1;

    public MapComponent(Activity activity) {
        super(activity);
    }

    public LatLng getSearchingLatLng() {
        return searchingLatLng;
    }

    public void setSearchingLatLng(LatLng searchingLatLng) {
        this.searchingLatLng = searchingLatLng;
        moveCamera(this.searchingLatLng);
    }

    GoogleMap map;
    SupportMapFragment mapFragment;
    View mapView;


    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.mapLayout);
        mapFragment = (SupportMapFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = this.activity.findViewById(R.id.map);
        map = mapFragment.getMap();

        //if (map == null)
        //setUpMap();
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void zoomToLocation(LatLng latLng) {
        moveCamera(latLng);
    }

    private void drawMySearchingMarker(LatLng latLng) {
//        if (searchingMarker == null)
//            searchingMarker = map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(activity.getString(R.string.current_search_location)));
//        else
//            searchingMarker.setPosition(latLng);
    }

    private LatLng drawMyLocationMarker() {
        LatLng latLng = Utils.getMyLocationLatLng(this.activity);
//        if (myLocationMarker == null) {
//            myLocationMarker = map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(activity.getString(R.string.your_location))
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//        } else
//            myLocationMarker.setPosition(latLng);
        return latLng;
    }

    @NonNull
    private LatLngBounds getLatLngBounds(LatLng latLng, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private float getZoomLevel(float radius) {
        if (zoomLevel <= 0) {
            float scale = radius / 500;
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public void moveCamera(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel(Constants.DEFAULT_ZOOM_RADIUS)));
    }

    private void displayParkingAreas() {
        drawMyLocationMarker();
        drawMySearchingMarker(searchingLatLng);
        if (shouldRefreshMap()) {
            refresh();
        }
    }

    //refresh map if the distance between the current location and searching location is greater than 100 km
    private boolean shouldRefreshMap() {
        if (currLatLng != null && prevLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(prevLatLng.latitude, prevLatLng.longitude, currLatLng.latitude, currLatLng.longitude, results);
            return results[0] > 100000;
        }
        return prevLatLng == null;
    }

    private void clearMarkers() {
        searchingMarker = null;
        myLocationMarker = null;
        parkingLocations = new ArrayList<>();
    }

    private void drawLocationMarkers() {
        if (parkingLocations == null || parkingLocations.size() <= 0) {
//            Utils.Notifier.notify(activity.getString(R.string.no_parking_location_found));
        } else {
            for (ParkingLocation parkingLocation : parkingLocations) {
                try {
                    drawParkingLocationMarker(parkingLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawParkingLocationMarker(ParkingLocation parkingLocation) {
        LatLng latLng = new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude());
        if (!markerAdded(latLng)) {
            String imageName = Utils.getParkingIconName(parkingLocation);
            markers.add(map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(parkingLocation.getLocationTitle())
                    .icon(BitmapDescriptorFactory.fromResource(Utils.getImage(imageName)))));
        }
    }

    private boolean markerAdded(LatLng latLng) {
        for (Marker marker : markers) {
            if (marker.getPosition().equals(latLng))
                return true;
        }
        return false;
    }

    public LatLngBounds getCurrentLatLngBounds() {
        if (currentCameraBounds == null) {
            LatLngBounds bounds = null;
            try {
                bounds = map.getProjection().getVisibleRegion().latLngBounds;
            } catch (Exception e) {
                Utils.Log.e("BSTP_MapComponent_getCurrentLatLngBounds: ", e.getMessage());
            }
            if (bounds == null) {
                return getLatLngBounds(Utils.getMyLocationLatLng(this.activity), Constants.DEFAULT_ZOOM_RADIUS);
            }
            currentCameraBounds = bounds;
        }
        return currentCameraBounds;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        if (map != null) {
            if (enabled) {
                if (mapView != null && mapView.findViewById(1) != null) {

                    // Get the button view
                    View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0,
                            getResources().getDimensionPixelSize(R.dimen.location_button_margin_right),
                            getResources().getDimensionPixelSize(R.dimen.location_button_margin_bottom));

                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            Event.broadcast("", Constants.EventType.CAMERA_CHANGED.toString());
                            currentCameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
                            if (currentCameraBounds != null) {
                                LatLng center = currentCameraBounds.getCenter();
                                if (center != null) {
                                    currLatLng = center;
                                    displayParkingAreas();
                                }
                            }
                        }
                    });
                    map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            searchingLatLng = Utils.getMyLocationLatLng(activity);
                            zoomToLocation(searchingLatLng);
                            return true;
                        }
                    });
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            openLocationDetail(marker);
                            return true;
                        }
                    });
                    searchingLatLng = Utils.isLocationServiceDisabled(this.activity) ? Constants.DEFAULT_LOCATION : drawMyLocationMarker();
                    zoomToLocation(searchingLatLng);
                }
            } else {

            }
            map.setMyLocationEnabled(enabled);
            map.getUiSettings().setMyLocationButtonEnabled(enabled);
            map.getUiSettings().setZoomControlsEnabled(enabled);
            map.getUiSettings().setScrollGesturesEnabled(enabled);
            map.getUiSettings().setTiltGesturesEnabled(enabled);
            map.getUiSettings().setRotateGesturesEnabled(enabled);
        }
    }

    private void openLocationDetail(final Marker marker) {
        ParkingLocation parkingLocation = getParkingLocationFromMarker(marker);
        if (parkingLocation != null) {
            ((MapsActivity) activity).openLocationDetail(parkingLocation);
            return;
        }
    }

    private ParkingLocation getParkingLocationFromMarker(Marker marker) {
        List<ParkingLocation> parkingLocations = DataManager.getInstance().getParkingLocations();
        for (ParkingLocation parkingLocation : parkingLocations) {
            if (marker.getPosition().equals(new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude()))) {
                return parkingLocation;
            }
        }
        return null;
    }

    public void refresh() {
        AsyncTask<Void, Void, String> getAddressTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(currLatLng.latitude, currLatLng.longitude, 1);
                    return addresses.get(0).getFeatureName();
                } catch (Exception e) {
                    try {
                        return Utils.getLocationName(currLatLng.latitude, currLatLng.longitude);
                    } catch (Exception e1) {
                        e.printStackTrace();
                        e1.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String locationName) {
                if (locationName != null) {
                    ((MapsActivity) activity).getSearchCriteria()
                            .setLocationName(locationName)
                            .setLatLng(currLatLng)
                            .createSearchService()
                            .executeAsync(new ServiceCallback() {
                                @Override
                                public void success(IService service) {

                                    final String responseString = service.getResponseString();

                                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected void onPreExecute() {
                                            showIndicator();
                                            clearMarkers();
                                            drawMyLocationMarker();
                                            drawMySearchingMarker(searchingLatLng);
                                        }

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            Gson gson = new Gson();
                                            parkingLocations = gson.fromJson(responseString, new TypeToken<ArrayList<ParkingLocation>>() {
                                            }.getType());
                                            DataManager.getInstance().setParkingLocations(parkingLocations);
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            drawLocationMarkers();
                                            hideIndicator();
                                        }
                                    };

                                    task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }

                                @Override
                                public void failure(IService service) {
                                    Utils.Notifier.notify(activity.getString(R.string.service_error_message));
                                }
                            });
                }
            }
        };

        getAddressTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }

    private void showIndicator() {
        this.activity.showIndicator();
    }

    private void hideIndicator() {
        this.activity.hideIndicator();
    }
}
