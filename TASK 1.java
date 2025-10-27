import java.util.ArrayList;
import java.util.Scanner;

class Student {
    private String name;
    private double grade;

    public Student(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }
}

public class StudentGradeManager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        System.out.println("=== Student Grade Manager === - TASK 1.java:27");

        while (true) {
            System.out.print("\nEnter student name (or type 'done' to finish): - TASK 1.java:30");
            String name = scanner.nextLine().trim();

            if (name.equalsIgnoreCase("done")) {
                break;
            }

            double grade = -1;
            while (true) {
                System.out.print("Enter grade for - TASK 1.java:39" + name + ": ");
                try {
                    grade = Double.parseDouble(scanner.nextLine());
                    if (grade < 0 || grade > 100) {
                        System.out.println("⚠️ Please enter a valid grade between 0 and 100. - TASK 1.java:43");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Invalid input. Please enter a number. - TASK 1.java:48");
                }
            }

            students.add(new Student(name, grade));
        }

        if (students.isEmpty()) {
            System.out.println("\nNo students entered. Exiting program. - TASK 1.java:56");
            return;
        }

        // Calculate statistics
        double total = 0, highest = Double.MIN_VALUE, lowest = Double.MAX_VALUE;
        String topStudent = "", lowStudent = "";

        for (Student s : students) {
            double g = s.getGrade();
            total += g;

            if (g > highest) {
                highest = g;
                topStudent = s.getName();
            }

            if (g < lowest) {
                lowest = g;
                lowStudent = s.getName();
            }
        }

        double average = total / students.size();

        // Display summary
        System.out.println("\n=== Summary Report === - TASK 1.java:82");
        System.out.printf("%-20s %-10s\n", "Student Name", "Grade");
        System.out.println("");
        for (Student s : students) {
            System.out.printf("%-20s %-10.2f\n", s.getName(), s.getGrade());
        }
        System.out.println("");
        System.out.printf("Average Grade: %.2f\n", average);
        System.out.printf("Highest Grade: %.2f (%s)\n", highest, topStudent);
        System.out.printf("Lowest Grade: %.2f (%s)\n", lowest, lowStudent);

        System.out.println("\nThank you for using Student Grade Manager! - TASK 1.java:93");
        scanner.close();
    }
}

