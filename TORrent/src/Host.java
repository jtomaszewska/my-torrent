import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Justynaa on 2018-01-05.
 */
public class Host {

    protected String workingDirectory;
    private ServerSocket welcomeSocket;
    private Thread serverThread;
    private ArrayList<Peer> activePeers = new ArrayList<>();
    private int counter = 1;
    private int hostId;

    public Host(int hostId) {
        this.hostId = hostId;
        workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString() + "\\" + hostId;
    }

    public void startHost(List<String> commandsToRun) throws Exception {
        System.out.println("Uruchomiony nowy host: " + hostId + ", folder: " + workingDirectory);

        if (commandsToRun != null) {
            for (String command : commandsToRun) {
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
                connectToServer(commandSplit[1], commandSplit[2]);
                break;
            case "disconnect":
                disconnectFrom(commandSplit[1]);
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
                System.out.println("zamykanie aplikacji");
                clenaup();
                System.exit(0);
                break;
            case "wait":
                Thread.sleep(Integer.parseInt(commandSplit[1]));
                break;
            case "setworkingdirectory":
                this.workingDirectory = commandSplit[1];
                System.out.println("folder roboczy ustawiony");
                break;
            default:
                System.out.println("nieznane polecenie: " + commandSplit[0]);
                break;
        }
    }

    private void sendFilesToPeer(String[] commandSplit) throws Exception {
        String hostId = commandSplit[1];
        System.out.println("Wysylam pliki do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null) {
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        ArrayList<String> filesList = new ArrayList<>();
        for (int i = 2; i < commandSplit.length; i++) {
            filesList.add(commandSplit[i]);
        }
        peer.sendFiles(filesList);

    }

    private void requestForFiles(String[] commandSplit) throws Exception {
        String hostId = commandSplit[1];
        System.out.println("Wysylam zapytanie o pliki do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null) {
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        ArrayList<String> filesList = new ArrayList<>();
        for (int i = 2; i < commandSplit.length; i++) {
            filesList.add(commandSplit[i]);
        }
        peer.requestForFiles(filesList);
    }

    private void requestForFilesList(String hostId) throws Exception {
        System.out.println("Wysylam zapytanie o listę plików do " + hostId);
        Peer peer = findPeerByName(hostId);
        if (peer == null) {
            System.out.println("Nieznany peer: " + hostId);
            return;
        }
        peer.requestFilesList();
    }


    private void connectToServer(String ip, String port) throws Exception {
        final int serverPort = Integer.parseInt(port);
        System.out.println("Peer łączenie z: " + ip +" : "+ serverPort);
        Socket clientSocket;
        try {
            clientSocket = new Socket(ip, serverPort);
        }
        catch(Exception e)
        {
            System.out.println("nie można połączyć z "+ip+" "+serverPort);
            return;
        }
        addNewPeer(clientSocket);
    }

    private void clenaup() throws Exception {
        if(welcomeSocket!=null){
            welcomeSocket.close();
        }
        for (Peer peer:activePeers) {
            disconnectFrom(peer.getName());
        }
    }

    private void disconnectFrom(String hostId) throws Exception {
        System.out.println("Rozłączenie z peerem " + hostId);
        Peer peerToDisconnect = findPeerByName(hostId);
        if(peerToDisconnect!=null){
            peerToDisconnect.getClientSocket().close();
            removePeer(peerToDisconnect);
        } else {
            System.out.println("Podany peer nie istnieje");
        }
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
        System.out.println("Serwer uruchomiony na porcie: " + serverPort);
        welcomeSocket = new ServerSocket(serverPort);
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
        Peer peer = new Peer();
        peer.setClientSocket(clientSocket);
        peer.setName("Peer_" + hostId + "_" + counter);
        peer.setHost(this);
        peer.startListening();
        activePeers.add(peer);
        counter++;
        System.out.println("Dodano połączenie z: " + peer.getName());

    }

    public void removePeer(Peer peer) {
        activePeers.remove(peer);
        System.out.println("Rozłączono z "+peer.getName());
    }
}
