public final class CSV{
    private CSV(){};
    public static String[]tokenizeLine(String string){
        String[]tokens=new String[1+string.length()-string.replace(",","").length()];
        boolean quoted=false;
        int tokenStart=0,arrayIndex=0;
        for(int i=0;i<string.length();++i){
            final char c=string.charAt(i);
            if(c==','&&!quoted){
                tokens[arrayIndex++]=string.substring(tokenStart,i).replace("\"","");
                tokenStart=i+1;
            }
            else if(c=='"'){
                if(quoted){
                    if(string.charAt(i+1)=='"')++i;//skip double quotes in quoted sections
                    else quoted=false;
                }
                else{
                    quoted=true;
                }
            }
        }
        tokens[arrayIndex]=string.substring(tokenStart,string.length());
        return tokens;
    }
}