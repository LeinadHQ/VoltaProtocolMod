package voltaprotocol.expand.maps;

import static mindustry.Vars.*;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import arc.util.noise.Ridged;

import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.game.Schematics;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Tile;
import mindustry.world.TileGen;

import voltaprotocol.content.VPBlocks;

public class VoltaPlanetGenerator extends PlanetGenerator {

    BaseGenerator basegen = new BaseGenerator();
    public static final Color andesitaOscura    = Color.valueOf("2d2d31");
    public static final Color plataConductiva   = Color.valueOf("9aa4b2");
    public static final Color miasmaConcentrado = Color.valueOf("3a0d25");
    public static final Color nieveCumbre       = Color.valueOf("dce8f0");
    public static final Color oxicloruroColor   = Color.valueOf("2a5c3f");
    public static final Color musgoSerpulo      = Color.valueOf("4a2060");
    public static final Color arenaOscuraColor  = Color.valueOf("3d3228");
    public static Interp interp = new Interp.Exp(2, 3);

    @Override
    public float getHeight(Vec3 position) {
        float noise    = Simplex.noise3d(seed, 3, 0.5f, 0.3f, position.x, position.y, position.z);
        float latitude = Math.abs(position.y);
        return Mathf.clamp(noise * 0.5f + latitude * 0.4f, 0.05f, 0.95f);
    }

    public Color getColor(Vec3 position) {
        float depth = getHeight(position);
        float patch = Simplex.noise3d(seed + 50, 3, 0.4f, 0.5f,
                position.x * 2, position.y * 2, position.z * 2);

        float detailA = Simplex.noise3d(seed + 80, 2, 0.5f, 0.8f,
                position.x * 3, position.y * 3, position.z * 3);
        float detailB = Simplex.noise3d(seed + 130, 2, 0.6f, 0.6f,
                position.x * 4, position.y * 4 + 1f, position.z * 4);

        Color base;

        if (depth > 0.85f) {
            base = nieveCumbre;
        } else if (depth > 0.78f && patch > 0.3f) {
            base = Tmp.c2.set(nieveCumbre).lerp(plataConductiva, 0.4f);
        }
        else if (depth < 0.45f && detailB > 0.72f) {
            base = oxicloruroColor;
        }
        else if (depth > 0.30f && depth < 0.60f && detailA > 0.60f && patch < 0.35f) {
            base = musgoSerpulo;
        }
        else if (depth < 0.38f && patch > 0.50f && detailA < 0.40f) {
            base = arenaOscuraColor;
        }
        else if (patch > 0.45f) {
            base = plataConductiva;
        } else if (patch > 0.15f) {
            base = miasmaConcentrado;
        } else {
            base = andesitaOscura;
        }

        return Tmp.c1.set(base).mul(Mathf.clamp(0.4f + depth * 0.6f, 0.3f, 1f));
    }

@Override
    public void genTile(Vec3 position, TileGen tile) {
        float h     = getHeight(position);
        float patch = Simplex.noise3d(seed + 50, 3, 0.4f, 0.5f,
                position.x * 2, position.y * 2, position.z * 2);

        float iceVar = Simplex.noise3d(seed + 110, 2, 0.6f, 0.6f,
                position.x * 5, position.y * 5, position.z * 5);

        if (h > 0.85f) {
            if      (iceVar > 0.50f)  tile.floor = Blocks.ice;
            else if (iceVar > 0.15f)  tile.floor = Blocks.iceSnow;
            else                      tile.floor = Blocks.snow;

        } else if (h > 0.70f) {
            if      (iceVar > 0.55f)  tile.floor = Blocks.ice;
            else if (iceVar > 0.28f)  tile.floor = Blocks.iceSnow;
            else                      tile.floor = Blocks.snow;

        } else if (patch > 0.60f) {
            tile.floor = VPBlocks.conductiveSand.asFloor();
        } else if (h > 0.45f) {
            tile.floor = VPBlocks.argentAndesite.asFloor();
        } else {
            tile.floor = VPBlocks.darkAndesite.asFloor();
        }

        if      (tile.floor == VPBlocks.argentAndesite)                 tile.block = VPBlocks.argentAndesiteWall;
        else if (tile.floor == VPBlocks.conductiveSand)                 tile.block = VPBlocks.conductiveSandWall;
        else if (tile.floor == VPBlocks.darkAndesite)                   tile.block = VPBlocks.darkAndesiteWall;
        else if (tile.floor == Blocks.ice || tile.floor == Blocks.iceSnow) tile.block = Blocks.iceWall;
        else if (tile.floor == Blocks.snow)                             tile.block = Blocks.snowWall;
        else                                                            tile.block = tile.floor.asFloor().wall;

        if (Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31f) {
            tile.block = Blocks.air;
        }
    }

    @Override
    protected void generate() {

        cells(4);
        distort(10f, 14f);

        class Room {
            int x, y, radius;
            ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius) {
                this.x = x; this.y = y; this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2) {
                float nscl  = rand.random(70f, 120f);
                int   stroke = rand.random(6, 11);
                brush(
                    pathfind(x1, y1, x2, y2,
                        t -> (t.solid() ? 50f : 0f)
                           + noise(t.x, t.y, 2, 0.4f, 1f / nscl) * 500f,
                        Astar.manhattan),
                    stroke
                );
            }

            void connect(Room to) {
                if (!connected.add(to) || to == this) return;
                Vec2 mid = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                mid.add(Tmp.v2.setToRandomDirection(rand)
                              .scl(Tmp.v1.dst(x, y) * 0.4f));
                mid.sub(width / 2f, height / 2f)
                   .limit(width / 2f / Mathf.sqrt3)
                   .add(width / 2f, height / 2f);
                int mx = (int) mid.x, my = (int) mid.y;
                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }
        }

        float mapRadius = width / 2f / Mathf.sqrt3;
        float constraint = 1.4f;

        int rooms = rand.random(4, 7);
        Seq<Room> roomseq = new Seq<>();

        for (int i = 0; i < rooms; i++) {
            Tmp.v1.trns(rand.random(360f), rand.random(mapRadius / constraint));
            float rx   = width  / 2f + Tmp.v1.x;
            float ry   = height / 2f + Tmp.v1.y;
            float maxr = mapRadius - Tmp.v1.len();

            float rrad = Math.min(rand.random(16f, maxr / 1.4f), 40f);
            roomseq.add(new Room((int) rx, (int) ry, (int) rrad));
        }

        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int   enemySpawns = rand.random(1, Math.max((int)(sector.threat * 4), 1));
        int   offset      = rand.nextInt(360);
        float spawnLength = width / 2.55f - rand.random(10, 20);
        int   angleStep   = 5;
        int   waterCheckR = 4;

        for (int i = 0; i < 360; i += angleStep) {
            int angle = offset + i;
            int cx = (int)(width  / 2 + Angles.trnsx(angle, spawnLength));
            int cy = (int)(height / 2 + Angles.trnsy(angle, spawnLength));

            int waterTiles = 0;
            for (int rx = -waterCheckR; rx <= waterCheckR; rx++) {
                for (int ry = -waterCheckR; ry <= waterCheckR; ry++) {
                    Tile t = tiles.get(cx + rx, cy + ry);
                    if (t == null || t.floor().liquidDrop != null) waterTiles++;
                }
            }

            if (waterTiles <= 4 || (i + angleStep >= 360)) {
                roomseq.add(spawn = new Room(cx, cy, rand.random(14, 22)));

                for (int j = 0; j < enemySpawns; j++) {
                    float eoff = rand.range(60f);
                    Tmp.v1.set(cx - width / 2, cy - height / 2)
                          .rotate(180f + eoff)
                          .add(width / 2f, height / 2f);

                    Room espawn = new Room((int) Tmp.v1.x, (int) Tmp.v1.y,
                                          rand.random(12, 20));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }
                break;
            }
        }

        for (Room room : roomseq) {
            erase(room.x, room.y, room.radius);
        }

        for (Room room : roomseq) {
            if (room != spawn) spawn.connect(room);
        }
        int extras = rand.random(2, rooms + 2);
        for (int i = 0; i < extras; i++) {
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        Room fspawn = spawn;

        cells(1);
        distort(5f, 6f);

        pass((x, y) -> {
            if (floor == VPBlocks.darkAndesite
                    && noise(x, y, 3, 0.4f, 14f, 1f) > 0.57f) {
                block = VPBlocks.darkAndesiteWall;
            }
            if (floor == VPBlocks.argentAndesite
                    && noise(x + 400, y, 3, 0.4f, 13f, 1f) > 0.60f) {
                block = VPBlocks.argentAndesiteWall;
            }
        });

        pass((x, y) -> {

            if (Mathf.within(x, y, fspawn.x, fspawn.y, 18)) return;

            float fissure = Ridged.noise2d(seed, x * 0.018, y * 0.018, 3, 1.0);
            if (fissure > 0.92f) {
                floor = VPBlocks.voltaicMagma.asFloor();
                if (block.solid && rand.chance(0.06)) block = VPBlocks.darkAndesiteWall;
                else if (block.solid)                 block = Blocks.air;
            } else if (fissure > 0.87f) {
                floor = VPBlocks.pyroclasticAndesite.asFloor();
            }

            float lavaLake = noise(x + 200, y - 200, 3, 0.7f, 60f, 1f);
            if (lavaLake > 0.85f) {
                floor = Blocks.slag;
                if (block.solid) block = Blocks.air;
            }

            float tarLake = noise(x - 300, y + 400, 3, 0.7f, 50f, 1f);
            if (tarLake > 0.87f) {
                floor = Blocks.tar;
                if (block.solid) block = Blocks.air;
            }

            float toxicPool = noise(x + 500, y - 500, 3, 0.6f, 40f, 1f);
            if (toxicPool > 0.80f) {
                floor = VPBlocks.oxychlorideFloor;
                if (block.solid) block = Blocks.air;
            }
            if (block == Blocks.air
                && (floor == Blocks.ice || floor == Blocks.iceSnow || floor == Blocks.snow)) {
                float snowDetail = noise(x - 100, y + 300, 3, 0.60f, 55f, 1f);

                if      (snowDetail > 0.73f) floor = Blocks.water;
                else if (snowDetail > 0.65f) floor = Blocks.moss;
                else if (snowDetail > 0.57f) floor = Blocks.sporeMoss;
            }

            if (floor.asFloor().isLiquid && block.solid) block = Blocks.air;
        });

        median(2, 0.6, VPBlocks.voltaicMagma.asFloor());
        median(2, 0.6, VPBlocks.oxychlorideFloor);
        median(3, 0.6, Blocks.slag);

        FloatSeq oreFreqs = new FloatSeq();
        for (int i = 0; i < 9; i++) {
            oreFreqs.add(rand.random(-0.04f, 0.01f) - i * 0.008f);
        }

        pass((x, y) -> {

            if (block != Blocks.air) {
                if (!nearAir(x, y)) return;

                if (floor == VPBlocks.voltaicMagma
                        || floor == VPBlocks.pyroclasticAndesite) return;

                float wallOre = noise(x + 100, y + 100, 3, 0.5f, 22f, 1f);
                float dst = Mathf.dst(x, y, fspawn.x, fspawn.y) / (width / 2f);

                if      (wallOre > 0.84f && dst > 0.40f && countWalls(x, y) > 2)
                    ore = Blocks.wallOreTungsten;
                else if (wallOre > 0.80f && countWalls(x, y) > 2)
                    ore = Blocks.wallOreBeryllium;
                else if (wallOre > 0.73f)
                    block = Blocks.graphiticWall;
                return;
            }

            if (floor.asFloor().isLiquid) return;
            if (floor == VPBlocks.voltaicMagma
                    || floor == VPBlocks.pyroclasticAndesite) return;
            if (nearWall(x, y)) return;

            float dst = Mathf.dst(x, y, fspawn.x, fspawn.y) / (width / 2f);
            int ox = x - 4, oy = y + 23;

            if (dst < 0.35f) {
                if (Math.abs(0.5f - noise(ox, oy,        2, 0.7f, 40f, 1f)) > 0.24f &&
                    Math.abs(0.5f - noise(ox, oy,        1,    1, 30f, 1f)) > 0.41f + oreFreqs.get(0))
                    { ore = Blocks.oreCopper;   return; }

                if (Math.abs(0.5f - noise(ox, oy + 999,  2, 0.7f, 42f, 1f)) > 0.25f &&
                    Math.abs(0.5f - noise(ox, oy - 999,  1,    1, 32f, 1f)) > 0.42f + oreFreqs.get(1))
                    { ore = Blocks.oreLead;     return; }

                if (Math.abs(0.5f - noise(ox, oy + 1998, 2, 0.7f, 44f, 1f)) > 0.26f &&
                    Math.abs(0.5f - noise(ox, oy - 1998, 1,    1, 34f, 1f)) > 0.43f + oreFreqs.get(2))
                    { ore = Blocks.oreCoal;     return; }
            }
            else if (dst < 0.75f) {
                if (Math.abs(0.5f - noise(ox, oy + 2997, 2, 0.7f, 40f, 1f)) > 0.24f &&
                    Math.abs(0.5f - noise(ox, oy - 2997, 1,    1, 30f, 1f)) > 0.41f + oreFreqs.get(3))
                    { ore = Blocks.oreTitanium; return; }

                if (Math.abs(0.5f - noise(ox, oy + 3996, 2, 0.7f, 42f, 1f)) > 0.25f &&
                    Math.abs(0.5f - noise(ox, oy - 3996, 1,    1, 32f, 1f)) > 0.42f + oreFreqs.get(4))
                    { ore = Blocks.oreBeryllium; return; }

                if (Math.abs(0.5f - noise(ox, oy + 4995, 2, 0.7f, 44f, 1f)) > 0.26f &&
                    Math.abs(0.5f - noise(ox, oy - 4995, 1,    1, 34f, 1f)) > 0.43f + oreFreqs.get(5))
                    { ore = VPBlocks.silverOre; return; }
            }
            else {
                if (Math.abs(0.5f - noise(ox, oy + 5994, 2, 0.7f, 40f, 1f)) > 0.24f &&
                    Math.abs(0.5f - noise(ox, oy - 5994, 1,    1, 30f, 1f)) > 0.41f + oreFreqs.get(6))
                    { ore = Blocks.oreThorium;  return; }

                if (Math.abs(0.5f - noise(ox, oy + 6993, 2, 0.7f, 42f, 1f)) > 0.25f &&
                    Math.abs(0.5f - noise(ox, oy - 6993, 1,    1, 32f, 1f)) > 0.42f + oreFreqs.get(7))
                    { ore = Blocks.oreTungsten; return; }

                if (Math.abs(0.5f - noise(ox, oy + 7992, 2, 0.7f, 44f, 1f)) > 0.26f &&
                    Math.abs(0.5f - noise(ox, oy - 7992, 1,    1, 34f, 1f)) > 0.43f + oreFreqs.get(8))
                    { ore = VPBlocks.palladiumOre; return; }
            }
        });

        for (Tile tile : tiles) {
            if (tile.overlay().needsSurface && !tile.floor().hasSurface()) {
                tile.setOverlay(Blocks.air);
            }
        }

        trimDark();
        median(2);
        inverseFloodFill(tiles.getn(fspawn.x, fspawn.y));

int borderDepth = 8;
        pass((x, y) -> {
            int edgeDist = Math.min(Math.min(x, y), Math.min(width - 1 - x, height - 1 - y));
            if (edgeDist >= borderDepth) return;

            float organicThresh = 1f + noise(x, y, 2, 0.5f, 20f, 1f) * (borderDepth - 1f);
            if (edgeDist < (int) organicThresh) {
                if (floor == VPBlocks.darkAndesite || floor == VPBlocks.pyroclasticAndesite
                        || floor.asFloor().isLiquid) {
                    floor = VPBlocks.darkAndesite;
                    block = VPBlocks.darkAndesiteWall;
                } else if (floor == VPBlocks.argentAndesite) {
                    block = VPBlocks.argentAndesiteWall;
                } else if (floor == VPBlocks.conductiveSand) {
                    block = VPBlocks.conductiveSandWall;
                } else if (floor == Blocks.snow || floor == Blocks.ice) {
                    block = Blocks.iceWall;
                } else {
                    floor = VPBlocks.darkAndesite;
                    block = VPBlocks.darkAndesiteWall;
                }
                ore = Blocks.air;
            }
        });

        erase(fspawn.x, fspawn.y, 20);
        Schematics.placeLaunchLoadout(fspawn.x, fspawn.y);

        Seq<Tile> enemyTiles = new Seq<>();
        for (Room r : enemies) {
            Tile t = tiles.getn(r.x, r.y);
            if (t != null) enemyTiles.add(t);
        }

        if (sector.hasEnemyBase()) {
            if (enemyTiles.any()) {
                basegen.generate(
                    tiles, enemyTiles, tiles.get(fspawn.x, fspawn.y),
                    Team.crux, sector, sector.threat
                );
            }
            state.rules.attackMode = true;
        } else {
            for (Room r : enemies) {
                Tile t = tiles.getn(r.x, r.y);
                if (t != null) t.setOverlay(Blocks.spawn);
            }
            state.rules.winWave = 10 + 5 * (int) Math.max(sector.threat * 10, 1);
        }

        state.rules.waves       = true;
        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2f, 60f * 60f,
                                             Mathf.clamp(sector.threat));
        state.rules.env         = sector.planet.defaultEnv;
    }

    private int countWalls(int x, int y) {
        int total = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Tile t = tiles.get(x + dx, y + dy);
                if (t != null && t.solid()) total++;
            }
        }
        return total;
    }
}
