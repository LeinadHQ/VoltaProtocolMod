package voltaprotocol.world.draw;

import arc.*;
import arc.graphics.Blending;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import voltaprotocol.world.blocks.storage.ModularModuleV2;
import voltaprotocol.world.blocks.storage.ModularModuleV2.ModularModuleBuildV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;

public class VPDrawTurret extends DrawBlock {

    public TextureRegion baseRegion, cannonRegion, heatRegion;

    @Override
    public void load(Block block) {
        baseRegion   = Core.atlas.find(block.name + "-turret");
        cannonRegion = Core.atlas.find(block.name + "-turret-cannon");
        heatRegion   = Core.atlas.find(block.name + "-turret-cannon-heat");
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!(block instanceof ModularModuleV2 mb) || mb.protocol != ModuleProtocol.ASSAULT) return;
        float rot = plan.rotation * 90f - 90f;
        if (baseRegion != null && baseRegion.found()) {
            Draw.rect(baseRegion, plan.drawx(), plan.drawy(), rot);
        }
        if (cannonRegion != null && cannonRegion.found()) {
            Draw.rect(cannonRegion, plan.drawx(), plan.drawy(), rot);
        }
    }

    @Override
    public TextureRegion[] icons(Block block) {
        if (baseRegion != null && baseRegion.found() && cannonRegion != null && cannonRegion.found()) {
            return new TextureRegion[]{baseRegion, cannonRegion};
        }
        return new TextureRegion[0];
    }

    @Override
    public void draw(Building build) {
        if (!(build instanceof ModularModuleBuildV2 m)) return;
        if (m.mb().protocol != ModuleProtocol.ASSAULT) return;

        if (baseRegion != null && baseRegion.found()) {
            Draw.rect(baseRegion, build.x, build.y, m.rotation - 90f);
        }

        float cx = build.x + Angles.trnsx(m.rotation, -m.recoil * 3f);
        float cy = build.y + Angles.trnsy(m.rotation, -m.recoil * 3f);

        if (cannonRegion != null && cannonRegion.found()) {
            Draw.rect(cannonRegion, cx, cy, m.rotation - 90f);
        }

        if (m.turretHeat > 0.001f && heatRegion != null && heatRegion.found()) {
            final float heat = m.turretHeat;
            final float fx = cx, fy = cy;
            final float rot = m.rotation;
            Draw.draw(Layer.blockAdditive, () -> {
                Draw.blend(Blending.additive);
                Draw.color(m.mb().heatColor, heat);
                Draw.rect(heatRegion, fx, fy, rot - 90f);
                Draw.reset();
                Draw.blend();
            });
        }
    }
}