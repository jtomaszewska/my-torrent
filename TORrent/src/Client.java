import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Justynaa on 2017-12-27.
 */
public class Client {

    public static void makeRequest(Socket clientSocket, RequestType requestType, String fileName) throws Exception {
        OutputStream outStr = clientSocket.getOutputStream();
        ObjectOutputStream obj_out = new ObjectOutputStream(outStr);
        Request request = new Request();
        request.setRequestType(requestType);
        request.setFileName(fileName);
        obj_out.writeObject(request);
    }

    public static void getResponse(Socket clientSocket, String workingDirectory) throws Exception {

        InputStream inStr = clientSocket.getInputStream();
        ObjectInputStream obj_in = new ObjectInputStream((inStr));
        Object receivedObj = obj_in.readObject();
        Response res = (Response) receivedObj;

        switch (res.getRequestType()) {

            case FILES_LIST:
                for (int i = 0; i < res.getFilesList().length; i++) {
                    System.out.println("nazwa: "+res.getFilesList()[i].getFileName()+" suma kontrolna: "+res.getFilesList()[i].getCheckSum());
                }
                break;
            case FILE_PULL:
                String path = workingDirectory + "\\" + res.getFileName();
                if (CheckSum.chceckCheckSum(res.getData(), res.getCheckSum()))
                    FileHelper.fileSave(res.getData(), path);
                break;
        }
    }

    public static void main(String[] args) throws Exception {

        String workingDirectory = "C:\\Users\\Justynaa\\IdeaProjects\\TORrent\\Client";
        final String serverName = "localhost";
        // = localhost - zamiast podawać IP mówimy: szukaj otwartego portu na tej maszynie na ktorej jesteś
        // = 127.0.0.1 - to samo co localhost, ale podane jako adres IP
        // "192.168.0.94"; - IP przypisane w ramach sieci do której aktualnie jest sie podłacząnym

        final int serverPort = 12000;
        Socket clientSocket = new Socket(serverName, serverPort);

        System.out.println("klient wysyła zapytanie o listę plikow");
        makeRequest(clientSocket, RequestType.FILES_LIST, "");

        System.out.println("klient odbiera listę plikow");
        getResponse(clientSocket, workingDirectory);

        System.out.println("wybierz plik wpisujac jego nazwe");
        Scanner read = new Scanner(System.in);
        String fileName = read.nextLine();
        System.out.println("pobranao nazwę pliku");

        System.out.println("klient wysyła zapytanie o plik");
        makeRequest(clientSocket, RequestType.FILE_PULL, fileName);

        System.out.println("klient odbiera plik");
        getResponse(clientSocket, workingDirectory);

        System.out.println("klient czyta swoja liste plikow");
        Response myList = new Response();
        myList.setRequestType(RequestType.FILES_LIST);
        myList.setFilesList(FileHelper.generateFilesList(workingDirectory));
        for (int i = 0; i < myList.getFilesList().length; i++) {
            System.out.println("nazwa: "+myList.getFilesList()[i].getFileName()+" suma kontrolna: "+myList.getFilesList()[i].getCheckSum());
        }
        System.out.println("wybierz plik do wysłania wpisujac jego nazwe");
        Scanner read2 = new Scanner(System.in);
        String fileToSend = read2.nextLine();
        System.out.println("pobranao nazwę pliku");

        System.out.println("klient wysyła plik");
        makeRequest(clientSocket, RequestType.FILE_PUSH, fileToSend);


        clientSocket.close();

    }
}
