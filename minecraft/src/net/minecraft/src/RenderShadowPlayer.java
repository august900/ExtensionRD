package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderShadowPlayer extends RenderLiving {

    public static boolean DEBUG_MODE = false;

    public RenderShadowPlayer() {
        super(new ModelBiped(0.0F), 0.5F);
    }
    
    @Override
    protected void loadDownloadableImageTexture(String skinUrl, String fallbackTexture) {
        if (fallbackTexture != null) {
            this.loadTexture(fallbackTexture);
            return ;
        }
        this.loadTexture("/char2.png");
        return ;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pitch) {
        
        if (entity instanceof EntityShadowPlayer) {
            EntityShadowPlayer shadow = (EntityShadowPlayer) entity;
            if (!shadow.hasAnnounced) {
                shadow.hasAnnounced = true;
            }
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        super.doRender(entity, x, y, z, yaw, pitch);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        if (DEBUG_MODE) {
            renderDebugBeacon(x, y, z);
        }
    }

    @Override
    protected void preRenderCallback(EntityLiving entity, float partialTicks) {
    }

    private void renderDebugBeacon(double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 0, 0, 200);

        double w = 0.3D;
        double bottom = y;
        double top = y + 128.0D;

        tessellator.addVertex(x - w, top, z - w);
        tessellator.addVertex(x + w, top, z - w);
        tessellator.addVertex(x + w, bottom, z - w);
        tessellator.addVertex(x - w, bottom, z - w);

        tessellator.addVertex(x - w, bottom, z + w);
        tessellator.addVertex(x + w, bottom, z + w);
        tessellator.addVertex(x + w, top, z + w);
        tessellator.addVertex(x - w, top, z + w);

        tessellator.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }
}