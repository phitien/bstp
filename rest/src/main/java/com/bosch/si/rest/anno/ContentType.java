package com.bosch.si.rest.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sgp0458 on 18/12/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentType {
    String value() default "application/json";
}
