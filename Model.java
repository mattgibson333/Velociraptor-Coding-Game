import java.util.*;

/**
 * Creates and maintains a "field" for the person and velociraptor, and sets
 * up a number of methods to be used by animals. This class, rather
 * than the Animal class, is in charge of letting animals move and
 * look around--this makes it harder for an animal to "cheat" by
 * knowing things it shouldn't know, or making moves it shouldn't
 * be allowed to make.
 * 
 * @author David Matuszek 
 * @version October 26, 2001
 */
public class Model {
    
    // define instance variables
    private Object[][] field;
    private int numberOfRows;
    private int numberOfColumns;
    private Person person;
    private Velociraptor velociraptor;
    private int velociraptorRow;
    private int velociraptorColumn;
    private int personRow;
    private int personColumn;
    private Bush bush; // all bushes are the same bush
    private long oldRandomSeed;
    private static Random randomNumberGenerator = new Random();
    private boolean isUnderConstruction;
    
    /** Flag used to end the game when the velociraptor wins */
    boolean personIsAlive;
    /** Flag to tell when the game ends */
    boolean gameIsOver;
    /** The number of turns taken so far in the game */
    int stepsTaken;
    /** The number of turns the person has to survive in order to win */
    final int MAX_NUMBER_OF_STEPS = 100;
    /** The number of rows in the field */
    static int NUMBER_OF_ROWS;
    /** The number of columns in the field */
    static int NUMBER_OF_COLUMNS;
    /** Remember each time animal does look(direction) for later viewing */
    static Vector looks = new Vector();
    /** True if person's turn, false if velociraptor's turn */
    static boolean isPersonsTurn;

    // define some class constants to represent directions
    /** Represents the direction NORTH */
        static final int N = 0;
    /** Represents the direction NORTHEAST */
        static final int NE = 1;
    /** Represents the direction EAST */
        static final int E = 2;
    /** Represents the direction SOUTHEAST */
        static final int SE = 3;
    /** Represents the direction SOUTH */
        static final int  S = 4;
    /** Represents the direction SOUTHWEST */
        static final int SW = 5;
    /** Represents the direction WEST */
        static final int W = 6;
    /** Represents the direction NORTHWEST */
        static final int NW = 7;
    /** Represents the direction "right here" */
        static final int STAY = 8;
    /** The smallest int representing a direction */
        static final int MIN_DIRECTION = 0;
    /** The largest int representing a direction */
        static final int MAX_DIRECTION = 7;

    // define some constants to represent objects
    /** Indicates the edge of the playing field */
        final static int EDGE = 0;
    /** Indicates a bush */
        final static int VELOCIRAPTOR = 1;
    /** Indicates a person */
        final static int PERSON = 2;
    /** Indicates a velociraptor */
        final static int BUSH = 3;
    
    /**
     * Constructs a model that uses the given field.
     *
     * @param field  the field to be used by the model
     */
    Model(Object[][] field) {
        this.field = field;
        NUMBER_OF_ROWS = numberOfRows = field.length;
        NUMBER_OF_COLUMNS = numberOfColumns = field[0].length;
        reset();
    }
    
    /**
     * Sets up a new hunt.
     */
    void reset() {
        gameIsOver = false;
        personIsAlive = true;
        isPersonsTurn = false; // will be changed before first move
        stepsTaken = 0;
        // populate using new random numbers
        oldRandomSeed = randomNumberGenerator.nextLong();
        randomNumberGenerator.setSeed(oldRandomSeed);
        populate();
    }
    
    /**
     * Sets up the same hunt all over again, using old random seed.
     */
    void replay() {
        gameIsOver = false;
        personIsAlive = true;
        isPersonsTurn = false; // will be changed before first move
        stepsTaken = 0;
        // populate using same random numbers as last time
        randomNumberGenerator.setSeed(oldRandomSeed);
        populate();
    }

    /**
     * Puts a person, a velociraptor, and some bushes in the field.
     */
    void populate () {
    
       // protect against calls during creation of game
       isUnderConstruction = true;
       
       // create some abbreviations, just to save some typing
       int numRows = numberOfRows;
       int numCols = numberOfColumns;
       
       // remove any previous contents of field
       for (int i = 0; i < numRows; i++)
           for (int j = 0; j < numCols; j++)
               field[i][j] = null;
       
       // put the person in a random location
       personRow = random(0, numRows - 1);
       personColumn = random(0, numCols - 1);
       isPersonsTurn = true; // so error messages can place the blame
       person = new Person(this, personRow, personColumn);
       field[personRow][personColumn] = person;
       
       // put the velociraptor in a random location, not too close to the person
       int distance;
       do {
           velociraptorRow = random(0, numRows - 1);
           velociraptorColumn = random(0, numCols - 1);
           distance = Math.max(Math.abs(velociraptorRow - personRow),
                               Math.abs(velociraptorColumn - personColumn));
       } while (distance < (numRows + numCols) / 4);
       isPersonsTurn = false; // so error messages can place the blame
       velociraptor = new Velociraptor(this, velociraptorRow, velociraptorColumn);
       field[velociraptorRow][velociraptorColumn] = velociraptor;
       
       // put in some random bushes
       // (since bushes don't do anything, we cheat and use the same bush)
       bush = new Bush();
       int numberOfBushes = (numRows * numCols) / 20;
       for (int i = 0; i < numberOfBushes; i++) {
           int bushRow = random(0, numRows - 1);
           int bushColumn = random(0, numCols - 1);
           if (field[bushRow][bushColumn] == null) {
               field[bushRow][bushColumn] = bush;
           }
           else i--;
       }
       
       // finish
       isUnderConstruction = false;
    }
    
    /**
     * Gives one animal a chance to move.
     *
     */
    void allowSingleMove() {
        Animal animal;
        int direction;
        int newRow;
        int newColumn;
        
        // make sure it's legal to allow moves
        if (gameIsOver) return;
        
        // prepare to save info about looks (for later use by view)
        looks.clear();
        
        // decide whose turn it is now (change isPersonsTurn)
        isPersonsTurn = !isPersonsTurn;
        if (isPersonsTurn) {
            animal = person;
        }
        else { // velociraptor's turn
            animal = velociraptor;
        }
        
        // ask the animal to decide a direction
        direction = animal.decideMove();
        
        // if move is legal, do it
        if (direction != STAY) {
            newRow = animal.row + rowChange(direction);
            newColumn = animal.column + columnChange(direction);
            if (legalLocation(newRow, newColumn) &&
                    !(field[newRow][newColumn] instanceof Bush)) {
                moveAnimal(animal, newRow, newColumn);
            }
        }
        
        // check whether move was fatal for person
        if (person.row == velociraptor.row && person.column == velociraptor.column) {
            personIsAlive = false;
            gameIsOver = true;
        }
        
        // increment steps taken; check for end of game after velociraptor's turn
        if (isPersonsTurn) {
            stepsTaken++;
        }
        else if (stepsTaken >= MAX_NUMBER_OF_STEPS) {
            gameIsOver = true;
        }
    }
            
    /**
     * Gives the person a chance to move, then gives the velociraptor
     * a chance to move.
     */
    void allowMoves() {
        allowSingleMove();
        allowSingleMove();
    }
    
    /**
     * Utility method to absolutely move an animal from
     * one location to another. Any error checking must
     * be done before this method is invoked.
     *
     * @param animal the animal to be relocated
     * @param newRow the new row number for the animal
     * @param newColumn the new column number for the animal
     */
    private void moveAnimal(Animal animal, int row, int column) {

        // perform move
        field[animal.row][animal.column] = null;
        field[row][column] = animal;
        animal.row = row;
        animal.column = column;
        
        if (animal instanceof Person) {
            personRow = row;
            personColumn = column;
        }
        else { // animal must be a velociraptor
            velociraptorRow = row;
            velociraptorColumn = column;
        }
    }
    
    /**
     * Utility method to choose a random integer from min
     * to max, inclusive.
     *
     * @param min  the smallest number to be returned
     * @param max  the largest number to be returned
     * @return a random number N, where min &lt;= N &lt;= max
     */
    static int random(int min, int max) {
        return randomNumberGenerator.nextInt(max - min + 1) + min;
    }

    /**
     * Determines how moving in the given direction affects the
     * row number.
     *
     * @param direction the direction in which to move
     * @return the amount by which the row number will change
     */
    static int rowChange(int direction) {
        int change = 0;
        switch (direction) {
            case N:
            case NE:
            case NW:
                change = -1;
                break;
            case S:
            case SE:
            case SW:
                change = +1;
                break;
        }
        return change;
    }
    
    /**
     * Determines how moving in the given direction affects the
     * column number.
     *
     * @param direction  the direction in which to move
     * @return the amount by which the column number will change
     */
    static int columnChange(int direction) {
        int change = 0;
        switch (direction) {
            case W:
            case NW:
            case SW:
                change = -1;
                break;
            case E:
            case NE:
            case SE:
                change = +1;
        }
        return change;
    }

    /** Determines whether the given row and column numbers represent
     * a legal location in the field.
     *
     * @param row    the row number
     * @param column the column number
     */
    boolean legalLocation(int row, int column) {
        return    row >= 0 &&    row < numberOfRows &&
               column >= 0 && column < numberOfColumns;
    }
    
    /**
     * Determines what can be seen from a given location, looking
     * in a given direction.
     *
     * @param row  the row of the object doing the looking
     * @param column  the column of the object doing the looking
     * @param direction  the direction of the look
     * @return the object seen
     */
    int look(int row, int column, int direction) {

        // check for illegal request
        if (!verifyLocation("look", row, column)) {
            return Model.EDGE;
        }
        
        // save the fact that this look was performed--this is
        // only here for purposes of later viewing
        looks.add(new Integer(direction));
        
        // decode direction into its x-y components
        int rowDelta = rowChange(direction);
        int columnDelta = columnChange(direction);
        
        // check in that direction until you see something
        // (if nothing else, you will eventually see the edge of the
        //  array, thus the loop <i>will</i> terminate)
        while (true) {
            row = row + rowDelta;
            column = column + columnDelta;
            if (!legalLocation(row, column))
                return EDGE;
            if (field[row][column] instanceof Person)
                return PERSON;
            if (field[row][column] instanceof Velociraptor)
                return VELOCIRAPTOR;
            if (field[row][column] instanceof Bush)
                return BUSH;
        }
    }

    /**
     * Determines the distance to the nearest thing, or to the
     * edge of the field, looking in a given direction.
     *
     * @param row    the row of the object doing the looking
     * @param column the column of the object doing the looking
     * @param direction the direction of the look
     * @return the distance
     */
    int distance(int row, int column, int direction) {

        // check for illegal request
        // check for illegal request
        if (!verifyLocation("distance", row, column)) {
            return 1000;
        }
        
        
        // decode direction into its x-y components
        int rowDelta = rowChange(direction);
        int columnDelta = columnChange(direction);
        
        // check in that direction until you see something
        // (if nothing else, you will eventually see the edge of the
        //  array, thus the loop <i>will</i> terminate)
        int steps = 0;
        while (true) {
            row = row + rowDelta;
            column = column + columnDelta;
            steps++;
            if (!legalLocation(row, column) || field[row][column] != null) {
                return steps;
            }
        }
    }
    
    /**
     * Given a direction and a number of times to make 1/8 turn clockwise,
     * return the resultant direction.
     *
     * @param direction the initial direction
     * @param number of 45 degree turns clockwise
     * @return the resultant direction
     */
    static int turn(int direction, int number) {
        int mod = (direction + number) % (MAX_DIRECTION - MIN_DIRECTION + 1);
        if (mod >= MIN_DIRECTION) return mod;
        else return 8 + mod;
    }
    
    /**
     * Ensures that the person or velociraptor is at the location it claims
     * to be.
     *
     * @param methodName   the name of the method being used
     * @param row          the row that the caller claims the animal is in
     * @param column       the column that the caller claims the animal is in
     * @return  true if the location is valid
     */
    private boolean verifyLocation(String methodName, int row, int column) {
        if (isUnderConstruction) {
            System.out.println("Error! Call to " + methodName +
                               " while the hunt is still under construction!");
            return false;
        }
            
        if (isPersonsTurn) {
            if (field[row][column] == person) {
                return true;
            }
            else {
                System.out.println("Illegal call by person: " + methodName +
                                   "(direction, " + row + ", " + column + ")");
                System.out.println("The person is actually at row " +
                                   personRow + ", column " + personColumn);
                return false;
            }
        }
        else { // velociraptor's turn
            if (field[row][column] == velociraptor) {
                return true;
            }
            else {
                System.out.println("Illegal call by velociraptor: " + methodName +
                                   "(direction, " + row + ", " + column + ")");
                System.out.println("The velociraptor is actually at row " +
                                   velociraptorRow + ", column " + velociraptorColumn);
                return false;
            }
        }
    }
}
