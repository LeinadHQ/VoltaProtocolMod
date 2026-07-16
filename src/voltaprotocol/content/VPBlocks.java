package voltaprotocol.content;

import arc.util.Log;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.graphics.CacheLayer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.meta.BuildVisibility;
import voltaprotocol.world.blocks.defense.RegenerativeWall;
import voltaprotocol.world.blocks.storage.ModularCoreV2;
import voltaprotocol.world.blocks.storage.ModularModuleV2;
import voltaprotocol.world.blocks.storage.ModuleProtocol;
import mindustry.world.meta.Attribute;
import static mindustry.type.ItemStack.with;
import voltaprotocol.world.blocks.units.LiquidCargoLoader;
import voltaprotocol.world.blocks.units.LiquidCargoUnloadPoint;

public class VPBlocks {
//environment
   //tiles
    public static Floor argentAndesite;
    public static Floor conductiveSand;
    public static Floor darkAndesite;
    //fosas
    public static Floor oxychlorideFloor;
    public static Floor oxychlorideShoalFloor;
    public static Floor oxychlorideDeepFloor;
    public static Floor oxychloridedandesiteShoalFloor;
    public static Floor oxychlorideFloorSand;
    //Muros relieve
    public static StaticWall argentAndesiteWall;
    public static StaticWall conductiveSandWall;
    public static StaticWall darkAndesiteWall;
    //Calor-ventila
    public static Floor pyroclasticAndesite;
    public static Floor voltaicMagma;
    public static SteamVent voltaicGeyser;
    //ores
    public static OreBlock silverOre;
    public static OreBlock palladiumOre;
//bloques construibles
    //defence
    public static Block vpTemporalTT;
    public static Wall silverWall;
    public static Wall silverWallLarge;
    public static Wall palladiumWall;
    public static Wall palladiumWallLarge;
    public static RegenerativeWall regenerativeWall;
    public static RegenerativeWall regenerativeWallLarge;
    //Distribution
    public static Block liquidCargoLoader;
    public static Block liquidCargoUnloadPoint;
    //Core
    public static ModularCoreV2     voltaCore;
    //Core-Modules
    public static ModularModuleV2   moduleBasic, moduleGreen, moduleRed, moduleBlue, moduleOrange, moduleSilver;

    public static void load(){
        Log.info("[Volta Protocol] Cargando bloques y configurando ingeniería defensiva...");
    //environment
        argentAndesite = new Floor("env-argent-andesite-vp"){{
            localizedName = "Andesita Argenta";
            variants = 4;
        }};

        conductiveSand = new Floor("env-conductive-sand-vp"){{
            localizedName = "Arena Conductora";
            variants = 3;
            speedMultiplier = 0.9f;
        }};

        darkAndesite = new Floor("env-dark-andesite-vp"){{
            localizedName = "Andesita Oscura";
            variants = 3;
        }};

        oxychlorideFloor = new Floor("env-oxychloride-floor"){{
            localizedName = "Oxicloruro superficial (andesita oscura)";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 1.5f;
            variants = 0;
            speedMultiplier = 0.4f;
            drownTime = 150f;
            status = StatusEffects.corroded;
            statusDuration = 90f;
            cacheLayer = CacheLayer.water; 
            blendGroup = this;
            oreDefault = false;
            albedo = 0.9f;
        }};

        oxychlorideShoalFloor = new Floor("env-oxychloride-shoal"){{
            localizedName = "Orilla de Oxicloruro (arena conductora)";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 1.0f;
            variants = 0;
            speedMultiplier = 0.3f;
            drownTime = 180f;
            status = StatusEffects.corroded;
            statusDuration = 45f;
            cacheLayer = CacheLayer.water; 
            blendGroup = oxychlorideFloor;
            albedo = 0.9f;
        }};

        oxychlorideFloorSand = new Floor("env-oxychloride-floor-sand"){{
            localizedName = "Oxicloruro superficial (arena conductora)";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 1.5f;
            variants = 0;
            speedMultiplier = 0.4f;
            drownTime = 180f;
            status = StatusEffects.corroded;
            statusDuration = 90f;
            cacheLayer = CacheLayer.water; 
            blendGroup = oxychlorideFloor;
            oreDefault = false;
            albedo = 0.9f;
        }};

        oxychloridedandesiteShoalFloor = new Floor("env-oxychloride-shoal-andesite-dark"){{
            localizedName = "Orilla de Oxicloruro (andesita oscura)";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 1.0f;
            variants = 0;
            speedMultiplier = 0.3f;
            drownTime = 180f;
            status = StatusEffects.corroded;
            statusDuration = 45f;
            cacheLayer = CacheLayer.water; 
            blendGroup = oxychlorideFloor;
            albedo = 0.9f;
        }};

        oxychlorideDeepFloor = new Floor("env-oxychloride-deep-floor"){{
            localizedName = "Oxicloruro profundo";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 2.0f;
            variants = 0;
            speedMultiplier = 0.25f;
            drownTime = 120f;
            status = StatusEffects.corroded;
            statusDuration = 200f; 
            cacheLayer = CacheLayer.water;
            blendGroup = oxychlorideFloor;
            oreDefault = false;
            albedo = 0.9f;
        }};

        argentAndesiteWall = new StaticWall("env-argent-andesite-wall"){{
            localizedName = "Muro de Andesita Argenta";
            variants = 2;
        }};

        conductiveSandWall = new StaticWall("env-conductive-sand-wall"){{
            localizedName = "Muro de Arena Conductora";
            variants = 2;
            attributes.set(Attribute.sand, 1f);
        }};

        darkAndesiteWall = new StaticWall("env-dark-andesite-wall"){{
            localizedName = "Muro de Andesita Oscura";
            attributes.set(Attribute.water, 0.2f);
            variants = 2;
        }};

        pyroclasticAndesite = new Floor("env-pyroclastic-andesite"){{
            localizedName = "Andesita Piroclastica";
            variants = 3;
            speedMultiplier = 0.8f;
            attributes.set(Attribute.heat, 0.5f); 
            attributes.set(Attribute.water, 0f);
            emitLight = true;
            lightRadius = 40f;
            lightColor = arc.graphics.Color.valueOf("e05316").a(0.2f);
            oreDefault = false;
        }};

        voltaicMagma = new Floor("env-voltaic-magma"){{
            localizedName = "Magma Voltaico";
            variants = 3;
            speedMultiplier = 0.5f;
            attributes.set(Attribute.heat, 1.0f); 
            attributes.set(Attribute.water, 0f);
            emitLight = true;
            lightRadius = 65f;
            lightColor = arc.graphics.Color.valueOf("ff4500").a(0.4f);
            oreDefault = false;
        }};

        voltaicGeyser = new SteamVent("env-voltaic-geyser"){{
            localizedName = "Geiser voltaico";
            variants = 0;
            attributes.set(Attribute.steam, 1.5f);
            parent = blendGroup = darkAndesite;
            oreDefault = false;
        }};

        silverOre = new OreBlock("vp-silver-ore", VPItems.silver){{
            localizedName = "Mena de plata";
            variants = 3;
        }};

        palladiumOre = new OreBlock("vp-palladium-ore", VPItems.palladium){{
            localizedName = "Mena de paladio";
            variants = 3;
        }};
    //defence
        vpTemporalTT = new Block("vp-temporal-tt"){{
            localizedName = "Volta Protocol Core";
            size = 2;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            inEditor = false;
        }};
        
        silverWall = new Wall("a0-silver-wall"){{
            health = 460;
            armor = 2f;
            chanceDeflect = 0.08f;
            size = 1;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.silver, 6));
        }};

        silverWallLarge = new Wall("a1-silver-wall-large"){{
            health = 1900;
            armor = 2f;
            chanceDeflect = 0.14f;
            size = 2;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.silver, 24));
        }};
        
        palladiumWall = new Wall("a2-palladium-wall"){{
            health = 600;
            armor = 3f;
            insulated = true;
            
            lightningChance = 0.09f;
            lightningLength = 6;
            lightningDamage = 12f;
            
            size = 1;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.palladium, 6));
        }};

        palladiumWallLarge = new Wall("a3-palladium-wall-large"){{
            health = 2400;
            armor = 3f;
            insulated = true;
            
            lightningChance = 0.18f;
            lightningLength = 8;
            lightningDamage = 18f;
            
            size = 2;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.palladium, 24));
        }};
        
        regenerativeWall = new RegenerativeWall("a4-regenerative-wall", 1, 950, 3f, 2f, 10f){{
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.bioComposite, 6, VPItems.palladium, 1));
        }};

        regenerativeWallLarge = new RegenerativeWall("a5-regenerative-wall-large", 2, 3800, 4f, 4f, 25f){{
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.bioComposite, 24, VPItems.palladium, 4));
        }};

        liquidCargoLoader = new LiquidCargoLoader("liquid-cargo-loader"){{
            size = 3;
            category = Category.liquid;
            buildVisibility = BuildVisibility.shown;
            
            unitType = VPUnits.sifon;
            unitBuildTime = 60f * 10f;
            liquidCapacity = 400f;
            
            consumePower(2.5f); 
            
            requirements(Category.liquid, ItemStack.with(VPItems.silver, 120, VPItems.palladium, 80));
        }};

        liquidCargoUnloadPoint = new LiquidCargoUnloadPoint("liquid-cargo-unload-point"){{
            size = 2;
            category = Category.liquid;
            buildVisibility = BuildVisibility.shown;
            
            hasLiquids = true;
            liquidCapacity = 200f;
            outputsLiquid = true;
            
            requirements(Category.liquid, ItemStack.with(VPItems.silver, 45, VPItems.palladium, 20));
        }};
        // Core-Modular
        voltaCore = new ModularCoreV2("mc-1a-modular-core") {{ 
            requirements(Category.effect, with(
                VPItems.silver,    2500,
                VPItems.palladium, 2200,
                Items.silicon,     2400,
                Items.thorium,     1500
            ));
            unitType = VPUnits.kernelDrone;
            alwaysUnlocked = true;
            size = 4;
            itemCapacity = 10000;
            health = 4000;
            baseArmor = 4f;
            maxActiveModules = 6;
            isFirstTier = false;
        }};
                
        // Modulos
        moduleBasic = new ModularModuleV2("mm-1a-module-basic") {{
            requirements(Category.effect, with(VPItems.silver, 250));
            
            protocol = ModuleProtocol.STORAGE;
            canOverflow = true;
            
            size = 2;
            health = 300;
            itemCapacity = 300; 
            capacityBonus = 0;
            armorBonus = 1f;
            healthBonus = 0f;
        }};
                
        moduleGreen = new ModularModuleV2("mm-1d-module-green") {{
            requirements(Category.effect, with(VPItems.bioComposite, 250, VPItems.silver, 150));
            
            protocol = ModuleProtocol.HEALING;
            
            size = 3;
            itemCapacity = 1100;
            health = 550;
            capacityBonus = 0;
            armorBonus = -1f;
            healthBonus = 0f;
            healRate = 12f;
            
            hasGlow = false; 
            hasActiveGlow = false; 

            healEffect = mindustry.content.Fx.healBlockFull;
            healEffectChance = 0.04f;
        }};
                
        moduleRed = new ModularModuleV2("mm-1c-module-red") {{
            requirements(Category.effect, with(VPItems.palladium, 250, VPItems.silver, 180));
            
            protocol = ModuleProtocol.ASSAULT;
            
            size = 3;
            health = 800;
            itemCapacity = 500;
            capacityBonus = 0;
            armorBonus = 1f;
            healthBonus = -200f;

            turretRange = 240f;
            turretDamage = 135f;
            turretReload = 150f;
            ammoItem = VPItems.palladium;
            ammoPerShot = 3;
            shootSound = mindustry.gen.Sounds.shootDisperse;

            turretBullet = new mindustry.entities.bullet.BasicBulletType(12f, turretDamage){{
                width = 8f;        
                height = 22f;      
                hitSize = 5f;      
                lifetime = 20f; 
                
                shootEffect = new mindustry.entities.effect.MultiEffect(mindustry.content.Fx.shootBigColor, mindustry.content.Fx.colorSparkBig);
                smokeEffect = mindustry.content.Fx.shootBigSmoke;
                
                frontColor = arc.graphics.Color.white; 
                backColor = arc.graphics.Color.valueOf("d96c00");
                hitColor = arc.graphics.Color.valueOf("ffa64d");
                trailColor = arc.graphics.Color.valueOf("d96c00");
                trailWidth = 1.7f; 
                trailLength = 12;
                hitEffect = mindustry.content.Fx.hitBulletColor;
                despawnEffect = mindustry.content.Fx.hitBulletColor;
                
                pierce = true;
                pierceCap = 2;
                pierceBuilding = true;
            }};
        }};
                
        moduleBlue = new ModularModuleV2("mm-1e-module-blue") {{
            requirements(Category.effect, with(VPItems.aegesium, 200, VPItems.palladium, 200));
            
            protocol = ModuleProtocol.DEFENSE;
            
            size = 3;
            itemCapacity = 800;
            health = 900; 
            capacityBonus = 0;
            armorBonus = 1.5f;
            healthBonus = 400f;
            maxActive = 4;
        }};
                
        moduleOrange = new ModularModuleV2("mm-1f-module-orange") {{
            requirements(Category.effect, with(VPItems.voltium, 150, VPItems.aegesium, 200));
            
            protocol = ModuleProtocol.ENERGY;
            
            size = 3;
            hasPower = true;
            outputsPower = true;
            consumesPower = false;
            itemCapacity = 400;
            capacityBonus = 0;
            armorBonus = 0f;
            health = 600;
            healthBonus = -400f;
            powerOutput = 1050f;
            maxActive = 2;

            ambientSound = mindustry.gen.Sounds.loopPulse;
            ambientSoundVolume = 0.04f;
        }};
                
        moduleSilver = new ModularModuleV2("mm-1b-module-silver") {{
            requirements(Category.effect, with(VPItems.silver, 300, VPItems.palladium, 120));
            
            protocol = ModuleProtocol.STORAGE;
            canOverflow = true;
            
            size = 3;
            health = 750;
            itemCapacity = 1250;
            capacityBonus = 0;
            armorBonus = 1f;
            healthBonus = 200f;
        }};
        Log.info("[Volta Protocol] ¡Estructuras cargadas perfectamente en memoria!");
    }
}
