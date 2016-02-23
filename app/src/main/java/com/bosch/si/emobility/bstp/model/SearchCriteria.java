package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Model;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.service.SearchService;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class SearchCriteria extends Model {

    private String direction;
    private Date endTime;
    private Date startTime;
    private String highway;
    private double latitude;
    private String locationName;
    private double longitude;
    private float radius = Constants.DEFAULT_ZOOM_RADIUS;
    private String searchString;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getEndTime() {
        return endTime;
    }

    public SearchCriteria setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getHighway() {
        return highway;
    }

    public SearchCriteria setHighway(String highway) {
        this.highway = highway;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public SearchCriteria setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchCriteria setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public SearchCriteria setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public SearchCriteria setLatLng(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        return this;
    }

    private SearchService fillToSearchService(SearchService service) {

        service.direction = direction;
        service.endTime = Utils.getRestfulFormattedDatetime(endTime);
        service.startTime = Utils.getRestfulFormattedDatetime(startTime);
        service.highway = highway;
        service.latitude = latitude;
        service.locationName = locationName;
        service.longitude = longitude;
        service.searchString = searchString;
        service.radius = radius;
        return service;
    }

    public SearchService createSearchService() {
        SearchService service = new SearchService();
        return fillToSearchService(service);
    }
}
