import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justynaa on 2018-01-06.
 */
public class Peer {

    private Socket clientSocket;
    private String name;
    private String workingDirectory;
    private Thread peerListeningThread;

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void startListening(){
        peerListeningThread = new Thread(() -> {
            try {
                processIncomingMessages();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        peerListeningThread.start();
    }

    public void requestFilesList() throws Exception {
        Message request = new Message();
        request.setRequestType(RequestType.FILES_LIST_REQUEST);
        sendMessage(request);
    }

    private void sendMessage(Message message) throws Exception {
        OutputStream outStr = this.getClientSocket().getOutputStream();
        ObjectOutputStream obj_out = new ObjectOutputStream(outStr);
        obj_out.writeObject(message);
        System.out.println("Wysłano wiadomosc " + message.getRequestType());
    }

    public void requestForFiles(ArrayList<String> filesList) throws Exception {
        Message request = new Message();
        request.setRequestType(RequestType.FILE_PUSH);
        FileData[] filesDataList = FileHelper.createFileDataList(filesList);
        request.setFilesList(filesDataList);
        sendMessage(request);
    }

    public void processIncomingMessages() throws Exception {

        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        do {
            Message incomingMessage = (Message) objectInputStream.readObject();
            System.out.println("Peer " + getName() + " przesyla wiadomosc: " + incomingMessage.getRequestType());

            switch (incomingMessage.getRequestType()) {
                case FILES_LIST_REQUEST:
                    Message response = createMyFilesListMessage();
                    sendMessage(response);
                    break;
                case FILES_LIST_RESPONSE:
                    System.out.println("Lista plików: ");
                    for (FileData file : incomingMessage.getFilesList()) {
                        System.out.println("nazwa: " + file.getFileName() + " suma kontrolna: " + file.getCheckSum());
                    }
                    break;
                case FILE_PULL:
                    break;
                case FILE_PUSH:
                    break;
            }
        }
        while (true);

    }

    private Message createMyFilesListMessage() throws Exception {
        FileData[] filesList = FileHelper.generateFilesList(workingDirectory);
        Message resultList = new Message();
        resultList.setFilesList(filesList);
        resultList.setRequestType(RequestType.FILES_LIST_RESPONSE);
        return resultList;
    }

    private Message createFilesMessage(RequestType type, List<String> requestedFiles) throws Exception {
        FileData[] filesList = FileHelper.readFiles(workingDirectory, requestedFiles);
        Message resultList = new Message();
        resultList.setFilesList(filesList);
        resultList.setRequestType(type);
        return resultList;
    }
}
