package igbokey.igwe.amanda;

import android.content.Context;
import android.content.SharedPreferences;

class PrefManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;

    // Shared preferences file name
    private static final String PREF_NAME = "igbokey.igwe.amanda";
    private static final String IS_ACTIVATED = "IsActivated";
    private static final String IS_ADDED = "isAdded";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setIsActivated(boolean onoff) {
        editor.putBoolean(IS_ACTIVATED, onoff);
        editor.apply();
    }

    public boolean isActivated() {
        return pref.getBoolean(IS_ACTIVATED, false);
    }

    public void setAdded(boolean onoff) {
        editor.putBoolean(IS_ADDED, onoff);
        editor.apply();
    }

    public boolean isAdded() {
        return pref.getBoolean(IS_ADDED, false);
    }


}
