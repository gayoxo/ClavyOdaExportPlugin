/**
 * 
 */
package fdi.ucm.server.exportparser.oda2.localhost;

import java.io.IOException;
import java.util.ArrayList;

import fdi.ucm.server.exportparser.oda2.MySQLConnectionOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2NoOverwrite;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2Overwrite;
import fdi.ucm.server.modelComplete.ImportExportDataEnum;
import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;
import fdi.ucm.server.modelComplete.SaveCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionLog;

/**
 * Clase que impementa el plugin de oda para Localhost
 * @author Joaquin Gayoso-Cabada
 *
 */
public class Oda2SaveLocalhostCollection extends SaveCollection {

	private static final String ODA = "Oda 2.0 en Servidor Local (copia completa)";
	private static final String ErrorCreandoCarpeta = "Error creando carpeta";
	private static final String ErrorDeProceso = "Error creando proceso de crear carpeta";
	private Boolean KeepConfig;
	private ArrayList<ImportExportPair> Parametros;
	private boolean Create;
	private String Database;
	private boolean Overwrite;

	/**
	 * Constructor por defecto
	 */
		public Oda2SaveLocalhostCollection() {
	}

	/* (non-Javadoc)
	 * @see fdi.ucm.server.SaveCollection#processCollecccion(fdi.ucm.shared.model.collection.Collection)
	 */
	@Override
	public CompleteCollectionLog processCollecccion(CompleteCollection Salvar,
			String PathTemporalFiles) throws CompleteImportRuntimeException{
		try {
			
			CompleteCollectionLog CL=new CompleteCollectionLog();
			SaveProcessMainOdA2 oda;
			
			if (Overwrite)
			{
			
			
			oda = new SaveProcessMainOdA2Overwrite(Salvar,CL,"/var/www/"+Database+"/bo/download/");
			if (MySQLConnectionOdA2.isDataBaseCreada()||!KeepConfig)
				SaveProcessMainOdA2Overwrite.resetProfundoTablas();
			else
				SaveProcessMainOdA2Overwrite.resetBasico();
			
			}
			else
		{
				
			oda = new SaveProcessMainOdA2NoOverwrite(Salvar,CL,"/var/www/"+Database+"/bo/download/");
			if (MySQLConnectionOdA2.isDataBaseCreada())
				SaveProcessMainOdA2Overwrite.resetProfundoTablas();
				
				
		}
			
			oda.preocess();
				
			CL.getLogLines().add("URL de la coleccion : http://a-note.fdi.ucm.es:10000/"+Database);
			return CL;

		} catch (CompleteImportRuntimeException e) {
			System.err.println("Exception OdaException " +e.getGENERIC_ERROR());
			e.printStackTrace();
			throw e;
		}
		
	}

	/**
	 * QUitar caracteres especiales.
	 * @param str texto de entrada.
	 * @return texto sin caracteres especiales.
	 */
	public String RemoveSpecialCharacters(String str) {
		   StringBuilder sb = new StringBuilder();
		   for (int i = 0; i < str.length(); i++) {
			   char c = str.charAt(i);
			   if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_') {
			         sb.append(c);
			      }
		}
		   return sb.toString();
		}

	


	@Override
	public ArrayList<ImportExportPair> getConfiguracion() {
		if (Parametros==null)
		{
			ArrayList<ImportExportPair> ListaCampos=new ArrayList<ImportExportPair>();
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "Name of Oda Collection (Used as name of database)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "User"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.EncriptedText, "Password"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Keep collection details if exist (Keep Oda Configuration, only for overwrite option true)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Create if not exist (Create a new database and generate structure by zero)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Overwrite Documents and Grammar (Delete and generate everything)"));
			Parametros=ListaCampos;
			return ListaCampos;
		}
		else return Parametros;
	}

	@Override
	public void setConfiguracion(ArrayList<String> DateEntrada) {
		if (DateEntrada!=null)
		{
			Database = RemoveSpecialCharacters(DateEntrada.get(0));
			boolean existe=MySQLConnectionOdA2.CheckDBS(Database,DateEntrada.get(1),DateEntrada.get(2));
			KeepConfig=Boolean.parseBoolean(DateEntrada.get(3));
			Create=Boolean.parseBoolean(DateEntrada.get(4));
			Overwrite=Boolean.parseBoolean(DateEntrada.get(5));
			if (!existe&&!Create)
				throw new CompleteImportRuntimeException("DDBB not exist and you do not select \"Create if not exist\" checkbox");
			else{
				try 
				{
					if (!existe)	
						Oda2UnixLocalhost.SystemProcess(Database,DateEntrada.get(1),DateEntrada.get(2));
					
					MySQLConnectionOdA2.getInstance(Database,DateEntrada.get(1),DateEntrada.get(2));
				
				} catch (IOException e) {
					System.err.println(ErrorCreandoCarpeta);
					e.printStackTrace();
				}catch (InterruptedException e) {
					System.err.println(ErrorDeProceso);
					e.printStackTrace();
				}
			}
		}
		}
		

	@Override
	public String getName() {
		return ODA;
	}


	@Override
	public boolean isFileOutput() {
		return false;
	}

	@Override
	public String FileOutput() {
		return "";
	}

	@Override
	public void SetlocalTemporalFolder(String TemporalPath) {
		
	}

	

	
}
