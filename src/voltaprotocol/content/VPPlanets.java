package voltaprotocol.content;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.type.Planet;
import voltaprotocol.expand.maps.VoltaPlanetGenerator;

public class VPPlanets {
    public static Planet Volta;

    public static void load() {
        Volta = new Planet("volta", Planets.sun, 1f, 2) {{
            visible = true;
            accessible = true;
            alwaysUnlocked = true;
            iconColor = Color.valueOf("e1897a");

            meshLoader = () -> new HexMesh(this, 6); 
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(
                    this,
                    11,
                    0.15f,
                    0.14f,
                    5,
                    Color.valueOf("73003e"),
                    2,
                    0.45f,
                    1.0f,
                    0.43f
                )
            );
            atmosphereColor = Color.valueOf("73003e");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.25f;

            generator = new VoltaPlanetGenerator(); 

            allowLaunchToNumbered = true;
            
            startSector = 0;
        }};
    }
}