
package tolt.server.service.util;

import java.util.Vector;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BufferBuilder {

    private Vector<Byte> buffer = new Vector<Byte>();
    private int index = 0;

    public int size () { return buffer.size(); }
    public void reset () { index = 0; }
    public void clear () { buffer.clear(); }

    private byte[] cache = new byte[0];
    public byte[] toArray () {

        if (cache.length != buffer.size()) {

            cache = new byte[buffer.size()];
            for (int i = 0; i < buffer.size(); ++i)
                cache[i] = buffer.get(i);
        }
        return cache;
    }
    public String toString () {

        String string = "[";
        for (int i = 0; i < buffer.size(); ++i)
            string += buffer.get(i) + ",";

        return string + "EOB]";
    }


    public BufferBuilder () {}
    public BufferBuilder (byte[] v) { for (byte b : v) buffer.add(b); }


    public void append (byte b) { buffer.add(b); }
    public void append (short v) { for (byte b : ByteBuffer.allocate(2).putShort(v).array()) buffer.add(b); }
    public void append (int v) { for (byte b : ByteBuffer.allocate(4).putInt(v).array()) buffer.add(b); }
    public void append (long v) { for (byte b : ByteBuffer.allocate(8).putLong(v).array()) buffer.add(b); }
    public void append (float v) { for (byte b : ByteBuffer.allocate(4).putFloat(v).array()) buffer.add(b); }
    public void append (double v) { for (byte b : ByteBuffer.allocate(8).putDouble(v).array()) buffer.add(b); }
    public void append (boolean v) { buffer.add((byte)(v ? 1 : 0)); }
    public void append (String v) {
        byte[] a = v.getBytes(StandardCharsets.UTF_8);
        for (byte b : ByteBuffer.allocate(4).putInt(a.length).array()) buffer.add(b);
        for (byte b : ByteBuffer.allocate(a.length).put(a).array()) buffer.add(b);
    }
    public void append (byte[] v) {
        for (byte b : ByteBuffer.allocate(4).putInt(v.length).array()) buffer.add(b);
        for (byte b : ByteBuffer.allocate(v.length).put(v).array()) buffer.add(b);
    }

    public byte getByte () { return buffer.get(++index); }
    public short getShort () {
        short v = ByteBuffer.wrap(toArray(), index, 2).getShort(); index += 2;
        return v;
    }
    public int getInt () {
        int v = ByteBuffer.wrap(toArray(), index, 4).getInt(); index += 4;
        return v;
    }
    public long getLong () {
        long v = ByteBuffer.wrap(toArray(), index, 8).getLong(); index += 8;
        return v;
    }
    public float getFloat () {
        float v = ByteBuffer.wrap(toArray(), index, 4).getFloat(); index += 4;
        return v;
    }
    public double getDouble () {
        double v = ByteBuffer.wrap(toArray(), index, 8).getDouble(); index += 8;
        return v;
    }
    public boolean getBoolean () {
        boolean v = buffer.get(++index) == (byte)1;
        return v;
    }
    public String getString () {
        int s = ByteBuffer.wrap(toArray(), index, 4).getInt(); index += 4;
        String v = new String(toArray(), index, s); index += s;
        return v;
    }
    public byte[] getByteArray () {
        int s = ByteBuffer.wrap(toArray(), index, 4).getInt(); index += 4;
        byte[] v = ByteBuffer.wrap(toArray(), index, s).array(); index += s;
        return v;
    }
}
