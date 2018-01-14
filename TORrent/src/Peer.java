import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Justynaa on 2018-01-06.
 */
public class Peer {

    private Socket clientSocket;
    private String name;
    private Thread peerListeningThread;
    private Host host;

    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;

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

        if (objectOutputStream == null) {
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
            readIncomingMessage();
        }
        while (!peerListeningThread.isInterrupted());

    }


    private void readIncomingMessage() throws Exception {
        Message incomingMessage;
        try {
            if (objectInputStream == null) {
                InputStream inputStream = clientSocket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
            }

            incomingMessage = (Message) objectInputStream.readObject();
        } catch (SocketException | EOFException se) {
            System.out.println("Peer " + getName() + " zamknął połaczenie");
            host.removePeer(this);
            peerListeningThread.interrupt();
            return;
        }

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
                        FileHelper.fileSave(file.getData(), host.workingDirectory + "\\" + file.getFileName());
                    } else {
                        System.out.println("błąd zapisywania: niepoprawna suma kontrolna pliku: " + file.getFileName());
                    }
                }
                break;
            case FILES_REQUEST:
                System.out.println("Wysłyam pliki: ");
                ArrayList<String> filesNamesToSend = new ArrayList<>();
                for (FileData fileData : incomingMessage.getFilesList()) {
                    filesNamesToSend.add(fileData.getFileName());
                }
                sendFilesToPeer(filesNamesToSend);
                break;
        }
    }

    private void sendFilesToPeer(ArrayList<String> filesNamesToSend) throws Exception {
        FileData[] filesToSend = FileHelper.readFiles(host.workingDirectory, filesNamesToSend);
        for (FileData file : filesToSend) {
            System.out.println("plik: " + file);
        }
        Message responseWithFiles = new Message();
        responseWithFiles.setRequestType(RequestType.FILES_SAVE);
        responseWithFiles.setFilesList(filesToSend);

        sendMessage(responseWithFiles);
    }

    private Message createMyFilesListMessage() throws Exception {
        FileData[] filesList = FileHelper.generateFilesList(host.workingDirectory);
        Message resultList = new Message();
        resultList.setFilesList(filesList);
        resultList.setRequestType(RequestType.FILES_LIST_RESPONSE);
        return resultList;
    }

    public void sendFiles(ArrayList<String> filesList) throws Exception {
        sendFilesToPeer(filesList);
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
