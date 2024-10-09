import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunEdgeConvert {
   private static final Logger logger = LogManager.getLogger(RunEdgeConvert.class.getName());
   
   public static void main(String[] args) {
      EdgeConvertGUI edge = new EdgeConvertGUI();
      logger.info("RunEdgeConvert main function ran, EdgeConvertGUI called");
   }
}
