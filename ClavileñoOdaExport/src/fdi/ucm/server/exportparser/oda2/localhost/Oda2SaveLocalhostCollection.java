/**
 * 
 */
package fdi.ucm.server.exportparser.oda2.localhost;

import java.io.IOException;
import java.util.ArrayList;

import fdi.ucm.server.exportparser.oda2.MySQLConnectionOdA2;
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
	private Boolean Merge;
	private ArrayList<ImportExportPair> Parametros;
	private boolean Create;
	private String Database;

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
			SaveProcessMainOdA2Local oda;
			oda = new SaveProcessMainOdA2Local(Salvar,CL,Database);
			if (MySQLConnectionOdA2.isDataBaseCreada()||!Merge)
				SaveProcessMainOdA2Local.resetProfundoTablas();
			else
				SaveProcessMainOdA2Local.resetBasico();
				
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
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "Database"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "User"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.EncriptedText, "Password"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Merge if exist"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Create if not exist"));
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
			Merge=Boolean.parseBoolean(DateEntrada.get(3));
			Create=Boolean.parseBoolean(DateEntrada.get(4));
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

	

	
}
