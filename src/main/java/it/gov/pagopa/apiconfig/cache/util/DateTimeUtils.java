package it.gov.pagopa.apiconfig.cache.util;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {

    public static String getString(ZonedDateTime utcDateTime) {
        ZonedDateTime romeDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Rome")).truncatedTo(ChronoUnit.MICROS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXXXX'['VV']'");
        return formatter.format(romeDateTime);
    }

    public static ZonedDateTime getZonedDateTime(ZonedDateTime utcDateTime) {
        return utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Rome")).truncatedTo(ChronoUnit.MICROS);
    }
}

