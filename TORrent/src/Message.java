import java.io.Serializable;

/**
 * Created by Justynaa on 2018-01-05.
 */
public class Message implements Serializable {

    private FileData[] filesList;
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

}
