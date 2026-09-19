package net.minecraft.src;

public class EntityShadowPlayer extends EntityLiving {

	private int blockedSightTicks = 0;
    public boolean hasAnnounced = false;

    private int stareTicks = 0;
    private static final int MAX_STARE_TICKS = 60;
    public EntityShadowPlayer(World world) {
        super(world);
        this.texture = "/char2.png";
        this.moveSpeed = 0.0F; 
        this.health = 20;
    }

    public EntityShadowPlayer(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, 128.0D);

        if (player != null) {
            this.faceEntity(player, 10.0F);

            double distanceSq = this.getDistanceSqToEntity(player);
            if (distanceSq < 400.0D) {
                this.vanish();
            }
            
            if (isPlayerStaringAtMe(player)) {
                stareTicks++;
                if (stareTicks >= MAX_STARE_TICKS) {
                    this.vanish();
                }
            } else {
                if (stareTicks > 0) {
                    stareTicks--;
                }
            }
            
            if (!hasLineOfSightTo(player)) {
                blockedSightTicks++;
                if (blockedSightTicks > 100) {
                    this.setEntityDead(); 
                    return;
                }
            } else {
                blockedSightTicks = 0;
            }
            
            
        }
    }
    public boolean hasLineOfSightTo(EntityPlayer player) {
        Vec3D start = Vec3D.createVector(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
        Vec3D end = Vec3D.createVector(this.posX, this.posY + 1.62D, this.posZ);
        return this.worldObj.rayTraceBlocks(start, end) == null;
    }
    private boolean isPlayerStaringAtMe(EntityPlayer player) {
        double dx = this.posX - player.posX;
        double dy = (this.posY + (double)this.getEyeHeight()) - (player.posY + (double)player.getEyeHeight());
        double dz = this.posZ - player.posZ;
        
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance == 0.0D) return false;

        dx /= distance;
        dy /= distance;
        dz /= distance;
        double pitchRad = Math.toRadians(-player.rotationPitch);
        double yawRad = Math.toRadians(-player.rotationYaw - 90.0F);

        double lookX = Math.cos(pitchRad) * Math.cos(yawRad);
        double lookY = Math.sin(pitchRad);
        double lookZ = Math.cos(pitchRad) * Math.sin(yawRad);

        double dotProduct = dx * lookX + dy * lookY + dz * lookZ;

        return dotProduct > 0.95D;
    }

    public void vanish() {
    	System.out.println(" has disconnected.");
        this.setEntityDead();
    }

    @Override
    public boolean attackEntityFrom(Entity attacker, int damage) {
        this.vanish();
        return true;
    }
}