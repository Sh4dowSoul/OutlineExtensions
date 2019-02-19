package com.schnettler.outlinecolors.telegram;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.schnettler.outlinecolors.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TelegramFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    ListPreference listPreferenceBackground;
    ListPreference listPreferenceAccent;
    ListPreference listPreferencePalette;
    SwitchPreferenceCompat dynamicThemeSwitch;
    SharedPreferences sp;
    PreferenceCategory appColorsCategory;
    ListPreference backgroundPreference;
    FloatingActionButton floatingActionButton;

    String fileName = "";
    private static final int STORAGE_REQUEST = 1;

    LinearLayout bubbleOut;
    TextView colorHeader;
    TextView bubbleHeader;
    ConstraintLayout bottomsheet;
    TextView previewBlue;
    TextView previewRed;
    TextView previewGreen;
    TextView previewOrange;
    TextView previewPurple;

    Theme currentTheme;
    BottomSheetBehavior bottomSheetBehavior;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_telegram);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(this);

        listPreferenceBackground = (ListPreference) findPreference(getContext().getResources().getString(R.string.background_key));
        listPreferenceAccent = (ListPreference) findPreference(getContext().getResources().getString(R.string.accent_key));
        listPreferencePalette = (ListPreference) findPreference(getContext().getResources().getString(R.string.palette_key));
        dynamicThemeSwitch = (SwitchPreferenceCompat) findPreference(getContext().getResources().getString(R.string.dynamic_theme_key));
        appColorsCategory = (PreferenceCategory) findPreference("app_colors_key") ;
        PackageManager pm = getContext().getPackageManager();
        boolean supportedThemesInstalled = Util.isPackageInstalled("com.schnettler.outline", pm) | Util.isPackageInstalled("com.schnettler.ethereal", pm);
        dynamicThemeSwitch.setEnabled(dynamicThemeSwitch.isChecked() ? true : supportedThemesInstalled);
        backgroundPreference = (ListPreference) findPreference(getContext().getResources().getString(R.string.background_key));
        if (supportedThemesInstalled | dynamicThemeSwitch.isChecked()){
            //Add Depency
            appColorsCategory.setDependency(getContext().getResources().getString(R.string.dynamic_theme_key));
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Save as");
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
                            .getDisplayMetrics());
            lp.setMargins(margin,0,margin,0);
            final EditText input = new EditText(getContext());
            input.setLayoutParams(lp);
            input.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
            StringBuilder fileNameString = new StringBuilder();
            fileNameString.append(currentTheme.isDark() ? "Ethereal" : "Outline");
            if (!currentTheme.isDynamic()){
                if(currentTheme.isDark()) {
                    fileNameString.append("_" + listPreferenceBackground.getEntry());
                }
                fileNameString.append("_" + listPreferencePalette.getEntry().toString().replace(" ",""));
                fileNameString.append("_" + listPreferenceAccent.getEntry());
                if (currentTheme.isUseAccentAsPrimary()){
                    fileNameString.append("_ColoredToolbar");
                }
            }
            input.setText(fileNameString);
            ll.addView(input, lp);
            builder.setView(ll);

            builder.setPositiveButton("OK", (dialog, which) -> {
                fileName = input.getText().toString();
                createTheme();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        bottomsheet = view.findViewById(R.id.bottom_sheet);
        bubbleOut = view.findViewById(R.id.bubbleOut);
        colorHeader = view.findViewById(R.id.colorHeader);
        bubbleHeader = view.findViewById(R.id.bubbleHeader);
        previewBlue = view.findViewById(R.id.previewBlue);
        previewRed = view.findViewById(R.id.previewRed);
        previewGreen = view.findViewById(R.id.previewGreen);
        previewOrange = view.findViewById(R.id.previewOrange);
        previewPurple = view.findViewById(R.id.previewPurple);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        generatePreview();

        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (getActivity() != null) {
            if(s.equals(getString(R.string.dark_theme_key))) {
                getActivity().recreate();
            } else {
                generatePreview();
            }
        }
    }

    private void getCurrentTheme() {
        if (currentTheme == null) {
            currentTheme = new Theme(getContext());
        }
        currentTheme.setDark(sp.getBoolean(getContext().getResources().getString(R.string.dark_theme_key), false));
        currentTheme.setDynamic(sp.getBoolean(getContext().getResources().getString(R.string.dynamic_theme_key), false));
        currentTheme.setUseAccentAsPrimary(sp.getBoolean(getContext().getResources().getString(R.string.toolbar_key), false));
        currentTheme.setAccentMain(sp.getString(getContext().getResources().getString(R.string.accent_key), "#FFFFFF"));
        currentTheme.setBackgroundMain(currentTheme.isDark() ? sp.getString(getContext().getResources().getString(R.string.background_key), "#f00") : "#FFFFFF");
        currentTheme.setColorPalette(sp.getString(getResources().getString(R.string.palette_key),"#ffffff"));
    }


    @AfterPermissionGranted(STORAGE_REQUEST)
    private void createTheme() {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //Check Permission
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            if (Util.isExternalStorageWritable()) {
                currentTheme.setName(fileName);

                //Create Save Directory
                String path = Environment.getExternalStorageDirectory() + File.separator + "Telegram Themes";
                File folder = new File(path);
                folder.mkdirs();
                File file = new File(folder, fileName + ".tgx-theme");

                String result = currentTheme.toString();

                try {
                    file.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(file);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(result);

                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();

                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Theme saved",
                            Snackbar.LENGTH_LONG).setAction("Open Telegram X", view -> {
                        initShareIntent("challegram", file.getAbsolutePath());
                    }).setAnchorView(floatingActionButton).show();
                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
            } else {
                Log.e("Storage", "No Storage Access");
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Storage access needed to save File", STORAGE_REQUEST, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initShareIntent(String type, String myPath) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        boolean found = false;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getContext().getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(myPath)) ); // Optional, just if you wanna share an image.
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(share, "Select"));
        }
    }

    private void generatePreview() {
        getCurrentTheme();

        listPreferenceBackground.getIcon().setTint(Color.parseColor(currentTheme.getBackgroundMain()));
        listPreferencePalette.getIcon().setTint(Color.parseColor(currentTheme.getBlue()));
        listPreferenceAccent.getIcon().setTint(Color.parseColor(currentTheme.getAccentMain()));
        backgroundPreference.setEnabled(!currentTheme.isDynamic());

        int selected = listPreferenceAccent.findIndexOfValue(listPreferenceAccent.getValue());
        switch (currentTheme.getBlue()) {
            case "#7E8ACD"://Pastel
                listPreferenceAccent.setEntryValues(getContext().getResources().getStringArray(R.array.colorsPastel));
                break;
            case "#A4B0D9"://Light Patel
                listPreferenceAccent.setEntryValues(getContext().getResources().getStringArray(R.array.colorsPastelLight));
                break;
            case "#3F51b5"://Material
                listPreferenceAccent.setEntryValues(getContext().getResources().getStringArray(R.array.colorsMaterial));
                break;
            case "#5C6BC0"://Outline
                listPreferenceAccent.setEntryValues(getContext().getResources().getStringArray(R.array.colorsOutline));
                break;
            case "#64A4D8"://BubbleGum
                listPreferenceAccent.setEntryValues(getContext().getResources().getStringArray(R.array.colorsBubblegum));
                break;
        }
        if (selected != -1) {
            listPreferenceAccent.setValueIndex(selected);
        }

        //Preview Sheet
        int colorAccent = Color.parseColor(currentTheme.getAccentMain());
        int backgroundColor = Color.parseColor(currentTheme.getBackgroundMain());

        bottomsheet.setBackgroundColor(backgroundColor);
        getActivity().getWindow().setNavigationBarColor((currentTheme.isDark() || Build.VERSION.SDK_INT > 26) ? backgroundColor : Color.BLACK);
        colorHeader.setTextColor(colorAccent);
        bubbleHeader.setTextColor(colorAccent);
        bubbleOut.getBackground().setTint(colorAccent);
        previewBlue.getBackground().setTint(Color.parseColor(currentTheme.getBlue()));
        previewRed.getBackground().setTint(Color.parseColor(currentTheme.getRed()));
        previewGreen.getBackground().setTint(Color.parseColor(currentTheme.getGreen()));
        previewOrange.getBackground().setTint(Color.parseColor(currentTheme.getOrange()));
        previewPurple.getBackground().setTint(Color.parseColor(currentTheme.getPurple()));
    }

    public boolean handleBottomSheet() {
        boolean expanded = bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
        if (expanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        return expanded;
    }
}
