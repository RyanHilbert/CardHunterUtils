
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class FileUtils{

    public static String textFromFile(String path) throws FileNotFoundException{
        File fileData=new File(path);

        if(fileData.isFile()){
            return textFromFile(fileData);
        }
        else{
            throw new FileNotFoundException(path);
        }
    }

    public static String textFromFile(File file){
        StringBuilder sb=new StringBuilder(512);
        try{
            FileInputStream stream=new FileInputStream(file);
            Reader r=new InputStreamReader(stream,"UTF-8");
            int c=0;
            while((c=r.read())!=-1){
                sb.append((char) c);
            }
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    static void writeFile(File file, String text) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), "utf-8"))) {
            writer.write(text);
        } catch (IOException ex) {
            System.out.format("Problem writing to file %s: %s", file.getPath(), ex.getMessage());
        }
    }
}
