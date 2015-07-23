
import java.util.ArrayList;

public final class CSV{
    private CSV(){};
    public static String[]tokenizeLine(String string){
        ArrayList<String> tokens=new ArrayList<String>(1+string.length()-string.replace(",","").length());
        boolean inQuotes=false;
        int tokenStart=0, arrayIndex=0, len=string.length();
        for(int i=0;i<len;++i){
            final char c=string.charAt(i);
            final char n=(i==len-1) ? ' ' : string.charAt(i+1);

            if(inQuotes){ // look ahead for the end of the quoted field we're in.
                if(c=='"'){
                    if(n!='"'){ // if there's a quote not immediately followed by a quote, we're leaving the quoted block.
                        inQuotes=false;
                        if(n==',') { // we're leaving the token as well, so capture it...
                            String token=string.substring(tokenStart,i).replace("\"\"","\"");
                            tokens.add(token);
                            i++; // ... and advance pointers into the next token...
                            tokenStart=i+1;
                            if (string.charAt(tokenStart)=='"') { // ... and, if we're re-entering a (2nd) quoted field, flag it.
                                i++;
                                tokenStart++;
                                inQuotes=true;
                            }
                        }
                    }
                    else // otherwise, this is a two-quote sequence, which will resolve to a single quote in the output, so skip it.
                        i++; 
                }
            }
            else if(c==','){ // we're in a standard token, so look for a comma...
                String token=string.substring(tokenStart,i).replace("\"\"","\""); // ... capture the token...
                tokens.add(token);
                tokenStart=i+1;
                if(n=='"'){ // ... and check for whether we're entering a new, quoted field.  If so, flag it.
                    i++;
                    tokenStart++;
                    inQuotes=true;
                }
            }
        }
        tokens.add(string.substring(tokenStart,string.length()).replace("\"\"","\"")); // capture whatever's left on the line as the final token
        return tokens.toArray(new String[tokens.size()]);
    }
}