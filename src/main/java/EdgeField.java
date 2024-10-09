import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeField {
   private int numFigure, tableID, tableBound, fieldBound, dataType, varcharValue;
   private String name, defaultValue;
   private boolean disallowNull, isPrimaryKey;
   private static String[] strDataType = {"Varchar", "Boolean", "Integer", "Double"};
   public static final int VARCHAR_DEFAULT_LENGTH = 1;
   
   private final Logger logger = LogManager.getLogger(EdgeField.class.getName());

   public EdgeField(String inputString) {
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numFigure = Integer.parseInt(st.nextToken());
      name = st.nextToken();
      tableID = 0;
      tableBound = 0;
      fieldBound = 0;
      disallowNull = false;
      isPrimaryKey = false;
      defaultValue = "";
      varcharValue = VARCHAR_DEFAULT_LENGTH;
      dataType = 0;

      logger.info("EdgeField constructor called with inputString");
   }
   
   public int getNumFigure() {
      logger.info("EdgeField getNumFigure: " + numFigure);
      return numFigure;
   }
   
   public String getName() {
      logger.info("EdgeField getName: " + name);
      return name;
   }
   
   public int getTableID() {
      logger.info("EdgeField getTableID: " + tableID);
      return tableID;
   }
   
   public void setTableID(int value) {
      tableID = value;
      logger.info("EdgeField setTableID: " + value);
   }
   
   public int getTableBound() {
      logger.info("EdgeField getTableBound: " + tableBound);
      return tableBound;
   }
   
   public void setTableBound(int value) {
      tableBound = value;
      logger.info("EdgeField setTableBound: " + value);
   }

   public int getFieldBound() {
      logger.info("EdgeField getFieldBound: " + fieldBound);
      return fieldBound;
   }
   
   public void setFieldBound(int value) {
      fieldBound = value;
      logger.info("EdgeField setFieldBound: " + value);
   }

   public boolean getDisallowNull() {
      logger.info("EdgeField getDisallowNull: " + disallowNull);
      return disallowNull;
   }
   
   public void setDisallowNull(boolean value) {
      disallowNull = value;
      logger.info("EdgeField setDisallowNull: " + value);
   }
   
   public boolean getIsPrimaryKey() {
      logger.info("EdgeField getIsPrimaryKey: " + isPrimaryKey);
      return isPrimaryKey;
   }
   
   public void setIsPrimaryKey(boolean value) {
      isPrimaryKey = value;
      logger.info("EdgeField setIsPrimaryKey: " + value);
   }
   
   public String getDefaultValue() {
      logger.info("EdgeField getDefaultValue: " + defaultValue);
      return defaultValue;
   }
   
   public void setDefaultValue(String value) {
      defaultValue = value;
      logger.info("EdgeField setDefaultValue: " + value);
   }
   
   public int getVarcharValue() {
      logger.info("EdgeField getVarcharValue: " + varcharValue);
      return varcharValue;
   }
   
   public void setVarcharValue(int value) {
      if (value > 0) {
         varcharValue = value;
      }
      logger.info("EdgeField setVarcharValue: " + value);
   }
   public int getDataType() {
      logger.info("EdgeField getDataType: " + dataType);
      return dataType;
   }
   
   public void setDataType(int value) {
      if (value >= 0 && value < strDataType.length) {
         dataType = value;
      }
      logger.info("EdgeField setDataType: " + value);
   }
   
   public static String[] getStrDataType() {
      logger.info("EdgeField getStrDataType: " + strDataType);
      return strDataType;
   }
   
   public String toString() {
      logger.info("EdgeField toString method");
      return numFigure + EdgeConvertFileParser.DELIM +
      name + EdgeConvertFileParser.DELIM +
      tableID + EdgeConvertFileParser.DELIM +
      tableBound + EdgeConvertFileParser.DELIM +
      fieldBound + EdgeConvertFileParser.DELIM +
      dataType + EdgeConvertFileParser.DELIM +
      varcharValue + EdgeConvertFileParser.DELIM +
      isPrimaryKey + EdgeConvertFileParser.DELIM +
      disallowNull + EdgeConvertFileParser.DELIM +
      defaultValue;
   }
}
