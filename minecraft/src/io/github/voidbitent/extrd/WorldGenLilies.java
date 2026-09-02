package io.github.voidbitent.extrd;

import java.util.Random;
import net.minecraft.src.*;

public class WorldGenLilies extends WorldGenerator {
    private int lilyBlockId;

    public WorldGenLilies(int lilyBlockId) {
        this.lilyBlockId = lilyBlockId;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
    	int numberOfLilies = rand.nextInt(10) + 9;
        for (int i = 0; i < numberOfLilies; ++i) {
            int patchX = x + rand.nextInt(8) - rand.nextInt(8);
            int patchZ = z + rand.nextInt(8) - rand.nextInt(8);


            int patchY = world.getTopSolidOrLiquidBlock(patchX, patchZ);

            int blockBelow = world.getBlockId(patchX, patchY - 1, patchZ);
            if (blockBelow == Block.waterStill.blockID || blockBelow == Block.ice.blockID) {
                
                if (world.getBlockId(patchX, patchY, patchZ) == 0) {
                    world.setBlockWithNotify(patchX, patchY, patchZ, this.lilyBlockId);
                }
            }
        }

        return true;
    }
}