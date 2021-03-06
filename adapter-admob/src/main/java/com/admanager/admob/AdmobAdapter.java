package com.admanager.admob;

import android.text.TextUtils;

import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Adapter;
import com.admanager.core.Consts;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;

public class AdmobAdapter extends Adapter {
    public static final String ADMOB_INTERS_TEST_ID = "ca-app-pub-3940256099942544/1033173712";
    private final AdListener ADMOB_AD_LISTENER = new AdListener() {
        @Override
        public void onAdClosed() {
            logv("onAdClosed");
            closed();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            error("code:" + i);
        }

        @Override
        public void onAdLoaded() {
            logv("onAdLoaded");
            loaded();
        }

        @Override
        public void onAdClicked() {
            logv("onAdClicked");
            clicked();
        }
    };
    private String adUnitId;
    private InterstitialAd ad;
    private boolean anyIdMethodCalled;
    private String testDevice;

    public AdmobAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("Admob", rcEnableKey);
    }

    public AdmobAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public AdmobAdapter withId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }

    public AdmobAdapter addTestDevice(@Size(min = 32, max = 32) String testDevice) {
        this.testDevice = testDevice;
        return this;
    }

    @Override
    protected void init() {
        if (isTestMode()) {
            this.adUnitId = ADMOB_INTERS_TEST_ID;
        }
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        ad = new InterstitialAd(getActivity());
        ad.setAdUnitId(adUnitId);
        ad.setAdListener(ADMOB_AD_LISTENER);
        AdRequest.Builder builder = new AdRequest.Builder();
        if (testDevice != null) {
            RequestConfiguration configuration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(Collections.singletonList(testDevice))
                    .build();
            MobileAds.setRequestConfiguration(configuration);
        }
        ad.loadAd(builder.build());
    }

    @Override
    protected void destroy() {
        super.destroy();
        ad = null;
    }

    @Override
    protected void show() {
        if (ad != null && ad.isLoaded()) {
            ad.show();
        } else {
            closed();
            loge("NOT LOADED");
        }
    }
}