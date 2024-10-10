import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeConvertGUI {

   private static final Logger logger = LogManager.getLogger(EdgeConvertGUI.class.getName());
   
   public static final int HORIZ_SIZE = 635;
   public static final int VERT_SIZE = 400;
   public static final int HORIZ_LOC = 100;
   public static final int VERT_LOC = 100;
   public static final String DEFINE_TABLES = "Define Tables";
   public static final String DEFINE_RELATIONS = "Define Relations";
   public static final String CANCELLED = "CANCELLED";
   private static JFileChooser jfcEdge, jfcGetClass, jfcOutputDir;
   private static ExampleFileFilter effEdge, effSave, effClass;
   private File parseFile, saveFile, outputFile, outputDir, outputDirOld;
   private String truncatedFilename;
   private String sqlString;
   private String databaseName;
   EdgeMenuListener menuListener;
   EdgeRadioButtonListener radioListener;
   EdgeWindowListener edgeWindowListener;
   CreateDDLButtonListener createDDLListener;
   private EdgeConvertFileParser ecfp;
   private EdgeConvertCreateDDL eccd;
   private static PrintWriter pw;
   private EdgeTable[] tables; //master copy of EdgeTable objects
   private EdgeField[] fields; //master copy of EdgeField objects
   private EdgeTable currentDTTable, currentDRTable1, currentDRTable2; //pointers to currently selected table(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeField currentDTField, currentDRField1, currentDRField2; //pointers to currently selected field(s) on Define Tables (DT) and Define Relations (DR) screens
   private static boolean readSuccess = true; //this tells GUI whether to populate JList components or not
   private boolean dataSaved = true;
   private ArrayList alSubclasses, alProductNames;
   private String[] productNames;
   private Object[] objSubclasses;

   //Define Tables screen objects
   static JFrame jfDT;
   static JPanel jpDTBottom, jpDTCenter, jpDTCenter1, jpDTCenter2, jpDTCenterRight, jpDTCenterRight1, jpDTCenterRight2, jpDTMove;
   static JButton jbDTCreateDDL, jbDTDefineRelations, jbDTVarchar, jbDTDefaultValue, jbDTMoveUp, jbDTMoveDown;
   static ButtonGroup bgDTDataType;
   static JRadioButton[] jrbDataType;
   static String[] strDataType;
   static JCheckBox jcheckDTDisallowNull, jcheckDTPrimaryKey;
   static JTextField jtfDTVarchar, jtfDTDefaultValue;
   static JLabel jlabDTTables, jlabDTFields;
   static JScrollPane jspDTTablesAll, jspDTFieldsTablesAll;
   static JList jlDTTablesAll, jlDTFieldsTablesAll;
   static DefaultListModel dlmDTTablesAll, dlmDTFieldsTablesAll;
   static JMenuBar jmbDTMenuBar;
   static JMenu jmDTFile, jmDTOptions, jmDTHelp;
   static JMenuItem jmiDTOpenEdge, jmiDTOpenSave, jmiDTSave, jmiDTSaveAs, jmiDTExit, jmiDTOptionsOutputLocation, jmiDTOptionsShowProducts, jmiDTHelpAbout;
   
   //Define Relations screen objects
   static JFrame jfDR;
   static JPanel jpDRBottom, jpDRCenter, jpDRCenter1, jpDRCenter2, jpDRCenter3, jpDRCenter4;
   static JButton jbDRCreateDDL, jbDRDefineTables, jbDRBindRelation;
   static JList jlDRTablesRelations, jlDRTablesRelatedTo, jlDRFieldsTablesRelations, jlDRFieldsTablesRelatedTo;
   static DefaultListModel dlmDRTablesRelations, dlmDRTablesRelatedTo, dlmDRFieldsTablesRelations, dlmDRFieldsTablesRelatedTo;
   static JLabel jlabDRTablesRelations, jlabDRTablesRelatedTo, jlabDRFieldsTablesRelations, jlabDRFieldsTablesRelatedTo;
   static JScrollPane jspDRTablesRelations, jspDRTablesRelatedTo, jspDRFieldsTablesRelations, jspDRFieldsTablesRelatedTo;
   static JMenuBar jmbDRMenuBar;
   static JMenu jmDRFile, jmDROptions, jmDRHelp;
   static JMenuItem jmiDROpenEdge, jmiDROpenSave, jmiDRSave, jmiDRSaveAs, jmiDRExit, jmiDROptionsOutputLocation, jmiDROptionsShowProducts, jmiDRHelpAbout;
   
   public EdgeConvertGUI() {
      menuListener = new EdgeMenuListener();
      radioListener = new EdgeRadioButtonListener();
      edgeWindowListener = new EdgeWindowListener();
      createDDLListener = new CreateDDLButtonListener();
      this.showGUI();
      
      logger.info("EdgeConvertGUI initialized");
   } // EdgeConvertGUI.EdgeConvertGUI()
   
   public void showGUI() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //use the OS native LAF, as opposed to default Java LAF
         // logger.info("here");
         logger.info("UI LAF set");
      } catch (Exception e) {
         logger.warn("Error setting native LAF: " + e);
      }

      logger.info("Intializing GUI screen setup");
      createDTScreen();
      createDRScreen();
      logger.info("Finalized GUI screen setup");
   } //showGUI()

   public void createDTScreen() {//create Define Tables screen
      logger.info("Creating DT screen");
      jfDT = new JFrame(DEFINE_TABLES);
      jfDT.setLocation(HORIZ_LOC, VERT_LOC);
      Container cp = jfDT.getContentPane();
      jfDT.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDT.addWindowListener(edgeWindowListener);
      jfDT.getContentPane().setLayout(new BorderLayout());
      jfDT.setVisible(true);
      jfDT.setSize(HORIZ_SIZE + 150, VERT_SIZE);

      //setup menubars and menus
      logger.debug("Setting up menu bar");
      jmbDTMenuBar = new JMenuBar();
      jfDT.setJMenuBar(jmbDTMenuBar);

      jmDTFile = new JMenu("File");
      jmDTFile.setMnemonic(KeyEvent.VK_F);
      jmbDTMenuBar.add(jmDTFile);
      logger.debug("Added File menu");

      jmiDTOpenEdge = new JMenuItem("Open Edge File");
      jmiDTOpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDTOpenEdge.addActionListener(menuListener);
      logger.debug("Added Open Edge File menu");

      jmiDTOpenSave = new JMenuItem("Open Save File");
      jmiDTOpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDTOpenSave.addActionListener(menuListener);
      logger.debug("Added Open Save File menu");

      jmiDTSave = new JMenuItem("Save");
      jmiDTSave.setMnemonic(KeyEvent.VK_S);
      jmiDTSave.setEnabled(false);
      jmiDTSave.addActionListener(menuListener);
      logger.debug("Added Save menu");

      jmiDTSaveAs = new JMenuItem("Save As...");
      jmiDTSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDTSaveAs.setEnabled(false);
      jmiDTSaveAs.addActionListener(menuListener);
      logger.debug("Added Save As... menu");

      jmiDTExit = new JMenuItem("Exit");
      jmiDTExit.setMnemonic(KeyEvent.VK_X);
      jmiDTExit.addActionListener(menuListener);
      logger.debug("Added Exit menu");

      jmDTFile.add(jmiDTOpenEdge);
      jmDTFile.add(jmiDTOpenSave);
      jmDTFile.add(jmiDTSave);
      jmDTFile.add(jmiDTSaveAs);
      jmDTFile.add(jmiDTExit);
      logger.debug("Finalized DT File menu setup");
      
      jmDTOptions = new JMenu("Options");
      jmDTOptions.setMnemonic(KeyEvent.VK_O);
      jmbDTMenuBar.add(jmDTOptions);
      logger.debug("Added Options menu");

      jmiDTOptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDTOptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDTOptionsOutputLocation.addActionListener(menuListener);
      logger.debug("Added Set Output File Definition Location menu");

      jmiDTOptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDTOptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDTOptionsShowProducts.setEnabled(false);
      jmiDTOptionsShowProducts.addActionListener(menuListener);
      logger.debug("Added Show Database Products Available menu");

      jmDTOptions.add(jmiDTOptionsOutputLocation);
      jmDTOptions.add(jmiDTOptionsShowProducts);
      logger.debug("Finalized DT Options menu setup");
      
      jmDTHelp = new JMenu("Help");
      jmDTHelp.setMnemonic(KeyEvent.VK_H);
      jmbDTMenuBar.add(jmDTHelp);
      jmiDTHelpAbout = new JMenuItem("About");
      jmiDTHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDTHelpAbout.addActionListener(menuListener);
      jmDTHelp.add(jmiDTHelpAbout);
      logger.debug("Finalized Help menu setup");
      
      jfcEdge = new JFileChooser(".");
      jfcOutputDir = new JFileChooser("..");
	   effEdge = new ExampleFileFilter("edg", "Edge Diagrammer Files");
   	effSave = new ExampleFileFilter("sav", "Edge Convert Save Files");
      jfcOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      logger.debug("Set up File Chooser: edg/sav");

      jpDTBottom = new JPanel(new GridLayout(1, 2));

      jbDTCreateDDL = new JButton("Create DDL");
      jbDTCreateDDL.setEnabled(false);
      jbDTCreateDDL.addActionListener(createDDLListener);
      logger.debug("Added Create DDL button");

      jbDTDefineRelations = new JButton (DEFINE_RELATIONS);
      jbDTDefineRelations.setEnabled(false);
      jbDTDefineRelations.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               logger.info("Switch from Define Tables to Define Relations screen");
               jfDT.setVisible(false);
               jfDR.setVisible(true); //show the Define Relations screen
               clearDTControls();
               dlmDTFieldsTablesAll.removeAllElements();
            }
         }
      );
      logger.debug("Added Define Relations button");

      jpDTBottom.add(jbDTDefineRelations);
      jpDTBottom.add(jbDTCreateDDL);
      jfDT.getContentPane().add(jpDTBottom, BorderLayout.SOUTH);

      jpDTCenter = new JPanel(new GridLayout(1, 3));
      jpDTCenterRight = new JPanel(new GridLayout(1, 2));
      dlmDTTablesAll = new DefaultListModel();
      jlDTTablesAll = new JList(dlmDTTablesAll);
      jlDTTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDTTablesAll.getSelectedIndex();
               logger.debug("List selection changed. Selected index: " + selIndex);
               if (selIndex >= 0) {
                  String selText = dlmDTTablesAll.getElementAt(selIndex).toString();
                  logger.info("Selected table: " + selText);
                  setCurrentDTTable(selText); //set pointer to the selected table
                  int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
                  jlDTFieldsTablesAll.clearSelection();
                  dlmDTFieldsTablesAll.removeAllElements();
                  jbDTMoveUp.setEnabled(false);
                  jbDTMoveDown.setEnabled(false);
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     logger.debug("Adding field: " + getFieldName(currentNativeFields[fIndex]));
                     dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
               }
               disableControls();
            }
         }
      );
      
      dlmDTFieldsTablesAll = new DefaultListModel();
      jlDTFieldsTablesAll = new JList(dlmDTFieldsTablesAll);
      jlDTFieldsTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               logger.debug("Field selection changed. Selected index: " + selIndex);
               if (selIndex >= 0) {
                  if (selIndex == 0) {
                     jbDTMoveUp.setEnabled(false);
                  } else {
                     jbDTMoveUp.setEnabled(true);
                  }
                  if (selIndex == (dlmDTFieldsTablesAll.getSize() - 1)) {
                     jbDTMoveDown.setEnabled(false);
                  } else {
                     jbDTMoveDown.setEnabled(true);
                  }
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  logger.info("Selected field: " + selText);
                  setCurrentDTField(selText); //set pointer to the selected field
                  enableControls();
                  jrbDataType[currentDTField.getDataType()].setSelected(true); //select the appropriate radio button, based on value of dataType
                  if (jrbDataType[0].isSelected()) { //this is the Varchar radio button
                     logger.debug("Varchar button enabled");
                     jbDTVarchar.setEnabled(true); //enable the Varchar button
                     jtfDTVarchar.setText(Integer.toString(currentDTField.getVarcharValue())); //fill text field with varcharValue
                  } else { //some radio button other than Varchar is selected
                     logger.trace("Non-Varchar type selected. Clear text field and disable button");
                     jtfDTVarchar.setText(""); //clear the text field
                     jbDTVarchar.setEnabled(false); //disable the button
                  }
                  jcheckDTPrimaryKey.setSelected(currentDTField.getIsPrimaryKey()); //clear or set Primary Key checkbox
                  jcheckDTDisallowNull.setSelected(currentDTField.getDisallowNull()); //clear or set Disallow Null checkbox
                  jtfDTDefaultValue.setText(currentDTField.getDefaultValue()); //fill text field with defaultValue
               }
            }
         }
      );
      
      jpDTMove = new JPanel(new GridLayout(2, 1));
      jbDTMoveUp = new JButton("^");
      jbDTMoveUp.setEnabled(false);
      jbDTMoveUp.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex();
               logger.debug("Move up action for index: " + selection);
               currentDTTable.moveFieldUp(selection);
               //repopulate Fields List
               int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  logger.trace("Repopulating field: " + getFieldName(currentNativeFields[fIndex]));
                  dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection - 1);
               dataSaved = false;
               logger.info("Move up successful");
            }
         }
      );
      jbDTMoveDown = new JButton("v");
      jbDTMoveDown.setEnabled(false);
      jbDTMoveDown.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex(); //the original selected index
               logger.debug("Move down action for index: " + selection);
               currentDTTable.moveFieldDown(selection);
               //repopulate Fields List
               int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  logger.trace("Repopulating field: " + getFieldName(currentNativeFields[fIndex]));
                  dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection + 1);
               dataSaved = false;
               logger.info("Move down successful");
            }
         }
      );
      jpDTMove.add(jbDTMoveUp);
      jpDTMove.add(jbDTMoveDown);

      jspDTTablesAll = new JScrollPane(jlDTTablesAll);
      jspDTFieldsTablesAll = new JScrollPane(jlDTFieldsTablesAll);
      jpDTCenter1 = new JPanel(new BorderLayout());
      jpDTCenter2 = new JPanel(new BorderLayout());
      jlabDTTables = new JLabel("All Tables", SwingConstants.CENTER);
      jlabDTFields = new JLabel("Fields List", SwingConstants.CENTER);
      jpDTCenter1.add(jlabDTTables, BorderLayout.NORTH);
      jpDTCenter2.add(jlabDTFields, BorderLayout.NORTH);
      jpDTCenter1.add(jspDTTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jspDTFieldsTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jpDTMove, BorderLayout.EAST);
      jpDTCenter.add(jpDTCenter1);
      jpDTCenter.add(jpDTCenter2);
      jpDTCenter.add(jpDTCenterRight);
      logger.debug("Added components to jpDTCenter");

      strDataType = EdgeField.getStrDataType(); //get the list of currently supported data types
      jrbDataType = new JRadioButton[strDataType.length]; //create array of JRadioButtons, one for each supported data type
      bgDTDataType = new ButtonGroup();
      jpDTCenterRight1 = new JPanel(new GridLayout(strDataType.length, 1));
      logger.debug("Initialized JRadioButton array Data Type");

      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i] = new JRadioButton(strDataType[i]); //assign label for radio button from String array
         jrbDataType[i].setEnabled(false);
         jrbDataType[i].addActionListener(radioListener);
         bgDTDataType.add(jrbDataType[i]);
         jpDTCenterRight1.add(jrbDataType[i]);
         logger.debug("Added radio button for data type: " + strDataType[i]);
      }
      jpDTCenterRight.add(jpDTCenterRight1);
      
      jcheckDTDisallowNull = new JCheckBox("Disallow Null");
      jcheckDTDisallowNull.setEnabled(false);
      jcheckDTDisallowNull.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
               currentDTField.setDisallowNull(jcheckDTDisallowNull.isSelected());
               dataSaved = false;
               logger.debug("Disallow Null checkbox switched to: " + jcheckDTDisallowNull.isSelected());
            }
         }
      );
      
      jcheckDTPrimaryKey = new JCheckBox("Primary Key");
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTPrimaryKey.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
               currentDTField.setIsPrimaryKey(jcheckDTPrimaryKey.isSelected());
               dataSaved = false;
               logger.debug("Primary Key checkbox switched to: " + jcheckDTPrimaryKey.isSelected());
            }
         }
      );
      
      jbDTDefaultValue = new JButton("Set Default Value");
      jbDTDefaultValue.setEnabled(false);
      jbDTDefaultValue.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               String prev = jtfDTDefaultValue.getText();
               boolean goodData = false;
               int i = currentDTField.getDataType();
               logger.info("Setting default value for data type");
               do {
                  String result = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the default value:",
                       "Default Value",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       prev);

                  if ((result == null)) {
                     jtfDTDefaultValue.setText(prev);
                     logger.info("Default value result is null");
                     return;
                  }
                  switch (i) {
                     case 0: //varchar
                        if (result.length() <= Integer.parseInt(jtfDTVarchar.getText())) {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                           logger.debug("Set default varchar value: " + result);
                        } else {
                           logger.warn("Value length exceeds limit");
                           JOptionPane.showMessageDialog(null, "The length of this value must be less than or equal to the Varchar length specified.");
                        }
                        break;
                     case 1: //boolean
                        String newResult = result.toLowerCase();
                        if (newResult.equals("true") || newResult.equals("false")) {
                           jtfDTDefaultValue.setText(newResult);
                           goodData = true;
                           logger.debug("Set default boolean value: " + newResult);
                        } else {
                           logger.warn("Invalid boolean value");
                           JOptionPane.showMessageDialog(null, "You must input a valid boolean value (\"true\" or \"false\").");
                        }
                        break;
                     case 2: //Integer
                        try {
                           int intResult = Integer.parseInt(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                           logger.debug("Set default integer value: " + result);
                        } catch (NumberFormatException nfe) {
                           logger.error("Invalid integer value", nfe);
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not an integer or is outside the bounds of valid integer values.");
                        }
                        break;
                     case 3: //Double
                        try {
                           double doubleResult = Double.parseDouble(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                           logger.debug("Set default double value: " + result);
                        } catch (NumberFormatException nfe) {
                           logger.error("Invalid double value", nfe);
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a double or is outside the bounds of valid double values.");
                        }
                        break;
                     case 4: //Timestamp
                        try {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                           logger.debug("Set default timestamp value: " + result);
                        }
                        catch (Exception e) {
                           logger.error("Error setting timestamp value", e);
                        }
                        break;
                  }
               } while (!goodData);
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTField(selText);
                  currentDTField.setDefaultValue(jtfDTDefaultValue.getText());
                  logger.debug("setCurrentDTField: " + selText);
               }
               dataSaved = false;
            }
         }
      ); //jbDTDefaultValue.addActionListener
      jtfDTDefaultValue = new JTextField();
      jtfDTDefaultValue.setEditable(false);

      jbDTVarchar = new JButton("Set Varchar Length");
      jbDTVarchar.setEnabled(false);
      jbDTVarchar.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               logger.info("Setting Varchar Length");
               String prev = jtfDTVarchar.getText();
               String result = (String)JOptionPane.showInputDialog(
                    null,
                    "Enter the varchar length:",
                    "Varchar Length",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    prev);
               if ((result == null)) {
                  jtfDTVarchar.setText(prev);
                  logger.info("Varchar length result is null");
                  return;
               }
               logger.debug("Varchar length input: " + result);
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               int varchar;
               try {
                  if (result.length() > 5) {
                     logger.warn("Invalid varchar length: " + result);
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
                  varchar = Integer.parseInt(result);
                  logger.debug("Parsed varchar length: " + varchar);
                  if (varchar > 0 && varchar <= 65535) { // max length of varchar is 255 before v5.0.3
                     jtfDTVarchar.setText(Integer.toString(varchar));
                     currentDTField.setVarcharValue(varchar);
                     logger.info("Varchar length set to: " + varchar);
                  } else {
                     logger.warn("Varchar length out of valid range");
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
               } catch (NumberFormatException nfe) {
                  logger.error("Invalid number format for varchar length", nfe);
                  JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a number");
                  jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                  return;
               }
               dataSaved = false;
            }
         }
      );
      jtfDTVarchar = new JTextField();
      jtfDTVarchar.setEditable(false);
      
      jpDTCenterRight2 = new JPanel(new GridLayout(6, 1));
      jpDTCenterRight2.add(jbDTVarchar);
      jpDTCenterRight2.add(jtfDTVarchar);
      jpDTCenterRight2.add(jcheckDTPrimaryKey);
      jpDTCenterRight2.add(jcheckDTDisallowNull);
      jpDTCenterRight2.add(jbDTDefaultValue);
      jpDTCenterRight2.add(jtfDTDefaultValue);
      jpDTCenterRight.add(jpDTCenterRight1);
      jpDTCenterRight.add(jpDTCenterRight2);
      jpDTCenter.add(jpDTCenterRight);
      jfDT.getContentPane().add(jpDTCenter, BorderLayout.CENTER);
      logger.info("Finalized DT Screen creation");
      jfDT.validate();
   } //createDTScreen

   public void createDRScreen() {
      logger.info("Creating DR screen");
      //create Define Relations screen
      jfDR = new JFrame(DEFINE_RELATIONS);
      jfDR.setSize(HORIZ_SIZE, VERT_SIZE);
      jfDR.setLocation(HORIZ_LOC, VERT_LOC);
      jfDR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDR.addWindowListener(edgeWindowListener);
      jfDR.getContentPane().setLayout(new BorderLayout());

      //setup menubars and menus
      logger.debug("Setting up menu bar");
      jmbDRMenuBar = new JMenuBar();
      jfDR.setJMenuBar(jmbDRMenuBar);

      jmDRFile = new JMenu("File");
      jmDRFile.setMnemonic(KeyEvent.VK_F);
      jmbDRMenuBar.add(jmDRFile);
      logger.debug("Added File menu");

      jmiDROpenEdge = new JMenuItem("Open Edge File");
      jmiDROpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDROpenEdge.addActionListener(menuListener);
      logger.debug("Added Open Edge File menu");

      jmiDROpenSave = new JMenuItem("Open Save File");
      jmiDROpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDROpenSave.addActionListener(menuListener);
      logger.debug("Added Open Save File menu");

      jmiDRSave = new JMenuItem("Save");
      jmiDRSave.setMnemonic(KeyEvent.VK_S);
      jmiDRSave.setEnabled(false);
      jmiDRSave.addActionListener(menuListener);
      logger.debug("Added Save menu");

      jmiDRSaveAs = new JMenuItem("Save As...");
      jmiDRSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDRSaveAs.setEnabled(false);
      jmiDRSaveAs.addActionListener(menuListener);
      logger.debug("Added Save As... menu");

      jmiDRExit = new JMenuItem("Exit");
      jmiDRExit.setMnemonic(KeyEvent.VK_X);
      jmiDRExit.addActionListener(menuListener);
      logger.debug("Added Exit menu");

      jmDRFile.add(jmiDROpenEdge);
      jmDRFile.add(jmiDROpenSave);
      jmDRFile.add(jmiDRSave);
      jmDRFile.add(jmiDRSaveAs);
      jmDRFile.add(jmiDRExit);
      logger.debug("Finalized DR File menu setup");

      jmDROptions = new JMenu("Options");
      jmDROptions.setMnemonic(KeyEvent.VK_O);
      jmbDRMenuBar.add(jmDROptions);
      logger.debug("Added Options menu");

      jmiDROptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDROptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDROptionsOutputLocation.addActionListener(menuListener);
      logger.debug("Added Set Output File Definition Location menu");

      jmiDROptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDROptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDROptionsShowProducts.setEnabled(false);
      jmiDROptionsShowProducts.addActionListener(menuListener);
      logger.debug("Added Show Database Products Available menu");

      jmDROptions.add(jmiDROptionsOutputLocation);
      jmDROptions.add(jmiDROptionsShowProducts);
      logger.debug("Finalized DR Options menu setup");

      jmDRHelp = new JMenu("Help");
      jmDRHelp.setMnemonic(KeyEvent.VK_H);
      jmbDRMenuBar.add(jmDRHelp);
      jmiDRHelpAbout = new JMenuItem("About");
      jmiDRHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDRHelpAbout.addActionListener(menuListener);
      jmDRHelp.add(jmiDRHelpAbout);
      logger.debug("Finalized Help menu setup");

      jpDRCenter = new JPanel(new GridLayout(2, 2));
      jpDRCenter1 = new JPanel(new BorderLayout());
      jpDRCenter2 = new JPanel(new BorderLayout());
      jpDRCenter3 = new JPanel(new BorderLayout());
      jpDRCenter4 = new JPanel(new BorderLayout());

      dlmDRTablesRelations = new DefaultListModel();
      jlDRTablesRelations = new JList(dlmDRTablesRelations);
      jlDRTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelations.getSelectedIndex();
               logger.debug("Selected index in TablesRelations: " + selIndex);
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelations.getElementAt(selIndex).toString();
                  logger.info("Selected table relation: " + selText);
                  setCurrentDRTable1(selText);

                  int[] currentNativeFields, currentRelatedTables, currentRelatedFields;
                  currentNativeFields = currentDRTable1.getNativeFieldsArray();
                  currentRelatedTables = currentDRTable1.getRelatedTablesArray();
                  jlDRFieldsTablesRelations.clearSelection();
                  jlDRTablesRelatedTo.clearSelection();
                  jlDRFieldsTablesRelatedTo.clearSelection();
                  dlmDRFieldsTablesRelations.removeAllElements();
                  dlmDRTablesRelatedTo.removeAllElements();
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  logger.debug("clearSelection and removeAllElements");

                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     logger.trace("populating FieldsTablesRelations: " + getFieldName(currentNativeFields[fIndex]));
                     dlmDRFieldsTablesRelations.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
                  for (int rIndex = 0; rIndex < currentRelatedTables.length; rIndex++) {
                     logger.trace("populating TablesRelatedTo: " + getFieldName(currentNativeFields[rIndex]));
                     dlmDRTablesRelatedTo.addElement(getTableName(currentRelatedTables[rIndex]));
                  }
               }
            }
         }
      );

      dlmDRFieldsTablesRelations = new DefaultListModel();
      jlDRFieldsTablesRelations = new JList(dlmDRFieldsTablesRelations);
      jlDRFieldsTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               logger.debug("Selected index in FieldsTablesRelations: " + selIndex);
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelations.getElementAt(selIndex).toString();
                  logger.info("Selected field in table relation: " + selText);
                  setCurrentDRField1(selText);
                  if (currentDRField1.getFieldBound() == 0) {
                     jlDRTablesRelatedTo.clearSelection();
                     jlDRFieldsTablesRelatedTo.clearSelection();
                     dlmDRFieldsTablesRelatedTo.removeAllElements();
                     logger.debug("currentDRFieldBound == 0, clear and remove");
                  } else {
                     jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true);
                     jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true);
                     logger.debug("Set TablesRelations and FieldsTablesRelations");
                  }
               }
            }
         }
      );

      dlmDRTablesRelatedTo = new DefaultListModel();
      jlDRTablesRelatedTo = new JList(dlmDRTablesRelatedTo);
      jlDRTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelatedTo.getSelectedIndex();
               logger.debug("Selected index in TablesRelatedTo: " + selIndex);
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelatedTo.getElementAt(selIndex).toString();
                  logger.info("Selected related table: " + selText);
                  setCurrentDRTable2(selText);
                  int[] currentNativeFields = currentDRTable2.getNativeFieldsArray();
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     logger.trace("populating FieldsTablesRelatedTo: " + getFieldName(currentNativeFields[fIndex]));
                     dlmDRFieldsTablesRelatedTo.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
               }
            }
         }
      );

      dlmDRFieldsTablesRelatedTo = new DefaultListModel();
      jlDRFieldsTablesRelatedTo = new JList(dlmDRFieldsTablesRelatedTo);
      jlDRFieldsTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelatedTo.getSelectedIndex();
               logger.debug("Selected index in FieldsTableRelatedTo: " + selIndex);
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelatedTo.getElementAt(selIndex).toString();
                  logger.info("Selected field in related table: " + selText);
                  setCurrentDRField2(selText);
                  jbDRBindRelation.setEnabled(true);
               } else {
                  jbDRBindRelation.setEnabled(false);
                  logger.debug("selIndex less than 0, BindRelation false");
               }
            }
         }
      );

      jspDRTablesRelations = new JScrollPane(jlDRTablesRelations);
      jspDRFieldsTablesRelations = new JScrollPane(jlDRFieldsTablesRelations);
      jspDRTablesRelatedTo = new JScrollPane(jlDRTablesRelatedTo);
      jspDRFieldsTablesRelatedTo = new JScrollPane(jlDRFieldsTablesRelatedTo);
      jlabDRTablesRelations = new JLabel("Tables With Relations", SwingConstants.CENTER);
      jlabDRFieldsTablesRelations = new JLabel("Fields in Tables with Relations", SwingConstants.CENTER);
      jlabDRTablesRelatedTo = new JLabel("Related Tables", SwingConstants.CENTER);
      jlabDRFieldsTablesRelatedTo = new JLabel("Fields in Related Tables", SwingConstants.CENTER);
      jpDRCenter1.add(jlabDRTablesRelations, BorderLayout.NORTH);
      jpDRCenter2.add(jlabDRFieldsTablesRelations, BorderLayout.NORTH);
      jpDRCenter3.add(jlabDRTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter4.add(jlabDRFieldsTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter1.add(jspDRTablesRelations, BorderLayout.CENTER);
      jpDRCenter2.add(jspDRFieldsTablesRelations, BorderLayout.CENTER);
      jpDRCenter3.add(jspDRTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter4.add(jspDRFieldsTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter.add(jpDRCenter1);
      jpDRCenter.add(jpDRCenter2);
      jpDRCenter.add(jpDRCenter3);
      jpDRCenter.add(jpDRCenter4);
      logger.debug("Added components to jpDRCenter");
      jfDR.getContentPane().add(jpDRCenter, BorderLayout.CENTER);
      jpDRBottom = new JPanel(new GridLayout(1, 3));

      jbDRDefineTables = new JButton(DEFINE_TABLES);
      jbDRDefineTables.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               logger.info("Define Tables button clicked, show Define Tables screen");
               jfDT.setVisible(true); //show the Define Tables screen
               jfDR.setVisible(false);
               clearDRControls();
               depopulateLists();
               populateLists();
            }
         }
      );

      jbDRBindRelation = new JButton("Bind/Unbind Relation");
      jbDRBindRelation.setEnabled(false);
      jbDRBindRelation.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               logger.info("Bind/Unbind Relation button clicked");
               int nativeIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               int relatedField = currentDRField2.getNumFigure();
               if (currentDRField1.getFieldBound() == relatedField) { //the selected fields are already bound to each other
                  int answer = JOptionPane.showConfirmDialog(null, "Do you wish to unbind the relation on field " +
                                                             currentDRField1.getName() + "?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.YES_OPTION) {
                     logger.info("Unbinding relation for field: " + currentDRField1.getName());
                     currentDRTable1.setRelatedField(nativeIndex, 0); //clear the related field
                     currentDRField1.setTableBound(0); //clear the bound table
                     currentDRField1.setFieldBound(0); //clear the bound field
                     jlDRFieldsTablesRelatedTo.clearSelection(); //clear the listbox selection
                  }
                  return;
               }
               if (currentDRField1.getFieldBound() != 0) { //field is already bound to a different field
                  int answer = JOptionPane.showConfirmDialog(null, "There is already a relation defined on field " +
                                                             currentDRField1.getName() + ", do you wish to overwrite it?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                     logger.debug("Revert selections to saved settings");
                     jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true); //revert selections to saved settings
                     jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true); //revert selections to saved settings
                     return;
                  }
                  logger.warn("Field is already bound to different field");
               }
               if (currentDRField1.getDataType() != currentDRField2.getDataType()) {
                  logger.error("Field datatype do not match. Unable to bind");
                  JOptionPane.showMessageDialog(null, "The datatypes of " + currentDRTable1.getName() + "." +
                                                currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                  return;
               }
               if ((currentDRField1.getDataType() == 0) && (currentDRField2.getDataType() == 0)) {
                  if (currentDRField1.getVarcharValue() != currentDRField2.getVarcharValue()) {
                     JOptionPane.showMessageDialog(null, "The varchar lengths of " + currentDRTable1.getName() + "." +
                                                   currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                   "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                     return;
                  }
               }
               currentDRTable1.setRelatedField(nativeIndex, relatedField);
               currentDRField1.setTableBound(currentDRTable2.getNumFigure());
               currentDRField1.setFieldBound(currentDRField2.getNumFigure());
               JOptionPane.showMessageDialog(null, "Table " + currentDRTable1.getName() + ": native field " +
                                             currentDRField1.getName() + " bound to table " + currentDRTable2.getName() +
                                             " on field " + currentDRField2.getName());
               logger.info("Successfully bound: " + currentDRField1.getName());
               dataSaved = false;
            }
         }
      );

      jbDRCreateDDL = new JButton("Create DDL");
      jbDRCreateDDL.setEnabled(false);
      jbDRCreateDDL.addActionListener(createDDLListener);
      logger.debug("Added Create DDL button");

      jpDRBottom.add(jbDRDefineTables);
      jpDRBottom.add(jbDRBindRelation);
      jpDRBottom.add(jbDRCreateDDL);
      jfDR.getContentPane().add(jpDRBottom, BorderLayout.SOUTH);
      logger.info("Finalized DR Screen creation");
   } //createDRScreen
   
   public static void setReadSuccess(boolean value) {
      logger.info("Setting read success to: " + value);
      readSuccess = value;
   }
   
   public static boolean getReadSuccess() {
      logger.info("Getting read success value: " + readSuccess);
      return readSuccess;
   }
   
   private void setCurrentDTTable(String selText) {
      logger.info("Setting current data type table to: " + selText);
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDTTable = tables[tIndex];
            logger.debug("Current data type table set to: " + currentDTTable.getName());
            return;
         }
      }
      logger.warn("Table not found for selection: " + selText);
   }

   private void setCurrentDTField(String selText) {
      logger.info("Setting current data type field to: " + selText);
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) && fields[fIndex].getTableID() == currentDTTable.getNumFigure()) {
            currentDTField = fields[fIndex];
            logger.debug("Current data type field set to: " + currentDTField.getName());
            return;
         }
      }
      logger.warn("Field not found for selection: " + selText);
   }

   private void setCurrentDRTable1(String selText) {
      logger.info("Setting current DR table 1 to: " + selText);
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDRTable1 = tables[tIndex];
            logger.debug("Current data relation table 1 set to: " + currentDRTable1.getName());
            return;
         }
      }
      logger.warn("Table not found for selection: " + selText);
   }

   private void setCurrentDRTable2(String selText) {
      logger.info("Setting current DR table 2 to: " + selText);
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDRTable2 = tables[tIndex];
            logger.debug("Current data relation table 2 set to: " + currentDRTable2.getName());
            return;
         }
      }
      logger.warn("Table not found for selection: " + selText);
   }

   private void setCurrentDRField1(String selText) {
      logger.info("Setting current DR field 1 to: " + selText);
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
             fields[fIndex].getTableID() == currentDRTable1.getNumFigure()) {
            currentDRField1 = fields[fIndex];
            logger.debug("Current DR field 1 set to: " + currentDRField1.getName());
            return;
         }
      }
      logger.warn("Field not found for selection: " + selText);
   }

   private void setCurrentDRField2(String selText) {
      logger.info("Setting current DR field 2 to: " + selText);
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
             fields[fIndex].getTableID() == currentDRTable2.getNumFigure()) {
            currentDRField2 = fields[fIndex];
            logger.debug("Current DR field 2 set to: " + currentDRField2.getName());
            return;
         }
      }
      logger.warn("Field not found for selection: " + selText);
   }
   
   private String getTableName(int numFigure) {
      logger.info("Getting table name for numFigure: " + numFigure);
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (tables[tIndex].getNumFigure() == numFigure) {
            logger.debug("Found table name: " + tables[tIndex].getName());
            return tables[tIndex].getName();
         }
      }
      logger.warn("No table found for numFigure: " + numFigure);
      return "";
   }
   
   private String getFieldName(int numFigure) {
      logger.info("Getting field name for numFigure: " + numFigure);
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (fields[fIndex].getNumFigure() == numFigure) {
            logger.debug("Found field name: " + fields[fIndex].getName());
            return fields[fIndex].getName();
         }
      }
      logger.warn("No field found for numFigure: " + numFigure);
      return "";
   }
   
   private void enableControls() {
      logger.info("Enabling controls.");
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(true);
      }
      jcheckDTPrimaryKey.setEnabled(true);
      jcheckDTDisallowNull.setEnabled(true);
      jbDTVarchar.setEnabled(true);
      jbDTDefaultValue.setEnabled(true);
   }
   
   private void disableControls() {
      logger.info("Disabling controls.");
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(false);
      }
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTDisallowNull.setEnabled(false);
      jbDTDefaultValue.setEnabled(false);
      jtfDTVarchar.setText("");
      jtfDTDefaultValue.setText("");
   }
   
   private void clearDTControls() {
      logger.info("Clearing DT controls.");
      jlDTTablesAll.clearSelection();
      jlDTFieldsTablesAll.clearSelection();
   }
   
   private void clearDRControls() {
      logger.info("Clearing DR controls.");
      jlDRTablesRelations.clearSelection();
      jlDRTablesRelatedTo.clearSelection();
      jlDRFieldsTablesRelations.clearSelection();
      jlDRFieldsTablesRelatedTo.clearSelection();
   }
   
   private void depopulateLists() {
      logger.info("Depopulating lists.");
      dlmDTTablesAll.clear();
      dlmDTFieldsTablesAll.clear();
      dlmDRTablesRelations.clear();
      dlmDRFieldsTablesRelations.clear();
      dlmDRTablesRelatedTo.clear();
      dlmDRFieldsTablesRelatedTo.clear();
   }
   
   private void populateLists() {
      if (readSuccess) {
         logger.info("Populating lists with tables and fields.");
         jfDT.setVisible(true);
         jfDR.setVisible(false);
         disableControls();
         depopulateLists();
         for (int tIndex = 0; tIndex < tables.length; tIndex++) {
            String tempName = tables[tIndex].getName();
            dlmDTTablesAll.addElement(tempName);
            logger.debug("Added table to data type list: " + tempName);
            int[] relatedTables = tables[tIndex].getRelatedTablesArray();
            if (relatedTables.length > 0) {
               dlmDRTablesRelations.addElement(tempName);
               logger.debug("Added table to relations list: " + tempName);
            }
         }
      }
      readSuccess = true;
   }
   
   private void saveAs() {
      int returnVal;
      jfcEdge.addChoosableFileFilter(effSave);
      returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         saveFile = jfcEdge.getSelectedFile();
         logger.info("Selected save file: " + saveFile.getAbsolutePath());
         if (saveFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                logger.warn("User canceled overwrite of existing file: " + saveFile.getAbsolutePath());
                return;
             }
         }
         if (!saveFile.getName().endsWith("sav")) {
            String temp = saveFile.getAbsolutePath() + ".sav";
            saveFile = new File(temp);
            logger.info("Changed save file name to: " + saveFile.getAbsolutePath());
         }
         jmiDTSave.setEnabled(true);
         truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
         jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
         jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
         logger.info("Updated window titles to reflect the save file: " + truncatedFilename);
      } else {
         logger.info("User canceled save dialog.");
         return;
      }
      writeSave();
   }
   
   private void writeSave() {
      if (saveFile != null) {
         try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(saveFile, false)));
            //write the identification line
            pw.println(EdgeConvertFileParser.SAVE_ID);
            logger.info("Writing identification line: " + EdgeConvertFileParser.SAVE_ID);
            //write the tables 
            pw.println("#Tables#");
            logger.info("Writing tables:");
            for (int i = 0; i < tables.length; i++) {
               pw.println(tables[i]);
               logger.debug("Table written: " + tables[i]);
            }
            //write the fields
            pw.println("#Fields#");
            logger.info("Writing fields:");
            for (int i = 0; i < fields.length; i++) {
               pw.println(fields[i]);
               logger.debug("Field written: " + fields[i]);
            }
            //close the file
            pw.close();
            logger.info("Successfully saved to file: " + saveFile.getAbsolutePath());
         } catch (IOException ioe) {
            System.out.println(ioe);
            logger.error("IOException occurred while saving: " + ioe.getMessage());
         }
         dataSaved = true;
      }
   }

   private void setOutputDir() {
      int returnVal;
      outputDirOld = outputDir;
      alSubclasses = new ArrayList();
      alProductNames = new ArrayList();

      returnVal = jfcOutputDir.showOpenDialog(null);
      
      if (returnVal == JFileChooser.CANCEL_OPTION) {
         logger.info("User canceled directory selection.");
         return;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputDir = jfcOutputDir.getSelectedFile();
         logger.info("Selected output directory: " + outputDir.getAbsolutePath());
      }
      
      getOutputClasses();

      if (alProductNames.size() == 0) {
         JOptionPane.showMessageDialog(null, "The path:\n" + outputDir + "\ncontains no valid output definition files.");
         logger.warn("No valid output definition files found in the selected directory: " + outputDir.getAbsolutePath());
         outputDir = outputDirOld;
         return;
      }
      
      if ((parseFile != null || saveFile != null) && outputDir != null) {
         jbDTCreateDDL.setEnabled(true);
         jbDRCreateDDL.setEnabled(true);
         logger.info("Buttons enabled for DDL creation.");
      }

      JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
      logger.info("Available products to create DDL statements: " + displayProductNames());
      jmiDTOptionsShowProducts.setEnabled(true);
      jmiDROptionsShowProducts.setEnabled(true);
   }
   
   private String displayProductNames() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < productNames.length; i++) {
         sb.append(productNames[i] + "\n");
      }
      logger.debug("Displaying product names: " + sb.toString());
      return sb.toString();
   }
   
   private void getOutputClasses() {
      File[] resultFiles = {};
      Class resultClass = null;
      Class[] paramTypes = {EdgeTable[].class, EdgeField[].class};
      Class[] paramTypesNull = {};
      Constructor conResultClass;
      Object[] args = {tables, fields};
      Object objOutput = null;
	
      String classLocation = EdgeConvertGUI.class.getResource("EdgeConvertGUI.class").toString();
      if (classLocation.startsWith("jar:")) {
          String jarfilename = classLocation.replaceFirst("^.*:", "").replaceFirst("!.*$", "");
          logger.info("Jarfile: " + jarfilename);
          System.out.println("Jarfile: " + jarfilename);
          try (JarFile jarfile = new JarFile(jarfilename)) {
              ArrayList<File> filenames = new ArrayList<>();
              for (JarEntry e : Collections.list(jarfile.entries())) {
                  filenames.add(new File(e.getName()));
              }
              resultFiles = filenames.toArray(new File[0]);
          } catch (IOException ioe) {
              logger.error("IOException occurred while reading jar file: " + ioe.getMessage());
              throw new RuntimeException(ioe);
          }
      } 
      else {
          resultFiles = outputDir.listFiles();
      }
      alProductNames.clear();
      alSubclasses.clear();
      try {
         for (int i = 0; i < resultFiles.length; i++) {
         System.out.println(resultFiles[i].getName());
            logger.debug("Processing file: " + resultFiles[i].getName());
            if (!resultFiles[i].getName().endsWith(".class")) {
               logger.warn("Ignoring non-class file: " + resultFiles[i].getName());
               continue; //ignore all files that are not .class files
            }
            resultClass = Class.forName(resultFiles[i].getName().substring(0, resultFiles[i].getName().lastIndexOf(".")));
            if (resultClass.getSuperclass().getName().equals("EdgeConvertCreateDDL")) { //only interested in classes that extend EdgeConvertCreateDDL
               if (parseFile == null && saveFile == null) {
                  conResultClass = resultClass.getConstructor(paramTypesNull);
                  objOutput = conResultClass.newInstance(null);
                  } else {
                  conResultClass = resultClass.getConstructor(paramTypes);
                  objOutput = conResultClass.newInstance(args);
               }
               alSubclasses.add(objOutput);
               Method getProductName = resultClass.getMethod("getProductName", null);
               String productName = (String)getProductName.invoke(objOutput, null);
               alProductNames.add(productName);
               logger.info("Added product name: " + productName);
            }
         }
      } catch (InstantiationException ie) {
         ie.printStackTrace();
         logger.error("InstantiationException occurred: " + ie.getMessage());
      } catch (ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
         logger.error("ClassNotFoundException occurred: " + cnfe.getMessage());
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
         logger.error("IllegalAccessException occurred: " + iae.getMessage());
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
         logger.error("NoSuchMethodException occurred: " + nsme.getMessage());
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
         logger.error("InvocationTargetException occurred: " + ite.getMessage());
      }
      if (alProductNames.size() > 0 && alSubclasses.size() > 0) { //do not recreate productName and objSubClasses arrays if the new path is empty of valid files
         productNames = (String[])alProductNames.toArray(new String[alProductNames.size()]);
         objSubclasses = (Object[])alSubclasses.toArray(new Object[alSubclasses.size()]);
         logger.info("Output classes populated with " + productNames.length + " products.");
      }
   }
   
   private String getSQLStatements() {
      logger.debug("getSQLStatements called.");
      String strSQLString = "";
      String response = (String)JOptionPane.showInputDialog(
                    null,
                    "Select a product:",
                    "Create DDL",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    productNames,
                    null);
                    
      if (response == null) {
         logger.warn("User cancelled product selection.");
         return EdgeConvertGUI.CANCELLED;
      }
      
      int selected;
      for (selected = 0; selected < productNames.length; selected++) {
         if (response.equals(productNames[selected])) {
            logger.info("Selected product: " + productNames[selected]);
            break;
         }
      }

      try {
         Class selectedSubclass = objSubclasses[selected].getClass();
         Method getSQLString = selectedSubclass.getMethod("getSQLString", null);
         Method getDatabaseName = selectedSubclass.getMethod("getDatabaseName", null);
         strSQLString = (String)getSQLString.invoke(objSubclasses[selected], null);
         databaseName = (String)getDatabaseName.invoke(objSubclasses[selected], null);
         logger.info("Generated SQL string: " + strSQLString  + "and database name: " + databaseName);
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
         logger.error("IllegalAccessException occurred while invoking methods: " + iae.getMessage());
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
         logger.error("NoSuchMethodException occured: " + nsme.getMessage());
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
         logger.error("InvocationTargetException occurred: " + ite.getMessage());
      }

      return strSQLString;
   }

   private void writeSQL(String output) {
      logger.debug("writeSQL called with output size: " + output.length());
      jfcEdge.resetChoosableFileFilters();
      String str;
      if (parseFile != null) {
         outputFile = new File(parseFile.getAbsolutePath().substring(0, (parseFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      } else {
         outputFile = new File(saveFile.getAbsolutePath().substring(0, (saveFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      }
      if (databaseName.equals("")) {
         logger.warn("Database name is empty, write operation aborted.");
         return;
      }
      jfcEdge.setSelectedFile(outputFile);
      int returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputFile = jfcEdge.getSelectedFile();
         logger.info("Selected output file: " +  outputFile.getAbsolutePath());
         if (outputFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                                                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                logger.warn("User cancelled overwrite of existing file: " + outputFile.getAbsolutePath());
                return;
             }
         }
         try {
            logger.debug("Writing to file: " + outputFile.getAbsolutePath());
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
            //write the SQL statements
            pw.println(output);
            logger.info("SQL statements written to file: " + outputFile.getAbsolutePath());
            //close the file
            pw.close();
         } catch (IOException ioe) {
            System.out.println(ioe);
            logger.error("IOException occurred while writing SQL to file: " + ioe.getMessage());
         }
      }
   }
   
   class EdgeRadioButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         logger.debug("Radio button action performed.");
         for (int i = 0; i < jrbDataType.length; i++) {
            if (jrbDataType[i].isSelected()) {
               currentDTField.setDataType(i);
               logger.info("Data type set to index: " + i);
               break;
            }
         }
         if (jrbDataType[0].isSelected()) {
            jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
            jbDTVarchar.setEnabled(true);
            logger.debug("VARCHAR data type selected, setting default length: {}", EdgeField.VARCHAR_DEFAULT_LENGTH);
         } else {
            jtfDTVarchar.setText("");
            jbDTVarchar.setEnabled(false);
            logger.debug("Non-VARCHAR data type selected, clearing VARCHAR field.");
         }
         jtfDTDefaultValue.setText("");
         currentDTField.setDefaultValue("");
         dataSaved = false;
         logger.info("Default value cleared, dataSaved set to false.");
      }
   }
   
   class EdgeWindowListener implements WindowListener {
      public void windowActivated(WindowEvent we) {}
      public void windowClosed(WindowEvent we) {}
      public void windowDeactivated(WindowEvent we) {}
      public void windowDeiconified(WindowEvent we) {}
      public void windowIconified(WindowEvent we) {}
      public void windowOpened(WindowEvent we) {}
      
      public void windowClosing(WindowEvent we) {
         logger.debug("Window closing event triggered: " + we.getWindow().getName());
         if (!dataSaved) {
            logger.warn("Data is unsaved, prompting the user to save.");
            int answer = JOptionPane.showOptionDialog(null,
                "You currently have unsaved data. Would you like to save?",
                "Are you sure?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
            logger.debug("User response to unsaved data prompt: " + answer);
            if (answer == JOptionPane.YES_OPTION) {
               logger.debug("User opted to save data before closing.");
               if (saveFile == null) {
                  logger.debug("No existing save file, triggering Save As.");
                  saveAs();
               }
               writeSave();
               logger.info("Data saved successfully.");
            }
            if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
               logger.debug("User cancelled window closing operation.");
               if (we.getSource() == jfDT) {
                  jfDT.setVisible(true);
                  logger.trace("DT window open.");
               }
               if (we.getSource() == jfDR) {
                  jfDR.setVisible(true);
                  logger.trace("DR window open.");
               }
               return;
            }
         }
         logger.info("Exiting the application.");
         System.exit(0); //No was selected
      }
   }
   
   class CreateDDLButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         logger.debug("Create DDL button clicked.");

         while (outputDir == null) {
            logger.warn("Output directory not set. Prompting user to select output directory.");
            JOptionPane.showMessageDialog(null, "You have not selected a path that contains valid output definition files yet.\nPlease select a path now.");
            setOutputDir();
            logger.debug("User prompted to set output directory.");
         }

         logger.info("Output directory set to: " + outputDir.getAbsolutePath());
         getOutputClasses(); //in case outputDir was set before a file was loaded and EdgeTable/EdgeField objects created
         logger.trace("Fetching output classes.");
         sqlString = getSQLStatements();
         if (sqlString.equals(EdgeConvertGUI.CANCELLED)) {
            logger.debug("SQL statement generation was cancelled.");
            return;
         }
         writeSQL(sqlString);
      }
   }

   class EdgeMenuListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         int returnVal;
         if ((ae.getSource() == jmiDTOpenEdge) || (ae.getSource() == jmiDROpenEdge)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  logger.info("User opted not to proceed with opening a new file due to unsaved data.");
                  return;
               }
            }
            jfcEdge.addChoosableFileFilter(effEdge);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               parseFile = jfcEdge.getSelectedFile();
               logger.info("Selected file to open: " + parseFile.getName());
               ecfp = new EdgeConvertFileParser(parseFile);
               tables = ecfp.getEdgeTables();
               for (int i = 0; i < tables.length; i++) {
                  tables[i].makeArrays();
               }
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               saveFile = null;
               jmiDTSave.setEnabled(false);
               jmiDRSave.setEnabled(false);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);
               
               truncatedFilename = parseFile.getName().substring(parseFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               logger.info("File open operation cancelled by user.");
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTOpenSave) || (ae.getSource() == jmiDROpenSave)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  logger.info("New file opening not processed due to unsaved data.");
                  return;
               }
            }
            jfcEdge.addChoosableFileFilter(effSave);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               saveFile = jfcEdge.getSelectedFile();
               logger.info("Selected save file to open: {}", saveFile.getName());
               ecfp = new EdgeConvertFileParser(saveFile);
               tables = ecfp.getEdgeTables();
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               parseFile = null;
               jmiDTSave.setEnabled(true);
               jmiDRSave.setEnabled(true);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);

               truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);

               logger.info("Save file parsed and GUI updated successfully: {}", truncatedFilename);

            } else {
               logger.info("File open operation cancelled by user.");
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs) ||
             (ae.getSource() == jmiDTSave) || (ae.getSource() == jmiDRSave)) {
            if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs)) {
               logger.info("User triggered Save As operation.");
               saveAs();
            } else {
               logger.info("User triggered Save operation.");
               writeSave();
            }
         }
         
         if ((ae.getSource() == jmiDTExit) || (ae.getSource() == jmiDRExit)) {
            if (!dataSaved) {
               int answer = JOptionPane.showOptionDialog(null,
                   "You currently have unsaved data. Would you like to save?",
                   "Are you sure?",
                   JOptionPane.YES_NO_CANCEL_OPTION,
                   JOptionPane.QUESTION_MESSAGE,
                   null, null, null);
               if (answer == JOptionPane.YES_OPTION) {
                  if (saveFile == null) {
                     logger.info("User data saved before exiting.");
                     saveAs();
                  }
               }
               if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
                  logger.info("Exiting operation cancelled by user.");
                  return;
               }
            }
            System.exit(0); //No was selected
         }
         
         if ((ae.getSource() == jmiDTOptionsOutputLocation) || (ae.getSource() == jmiDROptionsOutputLocation)) {
            logger.info("User selected output directory.");
            setOutputDir();
         }

         if ((ae.getSource() == jmiDTOptionsShowProducts) || (ae.getSource() == jmiDROptionsShowProducts)) {
            logger.info("User requested product information.");
            JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
         }
         
         if ((ae.getSource() == jmiDTHelpAbout) || (ae.getSource() == jmiDRHelpAbout)) {
            logger.info("User requested about information.");
            JOptionPane.showMessageDialog(null, "EdgeConvert ERD To DDL Conversion Tool\n" +
                                                "by Stephen A. Capperell\n" +
                                                " 2007-2008");
         }
      } // EdgeMenuListener.actionPerformed()
   } // EdgeMenuListener
} // EdgeConvertGUI
