package plantUML.diagram;

public abstract class CreateUML {
	
	protected static final String NULL_BACKGROUND = "#FFFFFF";
	protected static final String NULL_LINES = "#BDBDBD";
	
	
	protected static final String ENTR = "\r\n";
	
	protected static final String START = "@startuml" + ENTR + ENTR;
	protected static final String CLASS_NULL = "<<???>>";
	
	protected static final String START_CLASS_DIAGRAM = START + "skinparam object {" + ENTR + "BackgroundColor" + CLASS_NULL + " " + NULL_BACKGROUND + ENTR + "BorderColor"
			+ CLASS_NULL + " " + NULL_LINES + ENTR + "}"
			+ ENTR + "skinparam nodeSep 30 " + ENTR /////// configura espacio horizontal
			+ "skinparam rankSep 30 " + ENTR /////// configura espacio vertical
			+ "hide empty members   " + ENTR; /////// oculta la cajita de atributos si está vacía
	protected static final String END = ENTR + "@enduml";

	public abstract String createUML();
}
