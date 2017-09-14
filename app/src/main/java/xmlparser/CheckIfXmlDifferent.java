package xmlparser;

import android.content.Context;
import android.content.ContextWrapper;

import Database.DBReceiveTokenAndUserType;
import Database.DataStoreInTokenAndUserType;

/**
 * Created by Sagar on 6/16/2017.
 */

public class CheckIfXmlDifferent extends ContextWrapper {
    public CheckIfXmlDifferent(Context base) {
        super(base);
    }

    public boolean checkIfDifferent(String received) {
        boolean is_different = false;
        DataStoreInTokenAndUserType db_store = new DataStoreInTokenAndUserType(getApplicationContext());


        DBReceiveTokenAndUserType db_receive = new DBReceiveTokenAndUserType(getApplicationContext());
        String data_in_database = db_receive.receiveXML();

        if (data_in_database == null) {
            //   db_store.storeXML(received);
            is_different = true;
        } else if (data_in_database.equals(received)) {
            is_different = false;
        } else {
            is_different = true;
            //  db_store.storeXML(received);
        }
        return is_different;
    }
}
