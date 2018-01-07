//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//
///**
// * Created by Justynaa on 2017-12-27.
// */
//
//
//public class Server {
//
//    public static Message listen(Socket clientSocket) throws Exception {
//        InputStream inputStream = clientSocket.getInputStream();
//        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//        Object receivedFile = objectInputStream.readObject();
//        Message request = (Message) receivedFile;
//        System.out.println(request.getRequestType());
//        return request;
//    }
//
//    public static void reactToMessage(Socket clientSocket, String workingDirectory, Message r) throws Exception {
//        OutputStream outStr = clientSocket.getOutputStream();
//        ObjectOutputStream obj_out = new ObjectOutputStream(outStr);
//
//        switch (r.getRequestType()) {
//            case FILES_LIST:
//                System.out.println("SER: wysyłanie listy plików");
//                Message resultList = createFilesListMessage(workingDirectory);
//                obj_out.writeObject(resultList);
//                break;
//            case FILES_SAVE:
//                System.out.println("SER: wysyłanie plików");
//
//                String[] fileName = r.getFilesNames();
//                byte[] file = FileHelper.fileRead(workingDirectory + "\\" + fileName[0]);
//                Message resultFile = new Message();
//                resultFile.setFilesNames(fileName);
//                resultFile.setData(file);
//                resultFile.setRequestType(RequestType.FILES_SAVE);
//                byte[] checkSum = CheckSum.generateCheckSum(file);
//                resultFile.setCheckSum(checkSum);
//                System.out.println("suma kontrolna " + checkSum);
//                obj_out.writeObject(resultFile);
//                break;
//            case FILES_REQUEST:
//                System.out.println("SER: zapisywanie pliku");
//                //Message saveFile = new Message();
////                saveFile.setFilesNames(r.getFilesNames());
////                String path = workingDirectory + "\\" + saveFile.getFilesNames()[0];
////                if (CheckSum.chceckCheckSum(saveFile.getData(), saveFile.getCheckSum()))
////                    FileHelper.fileSave(saveFile.getData(), path);
//                String path = workingDirectory + "\\" + r.getFilesNames()[0];
//                //for (int j = 0; j < r.getFilesNames().length; j++) {
//                byte[] dataOfFile = r.getData();
//                byte[] orginalCheckSum = r.getCheckSum();
//                // r.getData(), r.getCheckSum()- do metody checkCheckSum
//                    if (CheckSum.chceckCheckSum(r.getData(), r.getCheckSum()))
//                        FileHelper.fileSave(r.getData(), path);
//                //}
//                break;
//
//        }
//    }
//
//    private static Message createFilesListMessage(String workingDirectory) throws Exception {
//        FileData[] filesList = FileHelper.generateFilesList(workingDirectory);
//        Message resultList = new Message();
//        resultList.setFilesList(filesList);
//        resultList.setRequestType(RequestType.FILES_LIST);
//        return resultList;
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        String workingDirectory = "C:\\Users\\Justynaa\\IdeaProjects\\TORrent\\Server";
//        final int serverPort = 12000;
//
//        System.out.println("SER: Starting");
//        System.out.println("SER: Server socket creating");
//
//        ServerSocket welcomeSocket = new ServerSocket(serverPort);
//        System.out.println("SER: Server socket created");
//        try {
//
//
//            System.out.println("SER: Server socket listening");
//            Socket clientSocket = welcomeSocket.accept();
//
//            System.out.println("SER: Server socket connected");
//
//            System.out.println("serwer słucha");
//            Message request1 = listen(clientSocket);
//
//            System.out.println("serwer wysyła listę");
//            reactToMessage(clientSocket, workingDirectory, request1);
//
//            System.out.println("serwer słucha 2");
//            Message request2 = listen(clientSocket);
//
//            System.out.println("serwer wysyła plik");
//            reactToMessage(clientSocket, workingDirectory, request2);
//
//            System.out.println("serwer słucha 3");
//            Message request3 = listen(clientSocket);
//
//            System.out.println("serwer odbiera plik");
//            reactToMessage(clientSocket, workingDirectory, request3);
//
//        } finally {
//            welcomeSocket.close();
//            System.out.println("SER: Server socket closed");
//        }
//
//
//    }
//}
