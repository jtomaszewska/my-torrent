import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Justynaa on 2017-12-29.
 */
public class FileData implements Serializable{

    private String fileName;
    private byte[] checkSum;
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }

    public String getCheckSumString(){
        return checkSum.toString();
    }

    public void setCheckSum(byte[] checkSum) {
        this.checkSum = checkSum;
    }
}
