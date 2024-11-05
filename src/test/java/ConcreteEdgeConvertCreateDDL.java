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
        return null;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getSQLString() {
        return null;
    }

    @Override
    public void createDDL() {
        //nothing
    }
}



