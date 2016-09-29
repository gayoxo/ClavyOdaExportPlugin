/**
 * 
 */
package fdi.ucm.server.exportparser.oda2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fdi.ucm.server.modelComplete.CompleteImportRuntimeException;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteLogAndUpdates;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteIterator;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteStructure;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * @author Joaquin Gayoso-Cabada
 *
 */
public class SaveProcessMainOdA2NoOverwrite extends
		SaveProcessMainOdA2 {

	
	HashMap<Integer, ArrayList<CompleteStructure>> VocabulariosElement;
	
	
	
	public SaveProcessMainOdA2NoOverwrite(CompleteCollection coleccion,
			CompleteLogAndUpdates cL, String pathGeneral,boolean ReturnsIds) {
		super(coleccion, cL, pathGeneral,ReturnsIds);
		VocabulariosElement=new HashMap<Integer,ArrayList<CompleteStructure>>();
	}

	@Override
	protected void processOV(List<CompleteDocuments> list)
			throws CompleteImportRuntimeException {
		for (CompleteDocuments resources : list) {
			if (StaticFuctionsOda2.isAVirtualObject(resources))
				saveOV(resources);
		}
		
		for (Entry<CompleteDocuments, Integer> completeDocuments : tabla.entrySet()) {
			procesa_Atributos(completeDocuments.getKey(),completeDocuments.getValue());
		}
	}
	
	@Override
	public void preocess() throws CompleteImportRuntimeException {
		
		ProcessValidacion();

		
		ArrayList<CompleteStructure> DatosYMeta=new ArrayList<CompleteStructure>();
		
		
		ArrayList<CompleteElementType> MetaDatos=findMetaDatos();
		if (MetaDatos!=null)
			DatosYMeta.addAll(MetaDatos);
		
		//buscar el IDOV
		removeIGNOREDC(MetaDatos);
		
		
		IDOV=findIdov();
		
		processModeloIniciales(DatosYMeta);	
		
		processOV(toOda.getEstructuras());
	}
	
	
	

	@Override
	protected void rellenaTablaVocabularios(List<CompleteStructure> list) {
		for (CompleteStructure attribute : list) {
			if (attribute instanceof CompleteElementType)
				if (attribute instanceof CompleteTextElementType&&StaticFuctionsOda2.isControled((CompleteTextElementType)attribute))
				{
					Integer Numero=StaticFuctionsOda2.getVocNumber((CompleteTextElementType) attribute);
					Boolean Compartido=StaticFuctionsOda2.getVocCompartido((CompleteTextElementType) attribute);
					if (Numero!=null&&Compartido)
							{
							Vocabularios.add(Numero);
							ArrayList<CompleteStructure> listaAc=VocabulariosElement.get(Numero);
							if (listaAc==null)
								listaAc=new ArrayList<CompleteStructure>();
							if (!listaAc.contains(attribute))
								listaAc.add(attribute);
							VocabulariosElement.put(Numero,listaAc);
							}

				}
			rellenaTablaVocabularios(attribute.getSons());
		}
	}
	
	@Override
	protected void processModelo(List<CompleteStructure> list, int padre)
			throws CompleteImportRuntimeException {
		
		int pos = getOrden(padre);
		
		for (CompleteStructure Cattribute : list) {
			
//			int pos = getOrden(padre);
			
			if (Cattribute instanceof CompleteIterator)
				throw new CompleteImportRuntimeException(EXISTE_ERROR_EN_EL_PARSEADO_DE_LAS_ITERACIONES);
			else{
				CompleteElementType attribute=(CompleteElementType) Cattribute;
			
				
			Integer Salida=	StaticFuctionsOda2.getIDODAD(attribute);	
				
			if (Salida==null)
			{
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
			
			Salida=2;
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
						Salida= MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'C', 'S', '"+catalogocomp+"');");
					}
					else {
						if (Vocabularios.contains(vocabularioN))
							{
							catalogocomp="1";
							Salida= MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'C', 'S', '"+catalogocomp+"');");
							VocabulariosSalida.put(vocabularioN,Salida );
							}
						else {
						catalogocomp="0";
						Salida= MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'C', 'S', '"+catalogocomp+"');");
						}
					}
					}
				else
					Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'T', '"+Extensible+"', '0');");
			}

			else {
			
				Salida=MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `section_data` (`idpadre`, `nombre`, `visible`,`orden`, `browseable`, `tipo_valores`, `extensible`, `vocabulario`) VALUES ('"+padre+"','"+Name+"', '"+Visible+"','"+pos+"','"+Browser+"' , 'X', '"+Extensible+"', '0');");
			}
			
			
			StaticFuctionsOda2.findPresentacionYCompleta(attribute,Salida);
			ColectionLog.getNuevosOperationalViewStructure().add(attribute);
			
			}
			ModeloOda.put(attribute, Salida);
			processModelo(attribute.getSons(),Salida);
			pos++;	
			}
		} 
	}
	
	@Override
	protected void saveOV(CompleteDocuments ObjetoDigital)
			throws CompleteImportRuntimeException {
		
		
		Integer IDOV=buscaIDOV(ObjetoDigital);
		
		
		
		
		if (IDOV!=null)
		{
			boolean Public=StaticFuctionsOda2.getPublic(ObjetoDigital);
			boolean Private=StaticFuctionsOda2.getPrivate(ObjetoDigital);
			
			String SPublic="N";
			if (Public)
				SPublic="S";
			
			String SPrivate="N";
			if (Private)
				SPrivate="S";
			
					
			MySQLConnectionOdA2.RunQuerryUPDATE("UPDATE `virtual_object` SET `ispublic`='"+SPublic+"' WHERE id='"+IDOV+"';");
			MySQLConnectionOdA2.RunQuerryUPDATE("UPDATE `virtual_object` SET `isprivate`='"+SPrivate+"' WHERE id='"+IDOV+"';");
			
			deleteValues(IDOV);
			
			tabla.put(ObjetoDigital,IDOV);
			procesa_descripcion(ObjetoDigital,IDOV);
			procesa_iconos(ObjetoDigital,IDOV);
		}
		else{

		try{
//			Integer Idov=StaticFuctionsOda2.findIdov(ObjetoDigital);
			boolean Public=StaticFuctionsOda2.getPublic(ObjetoDigital);
			boolean Private=StaticFuctionsOda2.getPrivate(ObjetoDigital);
			
			String SPublic="N";
			if (Public)
				SPublic="S";
			
			String SPrivate="N";
			if (Private)
				SPrivate="S";
			
					
			int Salida = MySQLConnectionOdA2.RunQuerryINSERT("INSERT INTO `virtual_object` (`ispublic`,`isprivate`) VALUES ('"+SPublic+"','"+SPrivate+"');");
			
			if (ReturnIDs)
			{
			CompleteTextElement TT=new CompleteTextElement(this.IDOV, Integer.toString(Salida));
			TT.setDocumentsFather(ObjetoDigital);
			ColectionLog.getNuevosElementos().add(TT);
			}
			
			tabla.put(ObjetoDigital,Salida);
			procesa_descripcion(ObjetoDigital,Salida);
			procesa_iconos(ObjetoDigital,Salida);
			
			
			}catch (NumberFormatException e)
			{
				throw new CompleteImportRuntimeException("Los Identificadores no son numeros.");
			}
		}
	}

	private void deleteValues(Integer iDOV2) {
		MySQLConnectionOdA2.RunQuerry("DELETE FROM `text_data` WHERE idov='"+iDOV2+"';");
		MySQLConnectionOdA2.RunQuerry("DELETE FROM `controlled_data` WHERE idov='"+iDOV2+"';");
		MySQLConnectionOdA2.RunQuerry("DELETE FROM `date_data` WHERE idov='"+iDOV2+"';");
		MySQLConnectionOdA2.RunQuerry("DELETE FROM `numeric_data` WHERE idov='"+iDOV2+"';");
		MySQLConnectionOdA2.RunQuerry("DELETE FROM `resources` WHERE idov='"+iDOV2+"';");
		
	}

	private Integer buscaIDOV(CompleteDocuments objetoDigital) {
		for (CompleteElement iterable_element : objetoDigital.getDescription()) {
			if (iterable_element instanceof CompleteTextElement && iterable_element.getHastype()==IDOV)
				try {
					return Integer.parseInt(((CompleteTextElement)iterable_element).getValue());
				} catch (Exception e) {
					//ignorado, ya devolvera null si no hay otro
				}
		}
		return null;
	}
}
