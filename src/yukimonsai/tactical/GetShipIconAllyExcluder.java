package yukimonsai.tactical;

import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;

public class GetShipIconAllyExcluder implements GetShipIconListener{
    public ShipIcon getShipIcon(DeployedFleetMemberAPI member) {
        return null;//this EXCLUDES all dead ship
    }
    public float getScore(DeployedFleetMemberAPI member) {
        if (member.isAlly()) return 100;
        else return -100;
    }
}
