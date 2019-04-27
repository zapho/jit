package fr.aupert.jit.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static fr.aupert.jit.util.IO.inputStreamToBytes;

/**
 * Git saves disk space by compressing every object it stores using the DEFLATE
 * (https://tools.ietf.org/html/rfc1951) compression algorithm, which is
 * implemented by zlib (https://zlib.net/).
 * <p/>
 * This utility class provides inflate/uncompress deflate/compress methods to
 * achieve the same result as zlib using {@link Deflater} and {@link Inflater}.
 */
public class Deflate {

    public String decompress(InputStream is) {
        byte[] compressedBytes = inputStreamToBytes(is);
        return decompressToString(compressedBytes);
    }

    public byte[] compress(byte[] bytesToCompress) {
        Deflater deflater = new Deflater();
        deflater.setInput(bytesToCompress);
        deflater.finish();

        byte[] bytesCompressed = new byte[Short.MAX_VALUE];

        int numberOfBytesAfterCompression = deflater.deflate(bytesCompressed);

        byte[] returnValues = new byte[numberOfBytesAfterCompression];

        System.arraycopy
                (
                        bytesCompressed,
                        0,
                        returnValues,
                        0,
                        numberOfBytesAfterCompression
                );

        return returnValues;
    }

    public byte[] compress(String stringToCompress) {
        byte[] returnValues = null;

        try {

            returnValues = this.compress
                    (
                            stringToCompress.getBytes("UTF-8")
                    );
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }

        return returnValues;
    }

    public byte[] decompress(byte[] bytesToDecompress) {
        byte[] returnValues = null;
        Inflater inflater = new Inflater();
        int numberOfBytesToDecompress = bytesToDecompress.length;
        inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress);
        int bufferSizeInBytes = numberOfBytesToDecompress;
        int numberOfBytesDecompressedSoFar = 0;
        List<Byte> bytesDecompressedSoFar = new ArrayList<Byte>();

        try {
            while (inflater.needsInput() == false) {
                byte[] bytesDecompressedBuffer = new byte[bufferSizeInBytes];
                int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);
                numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime;
                for (int b = 0; b < numberOfBytesDecompressedThisTime; b++) {
                    bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
                }
            }
            returnValues = new byte[bytesDecompressedSoFar.size()];
            for (int b = 0; b < returnValues.length; b++) {
                returnValues[b] = (byte) (bytesDecompressedSoFar.get(b));
            }

        } catch (DataFormatException dfe) {
            dfe.printStackTrace();
        }
        inflater.end();
        return returnValues;
    }

    public String decompressToString(byte[] bytesToDecompress) {
        byte[] bytesDecompressed = this.decompress(bytesToDecompress);
        String returnValue = null;
        try {
            returnValue = new String(bytesDecompressed, 0, bytesDecompressed.length, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        return returnValue;
    }
}

