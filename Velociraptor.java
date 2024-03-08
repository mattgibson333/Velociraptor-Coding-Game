/**
 * Represents a velociraptor.
 * 
 * @author David Matuszek
 * @version October 12, 2001
 */
public class Velociraptor extends Animal {

    // instance variables

    private boolean haveSeenPerson = false;
    private boolean canSeePersonNow = false;
    private int distanceToPerson;
    private int directionToPerson;
    private int currentDirection;
    
    /**
     * Constructs a Velociraptor in the given model, at the given position
     * in the field.
     *
     * @param model  the model that controls this velociraptor.
     * @param row    the row of the field containing this velociraptor.
     * @param column the column of the field containing this velociraptor.
     */
    public Velociraptor(Model model, int row, int column) {
        super(model, row, column);
        currentDirection = Model.random(Model.MIN_DIRECTION,
                                        Model.MAX_DIRECTION);
    }
    
    /**
     * Controls the movement of the velociraptor.
     *
     * @return the direction in which the velociraptor wishes to move.
     */
     int decideMove() {
    
        // look all around for person
        canSeePersonNow = false;
        for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++) {
            if (look(i) == Model.PERSON) {
                canSeePersonNow = haveSeenPerson = true;
                directionToPerson = i;
                distanceToPerson = distance(i);
            }
        }
        
        // if person has been seen recently (not necessarily this time),
        // move toward its last known position
        if (haveSeenPerson) {
            if (distanceToPerson > 0) {
                distanceToPerson--;
                return directionToPerson;
            }
            else { // person was here--where did it go?
                haveSeenPerson = false;
                currentDirection = Model.random(Model.MIN_DIRECTION,
                                                Model.MAX_DIRECTION);
            }
        }
        
        // either haven't seen person, or lost track of person
        // continue with current direction, maybe dodging bushes
        if (canMove(currentDirection))
            return currentDirection;
        else if (canMove(Model.turn(currentDirection, 1)))
            return Model.turn(currentDirection, 1);
        else if (canMove(Model.turn(currentDirection, -1)))
            return Model.turn(currentDirection, -1);
        else {
            currentDirection = Model.random(Model.MIN_DIRECTION,
                                            Model.MAX_DIRECTION);
            for (int i = 0; i < 8; i++) {
                if (canMove(currentDirection))
                    return currentDirection;
                else
                    currentDirection = Model.turn(currentDirection, 1);
            }
        }
        // stuck! cannot move
        return Model.STAY;
    }
            
}
