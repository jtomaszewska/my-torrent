import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justynaa on 2017-12-27.
 */
public class FileHelper {


    public static FileData[] generateFilesList(String workingDirectory)throws Exception{

        List<String> list = new ArrayList();
        File[] files = new File(workingDirectory).listFiles();
        for (File file: files){
            list.add(file.getName());
        }
        FileData[] filesList = new FileData[list.size()];

        for (int i = 0; i < list.size(); i++) {
            filesList[i] = new FileData();
            filesList[i].setFileName(list.get(i));
            byte[] file = FileHelper.fileRead(workingDirectory + "\\" + list.get(i));
            filesList[i].setCheckSum(CheckSum.generateCheckSum(file));
        }
        return filesList;
    }

    public static byte[] fileRead(String filePath)throws Exception{
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public static void fileSave(byte[] file, String filePath) throws Exception {
        FileOutputStream stream = new FileOutputStream(filePath);
        try {
            stream.write(file);
        } finally {
            stream.close();
        }
    }
}
