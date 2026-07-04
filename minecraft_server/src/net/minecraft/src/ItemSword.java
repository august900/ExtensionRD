package net.minecraft.src;

public class ItemSword extends Item {
	private int weaponDamage;
	private boolean isWaraxe;

	public ItemSword(int var1, int var2, boolean isW) {
		super(var1);
		this.maxStackSize = 1;
		this.maxDamage = 32 << var2;
		this.isWaraxe = isW;
		if(var2 == 3) {
			this.maxDamage *= 4;
		}

		this.weaponDamage = 4 + var2 * 2;
	}

	private boolean isBlockInBEA(Block[] var1, Block var2) {
		for(int i = 0;i < var1.length;i++) {
			if(var2 == var1[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	public float getStrVsBlock(ItemStack var1, Block var2) {
		if(!isWaraxe) {
			return 1.5F;
		} else {
			Block[] blocksEffectiveAgainst = new Block[]{Block.planks, Block.bookshelf, Block.wood, Block.chest, Block.ladder, Block.stairCompactWood, Block.signStanding, Block.signWall};
			if(isBlockInBEA(blocksEffectiveAgainst, var2)) {
				return 7.0f;
			} else {
				return 1.5F;
			}
		}
	}

	public void onBlockDestroyed(ItemStack var1, int var2, int var3, int var4, int var5) {
		var1.damageItem(2);
	}
}
