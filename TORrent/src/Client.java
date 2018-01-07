//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//import java.util.Scanner;
//
///**
// * Created by Justynaa on 2017-12-27.
// */
//public class Client {
//
//    public static void createMessage(Socket clientSocket, RequestType requestType, String[] filesNames) throws Exception {
//
//
//        OutputStream outStr = clientSocket.getOutputStream();
//        ObjectOutputStream obj_out = new ObjectOutputStream(outStr);
//
//        Message request = new Message();
//        request.setRequestType(requestType);
//
//        request.setFilesNames(filesNames);
//        obj_out.writeObject(request);
//
//
//
//
//    }
//
//
//
//    public static void getResponse(Socket clientSocket, String workingDirectory) throws Exception {
//
//        InputStream inStr = clientSocket.getInputStream();
//        ObjectInputStream obj_in = new ObjectInputStream((inStr));
//        Object receivedObj = obj_in.readObject();
//        Message res = (Message) receivedObj;
//
//        switch (res.getRequestType()) {
//
//            case FILES_LIST:
//                for (int i = 0; i < res.getFilesList().length; i++) {
//                    System.out.println("nazwa: "+res.getFilesList()[i].getFileName()+" suma kontrolna: "+res.getFilesList()[i].getCheckSum());
//                }
//                break;
//            case FILES_SAVE:
//                String path = workingDirectory + "\\" + res.getFilesNames()[0];
//                for (int j = 0; j < res.getFilesNames().length; j++) {
//                    if (CheckSum.chceckCheckSum(res.getData(), res.getCheckSum()))
//                        FileHelper.fileSave(res.getData(), path);
//                }
//                break;
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        String workingDirectory = "C:\\Users\\Justynaa\\IdeaProjects\\TORrent\\Client";
//        final String serverName = "localhost";
//        // = localhost - zamiast podawać IP mówimy: szukaj otwartego portu na tej maszynie na ktorej jesteś
//        // = 127.0.0.1 - to samo co localhost, ale podane jako adres IP
//        // "192.168.0.94"; - IP przypisane w ramach sieci do której aktualnie jest sie podłacząnym
//
//        final int serverPort = 12000;
//        Socket clientSocket = new Socket(serverName, serverPort);
//
//        System.out.println("klient wysyła zapytanie o listę plikow");
//        String[] emptyFileName = {""};
//        createMessage(clientSocket, RequestType.FILES_LIST, emptyFileName);
//
//        System.out.println("klient odbiera listę plikow");
//        getResponse(clientSocket, workingDirectory);
//
//        System.out.println("wybierz plik wpisujac jego nazwe");
//        Scanner read = new Scanner(System.in);
//        String[] filesToDownload = new String[1];
//        filesToDownload[0] = read.nextLine();
//        System.out.println("pobranao nazwę pliku");
//
//        System.out.println("klient wysyła zapytanie o plik");
//        createMessage(clientSocket, RequestType.FILES_SAVE, filesToDownload);
//
//        System.out.println("klient odbiera plik");
//        getResponse(clientSocket, workingDirectory);
//
//        System.out.println("klient czyta swoja liste plikow");
//        Message myList = new Message();
//        myList.setRequestType(RequestType.FILES_LIST);
//        myList.setFilesList(FileHelper.generateFilesList(workingDirectory));
//        for (int i = 0; i < myList.getFilesList().length; i++) {
//            System.out.println("nazwa: "+myList.getFilesList()[i].getFileName()+" suma kontrolna: "+myList.getFilesList()[i].getCheckSum());
//        }
//        System.out.println("wybierz plik do wysłania wpisujac jego nazwe");
//        Scanner read2 = new Scanner(System.in);
//        String[] fileToSend = {read2.nextLine()};
//        System.out.println("pobranao nazwę pliku");
//
//        System.out.println("klient wysyła plik");
//        createMessage(clientSocket, RequestType.FILES_REQUEST, fileToSend);
//
//
//        clientSocket.close();
//
//    }
//}
