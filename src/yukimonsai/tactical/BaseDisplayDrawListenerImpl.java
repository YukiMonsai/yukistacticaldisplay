package yukimonsai.tactical;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;

public class BaseDisplayDrawListenerImpl implements DisplayDrawListener {

    public static Color TEXT_COLOR_OFF = new Color(255, 255, 255, 115);
    public static Color TEXT_COLOR_ON = new Color(255, 255, 255, 255);
    public static Color TEXT_COLOR_HIGHLIGHT = new Color(255, 255, 255, 180);


    @Override
    public boolean draw(NA_CombatPlugin.InputType input, int side, boolean flip, boolean flipv, float XX, float YY, float textxoff, float textoff, float textheight, float titlexoff, float textSpacing, InputEventAPI e) {
        if (!NA_SettingsListener.na_combatui_nocontrol) {
            if (Global.getCombatEngine().getPlayerShip().getShipTarget() != null
                    && Global.getCombatEngine().getPlayerShip().getShipTarget().getOwner() == side
                    && Global.getCombatEngine().getPlayerShip().getShipTarget().getName() != null) {
                MagicUI.addText(null, Global.getCombatEngine().getPlayerShip().getShipTarget().getName(), TEXT_COLOR_HIGHLIGHT, new Vector2f(XX+ textxoff + titlexoff, YY + textoff + textheight), false);
            }
            if (input != NA_CombatPlugin.InputType.NO_INPUT && side == 0) {
                if (e.getX() > XX+ textxoff && e.getX() < XX+ textxoff + textSpacing
                        && e.getY() > YY + textoff - textheight && e.getY() < YY + textoff) {
                    if (input == NA_CombatPlugin.InputType.CLICK ) {
                        if (NA_CombatPlugin.commandMode != NA_CombatPlugin.CommandMode.RETREAT_COMMAND)
                            NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.RETREAT_COMMAND;
                        else NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.NONE;
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                        e.consume();
                    }

                    return true;
                } else if (e.getX() > XX+ textxoff + textSpacing && e.getX() < XX+ textxoff + 2 * textSpacing
                        && e.getY() > YY + textoff - textheight && e.getY() < YY + textoff) {
                    if (input == NA_CombatPlugin.InputType.CLICK ) {

                        if (NA_CombatPlugin.commandMode != NA_CombatPlugin.CommandMode.ESCORT_COMMAND)
                            NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.ESCORT_COMMAND;
                        else NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.NONE;
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                        e.consume();
                    }

                    return true;
                } else if (e.getX() > XX+ textxoff + 2*textSpacing && e.getX() < XX+ textxoff + 3 * textSpacing
                        && e.getY() > YY + textoff - textheight && e.getY() < YY + textoff) {
                    if (input == NA_CombatPlugin.InputType.CLICK ) {

                        if (NA_CombatPlugin.commandMode != NA_CombatPlugin.CommandMode.SEARCHANDDESTROY_COMMAND)
                            NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.SEARCHANDDESTROY_COMMAND;
                        else NA_CombatPlugin.commandMode = NA_CombatPlugin.CommandMode.NONE;
                        Global.getSoundPlayer().playUISound("ui_button_patrol", 1f, 1f);
                        e.consume();
                    }

                    return true;
                }
            } else if (side == 0) {
                Color textColor_OFF = TEXT_COLOR_OFF;
                Color textColor_ON = TEXT_COLOR_ON;
                Color textColor_HL = TEXT_COLOR_HIGHLIGHT;
                if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getCommandPointsLeft() == 0) textColor_ON = textColor_OFF;
                boolean hl_ret = false;
                boolean hl_esc = false;
                boolean hl_snd = false;

                if (Global.getSettings().getMouseX() > XX+ textxoff && Global.getSettings().getMouseX() < XX+ textxoff + textSpacing
                        && Global.getSettings().getMouseY() > YY + textoff - textheight && Global.getSettings().getMouseY() < YY + textoff) {
                    hl_ret = true;
                } else if (Global.getSettings().getMouseX() > XX+ textxoff + textSpacing && Global.getSettings().getMouseX() < XX+ textxoff + 2 * textSpacing
                        && Global.getSettings().getMouseY() > YY + textoff - textheight && Global.getSettings().getMouseY() < YY + textoff) {
                    hl_esc = true;
                } else if (Global.getSettings().getMouseX() > XX+ textxoff + 2*textSpacing && Global.getSettings().getMouseX() < XX+ textxoff + 3 * textSpacing
                        && Global.getSettings().getMouseY() > YY + textoff - textheight && Global.getSettings().getMouseY() < YY + textoff) {
                    hl_snd = true;
                }

                if (NA_SettingsListener.na_combatui_copyright && Global.getCombatEngine().getPlayerShip().getShipTarget() == null
                        || NA_SettingsListener.na_combatui_copyright && Global.getCombatEngine().getPlayerShip().getShipTarget().getOwner() != 0)
                    MagicUI.addText(null, NA_CombatPlugin.title, textColor_OFF, new Vector2f(XX+ textxoff + titlexoff, YY + textoff + textheight), false);
                MagicUI.addText(null, "Retreat", NA_CombatPlugin.commandMode == NA_CombatPlugin.CommandMode.RETREAT_COMMAND ? textColor_ON : hl_ret ? textColor_HL : textColor_OFF,
                        new Vector2f(XX+ textxoff, YY + textoff), false);
                MagicUI.addText(null, "Escort", NA_CombatPlugin.commandMode == NA_CombatPlugin.CommandMode.ESCORT_COMMAND ? textColor_ON : hl_esc ? textColor_HL : textColor_OFF,
                        new Vector2f(XX+ textxoff + textSpacing, YY + textoff), false);
                MagicUI.addText(null, "S&D", NA_CombatPlugin.commandMode == NA_CombatPlugin.CommandMode.SEARCHANDDESTROY_COMMAND ? textColor_ON : hl_snd ? textColor_HL : textColor_OFF,
                        new Vector2f(XX+ textxoff + 2 * textSpacing, YY + textoff), false);

            }
        }

        return false;
    }
}
