package fdi.ucm.server.exportparser.oda2.localhost;

import java.io.IOException;

public class Oda2UnixLocalhost {

	public static void SystemProcess(String newName,String user,String pass) throws IOException, InterruptedException {
		String cmd = "cp -R -p /var/www/odaBase/ /var/www/"+newName;
		String php = "<?php " +
				"session_start(); " +
				"if(!ini_get('register_globals')){ " +
				  "$superglobales=array($_ENV,$_GET,$_POST,$_COOKIE,$_SERVER,$_FILES); " +
		          "if(isset($_SESSION)){ "+
		             "array_unshift($superglobales,$_SESSION); "+
		             "}"+
		          "foreach($superglobales as $superglobal){"+
		          	"extract($superglobal,EXTR_SKIP);"+
		          	"}"+
		          "}"+
		        "ini_set(\"magic_quotes\",\"0\")"+
				"ini_set(\"display_errors\", \"0\"); " +
				"ini_set( 'default_charset', 'UTF-8' ); " +
				"ini_set('session.auto_start','1'); "+
				"define('TZN_DB_HOST','localhost'); "  +
				"define('TZN_DB_USER','"+user+"'); " + 
				"define('TZN_DB_PASS','"+pass+"'); " +          
				"define('TZN_DB_BASE','"+newName+"'); "+         
				"define('TZN_DB_CLASS','tzn_mysql.php'); "+
				"?>"+
				"<?php "+
				"define('APP_NAME','"+newName+"'); "   +
				"?>";
	 	Process p = Runtime.getRuntime().exec(new String[] {"sh",  "-c", cmd});
		p.waitFor();
		
		String cmd2 = "echo \""+php+"\" > "+"/var/www/"+newName+"/config.php";
		System.err.println(cmd2);
		Process p2 = Runtime.getRuntime().exec(new String[] {"sh",  "-c", cmd2});
		p2.waitFor();
		
		
		
	}
}
