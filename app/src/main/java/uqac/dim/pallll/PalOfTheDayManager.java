package uqac.dim.pallll;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

public class PalOfTheDayManager {
    private static final String PREF_NAME = "PalOfTheDayPrefs";
    private static final String KEY_LAST_DATE = "";
    private static final String KEY_LAST_PAL_ID = "005";

    public static boolean checkLastDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String storedDateStr = prefs.getString(KEY_LAST_DATE, null);

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        cal.setTime(currentDate);
        int currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);

        return storedDateStr == null || storedDateStr.isEmpty() || !storedDateStr.equals(String.valueOf(currentDayOfYear));
    }

    public static void setLastDate(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        cal.setTime(currentDate);
        int currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        editor.putString(KEY_LAST_DATE, String.valueOf(currentDayOfYear));
        editor.apply();
    }

    public static int getLastPalId(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_LAST_PAL_ID, -1);
    }

    public static void setLastPalId(Context context, int palId){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_LAST_PAL_ID, palId);
        editor.apply();
    }
}