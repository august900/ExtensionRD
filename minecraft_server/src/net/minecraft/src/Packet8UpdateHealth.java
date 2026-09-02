package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet {
    public int healthMP;

    public Packet8UpdateHealth() {
    }

    public Packet8UpdateHealth(int var1) {
        this.healthMP = var1;
    }

    @Override
    public void readPacketData(DataInputStream var1) throws IOException {
        this.healthMP = var1.readShort();
    }

    @Override
    public void writePacket(DataOutputStream var1) throws IOException {
        var1.writeShort(this.healthMP);
    }

    @Override
    public void processPacket(NetHandler var1) {
        var1.handleUpdateHealth(this);
    }

    @Override
    public int getPacketSize() {
        return 2;
    }
}