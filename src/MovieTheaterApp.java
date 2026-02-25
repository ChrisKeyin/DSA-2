import java.util.Scanner;

// Simple command-line movie theater seat reservation app.
// Allows showing the seating chart, reserving seats, and cancelling reservations.
public class MovieTheaterApp {

    // Number of rows and seats per row
    private static final int ROWS = 5;
    private static final int COLS = 8;

    // Symbols used in the seating chart: O = open, X = reserved
    private static final char OPEN = 'O';
    private static final char RESERVED = 'X';

    public static void main(String[] args) {
        // Present a simple menu
        Scanner scanner = new Scanner(System.in);

        // Create initial seating chart
        char[][] seats = createInitialSeatingChart(ROWS, COLS);

        // Menu loop
        while (true) {
            System.out.println("\n=== Movie Theater Seat System ===");
            System.out.println("1) Show seating chart");
            System.out.println("2) Reserve a seat");
            System.out.println("3) Cancel a reservation");
            System.out.println("4) Exit");
            System.out.print("Choose an option: ");

            int choice = readInt(scanner);

            if (choice == 1) {
                printSeatingChart(seats);
            } else if (choice == 2) {
                reserveSeat(scanner, seats);
            } else if (choice == 3) {
                cancelSeat(scanner, seats);
            } else if (choice == 4) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Please choose 1-4.");
            }
        }

        scanner.close();
    }

    // Create a 2D array representing seats, mark every seat as OPEN
    private static char[][] createInitialSeatingChart(int rows, int cols) {
        char[][] seats = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                seats[r][c] = OPEN;
            }
        }
        return seats;
    }

    // Print seating chart with column numbers and row labels
    private static void printSeatingChart(char[][] seats) {
        System.out.println("\nSeating Chart (O = Open, X = Reserved)");
        System.out.print("     ");
        for (int c = 1; c <= seats[0].length; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

        for (int r = 0; r < seats.length; r++) {
            System.out.printf("Row %2d ", (r + 1));
            for (int c = 0; c < seats[r].length; c++) {
                System.out.printf(" %c ", seats[r][c]);
            }
            System.out.println();
        }
    }

    // Reserve a specific seat if open, otherwise suggest nearest available
    private static void reserveSeat(Scanner scanner, char[][] seats) {
        printSeatingChart(seats);

        System.out.print("\nEnter row number to reserve (1-" + seats.length + "): ");
        int row = readInt(scanner) - 1;

        System.out.print("Enter seat number in that row (1-" + seats[0].length + "): ");
        int col = readInt(scanner) - 1;

        if (!isValidSeat(seats, row, col)) {
            System.out.println("That seat does not exist.");
            return;
        }

        if (seats[row][col] == OPEN) {
            seats[row][col] = RESERVED;
            System.out.println("Seat reserved successfully! (Row " + (row + 1) + ", Seat " + (col + 1) + ")");
        } else {
            System.out.println("That seat is already taken.");

            // Try to find a nearby open seat in same row or nearby rows
            int[] suggestion = findNearestAvailableSeat(seats, row, col);
            if (suggestion != null) {
                System.out.println("Suggested available seat: Row " + (suggestion[0] + 1) + ", Seat " + (suggestion[1] + 1));
                System.out.print("Do you want to reserve the suggested seat? (y/n): ");
                String ans = scanner.next().trim().toLowerCase();
                if (ans.equals("y")) {
                    seats[suggestion[0]][suggestion[1]] = RESERVED;
                    System.out.println("Suggested seat reserved!");
                } else {
                    System.out.println("No seat reserved.");
                }
            } else {
                System.out.println("No available seats left in the theater.");
            }
        }
    }

    // Cancel a reservation if the seat is currently reserved
    private static void cancelSeat(Scanner scanner, char[][] seats) {
        printSeatingChart(seats);

        System.out.print("\nEnter row number to cancel (1-" + seats.length + "): ");
        int row = readInt(scanner) - 1;

        System.out.print("Enter seat number in that row (1-" + seats[0].length + "): ");
        int col = readInt(scanner) - 1;

        if (!isValidSeat(seats, row, col)) {
            System.out.println("That seat position is out of range.");
            return;
        }

        if (seats[row][col] == RESERVED) {
            seats[row][col] = OPEN;
            System.out.println("Reservation cancelled. (Row " + (row + 1) + ", Seat " + (col + 1) + ")");
        } else {
            System.out.println("That seat is not reserved, so there is nothing to cancel.");
        }
    }

    // Check whether the given row and column indices are inside the seating array
    private static boolean isValidSeat(char[][] seats, int row, int col) {
        return row >= 0 && row < seats.length && col >= 0 && col < seats[0].length;
    }

    // Search for nearest open seat: first scan same row left/right, then nearby rows
    private static int[] findNearestAvailableSeat(char[][] seats, int row, int col) {
        // Check same row with increasing distance from the requested column
        for (int offset = 0; offset < seats[0].length; offset++) {
            int left = col - offset;
            int right = col + offset;

            if (left >= 0 && seats[row][left] == OPEN) {
                return new int[]{row, left};
            }
            if (right < seats[0].length && seats[row][right] == OPEN) {
                return new int[]{row, right};
            }
        }

        // If none in same row, look at rows above and below
        for (int rowOffset = 1; rowOffset < seats.length; rowOffset++) {
            int up = row - rowOffset;
            int down = row + rowOffset;

            if (up >= 0) {
                int[] found = findFirstOpenInRow(seats, up);
                if (found != null) return found;
            }
            if (down < seats.length) {
                int[] found = findFirstOpenInRow(seats, down);
                if (found != null) return found;
            }
        }

        return null;
    }

    // Return first open seat index in given row, or null if none
    private static int[] findFirstOpenInRow(char[][] seats, int row) {
        for (int c = 0; c < seats[0].length; c++) {
            if (seats[row][c] == OPEN) {
                return new int[]{row, c};
            }
        }
        return null;
    }

    // Read number from scanner, prompting until a valid number is entered
    private static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}