package voltaprotocol.content;

import arc.util.Log;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BuildVisibility;
import voltaprotocol.world.blocks.defense.RegenerativeWall;

public class VPBlocks {

    public static Block vpTemporalTT;
    public static Wall silverWall;
    public static Wall silverWallLarge;
    public static Wall palladiumWall;
    public static Wall palladiumWallLarge;
    public static RegenerativeWall regenerativeWall;
    public static RegenerativeWall regenerativeWallLarge;

    public static void load(){
        Log.info("[Volta Protocol] Cargando bloques y configurando ingeniería defensiva...");

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
