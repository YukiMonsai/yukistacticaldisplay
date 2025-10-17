package yukimonsai.tactical;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.input.InputEventType;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NA_CombatPlugin implements EveryFrameCombatPlugin {

    public static boolean toggle = true;

    public static String title = "Nightcross Tactical Display";

    public static HashMap<DeployedFleetMemberAPI, ShipIcon>[] iconMap = new HashMap[2];
    static {
        iconMap[0] = new HashMap<DeployedFleetMemberAPI, ShipIcon>();
        iconMap[1] = new HashMap<DeployedFleetMemberAPI, ShipIcon>();
    }

    public static List<GetShipIconListener> GetShipIconListeners = new ArrayList<GetShipIconListener>();
    static {
        GetShipIconListeners.add(new GetShipIconModuleExcluder());
        GetShipIconListeners.add(new GetShipIconDeadExcluder());
}
    public static List<DisplayDrawListener> DisplayDrawListeners = new ArrayList<DisplayDrawListener>();
    static {
        DisplayDrawListeners.add(new BaseDisplayDrawListenerImpl());
    }

    public static float uiRefreshTime = 0.1f;
    public static boolean refreshUI = true;
    public static float lastUIUpdateTime = 0;
    public static IntervalUtil uiRefreshTimer = new IntervalUtil(uiRefreshTime, uiRefreshTime);

    public static float dataRefreshTime = 0.1f;
    public static boolean refreshData = true;
    public static IntervalUtil dataRefreshTimer = new IntervalUtil(dataRefreshTime, dataRefreshTime);

    public static float max_size = 100;
    public static float min_size = 30;

    public enum InputType {
        NO_INPUT,
        CLICK,
        HOLD,
    }
    enum CommandMode {
        RETREAT_COMMAND,
        ESCORT_COMMAND,
        SEARCHANDDESTROY_COMMAND,
        NONE,
    }

    public void addGetShipIconListener(GetShipIconListener listener) {
        GetShipIconListeners.add(listener);
    }
    public boolean hasGetShipIconListenerOfClass(Class cls) {
        for (GetShipIconListener listener : GetShipIconListeners) {
            if (listener.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }
    public GetShipIconListener removeGetShipIconListenerOfClass(Class cls) {
        GetShipIconListener toDelete = null;
        for (GetShipIconListener listener : GetShipIconListeners) {
            if (listener.getClass().equals(cls)) {
                toDelete = listener;
                break;
            }
        }
        if (toDelete != null) GetShipIconListeners.remove(toDelete);
        return toDelete;
    }

    public void addDisplayDrawListener(DisplayDrawListener listener) {
        DisplayDrawListeners.add(listener);
    }
    public boolean hasDisplayDrawListenerOfClass(Class cls) {
        for (DisplayDrawListener listener : DisplayDrawListeners) {
            if (listener.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }
    public DisplayDrawListener removeDisplayDrawListenerOfClass(Class cls) {
        DisplayDrawListener toDelete = null;
        for (DisplayDrawListener listener : DisplayDrawListeners) {
            if (listener.getClass().equals(cls)) {
                toDelete = listener;
                break;
            }
        }
        if (toDelete != null) DisplayDrawListeners.remove(toDelete);
        return toDelete;
    }

    public static CommandMode commandMode = CommandMode.NONE;
    public static boolean mouseDown = false;

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {
        if (mouseDown) {
            // jank
            for (InputEventAPI e: events) {
                if (!e.isConsumed() && e.isMouseUpEvent()) {
                    mouseDown = false;
                    break;
                }
            }
        }
        CombatEngineAPI engine = Global.getCombatEngine();
        if (YukiTacticalPlugin.hasLunaLib) {
            if (NA_SettingsListener.na_combatui_hotkey > 0) {
                if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && engine.getCombatUI() != null && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                        || engine.isPaused()
                )) {
                    for (InputEventAPI e: events) {
                        if (!e.isConsumed() && e.isKeyDownEvent()) {
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


                if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && engine.getCombatUI() != null && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                        || engine.isPaused()
                )) {
                    InputEventAPI cancel = null;
                    for (InputEventAPI e: events) {
                        if (!e.isConsumed() && ((e.isLMBDownEvent()) || (e.isMouseMoveEvent() && mouseDown))) {
                            // any clicks outside will reset the command mode
                            if (drawYukiTacticalDisplay(e.getEventType().equals(InputEventType.MOUSE_DOWN) ? InputType.CLICK : InputType.HOLD, e, events, 0)) {
                                cancel = e;
                                break;
                            }
                            if (drawYukiTacticalDisplay(e.isMouseDownEvent() ? InputType.CLICK : InputType.HOLD, e, events, 1)) {cancel = e; break;}


                            if (e.isMouseDownEvent())
                                commandMode = CommandMode.NONE;
                        } else if (!e.isLMBDownEvent() && e.isMouseDownEvent())
                            commandMode = CommandMode.NONE;

                    }
                    if (cancel != null) {
                        if (cancel.isConsumed())
                            events.remove(cancel);
                        if (cancel.isMouseDownEvent()) mouseDown = true;
                        Global.getCombatEngine().getCombatUI().setDisablePlayerShipControlOneFrame(true);
                    }

                }
            }
        }



    }

    static boolean autoPilotWasOn = false;

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (autoPilotWasOn && !Global.getCombatEngine().isUIAutopilotOn()) {
            //((CombatState) Global.getCombatEngine()).setAutopilot(true);
            autoPilotWasOn = false;
        }
        if (dataRefreshTimer.intervalElapsed()) {
            dataRefreshTimer.setElapsed(0);
            refreshData = true;
        } else dataRefreshTimer.advance(amount);
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    public ShipIcon getIconClassForMember(DeployedFleetMemberAPI member) {
        float bestScore = 0;
        GetShipIconListener bestListener = null;
        for (GetShipIconListener listener : GetShipIconListeners) {
            float score = listener.getScore(member);
            if (score > bestScore) {
                bestScore = score;
                bestListener = listener;
            }
        }
        if (bestListener != null) {
            return bestListener.getShipIcon(member);
        }
        return new ShipIconImpl(member);
    }

    List<List<DeployedFleetMemberAPI>> getMemberDisplayList(int side) {
        CombatEngineAPI engine = Global.getCombatEngine();
        List<DeployedFleetMemberAPI> members = engine.getFleetManager(side).getDeployedCopyDFM();
        List<DeployedFleetMemberAPI> capitals = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> cruisers = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> destroyers = new ArrayList<DeployedFleetMemberAPI>();
        List<DeployedFleetMemberAPI> frigates = new ArrayList<DeployedFleetMemberAPI>();

        HashMap<DeployedFleetMemberAPI, ShipIcon> newIconMap = new HashMap<DeployedFleetMemberAPI, ShipIcon>();
        // filter
        for (DeployedFleetMemberAPI member : members) {
            if (member.getMember().isAlly()) continue; // allies manage themselves
            if (iconMap[side].get(member) != null && iconMap[side].get(member).maintain()) {
                newIconMap.put(member, iconMap[side].get(member));
            } else {
                ShipIcon icon = getIconClassForMember(member);
                if (icon != null)
                    newIconMap.put(member, icon);
            }
            if (iconMap[side].containsKey(member)) {
                if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) capitals.add(member);
                else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.CRUISER) cruisers.add(member);
                else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.DESTROYER) destroyers.add(member);
                else if (member.getMember().getHullSpec().getHullSize() == ShipAPI.HullSize.FRIGATE) frigates.add(member);
            }


        }
        iconMap[side] = newIconMap;

        // build the render list
        List<List<DeployedFleetMemberAPI>> display = new ArrayList<List<DeployedFleetMemberAPI>>();
        display.add(capitals);
        display.add(cruisers);
        display.add(destroyers);
        display.add(frigates);

        return display;
    }

    public boolean drawYukiTacticalDisplay(NA_CombatPlugin.InputType input, InputEventAPI e, List<InputEventAPI> events, int side) {
        CombatEngineAPI engine = Global.getCombatEngine();

        if (!toggle) return false;
        if (engine.isCombatOver() || !((engine.isMission() && engine.getMissionId() != null) || engine.isInCampaign() || engine.isSimulation())) return false;
        if (e != null && e.isConsumed()) return false;

        boolean flip = side == 1;
        boolean flipv = NA_SettingsListener.na_combatui_flipv;
        if (NA_SettingsListener.na_combatui_flip) flip = !flip;

        if (side == 1 && input != InputType.NO_INPUT && !NA_SettingsListener.na_combatui_enemy) return false;


        // get
        List<List<DeployedFleetMemberAPI>> display = getMemberDisplayList(side);

        // sort by DP
        for (List<DeployedFleetMemberAPI> list : display) {
            list.sort(new Comparator<DeployedFleetMemberAPI>() {
                @Override
                public int compare(DeployedFleetMemberAPI o1, DeployedFleetMemberAPI o2) {
                    if (iconMap[side].containsKey(o1) && iconMap[side].containsKey(o2)) {
                        return (int) (iconMap[side].get(o1).getSortValue() - iconMap[side].get(o2).getSortValue());
                    }
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

        HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList = new HashMap<>();

        List<CombatFleetManagerAPI.AssignmentInfo> assignments = Global.getCombatEngine().getFleetManager(side).getTaskManager(false).getAllAssignments();
        for (CombatFleetManagerAPI.AssignmentInfo info : assignments) {
            if (info.getType().equals(CombatAssignmentType.LIGHT_ESCORT)
            || info.getType().equals(CombatAssignmentType.MEDIUM_ESCORT)
            || info.getType().equals(CombatAssignmentType.HEAVY_ESCORT)) assignmentList.put(((DeployedFleetMemberAPI) info.getTarget()).getShip().getId(), info);
        }

        // render
        float w = NA_SettingsListener.na_combatui_size;
        float h = NA_SettingsListener.na_combatui_size;
        if (side == 1 && NA_SettingsListener.na_combatui_enemysize != 0) {
            w += NA_SettingsListener.na_combatui_enemysize;
            h += NA_SettingsListener.na_combatui_enemysize;
            w = Math.max(min_size, Math.min(max_size, w));
            h = Math.max(min_size, Math.min(max_size, h));
        }

        float Xspacing = !flip ? w + NA_SettingsListener.na_combatui_hspace : -w - NA_SettingsListener.na_combatui_hspace;
        float Yspacing = -h - NA_SettingsListener.na_combatui_vspace;


        float shipSizeScale = NA_SettingsListener.na_combatui_sizematters;


        if (NA_SettingsListener.na_combatui_sizedynamic != 0 || NA_SettingsListener.na_combatui_sizedynamicy != 0) {
            float x_spacing = Math.abs(Xspacing);
            float y_spacing = Math.abs(Yspacing);
            float maxSizex = 0;
            float maxSizey = 0;
            float rows = 0;
            float cols = 0;
            for (List<DeployedFleetMemberAPI> list : display) {
                if (list.isEmpty()) continue;
                rows += 1;
                for (DeployedFleetMemberAPI member : list) {
                    cols += 1;
                }
                maxSizex = Math.max(maxSizex, cols*x_spacing);
            }
            if (rows > 0 && cols > 0) {
                maxSizey = rows*Math.abs(y_spacing);
            }
            float dynamicMin = Math.min(min_size, NA_SettingsListener.na_combatui_sizedynamicmin);
            if (NA_SettingsListener.na_combatui_sizedynamic > 0 && maxSizex > NA_SettingsListener.na_combatui_sizedynamic) {
                float factor = NA_SettingsListener.na_combatui_sizedynamic/maxSizex;

                float diff = w;
                h *= factor;
                w *= factor;
                w = (int) Math.max(dynamicMin, Math.min(max_size, w));
                h = (int) Math.max(dynamicMin, Math.min(max_size, h));
                diff = w / diff;
                Xspacing *= diff;
                Yspacing *= diff;
                shipSizeScale *= diff;
                Xspacing = (int)Xspacing;
                Yspacing = (int)Yspacing;
                shipSizeScale = (int)shipSizeScale;

            }
            if (NA_SettingsListener.na_combatui_sizedynamicy > 0 && maxSizey > NA_SettingsListener.na_combatui_sizedynamicy) {
                float factor = NA_SettingsListener.na_combatui_sizedynamicy/maxSizey;

                float diff = h;
                h *= factor;
                w *= factor;
                w = (int) Math.max(dynamicMin, Math.min(max_size, w));
                h = (int) Math.max(dynamicMin, Math.min(max_size, h));
                diff = w / diff;
                Xspacing *= diff;
                Yspacing *= diff;
                shipSizeScale *= diff;
                Xspacing = (int)Xspacing;
                Yspacing = (int)Yspacing;
                shipSizeScale = (int)shipSizeScale;
            }
        }


        float YY = Global.getSettings().getScreenHeightPixels() - NA_SettingsListener.tacticalRenderHeightOffset - (side == 1 ? NA_SettingsListener.tacticalRenderHeightOffsetEnemy : 0);
        float XXstart = !flip ? NA_SettingsListener.tacticalRenderSideOffset + (side == 1 ? NA_SettingsListener.tacticalRenderSideOffsetEnemy : 0):
                Global.getSettings().getScreenWidthPixels() - NA_SettingsListener.tacticalRenderSideOffset - w - (side == 1 ? NA_SettingsListener.tacticalRenderSideOffsetEnemy : 0);


        float TEXTOFF = 30 + h;
        float TEXTHEIGHT = 20;
        float textSpacing = 100;
        float TEXTXOFF = !flip ? 0 : -2*textSpacing;
        float TITLEXOFF = !flip ? 12 : 12;
        float sineAmt = (float) Math.sin(9f * engine.getTotalElapsedTime(true) % (2*Math.PI));


        float movedUpExtra = 0;
        float sizedUpTicks = 3;
        boolean setTextOff = false;
        float XX = XXstart;
        if (flipv) {
            YY = NA_SettingsListener.tacticalRenderHeightOffset + (side == 1 ? NA_SettingsListener.tacticalRenderHeightOffsetEnemy : 0) - 20;

            for (List<DeployedFleetMemberAPI> list : display) {
                sizedUpTicks--;
                if (list.isEmpty()) continue;
                if (!setTextOff && sizedUpTicks > -1) {
                    TEXTOFF += shipSizeScale * (sizedUpTicks + 1);
                    setTextOff = true;
                    XX += (flip ? -1 : 1)*shipSizeScale;
                }
                if (!list.isEmpty()) {
                    if (shipSizeScale > 0 && sizedUpTicks > 0) {
                        YY += shipSizeScale * sizedUpTicks;
                    }
                    YY -= Yspacing;
                }
            }
        }

        for (DisplayDrawListener listener : DisplayDrawListeners) {
            if (listener.draw(input, side, flip, flipv, XX, YY, TEXTXOFF, TEXTOFF, TEXTHEIGHT, TITLEXOFF, textSpacing, e)) {

                return true;
            }
        }

        float w_orig = w;
        float h_orig = h;
        float xSpacing_orig = Xspacing;
        float ySpacing_orig = Yspacing;

        sizedUpTicks = 3;
        for (List<DeployedFleetMemberAPI> list : display) {

            XX = XXstart + (flip ? -1 : 1) * sizedUpTicks * (shipSizeScale);

            w = w_orig;
            h = h_orig;
            Xspacing = xSpacing_orig;
            Yspacing = ySpacing_orig;

            w += sizedUpTicks*shipSizeScale;
            h += sizedUpTicks*shipSizeScale;
            Xspacing += sizedUpTicks*(flip ? -1 : 1) * shipSizeScale;
            Yspacing -= Math.max(0, sizedUpTicks-1)*shipSizeScale;

            sizedUpTicks--;
            if (list.isEmpty()) continue;
            for (DeployedFleetMemberAPI member : list) {
                if (input != InputType.NO_INPUT) {
                    if (iconMap[side].containsKey(member)) {
                        if (input == InputType.HOLD) {
                            if (iconMap[side].get(member).handleHold(flip, flipv, XX, YY, w, h, assignmentList, e)) {
                                return true;
                            }
                        } else if (iconMap[side].get(member).handleInput(flip, flipv, XX, YY, w, h, assignmentList, e)) {

                            return true;
                        }
                    }
                } else {
                    if (iconMap[side].containsKey(member)) iconMap[side].get(member).render(flip, flipv, XX, YY, w, h, assignmentList, true, sineAmt, refreshUI, refreshData);
                }

                XX += Xspacing;
            }
            //XX = XXstart;
            YY += Yspacing;
        }
        return false;
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {

        if (uiRefreshTimer.intervalElapsed()) {
            uiRefreshTimer.setElapsed(0);
            refreshUI = true;
        } else uiRefreshTimer.advance(Global.getCombatEngine().getTotalElapsedTime(true) - lastUIUpdateTime);
        lastUIUpdateTime = Global.getCombatEngine().getTotalElapsedTime(true);

        if (YukiTacticalPlugin.hasLunaLib && NA_SettingsListener.na_combatui_enable) {
            CombatEngineAPI engine = Global.getCombatEngine();
            if ((engine.isUIShowingHUD() || NA_SettingsListener.na_combatui_force) && engine.getCombatUI() != null && !engine.getCombatUI().isShowingCommandUI() && (!NA_SettingsListener.na_combatui_pause
                    || engine.isPaused()
            )) {
                drawYukiTacticalDisplay(InputType.NO_INPUT, null, null, 0);
                if (NA_SettingsListener.na_combatui_enemy)
                    drawYukiTacticalDisplay(InputType.NO_INPUT, null, null, 1);

            }
        }

        refreshUI = false;
        refreshData = false;
    }

    @Override
    public void init(CombatEngineAPI engine) {

    }
}
