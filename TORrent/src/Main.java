import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Justynaa on 2018-01-06.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Brak argumentow: Main id skrypt");
            return;
        }

        int id = Integer.parseInt(args[0]);


        Host host = new Host(id);
        List<String> commands = null;
        if (args.length == 2) {
            Path file = Paths.get(args[1]);
            commands = Files.readAllLines(file);
            System.out.println("wczytano skrypt z poleceniami "+args[1]);
        }
        host.run(commands);

    }


}
