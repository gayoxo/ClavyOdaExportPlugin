package fdi.ucm.server.exportparser.oda2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;

/**
 * Clase que implementa la conexion con un Servidor de oda en bade MySQL
 * @author Joaquin Gayoso-Cabada
 *
 */
public class MySQLConnectionOdA2 {
	

	
	private static MySQLConnectionOdA2 instance;
	private Connection conexion;

	
	
	private static final String DriverDatabase="com.mysql.jdbc.Driver";
	private static final String ErrorMySQLConnection="Error en driver de conexion al mySQL";
	private static final String ErrorCOnexionDB="Error en conexion a base de datos";
	private static final String ErrorUpdate="Error ejecutando Update Querry: ";
	private static final String ErrorSelect="Error ejecutando Querry: ";
	private static final String ErrorInsert="Error ejecutando Insert: ";
	private static boolean DataBaseCreada;
	

	public MySQLConnectionOdA2(String dbNameIP,String database,int Port, String user, String password) {
		try {
			Class.forName(DriverDatabase);
			InicializacionAnonima(dbNameIP,database,Port,user,password); 
		} catch (ClassNotFoundException e) {
			System.err.println(ErrorMySQLConnection);
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println(ErrorCOnexionDB);
			e.printStackTrace();
		}  
	}
	
	public MySQLConnectionOdA2(String database,String user, String password) {
		
			try {
				Class.forName(DriverDatabase);
				InicializacionAnonima(database,user,password); 
			} catch (ClassNotFoundException e) {
					System.err.println(ErrorMySQLConnection);
					e.printStackTrace();
					throw new CompleteImportRuntimeException(ErrorMySQLConnection);
				} catch (SQLException e) {
					System.err.println(ErrorCOnexionDB);
					e.printStackTrace();
					throw new CompleteImportRuntimeException(ErrorMySQLConnection);
				}
		
	}

	private void InicializacionAnonima(String dbNameIP,String database, int port, String user, String password) throws SQLException {
		String DBaseServerUnknow = "jdbc:mysql://"+dbNameIP+":"+port+"/";
		conexion = DriverManager.getConnection(DBaseServerUnknow, user, password);	
		ResultSet resultSet = conexion.getMetaData().getCatalogs();

//		database=database.toLowerCase();
		boolean encontrado=false;
        while (resultSet.next()) {

          String databaseName = resultSet.getString(1);
            if(databaseName.equals(database)){
                encontrado= true;
            }
        }
        resultSet.close();
        
        DataBaseCreada=false;
        
        if (!encontrado)
        {
        	Statement s = conexion.createStatement();
        	s.executeUpdate("CREATE DATABASE "+database);
        	 DataBaseCreada=true;
        }
        conexion.close();
		conexion = DriverManager.getConnection(DBaseServerUnknow+database, user, password);

        
	}

	/**
	 * 
	 * @param database
	 * @param user
	 * @param password
	 * @throws SQLException
	 */
	private void InicializacionAnonima(String database, String user, String password) throws SQLException {
		String DBaseServerUnknow = "jdbc:mysql://a-note.fdi.ucm.es:3306/";
		conexion = DriverManager.getConnection(DBaseServerUnknow, user, password);	
		ResultSet resultSet = conexion.getMetaData().getCatalogs();

		boolean encontrado=false;
        while (resultSet.next()) {

          String databaseName = resultSet.getString(1);
            if(databaseName.equals(database)){
                encontrado= true;
            }
        }
        resultSet.close();
        
        DataBaseCreada=false;
        
        if (!encontrado)
        {
        	Statement s = conexion.createStatement();
        	s.executeUpdate("CREATE DATABASE "+database);
        	 DataBaseCreada=true;
        }
        conexion.close();
		conexion = DriverManager.getConnection(DBaseServerUnknow+database, user, password);
        
	}
	
	public static MySQLConnectionOdA2 getInstance(String dbNameIP,String database,int port, String user, String password) {
			instance=new MySQLConnectionOdA2(dbNameIP,database,port,user,password);
		return instance;	
	}
	
	public static MySQLConnectionOdA2 getInstance(String database,String user, String password) {
		instance=new MySQLConnectionOdA2(database,user,password);
	return instance;	
}
	
	public static int RunQuerryINSERT(String querry)
	{
		int risultato=-1;
		try {
			Statement st = instance.conexion.createStatement();
			st.executeUpdate(querry, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.getGeneratedKeys();
	        if (rs.next()){
	            risultato = rs.getInt(1);
	        }
		} catch (SQLException e) {
			System.err.println(ErrorInsert + querry);
			e.printStackTrace();
		}
		return risultato;
	}
	
	
	public static void RunQuerryUPDATE(String querry)
	{		
		try {
			Statement st = instance.conexion.createStatement();
			st.executeUpdate(querry);
		} catch (SQLException e) {
			System.err.println(ErrorUpdate + querry);
			e.printStackTrace();
		}
	}
	
	public static ResultSet RunQuerrySELECT(String querry)
	{		
		try {
			Statement st = instance.conexion.createStatement();
			ResultSet rs = st.executeQuery(querry);
			return rs;
		} catch (SQLException e) {
			System.err.println(ErrorSelect + querry);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Funcion que ejecuta una querry generica
	 * @param querry
	 */
	public static void RunQuerry(String querry)
	{
		try {
			Statement st = instance.conexion.createStatement();
			st.executeUpdate(querry);
		} catch (SQLException e) {
			System.err.println(ErrorUpdate + querry);
			e.printStackTrace();
		}
	}

	/**
	 * Chequea que la base de datos existe en el servidor
	 * @param dbNameIP
	 * @param database
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public static Boolean CheckDBS(String dbNameIP, String database, int port,
			String user, String password) {
	    try{
	        Class.forName(DriverDatabase); 
	        String DBaseServerUnknow = "jdbc:mysql://"+dbNameIP+":"+port+"/";
	        Connection conn = DriverManager.getConnection(DBaseServerUnknow, user, password); 

	        ResultSet resultSet = conn.getMetaData().getCatalogs();

	        while (resultSet.next()) {

	          String databaseName = resultSet.getString(1);
	            if(databaseName.equals(database)){
	                return true;
	            }
	        }
	        resultSet.close();

	    }
	    catch(Exception e){
	        e.printStackTrace();
	        throw new RuntimeException();
	        
	    }

	    return false;
	}
	
	/**
	 * Chequea que la base de datos existe en el servidor
	 * @param dbNameIP
	 * @param database
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public static Boolean CheckDBS(String database,String user, String password) {
	    try{
	        Class.forName(DriverDatabase); 
	        String DBaseServerUnknow = "jdbc:mysql://a-note.fdi.ucm.es:3306/";
	        Connection conn = DriverManager.getConnection(DBaseServerUnknow, user, password); 

	        ResultSet resultSet = conn.getMetaData().getCatalogs();

	        while (resultSet.next()) {

	          String databaseName = resultSet.getString(1);
	            if(databaseName.equals(database)){
	                return true;
	            }
	        }
	        resultSet.close();

	    }
	    catch(Exception e){
	        e.printStackTrace();
	        e.getCause().printStackTrace();
	        throw new RuntimeException();
	        
	    }

	    return false;
	}

	/**
	 * @return the dataBaseCreada
	 */
	public static boolean isDataBaseCreada() {
		return DataBaseCreada;
	}

	/**
	 * @param dataBaseCreada the dataBaseCreada to set
	 */
	public static void setDataBaseCreada(boolean dataBaseCreada) {
		DataBaseCreada = dataBaseCreada;
	}
	
	


}
