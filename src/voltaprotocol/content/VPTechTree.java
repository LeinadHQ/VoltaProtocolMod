package voltaprotocol.content;

import arc.util.Log;
import mindustry.content.TechTree;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class VPTechTree {

    public static void load() {
        Log.info("[Volta Protocol] Acoplando mapa del árbol tecnológico sin referencias nulas...");

        Block rootBlock = VPBlocks.vpTemporalTT;
        
        VPBlocks.silverWall.researchCost = ItemStack.with(VPItems.silver, 150);
        VPBlocks.silverWallLarge.researchCost = ItemStack.with(VPItems.silver, 350);
        VPBlocks.palladiumWall.researchCost = ItemStack.with(VPItems.palladium, 200);
        VPBlocks.palladiumWallLarge.researchCost = ItemStack.with(VPItems.palladium, 450);
        VPBlocks.regenerativeWall.researchCost = ItemStack.with(VPItems.bioComposite, 250, VPItems.palladium, 100);
        VPBlocks.regenerativeWallLarge.researchCost = ItemStack.with(VPItems.bioComposite, 600, VPItems.palladium, 300);

        if (rootBlock != null) {
            TechTree.nodeRoot("volta-protocol-tech-tree", rootBlock, () -> {

                if (VPItems.silver != null) {
                    TechTree.nodeProduce(VPItems.silver, () -> {
                        
                        if (VPItems.aegesium != null) TechTree.nodeProduce(VPItems.aegesium, () -> {});
                        
                        if (VPItems.palladium != null) {
                            TechTree.nodeProduce(VPItems.palladium, () -> {
                                if (VPItems.voltium != null) TechTree.nodeProduce(VPItems.voltium, () -> {});
                                if (VPItems.bioComposite != null) TechTree.nodeProduce(VPItems.bioComposite, () -> {});
                            });
                        }

                        if (VPLiquids.oxychloride != null) {
                            TechTree.node(VPLiquids.oxychloride, () -> {
                                
                                if (VPLiquids.bioPlasma != null) {
                                    TechTree.node(VPLiquids.bioPlasma, () -> {
                                        
                                        if (VPLiquids.fluxPhase != null) {
                                            TechTree.node(VPLiquids.fluxPhase);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                // Muros (defensa)
                TechTree.node(VPBlocks.silverWall, () -> {
                    TechTree.node(VPBlocks.silverWallLarge);

                    TechTree.node(VPBlocks.palladiumWall, () -> {
                        TechTree.node(VPBlocks.palladiumWallLarge);

                        TechTree.node(VPBlocks.regenerativeWall, () -> {
                            TechTree.node(VPBlocks.regenerativeWallLarge);
                        });
                    });
                });

            });

            Log.info("[Volta Protocol] ¡Árbol maestro bifurcado desde el núcleo!");
        }
    }
}
