package com.schnettler.outlinecolors

object AdvancedConstants {

    // Custom message on theme launch, see theme_strings.xml for changing the dialog content
    // Set SHOW_DIALOG_REPEATEDLY to true if you want the dialog to be showed on every theme launch
    const val SHOW_LAUNCH_DIALOG = false
    const val SHOW_DIALOG_REPEATEDLY = false

    // List of all organization theming systems officially supported by the team
    val ORGANIZATION_THEME_SYSTEMS = arrayOf(
            "projekt.substratum",
            "projekt.substratum.debug",
            "projekt.substratum.lite",
            "projekt.themer"
    )

    // List of other theme systems that are officially unsupported by the team, but fully supported
    // by their corresponding organizations
    val OTHER_THEME_SYSTEMS = arrayOf(
            "com.slimroms.thememanager",
            "com.slimroms.omsbackend"
    )
}