import java.util.Properties;
import java.io.FileInputStream;

public class PropsTest {
   public static void main( String[] args ) throws Exception {
      Properties defaultProps = new Properties();
      FileInputStream in = new FileInputStream("defaultProperties");
      defaultProps.load( new FileInputStream("defaultProperties") );
      in.close();
     
      Properties appProps = new Properties( defaultProps ); 
      in = new FileInputStream("appProperties");
      appProps.load(in);
      in.close();
   }
}