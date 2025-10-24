package yukimonsai.tactical;

import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class GetShipIconFighterExcluder implements GetShipIconListener{
    public ShipIcon getShipIcon(DeployedFleetMemberAPI member) {
        return null;//this EXCLUDES all modules
    }
    public float getScore(DeployedFleetMemberAPI member) {
        if (member.getShip().getHullSize() == ShipAPI.HullSize.FIGHTER || member.getShip().getHullSize() == ShipAPI.HullSize.DEFAULT) return 100;
        else return -100;
    }
}
