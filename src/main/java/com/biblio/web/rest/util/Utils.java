/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biblio.web.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author kouwonou
 */
public final class Utils {

    public final static Date convertToDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (date != null && !date.isEmpty()) {
            return sdf.parse(date);
        }
        return null;
    }
    public final static String formatDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (date != null ) {
            return sdf.format(date);
        }
        return "";
    }
}
