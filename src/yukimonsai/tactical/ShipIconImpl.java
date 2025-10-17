package yukimonsai.tactical;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.combat.CombatFleetManager;
import com.fs.starfarer.combat.tasks.CombatTaskManager;
import lunalib.backend.util.ReflectionUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.HashMap;

public class ShipIconImpl implements ShipIcon {
    DeployedFleetMemberAPI member;
    ShipAPI ship;

    // updated when DataUpdate is true
    float armor_l = 0f;
    float armor_r = 0f;
    float armor_u = 0f;
    float armor_d = 0f;

    public static Color ESCORT_COLOR = new Color(75, 253, 63);

    public static Color ENGAGE_COLOR = new Color(253, 153, 13);
    public static Color ALERT_COLOR = new Color(253, 49, 13);
    public static Color CAP_COLOR = new Color(5, 255, 255);
    public static Color D_COLOR = new Color(14, 148, 253);
    public static Color YOU_COLOR = new Color(255, 255, 255);
    public static Color SND_COLOR = new Color(250, 39, 190);
    public static Color RETREAT_COLOR = new Color(250, 222, 39);
    public static float ARMOR_THRESH = 50;
    public static Color GOOD_CR_COLOR = new Color(150, 255, 180);
    public static Color PPT_COLOR = new Color(225, 255, 255);
    public static float ascalemult = 1.0f;


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
    public void render(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, boolean withText, float sineAmt, boolean UIUpdate, boolean DataUpdate) {
        // icon
        ship = member.getShip();
        int side = ship.getOwner();
        SpriteAPI sprite = Global.getSettings().getSprite(member.getMember().getHullSpec().getSpriteName());
        float hp = ship.getHullLevel();
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


        boolean retreating = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.RETREAT);

        boolean escort = !NA_SettingsListener.na_combatui_info && !retreating && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                && (
                Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                        || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                        || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.HEAVY_ESCORT)
        );
        boolean snd = !NA_SettingsListener.na_combatui_info && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY);

        if (retreating) {
            sprite.setAlphaMult(0.6f + 0.39f * (float)sineAmt);
        }

        sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
        sprite.setAdditiveBlend();
        sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
        sprite.setNormalBlend();


        float yyy = 0;
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            MagicUI.addText(ship, "you", YOU_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
            yyy -= 9;
        } else if (Global.getCombatEngine().getPlayerShip() != null && Global.getCombatEngine().getPlayerShip().getShipTarget() != null
                && Global.getCombatEngine().getPlayerShip().getShipTarget().getId().equals(ship.getId())) {

            SpriteAPI select = Global.getSettings().getSprite("icons","na_icon_select");
            select.setSize(w, w);
            if (side == 1) select.setColor(new Color(145, 40, 2, 255));
            else select.setColor(new Color(170, NA_SettingsListener.na_combatui_colorblind ? 175 : 255, NA_SettingsListener.na_combatui_colorblind ? 255 : 50, 255));
            select.setAdditiveBlend();
            select.setAlphaMult(0.7f + 0.25f * sineAmt);
            select.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
            select.setNormalBlend();
        }
        if (side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) {
            if (snd) {
                MagicUI.addText(ship, "S&D", SND_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            }
            if (!NA_SettingsListener.na_combatui_info && assignmentList.containsKey(ship.getId())) {
                if (assignmentList.get(ship.getId()).getAssignedMembers().isEmpty()) {
                    MagicUI.addText(ship, "-", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                } else
                if (assignmentList.get(ship.getId()).getType().equals(CombatAssignmentType.MEDIUM_ESCORT))
                    MagicUI.addText(ship, "M", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                else if (assignmentList.get(ship.getId()).getType().equals(CombatAssignmentType.HEAVY_ESCORT))
                    MagicUI.addText(ship, "H", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                else if (assignmentList.get(ship.getId()).getType().equals(CombatAssignmentType.LIGHT_ESCORT))
                    MagicUI.addText(ship, "L", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            } else if (!NA_SettingsListener.na_combatui_info) {
                if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                        && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType() == CombatAssignmentType.DEFEND) {
                    MagicUI.addText(ship, "DEF", D_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    yyy -= 9;
                } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                        && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType() == CombatAssignmentType.CAPTURE) {
                    MagicUI.addText(ship, "CAP", CAP_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    yyy -= 9;
                } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                        && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType() == CombatAssignmentType.CONTROL) {
                    MagicUI.addText(ship, "CON", D_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    yyy -= 9;
                } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                        && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType() == CombatAssignmentType.ENGAGE) {
                    MagicUI.addText(ship, "eng", ENGAGE_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    yyy -= 9;
                }



            }
        }

        if ((side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) && !snd && escort) {


            if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.LIGHT_ESCORT)
                    || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
                    || Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.HEAVY_ESCORT)) {
                MagicUI.addText(ship, "esc", ESCORT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            }
        }

        if (side == 0 || !NA_SettingsListener.na_combatui_noenemyinfo) {
            if (!NA_SettingsListener.na_combatui_info && retreating) {
                MagicUI.addText(ship, (NA_SettingsListener.na_combatui_hspace + NA_SettingsListener.na_combatui_size < 55) ? ((NA_SettingsListener.na_combatui_hspace + NA_SettingsListener.na_combatui_size <= 46) ? "ret." : "retr.") : "retreat", RETREAT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                yyy -= 9;
            } else if (!NA_SettingsListener.na_combatui_info) {
                if (sineAmt > 0 && ship != Global.getCombatEngine().getPlayerShip()
                        && ((ship.getShipAI() != null && ship.getShipAI().getAIFlags() != null && (
                        ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)
                                || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                                || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                                || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY)
                                || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)
                )))) {
                    if (ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.IN_CRITICAL_DPS_DANGER)
                            || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)
                            || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.RUN_QUICKLY)
                            || ship.getShipAI().getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TURN_QUICKLY))
                        MagicUI.addText(ship, "!!!", ALERT_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    else MagicUI.addText(ship, "!", ENGAGE_COLOR, new Vector2f(XX + 6, YY + h + yyy), false);
                    yyy -= 9;
                }
            }
        }

        // bars
        float off = 0;
        float SPACE = 9;

        if (((side == 0 && !NA_SettingsListener.na_combatui_armor) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyarmor))) {
            if (ship.getArmorGrid() != null && ship.getArmorGrid().getArmorRating() > ARMOR_THRESH) {
                // armor


                float armor = ship.getArmorGrid().getArmorRating();
                float armorThresh = 1f + Math.max(ARMOR_THRESH,  armor*.35f);

                if (DataUpdate) {
                    updateArmor();
                }



                if (armor_l > 0) {
                    float ar = Math.max(0, Math.min(1f, (armor_l-armorThresh)/(armor - armorThresh)));
                    Color armorColor = NA_SettingsListener.na_combatui_colorblind ?
                            new Color(250 - (int)(colorScale * 0.8 * ar), 50 + (int)(0.7f * colorScale * ar), 50 + (int)(colorScale * ar), 220)
                            : new Color(250 - (int)(colorScale * 0.9 * ar), 50 + (int)(colorScale * ar), 50, 220);

                    sprite = Global.getSettings().getSprite("icons", "ytd_armor_l");
                    sprite.setSize(ascalemult*w, ascalemult*h);
                    sprite.setColor(armorColor);
                    if (armor_l < armorThresh)
                        sprite.setAlphaMult(0.59f - 0.4f * sineAmt);
                    else sprite.setAlphaMult(NA_SettingsListener.na_combatui_armorAlpha);
                    sprite.setNormalBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setAdditiveBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                }
                if (armor_r > 0) {
                    float ar = Math.max(0, Math.min(1f, (armor_r-armorThresh)/(armor - armorThresh)));
                    Color armorColor = NA_SettingsListener.na_combatui_colorblind ?
                            new Color(250 - (int)(colorScale * 0.8 * ar), 50 + (int)(0.7f * colorScale * ar), 50 + (int)(colorScale * ar), 220)
                            : new Color(250 - (int)(colorScale * 0.9 * ar), 50 + (int)(colorScale * ar), 50, 220);

                    sprite = Global.getSettings().getSprite("icons", "ytd_armor_r");
                    sprite.setSize(ascalemult*w, ascalemult*h);
                    sprite.setColor(armorColor);
                    if (armor_r < armorThresh)
                        sprite.setAlphaMult(0.59f - 0.4f * sineAmt);
                    else sprite.setAlphaMult(NA_SettingsListener.na_combatui_armorAlpha);
                    sprite.setNormalBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setAdditiveBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                }
                if (armor_u > 0) {
                    float ar = Math.max(0, Math.min(1f, (armor_u-armorThresh)/(armor - armorThresh)));
                    Color armorColor = NA_SettingsListener.na_combatui_colorblind ?
                            new Color(250 - (int)(colorScale * 1.1 * ar), 25 + (int)(0.8f * colorScale * ar), 25 + (int)(1.1*colorScale * ar), 240)
                            : new Color(250 - (int)(colorScale * 1.1 * ar), 25 + (int)(1.1*colorScale * ar), 25, 240);

                    sprite = Global.getSettings().getSprite("icons", "ytd_armor_u");
                    sprite.setSize(ascalemult*w, ascalemult*h);
                    sprite.setColor(armorColor);
                    if (armor_u < armorThresh)
                        sprite.setAlphaMult(0.59f - 0.4f * sineAmt);
                    else sprite.setAlphaMult(NA_SettingsListener.na_combatui_armorAlpha);
                    sprite.setNormalBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setAdditiveBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                }
                if (armor_d > 0) {
                    float ar = Math.max(0, Math.min(1f, (armor_d-armorThresh)/(armor - armorThresh)));
                    Color armorColor = NA_SettingsListener.na_combatui_colorblind ?
                            new Color(250 - (int)(colorScale * 0.8 * ar), 50 + (int)(0.7f * colorScale * ar), 50 + (int)(colorScale * ar), 220)
                            : new Color(250 - (int)(colorScale * 0.9 * ar), 50 + (int)(colorScale * ar), 50, 220);

                    sprite = Global.getSettings().getSprite("icons", "ytd_armor_d");
                    sprite.setSize(ascalemult*w, ascalemult*h);
                    sprite.setColor(armorColor);
                    if (armor_d < armorThresh)
                        sprite.setAlphaMult(0.59f - 0.4f * sineAmt);
                    else sprite.setAlphaMult(NA_SettingsListener.na_combatui_armorAlpha);
                    sprite.setNormalBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                    sprite.setAdditiveBlend();
                    sprite.renderAtCenter(XX + w * 0.5f, YY + h * 0.5f);
                }

            }
        }

        if (((side == 0 && !NA_SettingsListener.na_combatui_ppt) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyppt))) {

            if (!retreating) {
                // dont show bars, do show armor though
                float cr = ship.getCurrentCR();
                float crMinorMal = Global.getSettings().getCRPlugin().getMalfunctionThreshold(ship.getMutableStats());
                float crMinorMaj = Global.getSettings().getCRPlugin().getCriticalMalfunctionThreshold(ship.getMutableStats());

                Color fill2 = new Color(
                        215,
                        ship.getPeakTimeRemaining() < 1 ?
                                (
                                        cr > crMinorMal ? 200 :
                                                (cr > crMinorMaj ? 150 : 50)
                                ) : 225,
                        ship.getPeakTimeRemaining() < 1 ? (
                                cr > crMinorMaj ? 100 : 50
                        )
                                : 197, ship.getPeakTimeRemaining() < 1f ? (int) (200 + 50 * sineAmt) : 225);
                Color border2 = new Color(169, 232, 8, 0); // was 180
                float crTimeFrac = ship.getPeakTimeRemaining()/
                        (1f + member.getMember().getStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime()));

                MagicUI.addBar(ship, cr, fill2, border2, 0, new Vector2f(XX + w * 0.125f, YY - 3 - SPACE), crTimeFrac > 0 ? 3 : 4, w*0.75f, false);
                if (crTimeFrac > 0)
                    MagicUI.addBar(ship, crTimeFrac, PPT_COLOR, border2, 0, new Vector2f(XX + w * 0.125f, YY - 6 - SPACE), 1, w*0.75f, false);
            }

        } else off = -SPACE;

        if ((side == 0 && !NA_SettingsListener.na_combatui_flux) || (side == 1 && !NA_SettingsListener.na_combatui_noenemyflux)) {
            Color fill = new Color(210, ship.getFluxTracker().isOverloaded() ? 150 : 82, 237, ship.getFluxTracker().isOverloadedOrVenting() ? (int) (200 + 50 * sineAmt) : 255);
            Color border = new Color(175, 134, 227, 180);

            MagicUI.addBar(ship, ship.getFluxLevel(), fill, border, ship.getHardFluxLevel(), new Vector2f(XX + w * 0.125f, YY - 4 + off), 6, w * 0.75f, true);

        }
    }

    public void updateArmor() {
        armor_l = ship.getAverageArmorInSlice(ship.getFacing() + 90f, 60);
        armor_r = ship.getAverageArmorInSlice(ship.getFacing() - 90f, 60);
        armor_u = ship.getAverageArmorInSlice(ship.getFacing(), 60);
        armor_d = ship.getAverageArmorInSlice(ship.getFacing() + 180f, 60);
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
                    if (ship.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) escortType = CombatAssignmentType.HEAVY_ESCORT;
                    else if (ship.getHullSize() == ShipAPI.HullSize.CRUISER) escortType = CombatAssignmentType.MEDIUM_ESCORT;

                    if (!assignmentList.containsKey(ship.getId())) {

                        if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null) {
                            Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship));
                        }


                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).createAssignment(escortType, member, true);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } else if (assignmentList.containsKey(ship.getId())) {

                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                assignmentList.get(ship.getId()));
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }
                } else if (commandMode == NA_CombatPlugin.CommandMode.SEARCHANDDESTROY_COMMAND) {
                    //List<CombatFleetManagerAPI.AssignmentInfo> assignments = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAllAssignments();
                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) == null
                            || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY)) {

                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderSearchAndDestroy(member, true);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.SEARCH_AND_DESTROY)) {


                        Object[] arr = new Object[1]; arr[0] = member;
                        ReflectionUtils.invoke("cancelDirectOrdersForMember", Global.getCombatEngine().getFleetManager(side).getTaskManager(false), arr, null, 1);


                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }
                } else if (commandMode == NA_CombatPlugin.CommandMode.RETREAT_COMMAND) {
                    if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) == null
                            || !Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.RETREAT)) {

                        if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null) {
                            Global.getCombatEngine().getFleetManager(side).getTaskManager(false).removeAssignment(
                                    Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship));
                        }


                        Global.getCombatEngine().getFleetManager(side).getTaskManager(false).orderRetreat(member, true, false);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } else if (Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship) != null
                            && Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAssignmentFor(ship).getType().equals(CombatAssignmentType.RETREAT)) {


                        Object[] arr = new Object[1]; arr[0] = member;
                        ReflectionUtils.invoke("cancelDirectOrdersForMember", Global.getCombatEngine().getFleetManager(side).getTaskManager(false), arr, null, 1);


                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }
                } else if (Global.getCombatEngine().getPlayerShip() != null && ship != Global.getCombatEngine().getPlayerShip()) {
                    if (ship != Global.getCombatEngine().getPlayerShip().getShipTarget()) {
                        Global.getCombatEngine().getPlayerShip().setShipTarget(ship);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    } else {
                        Global.getCombatEngine().getPlayerShip().setShipTarget(null);
                        Global.getSoundPlayer().playUISound("ui_button_full_retreat", 1f, 1f);
                    }

                }

            } else if (side == 1 && Global.getCombatEngine().getPlayerShip() != null && ship != Global.getCombatEngine().getPlayerShip()) {
                if (ship != Global.getCombatEngine().getPlayerShip().getShipTarget()) {
                    Global.getCombatEngine().getPlayerShip().setShipTarget(ship);
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

    @Override
    public boolean maintain() {
        // remove if the ship is dead
        return ship.isAlive();
    }
}
