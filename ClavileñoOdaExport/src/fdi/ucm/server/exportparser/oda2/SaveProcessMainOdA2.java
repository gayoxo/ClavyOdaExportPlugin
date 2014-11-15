package fdi.ucm.server.exportparser.oda2;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionLog;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteLinkElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteIterator;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteLinkElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalView;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteResourceElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteStructure;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * Clase que parsea una coleccion del sistema en el formato Oda.
 * @author Joaquin Gayoso-Cabada
 *
 */
public abstract class SaveProcessMainOdA2 {
	

	
	protected static final String ERROR_DE_CREACION_POR_FALTA_DE_OV_DUEÑO_EN_LOS_DOCUMENTOS_FILE = "Error de creacion por falta de OV dueño en los documentos File";
	protected static final String ERROR_DE_CREACION_POR_FALTA_DE_FILE_FISICO_EN_LOS_DOCUMENTOS_FILE = "Error de creacion por falta de File Fisico en los documentos File";
	protected static final String EXISTE_ERROR_EN_EL_PARSEADO_DE_LAS_ITERACIONES = "Existe error en el parseado de las iteraciones.";
	protected static final String ERROR_DE_CREACION_POR_FALTA_DE_META_OBJETO_VIRTUAL = "Error de creacion por falta de Meta Objeto virtual.";
	protected CompleteCollection toOda;
	protected HashMap<CompleteElementType, Integer> ModeloOda;
	protected HashSet<Integer> Vocabularios;
	protected HashMap<Integer,Integer> VocabulariosSalida;
	protected HashMap<Integer,CompleteFile>  Iconos;
	protected HashMap<CompleteDocuments, Integer> tabla;
	protected CompleteCollectionLog ColectionLog;

	/**
	 * Constructor por defecto
	 * @param cL 
	 * @param Coleccion coleccion a insertar en oda.
	 */
	public SaveProcessMainOdA2(CompleteCollection coleccion, CompleteCollectionLog cL){
		toOda=coleccion;
		ModeloOda=new HashMap<CompleteElementType, Integer>();
		Vocabularios=new HashSet<Integer>();
		Iconos=new HashMap<Integer, CompleteFile>();
		VocabulariosSalida=new HashMap<Integer, Integer>();
		tabla=new HashMap<CompleteDocuments, Integer>();
		ColectionLog=cL;
	}

	

	/**
	 * Procesa la clase
	 * @throws ImportRuntimeException en caso de errores varios. Consultar el error en {@link CompleteImportRuntimeException}
	 */
	public void preocess() throws CompleteImportRuntimeException {
		
		ProcessValidacion();

		
		ArrayList<CompleteStructure> DatosYMeta=new ArrayList<CompleteStructure>();
		
		
		ArrayList<CompleteElementType> MetaDatos=findMetaDatos();
		if (MetaDatos!=null)
			DatosYMeta.addAll(MetaDatos);
		
		
		processModeloIniciales(DatosYMeta);	
		
		processOV(toOda.getEstructuras());
		
	}


	/**
	 * Busca un meta con el texto VirtualObject
	 * @return the Meta
	 */
	private boolean findVOM() throws CompleteImportRuntimeException {
		for (CompleteGrammar iterable_element : toOda.getMetamodelGrammar()) {
				 if (StaticFuctionsOda2.isVirtualObject(iterable_element))
					 return true;

		}
		return false;
	}
	

	/**
	 * Busca un meta con el texto File
	 * @return the Meta
	 */
	private boolean findFile() {
		for (CompleteGrammar iterable_element : toOda.getMetamodelGrammar()) {
				if (StaticFuctionsOda2.isFile(iterable_element))
					 return true;
		}
		return false;
	}
	
	

//	/**
//	 * Busca un meta con el IDOV dentro de un meta
//	 * @param modeloObjetos
//	 * @return
//	 */
//	private boolean findIDOV() {
//		boolean NoError=false;
//		for (CompleteGrammar iterable_element : toOda.getMetamodelGrammar()) {
//			if (StaticFuctionsOda2.isVirtualObject(iterable_element))
//				 	 if (!findIDOVINOV( iterable_element))
//				 		 return false;
//				 	 else NoError=true;
//				}
//		return NoError;
//	}
	
//	/**
//	 * Busca el idov interno que da sentido al sistema
//	 * @param padre
//	 * @return
//	 */
//	private boolean findIDOVINOV(CompleteGrammar padre) {
//		for (CompleteStructure iterable_element2 : padre.getSons()) {
//			if (iterable_element2 instanceof CompleteElementType)
//			{
//			 if (StaticFuctionsOda2.isIDOV(((CompleteElementType) iterable_element2)))
//				 return true;
//			}
//		}
//		return false;
//	}


	/**
	 * Busca un File fisico dentro del File
	 * @return
	 */
	private boolean findFileFisico() {
		boolean NoError=false;
		for (CompleteGrammar iterable_element : toOda.getMetamodelGrammar()) {
				if (StaticFuctionsOda2.isFile(iterable_element))
					 {
					if (!findfindFileFisicoINFile(iterable_element))
				 		 return false;
				 	 else NoError=true;
					 }
		}
		return NoError;
	}
	
	
	/**
	 * Busca un File Dentro de los Meta File
	 * @param padre
	 * @return
	 */
	private boolean findfindFileFisicoINFile(CompleteGrammar padre) {
		for (CompleteStructure iterable_element2 : padre.getSons()) {
			if (iterable_element2 instanceof CompleteElementType)
			{
			 if (StaticFuctionsOda2.isFileFisico(((CompleteElementType) iterable_element2)))
				 return true;
			}
		}
		return false;
	}
	
	private boolean findFileOwner() {
		boolean NoError=false;
		for (CompleteGrammar iterable_element : toOda.getMetamodelGrammar()) {
				if (StaticFuctionsOda2.isFile( iterable_element))
					 {
					if (!findfindOwnerINFile( iterable_element))
				 		 return false;
				 	 else NoError=true;
					 }
		}
		return NoError;
	}


	private boolean findfindOwnerINFile(CompleteGrammar padre) {
		for (CompleteStructure iterable_element2 : padre.getSons()) {
			if (iterable_element2 instanceof CompleteElementType)
			{
			 if (StaticFuctionsOda2.isOwner(((CompleteElementType) iterable_element2)))
				 return true;
			}
		}
		return false;
	}


	/**
	 * Funcion que realiza una validacion basica del sistema
	 */
	private void ProcessValidacion() {
		if (!findVOM())
			{
			ColectionLog.getLogLines().add(ERROR_DE_CREACION_POR_FALTA_DE_META_OBJETO_VIRTUAL);
			throw new CompleteImportRuntimeException(ERROR_DE_CREACION_POR_FALTA_DE_META_OBJETO_VIRTUAL);
			}
		
		if (findFile())
		{
			if (!findFileFisico())
				{
				ColectionLog.getLogLines().add(ERROR_DE_CREACION_POR_FALTA_DE_FILE_FISICO_EN_LOS_DOCUMENTOS_FILE);
				throw new CompleteImportRuntimeException(ERROR_DE_CREACION_POR_FALTA_DE_FILE_FISICO_EN_LOS_DOCUMENTOS_FILE);
				}
			
			if (!findFileOwner())
				{
				ColectionLog.getLogLines().add(ERROR_DE_CREACION_POR_FALTA_DE_OV_DUEÑO_EN_LOS_DOCUMENTOS_FILE);
				throw new CompleteImportRuntimeException(ERROR_DE_CREACION_POR_FALTA_DE_OV_DUEÑO_EN_LOS_DOCUMENTOS_FILE);
				}
		}
		
		
	}
	


	


	

	/**
	 * Busca los metadatos
	 * @return Lista de Metadatos
	 */
	private ArrayList<CompleteElementType> findMetaDatos() {
		ArrayList<CompleteElementType> Salida=new ArrayList<CompleteElementType>();
		for (CompleteGrammar meta : toOda.getMetamodelGrammar()) {
				if (StaticFuctionsOda2.isVirtualObject(meta))
					Salida.addAll(findMetaDatosInVO(meta));
		}
		return Salida;

	}


	/**
	 * Busca los Metadatos y los Datos dentro del VO
	 * @param elementType
	 * @return
	 */
	private ArrayList<CompleteElementType> findMetaDatosInVO(CompleteGrammar elementType) {
		ArrayList<CompleteElementType> Salida=new ArrayList<CompleteElementType>();
		for (CompleteStructure iterable_element : elementType.getSons()) {
			if (iterable_element instanceof CompleteElementType)
				{
				if (StaticFuctionsOda2.isMetaDatos((CompleteElementType) iterable_element))
					Salida.add((CompleteElementType) iterable_element);
				else if (StaticFuctionsOda2.isDatos((CompleteElementType) iterable_element))
					Salida.add((CompleteElementType) iterable_element);
				}
			else if (iterable_element instanceof CompleteIterator)
			{
			for (CompleteStructure completeElementType : iterable_element.getSons()) {
				
				if ((completeElementType instanceof CompleteElementType)&&(StaticFuctionsOda2.isResources((CompleteElementType) completeElementType)))
					Salida.add((CompleteElementType) completeElementType);
			}
			
			}
					
		}
		return Salida;
	}



	/**
	 * Procesa el modelo para las raices del modelo.
	 * @param list raices del modelo de entrada.
	 * @throws ImportRuntimeException error si esta mal introducido el modelo en la funcion 
	 */
	private void processModeloIniciales(List<CompleteStructure> list) throws CompleteImportRuntimeException {
		list=CleanRepeticiones(list,new ArrayList<Integer>());
		rellenaTablaVocabularios(list);
		for (CompleteStructure Cattribute : list) {

				CompleteElementType attribute=(CompleteElementType) Cattribute;
			int Salida=3;
			if (StaticFuctionsOda2.isMetaDatos(attribute))
				{
					Salida=2;
					ModeloOda.put(attribute, Salida);
					processModelo(attribute.getSons(),Salida);
				}
			else if (StaticFuctionsOda2.isDatos(attribute))
				{
					Salida=1;
					ModeloOda.put(attribute, Salida);
					processModelo(attribute.getSons(),Salida);
				}
			else if (StaticFuctionsOda2.isResources(attribute))
			{
				Salida=3;
				ModeloOda.put(attribute, Salida);
				processModelo(attribute.getSons(),Salida);
			}
				

		}
	}

	/**
	 * Limpia de iteraciones la lista y las sustituye por meta, realiza el mismo proceso dentro de los atributos, quitando el contexto.
	 * @param list lista a limpiar
	 * @return Lista limpia de elementos
	 */
	private ArrayList<CompleteStructure> CleanRepeticiones(List<CompleteStructure> list,ArrayList<Integer> Ambitos) {
		ArrayList<CompleteStructure> Salida=new ArrayList<CompleteStructure>();
		for (CompleteStructure collectionAttribute : list) {
			if (collectionAttribute instanceof CompleteIterator)
			{
				ArrayList<CompleteStructure> Hermanos=processIteracion((CompleteIterator)collectionAttribute,Ambitos);
				for (CompleteStructure collectionAttribute2 : Hermanos) {
					Salida.add(collectionAttribute2);
				}
			}else
				{
				Salida.add(collectionAttribute);
				ArrayList<CompleteStructure> Entrada=CleanRepeticiones(collectionAttribute.getSons(),Ambitos);
				collectionAttribute.setSons(Entrada);
				}
				
		}
		return Salida;
	}


	/**
	 * Procesa una iteracion duplicando los elementos necesarios
	 * @param iteracionElement elemento iterador a procesar
	 * @param profundidad profundidad actual de ambito
	 * @param ambitos ambitos ya procesados en los que me encuentro
	 * @return lista de elementios duplicados
	 */
	private ArrayList<CompleteStructure> processIteracion(
			CompleteIterator iteracionElement,ArrayList<Integer> element) {
		ArrayList<CompleteStructure> Salida=new ArrayList<CompleteStructure>();
		Integer Repetir =iteracionElement.getAmbitoSTotales();
		for (CompleteStructure collectionAttribute : iteracionElement.getSons()) {
			Integer A;
			for (int i = 1; i <= Repetir; i++) {
				A=new Integer(i);
				element.add(A);
				if (collectionAttribute instanceof CompleteElementType)
					{
						CompleteElementType nuevo=CloneEle((CompleteElementType)collectionAttribute,element);
						nuevo.setFather(iteracionElement.getFather());
						if (
							(collectionAttribute instanceof CompleteLinkElementType)||
							(collectionAttribute instanceof CompleteResourceElementType)||
							(collectionAttribute instanceof CompleteTextElementType)
							)
						{
							
							replaceRefe(element,nuevo,(CompleteElementType) collectionAttribute);
						}
						Salida.add(nuevo);
						ArrayList<CompleteStructure> salidarec = CleanRepeticiones(nuevo.getSons(),element);
						nuevo.setSons(salidarec);
					}
				else 
					{
					CompleteIterator nuevo=new CompleteIterator();
					nuevo.setFather(iteracionElement.getFather());
					for (CompleteStructure iterable_element : collectionAttribute.getSons()) {
						CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
						nuevo.getSons().add(cloneEle);
						if (cloneEle instanceof CompleteElementType)
							replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType)iterable_element);
					}
					ArrayList<CompleteStructure> salidarec = processIteracion(nuevo, element);
					for (CompleteStructure collectionAttribute2 : salidarec) {
						Salida.add(collectionAttribute2);
					}
					}
				element.remove(A);
			}
		}
		
		return Salida;
	}


	/**
	 * REmplaza la referencia antigua por una nueva
	 * @param element lista de el ambito actual	
	 * @param nuevo nuevo elemento
	 * @param collectionAttribute viejo elemento
	 */
	private void replaceRefe(ArrayList<Integer> element, CompleteElementType nuevo,
			CompleteElementType collectionAttribute) {
		for (CompleteDocuments section : toOda.getEstructuras()) {
			for (CompleteElement meta : section.getDescription()) {
				if (meta.getHastype()==collectionAttribute&&compareAmbitos(meta.getAmbitos(),element))
					meta.setHastype(nuevo);
			}
		}
		
	}


	/**
	 * Compara si el elemento esta dentro del ambito.
	 * @param ambitos ambito del objeto.
	 * @param element ambito actual.
	 * @return si esta dentro del ambito.
	 */
	private boolean compareAmbitos(ArrayList<Integer> ambitos,
			ArrayList<Integer> element) {
		int itera=0;
		while (itera<element.size()) {
			if ((itera>=ambitos.size())||(ambitos.get(itera).intValue()!=element.get(itera)))
				return false;
			itera++;
		}
		return true;
	}


	/**
	 * Clona el elemento atribuido
	 * @param collectionAttribute elemento a colar
	 * @param element 
	 * @return nuevo elemento
	 */
	private CompleteElementType CloneEle(CompleteElementType collectionAttribute, ArrayList<Integer> element) {
		CompleteElementType nuevo=null;
//		if (collectionAttribute instanceof MetaControlled)
//				{
//				nuevo=new MetaControlled(((MetaControlled) collectionAttribute).getName(),collectionAttribute.getFather());
//				for (OperationalView nuevoShow : collectionAttribute.getShows()) {
//					OperationalView NuevoShownClonado=nuevoShow;
//					((ElementType)nuevo).getShows().add(NuevoShownClonado);
//				}
//				for (Structure iterable_element : collectionAttribute.getSons()) {
//
//					Structure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
//					nuevo.getSons().add(cloneEle);
//					if (cloneEle instanceof ElementType)
//						replaceRefe(element,(ElementType) cloneEle,(ElementType) iterable_element);
//				}
//				}
//		else
			if	(collectionAttribute instanceof CompleteLinkElementType)
		{
			nuevo=new CompleteLinkElementType(((CompleteLinkElementType) collectionAttribute).getName(),collectionAttribute.getFather());
			for (CompleteOperationalView nuevoShow : collectionAttribute.getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : collectionAttribute.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		else if	(collectionAttribute instanceof CompleteResourceElementType)
		{
			nuevo=new CompleteResourceElementType(((CompleteResourceElementType) collectionAttribute).getName(),collectionAttribute.getFather());
			for (CompleteOperationalView nuevoShow : collectionAttribute.getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : collectionAttribute.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		else if	(collectionAttribute instanceof CompleteTextElementType)
		{

			nuevo=new CompleteTextElementType(((CompleteTextElementType) collectionAttribute).getName(), collectionAttribute.getFather());
			for (CompleteOperationalView nuevoShow : collectionAttribute.getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : collectionAttribute.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}

		else {
			nuevo=new CompleteElementType(((CompleteElementType) collectionAttribute).getName(), collectionAttribute.getFather());
			for (CompleteOperationalView nuevoShow : collectionAttribute.getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : collectionAttribute.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		
		return nuevo;
	}


	

/**
 * Funcion que clona un elemento de manera profunda
 * @param elementoColonar elemento a clonar
 * @param padreNUevo nuevo padre
 * @param element 
 * @return el elemento nuevo
 */
	private CompleteStructure CloneEleProfundo(
			CompleteStructure elementoColonar, CompleteStructure padreNUevo, ArrayList<Integer> element) {
		CompleteStructure nuevo=null;
//		if (elementoColonar instanceof MetaControlled)
//		{
//		nuevo=new MetaControlled(((MetaControlled) elementoColonar).getName(), padreNUevo);
//		for (OperationalView nuevoShow : ((ElementType) elementoColonar).getShows()) {
//			OperationalView NuevoShownClonado=nuevoShow;
//			((ElementType)nuevo).getShows().add(NuevoShownClonado);
//		}
//		for (Structure iterable_element : elementoColonar.getSons()) {
//			Structure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
//			nuevo.getSons().add(cloneEle);
//			if (cloneEle instanceof ElementType)
//				replaceRefe(element,(ElementType) cloneEle,(ElementType) iterable_element);
//		}
//		}
//		else
			if	(elementoColonar instanceof CompleteLinkElementType)
		{
			nuevo=new CompleteLinkElementType(((CompleteLinkElementType) elementoColonar).getName(), padreNUevo);
			for (CompleteOperationalView nuevoShow : ((CompleteElementType) elementoColonar).getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : elementoColonar.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}	
		}
		else if	(elementoColonar instanceof CompleteResourceElementType)
		{
			nuevo=new CompleteResourceElementType(((CompleteResourceElementType) elementoColonar).getName(), padreNUevo);
			for (CompleteOperationalView nuevoShow : ((CompleteElementType) elementoColonar).getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : elementoColonar.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}	
		}
		else if	(elementoColonar instanceof CompleteTextElementType)
		{
			nuevo=new CompleteTextElementType(((CompleteTextElementType) elementoColonar).getName(), padreNUevo);
			for (CompleteOperationalView nuevoShow : ((CompleteElementType) elementoColonar).getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			for (CompleteStructure iterable_element : elementoColonar.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		else if (elementoColonar instanceof CompleteElementType){
			nuevo=new CompleteElementType(((CompleteElementType) elementoColonar).getName(), padreNUevo);
			for (CompleteOperationalView nuevoShow : ((CompleteElementType) elementoColonar).getShows()) {
				CompleteOperationalView NuevoShownClonado=nuevoShow;
				((CompleteElementType)nuevo).getShows().add(NuevoShownClonado);
			}
			
			for (CompleteStructure iterable_element : elementoColonar.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		else {
			nuevo=new CompleteIterator(padreNUevo);
			for (CompleteStructure iterable_element : elementoColonar.getSons()) {
				CompleteStructure cloneEle = CloneEleProfundo(iterable_element,nuevo,element);
				nuevo.getSons().add(cloneEle);
				if (cloneEle instanceof CompleteElementType)
					replaceRefe(element,(CompleteElementType) cloneEle,(CompleteElementType) iterable_element);
			}
		}
		return nuevo;
	}


	/**
	 * Rellena la tabla con los vocabularios y la tabla de compartidos con aquellos que compartiran vocabulario.
	 * @param list modelo de entrada.
	 */
	private void rellenaTablaVocabularios(List<CompleteStructure> list) {
		for (CompleteStructure attribute : list) {
			if (attribute instanceof CompleteElementType)
				if (attribute instanceof CompleteTextElementType&&StaticFuctionsOda2.isControled((CompleteTextElementType)attribute))
				{
					Integer Numero=StaticFuctionsOda2.getVocNumber((CompleteTextElementType) attribute);
					Boolean Compartido=StaticFuctionsOda2.getVocCompartido((CompleteTextElementType) attribute);
					if (Numero!=null&&Compartido)
							Vocabularios.add(Numero);

				}
			rellenaTablaVocabularios(attribute.getSons());
		}
	}

	/**
	 * Procea el modelo para las no raices de los metas.
	 * @param list hijos del padre.
	 * @param padre padre meta de modelo.
	 * @throws CompleteImportRuntimeException error si esta mal introducido el modelo en la funcion
	 */
	private void processModelo(List<CompleteStructure> list, int padre) throws CompleteImportRuntimeException {
	
		int pos =1;
		for (CompleteStructure Cattribute : list) {
			
			if (Cattribute instanceof CompleteIterator)
				throw new CompleteImportRuntimeException(EXISTE_ERROR_EN_EL_PARSEADO_DE_LAS_ITERACIONES);
			else{
				CompleteElementType attribute=(CompleteElementType) Cattribute;
			
			String Name = attribute.getName().replaceAll("'", "\\\\'");
			String Browser;
			if (StaticFuctionsOda2.getBrowseable(attribute))
				Browser="S";
			else Browser="N";
			
			String Visible;
			if (StaticFuctionsOda2.getVisible(attribute))
					Visible="S";
			else Visible="N";
			
			String Extensible;
			if (StaticFuctionsOda2.isExtensible(attribute))
				Extensible="S";
			else Extensible="N";
			
			int Salida=2;
			if (attribute instanceof CompleteTextElementType){
				
				if (StaticFuctionsOda2.isNumeric(attribute))
					Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'N', '"+Extensible+"', '0');");
				else if (StaticFuctionsOda2.isDate(attribute))
					Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'F', '"+Extensible+"', '0');");
				else if (StaticFuctionsOda2.isControled(attribute))
					{
					Integer vocabularioN=StaticFuctionsOda2.getVocNumber((CompleteTextElementType) attribute);
					Integer otro=VocabulariosSalida.get(vocabularioN);
					String catalogocomp;
					if (otro!=null)
					{
						catalogocomp=otro.toString();
						Salida=insertIntoFather(padre,Name,Browser,'C',catalogocomp,Visible);
					}
					else {
						if (Vocabularios.contains(vocabularioN))
							{
							catalogocomp="1";
							Salida=insertIntoFather(padre,Name,Browser,'C',catalogocomp,Visible);
							VocabulariosSalida.put(vocabularioN,Salida );
							}
						else {
						catalogocomp="0";
						Salida=insertIntoFather(padre,Name,Browser,'C',catalogocomp,Visible);
						}
					}
					}
				else
					Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'T', '"+Extensible+"', '0');");
			}

			else {
			
				Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'X', '"+Extensible+"', '0');");
			}
			ModeloOda.put(attribute, Salida);
			processModelo(attribute.getSons(),Salida);
			pos++;	
			}
		} 
	}

	/**
	 * inserta los elementos en el padre aasignado
	 * @param padre padre a insertar
	 * @param name nombre
	 * @param browser si es navegable
	 * @param Tipo tipo a insertar
	 * @param vocabulario voavulario
	 * @return id de salida en la tabla
	 */
	private int insertIntoFather(int padre, String name, String browser, char Tipo, String vocabulario,String visible) {
		try {
			ResultSet rs=MySQLConnectionOdA2.RunQuerrySELECT("SELECT MAX(orden) FROM section_data WHERE idpadre="+padre+";");
			if (rs!=null) 
			{
				rs.next();
					
					Object datoO= rs.getObject("MAX(orden)");
					String Dato="0";
					if (datoO!=null)
						Dato=datoO.toString();
					int orden=Integer.parseInt(Dato);
					orden++;
					int Salida= MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+name+"', '"+visible+"','"+orden+"','"+browser+"' , '"+Tipo+"', 'S', '"+vocabulario+"');");
				
			rs.close();
			return Salida;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Procesa los objetos virtuales
	 * @param list lista de Secciones candidatas a oV
	  * @throws ImportRuntimeException en caso de errores varios. Consultar el error en {@link ImportRuntimeException}
	 */
	private void processOV(List<CompleteDocuments> list) throws CompleteImportRuntimeException {
		for (CompleteDocuments resources : list) {
			if (StaticFuctionsOda2.isAVirtualObject(resources))
				saveOV(resources);
		}
		
		for (Entry<CompleteDocuments, Integer> completeDocuments : tabla.entrySet()) {
			procesa_Atributos(completeDocuments.getKey(),completeDocuments.getValue());
		}
	}

//	/**
//	 * Dtermina si un objeto es un recurso o es una categoria OV
//	 * @param resources
//	 * @return si es un OV
//	 */
//	private boolean ISOV(Construct resources) {
//		for (MetaValue valor : resources.getDescription()) {
//			if ((valor instanceof MetaTextValue)&&(valor.getHastype().getName().equals(Oda2StaticNames.IDOV)))
//				return true;
//				
//		}
//		return false;
//	}


	/**
	 * Funcion que procesa un OV ya asignado.
	 * @param ObjetoDigital OV en forma de seccion.
	 * @throws ImportRuntimeException en caso de errores varios. Consultar el error en {@link ImportRuntimeException}
	 */
	private void saveOV(CompleteDocuments ObjetoDigital) throws CompleteImportRuntimeException {
		try{
//		Integer Idov=StaticFuctionsOda2.findIdov(ObjetoDigital);
		boolean Public=StaticFuctionsOda2.getPublic(ObjetoDigital);
		boolean Private=StaticFuctionsOda2.getPrivate(ObjetoDigital);
		
		String SPublic="N";
		if (Public)
			SPublic="S";
		
		String SPrivate="N";
		if (Private)
			SPrivate="S";
		
				
		int Salida = MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `virtual_object` (`ispublic`,`isprivate`) VALUES ('"+SPublic+"','"+SPrivate+"');");
		
		tabla.put(ObjetoDigital,Salida);
		procesa_descripcion(ObjetoDigital,Salida);
		procesa_iconos(ObjetoDigital,Salida);
		
		
		}catch (NumberFormatException e)
		{
			throw new CompleteImportRuntimeException("Los Identificadores no son numeros.");
		}
		
	}


	/**
	 * Procesa la descripcion del OV
	 * @param objetoDigital
	 * @param idov
	 */
	private void procesa_descripcion(CompleteDocuments objetoDigital, Integer Idov) {
		MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `text_data` (`idov`, `idseccion`, `value`) VALUES ('"+Idov+"', '111', '"+SQLScaped(objetoDigital.getDescriptionText())+"');");
		
	}


	/**
	 * Procesalos iconos y los introduce en la lista de el idov
	 * @param ObjetoDigital recurso de entrada
	 * @param ObjetoDigitalIdov Identificador del ObjetoDigital
	 * @throws ImportRuntimeException si el Objeto no tiene idov
	 */
	private void procesa_iconos(CompleteDocuments ObjetoDigital, Integer ObjetoDigitalIdov) throws CompleteImportRuntimeException {
		
		String Path= StaticFuctionsOda2.getIcon(ObjetoDigital);
		if (Path!=null)
			Iconos.put(ObjetoDigitalIdov, new CompleteFile(Path,toOda));		
	}

	
	/**
	 * Procesa un recurso sobre su Objeto Digital
	 * @param recursoAProcesar Recurso que sera procesado
	 * @param idov identificador del sueño del recurso.
	 * @param visibleValue2 
	 * @return 
	 * @throws ImportRuntimeException si el elemento no tiene un campo en su descripcion necesario.
	 */
	protected abstract int procesa_recursos(CompleteDocuments recursoAProcesar, Integer idov, boolean visibleValue2);
	
//	/**
//	 * Procesa un recurso sobre su Objeto Digital
//	 * @param recursoAProcesar Recurso que sera procesado
//	 * @param idov identificador del sueño del recurso.
//	 * @param visibleValue2 
//	 * @return 
//	 * @throws ImportRuntimeException si el elemento no tiene un campo en su descripcion necesario.
//	 */
//	protected int procesa_recursos(CompleteDocuments recursoAProcesar, Integer idov, boolean visibleValue2) throws CompleteImportRuntimeException {
//
//		
//		boolean visBool=visibleValue2;
//		String VisString;
//		if (visBool) 
//				VisString="S";
//		else 
//			VisString="N";
//		
//	
//				CompleteDocuments recursoAProcesarC = (CompleteDocuments)recursoAProcesar;
//				
//				if (StaticFuctionsOda2.isAVirtualObject(recursoAProcesarC))
//				{
//					
//					Integer Idov=tabla.get(recursoAProcesarC);
//					if (Idov!=null)
//						 {
//						int Salida = MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `idov_refered`, `type`) VALUES ('"+idov+"', '"+VisString+"','N', '"+Idov+"','OV')");	
//						return Salida;
//						 }
//					else
//						ColectionLog.getLogLines().add("Link a objeto virtual: "+ recursoAProcesarC.getDescriptionText()+ "no existe en la lista de recursos, pero tiene un link, IGNORADO" );
//				}
//				else
//					if (StaticFuctionsOda2.isAFile(recursoAProcesarC))
//				{
//						CompleteResourceElement FIleRel=StaticFuctionsOda2.findMetaValueFile(recursoAProcesarC.getDescription());
//						CompleteLinkElement idovrefVal= StaticFuctionsOda2.findMetaValueIDOVowner(recursoAProcesarC.getDescription());
//					
//					
//					if (idovrefVal!=null)
//					{
//						Integer Idov=tabla.get(idovrefVal.getValue());
//					
//						
//						if  (FIleRel!=null && Idov!=null && (FIleRel instanceof CompleteResourceElementFile))
//							{
//								CompleteFile Icon=Iconos.get(idov);
//								String iconoov;
//								if (Icon!=null && Icon.getPath().equals((((CompleteResourceElementFile)FIleRel).getValue()).getPath()))
//									iconoov="S";
//								else iconoov="N";
//								String[] spliteStri=(((CompleteResourceElementFile)FIleRel).getValue()).getPath().split("/");
//								String NameS = spliteStri[spliteStri.length-1];
//								
//								
//								if (Idov==idov)
//									{
//									int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `name`, `type`) VALUES ('"+idov+"', '"+VisString+"','"+iconoov+"', '"+NameS+"', 'P' )");
//									return Salida;
//									}
//								else
//									{
//									int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`, `name`,`idresource_refered`, `type`) VALUES ('"+idov+"', '"+VisString+"', '"+NameS+"', '"+Idov+"','F')");
//									return Salida;
//									}
//							}
//						else ColectionLog.getLogLines().add("EL file referencia es nulo, o no es un file o el dueño no es un Objeto virtual valido con identificadorArchivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO");
//					}
//					else ColectionLog.getLogLines().add("El objeto dueño del archivo es nulo o no Objeto Virtual, Archivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO ");
//				}
//					else
//						if (StaticFuctionsOda2.isAURL(recursoAProcesarC))
//						{
//							//public static final String URI = "URI";
//							CompleteResourceElementURL UniFile=StaticFuctionsOda2.findMetaValueUri(recursoAProcesarC.getDescription());
//
//							
//								
//								if  (UniFile!=null&&!UniFile.getValue().isEmpty())
//									{
//
//											int Salida =MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `resources` (`idov`, `visible`,`iconoov`, `name`, `type`) VALUES ('"+idov+"', '"+VisString+"','N', '"+UniFile.getValue()+"', 'U' )");
//											return Salida;
//
//
//									}
//								else ColectionLog.getLogLines().add("El URI referencia es nulo, o vacio identificadorArchivo:"+recursoAProcesarC.getDescriptionText()+", IGNORADO");
//
//						}
//				return -1;
//		
//	}
//


	/**
	 * Clase que procesa los atributos de un OV
	 * @param DO Ovirtual
	 * @param Idov Identificador del objeto.
	 * @throws ImportRuntimeException en caso de errores varios. Consultar el error en {@link CompleteImportRuntimeException}
	 */
	private void procesa_Atributos(CompleteDocuments DO, Integer Idov) throws CompleteImportRuntimeException {
		
		ArrayList<CompleteElement> Normales=new ArrayList<CompleteElement>();
		ArrayList<CompleteElement> Recursos=new ArrayList<CompleteElement>();
		HashMap<Integer, Long> AmbitoSalida=new HashMap<Integer, Long>();
		
		for (CompleteElement attributeValue : DO.getDescription()) 
		{
			if (dependedelfile(attributeValue.getHastype()))
				Recursos.add(attributeValue);
			else
				Normales.add(attributeValue);
		}
		
		
		
		for (CompleteElement attributeValue : Normales) {
						
			if (attributeValue.getHastype() instanceof CompleteResourceElementType)
			{
				if (StaticFuctionsOda2.isResources(attributeValue.getHastype()))	
				{
					boolean VisibleValue= StaticFuctionsOda2.getVisible(((CompleteResourceElement)attributeValue));
						if (attributeValue instanceof CompleteLinkElement)
							{
							Integer Value=procesa_recursos(((CompleteLinkElement)attributeValue).getValue(),Idov,VisibleValue);
							if (Value>0&&attributeValue.getAmbitos().size()>0)
								AmbitoSalida.put(attributeValue.getAmbitos().get(0),new Long(Value));
							}
				}
			}
			if (attributeValue.getHastype() instanceof CompleteLinkElementType)
			{
				if (StaticFuctionsOda2.isResources(attributeValue.getHastype()))	
				{
					boolean VisibleValue= StaticFuctionsOda2.getVisible(((CompleteLinkElement)attributeValue));
						if (attributeValue instanceof CompleteLinkElement)
							{
							Integer Value=procesa_recursos(((CompleteLinkElement)attributeValue).getValue(),Idov,VisibleValue);
							if (Value>0&&attributeValue.getAmbitos().size()>0)
								AmbitoSalida.put(attributeValue.getAmbitos().get(0),new Long(Value));
							}
				}
			}
			else{
			Integer seccion=ModeloOda.get(attributeValue.getHastype());
			if (seccion!=null){
			if (attributeValue instanceof CompleteTextElement){
				
				if (StaticFuctionsOda2.isNumeric(attributeValue.getHastype()))
				{
					String value = SQLScaped(((CompleteTextElement) attributeValue).getValue());
					value=value.replace("'", "\\'");
					MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `numeric_data` (`idov`, `idseccion`, `value`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"');");

				}
				else if (StaticFuctionsOda2.isDate(attributeValue.getHastype()))
				{
					SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date fecha = null;
					try {
						fecha = formatoDelTexto.parse(((CompleteTextElement) attributeValue).getValue());
						DateFormat df = new SimpleDateFormat ("yyyyMMdd");
						String value=df.format(fecha);
						MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `date_data` (`idov`, `idseccion`, `value`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"');");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
				else if (StaticFuctionsOda2.isControled(attributeValue.getHastype()))
				{
					String value = ((CompleteTextElement) attributeValue).getValue();
					value=value.replace("'", "\\'");
					if (value!=null)
						MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `controlled_data` (`idov`, `idseccion`, `value`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"');");
				}
				else
				{
				
				String value = SQLScaped(((CompleteTextElement) attributeValue).getValue());
				value=value.replace("'", "\\'");
				MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `text_data` (`idov`, `idseccion`, `value`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"');");
				}
			}
			}
			}
		}
		
		for (CompleteElement attributeValue : Recursos) {
			
			Integer seccion=ModeloOda.get(attributeValue.getHastype());
			Long ValueRec=null;
			if (attributeValue.getAmbitos().size()>0)
				ValueRec=AmbitoSalida.get(attributeValue.getAmbitos().get(0));
			if (seccion!=null&&ValueRec!=null){
				
			if (attributeValue instanceof CompleteTextElement){
				
				if (StaticFuctionsOda2.isNumeric(attributeValue.getHastype()))
				{
					String value = SQLScaped(((CompleteTextElement) attributeValue).getValue());
					value=value.replace("'", "\\'");
					MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `numeric_data` (`idov`, `idseccion`, `value`,`idrecurso`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"', '"+ValueRec+"');");

				}
				else if (StaticFuctionsOda2.isDate(attributeValue.getHastype()))
				{
					SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date fecha = null;
					try {
						fecha = formatoDelTexto.parse(((CompleteTextElement) attributeValue).getValue());
						DateFormat df = new SimpleDateFormat ("yyyyMMdd");
						String value=df.format(fecha);
						MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `date_data` (`idov`, `idseccion`, `value`,`idrecurso`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"', '"+ValueRec+"');");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
				else if (StaticFuctionsOda2.isControled(attributeValue.getHastype()))
				{
					String value = ((CompleteTextElement) attributeValue).getValue();
					value=value.replace("'", "\\'");
					if (value!=null)
						MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `controlled_data` (`idov`, `idseccion`, `value`,`idrecurso`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"', '"+ValueRec+"');");
				}
				else
				{
				
				String value = SQLScaped(((CompleteTextElement) attributeValue).getValue());
				value=value.replace("'", "\\'");
				MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `text_data` (`idov`, `idseccion`, `value`,`idrecurso`) VALUES ('"+Idov+"', '"+seccion+"', '"+value+"', '"+ValueRec+"');");
				}
			}
			}
		}
		
	}

	/**
	 * Depende del file o es un atributo estandar
	 * @param hastype
	 * @return
	 */
	private boolean dependedelfile(CompleteStructure hastype) {
			if (hastype.getFather()==null)
				return false;
			else if ((hastype.getFather()instanceof CompleteElementType)&&StaticFuctionsOda2.isResources((CompleteElementType) hastype.getFather()))
				return true;
			else 
				return dependedelfile(hastype.getFather());
	}



	private String SQLScaped(String value) {
		String Salida=value;
		Salida=Salida.replace("'", "''");
		Salida=Salida.replace("\"", "\\\"");
		return Salida;
	}


	/**
	 * Reseta las tablas sin borrar las tablas añadidas
	 */
	public static void resetBasico() {
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `controlled_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `controlled_data` ("+
			" `id` int(11) NOT NULL AUTO_INCREMENT,"+
			" `idov` int(11) DEFAULT NULL,"+
			"`idseccion` int(11) DEFAULT NULL,"+
			"`value` text DEFAULT NULL,"+
			"`idrecurso` int(11) DEFAULT NULL,"+
			"PRIMARY KEY (`id`),"+
			"KEY `idseccion` (`idseccion`),"+
			"KEY `idov` (`idov`)"+
			") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");


		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `date_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `date_data` ("+
			"`id` int(11) NOT NULL AUTO_INCREMENT,"+
			"`idov` int(11) DEFAULT NULL,"+
			"`idseccion` int(11) DEFAULT NULL,"+
			"`value` int(8) DEFAULT NULL,"+
			"`idrecurso` int(11) DEFAULT NULL,"+
			"UNIQUE KEY `id` (`id`)"+
			") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;");
				
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `numeric_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `numeric_data` ("+
			"`id` int(11) NOT NULL AUTO_INCREMENT,"+
			"`idov` int(11) DEFAULT NULL,"+
			"`idseccion` int(11) DEFAULT NULL,"+
			"`value` decimal(30,15) DEFAULT NULL,"+
			"`idrecurso` int(11) DEFAULT NULL,"+
			"PRIMARY KEY (`id`)"+
			") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `resources`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `resources` ("+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`idov` int(11) DEFAULT NULL,"+
				  "`ordinal` int(11) DEFAULT NULL,"+
				  "`visible` char(1) DEFAULT NULL,"+
				  "`iconoov` char(1) DEFAULT NULL,"+
				  "`name` varchar(255) DEFAULT NULL,"+
				  "`idov_refered` int(11) DEFAULT NULL,"+
				  "`idresource_refered` int(11) DEFAULT NULL,"+
				  "`type` varchar(255) DEFAULT NULL,"+
				  "PRIMARY KEY (`id`)"+
				") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

				MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `section_data`;");
				MySQLConnectionOdA2.RunQuerry("CREATE TABLE `section_data` ("+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`idpadre` int(11) DEFAULT NULL,"+
				  "`nombre` varchar(255) DEFAULT NULL,"+
				  "`codigo` varchar(255) DEFAULT NULL,"+
				  "`tooltip` varchar(255) DEFAULT NULL,"+
				  "`visible` char(1) DEFAULT NULL,"+
				  "`orden` int(11) DEFAULT NULL,"+
				  "`browseable` char(1) DEFAULT NULL,"+
				  "`tipo_valores` varchar(255) DEFAULT NULL,"+
				  "`extensible` char(1) DEFAULT NULL,"+
				  "`vocabulario` int(11) DEFAULT NULL,"+
				  "`decimales` int(11) DEFAULT NULL,"+
				  "PRIMARY KEY (`id`)"+
				") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

				MySQLConnectionOdA2.RunQuerry("INSERT INTO `section_data` VALUES (2,0,'Modelo de MetaDatos','lom','metadatos','S',2,NULL,NULL,'S',0,NULL)," +
						"(3,0,'Modelo de Datos de los Recursos','recursos','recursos','S',3,NULL,NULL,'S',0,NULL)," +
						"(111,1,'Descripción',NULL,'fijo_texto','S',1,NULL,'T',NULL,NULL,NULL)," +
						"(112,1,'Tipo Registro',NULL,'fijo_controlado','S',5,'S','C',NULL,NULL,NULL)," +
						"(1,0,'Modelo de Datos','datos','datos','S',1,NULL,NULL,'S',0,NULL);");
				

				MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `text_data`;");
				MySQLConnectionOdA2.RunQuerry("CREATE TABLE `text_data` ("+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`idov` int(11) DEFAULT NULL,"+
				  "`idseccion` int(11) DEFAULT NULL,"+
				  "`value` text,"+
				  "`idrecurso` int(11) DEFAULT NULL,"+
				  "PRIMARY KEY (`id`)"+
				") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
				
				MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `virtual_object`;");
				MySQLConnectionOdA2.RunQuerry("CREATE TABLE `virtual_object` ("+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`ispublic` char(1) DEFAULT NULL,"+
				  "`isprivate` char(1) DEFAULT NULL,"+
				  "PRIMARY KEY (`id`)"+
				") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

				
	}

	
	/**
	 * Resetea profundamente las tablas borrando todo el contenido.
	 */
	public static void resetProfundoTablas() {
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `contenidos_pagina`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `contenidos_pagina` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idpagina` int(11) DEFAULT NULL,"+
		  "`tipo` varchar(255) DEFAULT NULL,"+
		  "`imagen` varchar(255) DEFAULT NULL,"+
		  "`contenido` text,"+
		  "`orden` int(11) DEFAULT NULL,"+
		 "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
	
		MySQLConnectionOdA2.RunQuerry("INSERT INTO `contenidos_pagina`" +
				" (`id`, `idpagina`, `tipo`, `imagen`, `contenido`, `orden`) VALUES" +
				"	(1, 1, '2', NULL, '<h1 style=\"text-align: center; \">" +
				"Presentaci&oacute;n del sitio web&nbsp;</h1>', 5);");
		
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `controlled_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `controlled_data` ("+
		 " `id` int(11) NOT NULL AUTO_INCREMENT,"+
		 " `idov` int(11) DEFAULT NULL,"+
		  "`idseccion` int(11) DEFAULT NULL,"+
		  "`value` text DEFAULT NULL,"+
		  "`idrecurso` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`),"+
		  "KEY `idseccion` (`idseccion`),"+
		  "KEY `idov` (`idov`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");


		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `date_data`;");
				MySQLConnectionOdA2.RunQuerry("CREATE TABLE `date_data` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "`idseccion` int(11) DEFAULT NULL,"+
		  "`value` int(8) DEFAULT NULL,"+
		  "`idrecurso` int(11) DEFAULT NULL,"+
		  "UNIQUE KEY `id` (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;");

		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `log_modificaciones`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `log_modificaciones` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idusuario` int(11) DEFAULT NULL,"+
		  "`fechaModificacion` varchar(14) DEFAULT NULL,"+
		  "`tipo` varchar(2) DEFAULT NULL,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `mensajes`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `mensajes` ("+
		 " `id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`lang` varchar(255) DEFAULT NULL,"+
		  "`atributo` varchar(255) DEFAULT NULL,"+
		  "`valor` text,"+
		  "`grupo` varchar(255) DEFAULT NULL,"+
		  "`formato` varchar(255) DEFAULT NULL,"+
		  "`tipo` varchar(255) DEFAULT NULL,"+
		  "`etiqueta` varchar(255) DEFAULT NULL,"+
		  "`orden` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

		
		MySQLConnectionOdA2.RunQuerry("INSERT INTO `mensajes` (`id`, `lang`, `atributo`, `valor`, `grupo`, `formato`, `tipo`, `etiqueta`, `orden`) VALUES" +
				"(121, 'es', 'datos_titulo', 'Objetos Digitales Arqueológicos', 'Datos del contenedor', 'largo', 'cabecera', 'Título del contenedor', 1)," +
				"(122, 'es', 'datos_descripcion', 'Contenedor de objetos digitales', 'Datos del sitio web', 'texto', 'cabecera', 'Descripción', 2)," +
				"(123, 'es', 'datos_palabras', ' museo virtual, contenedor de objetos virtuales de aprendizaje, repositorio de objetos de aprendizaje', 'Datos del sitio web', 'texto', 'cabecera', 'Palabras clave', 3)," +
				"(124, 'es', 'datos_imagen', '../../download/bancorecursos/cabecera_digitales.png', 'Datos del contenedor', 'imagen', 'cabecera', 'Imagen cabecera', 4)," +
				"(128, 'en', 'datos_imagen', NULL, 'Datos del contenedor', 'imagen', 'cabecera', 'Imagen Cabecera', 4);");
		
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `navegacion`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `navegacion` ("+
		 "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`nombre` varchar(255) DEFAULT NULL,"+
		  "`tooltip` varchar(255) DEFAULT NULL,"+
		  "`idpadre` int(11) DEFAULT NULL,"+
		  "`visible` char(1) DEFAULT NULL,"+
		  "`orden` int(11) DEFAULT NULL,"+
		  "`tipo_contenido` varchar(255) DEFAULT NULL,"+
		  "`idpagina` int(11) DEFAULT NULL,"+
		  "`tipo` varchar(255) DEFAULT NULL,"+
		  "`nombreseo` varchar(255) DEFAULT NULL,"+
		  "`imagen` varchar(255) DEFAULT NULL,"+
		  "`contenido` text,"+
		  "`url` varchar(255) DEFAULT NULL,"+
		  "`tiene_contenido` varchar(255) DEFAULT NULL,"+
		  "`protocolo` varchar(255) DEFAULT NULL,"+
		  "`ventanaexterna` char(1) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("insert into `navegacion`" +
				" (`id`, `nombre`, `tooltip`, `idpadre`, `visible`, `orden`, `tipo_contenido`, `idpagina`, `tipo`, `nombreseo`, `imagen`, `contenido`, `url`, `tiene_contenido`, `protocolo`, `ventanaexterna`)" +
				" values('1','MANTENIMIENTO','Mantenimiento','0','N','7','M',NULL,'I','mantenimiento',NULL,NULL,NULL,'S',NULL,'N');");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `numeric_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `numeric_data` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "`idseccion` int(11) DEFAULT NULL,"+
		  "`value` decimal(30,15) DEFAULT NULL,"+
		  "`idrecurso` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `paginas`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `paginas` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`titulo` varchar(255) DEFAULT NULL,"+
		  "`contenido` text,"+
		  "`visible` char(1) DEFAULT NULL,"+
		  "`documento` varchar(255) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("insert  into `paginas`(`id`,`titulo`,`contenido`,`visible`,`documento`) values (1,'Presentación del sitio web',NULL,'N',NULL);");

	
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `permisos`;");
				MySQLConnectionOdA2.RunQuerry("CREATE TABLE `permisos` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idusuario` int(11) DEFAULT NULL,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "`tipoPermiso` char(1) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `preferencias_presentacion`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `preferencias_presentacion` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`atributo` varchar(255) DEFAULT NULL,"+
		  "`valor` text,"+
		  "`tipo` varchar(255) DEFAULT NULL,"+
		  "`etiqueta` varchar(255) DEFAULT NULL,"+
		  "`orden` int(11) DEFAULT NULL,"+
		  "`formato` varchar(255) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("INSERT INTO `preferencias_presentacion` (`id`, `atributo`, `valor`, `tipo`, `etiqueta`, `orden`, `formato`) VALUES" +
				"(1, 'portada_contenido', 'I', 'P', NULL, NULL, NULL)," +
				"(2, 'imagen_fondo_global', NULL, 'F', 'Imagen de fondo global', 1, 'imagen')," +
				"(3, 'color_fondo', NULL, 'F', 'Color de fondo', 2, 'color')," +
				"(4, 'seguridad_web', 'N', NULL, NULL, NULL, NULL)," +
				"(5, 'extension_archivos', 'png;jpg', NULL, NULL, NULL, NULL)," +
				"(6, 'numeric_decimal', '1', NULL, NULL, NULL, NULL);");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `resources`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `resources` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "`ordinal` int(11) DEFAULT NULL,"+
		  "`visible` char(1) DEFAULT NULL,"+
		  "`iconoov` char(1) DEFAULT NULL,"+
		  "`name` varchar(255) DEFAULT NULL,"+
		  "`idov_refered` int(11) DEFAULT NULL,"+
		  "`idresource_refered` int(11) DEFAULT NULL,"+
		  "`type` varchar(255) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `section_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `section_data` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idpadre` int(11) DEFAULT NULL,"+
		  "`nombre` varchar(255) DEFAULT NULL,"+
		  "`codigo` varchar(255) DEFAULT NULL,"+
		  "`tooltip` varchar(255) DEFAULT NULL,"+
		  "`visible` char(1) DEFAULT NULL,"+
		  "`orden` int(11) DEFAULT NULL,"+
		  "`browseable` char(1) DEFAULT NULL,"+
		  "`tipo_valores` varchar(255) DEFAULT NULL,"+
		  "`extensible` char(1) DEFAULT NULL,"+
		  "`vocabulario` int(11) DEFAULT NULL,"+
		  "`decimales` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

		MySQLConnectionOdA2.RunQuerry("INSERT INTO `section_data` VALUES (2,0,'Modelo de MetaDatos','lom','metadatos','S',2,NULL,NULL,'S',0,NULL)," +
				"(3,0,'Modelo de Datos de los Recursos','recursos','recursos','S',3,NULL,NULL,'S',0,NULL)," +
				"(111,1,'Descripción',NULL,'fijo_texto','S',1,NULL,'T',NULL,NULL,NULL)," +
				"(112,1,'Tipo Registro',NULL,'fijo_controlado','S',5,'S','C',NULL,NULL,NULL)," +
				"(1,0,'Modelo de Datos','datos','datos','S',1,NULL,NULL,'S',0,NULL);");
		

		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `text_data`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `text_data` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`idov` int(11) DEFAULT NULL,"+
		  "`idseccion` int(11) DEFAULT NULL,"+
		  "`value` text,"+
		  "`idrecurso` int(11) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `usuarios`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `usuarios` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`nombre` varchar(255) DEFAULT NULL,"+
		  "`apellidos` varchar(255) DEFAULT NULL,"+
		  "`correo` varchar(255) DEFAULT NULL,"+
		  "`login` varchar(255) DEFAULT NULL,"+
		  "`password` varchar(255) DEFAULT NULL,"+
		  "`rol` varchar(255) DEFAULT NULL,"+
		  "`ultimo_acceso` varchar(20) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		

		MySQLConnectionOdA2.RunQuerry("INSERT INTO `usuarios` VALUES (11,'admin1','admin2','test@test.test','admin','admin','B','20121112035440')," +
			"(111,'user1','user2','test@test.test','user','user','C','20121113153706')," +
			"(1,'super1','super2','test@test.test','superadmin','superadmin','A','20121114025845');");
		
		MySQLConnectionOdA2.RunQuerry("DROP TABLE IF EXISTS `virtual_object`;");
		MySQLConnectionOdA2.RunQuerry("CREATE TABLE `virtual_object` ("+
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`ispublic` char(1) DEFAULT NULL,"+
		  "`isprivate` char(1) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

	
		
	}


	

}
