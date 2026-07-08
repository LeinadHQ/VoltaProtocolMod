package voltaprotocol.world.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import arc.graphics.g2d.Lines;

import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import voltaprotocol.ai.types.LiquidCargoAI;
import voltaprotocol.content.VPUnits;

public class LiquidCargoLoader extends Block{

    public UnitType unitType = VPUnits.sifon;
    public float unitBuildTime = 60f * 8f;

    public float restoreTime = 180f;

    public float polyStroke = 1.8f;
    public float polyRadius = 8f;
    public int polySides = 6;
    public float polyRotateSpeed = 1f;

    public Color defaultPolyColor = Color.valueOf("5eb1b1");

    public LiquidCargoLoader(String name){
        super(name);

        update = true;
        solid = true;

        hasLiquids = true;
        liquidCapacity = 200f;
    }

    public class LiquidCargoLoaderBuild extends Building{

        public @Nullable Unit drone;

        public int droneId = -1;

        public float restoreTimer = 0f;

        public float buildProgress;
        public float totalProgress;
        public float warmup;
        public float readiness;

        @Override
        public void updateTile(){

            if(drone == null && droneId != -1){

                drone = Groups.unit.getByID(droneId);

                if(drone != null){

                    if(drone instanceof BuildingTetherc bt){
                        bt.building(this);
                    }

                    droneId = -1;
                    restoreTimer = 0f;

                    return;
                }

                restoreTimer += edelta();
                if(restoreTimer < restoreTime){
                    return;
                }

                droneId = -1;
                restoreTimer = 0f;
            }

            if(drone != null){

                if(drone.dead || !drone.isAdded() || drone.team != team){
                    drone = null;
                }
                warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
                readiness = Mathf.approachDelta(readiness, drone != null ? 1f : 0f, 1f / 60f);
            }

            if(drone == null){

                buildProgress += edelta() / unitBuildTime;
                totalProgress += edelta();

                if(buildProgress >= 1f && !Vars.net.client()){
                    if(team.data().countType(unitType) >= Units.getCap(team)){
                        return;
                    }

                    buildProgress = 0f;

                    drone = unitType.create(team);

                    drone.set(x, y);
                    drone.rotation = 90f;

                    if(drone instanceof BuildingTetherc bt){
                        bt.building(this);
                    }

                    drone.controller(new LiquidCargoAI());

                    drone.add();

                    Fx.spawn.at(x, y);
                }
            }
        }

        @Override
        public void remove(){

            if(drone != null){
                drone.kill();
            }

            super.remove();
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return liquids.get(liquid) < liquidCapacity;
        }
        @Override
public void draw(){

    super.draw();

    if(drone != null){
        if(drone == null){

            Draw.draw(Layer.blockOver, () -> {
                Drawf.construct(
                    this,
                    unitType.fullIcon,
                    0f,
                    buildProgress,
                    warmup,
                    totalProgress
                );
            });

        }else{

        Draw.z(Layer.blockOver);

        Color color = defaultPolyColor;

        if(liquids != null && liquids.currentAmount() > 0.001f){
            color = liquids.current().color;
            }

            Draw.color(color);
            Lines.stroke(polyStroke * readiness);

            Lines.poly(
                x,
                y,
                polySides,
                polyRadius,
                Time.time * polyRotateSpeed
            );

            Draw.reset();
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(drone == null ? droneId : drone.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            drone = null;
            droneId = read.i();
            restoreTimer = 0f;
        }
    }
}