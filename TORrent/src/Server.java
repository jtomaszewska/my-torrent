import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Justynaa on 2017-12-27.
 */


public class Server {

    public static Request listen(Socket clientSocket) throws Exception {
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object receivedFile = objectInputStream.readObject();
        Request request = (Request) receivedFile;
        System.out.println(request.getRequestType());
        return request;
    }

    public static void reactToRequest(Socket clientSocket, String workingDirectory, Request r) throws Exception {
        OutputStream outStr = clientSocket.getOutputStream();
        ObjectOutputStream obj_out = new ObjectOutputStream(outStr);

        switch (r.getRequestType()) {
            case FILES_LIST:
                System.out.println("SER: wysyłanie listy plików");
                FileData[] filesList = FileHelper.generateFilesList(workingDirectory);
//                List<String> list = new ArrayList();
//                File[] files = new File(workingDirectory).listFiles();
//                for (File file: files){
//                    list.add(file.getName());
//                }
//                String[] filesList = new String[files.length];
//                for (int i = 0; i < files.length; i++) {
//                    filesList[i] = list.get(i);
//                }
//                for (int i = 0; i <filesList.length; i++) {
//                    System.out.println(i+" "+filesList[i]);
//                }
                Response resultList = new Response();
                resultList.setFilesList(filesList);
                resultList.setRequestType(RequestType.FILES_LIST);
                obj_out.writeObject(resultList);
                break;
            case FILE_PULL:
                System.out.println("SER: wysyłanie plików");

                String fileName = r.getFileName();
                byte[] file = FileHelper.fileRead(workingDirectory + "\\" + fileName);
                Response resultFile = new Response();
                resultFile.setFileName(fileName);
                resultFile.setData(file);
                resultFile.setRequestType(RequestType.FILE_PULL);
                byte[] checkSum = CheckSum.generateCheckSum(file);
                resultFile.setCheckSum(checkSum);
                System.out.println("suma kontrolna " + checkSum);
                obj_out.writeObject(resultFile);
                break;
            case FILE_PUSH:
                Response saveFile = new Response();
                saveFile.setFileName(r.getFileName());
                String path = workingDirectory + "\\" + saveFile.getFileName();
                if (CheckSum.chceckCheckSum(saveFile.getData(), saveFile.getCheckSum()))
                    FileHelper.fileSave(saveFile.getData(), path);
                break;

        }
    }

    public static void main(String[] args) throws Exception {

        String workingDirectory = "C:\\Users\\Justynaa\\IdeaProjects\\TORrent\\Server";
        final int serverPort = 12000;

        System.out.println("SER: Starting");
        System.out.println("SER: Server socket creating");

        ServerSocket welcomeSocket = new ServerSocket(serverPort);
        System.out.println("SER: Server socket created");
        try {


            System.out.println("SER: Server socket listening");
            Socket clientSocket = welcomeSocket.accept();

            System.out.println("SER: Server socket connected");

            System.out.println("serwer słucha");
            Request request1 = listen(clientSocket);

            System.out.println("serwer wysyła listę");
            reactToRequest(clientSocket, workingDirectory, request1);

            System.out.println("serwer słucha 2");
            Request request2 = listen(clientSocket);

            System.out.println("serwer wysyła plik");
            reactToRequest(clientSocket, workingDirectory, request2);

            System.out.println("serwer słucha 3");
            Request request3 = listen(clientSocket);

            System.out.println("serwer odbiera plik");
            reactToRequest(clientSocket, workingDirectory, request3);

        } finally {
            welcomeSocket.close();
            System.out.println("SER: Server socket closed");
        }


    }
}
