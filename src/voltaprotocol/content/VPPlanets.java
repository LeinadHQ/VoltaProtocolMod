package voltaprotocol.content;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import voltaprotocol.expand.maps.VoltaPlanetGenerator;

public class VPPlanets {
    public static Planet Volta;

    public static void load() {
        Volta = new Planet("volta", Planets.sun, 1.2f, 2) {{
            visible = true;
            accessible = true;
            alwaysUnlocked = true;
            iconColor = Color.valueOf("e1897a");
            
            generator = new VoltaPlanetGenerator();

            meshLoader = () -> new HexMesh(this, 6);

            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("531336"), 2, 0.45f, 1.0f, 0.43f),
                new HexSkyMesh(this, 3, 0.22f, 0.12f, 6, Color.valueOf("73003e"), 3, 0.38f, 1.2f, 0.40f)
            );

            atmosphereColor = Color.valueOf("4a1231");
            atmosphereRadIn = 0.03f;
            atmosphereRadOut = 0.30f;

            lightColor = Color.valueOf("c19393"); 

            startSector = 0; 
            techTree = TechTree.all.find(t -> t.content == Blocks.coreShard);
        }};
    }
}
