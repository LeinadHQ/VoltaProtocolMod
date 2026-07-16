package voltaprotocol.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import voltaprotocol.world.blocks.storage.ModularModuleV2.ModularModuleBuildV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;

public class VPDrawPlasma extends DrawBlock {

    public float plasmaAlpha = 0.65f;
    public int   plasmaCount = 3;
    public String suffix = "-plasma-";
    
    public float reactorLight      = 110f;
    public float reactorLightAlpha = 0.65f;

    public TextureRegion[] regions;

    @Override
    public void load(Block block) {
        regions = new TextureRegion[plasmaCount];
        for (int i = 0; i < plasmaCount; i++) {
            regions[i] = Core.atlas.find(block.name + suffix + i);
        }
    }

    @Override
    public void draw(Building build) {
        if (!(build instanceof ModularModuleBuildV2 m)) return;
        if (m.mb().protocol != ModuleProtocol.ENERGY) return;
        if (m.warmup <= 0.001f) return;

        float r = m.mb().plasmaCold.r + (m.mb().plasmaHot.r - m.mb().plasmaCold.r) * m.warmup;
        float g = m.mb().plasmaCold.g + (m.mb().plasmaHot.g - m.mb().plasmaCold.g) * m.warmup;
        float b = m.mb().plasmaCold.b + (m.mb().plasmaHot.b - m.mb().plasmaCold.b) * m.warmup;

        Draw.blend(Blending.additive);

        for (int i = 0; i < regions.length; i++) {
            if (regions[i] == null || !regions[i].found()) continue;
            float rot   = (i % 2 == 0 ? 1f : -1f) * m.totalProgress * (0.8f + i * 0.15f);
            float alpha = plasmaAlpha * m.warmup * (1f - i * 0.12f);
            Draw.color(r, g, b, alpha);
            Draw.rect(regions[i], build.x, build.y, rot);
        }
        Draw.blend();
        Draw.color();
    }

    @Override
    public void drawLight(Building build) {
        if (!(build instanceof ModularModuleBuildV2 m)) return;
        if (m.mb().protocol != ModuleProtocol.ENERGY || m.warmup <= 0.001f) return;
        float pulse = reactorLight * m.warmup * (1f + Mathf.absin(Time.time, 6f, 0.15f));
        Drawf.light(build.x, build.y, pulse,
            Tmp.c1.set(m.mb().plasmaCold).lerp(m.mb().plasmaHot, m.warmup),
            reactorLightAlpha * m.warmup);
    }
}