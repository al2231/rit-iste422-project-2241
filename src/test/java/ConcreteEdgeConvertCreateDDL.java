public class ConcreteEdgeConvertCreateDDL extends EdgeConvertCreateDDL {

    public ConcreteEdgeConvertCreateDDL(EdgeTable[] tables, EdgeField[] fields) {
        super(tables, fields);
    }

    public ConcreteEdgeConvertCreateDDL() {
        super();
    }

    // abstract stuff
    @Override
    public String getDatabaseName() {
        return "testDatabase";
    }

    @Override
    public String getProductName() {
        return "MySQL";
    }

    @Override
    public String getSQLString() {
        //idk
        return "";
    }

    @Override
    public void createDDL() {
        // idk
    }
}



