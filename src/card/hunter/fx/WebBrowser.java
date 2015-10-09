package card.hunter.fx;

import java.io.IOException;

public class WebBrowser {
    public static void launch(String url) {
        // inspired by http://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
        Runtime rt = Runtime.getRuntime();
        
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.indexOf( "win" ) >= 0)
                rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
            else if (os.indexOf( "mac" ) >= 0)
                rt.exec( "open " + url);
            else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {        
                String[] browsers = { "epiphany", "firefox", "mozilla", 
                    "konqueror", "netscape", "opera", "links", "lynx"
                };

                StringBuffer cmd = new StringBuffer();
                for (int i=0; i<browsers.length; i++)
                    cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");

                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            }
        } catch (IOException ex) {
            ErrorMsg.print("Failed to launch external browser: ", ex);
        }
        
        return;
    }
}
