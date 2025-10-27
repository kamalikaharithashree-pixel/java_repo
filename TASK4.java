import java.io.*;
import java.util.*;

/**
 * Enum for room categories.
 */
enum RoomType {
    STANDARD, DELUXE, SUITE
}

/**
 * Represents a single hotel room.
 */
class Room {
    private int roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean isBooked;

    public Room(int roomNumber, RoomType type, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.isBooked = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @Override
    public String toString() {
        return String.format("Room %d | %s | $%.2f/night | %s",
                roomNumber, type, pricePerNight, (isBooked ? "BOOKED" : "AVAILABLE"));
    }
}

/**
 * Represents a booking/reservation made by a user.
 */
class Booking {
    private String bookingId;
    private String customerName;
    private Room room;
    private int nights;
    private double totalAmount;
    private boolean paid;

    public Booking(String bookingId, String customerName, Room room, int nights) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.room = room;
        this.nights = nights;
        this.totalAmount = room.getPricePerNight() * nights;
        this.paid = false;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Room getRoom() {
        return room;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void markPaid() {
        this.paid = true;
    }

    @Override
    public String toString() {
        return String.format("Booking ID: %s | Customer: %s | Room: %d (%s) | Nights: %d | Total: $%.2f | Paid: %s",
                bookingId, customerName, room.getRoomNumber(), room.getType(), nights, totalAmount,
                (paid ? "YES" : "NO"));
    }
}

/**
 * Handles simulated payments.
 */
class Payment {
    public static boolean processPayment(double amount) {
        Scanner sc = new Scanner(System.in);
        System.out.printf("Payment due: $%.2f\nEnter 'pay' to complete payment: ", amount);
        String input = sc.nextLine().trim().toLowerCase();
        if (input.equals("pay")) {
            System.out.println("✅ Payment successful!");
            return true;
        }
        System.out.println("⚠️ Payment failed or canceled.");
        return false;
    }
}

/**
 * Represents the Hotel, managing rooms and bookings.
 */
class Hotel {
    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    public Hotel() {
        loadData();
    }

    /**
     * Adds sample rooms if no file exists.
     */
    public void initializeRooms() {
        if (rooms.isEmpty()) {
            rooms.add(new Room(101, RoomType.STANDARD, 80));
            rooms.add(new Room(102, RoomType.STANDARD, 85));
            rooms.add(new Room(201, RoomType.DELUXE, 150));
            rooms.add(new Room(202, RoomType.DELUXE, 160));
            rooms.add(new Room(301, RoomType.SUITE, 250));
            rooms.add(new Room(302, RoomType.SUITE, 270));
        }
    }

    public void displayAvailableRooms(RoomType type) {
        System.out.println("\n=== Available " + type + " Rooms ===");
        boolean found = false;
        for (Room r : rooms) {
            if (r.getType() == type && !r.isBooked()) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) System.out.println("No available rooms in this category.");
    }

    public Booking makeBooking(String customerName, RoomType type, int nights) {
        for (Room r : rooms) {
            if (r.getType() == type && !r.isBooked()) {
                String bookingId = UUID.randomUUID().toString().substring(0, 8);
                Booking booking = new Booking(bookingId, customerName, r, nights);
                r.setBooked(true);
                bookings.add(booking);
                System.out.println("✅ Room booked successfully with Booking ID: " + bookingId);
                return booking;
            }
        }
        System.out.println("⚠️ No available rooms of this type.");
        return null;
    }

    public void cancelBooking(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                b.getRoom().setBooked(false);
                bookings.remove(b);
                System.out.println("🗑️ Booking canceled successfully.");
                saveData();
                return;
            }
        }
        System.out.println("⚠️ Booking ID not found.");
    }

    public void displayAllBookings() {
        System.out.println("\n=== All Bookings ===");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking b : bookings) {
                System.out.println(b);
            }
        }
    }

    /** Save all data to file */
    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("hotel_data.txt"))) {
            for (Room r : rooms) {
                writer.printf("ROOM,%d,%s,%.2f,%s\n",
                        r.getRoomNumber(), r.getType(), r.getPricePerNight(), r.isBooked());
            }
            for (Booking b : bookings) {
                writer.printf("BOOKING,%s,%s,%d,%d,%.2f,%s\n",
                        b.getBookingId(), b.getRoom().getType(), b.getRoom().getRoomNumber(),
                        b.getRoom().isBooked() ? 1 : 0, b.getTotalAmount(), b.isPaid());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    /** Load data from file */
    public void loadData() {
        File file = new File("hotel_data.txt");
        if (!file.exists()) {
            initializeRooms();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            rooms.clear();
            bookings.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals("ROOM")) {
                    int roomNum = Integer.parseInt(parts[1]);
                    RoomType type = RoomType.valueOf(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    boolean booked = Boolean.parseBoolean(parts[4]);
                    Room r = new Room(roomNum, type, price);
                    r.setBooked(booked);
                    rooms.add(r);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}

/**
 * Main class with console interface.
 */
public class HotelBookingSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Hotel hotel = new Hotel();

    public static void main(String[] args) {
        hotel.initializeRooms();

        System.out.println("=== Welcome to Hotel Booking System ===");
        while (true) {
            showMenu();
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            switch (choice) {
                case 1 -> searchRooms();
                case 2 -> bookRoom();
                case 3 -> cancelBooking();
                case 4 -> hotel.displayAllBookings();
                case 0 -> {
                    hotel.saveData();
                    System.out.println("💾 Data saved. Exiting system...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("""
                
                1. Search Available Rooms
                2. Book a Room
                3. Cancel Booking
                4. View All Bookings
                0. Exit
                """);
        System.out.print("Enter your choice: ");
    }

    private static void searchRooms() {
        RoomType type = getRoomTypeFromUser();
        hotel.displayAvailableRooms(type);
    }

    private static void bookRoom() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        RoomType type = getRoomTypeFromUser();
        System.out.print("Enter number of nights: ");
        int nights = Integer.parseInt(scanner.nextLine());

        Booking booking = hotel.makeBooking(name, type, nights);
        if (booking != null) {
            if (Payment.processPayment(booking.getTotalAmount())) {
                booking.markPaid();
                hotel.saveData();
            } else {
                System.out.println("❌ Payment not completed. Booking not finalized.");
            }
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        String id = scanner.nextLine();
        hotel.cancelBooking(id);
    }

    private static RoomType getRoomTypeFromUser() {
        System.out.println("Select Room Type: 1. STANDARD  2. DELUXE  3. SUITE");
        int type = Integer.parseInt(scanner.nextLine());
        return switch (type) {
            case 2 -> RoomType.DELUXE;
            case 3 -> RoomType.SUITE;
            default -> RoomType.STANDARD;
        };
    }
}
