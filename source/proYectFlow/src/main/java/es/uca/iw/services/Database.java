package es.uca.iw.proYectFlow.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    public static String getPropertiesUrl() { return "./db.properties"; }

    public static Connection Connection() throws SQLException, IOException {
        Properties properties = Config.Properties(getPropertiesUrl());
        return DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password"));
    }

    public static void LoadDriver() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, IOException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Class.forName(Config.Properties(getPropertiesUrl()).getProperty(
                "jdbc.driverClassName")).getDeclaredConstructor().newInstance();
    }

    public static String String2Sql(String s, boolean bAddQuotes, boolean bAddWildcards) {
        s = s.replace("'", "''");
        if (bAddWildcards) s = "%" + s + "%";
        if (bAddQuotes) s = "'" + s + "'";
        return s;
    }

    public static int Boolean2Sql(boolean b) {
        if (b) return 1;
        else return 0;
    }

    public static int LastId(Connection con) throws IOException, SQLException{
        ResultSet rs = null;
        try {
            rs = con.createStatement().executeQuery(Config.Properties(getPropertiesUrl())
                    .getProperty("jdbc.lastIdSentence"));
            rs.next();
            return rs.getInt("LastId");
        }
        catch (SQLException e) { throw e; }
        finally { if (rs != null) rs.close(); }
    }
}