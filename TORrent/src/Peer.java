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

    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) throws Exception {

        this.clientSocket = clientSocket;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void startListening() {
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

        if (objectOutputStream == null){
            OutputStream outputStream = this.getClientSocket().getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
        }

        objectOutputStream.writeObject(message);
        System.out.println("Wysłano wiadomosc " + message.getRequestType());
    }

    public void requestForFiles(ArrayList<String> filesList) throws Exception {
        Message request = new Message();
        request.setRequestType(RequestType.FILES_REQUEST);
        FileData[] filesDataList = FileHelper.createFileDataList(filesList);
        request.setFilesList(filesDataList);
        sendMessage(request);
    }

    private void processIncomingMessages() throws Exception {

        do {
            //try{
                readIncomingMessage();
            //}catch (Exception ex){
            //   System.out.println("Wyjatek w trakcie przetwarzania przychodzacej wiadomosci: " + ex);
            //}
        }
        while (true);

    }

    private void readIncomingMessage() throws Exception {

        if (objectInputStream == null){
            InputStream inputStream = clientSocket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        }

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
                    System.out.println("nazwa: " + file.getFileName() + " suma kontrolna: " + file.getCheckSumString());
                }
                break;
            case FILES_SAVE:
                System.out.println("Zapisuję pliki: ");
                for (FileData file : incomingMessage.getFilesList()) {
                    if (CheckSum.chceckCheckSum(file.getData(), file.getCheckSum())) {
                        System.out.println("zapisuje plik nazwa: " + file.getFileName() + " suma kontrolna: " + file.getCheckSumString());
                        FileHelper.fileSave(file.getData(), this.getWorkingDirectory() + "\\" + file.getFileName());
                    }
                    else{
                        System.out.println("błąd zapisywania: niepoprawna suma kontrolna pliku: "+ file.getFileName());
                    }
                }
                break;
            case FILES_REQUEST:
                System.out.println("Wysyam pliki: ");
                ArrayList<String> filesNamesToSend = new ArrayList<>();
                for (FileData fileData : incomingMessage.getFilesList()) {
                    filesNamesToSend.add(fileData.getFileName());
                }
                sendFilesToPeer(filesNamesToSend);
                break;
        }
    }

    private void sendFilesToPeer(ArrayList<String> filesNamesToSend) throws Exception {
        FileData[] filesToSend = FileHelper.readFiles(workingDirectory, filesNamesToSend);
        System.out.println("Wysylam pliki");
        for (FileData file : filesToSend) {
            System.out.println("plik: " + file);
        }
        Message responseWithFiles = new Message();
        responseWithFiles.setRequestType(RequestType.FILES_SAVE);
        responseWithFiles.setFilesList(filesToSend);

        sendMessage(responseWithFiles);
    }

    private Message createMyFilesListMessage() throws Exception {
        FileData[] filesList = FileHelper.generateFilesList(workingDirectory);
        Message resultList = new Message();
        resultList.setFilesList(filesList);
        resultList.setRequestType(RequestType.FILES_LIST_RESPONSE);
        return resultList;
    }

    public void sendFiles(ArrayList<String> filesList) throws Exception {
        sendFilesToPeer(filesList);
    }
}
