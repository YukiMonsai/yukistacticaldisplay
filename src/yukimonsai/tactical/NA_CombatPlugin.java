package yukimonsai.tactical;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NA_CombatPlugin implements EveryFrameCombatPlugin {

    // TODO add optional display for other side (for tournaments maybe?)

    public static boolean toggle = true;

    public static Color TEXT_COLOR_OFF = new Color(255, 255, 255, 115);
    public static Color TEXT_COLOR_ON = new Color(255, 255, 255, 255);
    public static Color TEXT_COLOR_HIGHLIGHT = new Color(255, 255, 255, 180);
    public static Color ESCORT_COLOR = new Color(75, 253, 63);

    public static Color ENGAGE_COLOR = new Color(253, 153, 13);
    public static Color ALERT_COLOR = new Color(253, 49, 13);
    public static Color CAP_COLOR = new Color(5, 255, 255);
    public static Color D_COLOR = new Color(14, 148, 253);
    public static Color YOU_COLOR = new Color(255, 255, 255);
    public static Color SND_COLOR = new Color(250, 39, 190);
    public static Color RETREAT_COLOR = new Color(250, 222, 39);


    enum CommandMode {
        RETREAT_COMMAND,
        ESCORT_COMMAND,
        SEARCHANDDESTROY_COMMAND,
        NONE,
    }

    public static CommandMode commandMode = CommandMode.NONE;

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (YukiTacticalPlugin.hasLunaLib) {
            if (NA_SettingsListener.na_combatui_hotkey > 0) {
                if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                        || engine.isPaused()
                )) {
                    for (InputEventAPI e: events) {
                        if (e.isKeyboardEvent()) {
                            if (e.getEventValue() == NA_SettingsListener.na_combatui_hotkey) {
                                toggle = !toggle;
                                e.consume();
                                Global.getSoundPlayer().playUISound("ui_drone_mode_freeroam", 1f, 1f);
                            }
                        }

                    }

                }
            }


            if (!toggle) return;
            if (NA_SettingsListener.na_combatui_enable && !NA_SettingsListener.na_combatui_nocontrol) {
                if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                        || engine.isPaused()
                )) {
                    for (InputEventAPI e: events) {
                        if (e.isMouseDownEvent()) {
                            if (drawNightcrossTactical(true, e, events, 0)) return;
                            if (drawNightcrossTactical(true, e, events, 1)) return;

                            // any clicks outside will reset the command mode
                            commandMode = CommandMode.NONE;
                        }

                    }

                }
            }
        }



    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {

    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    public boolean drawNightcrossTactical(boolean input, InputEventAPI e, List<InputEventAPI> events, int side) {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (!toggle) return false;
        if (engine.isCombatOver() || !((engine.isMission() && engine.getMissionId() != null) || engine.isInCampaign() || engine.isSimulation())) return false;
        if (e != null && e.isConsumed()) return false;

        // get
        List<DeployedFleetMemberAPI> members = engine.getFleetManager(side).getDeployedCopyDFM();
        List<DeployedFleetMemberAPI> capitals = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> cruisers = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> destroyers = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> frigates = new ArrayList<DeployedFleetMemberAPI>();

        // filter
        for (DeployedFleetMemberAPI member : members) {
            if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) capitals.add(member);
            else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.CRUISER) cruisers.add(member);
            else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.DESTROYER) destroyers.add(member);
            else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.FRIGATE) frigates.add(member);
        }

        // build the render list
        List<List<DeployedFleetMemberAPI>> display = new ArrayList<List<DeployedFleetMemberAPI>>();
        display.add(capitals);
        display.add(cruisers);
        display.add(destroyers);
        display.add(frigates);

        // sort by DP
        for (List<DeployedFleetMemberAPI> list : display) {
            list.sort(new Comparator<DeployedFleetMemberAPI>() {
                @Override
                public int compare(DeployedFleetMemberAPI o1, DeployedFleetMemberAPI o2) {
                    boolean retreating1 = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(o1.getShip()) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(o1.getShip()).getType().equals(CombatAssignmentType.RETREAT);
                    boolean retreating2 = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(o2.getShip()) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(o2.getShip()).getType().equals(CombatAssignmentType.RETREAT);
                    if (retreating1 && !retreating2) return 1;
                    if (!retreating1 && retreating2) return -1;
                    return (int) (o1.getMember().getDeploymentPointsCost() - o2.getMember().getDeploymentPointsCost());
                }
            });
        }

        HashMap<String, CombatFleetManagerAPI.AssignmentInfo> escortList = new HashMap<>();



        /*for (List<DeployedFleetMemberAPI> list : display) {
            for (DeployedFleetMemberAPI member : list) {

                boolean retreating = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                        && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.RETREAT);

                boolean escort = !retreating && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                        && (
                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                                || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                                || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.HEAVY_ESCORT)
                );
                if (escort) {
                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                            || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                            || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.HEAVY_ESCORT)) {
                        AssignmentTargetAPI at = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentTargetFor(member.getShip());
                        if (at != null)
                            escortList.put(((DeployedFleetMemberAPI) at).getShip().getId(), Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));
                    }
                }




                XX += Xspacing;
            }
            XX = XXstart;
            YY += Yspacing;
        }*/

        List<CombatFleetManagerAPI.AssignmentInfo> assignments = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAllAssignments();
        for (CombatFleetManagerAPI.AssignmentInfo info : assignments) {
            if (info.getType().equals(CombatAssignmentType.LIGHT_ESCORT)
            || info.getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
            || info.getType().equals(CombatAssignmentType.HEAVY_ESCORT)) escortList.put(((DeployedFleetMemberAPI) info.getTarget()).getShip().getId(), info);
        }




        // render
        float YY = Global.getSettings().getScreenHeightPixels() - NA_SettingsListener.tacticalRenderHeightOffset;
        float Xspacing = side == 0 ? 85 : -85;
        float Yspacing = -90;
        float w = 80;
        float h = 60;
        if (NA_SettingsListener.na_combatui_compact) {
            w = 60f;
            h = 40f;
            Xspacing = side == 0 ? 65 : -65;
            Yspacing = -60f;
        }
        float XXstart = side == 0 ? NA_SettingsListener.tacticalRenderSideOffset :
                Global.getSettings().getScreenWidthPixels() - NA_SettingsListener.tacticalRenderSideOffset - w;
        float XX = XXstart;
        float TEXTHEIGHT = 20;
        float textSpacing = side == 0 ? 100 : -100;
        float TEXTOFF = 20 + h;
        double sineAmt = Math.sin(9f * engine.getTotalElapsedTime(true) % (2*Math.PI));

        if (!NA_SettingsListener.na_combatui_nocontrol) {
            if (Global.getCombatEngine().getPlayerShip().getShipTarget() != null && Global.getCombatEngine().getPlayerShip().getShipTarget().getName() != null) {
                MagicUI.addText(Global.getCombatEngine().getPlayerShip(), Global.getCombatEngine().getPlayerShip().getShipTarget().getName(), TEXT_COLOR_HIGHLIGHT, new Vector2f(XX+12, YY + TEXTOFF + TEXTHEIGHT), false);
            }
            if (input && side == 0) {
                if (e.getX() > XX && e.getX() < XX + textSpacing
                        && e.getY() > YY + TEXTOFF - TEXTHEIGHT && e.getY() < YY + TEXTOFF) {
                    commandMode = CommandMode.RETREAT_COMMAND;
                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    e.consume(); events.remove(e);
                    return true;
                } else if (e.getX() > XX + textSpacing && e.getX() < XX + 2 * textSpacing
                        && e.getY() > YY + TEXTOFF - TEXTHEIGHT && e.getY() < YY + TEXTOFF) {
                    commandMode = CommandMode.ESCORT_COMMAND;
                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    e.consume(); events.remove(e);
                    return true;
                } else if (e.getX() > XX + 2*textSpacing && e.getX() < XX + 3 * textSpacing
                        && e.getY() > YY + TEXTOFF - TEXTHEIGHT && e.getY() < YY + TEXTOFF) {
                    commandMode = CommandMode.SEARCHANDDESTROY_COMMAND;
                    Global.getSoundPlayer().playUISound("ui_button_patrol", 1f, 1f);
                    e.consume(); events.remove(e);
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

                if (Global.getSettings().getMouseX() > XX && Global.getSettings().getMouseX() < XX + textSpacing
                        && Global.getSettings().getMouseY() > YY + TEXTOFF - TEXTHEIGHT && Global.getSettings().getMouseY() < YY + TEXTOFF) {
                    hl_ret = true;
                } else if (Global.getSettings().getMouseX() > XX + textSpacing && Global.getSettings().getMouseX() < XX + 2 * textSpacing
                        && Global.getSettings().getMouseY() > YY + TEXTOFF - TEXTHEIGHT && Global.getSettings().getMouseY() < YY + TEXTOFF) {
                    hl_esc = true;
                } else if (Global.getSettings().getMouseX() > XX + 2*textSpacing && Global.getSettings().getMouseX() < XX + 3 * textSpacing
                        && Global.getSettings().getMouseY() > YY + TEXTOFF - TEXTHEIGHT && Global.getSettings().getMouseY() < YY + TEXTOFF) {
                    hl_snd = true;
                }

                if (!NA_SettingsListener.na_combatui_copyright && Global.getCombatEngine().getPlayerShip().getShipTarget() == null)
                    MagicUI.addText(Global.getCombatEngine().getPlayerShip(), "Nightcross Tactical Display", textColor_OFF, new Vector2f(XX+12, YY + TEXTOFF + TEXTHEIGHT), false);
                MagicUI.addText(Global.getCombatEngine().getPlayerShip(), "Retreat", commandMode == CommandMode.RETREAT_COMMAND ? textColor_ON : hl_ret ? textColor_HL : textColor_OFF, new Vector2f(XX, YY + TEXTOFF), false);
                MagicUI.addText(Global.getCombatEngine().getPlayerShip(), "Escort", commandMode == CommandMode.ESCORT_COMMAND ? textColor_ON : hl_esc ? textColor_HL : textColor_OFF, new Vector2f(XX + textSpacing, YY + TEXTOFF), false);
                MagicUI.addText(Global.getCombatEngine().getPlayerShip(), "S&D", commandMode == CommandMode.SEARCHANDDESTROY_COMMAND ? textColor_ON : hl_snd ? textColor_HL : textColor_OFF, new Vector2f(XX + 2 * textSpacing, YY + TEXTOFF), false);

            }
        }



        for (List<DeployedFleetMemberAPI> list : display) {
            if (list.isEmpty()) continue;
            for (DeployedFleetMemberAPI member : list) {
                if (input) {
                    if (e.getX() > XX && e.getX() < XX + w
                            && e.getY() > YY && e.getY() < YY + h) {

                        if (side == 0 && member.getShip() != engine.getPlayerShip() || commandMode == CommandMode.ESCORT_COMMAND) {
                            if (commandMode == CommandMode.ESCORT_COMMAND) {
                                CombatAssignmentType escortType = CombatAssignmentType.LIGHT_ESCORT;
                                if (member.getShip().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) escortType = CombatAssignmentType.HEAVY_ESCORT;
                                else if (member.getShip().getHullSize() == ShipAPI.HullSize.CRUISER) escortType = CombatAssignmentType.MEDIUM_ESCORT;

                                if (!escortList.containsKey(member.getShip().getId())) {

                                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null) {
                                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                                Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));
                                    }


                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).createAssignment(escortType, member, true);
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                } else if (escortList.containsKey(member.getShip().getId())) {

                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                            escortList.get(member.getShip().getId()));
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                }
                            } else if (commandMode == CommandMode.SEARCHANDDESTROY_COMMAND) {
                                //List<CombatFleetManagerAPI.AssignmentInfo> assignments = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAllAssignments();
                                if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) == null
                                        || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY)) {

                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderSearchAndDestroy(member, true);
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                }
                            } else if (commandMode == CommandMode.RETREAT_COMMAND) {
                                if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) == null
                                        || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.RETREAT)) {

                                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null) {
                                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                                Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));
                                    }


                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderRetreat(member, true, true);
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                }
                            } else if (Global.getCombatEngine().getPlayerShip() != null && member.getShip() != Global.getCombatEngine().getPlayerShip()) {
                                if (member.getShip() != Global.getCombatEngine().getPlayerShip().getShipTarget()) {
                                    Global.getCombatEngine().getPlayerShip().setShipTarget(member.getShip());
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                } else {
                                    Global.getCombatEngine().getPlayerShip().setShipTarget(null);
                                    Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                                }

                            }

                        } else if (side == 1 && Global.getCombatEngine().getPlayerShip() != null && member.getShip() != Global.getCombatEngine().getPlayerShip()) {
                            if (member.getShip() != Global.getCombatEngine().getPlayerShip().getShipTarget()) {
                                Global.getCombatEngine().getPlayerShip().setShipTarget(member.getShip());
                                Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                            } else {
                                Global.getCombatEngine().getPlayerShip().setShipTarget(null);
                                Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                            }

                        }



                        e.consume(); events.remove(e);
                        return true;
                    }
                } else {
                    // icon
                    SpriteAPI sprite = Global.getSettings().getSprite(member.getMember().getHullSpec().getSpriteName());
                    float hp = member.getShip().getHullLevel();
                    float colorScale = 200;

                    Color color = NA_SettingsListener.na_combatui_colorblind ?
                            new Color(250 - (int)(colorScale * 0.8 * hp), 50 + (int)(0.7f * colorScale * hp), 50 + (int)(colorScale * hp), 220)
                            : new Color(250 - (int)(colorScale * 0.9 * hp), 50 + (int)(colorScale * hp), 50, 220);
                    float scalex = (sprite.getWidth() > 0) ? w / sprite.getWidth() : 1f;
                    float scaley = (sprite.getHeight() > 0) ? h/ sprite.getHeight() : 1f;
                    float scale = Math.min(scalex, scaley);

                    sprite.setSize(scale * sprite.getWidth(), scale * sprite.getHeight());
                    sprite.setColor(color);
                    //sprite.setCenter(sprite.getWidth(), sprite.getHeight());


                    boolean retreating = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.RETREAT);

                    boolean escort = !NA_SettingsListener.na_combatui_info && !retreating && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                            && (
                            Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                                    || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                                    || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.HEAVY_ESCORT)
                    );
                    boolean snd = !NA_SettingsListener.na_combatui_info && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY);

                    if (retreating) {
                        sprite.setAlphaMult(0.6f + 0.39f * (float)sineAmt);
                    }

                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setAdditiveBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setNormalBlend();


                    float yyy = 0;
                    if (member.getShip() == Global.getCombatEngine().getPlayerShip()) {
                        MagicUI.addText(member.getShip(), "you", YOU_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                        yyy -= 9;
                    } else if (Global.getCombatEngine().getPlayerShip() != null && Global.getCombatEngine().getPlayerShip().getShipTarget() != null
                        && Global.getCombatEngine().getPlayerShip().getShipTarget().getId().equals(member.getShip().getId())) {

                        SpriteAPI select = Global.getSettings().getSprite("icons","na_icon_select");
                        select.setSize(w, w);
                        if (side == 1) select.setColor(new Color(145, 2, 2, 255));
                        else select.setColor(new Color(50, NA_SettingsListener.na_combatui_colorblind ? 175 : 255, NA_SettingsListener.na_combatui_colorblind ? 255 : 50, 255));
                        select.setAdditiveBlend();
                        select.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                        select.setNormalBlend();
                    }
                    if (side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) {
                        if (snd) {
                            MagicUI.addText(member.getShip(), "S&D", SND_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            yyy -= 9;
                        }
                        if (!NA_SettingsListener.na_combatui_info && escortList.containsKey(member.getShip().getId())) {
                            if (escortList.get(member.getShip().getId()).getAssignedMembers().isEmpty()) {
                                MagicUI.addText(member.getShip(), "-", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            } else
                            if (escortList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT))
                                MagicUI.addText(member.getShip(), "M", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            else if (escortList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.HEAVY_ESCORT))
                                MagicUI.addText(member.getShip(), "H", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            else if (escortList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.LIGHT_ESCORT))
                                MagicUI.addText(member.getShip(), "L", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            yyy -= 9;
                        } else if (!NA_SettingsListener.na_combatui_info) {
                            if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                                    && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType() == CombatAssignmentType.DEFEND) {
                                MagicUI.addText(member.getShip(), "DEF", D_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                yyy -= 9;
                            } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                                    && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType() == CombatAssignmentType.CAPTURE) {
                                MagicUI.addText(member.getShip(), "CAP", CAP_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                yyy -= 9;
                            } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                                    && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType() == CombatAssignmentType.CONTROL) {
                                MagicUI.addText(member.getShip(), "CON", D_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                yyy -= 9;
                            } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                                    && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType() == CombatAssignmentType.ENGAGE) {
                                MagicUI.addText(member.getShip(), "eng", ENGAGE_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                yyy -= 9;
                            }



                        }
                    }

                    if ((side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) && !snd && escort) {


                        if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                                || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                                || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.HEAVY_ESCORT)) {
                            MagicUI.addText(member.getShip(), "esc", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            yyy -= 9;
                        }
                    }

                    if (side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) {
                        if (!NA_SettingsListener.na_combatui_info && retreating) {
                            MagicUI.addText(member.getShip(), "retreat", RETREAT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                            yyy -= 9;
                        } else {
                            if (sineAmt > 0 && member.getShip() != Global.getCombatEngine().getPlayerShip()
                                    && ((member.getShip().getShipAI() != null && member.getShip().getShipAI().getAIFlags() != null && (
                                    member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)
                                            || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                                            || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                                            || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY)
                                            || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)
                            )))) {
                                if (member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)
                                        || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                                        || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                                        || member.getShip().getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY))
                                    MagicUI.addText(member.getShip(), "!!!", ALERT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                else MagicUI.addText(member.getShip(), "!", ENGAGE_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                                yyy -= 9;
                            }
                        }
                    }

                    // bars
                    if ((side == 0 && !NA_SettingsListener.na_combatui_flux) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyflux)) {
                        Color fill = new Color(210, member.getShip().getFluxTracker().isOverloaded() ? 150 : 82, 237, member.getShip().getFluxTracker().isOverloadedOrVenting() ? (int) (200 + 50 * sineAmt) : 255);
                        Color border = new Color(175, 134, 227, 180);
                        //if (NA_SettingsListener.na_combatui_compact)
                        MagicUI.addBar(member.getShip(), member.getShip().getFluxLevel(), fill, border, member.getShip().getHardFluxLevel(), new Vector2f(XX + w * 0.125f, YY - 4), 6, w * 0.75f, true);
                        //else MagicUI.addInterfaceStatusBar(member.getShip(), new Vector2f(XX, YY - 4), member.getShip().getFluxLevel(), fill, border, member.getShip().getHardFluxLevel());

                    }

                    if (((side == 0 && !NA_SettingsListener.na_combatui_ppt) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyppt)) && !retreating) {
                        Color fill2 = new Color(202, 197, 197, member.getShip().getPeakTimeRemaining() < 1f ? (int) (200 + 50 * sineAmt) : 255);
                        Color border2 = new Color(169, 232, 8, 180);
                        float crTimeFrac = member.getShip().getPeakTimeRemaining()/
                                (1f + member.getMember().getStats().getPeakCRDuration().computeEffective(member.getShip().getHullSpec().getNoCRLossTime()));
                        //if (NA_SettingsListener.na_combatui_compact) {
                        MagicUI.addBar(member.getShip(), member.getShip().getCurrentCR(), fill2, border2, crTimeFrac * member.getShip().getCurrentCR(), new Vector2f(XX + w * 0.125f, YY - 12), 6, w*0.75f, true);
                    }


                    //MagicUI.drawSystemBar(member.getShip(), new Vector2f(XX + w/2, YY - 4), crTimeFrac <= 0.1f ? border2 : fill2, crTimeFrac <= 0.1f ? crTimeFrac : member.getShip().getCurrentCR(), 0);
                    //}
                    //else MagicUI.addInterfaceStatusBar(member.getShip(), new Vector2f(XX, YY - 15), member.getShip().getCurrentCR(), fill2, border2, crTimeFrac);
                }


                XX += Xspacing;
            }
            XX = XXstart;
            YY += Yspacing;
        }
        return false;
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {
        if (YukiTacticalPlugin.hasLunaLib && NA_SettingsListener.na_combatui_enable) {
            CombatEngineAPI engine = Global.getCombatEngine();
            if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && engine.getCombatUI() != null && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                    || engine.isPaused()
            )) {
                drawNightcrossTactical(false, null, null, 0);
                if (NA_SettingsListener.na_combatui_enemy)
                    drawNightcrossTactical(false, null, null, 1);

            }
        }
    }

    @Override
    public void init(CombatEngineAPI engine) {

    }
}
