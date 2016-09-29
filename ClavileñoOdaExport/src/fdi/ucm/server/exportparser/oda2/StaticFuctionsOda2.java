/**
 * 
 */
package fdi.ucm.server.exportparser.oda2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteLinkElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteOperationalValue;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalValueType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteStructure;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * Clase que genera las funciones estaticas para Oda.
 * @author Joaquin Gayoso-Cabada
 *
 */
public class StaticFuctionsOda2 {

//	public static Integer findIdov(CompleteDocuments value) {
//		for (CompleteElement elem : value.getDescription()) {
//			if (elem instanceof CompleteTextElement&&isIDOV(elem.getHastype()))
//				return Integer.parseInt(((CompleteTextElement) elem).getValue());
//		}
//		return null;
//	}



	
	
	
	
	
	/**
	 * Obtiene su visibilidad en los casos de visibilidad asociados a las vistas
	 * @param completeElementType
	 * @return
	 */
	public static boolean getVisible(CompleteElementType completeElementType) {
		ArrayList<CompleteOperationalValueType> Shows = completeElementType.getShows();
		for (CompleteOperationalValueType show : Shows) {
				if (show.getName().equals(StaticNamesOda2.VISIBLESHOWN))
					if (show.getDefault().equals(Boolean.toString(true)))
						return true;
					else if (show.getDefault().equals(Boolean.toString(false)))
							return false;
		}
		return false;
	}



	/**
	 * Obtiene su navegabilidad en los casos de visibilidad asociados a las vistas
	 * @param completeElementType
	 * @return
	 */
	public static boolean getBrowseable(CompleteElementType completeElementType) {
		ArrayList<CompleteOperationalValueType> Shows = completeElementType.getShows();
		for (CompleteOperationalValueType show : Shows) {
				if (show.getName().equals(StaticNamesOda2.BROWSERSHOWN))
					if (show.getDefault().equals(Boolean.toString(true)))
						return true;
					else if (show.getDefault().equals(Boolean.toString(false)))
							return false;
		}
		return false;
	}

	/**
	 * Funcion que tetorna el icono de un recurso Objeto digital
	 * @param objetoDigital
	 * @return
	 */
	public static String getIcon(CompleteDocuments objetoDigital) {
		
		return objetoDigital.getIcon();

	}

	
	
	/**
	 * Revisa si un elemento es VirtualObject
	 * @param hastype
	 * @return
	 */
	public static boolean isVirtualObject(CompleteGrammar hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getViews();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.VIRTUAL_OBJECT)) 
										return true;

			}
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es VirtualObject
	 * @param ElementTypeId
	 * @return
	 */
	public static boolean isAVirtualObject(CompleteDocuments element, List<CompleteGrammar> Lista) {
		
		for (CompleteGrammar completeGrammar : Lista) {
		
		if (isInGrammar(element, completeGrammar)&&isVirtualObject(completeGrammar))
			return true;
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es VirtualObject
	 * @param ElementTypeId
	 * @return
	 */
	public static boolean isAFile(CompleteDocuments element, List<CompleteGrammar> Lista) {
		
		for (CompleteGrammar completeGrammar : Lista) {
		

			if (isInGrammar(element, completeGrammar)&&isFile(completeGrammar))
				return true;
		}
		return false;
	}
	

	
	/**
	 * Revisa si un elemento es File
	 * @param hastype
	 * @return
	 */
	public static boolean isFile(CompleteGrammar hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getViews();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.FILE)) 
										return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Revisa si un elemento es FileResource
	 * @param hastype
	 * @return
	 */
	public static boolean isFileFisico(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.FILERESOURCE)) 
										return true;
			}
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es Owner
	 * @param hastype
	 * @return
	 */
	public static boolean isOwner(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.IDOV_OWNER)) 
										return true;
			}
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es METADATOS
	 * @param hastype
	 * @return
	 */
	public static boolean isMetaDatos(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.METADATOS)) 
										return true;
			}
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es METADATOS
	 * @param hastype
	 * @return
	 */
	public static boolean isDatos(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.DATOS)) 
										return true;
			}
		}
		return false;
	}
	
	/**
	 * Revisa si un elemento es Resources
	 * @param hastype
	 * @return
	 */
	public static boolean isResources(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{

					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.RESOURCE)) 
										return true;
			}
		}
		return false;
	}


	
	/**
	 * Revisa si un elemento es Extensible
	 * @param hastype
	 * @return
	 */
	public static boolean isExtensible(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.ODA))
			{
					if (show.getName().equals(StaticNamesOda2.EXTENSIBLE))
						if (show.getDefault().equals(Boolean.toString(true)))
							return true;
						else if (show.getDefault().equals(Boolean.toString(false)))
								return false;
			}
		}
		return true;
	}
	
	/**
	 * Funcion que retorna si el OV es publico
	 * @param objetoDigital
	 * @return
	 */
	public static boolean getPublic(CompleteDocuments objetoDigital, List<CompleteGrammar> Lista) {
		
		for (CompleteGrammar completeGrammar : Lista) {
		

			if (isInGrammar(objetoDigital, completeGrammar)&&isVirtualObject(completeGrammar))
				{
				ArrayList<CompleteOperationalValue> ShowsInst = objetoDigital.getViewsValues();
				for (CompleteOperationalValue show : ShowsInst) {
					if (show.getType().getView().equals(StaticNamesOda2.ODA)&&show.getType().getName().equals(StaticNamesOda2.PUBLIC))
						if (show.getValue().equals(Boolean.toString(true)))
							return true;
						else if (show.getValue().equals(Boolean.toString(false)))
							return false;
				}
				
				ArrayList<CompleteOperationalValueType> Shows = completeGrammar.getViews();
				for (CompleteOperationalValueType show : Shows) {
					if (show.getView().equals(StaticNamesOda2.ODA))
					{
						if (show.getName().equals(StaticNamesOda2.PUBLIC))
							if (show.getDefault().equals(Boolean.toString(true)))
									return true;
							else if (show.getDefault().equals(Boolean.toString(false)))
									return false;

					}
				}
				return false;
				}
		}
		return false;

	}
	

	
	
	/**
	 * Funcion que retorna si el OV es publico
	 * @param objetoDigital
	 * @return
	 */
	public static boolean getPrivate(CompleteDocuments objetoDigital, List<CompleteGrammar> Lista) {
		
		for (CompleteGrammar completeGrammar : Lista) {
		

		if (isInGrammar(objetoDigital, completeGrammar)&&isVirtualObject(completeGrammar))
				{
			ArrayList<CompleteOperationalValue> ShowsInst = objetoDigital.getViewsValues();
				for (CompleteOperationalValue show : ShowsInst) {
					if (show.getType().getView().equals(StaticNamesOda2.ODA)&&show.getType().getName().equals(StaticNamesOda2.PRIVATE))
						if (show.getValue().equals(Boolean.toString(true)))
							return true;
						else if (show.getValue().equals(Boolean.toString(false)))
							return false;
				}
				
				ArrayList<CompleteOperationalValueType> Shows = completeGrammar.getViews();
				for (CompleteOperationalValueType show : Shows) {
					if (show.getView().equals(StaticNamesOda2.ODA))
					{
						if (show.getName().equals(StaticNamesOda2.PRIVATE))
							if (show.getDefault().equals(Boolean.toString(true)))
								return true;
							else if (show.getDefault().equals(Boolean.toString(false)))
									return false;
					}
				}
				return true;
				}
		}
		return true;

	}
	

	
	
	

	/**
	 * Funcion que retorna el icono de un MetaValue donde esta definido el icono
	 * @param elem
	 * @return
	 */
	public static boolean getVisible(CompleteElement elem) {
		
		ArrayList<CompleteOperationalValue> ShowsInst = elem.getShows();
		for (CompleteOperationalValue show : ShowsInst) {
			if (show.getType().getName().equals(StaticNamesOda2.VISIBLESHOWN))
				if (show.getValue().equals(Boolean.toString(true)))
					return true;
				else if (show.getValue().equals(Boolean.toString(false)))
					return false;
		}
		
		ArrayList<CompleteOperationalValueType> Shows = elem.getHastype().getShows();
		for (CompleteOperationalValueType show : Shows) {
				if (show.getName().equals(StaticNamesOda2.VISIBLESHOWN))
					if (show.getDefault().equals(Boolean.toString(true)))
							return true;
					else if (show.getDefault().equals(Boolean.toString(false)))
							return false;
		}
		return false;
		
	}

	/**
	 * Encuentra relation Value File en File
	 * @param description
	 * @return
	 */
	public static CompleteResourceElement findMetaValueFile(
			List<CompleteElement> description) {
		for (CompleteElement completeElement : description) {
			if (completeElement instanceof CompleteResourceElement&&StaticFuctionsOda2.isFileFisico(completeElement.getHastype()))
				return (CompleteResourceElement) completeElement;
		}
		return null;
	}

	/**
	 * Encuentra relacion entre el file y el OV dueño.
	 * @param description
	 * @return
	 */
	public static CompleteLinkElement findMetaValueIDOVowner(
			List<CompleteElement> description) {
		for (CompleteElement completeElement : description) {
			if (completeElement instanceof CompleteLinkElement&&StaticFuctionsOda2.isOwner(completeElement.getHastype()))
				return (CompleteLinkElement) completeElement;
		}
		return null;
	}







	/**
	 * Clase que define si es numerico
	 * @param hastype
	 * @return
	 */
	public static boolean isNumeric(CompleteElementType hastype) {
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {	
			if (show.getView().equals(StaticNamesOda2.METATYPE))
			{
					if (show.getName().equals(StaticNamesOda2.METATYPETYPE))
							if (show.getDefault().equals(StaticNamesOda2.NUMERIC)) 
										return true;
			}
		}
		return false;
	}








	public static boolean isNumeric(CompleteElement elem) {
		ArrayList<CompleteOperationalValue> ShowsInst = elem.getShows();
		for (CompleteOperationalValue show : ShowsInst) {
			if (show.getType().getView().equals(StaticNamesOda2.METATYPE)&&show.getType().getName().equals(StaticNamesOda2.METATYPETYPE))
				if (show.getValue().equals(StaticNamesOda2.NUMERIC))
					return true;
		}
		
		return isNumeric(elem.getHastype());
		
	}








	public static boolean isDate(CompleteElementType attribute) {
		ArrayList<CompleteOperationalValueType> Shows = attribute.getShows();
		for (CompleteOperationalValueType show : Shows) {	
			if (show.getView().equals(StaticNamesOda2.METATYPE))
			{
					if (show.getName().equals(StaticNamesOda2.METATYPETYPE))
							if (show.getDefault().equals(StaticNamesOda2.DATE)) 
										return true;
			}
		}
		return false;
	}








	public static Integer getVocNumber(CompleteTextElementType attribute) {
		ArrayList<CompleteOperationalValueType> Shows = attribute.getShows();
		for (CompleteOperationalValueType show : Shows) {	
			if (show.getView().equals(StaticNamesOda2.VOCABULARY))
			{
					if (show.getName().equals(StaticNamesOda2.VOCNUMBER))
						try {
							return Integer.parseInt(show.getDefault());
						} catch (Exception e) {
							return null;
						}	

			}
		}
		return null;
	}
	
	
	public static Boolean getVocCompartido(CompleteTextElementType attribute) {
		ArrayList<CompleteOperationalValueType> Shows = attribute.getShows();
		for (CompleteOperationalValueType show : Shows) {	
			if (show.getView().equals(StaticNamesOda2.VOCABULARY))
			{
					if (show.getName().equals(StaticNamesOda2.COMPARTIDO))
						try {
							return Boolean.parseBoolean(show.getDefault());
						} catch (Exception e) {
							return true;
						}	

			}
		}
		return true;
	}








	public static boolean isControled(CompleteElementType hastype) {
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {	
			if (show.getView().equals(StaticNamesOda2.METATYPE))
			{
				if (show.getName().equals(StaticNamesOda2.METATYPETYPE))
						if (show.getDefault().equals(StaticNamesOda2.CONTROLED)) 
										return true;
			}
		}
		return false;
	}



	public static boolean isAURL(CompleteDocuments recursoAProcesarC, List<CompleteGrammar> Lista) {
		
		for (CompleteGrammar completeGrammar : Lista) {
			if (isInGrammar(recursoAProcesarC, completeGrammar)&&isURL(completeGrammar))
				return true;
		}
	return false;
	}



	public static boolean isURL(CompleteGrammar document) {
		ArrayList<CompleteOperationalValueType> Shows = document.getViews();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.URL)) 
										return true;
			}
		}
		return false;
	}



	public static CompleteResourceElement findMetaValueUri(
			List<CompleteElement> description) {
		for (CompleteElement completeElement : description) {
			if (completeElement instanceof CompleteResourceElement&&StaticFuctionsOda2.isURI(completeElement.getHastype()))
				return (CompleteResourceElement) completeElement;
		}
		return null;
	}


	/**
	 * Revisa si un elemento es FileResource
	 * @param hastype
	 * @return
	 */
	public static boolean isURI(CompleteElementType hastype) {
		
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{
					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.URI)) 
										return true;
			}
		}
		return false;
	}



	public static Integer getIDODAD(CompleteElementType attribute) {
		ArrayList<CompleteOperationalValueType> Shows = attribute.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.PRESNTACION))
			{
					if (show.getName().equals(StaticNamesOda2.OdaID))
						try {
							Integer I=Integer.parseInt(show.getDefault());
								return I;
						} catch (Exception e) {
							return null;
						}
						
			}
		}
		return null;
		
	}



	public static boolean isIDOV(CompleteTextElementType hastype) {
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{

					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.IDOV)) 
										return true;
			}
		}
		return false;
	}



	public static void findPresentacionYCompleta(
			CompleteElementType attribute,Integer IdovNuevo) {
		ArrayList<CompleteOperationalValueType> Shows = attribute.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.PRESNTACION))
			{
				boolean found=false;

					if (show.getName().equals(StaticNamesOda2.OdaID))
						try {
							Integer.parseInt(show.getDefault());
								found=true;
						} catch (Exception e) {
							
						}

				
				if (!found)
					attribute.getShows().add(new CompleteOperationalValueType(StaticNamesOda2.OdaID, Integer.toString(IdovNuevo), show.getView()));
			}
		}
	}



	public static boolean isIgnored(CompleteElementType hastype) {
		ArrayList<CompleteOperationalValueType> Shows = hastype.getShows();
		for (CompleteOperationalValueType show : Shows) {
			
			if (show.getView().equals(StaticNamesOda2.META))
			{

					if (show.getName().equals(StaticNamesOda2.TYPE))
						if (show.getDefault().equals(StaticNamesOda2.IGNORED)) 
										return true;

			}
		}
		return false;
	}
	
	public static boolean isInGrammar(CompleteDocuments iterable_element,
			CompleteGrammar completeGrammar) {
		HashSet<Long> ElemT=new HashSet<Long>();
		for (CompleteElement dd : iterable_element.getDescription()) {
			ElemT.add(dd.getHastype().getClavilenoid());
		}
		
		return isInGrammar(ElemT, completeGrammar.getSons());
		
		
	}



	private static boolean isInGrammar(HashSet<Long> elemT,
			List<CompleteStructure> sons) {
		for (CompleteStructure CSlong1 : sons) {
			if (elemT.contains(CSlong1.getClavilenoid())||isInGrammar(elemT, CSlong1.getSons()))
				return true;
			
		}
		return false;
	}



	
}
