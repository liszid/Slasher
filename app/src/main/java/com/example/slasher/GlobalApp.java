package com.example.slasher;
import android.app.Application;

import com.example.slasher.modular.PreferencesManager;

public class GlobalApp extends Application {
    private PreferencesManager preferencesManager;

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesManager = new PreferencesManager(this);
    }

    public PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }
}
