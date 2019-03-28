package com.admanager.admob;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Consts;
import com.admanager.core.NativeLoader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class AdmobNativeLoader extends NativeLoader<AdmobNativeLoader> {

    private static final String TAG = "AdmobNativeLoader";
    private static final String ADMOB_NATIVE_TEST_ID = "ca-app-pub-3940256099942544/2247696110";

    private String adUnitId;
    @LayoutRes
    private int layoutId = R.layout.ad_native_unified;
    private UnifiedNativeAdView unifiedNativeAdView;

    public AdmobNativeLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, adContainer, rcEnableKey);
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        load();
    }

    public void loadWithId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        this.adUnitId = adUnitId;
        load();
    }

    public AdmobNativeLoader withCustomLayout(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public AdmobNativeLoader size(@NonNull NativeType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is not allowed to be null!");
        }
        switch (type) {
            case NATIVE_BANNER:
                this.layoutId = R.layout.ad_native_unified_sm;
                break;
            case NATIVE_XL:
                this.layoutId = R.layout.ad_native_unified_xl;
                break;
            case NATIVE_LARGE:
            default:
                this.layoutId = R.layout.ad_native_unified;
                break;
        }
        return this;
    }

    private void load() {
        if (isTestMode()) {
            this.adUnitId = ADMOB_NATIVE_TEST_ID;
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        // todo check required fields of layout

        unifiedNativeAdView = (UnifiedNativeAdView) getActivity().getLayoutInflater().inflate(layoutId, null);

        if (!super.isEnabled()) {
            return;
        }

        AdLoader mAdLoader = new AdLoader.Builder(getActivity(), this.adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        AdmobAdHelper.populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
                        initContainer(unifiedNativeAdView);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        error("onAdFailedToLoad: " + errorCode);
                    }
                }).build();

        AdRequest.Builder builder = new AdRequest.Builder();
        mAdLoader.loadAd(builder.build());
    }

    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE, NATIVE_XL
    }
}
