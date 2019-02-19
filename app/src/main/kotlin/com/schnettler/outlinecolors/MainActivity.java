package com.schnettler.outlinecolors;

import android.os.Bundle;
import android.preference.PreferenceManager;

import com.schnettler.outlinecolors.telegram.TelegramFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new TelegramFragment());
        ft.commit();

        getSupportActionBar().setTitle("Telegram X Generator");
    }

    @Override
    public void setTheme(int resId) {
        super.setTheme(resId);
        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ? MODE_NIGHT_YES : MODE_NIGHT_NO);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof TelegramFragment) {
            if (!((TelegramFragment) fragment).handleBottomSheet()) {   //If was not expanded
                super.onBackPressed();
            }
        }
    }
}