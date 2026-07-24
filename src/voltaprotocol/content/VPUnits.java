package voltaprotocol.content;

import mindustry.type.UnitType;
import mindustry.type.Weapon;
import voltaprotocol.ai.types.LiquidCargoAI;
import voltaprotocol.entities.type.LiquidCargoUnitType;
import voltaprotocol.world.blocks.storage.DroneManager;
import voltaprotocol.entities.type.KernelUnitType;

import static mindustry.Vars.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.gen.Sounds;

public class VPUnits{
//tanks
    public static UnitType amper;
    public static UnitType ion;
    public static UnitType valance;
    public static UnitType isotope;
    public static UnitType radiolysis;
//aerodeslizadores
    public static UnitType syrinxI;
    public static UnitType syrinxII;
    public static UnitType syrinxIII;
    public static UnitType syrinxIV;
//bosses
    public static UnitType boss1;
//cargo-drone
    public static LiquidCargoUnitType sifon;
//Core-drone
    public static KernelUnitType kernelDrone;

    public static void load(){

        amper = content.unit("w1-1a-amper");
        ion = content.unit("w1-a-ion");
        valance = content.unit("w1-b-valance");
        isotope = content.unit("w1-c-isotope");
        radiolysis = content.unit("w1-d-radiolysis");

        syrinxI = content.unit("w2-a-syrinx-i");
        syrinxII = content.unit("w2-b-syrinx-ii");
        syrinxIII = content.unit("w2-c-syrinx-iii");
        syrinxIV = content.unit("w2-d-syrinx-iv");

        boss1 = content.unit("xa-1a-boss-1");

        sifon = new LiquidCargoUnitType("w3-a-sifon"){{
            
            flying = true;
            drag = 0.05f;
            speed = 3.9f;
            accel = 0.05f;
            health = 250f;
            armor = 0f;
            hitSize = 7f;
            itemCapacity = 0;
            outlineRadius = 0;

            engines.clear();
            engines.add(new UnitEngine(5f, -5f, 2.0f, 45));
            engines.add(new UnitEngine( -5f,  -5f, 2.0f, -45f));
            
            isEnemy = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;

            constructor = mindustry.gen.BuildingTetherPayloadUnit::create;
            aiController = LiquidCargoAI::new;
        }};

        kernelDrone = new KernelUnitType("w3-b-kernel-drone") {{
            health = 800f;
            armor = 3f;
            hitSize = 14f;
            itemCapacity = 70;
            outlineRadius = 2;

            flying = true;
            drag = 0.05f;
            accel = 0.1f;
            engineSize = 2.5f;
            engineOffset = 5f;

            storageSpeed = 2.6f;
            storageMiningSpeed = 1.5f; 
            storageBuildSpeed = 1.2f;

            healingSpeed = 1.8f;
            healingMiningSpeed = 2.5f;
            healingRepairRate = 12f;
            healingRange = 100f;
            assaultSpeed = 3.2f;
            assaultDamageMult = 1.8f;
            maxLeashRange = 360f;
            depositRange = 55f;

            this.parts.add(new RegionPart("-cell") {{
                color = Color.valueOf("7fcdff");
                layerOffset = -0.001f;
                outline = false;
                blending = Blending.additive;
            }});

            weapons.add(new Weapon ("volta-protocol-drone-blaster") {{
                x = 6f;
                y = 0f;
                reload = 20f;
                speed = 3.4f;
                mineSpeed = 7.5f;
                mineTier = 2;
                rotate = true;
                rotateSpeed = 5f;
                shootCone = 15f; 
                rotationLimit = 15;
                mirror = true;
                shootSound = Sounds.shootAvert;
                shootSoundVolume = 0.4f;
                top = true;
                layerOffset = 0.05f;
                
                bullet = new LaserBoltBulletType(5.2f, 15f) {{
                    lifetime = 35f;
                    healPercent = 1f;
                    collidesTeam = true;
                    frontColor = arc.graphics.Color.white;
                    backColor = arc.graphics.Color.valueOf("84f5f5");
                }};
            }});
        }};

        DroneManager.kernelType = kernelDrone;
    }
}
