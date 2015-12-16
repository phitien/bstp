package com.bosch.si.rest.method;

/**
 * Created by sgp0458 on 27/8/15.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PUT {
    String value();

    //String contentType() default "application/x-www-form-urlencoded";
    String contentType() default "application/json";

}
