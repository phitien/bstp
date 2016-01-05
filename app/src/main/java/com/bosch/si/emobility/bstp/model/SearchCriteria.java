package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;
import com.bosch.si.emobility.bstp.service.SearchService;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class SearchCriteria extends Model {

    private String direction;
    private String endTime;
    private String highway;
    private String latitude;
    private String locationName;
    private String longitude;
    private String searchString;
    private String startTime;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEndTime() {
        return endTime;
    }

    public SearchCriteria setEndTime(String endTime) {
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

    public String getLatitude() {
        return latitude;
    }

    public SearchCriteria setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public SearchCriteria setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public SearchCriteria setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getSearchString() {
        return searchString;
    }

    public SearchCriteria setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public SearchCriteria setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public SearchCriteria setLatLng(LatLng latLng) {
        latitude = String.valueOf(latLng.latitude);
        longitude = String.valueOf(latLng.longitude);
        return this;
    }

    private SearchService fillToSearchService(SearchService service) {
        service.direction = direction;
        service.endTime = endTime;
        service.highway = highway;
        service.latitude = latitude;
        service.locationName = locationName;
        service.longitude = longitude;
        service.searchString = searchString;
        service.startTime = startTime;
        return service;
    }

    public SearchService createSearchService() {
        SearchService service = new SearchService();
        return fillToSearchService(service);
    }
}
