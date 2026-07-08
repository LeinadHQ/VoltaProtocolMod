package voltaprotocol.ai.types;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import voltaprotocol.world.blocks.units.LiquidCargoUnloadPoint;
import voltaprotocol.world.blocks.units.LiquidCargoUnloadPoint.LiquidCargoUnloadPointBuild;

public class LiquidCargoAI extends AIController {

    static Seq<Liquid> orderedLiquids = new Seq<>();
    static Seq<LiquidCargoUnloadPointBuild> targets = new Seq<>();

    public static float emptyWaitTime  = 60f * 2f;
    public static float transferRange  = 20f;
    public static float moveRange      = 6f;
    public static float moveSmoothing  = 20f;

    public @Nullable LiquidCargoUnloadPointBuild unloadTarget;
    public @Nullable Liquid liquidTarget;
    public int targetIndex = 0;

    public Liquid currentLiquid = null;
    public float liquidAmount = 0f;
    public float droneLiquidCapacity = 40f; 

    @Override
    public void updateMovement() {
        if (!(unit instanceof BuildingTetherc tether) || tether.building() == null) {
            unit.kill();
            return;
        }
        
        Building loaderBuild = tether.building();

        if (liquidAmount <= 0.01f) {
            moveTo(loaderBuild, moveRange, moveSmoothing);

            if (loaderBuild.liquids != null && unit.within(loaderBuild, transferRange)) {
                if (loaderBuild.liquids.currentAmount() > 0f) {
                    findAnyTarget(loaderBuild);
                    
                    if (liquidTarget != null && !Vars.net.client()) {
                        float amountToTake = Math.min(droneLiquidCapacity - liquidAmount, loaderBuild.liquids.get(liquidTarget));
                        if (amountToTake > 0f) {
                            currentLiquid = liquidTarget;
                            liquidAmount += amountToTake;
                            loaderBuild.liquids.remove(liquidTarget, amountToTake);
                        }
                    }
                }
            }
        } 
        else {
            if (!isValidTarget(unloadTarget)) {
                findAnyTarget(loaderBuild);
            }

            if (unloadTarget != null) {
                moveTo(unloadTarget, moveRange, moveSmoothing);

                if (unit.within(unloadTarget, transferRange) && !Vars.net.client()) {
                    float spaceLeft = unloadTarget.block.liquidCapacity - unloadTarget.liquids.get(currentLiquid);
                    float amountToUnload = Math.min(liquidAmount, spaceLeft);

                    if (amountToUnload > 0f) {
                        unloadTarget.liquids.add(currentLiquid, amountToUnload);
                        liquidAmount -= amountToUnload;
                    }

                    if (liquidAmount <= 0.01f) {
                        currentLiquid = null;
                        unloadTarget = null;
                    }
                }
            } else {
                moveTo(loaderBuild, moveRange, moveSmoothing);
            }
        }
    }

    void findAnyTarget(Building loaderBuild) {
        Seq<LiquidCargoUnloadPointBuild> baseTargets = LiquidCargoUnloadPoint.allReceptors.select(u -> u.team == unit.team && u.isAdded());
        if (baseTargets.isEmpty()) return;

        orderedLiquids.clear();
        if (loaderBuild.liquids != null) {
            for (Liquid l : Vars.content.liquids()) {
                if (loaderBuild.liquids.get(l) > 0f) orderedLiquids.add(l);
            }
        }
        orderedLiquids.sort(l -> -loaderBuild.liquids.get(l));

        for (Liquid liquid : orderedLiquids) {
            targets.clear();
            for (var t : baseTargets) {
                if (t.targetLiquid == liquid && t.liquids.get(liquid) < t.block.liquidCapacity) targets.add(t);
            }
            if (targets.size > 0) {
                liquidTarget = liquid;
                unloadTarget = targets.get(targetIndex % targets.size);
                targetIndex++;
                return;
            }
        }
    }

    boolean isValidTarget(LiquidCargoUnloadPointBuild target) {
        return target != null && target.isAdded() && 
               currentLiquid != null && target.targetLiquid == currentLiquid && 
               target.liquids.get(currentLiquid) < target.block.liquidCapacity;
    }
}