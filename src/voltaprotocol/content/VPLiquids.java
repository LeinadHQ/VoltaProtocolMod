package voltaprotocol.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class VPLiquids {
    public static Liquid oxychloride, bioPlasma, fluxPhase;

    public static void load() {
        oxychloride = new Liquid("vx-1-oxychloride", Color.valueOf("84f5f5")){{
            gas = true;
            flammability = 0.1f;
            explosiveness = 0.4f;
            viscosity = 0.3f;
            coolant = false;
        }};

        bioPlasma = new Liquid("vx-2-bio-plasma", Color.valueOf("c5e5ff")){{
            lightColor = Color.valueOf("84f5f533");
            heatCapacity = 1.2f;
            viscosity = 0.6f;
            explosiveness = 0f;
            flammability = 0f;
        }};

        fluxPhase = new Liquid("vx-3-flux-phase", Color.valueOf("f48eac")){{
            lightColor = Color.valueOf("ffa3c4");
            viscosity = 0.6f;
            temperature = 0.3f;
            heatCapacity = 0.9f;
            explosiveness = 0.1f;
        }};
    }
}
