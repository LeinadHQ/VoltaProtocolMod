package voltaprotocol.content;

import arc.util.Log;
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
import mindustry.world.meta.Attribute;

public class VPBlocks {
//environment
   //tiles
    public static Floor argentAndesite;
    public static Floor conductiveSand;
    public static Floor darkAndesite;
    //fosas
    public static Floor oxychlorideFloor;
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

    public static void load(){
        Log.info("[Volta Protocol] Cargando bloques y configurando ingeniería defensiva...");

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
            localizedName = "Oxicloruro superficial";
            isLiquid = true;
            liquidDrop = VPLiquids.oxychloride;
            liquidMultiplier = 1.5f;
            variants = 0;
            speedMultiplier = 0.75f;
            status = StatusEffects.corroded;
            statusDuration = 90f;
            cacheLayer = CacheLayer.water; 
            blendGroup = mindustry.content.Blocks.water;
            oreDefault = false;
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
            attributes.set(Attribute.steam, 2.0f);
            parent = darkAndesite;
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

        vpTemporalTT = new Block("vp-temporal-tt"){{
            localizedName = "Volta Protocol Core";
            size = 2;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            inEditor = false;
        }};
        
        silverWall = new Wall("a0-silver-wall"){{
            localizedName = "Muro de Plata";
            health = 460;
            armor = 2f;
            chanceDeflect = 0.08f;
            size = 1;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.silver, 6));
        }};

        silverWallLarge = new Wall("a1-silver-wall-large"){{
            localizedName = "Gran Muro de Plata";
            health = 1900;
            armor = 2f;
            chanceDeflect = 0.14f;
            size = 2;
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.silver, 24));
        }};
        
        palladiumWall = new Wall("a2-palladium-wall"){{
            localizedName = "Muro de Paladio";
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
            localizedName = "Gran Muro de Paladio";
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
            localizedName = "Muro Bio-Regenerativo";
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.bioComposite, 6, VPItems.palladium, 1));
        }};

        regenerativeWallLarge = new RegenerativeWall("a5-regenerative-wall-large", 2, 3800, 4f, 4f, 25f){{
            localizedName = "Gran Muro Bio-Regenerativo";
            category = Category.defense;
            buildVisibility = BuildVisibility.shown;
            requirements(Category.defense, ItemStack.with(VPItems.bioComposite, 24, VPItems.palladium, 4));
        }};

        Log.info("[Volta Protocol] ¡Estructuras cargadas perfectamente en memoria!");
    }
}
