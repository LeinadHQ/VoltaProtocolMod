package voltaprotocol.content;

import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import voltaprotocol.expand.maps.VoltaPlanetGenerator;
import voltaprotocol.expand.maps.VoltaRingMesh;

public class VPPlanets {
    public static Planet Volta;

    public static void load() {
        Volta = new Planet("volta", Planets.sun, 1.2f, 3) {{
            visible = true;
            accessible = true;
            alwaysUnlocked = true;
            statParent = Planets.serpulo;

            generator = new VoltaPlanetGenerator();

            meshLoader = () -> new HexMesh(this, 6);

            cloudMeshLoader = () -> new MultiMesh(

                new HexSkyMesh(this, 2, 0.15f, 0.13f, 5,
                    Color.valueOf("1a0510").a(0.40f), 2, 0.50f, 1.00f, 0.30f),

                new HexSkyMesh(this, 5, 0.50f, 0.15f, 5,
                    Color.valueOf("c01068").a(0.52f), 2, 0.45f, 1.09f, 0.42f),

                new VoltaRingMesh(this, 2.65f, 0.05f, 0.70f, 10, 18f, 0.18f,
                    Color.valueOf("8a2266").a(0.85f), Color.valueOf("4a1042").a(0.80f)),

                new VoltaRingMesh(this, 2.88f, 0.045f, 0.92f, 4, 18f, 0.18f,
                    Color.valueOf("5a1a8a").a(0.40f), Color.valueOf("7a2ba0").a(0.40f)),

                new VoltaRingMesh(this, 2.76f, 0.035f, 0.96f, 28, 18f, 0.28f,
                    Color.valueOf("0a0820").a(0.30f), Color.valueOf("4466ff").a(0.28f))
            );

            atmosphereColor  = Color.valueOf("d6147a").a(0.45f);
            atmosphereRadIn  = 1.06f;
            atmosphereRadOut = 1.22f;

            bloom = false;

            allowLaunchToNumbered = true;
            defaultCore = Blocks.coreNucleus;
            startSector = 0;
            techTree = TechTree.all.find(t -> t.content == Blocks.coreShard);

            //techTree = TechTree.all.find(t -> t.content == VPBlocks.voltaCore); 
            autoAssignPlanet = true;

            ruleSetter = r -> {
                r.waveSpacing = 120 * 60f;
                r.waves = true;
            };
        }};
    }
}
