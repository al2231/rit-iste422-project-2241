import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class EdgeConvertCreateDDLTest {
    EdgeConvertCreateDDL testObj;
    EdgeTable[] tables;
    EdgeField[] fields;

	@Before
	public void setUp() throws Exception {
		tables = new EdgeTable[]{
            new EdgeTable("1|TestTable"),
            new EdgeTable("2|TestTable2")
        };
        fields = new EdgeField[]{
            new EdgeField("1|TestField1"),
            new EdgeField("2|TestField2")
        };

        for (EdgeTable table : tables) {
            table.addRelatedTable(1);
            table.addNativeField(1);
            table.makeArrays();
        }

        testObj = new ConcreteEdgeConvertCreateDDL(tables, fields);
	}

    @Test
    public void givenDefault_whenCalled_tablesAndFieldsAreNull() {

        testObj = new ConcreteEdgeConvertCreateDDL();
        
        assertNull("Tables should be null", testObj.tables);
        assertNull("Fields should be null", testObj.fields);
        assertThat("Maxbound should be equal to 0", testObj.maxBound, is(0));
    }

    @Test
    public void givenParameterizedConstructor(){
        assertThat("testObj's tables array should be equal to the variable tables", testObj.tables, is(tables));
        assertThat("testObj's fields array should be equal to the variables fields", testObj.fields, is(fields));
    }

    @Test
    public void givenEmptyArrays(){
        testObj = new ConcreteEdgeConvertCreateDDL(new EdgeTable[0], new EdgeField[0]);
        assertEquals("Table array length should equal 0", 0, testObj.tables.length);
        assertEquals("Field array length should equal 0", 0, testObj.fields.length);
    }

    @Test
    public void givenInitializeCalled_AndNoTables(){
        testObj = new ConcreteEdgeConvertCreateDDL(new EdgeTable[0], new EdgeField[0]);
        testObj.initialize();
        assertThat("Num bound tables should be empty", testObj.numBoundTables.length, is(0));
        assertEquals("maxBound should still be 0", 0, testObj.maxBound); //changed
    }

    @Test
    public void givenNoRelatedFields_whenInitializeIsCalled_ThenTableShouldBeZero() { 
        for(EdgeTable table : tables) {
            table.makeArrays();
        }

        testObj.initialize();
        assertArrayEquals("numBoundTables should be zeroes", new int[]{0,0}, testObj.numBoundTables);
        assertThat("Maxbound should be equal to 0", testObj.maxBound, is(0));
    }

    @Test
    public void givenRelatedFields_whenInitalizeIsCalled_valuesAreCorrect() {
        tables[0].addNativeField(1);
        tables[1].addNativeField(1);
        for(EdgeTable table : tables) {
            table.makeArrays();
        }

        tables[0].setRelatedField(0, 1);
        tables[1].setRelatedField(0, 1);

        testObj.initialize();
        assertArrayEquals("numBoundTables should reflect the correct counts", new int[]{1,1}, testObj.numBoundTables);
        assertThat("Max bound should be 2", testObj.maxBound, is(1));
    }

    @Test
    public void givenMultipleRelatedFields() {
        tables[0].addNativeField(1);
        tables[0].addNativeField(2);
        tables[1].addNativeField(3);
        tables[1].addNativeField(4);
        tables[1].addNativeField(5);
        for(EdgeTable table : tables) {
            table.makeArrays();
        }

        tables[0].setRelatedField(0, 1);
        tables[0].setRelatedField(2, 2);
        tables[1].setRelatedField(0, 3);
        tables[1].setRelatedField(1, 3);
        tables[1].setRelatedField(2, 3);

        testObj.initialize();
        assertArrayEquals("numBoundTables should reflect the correct counts", new int[]{2,3}, testObj.numBoundTables);
        assertThat("Max bound should be 3", testObj.maxBound, is(3));
    }

    @Test
    public void testInitializeWithMismatched() {
        tables[0].addNativeField(0);
        tables[1].addNativeField(0);
        tables[0].makeArrays();
        tables[0].setRelatedField(0,1);

        testObj.initialize();
        assertArrayEquals("Only relevant counts", new int[]{1,0}, testObj.numBoundTables);
        assertThat("Max bound should be 1", testObj.maxBound, is(1));
    }

    @Test
    public void testInitializeWithDuplicates() {
        tables[0].addNativeField(0);
        tables[1].addNativeField(1);
        tables[0].makeArrays();
        tables[1].makeArrays();
        tables[0].setRelatedField(0, 1);
        tables[0].setRelatedField(0, 2); //duplicate
        tables[1].setRelatedField(0, 0);
        tables[1].setRelatedField(0, 1); //duplicate

        testObj.initialize();
        assertArrayEquals("numBoundTables should count distinct", new int[]{1,1}, testObj.numBoundTables);
        assertEquals("maxBound should be 1", 1, testObj.maxBound);
    }

    @Test 
    public void testInitializeStringBuffer() {
        testObj.initialize();
        assertEquals("String buffer is empty", 0, testObj.sb.length());
    }


    @Test
    public void givenValidNumFigure_whenGetTableCalled_thenReturnTable(){
        assertEquals("Should return TestTable", tables[0], testObj.getTable(1));
    }

    @Test
    public void givenInvalidNumFigure_whenGetTableCalled_thenReturnsNull() {
        assertNull("No table should be returned, should return null", testObj.getTable(19));
    }

    @Test
    public void givenValidNumFigure_whenGetFieldCalled_thenReturnsField() {
        assertEquals("Should return TestField1", fields[0], testObj.getField(1));
    }

    @Test public void givenInvalidNumFigure_whenGetFieldCalled_thenReturnsNull() {
        assertNull("Should not return any field, should return null", testObj.getField(-1));
    }

    //abstract method tests

    @Test
    public void testGetDatabaseName() {
        assertEquals("Database should be 'testDatabase", "testDatabase", testObj.getDatabaseName());
    }

    @Test
    public void testGetProductName() {
        assertEquals("Product should be MySQL", "MySQL", testObj.getProductName());
    }

}