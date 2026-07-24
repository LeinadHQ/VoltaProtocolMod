package voltaprotocol.content;

import arc.Events;
import arc.util.Log;
import mindustry.content.*;
import mindustry.content.TechTree.TechNode;
import mindustry.game.EventType.ContentInitEvent;
import mindustry.type.ItemStack;

public class VPTechTree {

    public static void load() {
        Log.info("[Volta Protocol] Acoplando ramas de investigación");

        VPBlocks.silverWall.researchCost = ItemStack.with(VPItems.silver, 150);
        VPBlocks.silverWallLarge.researchCost = ItemStack.with(VPItems.silver, 350);
        VPBlocks.palladiumWall.researchCost = ItemStack.with(VPItems.palladium, 200);
        VPBlocks.palladiumWallLarge.researchCost = ItemStack.with(VPItems.palladium, 450);
        VPBlocks.regenerativeWall.researchCost = ItemStack.with(VPItems.bioComposite, 250, VPItems.palladium, 100);
        VPBlocks.regenerativeWallLarge.researchCost = ItemStack.with(VPItems.bioComposite, 600, VPItems.palladium, 300);

        VPBlocks.moduleBasic.researchCost = ItemStack.with(VPItems.silver, 500);

        VPBlocks.liquidCargoLoader.researchCost =
            ItemStack.with(VPItems.silver, 600, VPItems.palladium, 400);
        VPBlocks.liquidCargoUnloadPoint.researchCost =
            ItemStack.with(VPItems.silver, 225, VPItems.palladium, 100);

        VPBlocks.palladiumLiquidBridge.researchCost = ItemStack.with(VPItems.palladium, 80, Items.metaglass, 80);
        VPBlocks.palladiumLiquidOverflow.researchCost = ItemStack.with(VPItems.palladium, 50, Items.metaglass, 50);
        VPBlocks.palladiumLiquidUnderflow.researchCost = ItemStack.with(VPItems.palladium, 50, Items.metaglass, 50);

        VPBlocks.moduleSilver.researchCost =
            ItemStack.with(VPItems.silver, 2000, VPItems.palladium, 500);

        VPBlocks.moduleRed.researchCost =
            ItemStack.with(VPItems.palladium, 1500, VPItems.silver, 1000);

        VPBlocks.moduleGreen.researchCost =
            ItemStack.with(VPItems.bioComposite, 1500, VPItems.silver, 2500);

        VPBlocks.moduleBlue.researchCost =
            ItemStack.with(VPItems.aegesium, 1500, VPItems.palladium, 800);

        VPBlocks.moduleOrange.researchCost =
            ItemStack.with(VPItems.voltium, 1500, VPItems.aegesium, 500);

        TechNode liquidRouterNode = TechTree.all.find(t -> t.content == Blocks.liquidRouter);
        
        if (liquidRouterNode != null) {
            new TechNode(liquidRouterNode, VPBlocks.liquidOverflow, VPBlocks.liquidOverflow.researchRequirements());
            new TechNode(liquidRouterNode, VPBlocks.liquidUnderflow, VPBlocks.liquidUnderflow.researchRequirements());
        }

        TechNode serpuloCoreNode = TechTree.all.find(t -> t.content == Blocks.coreShard);

        if (serpuloCoreNode != null) {
            TechNode voltaRoot = new TechNode(serpuloCoreNode, VPBlocks.voltaCore, VPBlocks.voltaCore.researchRequirements());
        }

        TechNode voltaNodeRef = TechTree.all.find(t -> t.content == VPBlocks.voltaCore);
        
        if (voltaNodeRef != null) {
            // Ítems
            if (VPItems.silver != null) {
                TechTree.nodeProduce(VPItems.silver, () -> {
                    if (VPItems.palladium != null) {
                        TechTree.nodeProduce(VPItems.palladium, () -> {
                            if (VPItems.voltium != null) TechTree.nodeProduce(VPItems.voltium, () -> {});
                            if (VPItems.bioComposite != null) TechTree.nodeProduce(VPItems.bioComposite, () -> {});
                        });
                    }
                    if (VPItems.aegesium != null) TechTree.nodeProduce(VPItems.aegesium, () -> {});
                    
                    if (VPLiquids.oxychloride != null) {
                        TechTree.nodeProduce(VPLiquids.oxychloride, () -> {
                            if (VPLiquids.bioPlasma != null) {
                                TechTree.nodeProduce(VPLiquids.bioPlasma, () -> {
                                    if (VPLiquids.fluxPhase != null) TechTree.nodeProduce(VPLiquids.fluxPhase, () -> {});
                                });
                            }
                        });
                    }
                });
            }
            // Muros
            if (VPBlocks.silverWall != null) {
                new TechNode(voltaNodeRef, VPBlocks.silverWall, VPBlocks.silverWall.researchRequirements());
                TechNode silverWallNode = TechTree.all.find(t -> t.content == VPBlocks.silverWall);
                if (silverWallNode != null) {
                    if (VPBlocks.silverWallLarge != null) new TechNode(silverWallNode, VPBlocks.silverWallLarge, VPBlocks.silverWallLarge.researchRequirements());
                    if (VPBlocks.palladiumWall != null) {
                        new TechNode(silverWallNode, VPBlocks.palladiumWall, VPBlocks.palladiumWall.researchRequirements());
                        TechNode palWallNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumWall);
                        if (palWallNode != null) {
                            if (VPBlocks.palladiumWallLarge != null) {
                                new TechNode(palWallNode, VPBlocks.palladiumWallLarge, VPBlocks.palladiumWallLarge.researchRequirements());
                                TechNode palWallLNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumWallLarge);
                                if (palWallLNode != null && VPBlocks.regenerativeWall != null) {
                                    new TechNode(palWallLNode, VPBlocks.regenerativeWall, VPBlocks.regenerativeWall.researchRequirements());
                                    TechNode regWallNode = TechTree.all.find(t -> t.content == VPBlocks.regenerativeWall);
                                    if (regWallNode != null && VPBlocks.regenerativeWallLarge != null) {
                                        new TechNode(regWallNode, VPBlocks.regenerativeWallLarge, VPBlocks.regenerativeWallLarge.researchRequirements());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Módulos
            if (VPBlocks.moduleBasic != null) {
                new TechNode(voltaNodeRef, VPBlocks.moduleBasic, VPBlocks.moduleBasic.researchRequirements());
                TechNode modBasicNode = TechTree.all.find(t -> t.content == VPBlocks.moduleBasic);
                if (modBasicNode != null && VPBlocks.moduleSilver != null) {
                    new TechNode(modBasicNode, VPBlocks.moduleSilver, VPBlocks.moduleSilver.researchRequirements());
                    TechNode modSilverNode = TechTree.all.find(t -> t.content == VPBlocks.moduleSilver);
                    if (modSilverNode != null) {
                        if (VPBlocks.moduleRed != null) {
                            new TechNode(modSilverNode, VPBlocks.moduleRed, VPBlocks.moduleRed.researchRequirements());
                            TechNode modRedNode = TechTree.all.find(t -> t.content == VPBlocks.moduleRed);
                            if (modRedNode != null && VPBlocks.moduleBlue != null) {
                                new TechNode(modRedNode, VPBlocks.moduleBlue, VPBlocks.moduleBlue.researchRequirements());
                            }
                        }
                        if (VPBlocks.moduleGreen != null) {
                            new TechNode(modSilverNode, VPBlocks.moduleGreen, VPBlocks.moduleGreen.researchRequirements());
                            TechNode modGreenNode = TechTree.all.find(t -> t.content == VPBlocks.moduleGreen);
                            if (modGreenNode != null && VPBlocks.moduleOrange != null) {
                                new TechNode(modGreenNode, VPBlocks.moduleOrange, VPBlocks.moduleOrange.researchRequirements());
                            }
                        }
                    }
                }
            }
            // Liquid-logistic
            if (VPBlocks.palladiumConduit != null) {
                new TechNode(voltaNodeRef, VPBlocks.palladiumConduit, VPBlocks.palladiumConduit.researchRequirements());
                TechNode palCondNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumConduit);
                
                if (palCondNode != null && VPBlocks.palladiumLiquidBridge != null) {
                    new TechNode(palCondNode, VPBlocks.palladiumLiquidBridge, VPBlocks.palladiumLiquidBridge.researchRequirements());
                    TechNode palBridgeNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumLiquidBridge);
                    
                    if (palBridgeNode != null && VPBlocks.palladiumLiquidRouter != null) {
                        new TechNode(palBridgeNode, VPBlocks.palladiumLiquidRouter, VPBlocks.palladiumLiquidRouter.researchRequirements());
                        TechNode palRouterNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumLiquidRouter);
                        
                        if (palRouterNode != null) {
                            if (VPBlocks.palladiumLiquidContainer != null) {
                                new TechNode(palRouterNode, VPBlocks.palladiumLiquidContainer, VPBlocks.palladiumLiquidContainer.researchRequirements());
                                TechNode palContNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumLiquidContainer);
                                if (palContNode != null && VPBlocks.liquidCargoLoader != null) {
                                    new TechNode(palContNode, VPBlocks.liquidCargoLoader, VPBlocks.liquidCargoLoader.researchRequirements());
                                    TechNode cargoLoaderNode = TechTree.all.find(t -> t.content == VPBlocks.liquidCargoLoader);
                                    if (cargoLoaderNode != null && VPBlocks.liquidCargoUnloadPoint != null) {
                                        new TechNode(cargoLoaderNode, VPBlocks.liquidCargoUnloadPoint, VPBlocks.liquidCargoUnloadPoint.researchRequirements());
                                    }
                                }
                            }
                            
                            if (VPBlocks.palladiumLiquidJunction != null) {
                                new TechNode(palRouterNode, VPBlocks.palladiumLiquidJunction, VPBlocks.palladiumLiquidJunction.researchRequirements());
                                TechNode palJunctionNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumLiquidJunction);
                                
                                if (palJunctionNode != null && VPBlocks.palladiumLiquidOverflow != null) {
                                    new TechNode(palJunctionNode, VPBlocks.palladiumLiquidOverflow, VPBlocks.palladiumLiquidOverflow.researchRequirements());
                                    TechNode palOverflowNode = TechTree.all.find(t -> t.content == VPBlocks.palladiumLiquidOverflow);
                                    
                                    if (palOverflowNode != null && VPBlocks.palladiumLiquidUnderflow != null) {
                                        new TechNode(palOverflowNode, VPBlocks.palladiumLiquidUnderflow, VPBlocks.palladiumLiquidUnderflow.researchRequirements());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Events.on(ContentInitEvent.class, e -> {
            for(TechNode root : TechTree.roots){
                if(root != null){
                    root.addPlanet(VPPlanets.Volta);
                }
            }

            Log.info("[Volta Protocol] ¡Árbol de Volta sincronizado!");
        });
    }
}
