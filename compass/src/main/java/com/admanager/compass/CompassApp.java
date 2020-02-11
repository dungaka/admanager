package com.admanager.compass;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

public class CompassApp {
    private static CompassApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;
    public int compassNeedle;
    public int compassBg;

    CompassApp(Application app, Ads ads, String title, int bgColor, int compassNeedle, int compassBg) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
        this.compassNeedle = compassNeedle;
        this.compassBg = compassBg;
    }

    public static CompassApp getInstance() {
        return INSTANCE;
    }

    private static CompassApp init(CompassApp compassApp) {
        INSTANCE = compassApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;
        private int compassNeedle;
        private int compassBg;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public CompassApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public CompassApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public CompassApp.Builder compassNeedle(@DrawableRes int compassNeedle) {
            Context c = context.get();
            if (c != null) this.compassNeedle = compassNeedle;
            return this;
        }

        public CompassApp.Builder compassBg(@DrawableRes int compassBg) {
            Context c = context.get();
            if (c != null) this.compassBg = compassBg;
            return this;
        }

        public CompassApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new Ads();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            CompassApp.init(new CompassApp(app, ads, title, bgColor, compassNeedle, compassBg));

        }

    }

    public static class Ads {
        public void loadTop(Activity activity, LinearLayout container) {

        }

        public void loadBottom(Activity activity, LinearLayout container) {

        }
    }
}