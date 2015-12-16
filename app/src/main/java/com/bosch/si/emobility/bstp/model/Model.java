package com.bosch.si.emobility.bstp.model;

import com.google.gson.Gson;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class Model implements IModel {

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
