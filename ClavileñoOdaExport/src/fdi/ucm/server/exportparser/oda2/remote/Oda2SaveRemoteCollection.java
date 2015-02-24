/**
 * 
 */
package fdi.ucm.server.exportparser.oda2.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fdi.ucm.server.exportparser.oda2.MySQLConnectionOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2NoOverwrite;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2Overwrite;
import fdi.ucm.server.modelComplete.ImportExportDataEnum;
import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;
import fdi.ucm.server.modelComplete.SaveCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteLogAndUpdates;

/**
 * Clase que impementa el plugin de oda para Localhost
 * @author Joaquin Gayoso-Cabada
 *
 */
public class Oda2SaveRemoteCollection extends SaveCollection {

	private static final String ODA = "Oda 2.0 en servidor remoto (MySQL only)";
	private Boolean KeepConfig;
	private ArrayList<ImportExportPair> Parametros;
	private boolean Create;
	private String Database;
	private String Path;
	private String FileIO;
//	private static final int BUFFER = 2048;
	private List<String> fileList; 
	private String OUTPUT_ZIP_FILE = "";
	private String SOURCE_FOLDER = ""; // SourceFolder path
	private boolean Overwrite;
	private boolean Return;
	
	/**
	 * Constructor por defecto
	 */
		public Oda2SaveRemoteCollection() {
	}

	/* (non-Javadoc)
	 * @see fdi.ucm.server.SaveCollection#processCollecccion(fdi.ucm.shared.model.collection.Collection)
	 */
	@Override
	public CompleteLogAndUpdates processCollecccion(CompleteCollection Salvar,
			String PathTemporalFiles) throws CompleteImportRuntimeException{
		try {
			Path=PathTemporalFiles;
			SOURCE_FOLDER=Path+"Oda"+File.separator;
			File Dir=new File(SOURCE_FOLDER);
			Dir.mkdirs();
			
			CompleteLogAndUpdates CL=new CompleteLogAndUpdates();
			
			SaveProcessMainOdA2 oda;
			
			if (Overwrite)
			{
			
			
			oda = new SaveProcessMainOdA2Overwrite(Salvar,CL,SOURCE_FOLDER,Return);
			if (MySQLConnectionOdA2.isDataBaseCreada()||!KeepConfig)
				SaveProcessMainOdA2Overwrite.resetProfundoTablas();
			else
				SaveProcessMainOdA2Overwrite.resetBasico();
			
			}
			else
		{
				
			oda = new SaveProcessMainOdA2NoOverwrite(Salvar,CL,SOURCE_FOLDER,Return);
			if (MySQLConnectionOdA2.isDataBaseCreada())
				SaveProcessMainOdA2Overwrite.resetProfundoTablas();
				
				
		}
			
			oda.preocess();
			
			
			
			
			
			fileList = new ArrayList<String>();
			OUTPUT_ZIP_FILE = Path+System.currentTimeMillis()+".zip";
			FileIO=OUTPUT_ZIP_FILE;
			
			generateFileList(new File(SOURCE_FOLDER));
			try {
				zipIt(OUTPUT_ZIP_FILE);
				CL.getLogLines().add("Descarga el zip y inserta en la localizacion base de oda \"<BaseOda>/bo/download\" ");
			} catch (Exception e) {
				e.printStackTrace();
				CL.getLogLines().add("Error en zip, refresh images manually");
			}
			
			
			
//			String A=Zip();
			
			
			
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
	private boolean hasSpecialChar(String str) {
		for (int i = 0; i < str.length(); i++) {
			   char c = str.charAt(i);
			   if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_')) {
			         return true;
			      }
				   
		}
		return false;
	}

	


	@Override
	public ArrayList<ImportExportPair> getConfiguracion() {
		if (Parametros==null)
		{
			ArrayList<ImportExportPair> ListaCampos=new ArrayList<ImportExportPair>();
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "MySQL Server Direction"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "MySQL Oda Destiny Database"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Number, "MySQL Port"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "MySQL User"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.EncriptedText, "MySQL Password"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Keep collection details if exist (Keep Oda Configuration, only affects overwrite option true)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Create if not exist (Create a new database and generate structure by zero)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Overwrite Documents and Grammar (Delete and generate everything)"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Boolean, "Return Documents Ids"));
			Parametros=ListaCampos;
			return ListaCampos;
		}
		else return Parametros;
	}

	@Override
	public void setConfiguracion(ArrayList<String> DateEntrada) {
		if (DateEntrada!=null)
		{
			
			Database=DateEntrada.get(1);
			if (hasSpecialChar(Database))
					throw new CompleteImportRuntimeException("DDBB Name errors, can not accept this name, please use only compatible characters");
			
			boolean existe=MySQLConnectionOdA2.CheckDBS(DateEntrada.get(0),Database,Integer.parseInt(DateEntrada.get(2)),DateEntrada.get(3),DateEntrada.get(4));
			KeepConfig=Boolean.parseBoolean(DateEntrada.get(5));
			Create=Boolean.parseBoolean(DateEntrada.get(6));
			Overwrite=Boolean.parseBoolean(DateEntrada.get(7));
			Return=Boolean.parseBoolean(DateEntrada.get(8));
			if (!existe&&!Create)
				throw new CompleteImportRuntimeException("DDBB not exist and you do not select \"Create if not exist\" checkbox");
			else{
					
					MySQLConnectionOdA2.getInstance(DateEntrada.get(0),Database,Integer.parseInt(DateEntrada.get(2)),DateEntrada.get(3),DateEntrada.get(4));
				

			}
		}
		}
		

	@Override
	public String getName() {
		return ODA;
	}

	@Override
	public boolean isFileOutput() {
		return true;
	}

	@Override
	public String FileOutput() {
		return FileIO;
	}

	@Override
	public void SetlocalTemporalFolder(String TemporalPath) {
		Path=TemporalPath;
		
	}



	
	
	public void zipIt(String zipFile)
	{
	   byte[] buffer = new byte[1024];
	   String source = "";
	   FileOutputStream fos = null;
	   ZipOutputStream zos = null;
	   try
	   {

	     fos = new FileOutputStream(zipFile);
	     zos = new ZipOutputStream(fos);

	     System.out.println("Output to Zip : " + zipFile);
	     FileInputStream in = null;

	     for (String file : this.fileList)
	     {
	        System.out.println("File Added : " + file);
	        ZipEntry ze = new ZipEntry(source + File.separator + file);
	        zos.putNextEntry(ze);
	        try
	        {
	           in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
	           int len;
	           while ((len = in.read(buffer)) > 0)
	           {
	              zos.write(buffer, 0, len);
	           }
	        }
	        finally
	        {
	           in.close();
	        }
	     }

	     zos.closeEntry();
	     System.out.println("Folder successfully compressed");

	  }
	  catch (IOException ex)
	  {
	     ex.printStackTrace();
	  }
	  finally
	  {
	     try
	     {
	        zos.close();
	     }
	     catch (IOException e)
	     {
	        e.printStackTrace();
	     }
	  }
	}

	public void generateFileList(File node)
	{

	  // add file only
	  if (node.isFile())
	  {
	     fileList.add(generateZipEntry(node.toString()));

	  }

	  if (node.isDirectory())
	  {
	     String[] subNote = node.list();
	     for (String filename : subNote)
	     {
	        generateFileList(new File(node, filename));
	     }
	  }
	}

	private String generateZipEntry(String file)
	{
	   return file.substring(SOURCE_FOLDER.length()-1, file.length());
	}
	
}
