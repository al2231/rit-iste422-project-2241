import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CreateDDLMySQLTest {
	CreateDDLMySQL testObj;
    EdgeTable[] dummyTables;
    EdgeField[] dummyFields;

	@Before
	public void setUp() throws Exception {

        EdgeTable table1 = new EdgeTable("1|Table");
        EdgeField field1 = new EdgeField("1|Field|0|255|true|false");
        EdgeField field2 = new EdgeField("2|Field|0|255|true|false");
        
        table1.addNativeField(1);
        table1.addNativeField(2);
        table1.makeArrays();

        dummyTables = new EdgeTable[] { table1 };
        dummyFields = new EdgeField[] { field1, field2 };

        //regular constructor
		testObj = new CreateDDLMySQL(dummyTables, dummyFields);
        assertThat("Regular instance created and not null", testObj, notNullValue());
    }

    @Test
	public void defaultConstructor() {
		CreateDDLMySQL test = new CreateDDLMySQL();

        assertThat("Default instance created and not null", test, notNullValue());
    }

	@Test
	public void generateDatabaseNameisNull_thenReturnEmptyString() {
        // user input ""
        testObj.databaseName = "";
        assertThat("Empty database name when user cancels input", 
            testObj.getDatabaseName(), is(""));
    }

    @Test
	public void generateDatabaseNameisEntered_thenReturnDatabaseName() {
        String db = "MySQLDB";
        testObj.generateDatabaseName();
        assertThat("dep", testObj.getDatabaseName(), is(db));
    }

    @Test
	public void createDDLisSuccessful() {

        testObj.databaseName = "MySQLDB";
        //non-empty database name
        //SQL string contains CREATE DATABASE {dbName}; and USE {dbName};
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("non-empty database name", testObj.getDatabaseName(), notNullValue());
        assertThat("string contains CREATE DATABASE and USE;", ddl, containsString("CREATE DATABASE MySQLDB;"));
        assertThat("string contains CREATE DATABASE and USE;", ddl, containsString("USE MySQLDB;"));

    }

    @Test
	public void createDDLgivenNoPrimaryOrForeignKeys_thenNoKeyConstraints() {

        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should not contain primary key constraint", ddl, not(containsString("PRIMARY KEY")));
        assertThat("should not contain foreign key constraint", ddl, not(containsString("FOREIGN KEY")));

    }

    @Test
	public void createDDLgivenSingleFieldAsPrimaryKey_thenIncludesPrimaryKeyConstraint() {
        EdgeTable table1 = new EdgeTable("1|Table");
        EdgeField field1 = new EdgeField("1|Field|0|255|true|false");
        field1.setIsPrimaryKey(true);
        EdgeField field2 = new EdgeField("2|Field|0|255|true|false");
        
        table1.addNativeField(1);
        table1.addNativeField(2);
        table1.makeArrays();

        dummyTables = new EdgeTable[] { table1 };
        dummyFields = new EdgeField[] { field1, field2 };

		testObj = new CreateDDLMySQL(dummyTables, dummyFields);
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain primary key constraint", ddl, containsString("PRIMARY KEY"));

    }

    @Test
	public void createDDLgivenTablewithForeignKey_thenIncludesForeignKeyConstraint() {
        EdgeTable table1 = new EdgeTable("1|Table1");
        EdgeTable table2 = new EdgeTable("2|Table2");
         
        EdgeField field1 = new EdgeField("1|Field1|0|255|true|false");
        EdgeField field2 = new EdgeField("2|Field2|0|255|true|false");
        
        table1.addNativeField(1);
        table1.addRelatedTable(table2.getNumFigure());
        table1.setRelatedField(0, 1);
        table2.addNativeField(2);
        table1.makeArrays();
        table2.makeArrays();

        dummyTables = new EdgeTable[] { table1, table2};
        dummyFields = new EdgeField[] { field1, field2 };

		testObj = new CreateDDLMySQL(dummyTables, dummyFields);
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain foreign key constraint", ddl, containsString("FOREIGN KEY"));

    }

    @Test
	public void createDDLgivenFieldOfVarcharType_thenIncludesVarchar() {
        // setup fields are varchar
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain VARCHAR type for field", ddl, containsString("VARCHAR"));

    }

    @Test
	public void createDDLgivenFieldOfNotNull_thenIncludesNotNullConstraint() {
        EdgeTable table1 = new EdgeTable("1|Table");
        
        EdgeField field1 = new EdgeField("1|Field|0|255|true|false");
        field1.setDisallowNull(true);
        EdgeField field2 = new EdgeField("2|Field|0|255|true|false");
        
        table1.addNativeField(1);
        table1.addNativeField(2);
        table1.makeArrays();

        dummyTables = new EdgeTable[] { table1 };
        dummyFields = new EdgeField[] { field1, field2 };

		testObj = new CreateDDLMySQL(dummyTables, dummyFields);
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain NOT NULL for field", ddl, containsString("NOT NULL"));

    }

    // @Test
	// public void createDDLgivenDBNameIsEmpty_thenReadSuccessFalseAndNoSQLDDL() {

    //     testObj.databaseName = null;
    //     String ddl = testObj.getSQLString();
    //     assertTrue("no SQL DDL generated when db empty", ddl.isEmpty());
    //     assertFalse("read succes should be False", EdgeConvertGUI.isReadSuccess());
    // }

    @Test
	public void getDatabaseName_thenReturnDatabaseName() {

        assertNull("getDatabaseName() = dbName", testObj.getDatabaseName());

    }

    @Test
	public void getProductName_thenReturnMySQL() {

        assertThat("getProductName() = MySQL", testObj.getProductName(), is("MySQL"));

    }
}