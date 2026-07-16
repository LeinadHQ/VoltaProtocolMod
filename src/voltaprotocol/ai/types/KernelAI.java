package voltaprotocol.ai.types;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.world.Tile;
import voltaprotocol.entities.type.KernelUnitType;
import voltaprotocol.world.blocks.storage.ModularCoreV2.ModularCoreBuildV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;

import static mindustry.Vars.*;

public class KernelAI extends AIController {

    private final ModularCoreBuildV2 home;
    private final KernelUnitType     ktype;

    private KernelState        state        = KernelState.IDLE;
    private ModuleProtocol     lastProtocol = null;
    private float              lastProtocolSpeed = 0f;

    private Tile     mineTile     = null;
    private Building repairTarget = null;
    private Unit     attackTarget = null;
    private float mineProgress = 0f;

    private float targetSearchTimer = 0f;
    private static final float TARGET_SEARCH_INTERVAL = 30f;
    private static final float MINE_DETECT_RANGE = 200f;
    private static final float ARRIVE_DIST  = 12f;
    private static final float MINE_DIST    = 20f;
    private static final float REPAIR_DIST  = 80f;
    private static final float ATTACK_STOP_DIST = 0.75f;

    public KernelAI(ModularCoreBuildV2 home, KernelUnitType ktype) {
        this.home  = home;
        this.ktype = ktype;
    }

    @Override
    public void updateUnit() {
        if (unit == null) return;

        if (!isHomeValid()) {
            updateOrphan();
            return;
        }

        ModuleProtocol protocol = home.protocolManager.dominantProtocol();
        if (protocol == null) protocol = ModuleProtocol.STORAGE;

        if (protocol != lastProtocol) {
            onProtocolChanged(protocol);
        }

        if (unit.stack.amount >= unit.type.itemCapacity && state != KernelState.DEPOSITING) {
            transitionTo(KernelState.DEPOSITING);
        }

        if (tooFarFromHome() && state != KernelState.DEPOSITING && state != KernelState.RETURNING) {
            transitionTo(KernelState.RETURNING);
        }

        targetSearchTimer -= Time.delta;
        updateByProtocol(protocol);
    }

    private void updateByProtocol(ModuleProtocol protocol) {
        switch (protocol) {

            case HEALING -> {
                switch (state) {
                    case IDLE       -> decideHealing();
                    case REPAIRING  -> tickRepairing();
                    case MINING     -> tickMining();
                    case DEPOSITING -> tickDepositing();
                    case RETURNING  -> tickReturning();
                    case ATTACKING  -> tickAttacking();
                }
            }

            case ASSAULT -> {
                switch (state) {
                    case IDLE       -> decideAssault();
                    case ATTACKING  -> tickAttacking();
                    case MINING     -> tickMining();
                    case DEPOSITING -> tickDepositing();
                    case RETURNING  -> tickReturning();
                    case REPAIRING  -> tickRepairing();
                }
            }

            default -> {
                switch (state) {
                    case IDLE       -> decideStorage();
                    case MINING     -> tickMining();
                    case DEPOSITING -> tickDepositing();
                    case RETURNING  -> tickReturning();
                    case REPAIRING  -> tickRepairing();
                    case ATTACKING  -> tickAttacking();
                }
            }
        }
    }

    private void decideStorage() {
        if (unit.stack.amount > 0 && isNearHome()) {
            doDeposit();
            transitionTo(KernelState.IDLE);
            return;
        }
        Tile ore = findNearestOre(MINE_DETECT_RANGE);
        if (ore != null) {
            mineTile = ore;
            transitionTo(KernelState.MINING);
            return;
        }
        wanderNearHome();
    }

    private void decideHealing() {
        if (shouldSearchTarget()) {
            repairTarget = findRepairTarget();
            resetSearchTimer();
        }
        if (repairTarget != null && repairTarget.isAdded() && repairTarget.healthf() < 0.99f) {
            transitionTo(KernelState.REPAIRING);
            return;
        }
        decideStorage();
    }

    private void decideAssault() {
        if (shouldSearchTarget()) {
            attackTarget = findNearestEnemy();
            resetSearchTimer();
        }
        if (attackTarget != null && attackTarget.isAdded() && !attackTarget.dead()) {
            transitionTo(KernelState.ATTACKING);
            return;
        }
        decideStorage();
    }

    private void tickMining() {
        if (mineTile == null || ! unit.validMine(mineTile)) {
            transitionTo(KernelState.IDLE);
            return;
        }

        float tx = mineTile.worldx(), ty = mineTile.worldy();
        float dst = unit.dst(tx, ty);

        if (dst > MINE_DIST) {
            moveTo(tx, ty);
        } else {
            unit.lookAt(tx, ty);

            float speedMult = ktype.miningSpeedFor(effectiveProtocol());
            float ticksPerMine = 60f / (unit.type.mineSpeed * speedMult);
            mineProgress += Time.delta;

            if (mineProgress >= ticksPerMine) {
                mineProgress = 0f;
                Item drop = mineTile.drop();
                if (drop != null && unit.acceptsItem(drop)) {
                    Call.transferItemToUnit(drop, tx, ty, unit);
                } else {
                    mineTile = null;
                    transitionTo(KernelState.IDLE);
                }
            }
        }
    }

    private void tickDepositing() {
        if (!isHomeValid()) {
            transitionTo(KernelState.IDLE);
            return;
        }

        float dst = unit.dst(home.x, home.y);
        if (dst > ktype.depositRange) {
            moveTo(home.x, home.y);
        } else {
            doDeposit();
            transitionTo(KernelState.IDLE);
        }
    }

    private void tickRepairing() {
        if (repairTarget == null || !repairTarget.isAdded() || repairTarget.healthf() >= 1f) {
            repairTarget = null;
            transitionTo(KernelState.IDLE);
            return;
        }

        float dst = unit.dst(repairTarget.x, repairTarget.y);

        if (dst > REPAIR_DIST) {
            moveTo(repairTarget.x, repairTarget.y);
        } else {
            unit.lookAt(repairTarget.x, repairTarget.y);
            float healAmount = ktype.healingRepairRate / 60f * Time.delta;
            repairTarget.heal(healAmount);
        }
    }

    private void tickAttacking() {
        if (attackTarget == null || !attackTarget.isAdded() || attackTarget.dead()) {
            attackTarget = null;
            transitionTo(KernelState.IDLE);
            return;
        }

        float weaponRange = unit.range();
        float stopDist    = weaponRange * ATTACK_STOP_DIST;
        float dst         = unit.dst(attackTarget);

        if (dst > stopDist) {
            moveTo(attackTarget.x, attackTarget.y);
        }

        unit.lookAt(attackTarget);
        target = attackTarget;
    }

    private void tickReturning() {
        if (!isHomeValid()) {
            transitionTo(KernelState.IDLE);
            return;
        }

        float dst = unit.dst(home.x, home.y);
        if (dst <= ktype.maxLeashRange * 0.5f) {
            transitionTo(KernelState.IDLE);
        } else {
            moveTo(home.x, home.y);
        }
    }

    private void updateOrphan() {
        Unit enemy = findNearestEnemy();
        if (enemy != null) {
            target = enemy;
            unit.lookAt(enemy);
            moveTo(enemy.x, enemy.y);
        }
    }

    private void transitionTo(KernelState next) {
        // Limpiar estado al salir
        switch (state) {
            case MINING    -> { mineTile = null; mineProgress = 0f; }
            case REPAIRING -> repairTarget = null;
            case ATTACKING -> { attackTarget = null; target = null; }
            default        -> {}
        }
        state = next;
    }

    private void onProtocolChanged(ModuleProtocol newProtocol) {
        lastProtocol = newProtocol;
        lastProtocolSpeed = ktype.speedFor(newProtocol);
        transitionTo(KernelState.IDLE);
        resetSearchTimer();
    }

    private void moveTo(float tx, float ty) {
        float speed = lastProtocolSpeed > 0f ? lastProtocolSpeed : unit.type.speed;
        Tmp.v1.set(tx, ty).sub(unit.x, unit.y).limit(speed * Time.delta);
        unit.vel.set(Tmp.v1);
        unit.lookAt(tx, ty);
    }

    private void wanderNearHome() {
        if (!isHomeValid()) return;
        float angle  = Mathf.random(360f);
        float radius = Mathf.random(ktype.maxLeashRange * 0.3f);
        float wx = home.x + Mathf.cosDeg(angle) * radius;
        float wy = home.y + Mathf.sinDeg(angle) * radius;
        moveTo(wx, wy);
    }

    private Tile findNearestOre(float range) {
        int tileRange = (int)(range / 8);
        int cx = (int)(unit.x / 8);
        int cy = (int)(unit.y / 8);

        Tile best    = null;
        float bestDst = Float.MAX_VALUE;

        for (int dx = -tileRange; dx <= tileRange; dx++) {
            for (int dy = -tileRange; dy <= tileRange; dy++) {
                Tile t = world.tile(cx + dx, cy + dy);
                if (t == null || t.drop() == null) continue;
                if (!unit.validMine(t)) continue;
                float d = unit.dst(t.worldx(), t.worldy());
                if (d < range && d < bestDst) {
                    bestDst = d;
                    best    = t;
                }
            }
        }
        return best;
    }

    private Building findRepairTarget() {
        float range  = ktype.healingRange;
        Building best = null;
        float bestHp  = Float.MAX_VALUE;

        Units.nearbyBuildings(unit.x, unit.y, range, b -> {
            if (b.team != unit.team) return;
            if (b.healthf() >= 1f) return;
        });

        int tileRange = (int)(range / 8);
        int cx = (int)(unit.x / 8), cy = (int)(unit.y / 8);
        for (int dx = -tileRange; dx <= tileRange; dx++) {
            for (int dy = -tileRange; dy <= tileRange; dy++) {
                Tile t = world.tile(cx + dx, cy + dy);
                if (t == null || t.build == null) continue;
                Building b = t.build;
                if (b.team != unit.team) continue;
                if (b.healthf() >= 1f) continue;
                float dst = unit.dst(b.x, b.y);
                if (dst > range) continue;
                if (b.healthf() < bestHp) {
                    bestHp = b.healthf();
                    best   = b;
                }
            }
        }
        return best;
    }

    private Unit findNearestEnemy() {
        return Units.closestEnemy(unit.team, unit.x, unit.y, unit.range() * 1.5f, u -> !u.dead);
    }

    private boolean isHomeValid() {
        return home != null && home.isAdded();
    }

    private boolean isNearHome() {
        return isHomeValid() && unit.dst(home.x, home.y) < ktype.depositRange;
    }

    private boolean tooFarFromHome() {
        return isHomeValid() && unit.dst(home.x, home.y) > ktype.maxLeashRange;
    }

    private boolean shouldSearchTarget() {
        return targetSearchTimer <= 0f;
    }

    private void resetSearchTimer() {
        targetSearchTimer = TARGET_SEARCH_INTERVAL;
    }

    private ModuleProtocol effectiveProtocol() {
        return lastProtocol != null ? lastProtocol : ModuleProtocol.STORAGE;
    }

    /** Depositar todos los items al núcleo */
    private void doDeposit() {
        if (!isHomeValid() || unit.stack.amount <= 0) return;
        Item item   = unit.stack.item;
        int  amount = unit.stack.amount;
        if (item != null && home.acceptItem(home, item)) {
            home.handleItem(home, item);
        }
        unit.stack.amount = 0;
        unit.stack.item   = null;
    }

    public enum KernelState {
        IDLE,
        MINING,
        DEPOSITING,
        REPAIRING,
        ATTACKING,
        RETURNING
    }
}