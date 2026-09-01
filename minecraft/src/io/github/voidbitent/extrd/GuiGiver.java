package io.github.voidbitent.extrd;

import net.minecraft.src.*;

public class GuiGiver extends GuiScreen {
	private int updateCounter = 0;
	private String id;
	private boolean failed;
	
	public GuiGiver() {
		this.id = "";
	}
	public void updateScreen() {
		this.updateCounter++;
	}
	
	public void initGui() {
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Give"));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
		((GuiButton)this.controlList.get(0)).enabled = false;
		
	}
	
	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id == 1) {
				this.mc.displayGuiScreen((GuiScreen)null);
			} else if(var1.id == 0) {
				int iid = Integer.parseInt(id);
				if(!isValidID(iid)) {
					failed = true;
				} else {
					this.give(iid);
				}
			}

		}
	}
	
	protected void keyTyped(char var1, int var2) {
		if(var1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		if(var2 == 14 && this.id.length() > 0) {
			this.id = this.id.substring(0, this.id.length() - 1);
		}

		if("1234567890".indexOf(var1) >= 0 && this.id.length() < 32) {
			this.id = this.id + var1;
		}
		
		if (var2 == 1) { 
			this.mc.displayGuiScreen((GuiScreen)null);
		}

		((GuiButton)this.controlList.get(0)).enabled = this.id.length() > 0;
	}
	
	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		this.drawString(this.fontRenderer, "Input ID", this.width / 2 - 30, this.height / 4 - 60 + 60 + 36, 14737632);
		int var4 = this.width / 2 - 100;
		int var5 = this.height / 4 - 10 + 50 + 18;
		short var6 = 200;
		byte var7 = 20;
		this.drawRect(var4 - 1, var5 - 1, var4 + var6 + 1, var5 + var7 + 1, -6250336);
		this.drawRect(var4, var5, var4 + var6, var5 + var7, -16777216);
		this.drawString(this.fontRenderer, this.id + (this.updateCounter / 6 % 2 == 0 ? "_" : ""), var4 + 4, var5 + (var7 - 8) / 2, 14737632);
		super.drawScreen(var1, var2, var3);
		if(failed) {
			this.drawCenteredString(this.fontRenderer, "Invalid ID!", this.width / 2, var5 + var7 + 10, 0xFF5555);
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
	
	private boolean isValidID(int id) {
	    if (id >= 0 && id < Block.blocksList.length) {
	        if (Block.blocksList[id] != null) {
	            return true;
	        }
	    }
	    
	    if (id >= 0 && id < Item.itemsList.length) {
	        if (Item.itemsList[id] != null) {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	private void give(int var1) {
		try {
            int parsedID = var1;
            
            if (this.isValidID(parsedID)) {
                ItemStack stackToGive = null;
                
                if (parsedID < Block.blocksList.length && Block.blocksList[parsedID] != null) {
                    stackToGive = new ItemStack(Block.blocksList[parsedID], 64);
                } 
                else if (parsedID < Item.itemsList.length && Item.itemsList[parsedID] != null) {
                    stackToGive = new ItemStack(Item.itemsList[parsedID], 1);
                }
                
                if (stackToGive != null) {
                    this.mc.thePlayer.inventory.addItemStackToInventory(stackToGive);
                }
            }
        } catch (NumberFormatException e) {
        }
        
        this.mc.displayGuiScreen((GuiScreen)null);
    }
}
