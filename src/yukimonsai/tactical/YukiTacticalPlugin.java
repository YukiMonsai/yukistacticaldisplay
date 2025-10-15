package yukimonsai.tactical;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import lunalib.lunaSettings.LunaSettings;

public class YukiTacticalPlugin extends BaseModPlugin {

    public static boolean hasMagicLib = false;
    public static boolean hasLunaLib = false;


    private static void initNA() {

    }
    @Override
    public void onApplicationLoad() {


        {

            hasLunaLib = Global.getSettings().getModManager().isModEnabled("lunalib");
            if (hasLunaLib) {
                LunaSettings.addSettingsListener(new NA_SettingsListener());
            }
            hasMagicLib = Global.getSettings().getModManager().isModEnabled("MagicLib");
            if (!hasMagicLib) {
                throw new RuntimeException("Yuki's Tactical Display requires MagicLib!" +
                        "\nGet it at http://fractalsoftworks.com/forum/index.php?topic=13718.0");
            }

        }


    }


    @Override
    public void onNewGame() {

    }


    @Override
    public void onGameLoad(boolean newGame) {

    }


    @Override
    public void onNewGameAfterEconomyLoad() {
    }

}

