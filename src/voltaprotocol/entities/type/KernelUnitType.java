package voltaprotocol.entities.type;

import mindustry.type.UnitType;
import voltaprotocol.ai.types.KernelAI;
import voltaprotocol.world.blocks.storage.ModularCoreV2.ModularCoreBuildV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;

public class KernelUnitType extends UnitType {

    protected static UnitType kernelDrone;

    public float storageSpeed        = 2.0f;
    public float storageMiningSpeed  = 1.2f;
    public float storageBuildSpeed   = 1.5f;

    public float healingSpeed        = 1.6f;
    public float healingMiningSpeed  = 2.2f;
    public float healingRepairRate   = 8f;
    public float healingRange        = 120f;
    public float healingMinerRange   = 200f;

    public float assaultSpeed        = 2.8f;
    public float assaultDamageMult   = 1.8f;
    public float assaultMiningSpeed  = 1.0f;
    public float assaultPatrolRadius = 180f;
    public float maxLeashRange   = 300f;
    public float depositRange    = 48f;

    public KernelUnitType(String name) {
        super(name);

        constructor = mindustry.gen.UnitEntity::create;
        speed         = storageSpeed;
        mineSpeed     = storageMiningSpeed;
        buildSpeed    = storageBuildSpeed;
        flying        = true;
        lowAltitude   = true;
        targetAir     = true;
        targetGround  = true;
        canBoost      = false;
        circleTarget  = false;
        rotateMoveFirst = true;
    }

    public mindustry.gen.Unit createForCore(ModularCoreBuildV2 core) {
        mindustry.gen.Unit unit = this.create(core.team);
        unit.set(core.x, core.y);
        unit.controller(new KernelAI(core, this));

        return unit;
    }

    public float speedFor(ModuleProtocol protocol) {
        return switch (protocol) {
            case HEALING -> healingSpeed;
            case ASSAULT -> assaultSpeed;
            default      -> storageSpeed;
        };
    }

    public float miningSpeedFor(ModuleProtocol protocol) {
        return switch (protocol) {
            case HEALING -> healingMiningSpeed;
            case ASSAULT -> assaultMiningSpeed;
            default      -> storageMiningSpeed;
        };
    }

    public float damageMult(ModuleProtocol protocol) {
        return protocol == ModuleProtocol.ASSAULT ? assaultDamageMult : 1.0f;
    }
}