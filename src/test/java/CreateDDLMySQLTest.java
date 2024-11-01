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
        
        // table1.addNativeField(1);

        dummyTables = new EdgeTable[] { table1 };
        dummyFields = new EdgeField[] { field1 };

        //regular constructor
		testObj = new CreateDDLMySQL(dummyTables, dummyFields);
        assertThat(testObj, notNullValue());
    }

    @Test
	public void defaultConstructor() {
		CreateDDLMySQL test = new CreateDDLMySQL();

        assertThat(test, notNullValue());
    }

	@Test
	public void generateDatabaseNameisNull_thenReturnEmptyString() {
        testObj.generateDatabaseName();
        assertThat("Empty database name when user cancels input", 
            testObj.getDatabaseName(), is(""));
    }

    @Test
	public void generateDatabaseNameisEntered_thenReturnDatabaseName() {
        String db = "test";
        testObj.generateDatabaseName();
        assertThat("dep", testObj.getDatabaseName(), is(db));
    }

    @Test
	public void createDDLisSuccessful() {

        testObj.databaseName = "test";
        //non-empty database name
        //SQL string contains CREATE DATABASE {dbName}; and USE {dbName};
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("non-empty database name", testObj.getDatabaseName(), notNullValue());
        assertThat("string contains CREATE DATABASE and USE;", ddl, containsString("CREATE DATABASE test;"));
        assertThat("string contains CREATE DATABASE and USE;", ddl, containsString("USE test;"));

    }

    @Test
	public void createDDLgivenNoPrimaryOrForeignKeys_thenNoKeyConstraints() {

        testObj.databaseName = "noKeys";
        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should not contain primary key constraint", ddl, not(containsString("PRIMARY KEY")));
        assertThat("should not contain foreign key constraint", ddl, not(containsString("FOREIGN KEY")));

    }

    @Test
	public void createDDLgivenSingleFieldAsPrimaryKey_thenIncludesPrimaryKeyConstraint() {

        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain primary key constraint", ddl, containsString("PRIMARY KEY"));

    }

    @Test
	public void createDDLgivenTablewithForeignKey_thenIncludesForeignKeyConstraint() {

        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain foreign key constraint", ddl, containsString("FOREIGN KEY"));

    }

    @Test
	public void createDDLgivenFieldOfVarcharType_thenIncludesVarchar() {

        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain VARCHAR type for field", ddl, containsString("VARCHAR"));

    }

    @Test
	public void createDDLgivenFieldOfNotNull_thenIncludesNotNullConstraint() {

        testObj.createDDL();
        String ddl = testObj.getSQLString();
        assertThat("should contain NOT NULL for field", ddl, containsString("NOT NULL"));

    }

    @Test
	public void createDDLgivenDBNameIsEmpty_thenReadSuccessFalseAndNoSQLDDL() {

        testObj.databaseName = "";
        String ddl = testObj.getSQLString();
        assertTrue("no SQL DDL generated when db empty", ddl.isEmpty());
        // assertFalse("read succes should be False", EdgeConvertGUI.isReadSuccess());
    }

    @Test
	public void getDatabaseName_thenReturnDatabaseName() {

        assertThat("getDatabaseName() = dbName", testObj.getDatabaseName(), is(""));

    }

    @Test
	public void getProductName_thenReturnMySQL() {

        assertThat("getProductName() = MySQL", testObj.getProductName(), is("MySQL"));

    }
}