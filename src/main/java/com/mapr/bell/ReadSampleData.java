package com.mapr.bell;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.*;

/**
 * Reads the sample data for the bell ringing and writes the data
 * as CSV files that can be read by R (or whatever).
 */
public class ReadSampleData {
    public static void main(String[] args) throws IOException {
        FloatBuffer hard1 = read32bFloat("hard-1.aiff");
        dumpData(hard1, "hard-1.csv");
        FloatBuffer soft1 = read32bFloat("soft-1.aiff");
        dumpData(soft1, "soft-1.csv");
        FloatBuffer soft2 = read32bFloat("soft-2.aiff");
        dumpData(soft2, "soft-2.csv");
    }

    private static void dumpData(FloatBuffer data, String outFile) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.printf("t,v\n");
            for (int i = 0; i < data.limit(); i++) {
                float mono = data.get(i);
                out.printf("%.5f,%.7f\n", i / 44100.0, mono);
            }
        }
    }

    private static FloatBuffer read32bFloat(String resourceName) throws IOException {
        ByteBuffer b1 = readBytes(resourceName);
        b1.order(ByteOrder.LITTLE_ENDIAN);
        return b1.asFloatBuffer();
    }

    private static ByteBuffer readBytes(String resourceName) throws IOException {
        ByteSource s1 = Resources.asByteSource(Resources.getResource(resourceName));
        ByteBuffer b1 = ByteBuffer.allocate((int) s1.size());
        int n = s1.openBufferedStream().read(b1.array());
        if (n != b1.array().length) {
            throw new IOException("Didn't read expected data");
        }
        return b1;
    }
}
