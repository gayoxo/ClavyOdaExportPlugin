package fdi.ucm.server.exportparser.oda2;



import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteLogAndUpdates;

/**
 * Clase que parsea una coleccion del sistema en el formato Oda.
 * @author Joaquin Gayoso-Cabada
 *
 */
public class SaveProcessMainOdA2Overwrite extends SaveProcessMainOdA2{
	
	
	public SaveProcessMainOdA2Overwrite(CompleteCollection coleccion, CompleteLogAndUpdates cL, String pathGeneral,boolean ReturnsIds){
		super(coleccion, cL,pathGeneral,ReturnsIds);
	}


	

}
