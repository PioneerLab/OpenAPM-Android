package com.hello2mao.openapm.rewriter.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {

    /**
     * NIO way
     *
     * @param filePath String
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] readFileToByteArray(String filePath) throws IOException {

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * NIO way
     *
     * @param filePath String
     * @param bytes byte[]
     * @throws IOException IOException
     */
    public static void writeByteArrayToFile(String filePath, byte[] bytes) throws IOException {

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }

        FileChannel channel = null;
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            while (channel.write(byteBuffer) != 0) {
                // do nothing
                // System.out.println("writing");
            }
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
