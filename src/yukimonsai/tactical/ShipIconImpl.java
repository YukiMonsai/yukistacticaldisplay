package yukimonsai.tactical;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.HashMap;

public class ShipIconImpl implements ShipIcon {
    DeployedFleetMemberAPI member;
    ShipAPI ship;


    public static Color ESCORT_COLOR = new Color(75, 253, 63);

    public static Color ENGAGE_COLOR = new Color(253, 153, 13);
    public static Color ALERT_COLOR = new Color(253, 49, 13);
    public static Color CAP_COLOR = new Color(5, 255, 255);
    public static Color D_COLOR = new Color(14, 148, 253);
    public static Color YOU_COLOR = new Color(255, 255, 255);
    public static Color SND_COLOR = new Color(250, 39, 190);
    public static Color RETREAT_COLOR = new Color(250, 222, 39);


    public ShipIconImpl(DeployedFleetMemberAPI member) {
        this.member = member;
        ship = member.getShip();
    }

    @Override
    public ShipAPI getShip() {
        return ship;
    }

    @Override
    public DeployedFleetMemberAPI get() {
        return member;
    }

    @Override
    public FleetMemberAPI getMember() {
        return member.getMember();
    }

    @Override
    public CombatFleetManagerAPI.AssignmentInfo getAssignment() {
        if (Global.getCombatEngine().getFleetManager(ship.getOwner()) != null) {

        }
        return null;
    }

    @Override
    public AssignmentTargetAPI getAssignmentTarget() {
        CombatFleetManagerAPI.AssignmentInfo assignment = getAssignment();
        return assignment == null ? null : getAssignment().getTarget();
    }

    @Override
    public float getSortValue() {

        CombatFleetManagerAPI.AssignmentInfo assignment = getAssignment();
        if (assignment != null && assignment.getType().equals(CombatAssignmentType.RETREAT)) return -100;

        return member.getMember().getDeploymentPointsCost();
    }

    @Override
    public void render(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, boolean withText, float sineAmt) {
        // icon
        int side = ship.getOwner();
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
            select.setAlphaMult(0.7f + 0.25f * sineAmt);
            select.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
            select.setNormalBlend();
        }
        if (side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) {
            if (snd) {
                MagicUI.addText(member.getShip(), "S&D", SND_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            }
            if (!NA_SettingsListener.na_combatui_info && assignmentList.containsKey(member.getShip().getId())) {
                if (assignmentList.get(member.getShip().getId()).getAssignedMembers().isEmpty()) {
                    MagicUI.addText(member.getShip(), "-", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                } else
                if (assignmentList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT))
                    MagicUI.addText(member.getShip(), "M", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                else if (assignmentList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.HEAVY_ESCORT))
                    MagicUI.addText(member.getShip(), "H", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                else if (assignmentList.get(member.getShip().getId()).getType().equals(CombatAssignmentType.LIGHT_ESCORT))
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
                MagicUI.addText(member.getShip(), (NA_SettingsListener.na_combatui_hspace + NA_SettingsListener.na_combatui_size < 55) ? ((NA_SettingsListener.na_combatui_hspace + NA_SettingsListener.na_combatui_size <= 46) ? "ret." : "retr.") : "retreat", RETREAT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            } else if (!NA_SettingsListener.na_combatui_info) {
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
        float off = 0;

        if (((side == 0 && !NA_SettingsListener.na_combatui_ppt) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyppt)) && !retreating) {
            Color fill2 = new Color(202, 197, 197, member.getShip().getPeakTimeRemaining() < 1f ? (int) (200 + 50 * sineAmt) : 255);
            Color border2 = new Color(169, 232, 8, 180);
            float crTimeFrac = member.getShip().getPeakTimeRemaining()/
                    (1f + member.getMember().getStats().getPeakCRDuration().computeEffective(member.getShip().getHullSpec().getNoCRLossTime()));

            MagicUI.addBar(member.getShip(), member.getShip().getCurrentCR(), fill2, border2, crTimeFrac * member.getShip().getCurrentCR(), new Vector2f(XX + w * 0.125f, YY - 12), 6, w*0.75f, true);
        } else off = -8;

        if ((side == 0 && !NA_SettingsListener.na_combatui_flux) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyflux)) {
            Color fill = new Color(210, member.getShip().getFluxTracker().isOverloaded() ? 150 : 82, 237, member.getShip().getFluxTracker().isOverloadedOrVenting() ? (int) (200 + 50 * sineAmt) : 255);
            Color border = new Color(175, 134, 227, 180);

            MagicUI.addBar(member.getShip(), member.getShip().getFluxLevel(), fill, border, member.getShip().getHardFluxLevel(), new Vector2f(XX + w * 0.125f, YY - 4 + off), 6, w * 0.75f, true);

        }
    }

    @Override
    public boolean handleInput(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, InputEventAPI e) {
        // input
        if (e.getX() > XX && e.getX() < XX + w
                && e.getY() > YY && e.getY() < YY + h) {
            CombatEngineAPI engine = Global.getCombatEngine();
            int side = ship.getOwner();
            NA_CombatPlugin.CommandMode commandMode = NA_CombatPlugin.commandMode;

            if (side == 0) {
                if (commandMode == NA_CombatPlugin.CommandMode.ESCORT_COMMAND) {
                    CombatAssignmentType escortType = CombatAssignmentType.LIGHT_ESCORT;
                    if (member.getShip().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) escortType = CombatAssignmentType.HEAVY_ESCORT;
                    else if (member.getShip().getHullSize() == ShipAPI.HullSize.CRUISER) escortType = CombatAssignmentType.MEDIUM_ESCORT;

                    if (!assignmentList.containsKey(member.getShip().getId())) {

                        if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null) {
                            Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));
                        }


                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).createAssignment(escortType, member, true);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } else if (assignmentList.containsKey(member.getShip().getId())) {

                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                assignmentList.get(member.getShip().getId()));
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }
                } else if (commandMode == NA_CombatPlugin.CommandMode.SEARCHANDDESTROY_COMMAND) {
                    //List<CombatFleetManagerAPI.AssignmentInfo> assignments = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAllAssignments();
                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) == null
                            || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY)) {

                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderSearchAndDestroy(member, true);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }
                } else if (commandMode == NA_CombatPlugin.CommandMode.RETREAT_COMMAND) {
                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) == null
                            || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.RETREAT)) {

                        if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null) {
                            Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));
                        }


                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderRetreat(member, true, true);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } /*else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()).getType().equals(CombatAssignmentType.RETREAT)) {

                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(member.getShip()));

                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }*/
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



            e.consume(); //events.remove(e);
            return true;
        }

        return false;
    }


    @Override
    public boolean handleHold(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, InputEventAPI e) {
        // input
        if (e.getX() > XX && e.getX() < XX + w
                && e.getY() > YY && e.getY() < YY + h) {
            //e.consume(); //events.remove(e);
            return true;
        }

        return false;
    }
}
