package voltaprotocol.world.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

public class LiquidOverflowGate extends LiquidRouter {

    public boolean invert = false;
    public float fullThreshold = 0.9f;
    public TextureRegion topRegion;

    public LiquidOverflowGate(String name) {
        super(name);
        rotate = true;
        solid = false;
        underBullets = true;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        if(bottomRegion == null || !bottomRegion.found()){
            bottomRegion = Core.atlas.find("liquid-router-bottom"); 
        }
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    public class LiquidOverflowGateBuild extends LiquidRouterBuild {
        
        public boolean lastLeft = false;

        @Override
        public void updateTile() {
            if (liquids.currentAmount() <= 0.0001f) return;

            Liquid liquid = liquids.current();
            Building front = front();
            Building left = left();
            Building right = right();

            if (!invert) { 
                if (available(front, liquid) && !saturated(front, liquid)) {
                    moveLiquid(front, liquid);
                } else {
                    if (lastLeft) {
                        if (available(left, liquid)) moveLiquid(left, liquid);
                        if (available(right, liquid)) moveLiquid(right, liquid);
                    } else {
                        if (available(right, liquid)) moveLiquid(right, liquid);
                        if (available(left, liquid)) moveLiquid(left, liquid);
                    }
                    lastLeft = !lastLeft;
                }

            } else { 
                boolean leftOk  = available(left, liquid) && !saturated(left, liquid);
                boolean rightOk = available(right, liquid) && !saturated(right, liquid);

                if (leftOk || rightOk) {
                    if (lastLeft) {
                        if (leftOk) moveLiquid(left, liquid);
                        if (rightOk) moveLiquid(right, liquid);
                    } else {
                        if (rightOk) moveLiquid(right, liquid);
                        if (leftOk) moveLiquid(left, liquid);
                    }
                    lastLeft = !lastLeft;
                } else {
                    if (available(front, liquid)) moveLiquid(front, liquid);
                }
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (source != null && source == front()) return false;
            return super.acceptLiquid(source, liquid);
        }

        private boolean available(Building b, Liquid liquid) {
            return b != null && b.team == team && b.acceptLiquid(this, liquid);
        }

        private boolean saturated(Building b, Liquid liquid) {
            if (b == null || b.block.liquidCapacity <= 0f) return true;
            return b.liquids.get(liquid) / b.block.liquidCapacity >= fullThreshold;
        }
        @Override
        public void draw() {
            super.draw(); 
            Draw.rect(topRegion, x, y, rotdeg()); 
        }
    }
}