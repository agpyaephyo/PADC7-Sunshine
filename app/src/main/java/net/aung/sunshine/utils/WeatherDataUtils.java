package net.aung.sunshine.utils;

import android.content.Context;
import android.util.ArrayMap;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aung on 12/13/15.
 */
public class WeatherDataUtils {

    private static Map<Integer, String> weatherDescMap;
    private static final String ICON_BASE_URL_COLORED = "http://www.aungpyaephyo.xyz/sunshine_icons-master/Archive/Colored/";

    private static final String ICON_BASE_URL_MONO = "http://www.aungpyaephyo.xyz/sunshine_icons-master/Archive/Mono/";

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    /**
     *
     * @param weatherId
     * @return
     */
    public static String getWeatherDescription(int weatherId) {
        if(weatherDescMap == null){
            loadWeatherDescMap();
        }
        return weatherDescMap.get(weatherId);
    }

    public static void loadWeatherDescMap() {
        Context context = SunshineApplication.getContext();
        weatherDescMap = new HashMap<>();
        weatherDescMap.put(200, context.getString(R.string.wd_200));
        weatherDescMap.put(201, context.getString(R.string.wd_201));
        weatherDescMap.put(202, context.getString(R.string.wd_202));
        weatherDescMap.put(210, context.getString(R.string.wd_210));
        weatherDescMap.put(211, context.getString(R.string.wd_211));
        weatherDescMap.put(212, context.getString(R.string.wd_212));
        weatherDescMap.put(221, context.getString(R.string.wd_221));
        weatherDescMap.put(230, context.getString(R.string.wd_230));
        weatherDescMap.put(231, context.getString(R.string.wd_231));
        weatherDescMap.put(232, context.getString(R.string.wd_232));

        weatherDescMap.put(300, context.getString(R.string.wd_300));
        weatherDescMap.put(301, context.getString(R.string.wd_301));
        weatherDescMap.put(302, context.getString(R.string.wd_302));
        weatherDescMap.put(310, context.getString(R.string.wd_310));
        weatherDescMap.put(311, context.getString(R.string.wd_311));
        weatherDescMap.put(312, context.getString(R.string.wd_312));
        weatherDescMap.put(313, context.getString(R.string.wd_313));
        weatherDescMap.put(314, context.getString(R.string.wd_314));
        weatherDescMap.put(321, context.getString(R.string.wd_321));

        weatherDescMap.put(500, context.getString(R.string.wd_500));
        weatherDescMap.put(501, context.getString(R.string.wd_501));
        weatherDescMap.put(502, context.getString(R.string.wd_502));
        weatherDescMap.put(503, context.getString(R.string.wd_503));
        weatherDescMap.put(504, context.getString(R.string.wd_504));
        weatherDescMap.put(511, context.getString(R.string.wd_511));
        weatherDescMap.put(520, context.getString(R.string.wd_520));
        weatherDescMap.put(521, context.getString(R.string.wd_521));
        weatherDescMap.put(522, context.getString(R.string.wd_522));
        weatherDescMap.put(531, context.getString(R.string.wd_531));

        weatherDescMap.put(600, context.getString(R.string.wd_600));
        weatherDescMap.put(601, context.getString(R.string.wd_601));
        weatherDescMap.put(602, context.getString(R.string.wd_602));
        weatherDescMap.put(611, context.getString(R.string.wd_611));
        weatherDescMap.put(612, context.getString(R.string.wd_612));
        weatherDescMap.put(615, context.getString(R.string.wd_615));
        weatherDescMap.put(616, context.getString(R.string.wd_616));
        weatherDescMap.put(620, context.getString(R.string.wd_620));
        weatherDescMap.put(621, context.getString(R.string.wd_621));
        weatherDescMap.put(622, context.getString(R.string.wd_622));

        weatherDescMap.put(701, context.getString(R.string.wd_701));
        weatherDescMap.put(711, context.getString(R.string.wd_711));
        weatherDescMap.put(721, context.getString(R.string.wd_721));
        weatherDescMap.put(731, context.getString(R.string.wd_731));
        weatherDescMap.put(741, context.getString(R.string.wd_741));
        weatherDescMap.put(751, context.getString(R.string.wd_751));
        weatherDescMap.put(761, context.getString(R.string.wd_761));
        weatherDescMap.put(762, context.getString(R.string.wd_762));
        weatherDescMap.put(771, context.getString(R.string.wd_771));
        weatherDescMap.put(781, context.getString(R.string.wd_781));

        weatherDescMap.put(800, context.getString(R.string.wd_800));
        weatherDescMap.put(801, context.getString(R.string.wd_801));
        weatherDescMap.put(802, context.getString(R.string.wd_802));
        weatherDescMap.put(803, context.getString(R.string.wd_803));
        weatherDescMap.put(804, context.getString(R.string.wd_804));

        weatherDescMap.put(900, context.getString(R.string.wd_900));
        weatherDescMap.put(901, context.getString(R.string.wd_901));
        weatherDescMap.put(902, context.getString(R.string.wd_902));
        weatherDescMap.put(903, context.getString(R.string.wd_903));
        weatherDescMap.put(904, context.getString(R.string.wd_904));
        weatherDescMap.put(905, context.getString(R.string.wd_905));
        weatherDescMap.put(906, context.getString(R.string.wd_906));

        weatherDescMap.put(951, context.getString(R.string.wd_951));
        weatherDescMap.put(952, context.getString(R.string.wd_952));
        weatherDescMap.put(953, context.getString(R.string.wd_953));
        weatherDescMap.put(954, context.getString(R.string.wd_954));
        weatherDescMap.put(955, context.getString(R.string.wd_955));
        weatherDescMap.put(956, context.getString(R.string.wd_956));
        weatherDescMap.put(957, context.getString(R.string.wd_957));
        weatherDescMap.put(958, context.getString(R.string.wd_958));
        weatherDescMap.put(959, context.getString(R.string.wd_959));
        weatherDescMap.put(960, context.getString(R.string.wd_960));
        weatherDescMap.put(961, context.getString(R.string.wd_961));
        weatherDescMap.put(962, context.getString(R.string.wd_962));
    }

    /**
     *
     * @param weatherId
     * @return
     */
    public static String getArtUrlFromWeatherCondition(int weatherId) {
        return getUrlForWeatherCondition(weatherId, true);
    }

    /**
     *
     * @param weatherId
     * @return
     */
    public static String getIconUrlForWeatherCondition(int weatherId) {
        return getUrlForWeatherCondition(weatherId, false);
    }

    /**
     *
     * @param weatherId
     * @param isColored
     * @return
     */
    private static String getUrlForWeatherCondition(int weatherId, boolean isColored) {
        String url = isColored ? ICON_BASE_URL_COLORED : ICON_BASE_URL_MONO;

        if (weatherId >= 200 && weatherId <= 232) {
            return url + "art_storm.png";
        } else if (weatherId >= 300 && weatherId <= 321) {
            return url + "art_light_rain.png";
        } else if (weatherId >= 500 && weatherId <= 504) {
            return url + "art_rain.png";
        } else if (weatherId == 511) {
            return url + "art_snow.png";
        } else if (weatherId >= 520 && weatherId <= 531) {
            return url + "art_rain.png";
        } else if (weatherId >= 600 && weatherId <= 622) {
            return url + "art_snow.png";
        } else if (weatherId >= 701 && weatherId <= 761) {
            return url + "art_fog.png";
        } else if (weatherId == 761 || weatherId == 781) {
            return url + "art_storm.png";
        } else if (weatherId == 800) {
            return url + "art_clear.png";
        } else if (weatherId == 801) {
            return url + "art_light_clouds.png";
        } else if (weatherId >= 802 && weatherId <= 804) {
            return url + "art_clouds.png";
        }

        return null;
    }

    /*
     * Helper method to provide the correct image according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return A string URL to an appropriate image or null if no mapping is found
     */
    public static String getImageUrlForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return "http://upload.wikimedia.org/wikipedia/commons/2/28/Thunderstorm_in_Annemasse,_France.jpg";
        } else if (weatherId >= 300 && weatherId <= 321) {
            return "http://upload.wikimedia.org/wikipedia/commons/a/a0/Rain_on_leaf_504605006.jpg";
        } else if (weatherId >= 500 && weatherId <= 504) {
            return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg";
        } else if (weatherId == 511) {
            return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG";
        } else if (weatherId >= 520 && weatherId <= 531) {
            return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg";
        } else if (weatherId >= 600 && weatherId <= 622) {
            return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG";
        } else if (weatherId >= 701 && weatherId <= 761) {
            return "http://upload.wikimedia.org/wikipedia/commons/e/e6/Westminster_fog_-_London_-_UK.jpg";
        } else if (weatherId == 761 || weatherId == 781) {
            return "http://upload.wikimedia.org/wikipedia/commons/d/dc/Raised_dust_ahead_of_a_severe_thunderstorm_1.jpg";
        } else if (weatherId == 800) {
            return "http://upload.wikimedia.org/wikipedia/commons/7/7e/A_few_trees_and_the_sun_(6009964513).jpg";
        } else if (weatherId == 801) {
            return "http://upload.wikimedia.org/wikipedia/commons/e/e7/Cloudy_Blue_Sky_(5031259890).jpg";
        } else if (weatherId >= 802 && weatherId <= 804) {
            return "http://upload.wikimedia.org/wikipedia/commons/5/54/Cloudy_hills_in_Elis,_Greece_2.jpg";
        }
        return null;
    }
}
