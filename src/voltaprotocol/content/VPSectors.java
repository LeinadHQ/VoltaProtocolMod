package voltaprotocol.content;

import mindustry.type.SectorPreset;

public class VPSectors {
    public static SectorPreset laUltimaSenal;

    public static void load() {
        laUltimaSenal = new SectorPreset("la-ultima-senal", VPPlanets.Volta, 0) {{
            localizedName = "La Última Señal";
            difficulty = 3;
            alwaysUnlocked = true;
            captureWave = 40;
        }};
    }
}