package main;

import service.*;
import ui.ConsoleUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Wire up dependencies
        FileService   fileService   = new FileService();
        BookService   bookService   = new BookService(fileService);
        MemberService memberService = new MemberService(fileService);
        BorrowService borrowService = new BorrowService(bookService, memberService, fileService);
        ReportService reportService = new ReportService(bookService, memberService, borrowService);

        Scanner    scanner = new Scanner(System.in);
        ConsoleUI  ui      = new ConsoleUI(bookService, memberService,
                                           borrowService, reportService, scanner);
        // Show dashboard on startup
        reportService.printDashboard();

        // Start the menu loop
        ui.run();

        scanner.close();
    }
}
