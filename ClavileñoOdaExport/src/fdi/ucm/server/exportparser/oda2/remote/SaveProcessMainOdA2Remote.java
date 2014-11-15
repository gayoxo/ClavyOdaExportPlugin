package fdi.ucm.server.exportparser.oda2.remote;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import fdi.ucm.server.exportparser.oda2.MySQLConnectionOdA2;
import fdi.ucm.server.exportparser.oda2.SaveProcessMainOdA2;
import fdi.ucm.server.exportparser.oda2.StaticFuctionsOda2;
import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionLog;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteLinkElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;

/**
 * Clase que parsea una coleccion del sistema en el formato Oda.
 * @author Joaquin Gayoso-Cabada
 *
 */
public class SaveProcessMainOdA2Remote extends SaveProcessMainOdA2{
	
	
	private String TempPath;



	public SaveProcessMainOdA2Remote(CompleteCollection coleccion, CompleteCollectionLog cL, String pathTemporalFiles){
		super(coleccion, cL);
		TempPath=pathTemporalFiles;

	}


	/**
	 * Procesa un recurso sobre su Objeto Digital
	 * @param recursoAProcesar Recurso que sera procesado
	 * @param idov identificador del sueño del recurso.
	 * @param visibleValue2 
	 * @return 
	 * @throws ImportRuntimeException si el elemento no tiene un campo en su descripcion necesario.
	 */
	@Override
	protected int procesa_recursos(CompleteDocuments recursoAProcesar, Integer idov, boolean visibleValue2) throws CompleteImportRuntimeException {

		if (recursoAProcesar==null)
			return -1;
		
		boolean visBool=visibleValue2;
		String VisString;
		if (visBool) 
				VisString="S";
		else 
			VisString="N";
		
	
				CompleteDocuments recursoAProcesarC = (CompleteDocuments)recursoAProcesar;
				
				if (StaticFuctionsOda2.isAVirtualObject(recursoAProcesarC))
				{
					
					Integer Idov=tabla.get(recursoAProcesarC);
					if (Idov!=null)
						 {
						int Salida = MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `idov_refered`, `type`) VALUES ('"+idov+"', '"+VisString+"','N', '"+Idov+"','OV')");	
						return Salida;
						 }
					else
						ColectionLog.getLogLines().add("Link a objeto virtual: "+ recursoAProcesarC.getDescriptionText()+ "no existe en la lista de recursos, pero tiene un link, IGNORADO" );
				}
				else
					if (StaticFuctionsOda2.isAFile(recursoAProcesarC))
				{
						CompleteResourceElement FIleRel=StaticFuctionsOda2.findMetaValueFile(recursoAProcesarC.getDescription());
						CompleteLinkElement idovrefVal= StaticFuctionsOda2.findMetaValueIDOVowner(recursoAProcesarC.getDescription());
					
					
					if (idovrefVal!=null)
					{
						Integer Idov=tabla.get(idovrefVal.getValue());
					
						
						if  (FIleRel!=null && Idov!=null && (FIleRel instanceof CompleteResourceElementFile))
							{
								CompleteFile Icon=Iconos.get(idov);
								String iconoov;
								if (Icon!=null && Icon.getPath().equals((((CompleteResourceElementFile)FIleRel).getValue()).getPath()))
									iconoov="S";
								else iconoov="N";
								String[] spliteStri=(((CompleteResourceElementFile)FIleRel).getValue()).getPath().split("/");
								String NameS = spliteStri[spliteStri.length-1];
								String[] ext = NameS.split(".");
								String extension="jpg";
								if (ext.length>0)
								 extension= ext[ext.length-1];
								
								
								if (Idov==idov)
									{
									
									 String Urls=TempPath+Idov+"/";
									 File DestinoD=new File(Urls); 
									 DestinoD.mkdirs();
									 Urls=Urls+NameS;
									 File DestinoF=new File(Urls); 
									 DestinoF.delete();
									 try {			 
										 URL url = new URL((((CompleteResourceElementFile)FIleRel).getValue()).getPath());
										 URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
										 url = uri.toURL();
										saveImage(url, Urls);
									} catch (MalformedURLException e) {
										ColectionLog.getLogLines().add("URL erronea " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urls);
										
										e.printStackTrace();
									} catch (IOException e) {
										ColectionLog.getLogLines().add("Problema I/O  " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urls);
										e.printStackTrace();
									} catch (URISyntaxException e) {
										ColectionLog.getLogLines().add("URI erronea " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urls);
										
										e.printStackTrace();
									}
									
									 
									 if (iconoov.equals("S"))
									 {
										 String Urlsi=TempPath+"iconos/";
										 File DestinoDi=new File(Urlsi); 
										 DestinoDi.mkdirs();
										 Urlsi=Urlsi+Idov+"."+extension;
										 File DestinoFi=new File(Urlsi); 
										 DestinoFi.delete();
										 try {
											 URL url2 = new URL((((CompleteResourceElementFile)FIleRel).getValue()).getPath());
											 URI uri2 = new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), url2.getRef());
											 url2 = uri2.toURL();
											saveImage(url2, Urlsi);
										} catch (MalformedURLException e) {
											ColectionLog.getLogLines().add("URL erronea en icono " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urlsi);
											e.printStackTrace();
										} catch (IOException e) {
											ColectionLog.getLogLines().add("Problema I/O  " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urlsi);
											e.printStackTrace();
										} catch (URISyntaxException e) {
											ColectionLog.getLogLines().add("URI erronea " + (((CompleteResourceElementFile)FIleRel).getValue()).getPath() + " a " + Urls);									
											e.printStackTrace();
										}
									 }
									 
									int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `name`, `type`) VALUES ('"+idov+"', '"+VisString+"','"+iconoov+"', '"+NameS+"', 'P' )");
									return Salida;
									
									}
								else
									{
									int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`, `name`,`idresource_refered`, `type`) VALUES ('"+idov+"', '"+VisString+"', '"+NameS+"', '"+Idov+"','F')");
									return Salida;
									}
							}
						else ColectionLog.getLogLines().add("EL file referencia es nulo, o no es un file o el dueño no es un Objeto virtual valido con identificadorArchivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO");
					}
					else ColectionLog.getLogLines().add("El objeto dueño del archivo es nulo o no Objeto Virtual, Archivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO ");
				}
					else
						if (StaticFuctionsOda2.isAURL(recursoAProcesarC))
						{
							//public static final String URI = "URI";
							CompleteResourceElementURL UniFile=StaticFuctionsOda2.findMetaValueUri(recursoAProcesarC.getDescription());

							
								
								if  (UniFile!=null&&!UniFile.getValue().isEmpty())
									{

											int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `name`, `type`) VALUES ('"+idov+"', '"+VisString+"','N', '"+UniFile.getValue()+"', 'U' )");
											return Salida;


									}
								else ColectionLog.getLogLines().add("El URI referencia es nulo, o vacio identificadorArchivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO");

						}
				return -1;
		
	}



	/**
	 * Salva una imagen dado un destino
	 * @param imageUrl
	 * @param destinationFile
	 * @throws IOException
	 */
	private void saveImage(URL imageUrl, String destinationFile) throws IOException {

		URL url = imageUrl;
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}

}
