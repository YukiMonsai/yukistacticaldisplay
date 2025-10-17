package yukimonsai.tactical;

import com.fs.starfarer.api.combat.AssignmentTargetAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.HashMap;

public interface ShipIcon {
    ShipAPI getShip();
    DeployedFleetMemberAPI get();
    FleetMemberAPI getMember();
    CombatFleetManagerAPI.AssignmentInfo getAssignment();
    AssignmentTargetAPI getAssignmentTarget();

    boolean maintain();
    float getSortValue();
    void render(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, boolean withText, float sineAmt, boolean UIUpdate, boolean DataUpdate);
    boolean handleInput(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, InputEventAPI e);
    boolean handleHold(boolean flip, boolean flipv, float XX, float YY, float w, float h, HashMap<String, CombatFleetManagerAPI.AssignmentInfo> assignmentList, InputEventAPI e);

}
