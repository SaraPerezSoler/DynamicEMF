package plantUML.diagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
/**
 * @author Sara Perez Soler
 * This class creates the text necessary to create an PlantUML diagram from a Model 
 * - org.eclipse.emf.common
 * - org.eclipse.emf.ecore.xmi
 * - org.eclipse.emf.ecore
 *
 * */
public class CreateModelUML extends CreateUML{

	private List<EObject> model;
	private Map<EClass, Integer> eClass_id = new HashMap<EClass, Integer>();
	private Map<EObject, String> eObject_id = new HashMap<EObject, String>();

	
	/**
	 * 
	 * @param resource the model resource
	 */
	public CreateModelUML(Resource resource) {
		this.model =  getAllObjects(resource);
	}

	
	/**
	 * 
	 * @return The String to create the PlantUML diagram
	 */
	public String createUML() {
		String cad = START_CLASS_DIAGRAM;

		for (EObject object : model) {
			cad += getObjectText(object);
		}
		for (EObject object : model) {
			cad += getReferencesText(object);
		}
		cad += END;
		return cad;
	}
	
	private List<EObject> getAllObjects(Resource resource) {
		List<EObject> ret = new ArrayList<>();

		TreeIterator<EObject> iterator = resource.getAllContents();
		while (iterator.hasNext()) {
			EObject object = iterator.next();
			ret.add(object);
		}
		for (EObject object : resource.getContents()) {
			if (!ret.contains(object)) {
				ret.add(object);
			}
		}
		return ret;
	}

	private String getObjectText(EObject object) {

		String head = createObjectHead(object);

		String cad = "object " + head + ENTR;

		for (EStructuralFeature feature : object.eClass().getEStructuralFeatures()) {
			if (feature instanceof EAttribute) {
				cad += getAttributeText(object, (EAttribute) feature);
			} else {
				Object value = object.eGet(feature);
				if (value == null) {
					cad += getNullReference(object, (EReference) feature);

				}
			}
		}
		cad += ENTR;

		return cad;

	}

	private String getReferencesText(EObject object) {
		String cad = "";
		for (EStructuralFeature feature : object.eClass().getEStructuralFeatures()) {
			if (feature instanceof EReference) {
				Object value = object.eGet(feature);
				if (value != null) {
					cad += getReference(object, (EReference) feature, value) + ENTR;

				}
			}
		}
		return cad;
	}

	private String getObjectName(EObject object) {
		String eObjectId = getObjectId(object);
		String name1 = "\":" + object.eClass().getName() + "\" as " + eObjectId + " ";
		return name1;
	}
	
	private String getObjectId(EObject object) {
		String objectId = eObject_id.get(object);
		if (objectId == null) {
			Integer id = eClass_id.get(object.eClass());
			if (id == null) {
				id = 0;
			}
			objectId = object.eClass().getName()+id;
			eObject_id.put(object, objectId);
			id++;
			eClass_id.put(object.eClass(), id);
		}
		return objectId;
	}
	

	private String createObjectHead(EObject cc) {
		return getObjectName(cc);
	}

	private String getAttributeText(EObject object, EAttribute ac) {

		String cad = "";
		String cadFin = ENTR;

		String value;
		Object val = object.eGet(ac);
		if (val != null) {
			value = val.toString();
		} else {
			value = "null ";
		}
		cad += getObjectId(object)+" : "+ ac.getName() + " = " + value + cadFin;
		return cad;

	}
	

	private String getNullReference(EObject object, EReference feature) {
		String cad = "";
		String cadFin = ENTR;

		cad += "Ref: " + feature.getName() + " = " + null + cadFin;
		return cad;
	}

	private String getReference(EObject object, EReference rc, Object value) {

		String cad = "";
		String line = "--";

		String nameStart = "";
		String nameEnd = ENTR;

		if (rc.isContainment()) {
			line = " *" + line + "";
		} else {
			line = " " + line + ">";
		}
		
		String parentName = getObjectId(object);
		if (value instanceof List<?>) {
			for (Object obj : (List<?>) value) {
				EObject valueControl = (EObject) obj;
				cad += nameStart + parentName + line + " " + getObjectId(valueControl) + " :\":" + rc.getName() + "\""
						+ nameEnd;
			}
		} else {
			EObject valueControl = (EObject) value;
			cad += nameStart + parentName + line + " " + getObjectId(valueControl) + " :\":" + rc.getName() + "\""
					+ nameEnd;
		}

		return cad;
	}

}
