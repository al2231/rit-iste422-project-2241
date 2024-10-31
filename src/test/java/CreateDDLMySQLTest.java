import static org.hamcrest.CoreMatchers.*;
import java.util.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CreateDDLMySQLTest {
	CreateDDLMySQL testObj;

	@Before
	public void setUp() throws Exception {
        //regular constructor
		testObj = new CreateDDLMySQL();
        assertThat(testObj, notNullValue());
    }

    @Test
	public void defaultConstructor() {
        CreateDDLMySQL test = new CreateDDLMySQL();
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

	@Test
	public void generateDatabaseNameisNull_thenReturnEmptyString() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void generateDatabaseNameisEntered_thenReturnDatabaseName() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLisSuccessful() {
        //non-empty database name
        //SQL string contains CREATE DATABASE {dbName}; and USE {dbName};
        assertThat("non-empty database name", testObj.getDatabaseName(), notNullValue());
        assertThat("string contains CREATE DATABASE and USE;", testObj.getSQLString(), is(10.0));
    }

    @Test
	public void createDDLgivenNoPrimaryOrForeignKeys_thenNoKeyConstraints() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLgivenSingleFieldAsPrimaryKey_thenIncludesPrimaryKeyConstraint() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLgivenTablewithForeignKey_thenIncludesForeignKeyConstraint() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLgivenFieldOfVarcharType_thenIncludesVarchar() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLgivenFieldOfNotNull_thenIncludesNotNullConstraint() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
    }

    @Test
	public void createDDLgivenDBNameIsEmpty_thenReadSuccessFalseAndNoSQLDDL() {
        assertThat(testObj, notNullValue());
        assertThat("dep", testObj.getClass(), is(10.0));
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