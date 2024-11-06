import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

// import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
// import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EdgeConvertFileParserTest {
    EdgeConvertFileParser testObj;
    File dummyEdgeFile;
    File nonValidFile;
    EdgeTable[] dummyTables;
    EdgeField[] dummyFields;
    EdgeConnector[] dummyConnectors;

    File dummySaveFile;

    @Before
    public void setUp() throws Exception {
        dummyEdgeFile = new File("Courses.edg");
        nonValidFile = new File("nonValidFile.txt");
        dummySaveFile = new File("Dummy.sav");

        testObj = new EdgeConvertFileParser(dummyEdgeFile);

        // Initialize alTables with dummy data for isTableDup Section
        // EdgeTable[] dummyTables = new ArrayList<EdgeTable>(); //.makeArray()
        // dummyTables.add(new EdgeTable("1|TableTest"));

        // EdgeTable table = new EdgeTable("1|TableTest");
        // EdgeTable[] dummyTables = new EdgeTable[] { table };
        // testObj.setAlTables(dummyTables);

    }
    
    // getter tests
    @Test
    public void testEdgeTableReturn() {
        // testObj.parseEdgeFile();
        EdgeTable[] dummyTable = testObj.getEdgeTables();
        assertNotNull("Edge tables returned", dummyTable);
    }

    @Test
    public void testEdgeFieldReturn() {
        // testObj.parseEdgeFile();
        EdgeField[] dummyField = testObj.getEdgeFields();
        assertNotNull("Edge fields returned", dummyField);

    }

    // constructor
    @Test
    public void testDefaultConstructor() {
        assertNotNull("Default EDGE File parser instance should not be null", testObj);

        // tables and fields should contain values so not null
        assertNotNull("Tables populated with data", testObj.getEdgeTables());
        assertNotNull("Fields populated with data", testObj.getEdgeFields());
    }

    // public void testNoArgConstructor() {
    //     EdgeConvertFileParser testObj = new EdgeConvertFileParser();
    //     assertNotNull("Default EDGE File parser instance should not be null", testObj);

    //     // tables and fields should be null
    //     assertNull("Tables should be null", testObj.getEdgeTables());
    //     assertNull("Fields should be null", testObj.getEdgeFields());
    // }

    // openFile tests
    @Test
    public void testValidEdgeDiagramFile() {
        testObj.openFile(dummyEdgeFile);

        assertNotNull("Tables populated with data", testObj.getEdgeTables());
        assertNotNull("Fields populated with data", testObj.getEdgeFields());
    }

    @Test (expected = FileNotFoundException.class)
    public void testNonValidEdgeDiagramFile() throws Exception {
        // Exception exception = assertThrows(java.io.FileNotFoundException.class, () -> {
        //     testObj.openFile(nonValidFile);
        // });
        testObj.openFile(nonValidFile);
    }

    @Test
    public void testValidSaveFile() {
        testObj.openFile(dummySaveFile);

        assertNotNull("Tables populated with data", testObj.getEdgeTables());
        assertNotNull("Fields populated with data", testObj.getEdgeFields());
    }

    // parseEdgeFile tests

    @Test 
    public void testEntryFigureWithEntityStyle() throws IOException {
        // testObj.parseEdgeFile();
        EdgeTable[] dummyTable = testObj.getEdgeTables();
        assertNotNull("Table should be created for entity figures", dummyTable);
        assertTrue("Table should have at least one item", dummyTable.length> 0);
    }

    // @Test
    public void testEntryFigureWithDuplicate() throws Exception {

    //     try (MockedStatic<JOptionPane> mock = mockStatic(JOptionPane.class)) {
    // //     testObj.parseEdgeFile();
    // //     boolean dup = testObj.isTableDup("TableTest");
    // // STUDENT
    // //     assertTrue("Table with this name already exists", dup);
    //     mock.verify(() -> 
    //     JOptionPane.showMessageDialog(null, 
    //         "There are multiple tables called " + text + 
    //         " in this diagram.\nPlease rename all but one of them and try again.",
    //         JOptionPane.ERROR_MESSAGE),
    //     times(1));
    //     //JOptionPane.showMessageDialog(null, "There are multiple tables called " + text + " in this diagram.\nPlease rename all but one of them and try again.");
    //     }
            // Thread dialogThread = new Thread(() -> {
            //     JOptionPane.showMessageDialog(null, "There are multiple tables called " + text + " in this diagram.\nPlease rename all but one of them and try again.");
            // });
        testObj = new EdgeConvertFileParser(dummyEdgeFile);
        String text = "STUDENT";

        SwingUtilities.invokeAndWait(() -> {
            JOptionPane.showMessageDialog(null, "There are multiple tables called " + text + " in this diagram.\nPlease rename all but one of them and try again.");
        });
    

        // Wait for a short while to allow the dialog to appear
        Thread.sleep(6000); 

        // Check if the dialog is showing by checking if it's showing in the parent window
        Window[] windows = Window.getWindows();
        boolean dialogVisible = false;

        for (Window window : windows) {
            if (window instanceof JDialog && ((JDialog) window).isVisible()) {
                dialogVisible = true;
                break;
            }
        }

        // Verify that the dialog is visible
        assertTrue("dialog is visible", dialogVisible);
    }

    @Test
    public void testEntryFigureWithBlankName() throws IOException {
        // assertThat error message "multiple tables called {text} displays"

        // testObj.parseEdgeFile();
        // boolean readName = EdgeConvertGUI.isReadSuccess();
        // getReadSuccess
        // assertFalse("Read success should be false if name is blank", readName);
    }

    // given attribute field, alFields should add a new EdgeField with isPrimaryKey set 
    @Test 
    public void testAttributeStyle() throws IOException{
        testObj.parseEdgeFile();
        EdgeField[] dummyFields = testObj.getEdgeFields();
        assertNotNull("Fields should be populated", dummyFields);
        assertTrue("at least one key should be set", dummyFields[0].getIsPrimaryKey());
    }

    // resolveConnectors() and end of parseEdgeFile() tests
    @Test
    public void testConnectorWithBothEndpoints() throws Exception {
        // testObj.parseEdgeFile();
        EdgeConvertFileParser instance = new EdgeConvertFileParser(dummyEdgeFile);
        Field dummyCon = EdgeConvertFileParser.class.getDeclaredField("connectors");
        dummyCon.setAccessible(true);
        // EdgeConnector[] dummyCon = testObj.connectors; //getEdgeConnectors doesn't exist?
        Object value = dummyCon.get(instance);

        EdgeConnector[] connectors = (EdgeConnector[]) value;
        assertNotNull("Connectors should be populated", connectors);
        assertTrue("Connector should have two endpoints", connectors[0].getEndPoint1() != 0 && connectors[0].getEndPoint2() != 0);

    }

    // // @Test
    // public void testConnectorWithSingleEndpoint() {
    //     testObj.parseEdgeFile();
    //     EdgeConnector[] dummyCon = testObj.getEdgeConnectors();
    //     assertNotNull("Connectors should be populated", dummyCon);
    //     assertTrue("Connector should have two endpoints", 
    //         (dummyCon[0].getEndPoint1() == 0 && dummyCon[0].getEndPoint2() != 0) ||
    //         (dummyCon[0].getEndPoint1() != 0 && dummyCon[0].getEndPoint2() == 0));

    // }

    // // @Test
    // public void testConnectorWithTableAndUnassignedFields() {
    //     testObj.parseEdgeFile();
    //     EdgeConnector[] dummyCon = testObj.getEdgeConnectors();
    //     EdgeField[] dummyFields = testObj.getEdgeFields();
    //     assertNotNull("Connector should be populated", dummyCon);
    //     assertNotNull("Fields should be populated", dummyFields);
    //     assertTrue("Unassigned field should exist", dummyFields[0].getTableID() == 0);
    // }

    // parseSaveFile tests
    @Test
    public void testCorrectTableFormat() throws IOException {
        // setup - create table entry in correct format, creates a EdgeTable assigning it valid NativeFields, RelativeTables, and RelatedFields
        testObj.parseSaveFile();
        EdgeTable[] dummyTables = testObj.getEdgeTables();
        assertNotNull("Table should be created from correct format", dummyTables);
        assertTrue("Native fields should be assigned", dummyTables[0].getNativeFieldsArray().length > 0);
        assertTrue("Related tables should be assigned", dummyTables[0].getRelatedTablesArray().length > 0);
    }

    @Test 
    public void testCorrectFieldFormat() throws IOException {
        //  Given a field entry with correct format, create a new EdgeField and set all properties, including optional DefaultValue
        testObj.parseSaveFile();
        EdgeField[] dummyFields = testObj.getEdgeFields();
        assertNotNull("Fields should be in correct format", dummyFields);
        assertNotNull("Default value should be assigned if possible", dummyFields[0].getDefaultValue());
    }

    // makeArrays tests
    @Test
    public void testListToArray() {
        // testObj.makeArrays();
        assertNotNull("Tables array should be created", testObj.getEdgeTables());
        assertNotNull("Fields array should be created", testObj.getEdgeFields());
        // assertNotNull("Connectors array should be created", testObj.getEdgeConnectors());
    }

    // // @Test
    // public void testSomeNullList() {
    //     testObj.setAlTables(null);
    //     testObj.makeArrays();
    //     assertNull("Tables array should be null", testObj.getEdgeTables());
    // }

    // // @Test
    // public void testAllNullList() {
    //     testObj.setAlTables(null);
    //     testObj.setAlFields(null);
    //     testObj.setAlConnectors(null);
    //     testObj.makeArrays();

    //     assertNull("Tables should be null", testObj.getEdgeTables());
    //     assertNull("Fields should be null", testObj.getEdgeFields());
    //     assertNull("Connectors should be null", testObj.getEdgeConnectors());
    // }

    // isTableDup tests
    @Test
    public void testTableNameExistsInProgram() throws Exception {
        // String exisitngTableName = "Table1";
        // boolean dummyName = testObj.isTableDup(exisitngTableName);
        // assertTrue("Table name already exists in file", dummyName);

        EdgeConvertFileParser instance = new EdgeConvertFileParser(dummyEdgeFile);
        Method isTableDupMethod = EdgeConvertFileParser.class.getDeclaredMethod("isTableDup", String.class);
        isTableDupMethod.setAccessible(true);
        // EdgeConnector[] dummyCon = testObj.connectors; //getEdgeConnectors doesn't exist?
        Object result = isTableDupMethod.invoke(instance, "STUDENT");
        boolean resultBool = Boolean.valueOf(result.toString());

        assertEquals(resultBool, true);
    }

    @Test
    public void testTableNameDoesNotExist() throws Exception {
    //     String newTableName = "dummyTableName";
    //     boolean dummyName = testObj.isTableDup(newTableName);
    //     assertFalse("Table does not currently exist in file", dummyName);
        EdgeConvertFileParser instance = new EdgeConvertFileParser(dummyEdgeFile);
        Method isTableDupMethod = EdgeConvertFileParser.class.getDeclaredMethod("isTableDup", String.class);
        isTableDupMethod.setAccessible(true);
        // EdgeConnector[] dummyCon = testObj.connectors; //getEdgeConnectors doesn't exist?
        Object result = isTableDupMethod.invoke(instance, "dummyTableName");
        boolean resultBool = Boolean.valueOf(result.toString());

        assertEquals(resultBool, false);
    }
}


