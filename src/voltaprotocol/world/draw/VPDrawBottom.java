package voltaprotocol.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class VPDrawBottom extends DrawBlock {

    public String suffix = "-bottom";
    public TextureRegion region;

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + suffix);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        if (region != null && region.found()) {
            return new TextureRegion[]{region};
        }
        return new TextureRegion[]{};
    }

    @Override
    public void draw(Building build) {
        if (region == null || !region.found()) return;
        Draw.rect(region, build.x, build.y);
    }
}