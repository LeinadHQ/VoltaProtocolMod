package voltaprotocol.expand.maps;

import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Tiles;
import mindustry.world.WorldParams;
import mindustry.world.TileGen;
import voltaprotocol.content.VPBlocks;

public class VoltaPlanetGenerator extends PlanetGenerator {

    @Override
        public void genTile(Vec3 position, TileGen tile){

            float noise = Simplex.noise3d(
                seed,
                4,
                0.5f,
                0.5f,
                position.x,
                position.y,
                position.z
            );

            if(noise > 0.2f){
                tile.floor = VPBlocks.argentAndesite;
                tile.block = VPBlocks.argentAndesiteWall;
            }else{
                tile.floor = VPBlocks.darkAndesite;
                tile.block = VPBlocks.darkAndesiteWall;
            }
        }

    @Override
    public void generate(Tiles tiles, Sector sector, WorldParams params) {

        if (sector.id == 0) {
            return;
        }

        for (int x = 0; x < tiles.width; x++) {
            for (int y = 0; y < tiles.height; y++) {
                tiles.get(x, y).setFloor(VPBlocks.darkAndesite.asFloor());

                float oreNoise = noise(x, y, 20.0, 1.0);
               
                if (oreNoise > 0.78f) {
                    tiles.get(x, y).setOverlay(VPBlocks.silverOre);
                } else if (oreNoise > 0.84f) {
                    tiles.get(x, y).setOverlay(VPBlocks.palladiumOre);
                }
            }
        }
    }

    @Override
    public float getHeight(Vec3 position) {

        float noise = Simplex.noise3d(
            seed,
            3,
            0.45f,
            0.6f,
            position.x,
            position.y,
            position.z
        );

        return 0.5f + (noise * 0.04f);
    }
}
