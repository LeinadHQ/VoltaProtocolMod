package voltaprotocol.world.blocks.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.modules.ItemModule;
import voltaprotocol.world.blocks.storage.ModularCoreV2.ModularCoreBuildV2;
import voltaprotocol.world.draw.*;

public class ModularModuleV2 extends StorageBlock {

    public ModuleProtocol protocol    = ModuleProtocol.STORAGE;
    public int            maxActive   = -1;
    public boolean        canOverflow = false;

    public int   capacityBonus = 0;
    public float armorBonus    = 0f;
    public float healthBonus   = 0f;
    public float healRate      = 0f;
    public float powerOutput   = 0f;

    public BulletType    turretBullet = Bullets.fireball;
    public @Nullable Item ammoItem    = null;
    public float         turretRange  = 200f;
    public float         turretDamage = 20f;
    public float         turretReload = 90f;
    public int           ammoPerShot  = 3;
    public arc.audio.Sound shootSound = Sounds.none;
    public Color         heatColor    = Color.valueOf("ff6214");

    public Color plasmaCold        = Color.valueOf("84f5f5");
    public Color plasmaHot         = Color.valueOf("ff9966");
    public float plasmaAlpha       = 0.85f;
    public float reactorLight      = 60f;
    public float reactorLightAlpha = 0.65f;

    public boolean hasGlow        = false;
    public boolean hasActiveGlow  = false;
    public Color   glowColor      = Color.white.cpy();
    public Color   activeGlowColor = new Color(1f, 1f, 1f, 0.7f);

    public @Nullable Effect ambientEffect       = null;
    public float            ambientEffectChance = 0.04f;
    public @Nullable Effect activeEffect        = null;
    public float            activeEffectChance  = 0.06f;
    public @Nullable Effect healEffect          = null;
    public float            healEffectChance    = 0.03f;

    public DrawBlock drawer = new DrawDefault();
    private TextureRegion previewRegion;

    public ModularModuleV2(String name) {
        super(name);

        this.drawCached  = false; 
        this.drawDynamic = true;
        update       = true;
        destructible = true;
        sync         = true;

        drawer = new DrawMulti(
            new VPDrawBottom(),
            new VPDrawPlasma(),
            new DrawDefault(),
            new VPDrawTeam(),
            new VPDrawModuleGlow(),
            new VPDrawTurret()
        );
    }

    public int effectiveMaxActive() {
        return maxActive >= 0 ? maxActive : protocol.defaultMaxActive;
    }

    public boolean solidRegion() {
        return false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void setStats() {
        super.setStats();

        if (capacityBonus > 0) {
            stats.remove(Stat.itemCapacity);
            stats.add(Stat.itemCapacity, capacityBonus, StatUnit.none);
        }
        if (armorBonus   != 0f) stats.add(Stat.armor,               armorBonus,    StatUnit.none);
        if (healthBonus  != 0f) stats.add(Stat.health,              healthBonus,   StatUnit.none);
        if (healRate      > 0f) stats.add(Stat.repairSpeed,         healRate,      StatUnit.perSecond);
        if (powerOutput   > 0f) stats.add(Stat.basePowerGeneration, powerOutput,   StatUnit.powerSecond);

        if (protocol == ModuleProtocol.ASSAULT) {
            stats.add(Stat.shootRange, turretRange / 8f, StatUnit.blocks);
            stats.add(Stat.damage,     turretDamage);
            stats.add(Stat.reload,     60f / turretReload, StatUnit.perSecond);
            if (ammoItem != null) stats.add(Stat.ammo, StatValues.items(new ItemStack(ammoItem, ammoPerShot)));
        }

        stats.add(new Stat("vpmoduleslots",    StatCat.general), effectiveMaxActive(), StatUnit.none);
        if (canOverflow) stats.add(new Stat("vpmoduleoverflow", StatCat.general), StatValues.bool(true));
    }

    @Override
    public TextureRegion[] icons() {
        previewRegion = Core.atlas.find(name + "-preview");
        return previewRegion != null && previewRegion.found()
            ? new TextureRegion[]{previewRegion}
            : super.icons();
    }

    public class ModularModuleBuildV2 extends StorageBuild {

        public @Nullable ModularCoreBuildV2 modularCore = null;

        public boolean activeSlot  = false;

        // ASSAULT
        public float reloadTimer = 0f;
        public float rotation    = 90f;
        public float turretHeat  = 0f;
        public float recoil      = 0f;

        // ENERGY
        public float warmup        = 0f;
        public float totalProgress = 0f;

        private ItemModule ownItems;

        public ModularModuleV2 mb() { return (ModularModuleV2) block; }

        @Override
        public void created() {
            super.created();
            ownItems = this.items;
        }

        @Override
        public void onProximityUpdate() {
            ModularCoreBuildV2 prevModular = modularCore;
            boolean prevSlot = activeSlot;

            super.onProximityUpdate();

            modularCore = null;
            activeSlot = false;

            if (linkedCore instanceof ModularCoreBuildV2 mc) {
                modularCore = mc;
                activeSlot = true;
            } else if (linkedCore instanceof CoreBuild) {
                activeSlot = true;
            }

            if (modularCore == null) {
                for (Building b : proximity) {
                    if (b.team == team && b instanceof ModularCoreBuildV2 mc) {
                        modularCore = mc;
                        linkedCore = mc;
                        activeSlot = true;
                        
                        this.items = mc.items;
                        break;
                    }
                }
            }

            if (linkedCore == null && ownItems != null) {
                this.items = ownItems; 
            }

            if (prevModular != modularCore || prevSlot != activeSlot) {
                if (prevModular != null) prevModular.scheduleRecalculate();
                if (modularCore != null) modularCore.scheduleRecalculate();
            }
        }

        @Override
        public void draw() {
            mb().drawer.draw(this);
        }

        @Override
        public void drawLight() {
            mb().drawer.drawLight(this);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            ModularModuleV2 mb = mb();

            if (mb.ambientEffect != null && Mathf.chance(mb.ambientEffectChance * edelta()))
                spawnEffect(mb.ambientEffect);
            if (activeSlot && mb.activeEffect != null && Mathf.chance(mb.activeEffectChance * edelta()))
                spawnEffect(mb.activeEffect);

            switch (mb.protocol) {
                case HEALING -> updateHealing(mb);
                case ENERGY  -> updateEnergy(mb);
                case ASSAULT -> updateAssault(mb);
                default      -> {}
            }
        }

        @Override
        public void sleep() {
        }

        private void updateHealing(ModularModuleV2 mb) {
            if (!activeSlot || mb.healRate <= 0f) return;

            Building targetCore = modularCore != null
                ? modularCore
                : (linkedCore instanceof CoreBuild cb ? cb : null);
            if (targetCore == null) return;
            if (modularCore != null && modularCore.protocolManager.isHealingBlocked()) return;
            if (targetCore.health < targetCore.maxHealth()) {
                targetCore.heal((mb.healRate * 0.5f) / 60f * edelta());
            }

            if (modularCore != null) {
                for (Building b : modularCore.proximity) {
                    if (b instanceof ModularModuleBuildV2 m && m.health < m.maxHealth())
                        m.heal((mb.healRate * 0.3f) / 60f * edelta());
                }
            }

            if (mb.healEffect != null && Mathf.chance(mb.healEffectChance * edelta()))
                spawnEffect(mb.healEffect);
        }

        private void updateEnergy(ModularModuleV2 mb) {
            boolean active = activeSlot
                && (modularCore != null || linkedCore instanceof CoreBuild);

            warmup         = Mathf.lerpDelta(warmup, active ? 1f : 0f, 0.01f);
            totalProgress += (warmup > 0.001f ? warmup : 0.05f) * Time.delta;
        }

        private void updateAssault(ModularModuleV2 mb) {
            CoreBuild activeCore = coreForAssault();
            if (activeCore == null) return;

            reloadTimer += edelta();
            turretHeat   = Mathf.lerpDelta(turretHeat, 0f, 0.09f);
            recoil       = Mathf.lerpDelta(recoil,     0f, 0.08f);

            Item ammo    = mb.ammoItem;
            boolean hasAmmo = (ammo == null) || (activeCore.items.get(ammo) >= mb.ammoPerShot);

            Unit target = Units.closestEnemy(team, x, y, mb.turretRange, u -> !u.dead);
            if (target == null) return;

            float targetAngle = angleTo(target);
            rotation = Angles.moveToward(rotation, targetAngle, 5f * Time.delta);

            if (reloadTimer >= mb.turretReload
                    && hasAmmo
                    && Angles.angleDist(rotation, targetAngle) < 10f) {
                float sx = x + Angles.trnsx(rotation, 8f);
                float sy = y + Angles.trnsy(rotation, 8f);
                mb.turretBullet.create(this, team, sx, sy, rotation, mb.turretDamage, 1f, 1f, null);
                mb.shootSound.at(x, y, Mathf.random(0.9f, 1.1f));
                if (ammo != null) activeCore.items.remove(ammo, mb.ammoPerShot);
                reloadTimer = 0f;
                turretHeat  = 1f;
                recoil      = 1f;
            }
        }

        private @Nullable CoreBuild coreForAssault() {
            if (modularCore != null && activeSlot) return modularCore;
            if (linkedCore instanceof CoreBuild cb) return cb;
            return null;
        }

        @Override
        public float getPowerProduction() {
            if (mb().protocol != ModuleProtocol.ENERGY) return 0f;
            if (!activeSlot) return 0f;
            if (modularCore != null)               return mb().powerOutput / 60f;
            if (linkedCore instanceof CoreBuild)   return mb().powerOutput / 60f * 0.9f;
            return 0f;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (modularCore != null) return modularCore.acceptItem(source, item);
            return super.acceptItem(source, item);
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (modularCore != null) {
                modularCore.noEffect = true;
                modularCore.handleItem(source, item);
            } else {
                super.handleItem(source, item);
            }
        }

        @Override
        public void itemTaken(Item item) {
            if (modularCore != null) modularCore.itemTaken(item);
            else super.itemTaken(item);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            if (modularCore != null) return modularCore.getMaximumAccepted(item);
            return super.getMaximumAccepted(item);
        }

        @Override
        public int removeStack(Item item, int amount) {
            if (modularCore != null) return modularCore.removeStack(item, amount);
            return super.removeStack(item, amount);
        }

        @Override
        public boolean canUnload() {
            if (modularCore != null) return modularCore.canUnload();
            return super.canUnload();
        }

        @Override
        public double sense(mindustry.logic.LAccess sensor) {
            if (sensor == mindustry.logic.LAccess.itemCapacity && modularCore != null)
                return modularCore.sense(sensor);
            return super.sense(sensor);
        }

        @Override
        public boolean canPickup()   { return modularCore == null && linkedCore == null; }

        @Override
        public boolean allowDeposit() {
            return modularCore != null || linkedCore != null || super.allowDeposit();
        }

        @Override
        public void remove() {
            if (ownItems != null) this.items = ownItems;

            ModularCoreBuildV2 core = modularCore;
            modularCore = null;
            linkedCore  = null;
            activeSlot  = false;

            super.remove();

            if (core != null) Time.run(1f, core::scheduleRecalculate);
        }

        @Override
        public void write(Writes write) {
            ItemModule current = this.items;
            if (ownItems != null) this.items = ownItems;
            super.write(write);
            this.items = current;

            write.bool(activeSlot);
            if (mb().protocol == ModuleProtocol.ENERGY) {
                write.f(warmup);
                write.f(totalProgress);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            activeSlot = read.bool();
            if (mb().protocol == ModuleProtocol.ENERGY) {
                warmup        = read.f();
                totalProgress = read.f();
            }
        }

        private void spawnEffect(Effect effect) {
            float spread = block.size * 4f;
            effect.at(x + Mathf.range(spread), y + Mathf.range(spread));
        }

        public boolean isHealActive() {
            if (!activeSlot) return false;
            if (modularCore != null)
                return !modularCore.protocolManager.isHealingBlocked();
            return linkedCore instanceof CoreBuild;
        }
    }
}
