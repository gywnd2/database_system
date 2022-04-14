public class dbAPI {
    // Constructor
    public dbAPI(){}

    // API method
    // Create table
    public dbTable createTable(){
        dbTable table=new dbTable();
        return table;
    }

    // Insert record
    public void insertRecord(dbTable table){
        // Insert
    }

    // Search record
    public dbSlottedPage searchRecord(dbTable table){
        // Search
        dbSlottedPage slot=new dbSlottedPage();
        return slot;
    }

    // Column search
    public void columnSearch(String columnName){
        // Find by column
    }

}
