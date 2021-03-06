package com.isaac.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isaac.coolweather.application.MyApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Name: Utilities
 * Created by IsaacCn on 2015/8/30.
 */

/*
* Find all cities in db, then find the city that is most close to the given geo info.
* */
public class Utilities {
    public static final double KELVIN_ZERO_DEGREE = 273.15;
    private static final String CURRENT_WEATHER_BY_LOCATION_URL_PREFIX = "http://api.openweathermap.org/data/2.5/weather?";//http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139

    /**
     * updateWeatherInfoByLocation
     *
     * @param context
     * @param longitudeParam
     * @param latitudeParam
     * @param listener       (callback, update UI)
     */
    public static void updateWeatherInfoByLocation(final Context context, final double longitudeParam, final double latitudeParam, final UpdateUIListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                CoolWeatherDBOpenHelper dbOpenHelper = new CoolWeatherDBOpenHelper(context, "CityInfo.db", null, 1);
//                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//                Cursor resultCursor = db.rawQuery("SELECT * FROM city_info", null);
//                /*Initialize one most close city to the given geo info*/
//                int mostCloseCityId = 0;
//                double mostCloseDistance = 259200;
//                if (resultCursor.moveToFirst()) {
//                    int cityId;
//                    double longitude;
//                    double latitude;
//                    do {
//                        cityId = resultCursor.getInt(resultCursor.getColumnIndex("city_id"));
//                        longitude = resultCursor.getFloat(resultCursor.getColumnIndex("lon"));
//                        latitude = resultCursor.getFloat(resultCursor.getColumnIndex("lat"));
//                        /*Judge whether the chosen city is more close to the given geo info*/
//                        if ((Math.pow((longitudeParam - longitude), 2) + Math.pow((latitudeParam - latitude), 2)) < mostCloseDistance) {
//                            mostCloseCityId = cityId;
//                            mostCloseDistance = Math.pow((longitudeParam - longitude), 2) + Math.pow((latitudeParam - latitude), 2);
//                        }
//                    } while (resultCursor.moveToNext());
//                }
//                resultCursor.close();
//                db.close();
                //到目前为止已经获得了距离给定坐标最近的城市ID
                HttpURLConnection connection = null;
                try {
                    //eg: //http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139
                    URL url = new URL(CURRENT_WEATHER_BY_LOCATION_URL_PREFIX + "lat=" + latitudeParam + "&lon=" + longitudeParam);
                    LogUtil.d("Utilities", url.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void updateCurrentCityWeatherInfo(final String urlWithCityId, final UpdateUIListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlWithCityId);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static boolean getAutoUpdateFlag() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        boolean autoUpdateFlag = pref.getBoolean("AutoUpdateFlag", true);
        return autoUpdateFlag;
    }

    public static int getAutoUpdateInterval() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        int autoUpdateInterval = pref.getInt("AutoUpdateInterval", 2);
        return autoUpdateInterval;
    }

    public static void setAutoUpdateFlag(boolean flag) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("AutoUpdateFlag", flag);
        editor.commit();
    }

    public static void setAutoUpdateInterval(int interval) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("AutoUpdateInterval", interval);
        editor.commit();
    }
}
