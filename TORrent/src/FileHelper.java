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

        List<String> list = new ArrayList<>();
        File[] files = new File(workingDirectory).listFiles();
        for (File file: files){
            list.add(file.getName());
        }
        return readFiles(workingDirectory, list, false);
    }

    public static FileData[] readFiles(String workingDirectory, List<String> filesToRead)throws Exception {
        return readFiles(workingDirectory, filesToRead, true);
    }

    private static FileData[] readFiles(String workingDirectory, List<String> filesToRead, boolean fillData)throws Exception{

        ArrayList<FileData> filesList = new ArrayList<>();
        for (String fileName: filesToRead) {

            String filePath = workingDirectory + "\\" + fileName;
            File f = new File(filePath);
            if(f.exists() && f.isFile()) {
                FileData fileData = new FileData();
                fileData.setFileName(fileName);
                byte[] file = FileHelper.fileRead(filePath);
                fileData.setCheckSum(CheckSum.generateCheckSum(file));
                if (fillData){
                    fileData.setData(file);
                }
                filesList.add(fileData);
            }
            else{
                System.out.println("Plik nie istnieje: " + filePath);
            }

        }

        return filesList.toArray(new FileData[0]);

//        for (int i = 0; i < filesToRead.size(); i++) {
//            filesList[i] = new FileData();
//            filesList[i].setFileName(filesToRead.get(i));
//            byte[] file = FileHelper.fileRead(workingDirectory + "\\" + filesToRead.get(i));
//            filesList[i].setCheckSum(CheckSum.generateCheckSum(file));
//            filesList[i].setData(file);
//            if (fillData){
//                filesList[i].setData(file);
//            }
//
//        }
//        return filesList;
    }

    public static FileData[] createFileDataList(List<String> filesToRead)throws Exception{

        FileData[] filesList = new FileData[filesToRead.size()];
        for (int i = 0; i < filesToRead.size(); i++) {
            filesList[i] = new FileData();
            filesList[i].setFileName(filesToRead.get(i));
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
