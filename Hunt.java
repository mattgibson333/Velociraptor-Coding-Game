/**
 * Describes a "person hunt", in which a velociraptor moves around a field
 * containing bushes, looking for a person. The person, of course,
 * tries not to be caught by the velociraptor.
 * 
 * @author David Matuszek
 * @version October 12, 2001
 */
public class Hunt {

    // class variables
    private static Object[][] field;
    private static Model model;
    private static View view;
    private static Controller controller;
    private static int numberOfRows;
    private static int numberOfColumns;

    /**
     * Main class for starting a person hunt; no parameters
     * are needed or used.
     */
    public static void main(String args[]) {
        numberOfRows = 20;
        numberOfColumns = 20;
        field = new Object[numberOfRows][numberOfColumns];
        model = new Model(field);
        view = new View(field);
        controller = new Controller(model, view);
    }
}
