import java.io.*;
import java.util.*;

public class BusReservationSystem {
    static Route[] routes = new Route[100];
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadRoutes();
        loadBookingsFromFile();

        int choice;
        do {
            System.out.println("\n=== Bus Reservation System ===");
            System.out.println("1. Book Seat");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View Seat Layout");
            System.out.println("4. View All Bookings");
            System.out.println("5. Admin Login");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> bookSeat();
                case 2 -> cancelBooking();
                case 3 -> viewSeatLayout();
                case 4 -> viewAllBookings();
                case 5 -> adminLogin();
                case 6 -> {
                    saveBookingsToFile();
                    System.out.println("Exiting... Goodbye!");
                }
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 6);
    }

    static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    static void adminLogin() {
        System.out.print("Enter admin password: ");
        String password = sc.nextLine();
        if (password.equals("admin123")) {
            int option;
            do {
                System.out.println("\n--- Admin Panel ---");
                System.out.println("1. Add Route");
                System.out.println("2. View Routes");
                System.out.println("3. Update Route");
                System.out.println("4. Delete Route");
                System.out.println("5. Sort Routes by Time");
                System.out.println("6. Back to Main Menu");
                System.out.print("Choose option: ");
                option = getIntInput();

                switch (option) {
                    case 1 -> addRoute();
                    case 2 -> displayRoutes();
                    case 3 -> updateRoute();
                    case 4 -> deleteRoute();
                    case 5 -> sortRoutesByTime();
                    case 6 -> System.out.println("Returning to Main Menu...");
                    default -> System.out.println("Invalid option. Try again!");
                }
            } while (option != 6);
        } else {
            System.out.println("Incorrect password!");
        }
    }

    static void addRoute() {
        System.out.print("Enter Source: ");
        String source = sc.nextLine();
        System.out.print("Enter Destination: ");
        String destination = sc.nextLine();
        System.out.print("Enter Time (e.g., 10:30 AM): ");
        String time = sc.nextLine();
        System.out.print("Enter Distance (in km): ");
        int distance = getIntInput();

        for (int i = 0; i < routes.length; i++) {
            if (routes[i] == null) {
                routes[i] = new Route(source, destination, time, distance);
                System.out.println("Route added successfully.");
                return;
            }
        }
        System.out.println("Route list is full!");
    }

    static void updateRoute() {
        displayRoutes();
        System.out.print("Enter route number to update: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= routes.length || routes[index] == null) {
            System.out.println("Invalid route number!");
            return;
        }

        Route r = routes[index];
        System.out.println("Leave field blank to keep old value.");

        System.out.print("New Source (" + r.source + "): ");
        String source = sc.nextLine();
        if (!source.isBlank())
            r.source = source;

        System.out.print("New Destination (" + r.destination + "): ");
        String dest = sc.nextLine();
        if (!dest.isBlank())
            r.destination = dest;

        System.out.print("New Time (" + r.time + "): ");
        String time = sc.nextLine();
        if (!time.isBlank())
            r.time = time;

        System.out.print("New Distance (" + r.distance + " km): ");
        String distanceStr = sc.nextLine();
        if (!distanceStr.isBlank()) {
            try {
                r.distance = Integer.parseInt(distanceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid distance input. Keeping old value.");
            }
        }

        System.out.println("Route updated successfully!");
    }

    static void deleteRoute() {
        displayRoutes();
        System.out.print("Enter route number to delete: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= routes.length || routes[index] == null) {
            System.out.println("Invalid route number!");
            return;
        }

        routes[index] = null;
        System.out.println("Route deleted successfully!");
    }

    static void sortRoutesByTime() {
        for (int i = 0; i < routes.length - 1; i++) {
            for (int j = i + 1; j < routes.length; j++) {
                if (routes[i] != null && routes[j] != null &&
                        routes[i].time.compareTo(routes[j].time) > 0) {
                    Route temp = routes[i];
                    routes[i] = routes[j];
                    routes[j] = temp;
                }
            }
        }
        System.out.println("Routes sorted by time!");
        displayRoutes();
    }

    static void displayRoutes() {
        System.out.println("\nAvailable Routes:\n");
        System.out.printf("%-4s %-20s %-20s %-12s %-10s %-10s\n", "No.", "Source", "Destination", "Time", "Distance",
                "Fare");
        System.out.println("--------------------------------------------------------------------------------------");

        for (int i = 0; i < routes.length; i++) {
            if (routes[i] != null) {
                Route r = routes[i];
                int fare = r.distance * 2;
                System.out.printf("%-4d %-20s %-20s %-12s %-10d Rs. %-10d\n",
                        (i + 1), r.source, r.destination, "[" + r.time + "]", r.distance, fare);
            }
        }
    }

    static void bookSeat() {
        displayRoutes();
        System.out.print("Select route number: ");
        int routeIndex = getIntInput() - 1;

        if (routeIndex < 0 || routeIndex >= routes.length || routes[routeIndex] == null) {
            System.out.println("Invalid route!");
            return;
        }

        Route route = routes[routeIndex];
        Bus bus = route.bus;

        System.out.print("Enter passenger name: ");
        String name = sc.nextLine();
        System.out.print("Enter age: ");
        int age = getIntInput();

        for (int i = 0; i < bus.totalSeats; i++) {
            if (!bus.seats[i]) {
                bus.seats[i] = true;
                Passenger p = new Passenger(name, age, i + 1);
                bus.bookings[i] = p;

                int fare = route.distance * 2;
                System.out.println("\n--- Ticket ---");
                System.out.println("Passenger: " + name);
                System.out.println("Age: " + age);
                System.out.println("Seat: " + (i + 1));
                System.out.println("From: " + route.source);
                System.out.println("To: " + route.destination);
                System.out.println("Departure: " + route.time);
                System.out.println("Fare: Rs. " + fare);
                System.out.println("-----------------\n");
                return;
            }
        }

        System.out.println("Bus is full! Adding to waitlist.");
        bus.waitlist.add(new Passenger(name, age, -1));
    }

    static void cancelBooking() {
        displayRoutes();
        System.out.print("Select route number: ");
        int routeIndex = getIntInput() - 1;

        if (routeIndex < 0 || routeIndex >= routes.length || routes[routeIndex] == null) {
            System.out.println("Invalid route!");
            return;
        }

        Route route = routes[routeIndex];
        Bus bus = route.bus;

        System.out.print("Enter seat number to cancel: ");
        int seat = getIntInput();

        if (seat > 0 && seat <= bus.totalSeats && bus.bookings[seat - 1] != null) {
            Passenger removed = bus.bookings[seat - 1];
            bus.bookings[seat - 1] = null;
            bus.seats[seat - 1] = false;
            System.out.println("Booking cancelled for seat: " + seat + " (" + removed.name + ")");

            if (!bus.waitlist.isEmpty()) {
                Passenger p = bus.waitlist.poll();
                p.seatNumber = seat;
                bus.seats[seat - 1] = true;
                bus.bookings[seat - 1] = p;
                System.out.println("Seat assigned to waitlisted passenger: " + p.name);
            }
        } else {
            System.out.println("No booking found for this seat.");
        }
    }

    static void viewSeatLayout() {
        displayRoutes();
        System.out.print("Select route number: ");
        int routeIndex = getIntInput() - 1;

        if (routeIndex < 0 || routeIndex >= routes.length || routes[routeIndex] == null) {
            System.out.println("Invalid route!");
            return;
        }

        Bus bus = routes[routeIndex].bus;
        System.out.println("\nSeat Layout:");
        for (int i = 0; i < bus.totalSeats; i++) {
            System.out.println("Seat " + (i + 1) + ": " + (bus.seats[i] ? "Booked" : "Available"));
        }
    }

    static void viewAllBookings() {
        for (Route route : routes) {
            if (route != null) {
                System.out.println("\nRoute: " + route.source + " → " + route.destination + " [" + route.time + "]");
                boolean noBookings = true;
                for (int i = 0; i < route.bus.totalSeats; i++) {
                    if (route.bus.bookings[i] != null) {
                        Passenger p = route.bus.bookings[i];
                        System.out.println("Seat " + (i + 1) + ": " + p.name + " (Age: " + p.age + ")");
                        noBookings = false;
                    }
                }
                if (noBookings) {
                    System.out.println("No bookings yet.");
                }
            }
        }
    }

    static void saveBookingsToFile() {
        try (PrintWriter pw = new PrintWriter("bookings.txt")) {
            for (Route route : routes) {
                if (route != null) {
                    for (int i = 0; i < route.bus.totalSeats; i++) {
                        if (route.bus.bookings[i] != null) {
                            Passenger p = route.bus.bookings[i];
                            pw.println(route.source + "," + route.destination + "," + route.time + "," +
                                    route.distance + "," + p.name + "," + p.age + "," + p.seatNumber);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings!");
        }
    }

    static void loadBookingsFromFile() {
        File file = new File("bookings.txt");
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String src = parts[0], dest = parts[1], time = parts[2];
                int dist = Integer.parseInt(parts[3]);
                String name = parts[4];
                int age = Integer.parseInt(parts[5]);
                int seat = Integer.parseInt(parts[6]);

                for (Route r : routes) {
                    if (r != null && r.source.equals(src) && r.destination.equals(dest) && r.time.equals(time)) {
                        r.bus.seats[seat - 1] = true;
                        r.bus.bookings[seat - 1] = new Passenger(name, age, seat);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings!");
        }
    }

    static void loadRoutes() {
        routes[0] = new Route("Chandigarh", "Delhi", "09:00 AM", 250);
        routes[1] = new Route("Delhi", "Jaipur", "12:00 PM", 280);
        routes[2] = new Route("Amritsar", "Ludhiana", "05:30 PM", 140);
        routes[3] = new Route("Mumbai", "Pune", "08:00 AM", 150);
        routes[4] = new Route("Bangalore", "Chennai", "06:00 PM", 350);
        routes[5] = new Route("Kolkata", "Durgapur", "10:00 AM", 200);
        routes[6] = new Route("Hyderabad", "Vizag", "03:00 PM", 600);
        routes[7] = new Route("Ahmedabad", "Surat", "11:00 AM", 270);
        routes[8] = new Route("Bhopal", "Indore", "01:30 PM", 190);
        routes[9] = new Route("Patna", "Gaya", "04:45 PM", 120);
    }
}

class Bus {
    int totalSeats = 10;
    boolean[] seats = new boolean[totalSeats];
    Passenger[] bookings = new Passenger[totalSeats];
    LinkedList<Passenger> waitlist = new LinkedList<>();
}

class Passenger {
    String name;
    int age;
    int seatNumber;

    Passenger(String name, int age, int seatNumber) {
        this.name = name;
        this.age = age;
        this.seatNumber = seatNumber;
    }
}

class Route {
    String source, destination, time;
    int distance;
    Bus bus = new Bus();

    Route(String src, String dest, String time, int dist) {
        this.source = src;
        this.destination = dest;
        this.time = time;
        this.distance = dist;
    }
}