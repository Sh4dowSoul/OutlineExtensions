package com.schnettler.outlinecolors.telegram;

import android.annotation.SuppressLint;
import android.content.Context;

import com.schnettler.outlinecolors.R;

public class Theme {
    private Context context;
    private String name;

    private boolean isDark;
    private boolean isDynamic;

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
            return context.getResources().getString(R.color.colorAccentDynamic).replace("#ff","#");
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
            return context.getResources().getString(R.color.colorBackgroundDynamic).replace("#ff","#");
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

        result.append(String.format("!\nname: %s\ntime: %s\nauthor: Sh4dowSoul\n@\nbubbleOutline: 0\nwallpaperId: 0\nwallpaperUsageId: 2\nlightStatusBar: %s\nparentTheme: %s\n#\n\nstatusBar: #0000", name, System.currentTimeMillis() / 1000L,isDark() ? 0 : isUseAccentAsPrimary() ? 0 : 1 ,isDark() ? "2" : "11"));

        //Accent
        result.append(Util.arrayToString(context.getResources().getStringArray(R.array.accent)));
        result.append(getAccentMain());

        //Accent Transparent
        result.append(Util.arrayToString(context.getResources().getStringArray(R.array.accent_transparent)));
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
        result.append(Util.arrayToString(context.getResources().getStringArray(isDark() ? R.array.dark_backgrounds : R.array.light_backgrounds)));
        result.append(getBackgroundMain());
        //Background Darker
        result.append(Util.arrayToString(context.getResources().getStringArray(isDark ? R.array.dark_backgrounds_darker : R.array.light_backgrounds_darker)));
        result.append(isDark ? Util.blendColor("#FFFFFF", getBackgroundMain(), 0.05f) : "#F2F3F4");

        //Primary
        result.append(Util.arrayToString(context.getResources().getStringArray(R.array.primary)));
        result.append(isUseAccentAsPrimary() ? getAccentMain() : getBackgroundMain());

        //Text
        result.append(Util.arrayToString(context.getResources().getStringArray(R.array.textColor_bubbleOut)));
        result.append("#FFFFFF");
        result.append(Util.arrayToString(context.getResources().getStringArray(isDark ? R.array.dark_textColor : R.array.light_textColor)));
        result.append(isDark ? "#FFF" : "#0009");

        if (!isDark) {
            //Header Text
            if (!useAccentAsPrimary) {
                result.append(Util.arrayToString(context.getResources().getStringArray(R.array.light_headerPrimary)));
                result.append("#000000B2");
                result.append(Util.arrayToString(context.getResources().getStringArray(R.array.light_headerSecondary)));
                result.append("#0006");
            }
            //Accent Light
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.light_accent)));
            result.append(getAccentMain());
        } else {
            //Accent Light
            result.append(Util.arrayToString(context.getResources().getStringArray(R.array.dark_colorControlNormal)));
            result.append("#BCBCC0");
        }

        return result.toString();
    }
}
