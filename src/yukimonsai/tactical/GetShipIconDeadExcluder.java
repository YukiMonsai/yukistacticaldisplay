package yukimonsai.tactical;

import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;

public class GetShipIconDeadExcluder implements GetShipIconListener{
    public ShipIcon getShipIcon(DeployedFleetMemberAPI member) {
        return null;//this EXCLUDES all dead ship
    }
    public float getScore(DeployedFleetMemberAPI member) {
        if (!member.getShip().isAlive()) return 100;
        else return -100;
    }
}
