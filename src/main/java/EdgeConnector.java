import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeConnector {
   private int numConnector, endPoint1, endPoint2;
   private String endStyle1, endStyle2;
   private boolean isEP1Field, isEP2Field, isEP1Table, isEP2Table;

   private static final Logger logger = LogManager.getLogger(EdgeConnector.class.getName());
   
   public EdgeConnector(String inputString) {
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numConnector = Integer.parseInt(st.nextToken());
      endPoint1 = Integer.parseInt(st.nextToken());
      endPoint2 = Integer.parseInt(st.nextToken());
      endStyle1 = st.nextToken();
      endStyle2 = st.nextToken();
      isEP1Field = false;
      isEP2Field = false;
      isEP1Table = false;
      isEP2Table = false;
      
      logger.info("EdgeConnector constructor called with inputString");
   }
   
   public int getNumConnector() {
      logger.info("EdgeConnector getNumConnector: " + numConnector);
   
      return numConnector;
   }
   
   public int getEndPoint1() {
      logger.info("EdgeConnector getEndPoint1: " + endPoint1);
      
      return endPoint1;
   }
   
   public int getEndPoint2() {
      logger.info("EdgeConnector getEndPoint2: " + endPoint2);
      
      return endPoint2;
   }
   
   public String getEndStyle1() {
      logger.info("EdgeConnector getEndStyle1: " + endStyle1);
      
      return endStyle1;
   }
   
   public String getEndStyle2() {
      logger.info("EdgeConnector getEndStyle2: " + endStyle2);
      
      return endStyle2;
   }
   public boolean getIsEP1Field() {
      logger.info("EdgeConnector getIsEP1Field: " + isEP1Field);
      
      return isEP1Field;
   }
   
   public boolean getIsEP2Field() {
      logger.info("EdgeConnector getIsEP2Field: " + isEP2Field);
      
      return isEP2Field;
   }

   public boolean getIsEP1Table() {
      logger.info("EdgeConnector getIsEP1Table: " + isEP1Table);
      
      return isEP1Table;
   }

   public boolean getIsEP2Table() {
      logger.info("EdgeConnector getIsEP2Table: " + isEP2Table);
      
      return isEP2Table;
   }

   public void setIsEP1Field(boolean value) {
      isEP1Field = value;
      
      logger.info("EdgeConnector setIsEP1Field: " + value);
   }
   
   public void setIsEP2Field(boolean value) {
      isEP2Field = value;

      logger.info("EdgeConnector setIsEP1Field: " + value);
   }

   public void setIsEP1Table(boolean value) {
      isEP1Table = value;

      logger.info("EdgeConnector setIsEP1Table: " + value);
   }

   public void setIsEP2Table(boolean value) {
      isEP2Table = value;

      logger.info("EdgeConnector setIsEP2Table: " + value);
   }
}
