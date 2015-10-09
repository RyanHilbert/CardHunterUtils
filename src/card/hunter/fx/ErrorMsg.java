package card.hunter.fx;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorMsg {
    public static String format(String desc, Exception ex) {
        // inspired by: http://stackoverflow.com/questions/1149703/how-can-i-convert-a-stack-trace-to-a-string
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return String.format("%s\nMessage: %s\nTrace: %s", desc, ex.getMessage(), sw.toString());
    }
    
    public static void print(String desc, Exception ex) {
        System.out.println(format(desc, ex));
    }
}
