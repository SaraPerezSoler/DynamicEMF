package dynamicEMF;
import java.io.IOException;

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
 * 
 * This is an example of how to instantiate the Store metamodel using the EMF libraries 
 * (see CreateMetamodel.java to create it, and ReadMetamodel.java to read it)
 * This main requires the following jars: 
 *  - org.eclipse.emf.common 
 *  - org.eclipse.emf.ecore.xmi 
 *  - org.eclipse.emf.ecore
 */
public class CreateModel {

	public static void main(String[] args) {
		
		// To read a metamodel, we need to open a ecore file (using a resource in EMF)
		// For it we need to create an configure a ResourceSet
		ResourceSet set = new ResourceSetImpl();
		// In the configuration we are going to registry the XMLResourceFactory for the
		// ecore extension
		// That means all files (or resources) with extension ecore are going to be
		// serialize and deserialize (save and read) in a XML format
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());

		// Using the ResourceSet, we open a resource in a concrete path (the file must
		// be exist previously).
		Resource resource = set.getResource(URI.createURI("./models/Store.ecore"), true);

		// Then we get the first element of the content in the resource, which is an
		// EPackage and the root element
		EPackage storePackage = (EPackage) resource.getContents().get(0);
		
		//To instantiate the model it is necessary registry the EPackage
		if (!EPackage.Registry.INSTANCE.containsKey(storePackage.getNsURI())) {
			EPackage.Registry.INSTANCE.put(storePackage.getNsURI(), storePackage);
		}
		
		//We can find the metamodel elements by name, like the EClass Store and the EAttribute name of the Store EClass
		EClass store = (EClass) storePackage.getEClassifier("Store");
		EAttribute storeName = (EAttribute) store.getEStructuralFeature("name");
		
		//Using the metamodels elements, we can create model instances
		//In this case, we create an object from the EClass Store, and set the EAttribute value as a Name
		//To create the elements we needs the EFactoryIntance of the EPackage
		EObject storeObject = storePackage.getEFactoryInstance().create(store);
		storeObject.eSet(storeName, "MyStore");
		
		
		//Here, we find the EClass Item and its EStructuralFeatures (EAttributes in this case) name and price
		EClass item = (EClass) storePackage.getEClassifier("Item");
		EAttribute itemName = (EAttribute) item.getEStructuralFeature("name");
		EAttribute itemPrice = (EAttribute) item.getEStructuralFeature("price");
		
		//Here, we create an Item Book (using its EClass) and set the name and the price (using the EAttributes) 
		EObject itemObject = storePackage.getEFactoryInstance().create(item);
		itemObject.eSet(itemName, "Book");
		itemObject.eSet(itemPrice, 10.0);
		
		//Here, we create an Item Pencil Box (using its EClass) and set the name and the price (using the EAttributes) 
		EObject itemObject2 = storePackage.getEFactoryInstance().create(item);
		itemObject2.eSet(itemName, "Pencil box");
		itemObject2.eSet(itemPrice, 2.5);
		
		//As we did previously, using its name, we find the items EReference of the Store EClass
		EReference items = (EReference) store.getEStructuralFeature("items");
		
		//We want to save items Book and Pencil Box in the reference items of the MyStore object
		//Previously we use the method eSet of the EObjects, but in these cases, the cardinality was [1..1]
		//In the items case, the cardinality is [0..*], that means items is a list, so to save element, 
		//we need to take the list (method eGet and then cast to EList<EObject>) and then add the elements, as we can see following
		((EList<EObject>)storeObject.eGet(items)).add(itemObject);
		((EList<EObject>)storeObject.eGet(items)).add(itemObject2);
		
		
		/*****************************************************************************************************************************/
		/** So far the model has been created, now we are going to save it in a xmi file (using a resource in EMF)                   */            
		/*****************************************************************************************************************************/
		
		//We use the same resourceSet that we create before, but need to add the configuration to serialize and deserialize (save and read) xmi files in a XML format
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		//Then we create the resource
		Resource modelResource = set.createResource(URI.createURI("./models/MyStore.xmi"));
		//We add the root element (in this case is the Store object MyStore) in the resource content
		modelResource.getContents().add(storeObject);
		
		//And finally we save the resource
		try {
			modelResource.save(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
