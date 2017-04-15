// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class Streams
{
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8192, false);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final boolean closeStreams) throws IOException {
        return copy(input, output, 8192, closeStreams);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final int bufferSize) throws IOException {
        return copy(input, output, bufferSize, false);
    }
    
    public static int copy(final InputStream input, final OutputStream output, final int bufferSize, final boolean closeStreams) throws IOException {
        try {
            final byte[] buffer = new byte[bufferSize];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }
        finally {
            if (closeStreams) {
                input.close();
                output.close();
            }
        }
    }
    
    public static byte[] slurpBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copy(in, out);
            out.flush();
            return out.toByteArray();
        }
        finally {
            out.close();
        }
    }
    
    public static String slurp(final InputStream in, final String encoding) throws IOException {
        final byte[] bytes = slurpBytes(in);
        return new String(bytes, encoding);
    }
    
    public static void copyBytesToFile(final File file, final byte[] newBytes) throws IOException {
        final OutputStream oStream = new FileOutputStream(file);
        try {
            copy(new ByteArrayInputStream(newBytes), oStream, true);
        }
        finally {
            oStream.close();
        }
    }
}
