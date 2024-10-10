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
      logger.debug("EdgeField getNumFigure: " + numFigure);
      return numFigure;
   }
   
   public String getName() {
      logger.debug("EdgeField getName: " + name);
      return name;
   }
   
   public int getTableID() {
      logger.info("EdgeField getTableID: " + tableID);
      return tableID;
   }
   
   public void setTableID(int value) {
      tableID = value;
      logger.debug("EdgeField setTableID: " + value);
   }
   
   public int getTableBound() {
      logger.debug("EdgeField getTableBound: " + tableBound);
      return tableBound;
   }
   
   public void setTableBound(int value) {
      tableBound = value;
      logger.debug("EdgeField setTableBound: " + value);
   }

   public int getFieldBound() {
      logger.debug("EdgeField getFieldBound: " + fieldBound);
      return fieldBound;
   }
   
   public void setFieldBound(int value) {
      fieldBound = value;
      logger.debug("EdgeField setFieldBound: " + value);
   }

   public boolean getDisallowNull() {
      logger.debug("EdgeField getDisallowNull: " + disallowNull);
      return disallowNull;
   }
   
   public void setDisallowNull(boolean value) {
      disallowNull = value;
      logger.debug("EdgeField setDisallowNull: " + value);
   }
   
   public boolean getIsPrimaryKey() {
      logger.debug("EdgeField getIsPrimaryKey: " + isPrimaryKey);
      return isPrimaryKey;
   }
   
   public void setIsPrimaryKey(boolean value) {
      isPrimaryKey = value;
      logger.debug("EdgeField setIsPrimaryKey: " + value);
   }
   
   public String getDefaultValue() {
      logger.debug("EdgeField getDefaultValue: " + defaultValue);
      return defaultValue;
   }
   
   public void setDefaultValue(String value) {
      defaultValue = value;
      logger.debug("EdgeField setDefaultValue: " + value);
   }
   
   public int getVarcharValue() {
      logger.debug("EdgeField getVarcharValue: " + varcharValue);
      return varcharValue;
   }
   
   public void setVarcharValue(int value) {
      if (value > 0) {
         varcharValue = value;
      }
      logger.debug("EdgeField setVarcharValue: " + value);
   }
   public int getDataType() {
      logger.debug("EdgeField getDataType: " + dataType);
      return dataType;
   }
   
   public void setDataType(int value) {
      if (value >= 0 && value < strDataType.length) {
         dataType = value;
      }
      logger.debug("EdgeField setDataType: " + value);
   }
   
   public static String[] getStrDataType() {
      logger.debug("EdgeField getStrDataType: " + strDataType);
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
