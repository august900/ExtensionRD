package net.minecraft.src;

import java.util.Random;

public class WorldGenLilies extends WorldGenerator {
    private int lilyBlockId;

    public WorldGenLilies(int lilyBlockId) {
        this.lilyBlockId = lilyBlockId;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        for (int i = 0; i < 10; ++i) {
            int patchX = x + rand.nextInt(4) - rand.nextInt(4);
            int patchZ = z + rand.nextInt(4) - rand.nextInt(4);

            int patchY = findWaterSurface(world, patchX, patchZ);

            if (patchY > 0) {
                int blockBelow = world.getBlockId(patchX, patchY - 1, patchZ);
                if ((blockBelow == Block.waterStill.blockID || blockBelow == Block.ice.blockID) 
                        && world.getBlockId(patchX, patchY, patchZ) == 0) {
                    
                    world.setBlockWithNotify(patchX, patchY, patchZ, this.lilyBlockId);
                }
            }
        }
        return true;
    }

    private int findWaterSurface(World world, int x, int z) {
        for (int y = 80; y > 40; --y) {
            int id = world.getBlockId(x, y, z);
            if (id == Block.waterStill.blockID || id == Block.waterMoving.blockID) {
                return y + 1;
            }
        }
        return -1;
    }
}