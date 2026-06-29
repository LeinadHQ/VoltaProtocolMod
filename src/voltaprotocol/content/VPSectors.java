package voltaprotocol.content;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.game.SpawnGroup;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;

public class VPSectors {
    public static SectorPreset laUltimaSenal;

    public static void load() {
        laUltimaSenal = new SectorPreset("la-ultima-senal", VPPlanets.Volta, 0) {{
            difficulty     = 3;
            alwaysUnlocked = true;
            captureWave    = 40;
        }};

        laUltimaSenal.rules = r -> {
            r.bannedBlocks.clear();
            r.damageExplosions = true;

            r.loadout = Seq.with(ItemStack.with(
                Items.copper,   2000,
                Items.lead,     2000,
                Items.silicon,  1000,
                Items.graphite,  800,
                VPItems.silver,  500
            ));

            r.spawns = Seq.with(
                new SpawnGroup(UnitTypes.dagger)      {{ begin = 0;  end = 18; spacing = 1; unitScaling = 1f;         }},

                new SpawnGroup(UnitTypes.nova)        {{ begin = 0;            spacing = 1; unitScaling = 2f;         }},

                new SpawnGroup(VPUnits.ion)          {{ begin = 0;            spacing = 3; unitScaling = 1f;         }},

                new SpawnGroup(UnitTypes.crawler)     {{ begin = 1;            spacing = 2; unitScaling = 0.8333333f; }},

                new SpawnGroup(UnitTypes.mace)        {{ begin = 2;  end = 33; spacing = 2; unitScaling = 0.5f;       }},

                new SpawnGroup(VPUnits.syrinxII)      {{ begin = 3;            spacing = 5; unitScaling = 0.25f;      }},

                new SpawnGroup(VPUnits.syrinxI)       {{ begin = 4;  end = 33; spacing = 3; unitScaling = 0.8333333f; }},

                new SpawnGroup(UnitTypes.stell)       {{ begin = 5;            spacing = 3; unitScaling = 0.625f;     }},

                new SpawnGroup(UnitTypes.pulsar)      {{ begin = 8;            spacing = 2; unitScaling = 1f;         }},

                new SpawnGroup(VPUnits.isotope)      {{ begin = 9;            spacing = 2; unitScaling = 0.5f;       }},

                new SpawnGroup(VPUnits.boss1)         {{
                    begin = 32; spacing = 35; unitScaling = 1f;
                    shields      = 12;
                    shieldScaling = 12f;
                }});

            r.winWave    = 40;
            r.waves      = true;
            r.waveSpacing = 60 * 60 * 4f;
        };
    }
}
