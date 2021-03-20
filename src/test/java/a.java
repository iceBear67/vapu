import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class a {
    private static final Pattern CS_INVOKE=Pattern.compile("(a\\.cs\\()(\\d*)(\\))");
    public static void main(String... a){
        Matcher matches=CS_INVOKE.matcher("a.cs(111)a.cs(222)");
        while(matches.find()){
            System.out.println(matches.group(2));
        }
    }
}
