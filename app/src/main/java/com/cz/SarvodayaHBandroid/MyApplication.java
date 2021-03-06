package com.cz.SarvodayaHBandroid;

import android.app.Application;

import com.cz.SarvodayaHBandroid.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by thulir on 9/10/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/avenir.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
