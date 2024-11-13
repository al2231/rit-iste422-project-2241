import java.io.*;
import java.util.*;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertFileParser {
   //private String filename = "test.edg";
   protected File parseFile; //changed from private to protected
   protected FileReader fr;
   protected BufferedReader br;
   protected String currentLine;
   protected ArrayList<EdgeTable> alTables;
   protected ArrayList<EdgeField> alFields;
   protected ArrayList<EdgeConnector> alConnectors; //changes from private to protected
   protected EdgeTable[] tables;
   protected EdgeField[] fields;
   // private EdgeField tempField;
   protected EdgeConnector[] connectors;
   // private String style;
   // private String text;
   // private String tableName;
   // private String fieldName;
   private boolean isEntity, isAttribute, isUnderlined = false;
   private int numFigure, numConnector;
   // numFields, numTables, numNativeRelatedFields;
   // private int endPoint1, endPoint2;
   private int numLine;
   // private String endStyle1, endStyle2;
   public static final String EDGE_ID = "EDGE Diagram File"; //first line of .edg files should be this
   public static final String SAVE_ID = "EdgeConvert Save File"; //first line of save files should be this
   public static final String DELIM = "|";
   protected static final Logger logger = LogManager.getLogger(EdgeConvertFileParser.class.getName());
   
   public EdgeConvertFileParser(File constructorFile) {
      numFigure = 0;
      numConnector = 0;
      alTables = new ArrayList();
      alFields = new ArrayList();
      alConnectors = new ArrayList();
      isEntity = false;
      isAttribute = false;
      parseFile = constructorFile;
      numLine = 0;
      this.openFile(parseFile);

      logger.info("EdgeConvertFileParser constructor called with constructorFile");

   }

   public abstract void parseFile() throws IOException;
   protected abstract void resolveConnectors();

   protected void makeArrays() { //convert ArrayList objects into arrays of the appropriate Class type
      logger.info("Converting ArrayList to arrays");
      if (alTables != null) {
         tables = (EdgeTable[])alTables.toArray(new EdgeTable[alTables.size()]);
      }
      if (alFields != null) {
         fields = (EdgeField[])alFields.toArray(new EdgeField[alFields.size()]);
      }
      if (alConnectors != null) {
         connectors = (EdgeConnector[])alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
      }
   }
   
   protected boolean isTableDup(String testTableName) {
      for (int i = 0; i < alTables.size(); i++) {
         EdgeTable tempTable = (EdgeTable)alTables.get(i);
         if (tempTable.getName().equals(testTableName)) {
            logger.warn("There is a duplicate table");
            return true;
         }
      }
      logger.debug("There are no duplicate tables.");
      return false;
   }
   
   public EdgeTable[] getEdgeTables() {
      logger.debug("EdgeConvertFileParser getEdgeTables: " + tables);
      return tables;
   }
   
   public EdgeField[] getEdgeFields() {
      logger.debug("EdgeConvertFileParser getEdgeFields: " + fields);
      return fields;
   }
   
   public void openFile(File inputFile) {
      logger.info("Opening file.");
      try {
         fr = new FileReader(inputFile);
         br = new BufferedReader(fr);
         //test for what kind of file we have
         currentLine = br.readLine().trim();
         numLine++;
         if (currentLine.startsWith(EDGE_ID)) { //the file chosen is an Edge Diagrammer file
            logger.debug("Edge file found.");
            // EdgeConvertParseEdgeFile edgFile = new EdgeConvertParseEdgeFile(inputFile); //parse the file
            this.parseFile();
            br.close();
            this.makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type
            this.resolveConnectors(); //Identify nature of Connector endpoints
         } else {
            if (currentLine.startsWith(SAVE_ID)) { //the file chosen is a Save file created by this application
               logger.debug("Save file found");
               // EdgeConvertParseSaveFile savFile = new EdgeConvertParseSaveFile(inputFile); //parse the file
               //parse the file
               this.parseFile();
               br.close();
               this.makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type
            } else { //the file chosen is something else
               logger.warn("Wrong type of file chosen");
               JOptionPane.showMessageDialog(null, "Unrecognized file format");
            }
         }
      } // try
      catch (FileNotFoundException fnfe) {
         logger.error("Cannot find \"" + inputFile.getName() + "\".");
         System.exit(0);
      } // catch FileNotFoundException
      catch (IOException ioe) {
         logger.error("IOException" + ioe);
         System.exit(0);
      } // catch IOException
   } // openFile()
} // EdgeConvertFileHandler
