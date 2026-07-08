package voltaprotocol.entities.type;

import arc.Core;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.type.*;
import voltaprotocol.ai.types.LiquidCargoAI;

public class LiquidCargoUnitType extends UnitType{

    public TextureRegion liquidRegion;

    public LiquidCargoUnitType(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();

        liquidRegion = Core.atlas.find(name + "-liquid");
    }

    @Override
    public void draw(Unit unit){

        super.draw(unit);

        if(unit.controller() instanceof LiquidCargoAI ai){

            if(ai.currentLiquid != null){

                Draw.color(ai.currentLiquid.color);

                Draw.rect(
                    liquidRegion,
                    unit.x,
                    unit.y,
                    unit.rotation - 90f
                );

                Draw.reset();
            }
        }
    }
}