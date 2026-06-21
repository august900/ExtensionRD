package net.minecraft.src;

import java.io.File;

public class WorldServer extends World {
	public ChunkProviderServer chunkProviderServer;
	public boolean disableSpawnProtection = false;
	public boolean levelSaving;

	public WorldServer(File var1, String var2) {
		super(var1, var2);
	}

	protected IChunkProvider getChunkProvider(File var1) {
		this.chunkProviderServer = new ChunkProviderServer(this, new ChunkLoader(var1, true), new ChunkProviderGenerate(this, this.randomSeed));
		return this.chunkProviderServer;
	}

	public boolean setBlockAndMetadataWithNotify(int var1, int var2, int var3, int var4, int var5) {
		if(!this.disableSpawnProtection) {
			int var6 = var1 - this.spawnX;
			int var7 = var3 - this.spawnZ;
			if(var6 < 0) {
				var6 = -var6;
			}

			if(var7 < 0) {
				var7 = -var7;
			}

			if(var6 > var7) {
				var7 = var6;
			}

			if(var7 < 16) {
				return false;
			}
		}

		return super.setBlockAndMetadataWithNotify(var1, var2, var3, var4, var5);
	}

	public boolean setBlockWithNotify(int var1, int var2, int var3, int var4) {
		if(!this.disableSpawnProtection) {
			int var5 = var1 - this.spawnX;
			int var6 = var3 - this.spawnZ;
			if(var5 < 0) {
				var5 = -var5;
			}

			if(var6 < 0) {
				var6 = -var6;
			}

			if(var5 > var6) {
				var6 = var5;
			}

			if(var6 < 16) {
				return false;
			}
		}

		return super.setBlockWithNotify(var1, var2, var3, var4);
	}
}
