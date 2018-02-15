import java.sql.*;
import java.util.List;

class FuturenetDatabase {
    private Connection connect;

    void connect() throws ClassNotFoundException, SQLException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        connect = DriverManager
                .getConnection("jdbc:mysql://localhost/futurenet?useUnicode=yes&characterEncoding=UTF-8");
        connect.setAutoCommit(false);
    }

    void insertPersons(List<Person> persons, String rootLabel) throws SQLException {
        // Statements allow to issue SQL queries to the database
        PreparedStatement pst = connect.prepareStatement("INSERT INTO profil " +
                "(label, cid, gender, lang, data_user, name) VALUES " +
                "(?,?,?,?,?,?)");
        for (Person p : persons) {
            pst.setString(1, p.label);
            pst.setInt(2, p.cid);
            pst.setInt(3, p.male);
            pst.setString(4, p.lang);
            pst.setInt(5, p.data_user);
            pst.setString(6, p.name);

            pst.addBatch();
        }

        try {
            pst.executeBatch();
        } catch (SQLException e) {
            if (!e.getSQLState().startsWith("23")) {
                throw e;
            }
        }
        pst.close();
        pst = connect.prepareStatement("update profil set friends = ?, visited = ?" +
                " where label = ?");
        pst.setInt(1, persons.size());
        pst.setInt(2,1);
        pst.setString(3, rootLabel);
        pst.executeUpdate();
        connect.commit();
        pst.close();
    }

    String getNextUnvisited() throws SQLException {
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("select label from profil where visited = 0 limit 1");
        if (resultSet.next())
            return resultSet.getString("label");
        else
            return null;
    }

    void setInvalidLink(String label) throws SQLException{
        PreparedStatement pst = connect.prepareStatement("update profil set visited = ?" +
                " where label = ?");
        pst.setInt(1, -1);
        pst.setString(2, label);
        pst.executeUpdate();
        connect.commit();

    }

    void insertTest(String label) throws SQLException{
        PreparedStatement pst = connect.prepareStatement("INSERT INTO profil " +
                "(label) VALUES " +
                "(?)");
        pst.setString(1, label);
        pst.executeUpdate();
        connect.commit();
    }
}