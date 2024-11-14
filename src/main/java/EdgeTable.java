import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeTable {
   private int numFigure;
   private String name;
   private ArrayList<Integer> alRelatedTables, alNativeFields;
   private int[] relatedTables, relatedFields, nativeFields;
   private static final Logger logger = LogManager.getLogger(EdgeTable.class.getName());
   
   public EdgeTable(String inputString) {
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numFigure = Integer.parseInt(st.nextToken());
      name = st.nextToken();
      alRelatedTables = new ArrayList();
      alNativeFields = new ArrayList();
      logger.info("EdgeTable constructor called with inputString");
   }
   
   public int getNumFigure() {
      logger.debug("EdgeTable getNumFigure: " + numFigure);
      return numFigure;
   }
   
   public String getName() {
      logger.debug("EdgeTable getName: " + name);
      return name;
   }
   
   public void addRelatedTable(int relatedTable) {
      // first warning resolved
      //alRelatedTables.add(new Integer(relatedTable));
      alRelatedTables.add(Integer.valueOf(relatedTable));
      logger.info("EdgeTable add a related table: " + relatedTable);
   }
   
   public int[] getRelatedTablesArray() {
      logger.debug("EdgeTable getRelatedTablesArray: " + relatedTables);
      return relatedTables;
   }
   
   public int[] getRelatedFieldsArray() {
      logger.debug("EdgeTable getRelatedFieldsArray: " + relatedFields);
      return relatedFields;
   }
   
   public void setRelatedField(int index, int relatedValue) {
      relatedFields[index] = relatedValue;
      logger.debug("EdgeTable setRelatedField at index " + index + ": " + relatedValue);
   }
   
   public int[] getNativeFieldsArray() {
      logger.debug("EdgeTable getNativeFieldsArray: " + nativeFields);
      return nativeFields;
   }

   public void addNativeField(int value) {
      // second warning resolved
      // alNativeFields.add(new Integer(value));
      alNativeFields.add(Integer.valueOf(value));
      logger.debug("EdgeTable add a native field: " + value);
   }

   public void moveFieldUp(int index) { //move the field closer to the beginning of the list
      if (index == 0) {
         logger.warn("Field is at the beginning of the list.");
         return;
      }
      int tempNative = nativeFields[index - 1]; //save element at destination index
      nativeFields[index - 1] = nativeFields[index]; //copy target element to destination
      nativeFields[index] = tempNative; //copy saved element to target's original location
      int tempRelated = relatedFields[index - 1]; //save element at destination index
      relatedFields[index - 1] = relatedFields[index]; //copy target element to destination
      relatedFields[index] = tempRelated; //copy saved element to target's original location

      logger.info("Field moved to " + index);
   }
   
   public void moveFieldDown(int index) { //move the field closer to the end of the list
      if (index == (nativeFields.length - 1)) {
         logger.warn("Field is at the end of the list.");
         return;
      }
      int tempNative = nativeFields[index + 1]; //save element at destination index
      nativeFields[index + 1] = nativeFields[index]; //copy target element to destination
      nativeFields[index] = tempNative; //copy saved element to target's original location
      int tempRelated = relatedFields[index + 1]; //save element at destination index
      relatedFields[index + 1] = relatedFields[index]; //copy target element to destination
      relatedFields[index] = tempRelated; //copy saved element to target's original location

      logger.info("Field moved to " + index);
   }

   public void makeArrays() { //convert the ArrayLists into int[]
      Integer[] temp;

      logger.info("Starting to convert Arraylists to arrays.");
      temp = (Integer[])alNativeFields.toArray(new Integer[alNativeFields.size()]);
      nativeFields = new int[temp.length];
      for (int i = 0; i < temp.length; i++) {
         nativeFields[i] = temp[i].intValue();
      }

      logger.debug("Converted to nativeFields. Length: " + nativeFields.length);
      
      temp = (Integer[])alRelatedTables.toArray(new Integer[alRelatedTables.size()]);
      relatedTables = new int[temp.length];
      for (int i = 0; i < temp.length; i++) {
         relatedTables[i] = temp[i].intValue();
      }

      logger.debug("Converted to relatedTables. Length: " + relatedTables.length);
      
      relatedFields = new int[nativeFields.length];
      for (int i = 0; i < relatedFields.length; i++) {
         relatedFields[i] = 0;
      }

      logger.info("Finished converting to int arrays.");
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("Table: " + numFigure + "\r\n");
      sb.append("{\r\n");
      sb.append("TableName: " + name + "\r\n");
      sb.append("NativeFields: ");
      for (int i = 0; i < nativeFields.length; i++) {
         sb.append(nativeFields[i]);
         if (i < (nativeFields.length - 1)){
            sb.append(EdgeConvertFileParser.DELIM);
         }
      }
      sb.append("\r\nRelatedTables: ");
      for (int i = 0; i < relatedTables.length; i++) {
         sb.append(relatedTables[i]);
         if (i < (relatedTables.length - 1)){
            sb.append(EdgeConvertFileParser.DELIM);
         }
      }
      sb.append("\r\nRelatedFields: ");
      for (int i = 0; i < relatedFields.length; i++) {
         sb.append(relatedFields[i]);
         if (i < (relatedFields.length - 1)){
            sb.append(EdgeConvertFileParser.DELIM);
         }
      }
      sb.append("\r\n}\r\n");

      logger.info("EdgeTable toString method: " + sb.toString());
      
      return sb.toString();
   }
}
