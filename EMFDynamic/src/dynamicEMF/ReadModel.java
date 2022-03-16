package dynamicEMF;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Sara Perez Soler 
 * This is an example of how to instantiate the Store metamodel using the EMF libraries (see InstantiateModel.java to create it) 
 * This main requires the following jars: 
 *    - org.eclipse.emf.common 
 *    - org.eclipse.emf.ecore.xmi 
 *    - org.eclipse.emf.ecore
 */
public class ReadModel {

	public static void main(String[] args) {

		// To read a model, first we need to open the metamodel file (using a resource
		// in EMF) and registry the EPackage
		// For it we need to create an configure a ResourceSet
		ResourceSet set = new ResourceSetImpl();
		// In the configuration we are going to registry the XMIResourceFactory for the
		// ecore extension
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());

		// Using the ResourceSet, we open a resource in a concrete path (the file must
		// be exist previously).
		Resource resource = set.getResource(URI.createURI("./models/Store.ecore"), true);

		// Then we get the first element of the content in the resource, which is an
		// EPackage and the root element
		EPackage storePackage = (EPackage) resource.getContents().get(0);

		// Registry the EPackage
		if (!EPackage.Registry.INSTANCE.containsKey(storePackage.getNsURI())) {
			EPackage.Registry.INSTANCE.put(storePackage.getNsURI(), storePackage);
		}

		// Then we can open the model file (using a resource in EMF)
		// We use the same resourceSet that we create before, but need to add the
		// configuration to serialize and deserialize (save and read) xmi files in a XMI
		// format
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		// Then we open the resource
		Resource modelResource = set.getResource(URI.createURI("./models/MyStore.xmi"), true);
		
		EObject root = modelResource.getContents().get(0);
		
		System.out.println("The model has: ");
		//To print the model in a generic way, we create a printObject methods, which is called recursively
		printEObject(root, "");
		
		//NOTE: This code can be used with any model, you only need to change the metamodel and the model path
	}

	/**
	 * This method print a EObject informations (EClass, EAttributes and its values, and EReferences and it values)
	 * @param object, the EObject to be printed
	 * @param tab, a String to count the tabulators, to call it, this string must be an empty String ""
	 * */
	private static void printEObject(EObject object, String tab) {
		//First we take the EClass of the EObject
		EClass objectEClass = object.eClass();
		System.out.println(tab+"* " + objectEClass.getName() + " with:");
		
		if (!objectEClass.getEAllAttributes().isEmpty()) {
			System.out.println(tab+" - Attributes:");
			//Using the EClass we can take the EAttributes to take its value in the EObject (method eGet)
			for (EAttribute eAttribute : objectEClass.getEAllAttributes()) {
				//If the max cardinality of the EAttribute is different than 1, means that the EAttribute is a List, and we need to go through the list to take every element
				if (eAttribute.getUpperBound() != 1) {
					System.out.print(tab+"\t " + eAttribute.getName() + " = [");
					EList<Object> values = (EList<Object>) object.eGet(eAttribute);
					for (int i=0; i<values.size(); i++) {
						System.out.print(values.get(i));
						if (i != values.size()-1) {
							System.out.print(", ");
						}
					}
					System.out.println("]");
				//If the cardinality is 1, there is only one element to take
				}else {
					System.out.println(tab+"\t " + eAttribute.getName() + " = " + object.eGet(eAttribute));
				}
			}
		}
		//Using the EClass we can take the EReference to take its value in the EObject (method eGet)
		if (!objectEClass.getEAllReferences().isEmpty()) {
			System.out.println(tab+" - References:");
			for (EReference eRefrence : objectEClass.getEAllReferences()) {
				//If the max cardinality of the EReference is different than 1, means that the EReference is a List, and we need to go through the list to take every element
				if (eRefrence.getUpperBound() != 1) {
					for (EObject referenceObject : ((EList<EObject>) object.eGet(eRefrence))) {
						//We call recursively the method  for each EObject to read them 
						printEObject(referenceObject, tab +"\t");
					}
				//If the cardinality is 1, there is only one element to take
				} else {
					//We call recursively the method  to read the EObject
					printEObject((EObject) object.eGet(eRefrence), tab +"\t");
				}
			}
		}
	}
}
