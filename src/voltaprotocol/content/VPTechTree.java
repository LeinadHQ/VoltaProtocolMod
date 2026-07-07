package voltaprotocol.content;

import arc.util.Log;
import mindustry.content.*;
import mindustry.content.TechTree.TechNode;
import mindustry.type.ItemStack;

public class VPTechTree {

    public static void load() {
        Log.info("[Volta Protocol] Acoplando ramas de investigacion al arbol global...");
    
        VPBlocks.silverWall.researchCost = ItemStack.with(VPItems.silver, 150);
        VPBlocks.silverWallLarge.researchCost = ItemStack.with(VPItems.silver, 350);
        VPBlocks.palladiumWall.researchCost = ItemStack.with(VPItems.palladium, 200);
        VPBlocks.palladiumWallLarge.researchCost = ItemStack.with(VPItems.palladium, 450);
        VPBlocks.regenerativeWall.researchCost = ItemStack.with(VPItems.bioComposite, 250, VPItems.palladium, 100);
        VPBlocks.regenerativeWallLarge.researchCost = ItemStack.with(VPItems.bioComposite, 600, VPItems.palladium, 300);

        addToVanillaTree(Blocks.coreShard);

        Log.info("[Volta Protocol] ¡Arbol de Volta sincronizado!");
    }

    private static void addToVanillaTree(mindustry.world.Block coreRoot) {
        TechNode contextNode = TechTree.all.find(t -> t.content == coreRoot);

        if (contextNode != null) {
            contextNode.children.add(TechTree.node(VPBlocks.vpTemporalTT, () -> {
                
                if (VPItems.silver != null) {
                    TechTree.nodeProduce(VPItems.silver, () -> {
                        if (VPItems.palladium != null) {
                            TechTree.nodeProduce(VPItems.palladium, () -> {
                                if (VPItems.voltium != null) TechTree.nodeProduce(VPItems.voltium, () -> {});
                                if (VPItems.bioComposite != null) TechTree.nodeProduce(VPItems.bioComposite, () -> {});
                            });
                        }
                        if (VPItems.aegesium != null) TechTree.nodeProduce(VPItems.aegesium, () -> {});
                    });
                }

                if (VPBlocks.silverWall != null) {
                    TechTree.node(VPBlocks.silverWall, () -> {
                        if (VPBlocks.silverWallLarge != null) TechTree.node(VPBlocks.silverWallLarge);
                        
                        if (VPBlocks.palladiumWall != null) {
                            TechTree.node(VPBlocks.palladiumWall, () -> {
                                if (VPBlocks.palladiumWallLarge != null) TechTree.node(VPBlocks.palladiumWallLarge, () -> {
                                    if (VPBlocks.regenerativeWall != null) {
                                        TechTree.node(VPBlocks.regenerativeWall, () -> {
                                            if (VPBlocks.regenerativeWallLarge != null) TechTree.node(VPBlocks.regenerativeWallLarge);
                                        });
                                    }
                                });
                            });
                        }
                    });
                }

                if (VPLiquids.oxychloride != null) {
                    TechTree.node(VPLiquids.oxychloride, () -> {
                        if (VPLiquids.bioPlasma != null) {
                            TechTree.node(VPLiquids.bioPlasma, () -> {
                                if (VPLiquids.fluxPhase != null) TechTree.node(VPLiquids.fluxPhase);
                            });
                        }
                    });
                }

            }));
        }
    }
}
