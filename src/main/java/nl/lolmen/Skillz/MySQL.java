package nl.lolmen.Skillz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.lolmen.Skills.SkillsSettings;

public class MySQL {

    private String table;
    private boolean fault;
    
    private MySQLConnectionPool pool;

    public MySQL(String host, int port, String username, String password, String database, String table) {
        this.table = table;
        try {
            this.pool = new MySQLConnectionPool("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
            this.setFault(true);
        }
        this.setupDatabase();
    }

    private void setupDatabase() {
        if (this.isFault()) {
            return;
        }
        this.executeStatement("CREATE TABLE IF NOT EXISTS " + this.table
                + "(player varchar(255), "
                + "skill varchar(255), "
                + "xp int, "
                + "level int)");
    }

    public boolean isFault() {
        return fault;
    }

    private void setFault(boolean fault) {
        this.fault = fault;
    }

    public int executeStatement(String statement) {
        if (isFault()) {
            System.out.println("[Skillz] Can't execute statement, something wrong with connection");
            return 0;
        }
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] Statement: " + statement);
        }
        try {
            Statement st = this.pool.getConnection().createStatement();
            int re = st.executeUpdate(statement);
            st.close();
            return re;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ResultSet executeQuery(String statement) {
        if (isFault()) {
            System.out.println("[Skillz] Can't execute query, something wrong with connection");
            return null;
        }
        if (statement.toLowerCase().startsWith("update") || statement.toLowerCase().startsWith("insert") || statement.toLowerCase().startsWith("delete")) {
            this.executeStatement(statement);
            return null;
        }
        if (SkillsSettings.isDebug()) {
            System.out.println("[Skillz - Debug] Query: " + statement);
        }
        try {
            Statement st = this.pool.getConnection().createStatement();
            return st.executeQuery(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        if (isFault()) {
            System.out.println("[Skillz] Can't close connection, something wrong with it");
            return;
        }
        this.pool.close();
    }

    void clean(String table) {
        ResultSet set = this.executeQuery("SELECT COUNT(*), player, skill FROM " + table + " GROUP BY player,skill HAVING COUNT(*) >1;");
        //finds duplicate entries
        if(set == null){
            System.out.println("Something is wrong with the database, query returned null");
            return;
        }
        try {
            while(set.next()){
                this.executeStatement("DELETE FROM " + table + " WHERE player='" + set.getString("player") + "' AND skill='" + set.getString("skill") + "' LIMIT " + (set.getInt(1) - 1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}