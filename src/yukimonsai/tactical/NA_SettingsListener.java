package yukimonsai.tactical;

import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;

public class NA_SettingsListener implements LunaSettingsListener {

    public static boolean na_combatui_enable = LunaSettings.getBoolean("Nightcross", "na_combatui_enable");
    public static boolean na_combatui_pause = LunaSettings.getBoolean("Nightcross", "na_combatui_pause");
    public static boolean na_combatui_compact = LunaSettings.getBoolean("Nightcross", "na_combatui_compact");
    public static boolean na_combatui_colorblind = LunaSettings.getBoolean("Nightcross", "na_combatui_colorblind");
    public static boolean na_combatui_nocontrol = LunaSettings.getBoolean("Nightcross", "na_combatui_nocontrol");
    public static boolean na_combatui_copyright = LunaSettings.getBoolean("Nightcross", "na_combatui_copyright");
    public static boolean na_combatui_info = LunaSettings.getBoolean("Nightcross", "na_combatui_info");
    public static boolean na_combatui_bars = LunaSettings.getBoolean("Nightcross", "na_combatui_bars");
    public static boolean na_combatui_force = LunaSettings.getBoolean("Nightcross", "na_combatui_force");
    public static boolean na_combatui_enemy = LunaSettings.getBoolean("Nightcross", "na_combatui_enemy");

    public static float tacticalRenderHeightOffset = LunaSettings.getFloat("Nightcross", "na_combatui_height");
    public static float tacticalRenderSideOffset = LunaSettings.getFloat("Nightcross", "na_combatui_side");








    //Gets called whenever settings are saved in the campaign or the main menu.
    @Override
    public void settingsChanged(String modID) {
        na_combatui_enable = LunaSettings.getBoolean("Nightcross", "na_combatui_enable");
        na_combatui_pause = LunaSettings.getBoolean("Nightcross", "na_combatui_pause");
        na_combatui_compact = LunaSettings.getBoolean("Nightcross", "na_combatui_compact");
        na_combatui_colorblind = LunaSettings.getBoolean("Nightcross", "na_combatui_colorblind");
        na_combatui_nocontrol = LunaSettings.getBoolean("Nightcross", "na_combatui_nocontrol");
        na_combatui_copyright = LunaSettings.getBoolean("Nightcross", "na_combatui_copyright");
        na_combatui_info = LunaSettings.getBoolean("Nightcross", "na_combatui_info");
        na_combatui_bars = LunaSettings.getBoolean("Nightcross", "na_combatui_bars");
        na_combatui_force = LunaSettings.getBoolean("Nightcross", "na_combatui_force");
        na_combatui_enemy = LunaSettings.getBoolean("Nightcross", "na_combatui_enemy");
        tacticalRenderHeightOffset = LunaSettings.getFloat("Nightcross", "na_combatui_height");
        tacticalRenderSideOffset = LunaSettings.getFloat("Nightcross", "na_combatui_side");
    }
}