package com.admanager.wastickers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.admanager.wastickers.R;
import com.admanager.wastickers.WastickersApp;
import com.admanager.wastickers.utils.PermissionChecker;

public class WAStickersActivity extends AppCompatActivity {
    PermissionChecker permissionChecker;

    public static void start(Context context) {
        Intent intent = new Intent(context, WAStickersActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wastickers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        permissionChecker = new PermissionChecker(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        WastickersApp.loadAndBindTo(this, permissionChecker, recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}