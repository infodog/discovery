package org.apache.solr.handler.dataimport.pigeon;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;

public class CommonTools {
    public static final int TailMagicNumber = 0x03ABCDEF;
    static final byte compressFlag = 0x01;
    static final byte stringFlag = 0x02;
    static final byte addFlag = 1 << 3;
    static Logger logger = Logger.getLogger("CommonTools");

    public static String getComparableString(long key, int number) {
        return StringUtils.leftPad(String.valueOf(key), number, '0');
    }

    static long pow(long key, int power) {
        long powers[] = new long[]{1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L};
        return key * powers[power];
    }

    public static int bytes2int(byte[] bytes) {
        long l;
        l = bytes[3] & 0xFF;
        l = l << 8;
        l |= bytes[2] & 0xFF;
        l <<= 8;
        l |= bytes[1] & 0xFF;
        l <<= 8;
        l |= bytes[0] & 0xFF;
        return (int) l;
    }

    public static int bytes2intLoger(byte[] writeBuffer) {
        int v = 0;
        v |= (writeBuffer[0] & 0xFF) << 24;
        v |= (writeBuffer[1] & 0xFF) << 16;
        v |= (writeBuffer[2] & 0xFF) << 8;
        v |= (writeBuffer[3] & 0xFF);
        return v;
    }

    private static long bytes2long(byte[] writeBuffer) {
        long v = 0;
        v |= (long) (writeBuffer[0] & 0xFF) << 56;
        v |= (long) (writeBuffer[1] & 0xFF) << 48;
        v |= (long) (writeBuffer[2] & 0xFF) << 40;
        v |= (long) (writeBuffer[3] & 0xFF) << 32;
        v |= (long) (writeBuffer[4] & 0xFF) << 24;
        v |= (long) (writeBuffer[5] & 0xFF) << 16;
        v |= (long) (writeBuffer[6] & 0xFF) << 8;
        v |= (long) (writeBuffer[7] & 0xFF);
        return v;
    }

    public static void writeLong(OutputStream out, long v) throws IOException {
        byte[] writeBuffer = new byte[8];
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 8);

    }

    public static void readFully(InputStream in, byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    public static long readLong(InputStream in) throws IOException {
        byte[] readBuffer = new byte[8];
        readFully(in, readBuffer, 0, 8);
        return (((long) readBuffer[0] << 56) +
                ((long) (readBuffer[1] & 255) << 48) +
                ((long) (readBuffer[2] & 255) << 40) +
                ((long) (readBuffer[3] & 255) << 32) +
                ((long) (readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) << 8) +
                ((readBuffer[7] & 255) << 0));
    }

    public static void writeBytes(OutputStream os, byte[] buf, int off, int len) throws IOException {
        byte[] lenbuf = new byte[4];
        lenbuf[0] = (byte) (len & 0x000000ff);
        lenbuf[1] = (byte) ((len >> 8) & 0x000000ff);
        lenbuf[2] = (byte) ((len >> 16) & 0x000000ff);
        lenbuf[3] = (byte) ((len >> 24) & 0x000000ff);
        os.write(lenbuf);
        os.write(buf, off, len);
    }

    public static void writeString(OutputStream os, String s) throws IOException {
        byte[] buf = s.getBytes("UTF-8");
        int len = buf.length;
        byte[] lenbuf = new byte[4];
        lenbuf[0] = (byte) (len & 0x000000ff);
        lenbuf[1] = (byte) ((len >> 8) & 0x000000ff);
        lenbuf[2] = (byte) ((len >> 16) & 0x000000ff);
        lenbuf[3] = (byte) ((len >> 24) & 0x000000ff);
        os.write(lenbuf);
        os.write(buf);
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] lenbuf = new byte[4];
        //int n = is.read(lenbuf);
        try {
            readFully(is, lenbuf, 0, 4);
        } catch (Exception e) {
            return null;
        }
        int len = bytes2int(lenbuf);
        byte[] sBuf = new byte[len];        /*
		int l = is.read(sBuf);
		if (l != len) {
			throw new Exception("File Corrupted.");
		} */
        try {
            readFully(is, sBuf, 0, len);
        } catch (Exception e) {
            return null;
        }

        return sBuf;
    }

    public static FlexObjectEntry readEntry(InputStream in) throws Exception {

        String name = CommonTools.readString(in);
        if (name == null) {
            return null;
        }
        if (StringUtils.isBlank(name)) {
            return FlexObjectEntry.empty;
        }
        FlexObjectEntry entry = new FlexObjectEntry();
        entry.setName(name);
        byte[] flagArr = CommonTools.readBytes(in);
        if (flagArr == null) {
            return null;
        }
        byte flag = flagArr[0];
        if ((flag & addFlag) > 0) {
            entry.setAdd(true);
        } else {
            entry.setAdd(false);
        }
        if ((flag & stringFlag) > 0) {
            entry.setString(true);
        } else {
            entry.setString(false);
        }
        if ((flag & compressFlag) > 0) {
            entry.setCompressed(true);
        } else {
            entry.setCompressed(false);
        }
        long hash = CommonTools.readLong(in);
        entry.setHash(hash);
        try {
            byte[] bytes = CommonTools.readBytes(in);
            entry.setBytesContent(bytes);
        } catch (Exception e) {
            return null;
        }

        return entry;
    }

    public static String readString(InputStream is) throws Exception {
        byte[] lenbuf = new byte[4];
        //int n = is.read(lenbuf);
        try {
            readFully(is, lenbuf, 0, 4);
        } catch (Exception e) {
            return null;
        }
        int len = bytes2int(lenbuf);
        byte[] sBuf = new byte[len];
		/*
		int l = is.read(sBuf);
		if (l != len) {
			throw new Exception("File Corrupted.");
		} */
        readFully(is, sBuf, 0, len);
        return new String(sBuf, "UTF-8");
    }

    public static byte[] unzip(byte[] bytes) throws IOException, DataFormatException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream zis = new GZIPInputStream(bis);
        byte[] buf = new byte[2048];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int n = zis.read(buf);
        while (n > 0) {
            bos.write(buf, 0, n);
            n = zis.read(buf);
        }
        zis.close();
        return bos.toByteArray();
    }

    public static VersionHistory getVersionHistoryFromFIS(InputStream fis) {
        VersionHistory vh = null;
        try {
            byte[] bytes4 = new byte[4];
            byte[] bytes8 = new byte[8];
            int count = fis.read(bytes4);
            if (count < 1) {
                return null;
            }
            if (count != 4) {
                throw new Exception("getVersionHistoryFromFIS (count != 4) ...... ");
            }
            int len1 = bytes2intLoger(bytes4);
            if (len1 < 0) {
                throw new Exception("getVersionHistoryFromFIS (len1 < 0) ...... ");
            }
            byte[] data = new byte[len1];
            count = fis.read(data);
            if (count != len1) {
                throw new Exception("getVersionHistoryFromFIS (count != len1) ...... ");
            }
            count = fis.read(bytes4);
            if (count != 4) {
                throw new Exception("getVersionHistoryFromFIS (count != 4) ...... ");
            }
            int len2 = bytes2intLoger(bytes4);
            if (len1 != len2) {
                throw new Exception("getVersionHistoryFromFIS (len1 != len2) ...... ");
            }
            count = fis.read(bytes8);
            if (count != 8) {
                throw new Exception("getVersionHistoryFromFIS (count != 8) ...... ");
            }
            long version = bytes2long(bytes8);
            count = fis.read(bytes4);
            if (count != 4) {
                throw new Exception("getVersionHistoryFromFIS (count != 4) ...... ");
            }
            int magic = bytes2intLoger(bytes4);
            if (magic != TailMagicNumber) {
                throw new Exception("getVersionHistoryFromFIS (magic != TailMagicNumber) ...... ");
            }
            vh = new VersionHistory(len1, data, len2, version, magic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public static InputStream getInputStreamFromVersionHistoryFile(InputStream fis) throws Exception {
        synchronized (fis) {
            InputStream mis = null;
            VersionHistory vh = getVersionHistoryFromFIS(fis);
            if (vh != null) {
                mis = new ByteArrayInputStream(vh.getData());
            }
            return mis;
        }
    }
}