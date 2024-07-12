package dados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexoes {
    //private static String url = "jdbc:postgresql://localhost:5432/clientes";
    //private static String userName = "postgres";
    //private static String password = "P057gr3SQL";
    private static String url = "jdbc:mysql://localhost:3306/doacao";
    private static String userName = "root";
    private static String password = "M3uB4nc0d3D4d05()";
    public static Connection conexao(){
        try{
            try {
                //Class.forName("org.postgresql.Driver");
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(url, userName, password);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
