import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Justynaa on 2018-01-05.
 */
public class Host {


    protected String workingDirectory = "C:\\Users\\Justynaa\\IdeaProjects\\TORrent\\Client";

    private ServerSocket welcomeSocket;
    private Thread serverThread;

    private ArrayList<Peer> activePeers = new ArrayList<>();
    private int counter = 1;
    private int hostId;

    public Host(int hostId) {
        this.hostId = hostId;
        workingDirectory =  Paths.get(".").toAbsolutePath().normalize().toString() + "\\" + hostId;
    }

    public void startHost(List<String> commandsToRun) throws Exception {
        System.out.println("Starting host: " + hostId + ", working dir: " + workingDirectory);

        if (commandsToRun != null){
            for (String command : commandsToRun){
                System.out.println("Uruchamiam polecenie " + command);
                processCommand(command);
            }
        }

        Scanner read = new Scanner(System.in);
        String command;
        while (true) {
            System.out.println("podaj polecenie");
            command = read.nextLine();
            processCommand(command);
        }


    }

    private void processCommand(String command) throws Exception {
        String[] commandSplit = command.split(" ");
        switch (commandSplit[0].toLowerCase()) {
            case "runserver":
                if (commandSplit.length != 2) {
                    System.out.println("składnia polecania: runserver port");
                    break;
                }
                serverThread = new Thread(() -> {
                    try {
                        startServer(commandSplit[1]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                serverThread.start();
                break;
            case "connect":
                connectToServer(commandSplit[1]);
                break;
            case "disconnect":
                disconnectFromServer(commandSplit[1]);
                break;
            case "fileslist":
                requestForFilesList(commandSplit[1]);
                break;
            case "downloadfile":
                requestForFiles(commandSplit);
                break;
            case "sendfile":
                sendFilesToPeer(commandSplit);
                break;
            case "exit":
                clenaup();
                System.out.println("zamykanie aplikacji");
                System.exit(0);
                break;
            case "wait":
                Thread.sleep(Integer.parseInt(commandSplit[1]));
                break;
            case "setworkingdirectory":
                this.workingDirectory = commandSplit[1];
                break;
            default:
                System.out.println("nieznane polecenie: " + commandSplit[0]);
                break;
        }
    }

    private void sendFilesToPeer(String[] commandSplit) throws Exception {
        String hostId = commandSplit[1];
        System.out.println("Wysyłam pliki do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null){
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        ArrayList<String> filesList = new ArrayList<>();
        for (int i = 2; i < commandSplit.length; i++){
            filesList.add(commandSplit[i]);
        }
        peer.sendFiles(filesList);

    }

    private void requestForFiles(String[] commandSplit) throws Exception {
        String hostId = commandSplit[1];
        System.out.println("Wysyłam zapytanie o pliki do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null){
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        ArrayList<String> filesList = new ArrayList<>();
        for (int i = 2; i < commandSplit.length; i++){
            filesList.add(commandSplit[i]);
        }
        peer.requestForFiles(filesList);
    }

    private void requestForFilesList(String hostId) throws Exception {
        System.out.println("Wysyłam zapytanie o listę plików do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null){
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        peer.requestFilesList();
    }


    private void connectToServer(String port) throws Exception {
        final int serverPort = Integer.parseInt(port);
        System.out.println("Peer: Connecting to localhost port: " + serverPort);
        Socket clientSocket = new Socket("localhost", serverPort);
        addNewPeer(clientSocket);
    }

    private void clenaup() throws Exception {
        welcomeSocket.close();
        //to do pozamykać połączenia z peerami
    }

    private void disconnectFromServer(String hostId) throws Exception {
        System.out.println("Peer: Disconnecting "+hostId);
        Peer peerToDisconnect = findPeerByName(hostId);
        peerToDisconnect.getClientSocket().close();
        activePeers.remove(peerToDisconnect);
        System.out.println("Peer: Disconnected "+hostId);
    }

    private Peer findPeerByName(String hostId) throws Exception {
        for (Peer activePeer : activePeers) {
            if (Objects.equals(activePeer.getName(), hostId)) {
                return activePeer;
            }
        }
        return null;
    }

    private void startServer(String port) throws Exception {
        final int serverPort = Integer.parseInt(port);
        System.out.println("SER: Starting on port: " + serverPort);

        System.out.println("SER: Server socket creating");
        welcomeSocket = new ServerSocket(serverPort);
        System.out.println("SER: Server socket listening");


        do {
            Socket clientSocket;
            try {
                clientSocket = welcomeSocket.accept();
            } catch (SocketException es) {
                return;
            }

            addNewPeer(clientSocket);


        } while (true);

    }

    private void addNewPeer(Socket clientSocket) throws Exception {

        System.out.println("SER: new incoming connection");

        Peer peer = new Peer();
        peer.setClientSocket(clientSocket);
        peer.setName("Peer_" + hostId + "_" + counter);
        peer.setWorkingDirectory(workingDirectory);
        peer.startListening();
        activePeers.add(peer);
        counter++;

        System.out.println("SER: new peer added: " + peer.getName());

    }
}
