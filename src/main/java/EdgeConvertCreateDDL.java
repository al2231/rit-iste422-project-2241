import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertCreateDDL {
   static String[] products = {"MySQL"};
   protected EdgeTable[] tables; //master copy of EdgeTable objects
   protected EdgeField[] fields; //master copy of EdgeField objects
   protected int[] numBoundTables;
   protected int maxBound;
   protected StringBuffer sb;
   protected int selected;

   private final Logger logger = LogManager.getLogger(EdgeConvertCreateDDL.class.getName());
   
   public EdgeConvertCreateDDL(EdgeTable[] tables, EdgeField[] fields) {
      this.tables = tables;
      this.fields = fields;
      logger.debug("EdgeConvertCreateDDL constructor tables and fields initialized: Tables: " + Arrays.toString(tables) 
         + "Fields: " + Arrays.toString(fields));
      initialize();

      logger.info("EdgeConvertCreateDDL constructor called with EdgeTable[] and EdgeField[]");
   } //EdgeConvertCreateDDL(EdgeTable[], EdgeField[])
   
   public EdgeConvertCreateDDL() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects
      logger.info("EdgeConvertCreateDDL default constructor called");
   } //EdgeConvertCreateDDL()

   public void initialize() {
      logger.info("Initializing num of bound tables");
      numBoundTables = new int[tables.length];
      maxBound = 0;
      sb = new StringBuffer();
      logger.debug("numBoundTables array and StringBuffer initialized");

      for (int i = 0; i < tables.length; i++) { //step through list of tables
         int numBound = 0; //initialize counter for number of bound tables
         int[] relatedFields = tables[i].getRelatedFieldsArray();
         logger.debug("Processing table: " + tables[i].getName() + " , related fields: " + Arrays.toString(relatedFields));
         for (int j = 0; j < relatedFields.length; j++) { //step through related fields list
            if (relatedFields[j] != 0) {
               numBound++; //count the number of non-zero related fields
               logger.debug("Found non-zero related field: " + relatedFields[j]);
            }
         }
         numBoundTables[i] = numBound;
         logger.debug("Table: " + tables[i].getName() + " has " + numBound + " tables");
         
         if (numBound > maxBound) {
            maxBound = numBound;
            logger.debug("Max bound updated to " + maxBound);   
         }
      }
      logger.info("EdgeConvertCreateDDL initialization complete");
   }
   
   protected EdgeTable getTable(int numFigure) {
      logger.debug("getTable for numFigure: " + numFigure);
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (numFigure == tables[tIndex].getNumFigure()) {
            logger.info("Table found for numFigure: " + numFigure 
               + " -> Table: " + tables[tIndex].getName());
            return tables[tIndex];
         }
      }

      logger.warn("No table found for numFigure: " + numFigure);
      return null;
   }
   
   protected EdgeField getField(int numFigure) {
      logger.debug("getField for numFigure: " + numFigure);
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (numFigure == fields[fIndex].getNumFigure()) {
            logger.info("Field found for numFigure: " + numFigure 
               + " -> Field: " + fields[fIndex].getName());
            return fields[fIndex];
         }
      }
      
      logger.warn("No field found for numFigure: " + numFigure);
      return null;
   }

   public abstract String getDatabaseName();

   public abstract String getProductName();
   
   public abstract String getSQLString();
   
   public abstract void createDDL();
   
}//EdgeConvertCreateDDL
