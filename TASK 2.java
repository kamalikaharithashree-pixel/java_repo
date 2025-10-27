import java.io.*;
import java.util.*;

/**
 * Represents a single Stock with a name, symbol, and price.
 */
class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice() {
        // Randomly adjust stock price by +/- 5%
        double change = (Math.random() - 0.5) * 0.1; // -5% to +5%
        price += price * change;
        if (price < 1) price = 1; // Prevent negative prices
    }

    @Override
    public String toString() {
        return String.format("%-10s %-15s $%.2f", symbol, name, price);
    }
}

/**
 * Represents a stock transaction (buy/sell).
 */
class Transaction {
    private String stockSymbol;
    private int quantity;
    private double price;
    private String type; // "BUY" or "SELL"
    private Date date;

    public Transaction(String stockSymbol, int quantity, double price, String type) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %d shares of %s @ $%.2f",
                date, type, quantity, stockSymbol, price);
    }
}

/**
 * Represents a user who owns a portfolio of stocks and balance.
 */
class User {
    private String name;
    private double balance;
    private Map<String, Integer> portfolio;
    private List<Transaction> transactions;

    public User(String name, double startingBalance) {
        this.name = name;
        this.balance = startingBalance;
        this.portfolio = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Integer> getPortfolio() {
        return portfolio;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void buyStock(Stock stock, int quantity) {
        double cost = stock.getPrice() * quantity;
        if (cost > balance) {
            System.out.println("⚠️ Not enough balance to complete this purchase! - TASK 2.java:103");
            return;
        }
        balance -= cost;
        portfolio.put(stock.getSymbol(), portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);
        transactions.add(new Transaction(stock.getSymbol(), quantity, stock.getPrice(), "BUY"));
        System.out.println("✅ Successfully bought - TASK 2.java:109" + quantity + " shares of " + stock.getSymbol());
    }

    public void sellStock(Stock stock, int quantity) {
        if (!portfolio.containsKey(stock.getSymbol()) || portfolio.get(stock.getSymbol()) < quantity) {
            System.out.println("⚠️ Not enough shares to sell! - TASK 2.java:114");
            return;
        }
        double revenue = stock.getPrice() * quantity;
        balance += revenue;
        portfolio.put(stock.getSymbol(), portfolio.get(stock.getSymbol()) - quantity);
        if (portfolio.get(stock.getSymbol()) == 0)
            portfolio.remove(stock.getSymbol());
        transactions.add(new Transaction(stock.getSymbol(), quantity, stock.getPrice(), "SELL"));
        System.out.println("✅ Sold - TASK 2.java:123" + quantity + " shares of " + stock.getSymbol());
    }

    public void displayPortfolio(Map<String, Stock> market) {
        System.out.println("\n=== Portfolio for - TASK 2.java:127" + name + " ===");
        double totalValue = 0;
        for (String symbol : portfolio.keySet()) {
            int qty = portfolio.get(symbol);
            double price = market.get(symbol).getPrice();
            double value = qty * price;
            totalValue += value;
            System.out.printf("%-10s Shares: %-5d Value: $%.2f\n", symbol, qty, value);
        }
        System.out.printf("\nCash Balance: $%.2f\n", balance);
        System.out.printf("Total Portfolio Value: $%.2f\n", totalValue + balance);
    }

    public void savePortfolioToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("portfolio.txt"))) {
            writer.println(name);
            writer.println(balance);
            for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
            System.out.println("💾 Portfolio saved to portfolio.txt - TASK 2.java:147");
        } catch (IOException e) {
            System.out.println("Error saving portfolio: - TASK 2.java:149" + e.getMessage());
        }
    }

    public static User loadPortfolioFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("portfolio.txt"))) {
            String name = reader.readLine();
            double balance = Double.parseDouble(reader.readLine());
            User user = new User(name, balance);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                user.portfolio.put(parts[0], Integer.parseInt(parts[1]));
            }
            System.out.println("📂 Portfolio loaded from file. - TASK 2.java:163");
            return user;
        } catch (IOException e) {
            System.out.println("No saved portfolio found. Creating a new one. - TASK 2.java:166");
            return null;
        }
    }
}

/**
 * Main simulation class.
 */
public class StockTradingPlatform {
    private static Map<String, Stock> market = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeMarket();

        System.out.print("Enter your name: - TASK 2.java:182");
        String name = scanner.nextLine();
        User user = User.loadPortfolioFromFile();
        if (user == null) {
            user = new User(name, 10000); // Starting balance
        }

        int choice;
        do {
            showMenu();
            choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> displayMarket();
                case 2 -> handleBuy(user);
                case 3 -> handleSell(user);
                case 4 -> user.displayPortfolio(market);
                case 5 -> updateMarketPrices();
                case 6 -> user.savePortfolioToFile();
                case 0 -> {
                    user.savePortfolioToFile();
                    System.out.println("👋 Exiting platform. Goodbye! - TASK 2.java:202");
                }
                default -> System.out.println("Invalid choice, try again. - TASK 2.java:204");
            }
        } while (choice != 0);
    }

    private static void showMenu() {
        System.out.println("\n=== Stock Trading Platform === - TASK 2.java:210");
        System.out.println("1. View Market Data - TASK 2.java:211");
        System.out.println("2. Buy Stock - TASK 2.java:212");
        System.out.println("3. Sell Stock - TASK 2.java:213");
        System.out.println("4. View Portfolio - TASK 2.java:214");
        System.out.println("5. Refresh Market Prices - TASK 2.java:215");
        System.out.println("6. Save Portfolio - TASK 2.java:216");
        System.out.println("0. Exit - TASK 2.java:217");
        System.out.print("Enter choice: - TASK 2.java:218");
    }

    private static void initializeMarket() {
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.50));
        market.put("GOOG", new Stock("GOOG", "Alphabet Inc.", 135.75));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 120.90));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 210.40));
        market.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 310.25));
    }

    private static void displayMarket() {
        System.out.println("\n=== Market Data === - TASK 2.java:230");
        System.out.printf("%-10s %-15s %-10s\n", "Symbol", "Company", "Price");
        System.out.println("");
        for (Stock stock : market.values()) {
            System.out.println(stock);
        }
    }

    private static void handleBuy(User user) {
        displayMarket();
        System.out.print("Enter stock symbol to buy: - TASK 2.java:240");
        String symbol = scanner.nextLine().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("⚠️ Invalid stock symbol! - TASK 2.java:243");
            return;
        }
        System.out.print("Enter quantity to buy: - TASK 2.java:246");
        int qty = Integer.parseInt(scanner.nextLine());
        user.buyStock(market.get(symbol), qty);
    }

    private static void handleSell(User user) {
        user.displayPortfolio(market);
        System.out.print("Enter stock symbol to sell: - TASK 2.java:253");
        String symbol = scanner.nextLine().toUpperCase();
        if (!market.containsKey(symbol)) {
            System.out.println("⚠️ Invalid stock symbol! - TASK 2.java:256");
            return;
        }
        System.out.print("Enter quantity to sell: - TASK 2.java:259");
        int qty = Integer.parseInt(scanner.nextLine());
        user.sellStock(market.get(symbol), qty);
    }

    private static void updateMarketPrices() {
        System.out.println("\n📈 Updating market prices... - TASK 2.java:265");
        for (Stock stock : market.values()) {
            stock.updatePrice();
        }
        System.out.println("✅ Market prices refreshed. - TASK 2.java:269");
    }
}

