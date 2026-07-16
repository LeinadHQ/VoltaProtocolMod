package voltaprotocol.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class VPDrawTeam extends DrawBlock {

    public String suffix = "-team";
    public TextureRegion region;

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + suffix);
    }

    @Override
    public void draw(Building build) {
        if (region == null || !region.found()) return;
        Draw.color(build.team.color);
        Draw.rect(region, build.x, build.y);
        Draw.color();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (region == null || !region.found()) return;
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(Block block) {
        if (region != null && region.found()) return new TextureRegion[]{region};
        return new TextureRegion[0];
    }
}