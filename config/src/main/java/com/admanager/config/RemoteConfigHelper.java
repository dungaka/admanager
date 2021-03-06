package com.admanager.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Gust on 10.04.2018.
 */

public class RemoteConfigHelper implements OnCompleteListener<Void> {
    private static final String TAG = "RemoteConfigHelper";

    private static RemoteConfigHelper instance;
    private final FirebaseRemoteConfig mRemoteConfig;
    private boolean adsEnabled = true;
    private boolean testMode;
    private WeakReference<Context> context;

    private RemoteConfigHelper(Map<String, Object> defaultMap, boolean isDeveloperModeEnabled) {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(defaultMap);
        int minimumFetchIntervalInSeconds = isDeveloperModeEnabled ? 0 : 10800;
        mRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(minimumFetchIntervalInSeconds).build());
        mRemoteConfig.fetch(minimumFetchIntervalInSeconds).addOnCompleteListener(this);
        testMode = isDeveloperModeEnabled;
        if (!testMode) { // don't activate server based values in debug mode
            mRemoteConfig.activate();
        }
    }

    public static boolean areAdsEnabled() {
        return getInstance().adsEnabled;
    }

    public static void setAdsEnabled(boolean adsEnabled) {
        getInstance().adsEnabled = adsEnabled;
        if (!adsEnabled) {
            disableTestMode();
        }
    }

    public static boolean isTestMode() {
        return getInstance().testMode;
    }

    public static void disableTestMode() {
        getInstance().testMode = false;
    }

    public static RemoteConfigHelper init(Context context) {
        return init(context, false);
    }

    public static RemoteConfigHelper init(Context context, boolean reload) {
        if (instance == null || reload) {
            synchronized (RemoteConfigHelper.class) {
                if (instance == null || reload) {
                    if (context != null) {
                        FirebaseApp.initializeApp(context);
                    }

                    boolean debug = context != null && (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
                    instance = init(RemoteConfigApp.getInstance().getDefaults(), debug, reload);
                }
            }
        }
        instance.context = new WeakReference<>(context);
        return instance;
    }

    private static RemoteConfigHelper init(Map<String, Object> defaultMap, boolean idDeveloperModeEnabled, boolean reload) {
        if (instance == null || reload) {
            synchronized (RemoteConfigHelper.class) {
                if (instance == null || reload) {
                    instance = new RemoteConfigHelper(defaultMap, idDeveloperModeEnabled);
                }
            }
        }
        return instance;
    }

    private static RemoteConfigHelper getInstance() {
        if (instance == null) {
            Log.e(TAG, "Not initialized yet! Call init() method first");
            init(null);
        }
        return instance;
    }

    public static FirebaseRemoteConfig getConfigs() {
        return getInstance().mRemoteConfig;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            if (isTestMode()) { // don't activate server based values in debug mode
                Log.i(TAG, "remote configs fetched");
                initPeriodicNotifIfExist();
            } else {
                mRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        Log.i(TAG, "remote configs fetched");
                        initPeriodicNotifIfExist();
                    }
                });
            }
        }
    }

    @NonNull
    private void initPeriodicNotifIfExist() {
        try {
            Class<?> cls = Class.forName("com.admanager.periodicnotification.PeriodicNotification");
            Method init = cls.getMethod("init", Context.class);

            if (context != null) {
                Context c = context.get();
                if (c != null) {
                    init.invoke(null, c);
                }
                Log.d(TAG, "Periodic Notification init by reflection");
            }
        } catch (ClassNotFoundException ignore) {
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
