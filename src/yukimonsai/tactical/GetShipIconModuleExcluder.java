package yukimonsai.tactical;

import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;

public class GetShipIconModuleExcluder implements GetShipIconListener{
    public ShipIcon getShipIcon(DeployedFleetMemberAPI member) {
        return null;//this EXCLUDES all modules
    }
    public float getScore(DeployedFleetMemberAPI member) {
        if (member.isStationModule()) return 100;
        else return -100;
    }
}
