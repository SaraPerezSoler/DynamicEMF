package dynamicEMF;


import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Sara Perez Soler
 * This is an example of how to create a Store metamodel using the EMF libraries
 * This main requires the following jars:
 * - org.eclipse.emf.common
 * - org.eclipse.emf.ecore.xmi
 * - org.eclipse.emf.ecore
 * */
public class CreateMetamodel {

	public static void main(String[] args) {
		
		// First we create the EPackage, the root element in the metamodel
		// To create all the EObjects of the metamodel it is necessary use EcoreFactory
		// We set the name, nsPrefix and nsUri in the package
		EPackage package_ = EcoreFactory.eINSTANCE.createEPackage();
		package_.setName("storePackage");
		package_.setNsPrefix("storePackage");
		package_.setNsURI("storePackage");
		
		// We create the EClass Store, and set the name
		EClass store = EcoreFactory.eINSTANCE.createEClass();
		store.setName("Store");
		
		//An EAttribute called name, with type EString and cardinality 1..1 
		EAttribute storeName = EcoreFactory.eINSTANCE.createEAttribute();
		storeName.setName("name");
		storeName.setEType(EcorePackage.eINSTANCE.getEString());
		storeName.setLowerBound(1);
		storeName.setUpperBound(1);
		
		//We save the EAttribute name in the Store EClass. The EClasses has an a list of EStrucutralFeatures, which can be EAttributes or EReferences 
		store.getEStructuralFeatures().add(storeName);
		
		//An EClass Item
		EClass item = EcoreFactory.eINSTANCE.createEClass();
		item.setName("Item");
		
		//Another EAttribute calls name, with type EString and cardinality 1..1. 
		//Although there is already an object EAttribute called name with the same properties, it is necessary one object different for each EClass who need it. 
		EAttribute itemName = EcoreFactory.eINSTANCE.createEAttribute();
		itemName.setName("name");
		itemName.setEType(EcorePackage.eINSTANCE.getEString());
		itemName.setUpperBound(1);
		itemName.setLowerBound(1);
		
		//Save the name EAttribute in the Item EClass
		item.getEStructuralFeatures().add(itemName);
		
		//An EAttribute with name price, type double and cardinality 1..1
		EAttribute itemPrice = EcoreFactory.eINSTANCE.createEAttribute();
		itemPrice.setName("price");
		itemPrice.setEType(EcorePackage.eINSTANCE.getEDouble());
		itemPrice.setUpperBound(1);
		itemPrice.setLowerBound(1);
		
		//Save the price EAttribute in Item EClass
		item.getEStructuralFeatures().add(itemPrice);
		
		//Create an EReference with name items, the type is the EClass Item and the cardinality 0..*, also it is a containment reference
		EReference items = EcoreFactory.eINSTANCE.createEReference();
		items.setName("items");
		items.setEType(item);
		items.setLowerBound(0);
		items.setUpperBound(-1);
		items.setContainment(true);
		
		//Save the items EReference in the Store EClass
		store.getEStructuralFeatures().add(items);
		
		//Save Store and Item EClasses in the EPackege
		package_.getEClassifiers().add(store);
		package_.getEClassifiers().add(item);
		
		
		/*****************************************************************************************************************************/
		/** So far the metamodel has been created, now we are going to save it in a ecore file (using a resource in EMF)             */            
		/*****************************************************************************************************************************/
		
		// First we need to create and configure a ResourseSet.
		ResourceSet set = new ResourceSetImpl();
		//In the configuration we are going to registry the XMLResourceFactory for the ecore extension
		//That means all files (or resources) with extension ecore are going to be serialize and deserialize (save and read) in a XML format 
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
		
		//Using the ResourceSet, we create the resource in a concrete path (the folder of the path must be created before). 
		//It is also possible open a resource, but createResource method creates a new one.
		Resource resource = set.createResource(URI.createURI("./models/Store.ecore"));
		
		//We add the root element in the resource content
		resource.getContents().add(package_);
		
		//And save the resource.
		try {
			resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//NOTE: all element which are not saved directly or indirectly in the root element of the metamodel, are no saved in the resource
		
	}

}
