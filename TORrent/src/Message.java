import java.io.Serializable;

/**
 * Created by Justynaa on 2018-01-05.
 */
public class Message implements Serializable {

    private String fileName;
    private FileData[] filesList;
    private byte[] data;
    private byte[] checkSum;
    private RequestType requestType;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public FileData[] getFilesList() {
        return filesList;
    }

    public void setFilesList(FileData[] filesList) {
        this.filesList = filesList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(byte[] checkSum) {
        this.checkSum = checkSum;
    }

}
