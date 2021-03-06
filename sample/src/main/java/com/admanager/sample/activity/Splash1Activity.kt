package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.adjust.AdmAdjust
import com.admanager.admob.AdmobAdapter
import com.admanager.config.RemoteConfigHelper
import com.admanager.core.AdManagerBuilder
import com.admanager.sample.BuildConfig
import com.admanager.sample.R
import com.admanager.sample.RCUtils

/**
 * Created by Gust on 20.11.2018.
 */
class Splash1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RemoteConfigHelper.init(this)
        RemoteConfigHelper.setAdsEnabled(!BuildConfig.DEBUG)
        //        If you want to close all ads for testing, use this method
        //RemoteConfigHelper.setAdsEnabled(!BuildConfig.DEBUG);

//        AdmStaticNotification.setClickListener(this, () -> System.out.println("CLICKED")); // you need to call this method in launcher Activity
//        BoosterNotificationApp.setClickListener(this, () -> System.out.println("CLICKED")); // you need to call this method in launcher Activity
        AdManagerBuilder(this)
            .add(AdmobAdapter(RCUtils.s1_admob_enabled).withRemoteConfigId(RCUtils.s1_admob_id))
            .clickListener { _, _, _, revenue ->
                AdmAdjust.event(
                    this,
                    R.string.adjust_event_inters,
                    revenue
                )
            }
            .thenStart(Splash2Activity::class.java)
            .build()
            .show()
    }
}