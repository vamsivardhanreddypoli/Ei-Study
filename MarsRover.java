import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

// Enum for Directions
enum Directions {
    NORTH, EAST, SOUTH, WEST;

    private static final Directions[] values = values();  

    // Method to get the next position based on direction
    public Position getNextPosition(Position current) {
        switch (this) {
            case NORTH -> current.setY(current.getY() + 1);
            case SOUTH -> current.setY(current.getY() - 1);
            case EAST -> current.setX(current.getX() + 1);
            case WEST -> current.setX(current.getX() - 1);
        }
        return current;
    }

    // Method to turn left
    public Directions turnLeft() {
        return values[(this.ordinal() + 3) % 4];  
    }

    // Method to turn right
    public Directions turnRight() {
        return values[(this.ordinal() + 1) % 4];  
    }
}

// Class to represent the Rover's position
class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}

// Base Rover Class
abstract class RoverBase {
    protected Position position;
    protected Directions direction;
    protected final int gridWidth;
    protected final int gridHeight;
    protected final Set<Position> obstacles;

    public RoverBase(int startX, int startY, Directions startDirection, int gridWidth, int gridHeight) {
        this.position = new Position(startX, startY);
        this.direction = startDirection;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.obstacles = new HashSet<>();
    }

    public void addObstacle(int x, int y) {
        obstacles.add(new Position(x, y));
    }

    public boolean isObstacle(Position pos) {
        return obstacles.contains(pos);
    }

    public abstract void moveForward();

    public void turnLeft() {
        direction = direction.turnLeft();
    }

    public void turnRight() {
        direction = direction.turnRight();
    }

    protected boolean isValidPosition(Position newPosition) {
        return newPosition.getX() >= 0 && newPosition.getX() < gridWidth &&
               newPosition.getY() >= 0 && newPosition.getY() < gridHeight &&
               !isObstacle(newPosition);
    }

    public void printStatus() {
        System.out.println("Rover is at " + position + " facing " + direction);
    }
}

// Concrete Rover Class
class Rover extends RoverBase {
    public Rover(int startX, int startY, Directions startDirection, int gridWidth, int gridHeight) {
        super(startX, startY, startDirection, gridWidth, gridHeight);
    }

    @Override
    public void moveForward() {
        Position newPosition = direction.getNextPosition(new Position(position.getX(), position.getY()));

        if (isValidPosition(newPosition)) {
            position = newPosition;
            System.out.println("Moved to " + position);
        } else {
            handleInvalidMove(newPosition);
        }
    }

    private void handleInvalidMove(Position newPosition) {
        if (isObstacle(newPosition)) {
            System.out.println("Obstacle detected at " + newPosition + ". Cannot move.");
        } else {
            System.out.println("Cannot move out of bounds.");
        }
    }
}

// Main Class
public class MarsRover {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize grid size
        System.out.print("Enter grid width and height: ");
        int gridWidth = scanner.nextInt();
        int gridHeight = scanner.nextInt();

        // Input starting position and direction
        System.out.print("Enter starting position of the rover (x y) and initial direction (NORTH, EAST, SOUTH, WEST): ");
        int startX = scanner.nextInt();
        int startY = scanner.nextInt();
        String initialDirection = scanner.next().toUpperCase();

        Directions direction = getValidDirection(initialDirection);

        Rover rover = new Rover(startX, startY, direction, gridWidth, gridHeight);

        // Input obstacles
        System.out.println("Enter the number of obstacles:");
        int numObstacles = scanner.nextInt();

        for (int i = 0; i < numObstacles; i++) {
            System.out.print("Enter obstacle position (x y): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            rover.addObstacle(x, y);
            System.out.println("Obstacle added at " + new Position(x, y));
        }

        // Input commands
        System.out.print("Enter commands (e.g., MMRML): ");
        String commands = scanner.next();

        // Process commands
        for (char command : commands.toCharArray()) {
            handleCommand(rover, command);
        }

        rover.printStatus();
        scanner.close();
    }

    private static Directions getValidDirection(String initialDirection) {
        try {
            return Directions.valueOf(initialDirection);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid direction: " + initialDirection + ". Setting to NORTH by default.");
            return Directions.NORTH;
        }
    }

    private static void handleCommand(Rover rover, char command) {
        switch (command) {
            case 'M' -> rover.moveForward();
            case 'L' -> rover.turnLeft();
            case 'R' -> rover.turnRight();
            default -> System.out.println("Invalid command: " + command);
        }
    }
}
