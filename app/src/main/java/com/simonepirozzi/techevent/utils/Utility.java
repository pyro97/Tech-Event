package com.simonepirozzi.techevent.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static boolean isNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED)
            return true;
        else return false;
    }

    public static List<String> getJson(Context context) {
        String json;
        List<String> numberList = new ArrayList();
        try {
            InputStream is = context.getAssets().open(Constants.JSON_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                numberList.add(jsonObject
                        .getString(Constants.JSON_COMUNE) + "," + jsonObject.getString(Constants.JSON_PROVINCE));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return numberList;
    }
}
