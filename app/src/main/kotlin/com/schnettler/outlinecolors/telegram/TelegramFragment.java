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
import androidx.core.content.FileProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class TelegramFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference listPreferenceBackground;
    private ListPreference listPreferenceAccent;
    private ListPreference listPreferencePalette;
    private SharedPreferences sp;
    private ListPreference backgroundPreference;
    private FloatingActionButton floatingActionButton;

    private String fileName = "";
    private static final int STORAGE_REQUEST = 1;

    private LinearLayout bubbleOut;
    private TextView colorHeader;
    private TextView bubbleHeader;
    private ConstraintLayout bottomsheet;
    private TextView previewBlue;
    private TextView previewRed;
    private TextView previewGreen;
    private TextView previewOrange;
    private TextView previewPurple;

    private Theme currentTheme;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_telegram);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(this);

        listPreferenceBackground = (ListPreference) findPreference(getContext().getResources().getString(R.string.background_key));
        listPreferenceAccent = (ListPreference) findPreference(getContext().getResources().getString(R.string.accent_key));
        listPreferencePalette = (ListPreference) findPreference(getContext().getResources().getString(R.string.palette_key));
        SwitchPreferenceCompat dynamicThemeSwitch = (SwitchPreferenceCompat) findPreference(getContext().getResources().getString(R.string.dynamic_theme_key));
        PreferenceCategory appColorsCategory = (PreferenceCategory) findPreference("app_colors_key");
        PackageManager pm = getContext().getPackageManager();
        boolean supportedThemesInstalled = Util.isPackageInstalled("com.schnettler.outline", pm) | Util.isPackageInstalled("com.schnettler.ethereal", pm);
        dynamicThemeSwitch.setEnabled(dynamicThemeSwitch.isChecked() || supportedThemesInstalled);
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
            input.setGravity(android.view.Gravity.TOP|android.view.Gravity.START);
            StringBuilder fileNameString = new StringBuilder();
            fileNameString.append(currentTheme.isDark() ? "Ethereal" : "Outline");
            if (!currentTheme.isDynamic()){
                if(currentTheme.isDark()) {
                    fileNameString.append("_").append(listPreferenceBackground.getEntry());
                }
                fileNameString.append("_").append(listPreferencePalette.getEntry().toString().replace(" ", ""));
                fileNameString.append("_").append(listPreferenceAccent.getEntry());
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
        currentTheme.setLegacy(sp.getBoolean("legacy_theme", false));
    }

    private void createTheme() {
        currentTheme.setName(fileName);
        String result = currentTheme.toString();
        try {
            File outputDir = new File(getContext().getCacheDir(), "themes") ;
            outputDir.mkdir();
            File outputFile = new File(outputDir + File.separator + fileName + (currentTheme.isLegacy() ? ".attheme" : ".tgx-theme"));
            FileOutputStream fOut = new FileOutputStream(outputFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(result);
            myOutWriter.close();
            fOut.flush();
            fOut.close();

            Uri contentUri = FileProvider.getUriForFile(getContext(), "com.schnettler.outlinecolors.fileprovider", outputFile);
            initShareIntent(currentTheme.isLegacy() ?"telegram" : "challegram", contentUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initShareIntent(String type, Uri uri) {
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
                    share.putExtra(Intent.EXTRA_STREAM, uri);
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
