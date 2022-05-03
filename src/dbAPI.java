import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
    public void insertRecord(String tableName){
        // Insert
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
