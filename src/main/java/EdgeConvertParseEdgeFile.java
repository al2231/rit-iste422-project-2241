import java.io.*;
import javax.swing.*;

public class EdgeConvertParseEdgeFile extends EdgeConvertFileParser {
   private String style;
   private String text;
   private int numFigure, numConnector;
   private int endPoint1, endPoint2;
   private String endStyle1, endStyle2;
   private boolean isEntity, isAttribute, isUnderlined = false;

   
   public EdgeConvertParseEdgeFile(File constructorFile) {
      super(constructorFile);
   }

   @Override
   public void parseFile() throws IOException {
      logger.info("Parsing edge diagram file");
      while ((currentLine = br.readLine()) != null) {
         currentLine = currentLine.trim();
         if (currentLine.startsWith("Figure ")) { // this is the start of a Figure entry
            numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); // get the Figure number
            currentLine = br.readLine().trim(); // this should be "{"
            currentLine = br.readLine().trim();

            logger.debug("Processing figure: " + numFigure);

            if (!currentLine.startsWith("Style")) { // this is to weed out other Figures, like Labels
               logger.debug("Skipping figure");
               continue;
            } else {
               style = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get the
                                                                                                            // Style
                                                                                                            // parameter
               if (style.startsWith("Relation")) { // presence of Relations implies lack of normalization
                  logger.warn("File contains relations, which means lack of normalization");
                  JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile
                        + "\ncontains relations.  Please resolve them and try again.");
                  EdgeConvertGUI.setReadSuccess(false);
                  break;
               }
               if (style.startsWith("Entity")) {
                  logger.debug("Entity found");
                  isEntity = true;
               }
               if (style.startsWith("Attribute")) {
                  logger.debug("Attribute found");
                  isAttribute = true;
               }
               if (!(isEntity || isAttribute)) { // these are the only Figures we're interested in
                  logger.debug("Skipping non-entity/non-attribute figure");
                  continue;
               }
               currentLine = br.readLine().trim(); // this should be Text
               text = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\""))
                     .replaceAll(" ", ""); // get the Text parameter
               if (text.equals("")) {
                  logger.warn("There are entites or attributes with blank names.");
                  JOptionPane.showMessageDialog(null,
                        "There are entities or attributes with blank names in this diagram.\nPlease provide names for them and try again.");
                  EdgeConvertGUI.setReadSuccess(false);
                  break;
               }
               int escape = text.indexOf("\\");
               if (escape > 0) { // Edge denotes a line break as "\line", disregard anything after a backslash
                  text = text.substring(0, escape);
               }

               do { // advance to end of record, look for whether the text is underlined
                  currentLine = br.readLine().trim();
                  if (currentLine.startsWith("TypeUnderl")) {
                     isUnderlined = true;
                  }
               } while (!currentLine.equals("}")); // this is the end of a Figure entry

               if (isEntity) { // create a new EdgeTable object and add it to the alTables ArrayList
                  if (isTableDup(text)) {
                     logger.warn("There is a duplicate table: " + text);
                     JOptionPane.showMessageDialog(null, "There are multiple tables called " + text
                           + " in this diagram.\nPlease rename all but one of them and try again.");
                     EdgeConvertGUI.setReadSuccess(false);
                     break;
                  }
                  alTables.add(new EdgeTable(numFigure + DELIM + text));
                  logger.debug("Added a new table: " + text);
               }
               if (isAttribute) { // create a new EdgeField object and add it to the alFields ArrayList
                  EdgeField tempField = new EdgeField(numFigure + DELIM + text);
                  tempField.setIsPrimaryKey(isUnderlined);
                  alFields.add(tempField);
                  logger.debug("Added a new attribute: " + text);
               }
               // reset flags
               isEntity = false;
               isAttribute = false;
               isUnderlined = false;
            }
         } // if("Figure")
         if (currentLine.startsWith("Connector ")) { // this is the start of a Connector entry
            logger.info("Processing connector");
            numConnector = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); // get the Connector
                                                                                                  // number
            currentLine = br.readLine().trim(); // this should be "{"
            currentLine = br.readLine().trim(); // not interested in Style
            currentLine = br.readLine().trim(); // Figure1
            endPoint1 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
            currentLine = br.readLine().trim(); // Figure2
            endPoint2 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
            currentLine = br.readLine().trim(); // not interested in EndPoint1
            currentLine = br.readLine().trim(); // not interested in EndPoint2
            currentLine = br.readLine().trim(); // not interested in SuppressEnd1
            currentLine = br.readLine().trim(); // not interested in SuppressEnd2
            currentLine = br.readLine().trim(); // End1
            endStyle1 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get the
                                                                                                             // End1
                                                                                                             // parameter
            currentLine = br.readLine().trim(); // End2
            endStyle2 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); // get the
                                                                                                             // End2
                                                                                                             // parameter
            logger.debug("Connector endpoints: " + endPoint1 + ", " + endPoint2);

            do { // advance to end of record
               currentLine = br.readLine().trim();
            } while (!currentLine.equals("}")); // this is the end of a Connector entry

            alConnectors.add(new EdgeConnector(
                  numConnector + DELIM + endPoint1 + DELIM + endPoint2 + DELIM + endStyle1 + DELIM + endStyle2));
            logger.info("Added new connector");
         } // if("Connector")
      } // while()
   } // parseEdgeFile()

   @Override
   protected void resolveConnectors() { //Identify nature of Connector endpoints
      int endPoint1, endPoint2;
      int fieldIndex = 0, table1Index = 0, table2Index = 0;

      logger.info("Starting to resolve connectors");

      for (int cIndex = 0; cIndex < connectors.length; cIndex++) {
         endPoint1 = connectors[cIndex].getEndPoint1();
         endPoint2 = connectors[cIndex].getEndPoint2();
         fieldIndex = -1;
         for (int fIndex = 0; fIndex < fields.length; fIndex++) { //search fields array for endpoints
            if (endPoint1 == fields[fIndex].getNumFigure()) { //found endPoint1 in fields array
               connectors[cIndex].setIsEP1Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint1 was found in
            }
            if (endPoint2 == fields[fIndex].getNumFigure()) { //found endPoint2 in fields array
               connectors[cIndex].setIsEP2Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint2 was found in
            }
         }
         for (int tIndex = 0; tIndex < tables.length; tIndex++) { //search tables array for endpoints
            if (endPoint1 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connectors[cIndex].setIsEP1Table(true); //set appropriate flag
               table1Index = tIndex; //identify which element of the tables array that endPoint1 was found in
            }
            if (endPoint2 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connectors[cIndex].setIsEP2Table(true); //set appropriate flag
               table2Index = tIndex; //identify which element of the tables array that endPoint2 was found in
            }
         }
         
         if (connectors[cIndex].getIsEP1Field() && connectors[cIndex].getIsEP2Field()) { //both endpoints are fields, implies lack of normalization
            logger.warn("The file contains composite attributes, which implies lack of normalization");
            JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile + "\ncontains composite attributes. Please resolve them and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }

         if (connectors[cIndex].getIsEP1Table() && connectors[cIndex].getIsEP2Table()) { //both endpoints are tables
            if ((connectors[cIndex].getEndStyle1().indexOf("many") >= 0) &&
                (connectors[cIndex].getEndStyle2().indexOf("many") >= 0)) { //the connector represents a many-many relationship, implies lack of normalization
               logger.warn("There is a many-many relationship between tables, which implies lack of normalization");
               JOptionPane.showMessageDialog(null, "There is a many-many relationship between tables\n\"" + tables[table1Index].getName() + "\" and \"" + tables[table2Index].getName() + "\"" + "\nPlease resolve this and try again.");
               EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
               break; //stop processing list of Connectors
            } else { //add Figure number to each table's list of related tables
               tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
               tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
               continue; //next Connector
            }
         }
         
         if (fieldIndex >=0 && fields[fieldIndex].getTableID() == 0) { //field has not been assigned to a table yet
            if (connectors[cIndex].getIsEP1Table()) { //endpoint1 is the table
               tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); //tell the field what table it belongs to
            } else { //endpoint2 is the table
               tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); //tell the field what table it belongs to
            }
         } else if (fieldIndex >=0) { //field has already been assigned to a table
            logger.warn("The field " +fields[fieldIndex].getName() +" has already been assigned to a table");
            JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName() + " is connected to multiple tables.\nPlease resolve this and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }
      } // connectors for() loop
   } // resolveConnectors()
}