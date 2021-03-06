package com.admanager.unity;

import android.text.TextUtils;

import androidx.annotation.Size;

import com.admanager.core.Adapter;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;


public class UnityAdapter extends Adapter {

    private String gameId;
    private String placementId;
    private final IUnityAdsExtendedListener LISTENER = new IUnityAdsExtendedListener() {

        @Override
        public void onUnityAdsReady(String placementId) {
            logv("onUnityAdsReady");
            loaded();
        }

        @Override
        public void onUnityAdsStart(String placementId) {
            logv("onUnityAdsStart");

        }

        @Override
        public void onUnityAdsFinish(String placementId, UnityAds.FinishState finishState) {
            logv("onUnityAdsFinish");
            UnityRouter.removeListener(placementId);
            if (finishState == UnityAds.FinishState.ERROR) {
                error("playback: " + placementId);
            } else {
                closed();
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String message) {
            UnityRouter.removeListener(placementId);
            error("code:" + unityAdsError + " / " + message);
        }


        @Override
        public void onUnityAdsClick(String placementId) {
            logv("onUnityAdsClick");
            clicked();
        }

        @Override
        public void onUnityAdsPlacementStateChanged(String placementId, UnityAds.PlacementState placementState, UnityAds.PlacementState placementState1) {
            logv("onUnityAdsPlacementStateChanged");

        }
    };

    public UnityAdapter(String enableKey) {
        super("Unity", enableKey);
        withTimeout(5_000);
    }

    public UnityAdapter withId(@Size(min = 5, max = 9) String gameId, @Size(min = 2) String placementId) {
        this.gameId = gameId;
        this.placementId = placementId;
        return this;
    }

    @Override
    protected void init() {
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(placementId)) {
            throw new IllegalStateException("call 'withId(gameId, placementId)' method after adapter creation.");
        }
        UnityRouter.addListener(placementId, LISTENER);
        if (!UnityRouter.isInitialized()) {
            UnityRouter.initUnityAds(gameId, getActivity(), isTestMode());
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        UnityRouter.removeListener(placementId);
    }

    @Override
    protected void show() {
        if (UnityAds.isReady(placementId)) {
            UnityRouter.showAd(getActivity(), placementId);
        } else {
            closed();
            loge("NOT LOADED");
        }
    }


}