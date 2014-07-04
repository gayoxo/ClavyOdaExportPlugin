package fdi.ucm.server.exportparser.oda2.localhost;

import java.io.IOException;

public class Oda2UnixLocalhost {

	public static void SystemProcess(String newName,String user,String pass) throws IOException, InterruptedException {
		String cmd = "cp -R -p /var/www/odaBase/ /var/www/"+newName;
		String php = "<?php " +
				"ini_set(\"register_globals\",\"1\"); " +
				"ini_set(\"magic_quotes\",\"0\"); " +
				"ini_set(\"display_errors\", \"0\"); " +
				"ini_set( 'default_charset', 'UTF-8' ); " +
				"error_reporting(E_ALL & ~(E_NOTICE | E_STRICT |E_DEPRECATED));"+
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
