package voltaprotocol.content;

import arc.util.Log;
import mindustry.content.*;
import mindustry.content.TechTree.TechNode;
import mindustry.type.ItemStack;

public class VPTechTree {

    public static void load() {

        Log.info("[Volta Protocol] Acoplando ramas de investigación al árbol global...");

        VPBlocks.silverWall.researchCost = ItemStack.with(VPItems.silver, 150);
        VPBlocks.silverWallLarge.researchCost = ItemStack.with(VPItems.silver, 350);

        VPBlocks.palladiumWall.researchCost = ItemStack.with(VPItems.palladium, 200);
        VPBlocks.palladiumWallLarge.researchCost = ItemStack.with(VPItems.palladium, 450);

        VPBlocks.regenerativeWall.researchCost = ItemStack.with(
            VPItems.bioComposite, 250,
            VPItems.palladium, 100
        );

        VPBlocks.regenerativeWallLarge.researchCost = ItemStack.with(
            VPItems.bioComposite, 600,
            VPItems.palladium, 300
        );

        addToVanillaTree(Blocks.coreShard);

        Log.info("[Volta Protocol] ¡Árbol de Volta sincronizado!");
    }

    private static void addToVanillaTree(mindustry.world.Block coreRoot){

        TechNode contextNode = TechTree.all.find(t -> t.content == coreRoot);

        if(contextNode == null) return;

        contextNode.children.add(
            TechTree.node(VPBlocks.vpTemporalTT, () -> {

                if(VPItems.silver != null){

                    TechTree.nodeProduce(VPItems.silver, () -> {

                        if(VPItems.palladium != null){
                            TechTree.nodeProduce(VPItems.palladium, () -> {

                                if(VPItems.voltium != null){
                                    TechTree.nodeProduce(VPItems.voltium, () -> {});
                                }

                                if(VPItems.bioComposite != null){
                                    TechTree.nodeProduce(VPItems.bioComposite, () -> {});
                                }

                            });
                        }

                        if(VPItems.aegesium != null){
                            TechTree.nodeProduce(VPItems.aegesium, () -> {});
                        }

                        if(VPLiquids.oxychloride != null){

                            TechTree.nodeProduce(VPLiquids.oxychloride, () -> {

                                if(VPBlocks.liquidCargoLoader != null){

                                    TechTree.node(VPBlocks.liquidCargoLoader, () -> {

                                        if(VPBlocks.liquidCargoUnloadPoint != null){
                                            TechTree.node(VPBlocks.liquidCargoUnloadPoint);
                                        }

                                    });
                                }

                                if(VPLiquids.bioPlasma != null){

                                    TechTree.nodeProduce(VPLiquids.bioPlasma, () -> {

                                        if(VPLiquids.fluxPhase != null){
                                            TechTree.nodeProduce(VPLiquids.fluxPhase, () -> {});
                                        }

                                    });

                                }

                            });

                        }

                    });

                }
            //muros
                if(VPBlocks.silverWall != null){

                    TechTree.node(VPBlocks.silverWall, () -> {

                        if(VPBlocks.silverWallLarge != null){
                            TechTree.node(VPBlocks.silverWallLarge);
                        }

                        if(VPBlocks.palladiumWall != null){

                            TechTree.node(VPBlocks.palladiumWall, () -> {

                                if(VPBlocks.palladiumWallLarge != null){

                                    TechTree.node(VPBlocks.palladiumWallLarge, () -> {

                                        if(VPBlocks.regenerativeWall != null){

                                            TechTree.node(VPBlocks.regenerativeWall, () -> {

                                                if(VPBlocks.regenerativeWallLarge != null){
                                                    TechTree.node(VPBlocks.regenerativeWallLarge);
                                                }

                                            });

                                        }

                                    });

                                }

                            });

                        }

                    });

                }

            })
        );

    }

}
