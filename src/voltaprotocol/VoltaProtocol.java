package voltaprotocol;

import arc.Events;
import mindustry.game.EventType.ResetEvent;
import arc.util.Log;
import mindustry.mod.Mod;

import voltaprotocol.content.VPLiquids;
import voltaprotocol.content.VPBlocks;
import voltaprotocol.content.VPItems;
import voltaprotocol.content.VPTechTree;
import voltaprotocol.content.VPUnits;
import voltaprotocol.content.VPPlanets;
import voltaprotocol.content.VPSectors;
import voltaprotocol.world.blocks.units.LiquidCargoUnloadPoint; 

public class VoltaProtocol extends Mod {
    public VoltaProtocol(){
        
        Events.on(ResetEvent.class, e -> {
            LiquidCargoUnloadPoint.allReceptors.clear();
        });
    }

    @Override
    public void loadContent(){
        Log.info("[Volta Protocol] Iniciando carga de contenido síncrono...");

        VPItems.load();
        VPLiquids.load();
        VPUnits.load();
        VPBlocks.load();
        VPPlanets.load();
        VPSectors.load();
        VPTechTree.load();

        Log.info("[Volta Protocol] Carga completada de forma segura.");
    }
}
