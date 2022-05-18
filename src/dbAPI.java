import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class dbAPI {
    // Constructor
    public dbAPI(){}

    // API method
    // Create table
    public dbTable createTable(String tableName, String[] columnsInput, int columnsCount){
        dbMetaData metaData=new dbMetaData(tableName, columnsInput, columnsCount);
        dbTable table=new dbTable();
        return table;
    }

    // Insert record
    public void insertRecord(dbTable table, String tableName, String[] columnsInput, dbMetaData metaData){
        for (int i=0; i<table.pages.length; i++){
            if (table.pages[i].records.length!=table.pages[i].MAX_RECORD_COUNT){
                dbRecord newRecord=new dbRecord(metaData, columnsInput);
                for (i=0; i<columnsInput.length; i++){
                    
                }
                table.pages[i].records[table.pages[i].recordCount]=
            }
        }
    }

    // Search record
    public void searchRecord(dbTable table){
        // Search
//        dbSlottedPage slot=new dbSlottedPage();
//        return slot;
    }

    // Column search
    public void columnSearch(String columnName){
        // Find by column
    }

    // Object to bytes
    public byte[] convertObjectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream boas=new ByteArrayOutputStream();
        try(ObjectOutputStream ois = new ObjectOutputStream(boas)){
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }

}
