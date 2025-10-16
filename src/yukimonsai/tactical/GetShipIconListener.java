package yukimonsai.tactical;

import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;

public interface GetShipIconListener {
    ShipIcon getShipIcon(DeployedFleetMemberAPI member);
    float getScore(DeployedFleetMemberAPI member);
}
