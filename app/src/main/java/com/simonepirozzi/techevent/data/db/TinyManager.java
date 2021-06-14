package com.simonepirozzi.techevent.data.db;

import android.content.Context;

public class TinyManager {
    public static final String EVENT = "evento";
    public static final String FAVOURITE = "preferiti";
    public static final String EVENT_MANAGEMENT = "gestioneEvento";
    public static final String EDIT_PROFILE = "modProfilo";
    public static final String YES = "si";
    public static final String PUBLISHING_EVENT = "mainToGestione";

    public static TinyDB getInstance(Context context){
        return new TinyDB(context);
    }
}
