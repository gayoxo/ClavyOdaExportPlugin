/**
 * 
 */
package fdi.ucm.server.exportparser.oda2.remote;

//import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fdi.ucm.server.exportparser.oda2.MySQLConnectionOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2;
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
public class Oda2SaveRemoteCollection extends SaveCollection {

	private static final String ODA = "Oda 2.0 en servidor remoto (MySQL only)";
	private Boolean Merge;
	private ArrayList<ImportExportPair> Parametros;
	private boolean Create;
	private String Database;
	private String Path;
	private String FileIO;
	private static final int BUFFER = 2048;
	 
	/**
	 * Constructor por defecto
	 */
		public Oda2SaveRemoteCollection() {
	}

	/* (non-Javadoc)
	 * @see fdi.ucm.server.SaveCollection#processCollecccion(fdi.ucm.shared.model.collection.Collection)
	 */
	@Override
	public CompleteCollectionLog processCollecccion(CompleteCollection Salvar,
			String PathTemporalFiles) throws CompleteImportRuntimeException{
		try {
			Path=PathTemporalFiles;
			CompleteCollectionLog CL=new CompleteCollectionLog();
			SaveProcessMainOdA2 oda;
			oda = new SaveProcessMainOdA2Remote(Salvar,CL,Path);
			if (MySQLConnectionOdA2.isDataBaseCreada()||!Merge)
				SaveProcessMainOdA2.resetProfundoTablas();
			else
				SaveProcessMainOdA2.resetBasico();
			oda.preocess();	
			
			String A=Zip();
			
			FileIO=A;
			
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
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "Server"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Text, "Database"));
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.Number, "Port"));
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
			Database = RemoveSpecialCharacters(DateEntrada.get(1));
			boolean existe=MySQLConnectionOdA2.CheckDBS(DateEntrada.get(0),Database,Integer.parseInt(DateEntrada.get(2)),DateEntrada.get(3),DateEntrada.get(4));
			Merge=Boolean.parseBoolean(DateEntrada.get(5));
			Create=Boolean.parseBoolean(DateEntrada.get(6));
			if (!existe&&!Create)
				throw new CompleteImportRuntimeException("DDBB not exist and you do not select \"Create if not exist\" checkbox");
			else{
//				try 
//				{
//					if (!existe)	
//						OdaUnixLocalhost.SystemProcess(Database,DateEntrada.get(3),DateEntrada.get(4));
					
					MySQLConnectionOdA2.getInstance(DateEntrada.get(0),Database,Integer.parseInt(DateEntrada.get(2)),DateEntrada.get(3),DateEntrada.get(4));
				
//				} catch (IOException e) {
//					System.err.println(ErrorCreandoCarpeta);
//					e.printStackTrace();
//				}catch (InterruptedException e) {
//					System.err.println(ErrorDeProceso);
//					e.printStackTrace();
//				}
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


	public String Zip() {
		  
		String Salida = Path+System.currentTimeMillis()+".zip";
		ZipOutputStream out = null;      
		try {
		         BufferedInputStream origin = null;
		         FileOutputStream dest = new 
		           FileOutputStream(Salida);
		         CheckedOutputStream checksum = new 
		           CheckedOutputStream(dest, new Adler32());
		         out = new 
		           ZipOutputStream(new 
		             BufferedOutputStream(checksum));
		         //out.setMethod(ZipOutputStream.DEFLATED);
		         byte data[] = new byte[BUFFER];
		         // get a list of files from current directory
		         File f = new File(Path);
		         String files[] = f.list();

		         
		         addFilesToZip(files,out,Salida,origin,data,"");
//		         for (int i=0; i<files.length; i++) {
//		        	 
//		        	 String elem=Path+files[i];
//		        	 if (!elem.equals(Salida))
//		        	 {
//		        		 System.out.println("Adding: "+elem);
//			            
//			            
//			            
//			            FileInputStream fi = new 
//			              FileInputStream(elem);
//			            origin = new 
//			              BufferedInputStream(fi, BUFFER);
//			            ZipEntry entry = new ZipEntry(elem);
//			            out.putNextEntry(entry);
//			            int count;
//			            while((count = origin.read(data, 0, 
//			              BUFFER)) != -1) {
//			               out.write(data, 0, count);
//			            }
//			            origin.close();
//		        	 }
//		         }
		         out.close();
		         System.out.println("checksum: "+checksum.getChecksum().getValue());
		      } catch(Exception e) {
		         e.printStackTrace();
		         
		      } finally 
		      {
		    	  if (out!=null)
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	  
		      }
		      return Salida;
	}

	private void addFilesToZip(String[] files, ZipOutputStream out, String salida, BufferedInputStream origin, byte[] data,String RelativePath) throws IOException {
		 for (int i=0; i<files.length; i++) {
        	 
        	 String elem=Path+RelativePath+files[i];
        	 File F=new File(elem);
        	 if (!elem.equals(salida)&&!F.isDirectory())
        	 {
        		 System.out.println("Adding: "+elem);
	            
	            
	            
	            FileInputStream fi = new 
	              FileInputStream(elem);
	            origin = new 
	              BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(elem);
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	              BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	            origin.close();
        	 }
        	 else if (!elem.equals(salida)&&F.isDirectory())
        	 {
        		 String files2[] = F.list();
		         addFilesToZip(files2,out,salida,origin,data,files[i]+File.separator);
        	 }
        	 
         }
		
	} 
	
	
}
