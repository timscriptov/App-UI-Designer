package com.mcal.uidesigner.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NonOverwritingFileOutputStream extends OutputStream {
    private byte[] buffer = new byte[1000];
    private final String path;
    private int pos;

    public NonOverwritingFileOutputStream(String path) {
        this.path = path;
    }

    @Override
    public void write(int oneByte) throws IOException {
        if (this.pos >= this.buffer.length) {
            grow(this.pos + 1);
        }
        byte[] bArr = this.buffer;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) oneByte;
    }

    @Override
    public void write(byte[] buf, int offset, int count) throws IOException {
        if (this.pos + count >= this.buffer.length) {
            grow(this.pos + count);
        }
        for (int i = 0; i < count; i++) {
            this.buffer[this.pos + i] = buf[offset + i];
        }
        this.pos += count;
    }

    private void grow(int len) {
        byte[] newBuffer = new byte[Math.max(this.buffer.length * 2, len)];
        System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
        this.buffer = newBuffer;
    }

    @Override
    public void close() throws IOException {
        new File(this.path).getParentFile().mkdirs();
        if (new File(this.path).exists() && new File(this.path).length() == ((long) this.pos)) {
            boolean differ = false;
            FileInputStream in = new FileInputStream(this.path);
            int i = 0;
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                int i2 = i + 1;
                if (b != this.buffer[i]) {
                    differ = true;
                    break;
                }
                i = i2;
            }
            in.close();
            if (!differ) {
                return;
            }
        }
        FileOutputStream out = new FileOutputStream(this.path);
        out.write(this.buffer, 0, this.pos);
        out.close();
    }
}
