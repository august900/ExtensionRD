package net.minecraft.src;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class ThreadDownloadResources extends Thread {
	public File resourcesFolder;
	private Minecraft mc;
	private boolean closing = false;

	public ThreadDownloadResources(File var1, Minecraft var2) {
		this.mc = var2;
		this.setName("Resource download thread");
		this.setDaemon(true);
		this.resourcesFolder = new File(var1, "resources/");
		if(!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
		}
	}

	public void run() {
		try {
			ArrayList var1 = new ArrayList();
			URL var2 = new URL("http://www.minecraft.net/resources/");
			BufferedReader var3 = new BufferedReader(new InputStreamReader(var2.openStream()));
			String var4 = "";

			while(true) {
				var4 = var3.readLine();
				if(var4 == null) {
					var3.close();

					for(int var5 = 0; var5 < 2; ++var5) {
						for(int var6 = 0; var6 < var1.size(); ++var6) {
							this.downloadAndInstallResource(var2, (String)var1.get(var6), var5);
							if(this.closing) {
								return;
							}
						}
					}
					break;
				}

				var1.add(var4);
			}
		} catch (IOException var7) {
			this.loadResource(this.resourcesFolder, "");
			var7.printStackTrace();
		}

	}

	private void loadResource(File var1, String var2) {
		File[] var3 = var1.listFiles();

		for(int var4 = 0; var4 < var3.length; ++var4) {
			if(var3[var4].isDirectory()) {
				this.loadResource(var3[var4], var2 + var3[var4].getName() + "/");
			} else {
				this.mc.installResource(var2 + var3[var4].getName(), var3[var4]);
			}
		}

	}

	private void downloadAndInstallResource(URL var1, String var2, int var3) {
		try {
			String[] var4 = var2.split(",");
			String var5 = var4[0];
			int var6 = var5.indexOf("/");
			String var7 = var5.substring(0, var6);
			if(!var7.equals("sound") && !var7.equals("newsound")) {
				if(var3 != 1) {
					return;
				}
			} else if(var3 != 0) {
				return;
			}

			int var8 = Integer.parseInt(var4[1]);
			long var9 = Long.parseLong(var4[2]);
			var9 /= 2L;
			File var11 = new File(this.resourcesFolder, var5);
			if(!var11.exists() || var11.length() != (long)var8) {
				var11.getParentFile().mkdirs();
				String var12 = var5.replaceAll(" ", "%20");
				this.downloadResource(new URL(var1, var12), var11, var8);
				if(this.closing) {
					return;
				}
			}

			this.mc.installResource(var5, var11);
		} catch (Exception var13) {
			var13.printStackTrace();
		}

	}

	private void downloadResource(URL var1, File var2, int var3) throws IOException {
		byte[] var4 = new byte[4096];
		DataInputStream var5 = new DataInputStream(var1.openStream());
		DataOutputStream var6 = new DataOutputStream(new FileOutputStream(var2));
		boolean var7 = false;

		do {
			int var8 = var5.read(var4);
			if(var8 < 0) {
				var5.close();
				var6.close();
				return;
			}

			var6.write(var4, 0, var8);
		} while(!this.closing);

	}

	public void a() {
		this.closing = true;
	}
}
