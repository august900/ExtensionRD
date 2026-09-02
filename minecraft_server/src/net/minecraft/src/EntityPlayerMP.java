package net.minecraft.src;

import java.util.HashSet;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.MinecraftServer;

public class EntityPlayerMP extends EntityPlayer {
	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public ItemInWorldManager theItemInWorldManager;
		private int lastHealth = 20;
					public static Logger logger = Logger.getLogger("Minecraft");
		/** Server-side regeneration ticker used when monsters are disabled in world settings. */
		private int regenTicker = 0;
	public double managedPosX;
	public double managedPosZ;
	public List loadedChunks = new LinkedList();
	public Set loadChunks = new HashSet();
	public double managedPosY;

	
	
	public EntityPlayerMP(MinecraftServer var1, World var2, String var3, ItemInWorldManager var4) {
		super(var2);
		int var5 = var2.spawnX + this.rand.nextInt(20) - 10;
		int var6 = var2.spawnZ + this.rand.nextInt(20) - 10;
		int var7 = var2.getTopSolidOrLiquidBlock(var5, var6);
		this.setLocationAndAngles((double)var5 + 0.5D, (double)var7, (double)var6 + 0.5D, 0.0F, 0.0F);
		this.mcServer = var1;
		this.stepHeight = 0.0F;
		var4.thisPlayer = this;
		this.username = var3;
		this.theItemInWorldManager = var4;
		this.yOffset = 0.0F;
		this.lastHealth = this.health;
	}

	public void onUpdate() {
	}

	public void onDeath(Entity var1) {
	}

	@Override
	public boolean attackEntityFrom(Entity entity, int damage) {
	    if (super.attackEntityFrom(entity, damage)) {
	        // Send updated health to the client immediately after taking damage
	        if (this.playerNetServerHandler != null) {
	            this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
	        }
	        return true;
	    }
	    return false;
	}

	@Override
	public void heal(int amount) {
	    super.heal(amount);
	    // Send updated health to the client immediately after healing
	    if (this.playerNetServerHandler != null) {
	        this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
	    }
		// Debug log for server-side heals
		if (logger != null) {
			logger.info("Healed player " + this.username + " by " + amount + ", health now " + this.health + " (server-side)");
		}
	}

	public void onUpdateEntity() {
		super.onUpdate();
		ChunkCoordIntPair var1 = null;
		double var2 = 0.0D;

		for(int var4 = 0; var4 < this.loadedChunks.size(); ++var4) {
			ChunkCoordIntPair var5 = (ChunkCoordIntPair)this.loadedChunks.get(var4);
			double var6 = var5.a(this);
			if(var4 == 0 || var6 < var2) {
				var1 = var5;
				var2 = var5.a(this);
			}
		}

		if(var1 != null) {
			boolean var8 = false;
			if(var2 < 1024.0D) {
				var8 = true;
			}

			if(this.playerNetServerHandler.getNumChunkDataPackets() < 2) {
				var8 = true;
			}

			if(var8) {
				this.loadedChunks.remove(var1);
				this.playerNetServerHandler.sendPacket(new Packet51MapChunk(var1.chunkXPos * 16, 0, var1.chunkZPos * 16, 16, 128, 16, this.mcServer.worldMngr));
				List var9 = this.mcServer.worldMngr.getTileEntityList(var1.chunkXPos * 16, 0, var1.chunkZPos * 16, var1.chunkXPos * 16 + 16, 128, var1.chunkZPos * 16 + 16);

				for(int var10 = 0; var10 < var9.size(); ++var10) {
					TileEntity var7 = (TileEntity)var9.get(var10);
					this.playerNetServerHandler.sendPacket(new Packet59ComplexEntity(var7.xCoord, var7.yCoord, var7.zCoord, var7));
				}
			}
		}

						// Send health updates to client if changed
																					// Regenerate health on server when monsters are disabled in the world settings
																																																																					if (this.worldObj instanceof WorldServer) {
																																																																						WorldServer ws = (WorldServer)this.worldObj;
																																																																						if (!ws.getMonsters()) {
																							this.regenTicker++;
																							// every 80 ticks (4 seconds) heal 1 HP if not at max
																							if (this.regenTicker >= 80) {
																								this.regenTicker = 0;
																								if (this.health < 20) {
																									this.heal(1);
																								}
																							}
																																																																						} else {
																							// reset ticker when monsters enabled
																							this.regenTicker = 0;
																						}
																					}
																					// Send health updates to client if changed
																					if (this.playerNetServerHandler != null && this.health != this.lastHealth) {
																						this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
																						this.lastHealth = this.health;
																					}

						// Simple death/respawn handling: if player is dead, respawn at world spawn
						if (this.health <= 0 && !this.isDead) {
							// perform death logic
							super.onDeath((Entity)null);
							// respawn at spawn
							int spawnX = this.mcServer.worldMngr.spawnX;
							int spawnZ = this.mcServer.worldMngr.spawnZ;
							int spawnY = this.mcServer.worldMngr.getTopSolidOrLiquidBlock(spawnX, spawnZ);
							this.setLocationAndAngles((double)spawnX + 0.5D, (double)spawnY + 1.5D, (double)spawnZ + 0.5D, 0.0F, 0.0F);
							this.health = 20;
							this.lastHealth = this.health;
													// Clear death flags so the player can move again and be updated by the world
													this.isDead = false;
													try {
														// EntityLiving has a protected 'dead' flag; clear it if present
														java.lang.reflect.Field f = this.getClass().getSuperclass().getDeclaredField("dead");
														f.setAccessible(true);
														f.setBoolean(this, false);
													} catch (Throwable ignored) {
														// ignore if field not present
													}
													this.deathTime = 0;
													this.hurtTime = 0;
													this.motionX = this.motionY = this.motionZ = 0.0D;
													this.onGround = true;
							if (this.playerNetServerHandler != null) {
								this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
								this.playerNetServerHandler.sendPacket(new Packet13PlayerLookMove(this.posX, this.posY + (double)1.62F, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, false));
							}
						}

	}

	public void onLivingUpdate() {
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.isJumping = false;
		super.onLivingUpdate();
	}

	public void onItemPickup(Entity var1, int var2) {
		if(!var1.isDead && var1 instanceof EntityItem) {
			this.playerNetServerHandler.sendPacket(new Packet17AddToInventory(((EntityItem)var1).item, var2));
			this.mcServer.entityTracker.sendPacketToTrackedPlayers(var1, new Packet22Collect(var1.entityID, this.entityID));
		}

		super.onItemPickup(var1, var2);
	}

	public void swingItem() {
		if(!this.isSwinging) {
			this.swingProgressInt = -1;
			this.isSwinging = true;
			this.mcServer.entityTracker.sendPacketToTrackedPlayers(this, new Packet18ArmAnimation(this, 1));
		}

	}

	/**
	 * Server-side implementation for attacking an entity initiated by this player.
	 */
	public void attackEntity(Entity var1) {
		ItemStack current = this.getCurrentEquippedItem();
		int var2 = current != null ? current.getDamageVsEntity(var1) : 1;
		if(var2 > 0) {
			var1.attackEntityFrom(this, var2);
			if(current != null && var1 instanceof EntityLiving) {
				current.hitEntity((EntityLiving)var1);
				if(current.stackSize <= 0) {
					current.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}
		}
	}

	/**
	 * Server-side implementation for using the current equipped item on an entity
	 * (e.g. feeding, using shears, etc.). Mirrors client-side interactWithEntity.
	 */
	public void usePlayerItemWithEntity(Entity var1) {
		// Try entity-specific interaction first
		if(var1 instanceof EntityLiving) {
			ItemStack current = this.getCurrentEquippedItem();
			if(current != null) {
				// delegate to ItemStack implementation (saddle/use on entity)
				current.useItemOnEntity((EntityLiving)var1);
				if(current.stackSize <= 0) {
					current.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
				return;
			}
		}
		// fallback to entity interaction
		if(!var1.interact(this)) {
			// nothing further
		}
	}

	protected float getEyeHeight() {
		return 1.62F;
	}
	
	@Override
	public void fall(float distance) {
	    super.fall(distance);
	}
}
