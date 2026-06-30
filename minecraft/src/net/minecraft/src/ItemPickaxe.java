package net.minecraft.src;

public class ItemPickaxe extends ItemTool {
	private static Block[] blocksEffectiveAgainst = new Block[]{Block.cobblestone, Block.stairDouble, Block.stairSingle, Block.stone, Block.cobblestoneMossy, Block.oreIron, Block.blockSteel, Block.oreCoal, Block.blockGold, Block.oreGold, Block.oreDiamond, Block.blockDiamond, Block.ice};
	private int harvestLevel;

	public ItemPickaxe(int var1, int var2) {
		super(var1, 2, var2, blocksEffectiveAgainst);
		this.harvestLevel = var2;
	}

	public boolean canHarvestBlock(Block var1) {
		 if (var1 == Block.obsidian || var1 == Block.oreRuby || var1 == Block.blockRuby) {
	         return this.harvestLevel == 3;
	     }
		
		 if (var1 == Block.blockDiamond || var1 == Block.oreDiamond || 
			        var1 == Block.blockGold    || var1 == Block.oreGold    || 
			        var1 == Block.oreRedstone  || var1 == Block.oreRedstoneGlowing) {
			        return this.harvestLevel >= 2;
		 }
		 
		 if (var1 == Block.blockSteel || var1 == Block.oreIron) {
		        return this.harvestLevel >= 1;
		 }
		 
		 if (var1.material == Material.rock) {
		        return true; 
		 }

		 return var1.material == Material.iron;
	}
}
