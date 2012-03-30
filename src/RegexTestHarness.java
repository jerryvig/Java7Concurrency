import java.io.Console;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTestHarness {
  public static void main( String[] args ) {
     Console con = System.console();
     if ( con == null ) {
	System.err.println( "No console." );
        System.exit(1); 
     }

     while ( true ) {
	 Pattern pattern = Pattern.compile(con.readLine("%nEnter your regex: "));
         Matcher matcher = pattern.matcher(con.readLine("Enter input string to search: "));

         boolean found = false;
         while ( matcher.find() ) {
	     con.format("I found the text" + "\"%s\" starting at " + "index %d and ending at index %d.%n", matcher.group(), matcher.start(), matcher.end());
             found = true;
         }
         if ( !found ) {
	     con.format("No match found.%n");
         }
     }  
  }
}