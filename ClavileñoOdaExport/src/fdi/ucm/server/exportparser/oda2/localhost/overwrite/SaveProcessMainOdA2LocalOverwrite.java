package fdi.ucm.server.exportparser.oda2.localhost.overwrite;



import fdi.ucm.server.exportparser.oda2.localhost.SaveProcessMainOdA2Local;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionLog;

/**
 * Clase que parsea una coleccion del sistema en el formato Oda.
 * @author Joaquin Gayoso-Cabada
 *
 */
public class SaveProcessMainOdA2LocalOverwrite extends SaveProcessMainOdA2Local{
	
	
	public SaveProcessMainOdA2LocalOverwrite(CompleteCollection coleccion, CompleteCollectionLog cL, String database){
		super(coleccion, cL,database);
	}


	

}
