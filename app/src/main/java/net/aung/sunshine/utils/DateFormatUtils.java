package net.aung.sunshine.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by aung on 12/14/15.
 */
public class DateFormatUtils {

    public static SimpleDateFormat sdfWeatherStatusDate =
            new SimpleDateFormat("EE MMM dd", SettingsUtils.getLocale());
    public static SimpleDateFormat sdfWeatherStatusDateToday =
            new SimpleDateFormat("MMMM dd", SettingsUtils.getLocale());
    public static SimpleDateFormat sdfWeatherStatusDateTomorrow =
            new SimpleDateFormat("MMM dd", SettingsUtils.getLocale());
    public static SimpleDateFormat sdfDay =
            new SimpleDateFormat("EEEE", SettingsUtils.getLocale());

    public static void loadDateFormat(Locale locale) {
        sdfWeatherStatusDate = new SimpleDateFormat("EE MMM dd", locale);
        sdfWeatherStatusDateToday = new SimpleDateFormat("MMMM dd", locale);
        sdfWeatherStatusDateTomorrow = new SimpleDateFormat("MMM dd", locale);
        sdfDay = new SimpleDateFormat("EEEE", locale);
    }
}
