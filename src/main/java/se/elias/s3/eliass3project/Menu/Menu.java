package se.elias.s3.eliass3project.Menu;

import se.elias.s3.eliass3project.service.S3Service;

import java.util.Scanner;


public class Menu {
    private final S3Service s3Service = new S3Service();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            printMenu();
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> s3Service.listFiles();
                    case 2 -> {
                        System.out.print("Fil att ladda upp: ");
                        s3Service.uploadFile(scanner.nextLine());
                    }
                    case 3 -> {
                        System.out.print("Key att ladda ned: ");
                        String key = scanner.nextLine();
                        System.out.print("Destination: ");
                        String dest = scanner.nextLine();
                        s3Service.downloadFile(key, dest);
                    }
                    case 4 -> {
                        System.out.print("Key att ta bort: ");
                        s3Service.deleteFile(scanner.nextLine());
                    }
                    case 5 -> {
                        System.out.print("Sökterm: ");
                        s3Service.searchFiles(scanner.nextLine());
                    }
                    case 6 -> {
                        System.out.print("Ny bucket: ");
                        s3Service.setBucket(scanner.nextLine());
                    }
                    case 7 -> {
                        System.out.print("Mapp att ladda upp: ");
                        s3Service.uploadFolderAsZip(scanner.nextLine());
                    }
                    case 0 -> {
                        System.out.println("Avslutar...");
                        return;
                    }
                    default -> System.out.println("Ogiltigt val");
                }
            } catch (Exception e) {
                System.err.println("Fel: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==== S3 Console App ====");
        System.out.println("1. Lista filer");
        System.out.println("2. Ladda upp fil");
        System.out.println("3. Ladda ned fil");
        System.out.println("4. Ta bort fil");
        System.out.println("5. Sök filer");
        System.out.println("6. Byt bucket");
        System.out.println("7. Ladda upp folder");
        System.out.println("0. Avsluta");
        System.out.print("Välj: ");
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Ogiltigt val, ange siffra:");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }
}