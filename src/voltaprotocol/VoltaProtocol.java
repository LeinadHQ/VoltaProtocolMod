package voltaprotocol;

import arc.util.Log;
import mindustry.mod.Mod;

import voltaprotocol.content.VPLiquids;
import voltaprotocol.content.VPBlocks;
import voltaprotocol.content.VPItems;
import voltaprotocol.content.VPTechTree;
import voltaprotocol.content.VPPlanets;
import voltaprotocol.content.VPSectors;

public class VoltaProtocol extends Mod {

@Override
    public void loadContent(){
        Log.info("[Volta Protocol] Iniciando carga de contenido síncrono...");

        VPItems.load();
        VPLiquids.load();
        VPBlocks.load();
        VPPlanets.load();
        VPSectors.load();
        VPTechTree.load();

        Log.info("[Volta Protocol] Carga completada de forma segura.");
    }
}
