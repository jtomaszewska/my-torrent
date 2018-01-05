import java.io.Serializable;

/**
 * Created by Justynaa on 2017-11-24.
 */
public class Request implements Serializable {
    private RequestType requestType;
    private String fileName;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
