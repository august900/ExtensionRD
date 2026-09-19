package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityPlayerSP extends EntityPlayer {
	public MovementInput movementInput;
	private Minecraft mc;
	private int shadowSpawnTimer = 0;

	public EntityPlayerSP(Minecraft var1, World var2, Session var3) {
		super(var2);
		this.mc = var1;
		if(var3 != null && var3.username != null && var3.username.length() > 0) {
			this.skinUrl = "http://www.minecraft.net/skin/" + var3.username + ".png";
			System.out.println("Loading texture " + this.skinUrl);
		}

		this.username = var3.username;
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump;
	}

	public void onLivingUpdate() {
		this.movementInput.updatePlayerMoveState(this);
		if(this.movementInput.sneak && this.ySize < 0.2F) {
			this.ySize = 0.2F;
		}

		super.onLivingUpdate();
		
		if (!this.worldObj.multiplayerWorld) {
	        shadowSpawnTimer  ++;
	        if (shadowSpawnTimer >= 400) {
	            shadowSpawnTimer = 0;
	            if (this.rand.nextFloat() < 0.50F) {
	                trySpawnShadowInSight();
	            }
	        }
	    }
	}
	
	private void trySpawnShadowInSight() {
	    float offsetAngle = (this.rand.nextFloat() - 0.5F) * 90.0F;
	    float spawnYaw = this.rotationYaw + offsetAngle;
	    double distance = 21.0D + (this.rand.nextDouble() * 10.0D);

	    double lookX = -Math.sin(spawnYaw * Math.PI / 180.0D);
	    double lookZ = Math.cos(spawnYaw * Math.PI / 180.0D);

	    double targetX = this.posX + (lookX * distance);
	    double targetZ = this.posZ + (lookZ * distance);

	    int xFloor = MathHelper.floor_double(targetX);
	    int zFloor = MathHelper.floor_double(targetZ);
	    int yGround = this.worldObj.getHeightValue(xFloor, zFloor);

	    if (yGround <= 0) {
	        //System.out.println("[ShadowSpawner] Failed: Invalid ground Y");
	        return;
	    }

	    EntityShadowPlayer shadow = new EntityShadowPlayer(this.worldObj, targetX, (double)yGround, targetZ);
	    
	    Vec3D eyePos = Vec3D.createVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
	    Vec3D targetEyePos = Vec3D.createVector(targetX, (double)yGround + 1.62D, targetZ);

	    MovingObjectPosition hit = this.worldObj.rayTraceBlocks(eyePos, targetEyePos);

	    if (hit != null) {
	        //System.out.println("[ShadowSpawner] Sight ray hit block ID: " + this.worldObj.getBlockId(hit.blockX, hit.blockY, hit.blockZ));
	    }

	    boolean spawned = this.worldObj.spawnEntityInWorld(shadow);
	    
	    if (spawned) {
	        //System.out.println("[ShadowSpawner] SUCCESS! Shadow Player spawned at X: " + (int)targetX + " Y: " + yGround + " Z: " + (int)targetZ);
	    	System.out.println(" has connected.");
	    } else {
	        //System.out.println("[ShadowSpawner] Failed: world.spawnEntityInWorld returned false");
	    }
	}

	public void resetPlayerKeyState() {
		this.movementInput.resetKeyState();
	}

	public void handleKeyPress(int var1, boolean var2) {
		this.movementInput.checkKeyForMovementInput(var1, var2);
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("Score", this.score);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.score = var1.getInteger("Score");
	}

	public void displayGUIChest(IInventory var1) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
	}

	public void displayGUIEditSign(TileEntitySign var1) {
		this.mc.displayGuiScreen(new GuiEditSign(var1));
	}

	public void displayWorkbenchGUI() {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory));
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
	}

	public void attackEntity(Entity var1) {
		int var2 = this.inventory.getDamageVsEntity(var1);
		if(var2 > 0) {
			var1.attackEntityFrom(this, var2);
			ItemStack var3 = this.getCurrentEquippedItem();
			if(var3 != null && var1 instanceof EntityLiving) {
				var3.hitEntity((EntityLiving)var1);
				if(var3.stackSize <= 0) {
					var3.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}
		}

	}

	public void onItemPickup(Entity var1, int var2) {
		this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
	}

	public int getPlayerArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	public void interactWithEntity(Entity var1) {
		if(!var1.interact(this)) {
			ItemStack var2 = this.getCurrentEquippedItem();
			if(var2 != null && var1 instanceof EntityLiving) {
				var2.useItemOnEntity((EntityLiving)var1);
				if(var2.stackSize <= 0) {
					var2.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}

		}
	}

	public void sendChatMessage(String var1) {
	}

	public void onPlayerUpdate() {
	}

	public boolean isSneaking() {
		return this.movementInput.sneak;
	}
}
