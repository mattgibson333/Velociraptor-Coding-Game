public class Person extends Animal {
    //Declared necessary variables for the person to store enough data to survive
    private boolean haveSeenVelociraptor = false;
    private boolean canSeeVelociraptorNow = false;
    private int distanceToVelociraptor;
    private int directionToVelociraptor;
    private int directionAwayFromVelociraptor;
    private int currentDirection;
    private int distanceToEdge;
    private int directionToEdge;
    private int directionAwayFromEdge;

    public Person(Model model, int row, int column) {
        super(model, row, column);
    }

    //The main decision-making method of the person class
    int decideMove()
    {
//This variable is set to false every turn so there is no way that the person could think that it saw the velociraptor before it looks around
        canSeeVelociraptorNow = false;
//Person looks around for the velociraptor using the look method
        for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++)
        {
//If the velociraptor is spotted,the person takes note of the distance and direction to the velociraptor
            if (look(i) == Model.VELOCIRAPTOR)
            {
                canSeeVelociraptorNow = haveSeenVelociraptor = true;
                directionToVelociraptor = i;
                distanceToVelociraptor = distance(i);
//This switch statement is used to define the opposite direction to the velociraptor given the person's current position
                switch (directionToVelociraptor)
                {
                    case 0:
                        directionAwayFromVelociraptor = 4;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 1:
                        directionAwayFromVelociraptor = 5;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 2:
                        directionAwayFromVelociraptor = 6;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 3:
                        directionAwayFromVelociraptor = 7;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 4:
                        directionAwayFromVelociraptor = 0;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 5:
                        directionAwayFromVelociraptor = 1;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 6:
                        directionAwayFromVelociraptor = 2;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                    case 7:
                        directionAwayFromVelociraptor = 3;
                        currentDirection = directionAwayFromVelociraptor;
                        break;
                }
            }
        }
        if (canSeeVelociraptorNow)
        {
//If the person can see the velociraptor, then it will attempt to move diagonally away from the velociraptor.
            if (canMove(Model.turn(currentDirection, 1)))
            {
                return Model.turn(currentDirection, 1);
            }
            else if (canMove(Model.turn(currentDirection, -1)))
            {
                return Model.turn(currentDirection, -1);
            }
//If the person cannot move diagonally away from the velociraptor, then it will move directly away from it instead
else if (canMove(currentDirection))
        {
            distanceToVelociraptor++;
            return directionAwayFromVelociraptor;
        }
//If the person cannot move diagonally or directly away from the velociraptor, it will attempt to move directly perpendicular to the velociraptor's line of sight
else if (canMove(Model.turn(currentDirection, 2)))
        {
            return Model.turn(currentDirection, 2);
        }
        else if (canMove(Model.turn(currentDirection, -2)))
        {
            return Model.turn(currentDirection, -2);
        }
//If all else fails, the person will move randomly
        else
        {
            return Model.random(Model.MIN_DIRECTION, Model.MAX_DIRECTION);
        }
        }
//This scan is used to check if the person is too close to the edge fo the board
        for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++)
        {
//The person looks for an edge in the same way that it looks for the velociraptor
            if (look(i) == Model.EDGE)
            {
                distanceToEdge = distance(i);
                directionToEdge = i;
                switch (directionToEdge)
                {
                    case 0:
                        directionAwayFromEdge = 4;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 1:
                        directionAwayFromEdge = 5;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 2:
                        directionAwayFromEdge = 6;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 3:
                        directionAwayFromEdge = 7;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 4:
                        directionAwayFromEdge = 0;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 5:
                        directionAwayFromEdge = 1;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 6:
                        directionAwayFromEdge = 2;
                        currentDirection = directionAwayFromEdge;
                        break;
                    case 7:
                        directionAwayFromEdge = 3;
                        currentDirection = directionAwayFromEdge;
                        break;
                }
//If the person is too close to the edge and cannot see the velociraptor currently, then the person will attempt to move away from the edge
                if (distanceToEdge <=2)
                {
                    distanceToEdge++;
                    return directionAwayFromEdge;
                }
            }
        }
//If the velociraptor has not yet been spotted, or if all other conditions fail, then the person will remain still to avoid alerting the velociraptor
        return Model.STAY;
    }
}
