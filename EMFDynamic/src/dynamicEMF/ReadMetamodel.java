package dynamicEMF;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Sara Perez Soler 
 * This is an example of how to read the Store metamodel using the EMF libraries (see CreateMetamodel.java to create it) 
 * This main requires the following jars: 
 *   - org.eclipse.emf.common 
 *   - org.eclipse.emf.ecore.xmi 
 *   - org.eclipse.emf.ecore
 */
public class ReadMetamodel {

	public static void main(String[] args) {

		// To read a metamodel, we need to open a ecore file (using a resource in EMF)
		// For it we need to create an configure a ResourceSet
		ResourceSet set = new ResourceSetImpl();
		// In the configuration we are going to registry the XMIResourceFactory for the
		// ecore extesion
		// That means all files (or resources) with extension ecore are going to be
		// serialize and deserialize (save and read) in a XMI format
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());

		// Using the ResourceSet, we open a resource in a concrete path (the file must
		// be exist previously).
		Resource resource = set.getResource(URI.createURI("./models/Store.ecore"), true);

		// Then we get the first element of the content in the resource, which is an
		// EPackage and the root element
		EPackage storePackage = (EPackage) resource.getContents().get(0);

		// We can find the EClass by name in the EPackage
		EClass store = (EClass) storePackage.getEClassifier("Store");
		// Also we can find EStructuralFeatures (EAttributes and ERefernces) by name in
		// each EClass
		EAttribute storeName = (EAttribute) store.getEStructuralFeature("name");

		System.out.println("The EClass " + store.getName() + " and the EAttribute " + storeName.getName()
				+ " have been found by name");

		// But also, we can see what the model have generically (without knowing the
		// names)
		System.out.println("\nMetamodel elements: ");

		//For example, get all the EClassifiers
		List<EClassifier> elements = storePackage.getEClassifiers();
		//Going through the list
		for (EClassifier classifier : elements) {
			//Taking the EClass elements
			if (classifier instanceof EClass) {
				EClass eClass = (EClass) classifier;
				System.out.println("* EClass " + eClass.getName());
				System.out.println("  - EAttributes:");
				//Taking EAttributes of the eClass
				for (EAttribute eAttribute : eClass.getEAllAttributes()) {
					System.out.println("\t " + eAttribute.getName() + " : " + eAttribute.getEType().getName() + " ["
							+ eAttribute.getLowerBound() + ".." + eAttribute.getUpperBound() + "]");
				}

				System.out.println("  - EReferences:");
				//Taking EReferences of the eClass
				for (EReference eReference : eClass.getEAllReferences()) {
					System.out.println("\t " + eReference.getName() + " : " + eReference.getEType().getName() + " ["
							+ eReference.getLowerBound() + ".." + eReference.getUpperBound() + "], containment="
							+ eReference.isContainment());
				}
			}
		}

		//NOTE: The first part of code (lines 43 to 50) only works for the store metamodel, becouse it use the concrete names of the metamodel elements, 
		//but the second part (lines 54 to 79) works for any metamodel 
	}
}
