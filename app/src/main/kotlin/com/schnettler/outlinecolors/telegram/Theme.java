package com.schnettler.outlinecolors.telegram;

import android.annotation.SuppressLint;
import android.content.Context;

import com.schnettler.outlinecolors.R;

public class Theme {
    private Context context;
    private String name;

    private boolean isDark;
    private boolean isDynamic;
    private boolean legacy;

    private String accentMain;
    private boolean useAccentAsPrimary;

    private String[] colorPalette;

    private String backgroundMain;

    public Theme(Context context) {
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.isDynamic = dynamic;
    }

    @SuppressLint("ResourceType")
    public String getAccentMain() {
        if (isDynamic) {
            return context.getResources().getString(context.getResources().getIdentifier("accent_material_dark", "color", "android")).replace("#ff","#");
        }
        return accentMain;
    }

    public void setAccentMain(String accentMain) {
        this.accentMain = accentMain;
    }

    public boolean isUseAccentAsPrimary() {
        return useAccentAsPrimary;
    }

    public void setUseAccentAsPrimary(boolean useAccentAsPrimary) {
        this.useAccentAsPrimary = useAccentAsPrimary;
    }

    @SuppressLint("ResourceType")
    public String getBackgroundMain() {
        if (isDark && isDynamic) {
            return context.getResources().getString(context.getResources().getIdentifier("background_material_dark", "color", "android")).replace("#ff","#");
        }
        return backgroundMain;
    }

    public void setBackgroundMain(String backgroundMain) {
        this.backgroundMain = backgroundMain;
    }

    public void setColorPalette(String colorBlue) {
        switch (colorBlue) {
            case "#7E8ACD"://Pastel
                colorPalette = context.getResources().getStringArray(R.array.colorsPastel);
                break;
            case "#A4B0D9"://Light Patel
                colorPalette = context.getResources().getStringArray(R.array.colorsPastelLight);
                break;
            case "#3F51b5"://Material
                colorPalette = context.getResources().getStringArray(R.array.colorsMaterial);
                break;
            case "#5C6BC0":
                //Outline
                colorPalette = context.getResources().getStringArray(R.array.colorsOutline);
                break;
            case "#64A4D8"://BubbleGum
                colorPalette = context.getResources().getStringArray(R.array.colorsBubblegum);
                break;
        }
    }

    @SuppressLint("ResourceType")
    public String getRed() {
        if (isDynamic) {
            return context.getResources().getString(android.R.color.holo_red_light).replace("#ff", "#");
        }
        return colorPalette[0];
    }

    @SuppressLint("ResourceType")
    public String getBlue() {
        if (isDynamic) {
            return context.getResources().getString(android.R.color.holo_blue_light).replace("#ff", "#");
        }
        return colorPalette[2];
    }

    @SuppressLint("ResourceType")
    public String getGreen() {
        if (isDynamic) {
            return context.getResources().getString(android.R.color.holo_green_light).replace("#ff", "#");
        }
        return colorPalette[1];
    }

    @SuppressLint("ResourceType")
    public String getOrange() {
        if (isDynamic) {
            return context.getResources().getString(android.R.color.holo_orange_light).replace("#ff", "#");
        }
        return colorPalette[3];
    }

    @SuppressLint("ResourceType")
    public String getPurple() {
        if (isDynamic) {
            return context.getResources().getString(android.R.color.holo_purple).replace("#ff", "#");
        }
        return colorPalette[4];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (!legacy) {
            result.append(String.format("!\nname: %s\ntime: %s\nauthor: Sh4dowSoul\n@\nbubbleOutline: 0\nwallpaperId: 0\nwallpaperUsageId: 2\nlightStatusBar: %s\nparentTheme: %s\n#\n\nstatusBar: #0000", name, System.currentTimeMillis() / 1000L, isDark() ? 0 : isUseAccentAsPrimary() ? 0 : 1, isDark() ? "2" : "11"));

            //Accent
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.accent)));
            result.append(getAccentMain());

            //Accent Transparent
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.accentBlend)));
            result.append(Util.blendColor(getAccentMain(), getBackgroundMain(), isDark ? 0.2f : 0.6f));

            //Color Palette
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.red)));
            result.append(getRed());
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.green)));
            result.append(getGreen());
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.blue)));
            result.append(getBlue());
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.orange)));
            result.append(getOrange());
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.purple)));
            result.append(getPurple());


            //Background
            result.append(Util.arrayToString(context.getResources().getStringArray(isDark() ? R.array.darkBackground : R.array.lightBackground)));
            result.append(getBackgroundMain());

            //Background Darker
            result.append(Util.arrayToString(context.getResources().getStringArray(isDark ? R.array.darkBackgroundElevated : R.array.lightBackgroundElevated)));
            result.append(isDark ? Util.blendColor("#FFFFFF", getBackgroundMain(), 0.05f) : "#F2F3F4");

            //Primary
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.primary)));
            result.append(isUseAccentAsPrimary() ? getAccentMain() : getBackgroundMain());

            //Text
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.textWhite)));
            result.append("#FFFFFF");
            result.append(Util.arrayToString(context.getResources().getStringArray(isDark ? R.array.darkPrimaryText : R.array.light_textColor)));
            result.append(isDark ? "#FFF" : "#0009");

            if (!isDark) {
                //Header Text
                if (!useAccentAsPrimary) {
                    result.append(Util.arrayToString(context.getResources().getStringArray(R.array.lightPrimaryText)));
                    result.append("#000000B2");
                    result.append(Util.arrayToString(context.getResources().getStringArray(R.array.lightSecondaryText)));
                    result.append("#0006");
                }
                //Accent Light
                result.append(Util.arrayToString(context.getResources().getStringArray(R.array.lightAccent)));
                result.append(getAccentMain());
            } else {
                //Accent Light
                result.append(Util.arrayToString(context.getResources().getStringArray(R.array.darkSecondaryText)));
                result.append("#BCBCC0");
            }
        } else {
            //Primary
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyPrimary), isUseAccentAsPrimary() ? getAccentMain() : getBackgroundMain()));

            //Accent
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyAccent), getAccentMain()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyAccentBlend), Util.blendColor(getAccentMain(), getBackgroundMain(), isDark ? 0.2f : 0.6f)));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyAccentTrans), getAccentMain().replace("#", "#33")));

            //Background
            result.append(Util.createLegacyLines(context.getResources().getStringArray(isDark() ? R.array.legacyDarkBackground : R.array.legacyLightBackground), getBackgroundMain()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(isDark() ? R.array.legacyDarkBackgroundElevated : R.array.legacyLightBackgroundElevated), isDark() ? Util.blendColor("#FFFFFF", getBackgroundMain(), 0.05f) : "#F2F3F4"));

            //Text & Icons
            result.append(Util.createLegacyLines(context.getResources().getStringArray(isDark() ? R.array.legacyDarkPrimaryText : R.array.light_textColor), isDark() ? "#FFFFFFFF" : "#DE000000"));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyTextWhite),"#FFFFFF" ));
            if (isDark()){
                result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyDarkSecondaryText),"#80FFFFFF"));
                result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyDarkShadow),"#00FFFFFF"));
            } else {
                if (!useAccentAsPrimary) {
                    result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyLightPrimaryText),"#B2000000"));
                    result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyLightSecondaryText), "#80000000"));
                }
                result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyLightAccent), getAccentMain()));
            }

            //Colors
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyRed), getRed()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyGreen), getGreen()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyBlue), getBlue()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyOrange), getOrange()));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyPurple), getPurple()));
            //Colors Transparent
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyRedTrans), getRed().replace("#", "#33")));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyBlueTrans), getBlue().replace("#", "#33")));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyGreenTrans), getGreen().replace("#", "#33")));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyOrangeTrans), getOrange().replace("#", "#33")));
            result.append(Util.createLegacyLines(context.getResources().getStringArray(R.array.legacyPurpleTrans), getPurple().replace("#", "#33")));
        }
        return result.toString();
    }

    public boolean isLegacy() {
        return legacy;
    }

    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

}
