package proj.database;

import java.sql.SQLException;

public class mainforpopulate{

    public static void main(String[] args) {
        try {
            Populate.populate();
        } catch (DataBaseConnectionException | SQLException e) {
            e.printStackTrace();
        }
    }
}