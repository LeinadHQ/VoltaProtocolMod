package voltaprotocol.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

public class LiquidCargoUnloadPoint extends Block {

    public static Seq<LiquidCargoUnloadPointBuild> allReceptors = new Seq<>();
    public TextureRegion topRegion;

    public LiquidCargoUnloadPoint(String name) {
        super(name);
        update           = true;
        solid            = true;
        hasLiquids       = true;
        configurable     = true;
        saveConfig       = true;
        clearOnDoubleTap = true;

        config(Liquid.class, (LiquidCargoUnloadPointBuild build, Liquid liquid) -> build.targetLiquid = liquid);
        configClear((LiquidCargoUnloadPointBuild build) -> build.targetLiquid = null);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public class LiquidCargoUnloadPointBuild extends Building {

        public Liquid targetLiquid;

        @Override
        public void add() {
            super.add();
            allReceptors.add(this);
        }

        @Override
        public void remove() {
            super.remove();
            allReceptors.remove(this); 
        }

        @Override
        public void draw() {
            super.draw();
            if (targetLiquid != null && topRegion != null) {
                Draw.color(targetLiquid.color);
                Draw.rect(topRegion, x, y);
                Draw.color();
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(LiquidCargoUnloadPoint.this, table, Vars.content.liquids(), () -> targetLiquid, this::configure);
        }

        @Override
        public Object config() {
            return targetLiquid;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return targetLiquid == liquid && liquids.get(liquid) < block.liquidCapacity;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(targetLiquid == null ? -1 : targetLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            short id = read.s();
            targetLiquid = (id == -1) ? null : Vars.content.liquid(id);
        }
        @Override
        public void updateTile() {
            super.updateTile();
            
            if (targetLiquid != null && liquids.get(targetLiquid) > 0f) {
                dumpLiquid(targetLiquid);
            }
        }
    }
}