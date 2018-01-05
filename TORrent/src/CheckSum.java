import java.security.MessageDigest;

/**
 * Created by Justynaa on 2017-12-28.
 */
public class CheckSum {

    public static byte[] generateCheckSum(byte[] data) throws Exception {
        return MessageDigest.getInstance("MD5").digest(data);
    }


    public static boolean chceckCheckSum(byte[] data, byte[] originalChecksum) throws Exception {

        byte[] newCheckSum = generateCheckSum(data);
        for (int i = 0; i < newCheckSum.length; i++) {
            if (originalChecksum[i] != newCheckSum[i])
                return false;
        }
        return true;
    }


}
