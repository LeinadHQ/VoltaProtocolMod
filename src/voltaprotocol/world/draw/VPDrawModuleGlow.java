package voltaprotocol.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import voltaprotocol.world.blocks.storage.ModularModuleV2;
import voltaprotocol.world.blocks.storage.ModularModuleV2.ModularModuleBuildV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;

public class VPDrawModuleGlow extends DrawBlock {

    public TextureRegion glowRegion;
    public TextureRegion activeGlowRegion;

    @Override
    public void load(Block block) {
        glowRegion       = Core.atlas.find(block.name + "-glow");
        activeGlowRegion = Core.atlas.find(block.name + "-glow-active");
    }

    @Override
    public void draw(Building build) {
        if (!(build instanceof ModularModuleBuildV2 m)) return;
        ModularModuleV2 mb = m.mb();

        float z = Draw.z();
        Draw.z(Layer.blockAdditive);

        if (mb.hasGlow && glowRegion != null && glowRegion.found()) {
            Draw.blend(Blending.additive);
            Draw.color(mb.glowColor);
            Draw.rect(glowRegion, build.x, build.y);
            Draw.reset();
            Draw.blend();
        }

        if (mb.hasActiveGlow && m.activeSlot && activeGlowRegion != null && activeGlowRegion.found()) {
            Draw.blend(Blending.additive);
            Draw.color(mb.activeGlowColor);
            Draw.rect(activeGlowRegion, build.x, build.y);
            Draw.reset();
            Draw.blend();
        }

        if (mb.protocol == ModuleProtocol.HEALING
                && glowRegion != null && glowRegion.found()
                && m.isHealActive()) {
            float alpha = Mathf.absin(Time.time, 8f, 0.6f) + 0.4f;
            Draw.blend(Blending.additive);
            Draw.color(Color.white, alpha);
            Draw.rect(glowRegion, build.x, build.y);
            Draw.reset();
            Draw.blend();
        }

        Draw.z(z);
    }
}