package plantUML.diagram;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;


/**
 * @author Sara Perez Soler
 * This class creates the text necessary to create an PlantUML diagram from a Metamodel 
 * - org.eclipse.emf.common
 * - org.eclipse.emf.ecore.xmi
 * - org.eclipse.emf.ecore
 *
 * */
public class CreateMetamodelUML extends CreateUML{


	private boolean nullReference = false;

	private EPackage pc;

	/**
	 * 
	 * @param pck Metamodel package
	 */
	public CreateMetamodelUML(EPackage pck) {
		this.pc = pck;
	}

	/**
	 * 
	 * @return The String to create the PlantUML diagram
	 */
	public String createUML() {
		String cad = START_CLASS_DIAGRAM;
		for (EClassifier cc : pc.getEClassifiers()) {
			cad += getClassText(cc);
		}
		cad += END;
		return cad;
	}
	private String getClassText(EClassifier c) {
		if (!(c instanceof EClass)) {
			return "";
		}
		EClass cc = (EClass) c;
		String cad = "";
		String head=createClassHead(cc);
		String name=getClassName(cc);


		if (cc.isInterface()) {
			cad += "interface " + head + " {" + ENTR;
		} else {
			if (cc.isAbstract()) {
				cad += "abstract ";
			}
			cad += "class " + head + " {" + ENTR;
		}
		
		
		for (EAttribute ac : cc.getEAllAttributes()) {
			cad += getAttributeText(ac);
		}
		cad += "}" + ENTR;

		for (EClass superType : cc.getEAllSuperTypes()) {
				cad += superType.getName() + " <|-- " + name + ENTR;
		}
		
		
		nullReference = false;
		for (EReference rc : cc.getEAllReferences()) {
			cad += getReference(rc);
		}
		if (nullReference) {
			cad += ENTR + "class \"Ø\" <<(?," + NULL_LINES + ") ???>>{" + ENTR + "}" + ENTR;
		}

		return cad;
	}
	
	private String getClassName(EClass cc){
		String name1 = cc.getName();
		
		if (name1.equals("Class")) {
			name1 = "\"" + cc.getName() + "\"";
		}
		return name1;
	}
	
	private String createClassHead(EClass cc){		
			
		return  getClassName(cc);
	}
	
	
	

	private String getAttributeText(EAttribute ac) {
	
		String cad = "";
		String cadFin = ENTR;

		String type;
		if (ac.getEType() != null) {
			type = ac.getEType().getInstanceClass().getSimpleName() + " ";
		} else {
			type = "?? ";
		}

		cad += "[" + ac.getLowerBound() + ",";
		if (ac.getUpperBound() == -1) {
			cad += "* ] ";
		} else {
			cad += ac.getUpperBound() + "] ";
		}
		cad += ac.getName() + ": " + type + cadFin;
		return cad;

	}

	private String getReference(EReference rc) {
		String cad = "";
		String line = "--";
		
		String nameStart = "";
		String nameEnd = ENTR;


		String parentName = "";
		if (rc.eContainer() == null) {
			parentName = "null";
		} else {
			parentName = ((EClass)rc.eContainer()).getName();
			if (parentName.equals("Class")) {
				parentName = "\"" + parentName + "\"";
			}
		}
		if (rc.isContainment()) {
			cad += parentName + " *" + line + "\"" + rc.getLowerBound() + "..";
		} else {
			cad += parentName + " " + line + ">\"" + rc.getLowerBound() + "..";
		}

		if (rc.getUpperBound() == -1) {
			cad += "*";
		} else {
			cad += rc.getUpperBound();
		}
		cad += "\"";
		EClassifier type = rc.getEType();
		if (type != null) {
			cad += type.getName() + " : ";
		} else {
			cad += "Ø : ";
			nullReference = true;
		}
		cad += nameStart+rc.getName() + nameEnd;
		return cad;
	}

}
