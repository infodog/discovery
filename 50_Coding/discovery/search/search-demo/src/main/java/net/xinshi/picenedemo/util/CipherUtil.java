package net.xinshi.picenedemo.util;


import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/17/13
 * Time: 4:10 PM
 */
public class CipherUtil {

    /**
     * Calculate digest of given String using given algorithm.
     * Encode digest in MIME-like base64.
     *
     * @param pass the String to be hashed
     * @param algorithm the algorithm to be used
     * @return String Base-64 encoding of digest
     *
     * @throws java.security.NoSuchAlgorithmException if the algorithm passed in cannot be found
     */
    public static String digestString(String pass, String algorithm) throws
            NoSuchAlgorithmException {

        MessageDigest md;
        ByteArrayOutputStream bos;

        try {
            md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(pass.getBytes("iso-8859-1"));
            bos = new ByteArrayOutputStream();
            OutputStream encodedStream = MimeUtility.encode(bos, "base64");
            encodedStream.write(digest);
            return bos.toString("iso-8859-1");
        } catch (IOException ioe) {
            throw new RuntimeException("Fatal error: " + ioe);
        } catch (MessagingException me) {
            throw new RuntimeException("Fatal error: " + me);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(digestString("secret", "MD5"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
