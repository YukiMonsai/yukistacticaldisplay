package yukimonsai.tactical;

import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class NA_SettingsListener implements LunaSettingsListener {

    public static boolean na_combatui_enable = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_enable");
    public static boolean na_combatui_pause = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_pause");
    public static boolean na_combatui_colorblind = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_colorblind");
    public static boolean na_combatui_nocontrol = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_nocontrol");
    public static boolean na_combatui_copyright = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_copyright");
    public static boolean na_combatui_info = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_info");
    public static boolean na_combatui_force = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_force");
    public static boolean na_combatui_enemy = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_enemy");
    public static boolean na_combatui_flux = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flux");
    public static boolean na_combatui_ppt = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_ppt");
    public static boolean na_combatui_noenemyflux = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyflux");
    public static boolean na_combatui_noenemyppt = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyppt");
    public static boolean na_combatui_noenemyinfo = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyinfo");
    public static boolean na_combatui_flip = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flip");
    public static boolean na_combatui_flipv = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flipv");
    public static boolean na_combatui_armor = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_armor");
    public static boolean na_combatui_noenemyarmor = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyarmor");
    public static int na_combatui_hotkey = LunaSettings.getInt("yukistacticaldisplay", "na_combatui_hotkey");
    public static float na_combatui_armorAlpha = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_armorAlpha");




    public static float tacticalRenderHeightOffset = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_height");
    public static float tacticalRenderSideOffset = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_side");
    public static float tacticalRenderHeightOffsetEnemy = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_eheight");
    public static float tacticalRenderSideOffsetEnemy = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_eside");
    public static float na_combatui_size = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_size");
    public static float na_combatui_vspace = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_vspace");
    public static float na_combatui_hspace = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_hspace");








    //Gets called whenever settings are saved in the campaign or the main menu.
    @Override
    public void settingsChanged(String modID) {
        na_combatui_enable = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_enable");
        na_combatui_pause = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_pause");
        na_combatui_colorblind = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_colorblind");
        na_combatui_nocontrol = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_nocontrol");
        na_combatui_copyright = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_copyright");
        na_combatui_info = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_info");
        na_combatui_force = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_force");
        na_combatui_enemy = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_enemy");
        na_combatui_flux = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flux");
        na_combatui_ppt = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_ppt");
        na_combatui_noenemyflux = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyflux");
        na_combatui_noenemyppt = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyppt");
        na_combatui_noenemyinfo = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyinfo");
        tacticalRenderHeightOffset = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_height");
        tacticalRenderSideOffset = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_side");
        tacticalRenderHeightOffsetEnemy = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_eheight");
        tacticalRenderSideOffsetEnemy = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_eside");
        na_combatui_flip = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flip");
        na_combatui_flipv = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_flipv");
        na_combatui_size = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_size");
        na_combatui_vspace = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_vspace");
        na_combatui_hspace = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_hspace");
        na_combatui_hotkey = LunaSettings.getInt("yukistacticaldisplay", "na_combatui_hotkey");
        na_combatui_armor = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_armor");
        na_combatui_noenemyarmor = LunaSettings.getBoolean("yukistacticaldisplay", "na_combatui_noenemyarmor");
        na_combatui_armorAlpha = LunaSettings.getFloat("yukistacticaldisplay", "na_combatui_armorAlpha");
    }
}