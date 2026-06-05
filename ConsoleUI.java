package ui;

import model.Book;
import model.BorrowRecord;
import model.Member;
import service.*;

import java.util.List;
import java.util.Scanner;

/**
 * ConsoleUI — handles all user interaction (menus, input, output).
 * Delegates logic to service classes.
 */
public class ConsoleUI {

    private final BookService   bookService;
    private final MemberService memberService;
    private final BorrowService borrowService;
    private final ReportService reportService;
    private final Scanner       scanner;

    public ConsoleUI(BookService bookService, MemberService memberService,
                     BorrowService borrowService, ReportService reportService,
                     Scanner scanner) {
        this.bookService   = bookService;
        this.memberService = memberService;
        this.borrowService = borrowService;
        this.reportService = reportService;
        this.scanner       = scanner;
    }

    // ── Main Loop ────────────────────────────────────────────────

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Your choice");
            switch (choice) {
                case 1: bookMenu();     break;
                case 2: memberMenu();   break;
                case 3: borrowMenu();   break;
                case 4: reportMenu();   break;
                case 5: running = false; break;
                default: System.out.println("  ⚠  Invalid choice.");
            }
        }
        System.out.println("\n  Thank you for using Library Management System! 👋\n");
    }

    // ══════════════════════════════════════════════════════════════
    // BOOK MENU
    // ══════════════════════════════════════════════════════════════

    private void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  ┌─────────────────────────────────┐");
            System.out.println("  │         📚 BOOK MANAGEMENT       │");
            System.out.println("  ├─────────────────────────────────┤");
            System.out.println("  │  1. Add New Book                │");
            System.out.println("  │  2. View All Books              │");
            System.out.println("  │  3. Search Books                │");
            System.out.println("  │  4. Update Book                 │");
            System.out.println("  │  5. Delete Book                 │");
            System.out.println("  │  6. View Available Books        │");
            System.out.println("  │  7. Back                        │");
            System.out.println("  └─────────────────────────────────┘");

            switch (readInt("Choice")) {
                case 1: addBook();            break;
                case 2: viewAllBooks();       break;
                case 3: searchBooks();        break;
                case 4: updateBook();         break;
                case 5: deleteBook();         break;
                case 6: viewAvailableBooks(); break;
                case 7: back = true;          break;
                default: System.out.println("  ⚠  Invalid option.");
            }
        }
    }

    private void addBook() {
        System.out.println("\n  ── Add New Book ──");
        String title    = readString("  Title");
        String author   = readString("  Author");
        String category = readString("  Category");
        int    copies   = readInt   ("  Number of Copies");

        Book b = bookService.addBook(title, author, category, copies);
        System.out.println("  ✅  Book added: " + b);
    }

    private void viewAllBooks() {
        List<Book> books = bookService.getAllBooks();
        printLine();
        System.out.printf("  %-6s %-30s %-20s %-15s %s%n",
                "ID", "Title", "Author", "Category", "Available/Total");
        printLine();
        if (books.isEmpty()) { System.out.println("  No books found."); }
        else books.forEach(b -> System.out.println("  " + b));
        printLine();
        System.out.println("  Total: " + books.size() + " books");
    }

    private void searchBooks() {
        System.out.println("\n  Search by: 1.Title  2.Author  3.Category  4.ID");
        int opt = readInt("  Option");
        List<Book> result;
        switch (opt) {
            case 1: result = bookService.searchByTitle(readString("  Keyword"));    break;
            case 2: result = bookService.searchByAuthor(readString("  Keyword"));   break;
            case 3: result = bookService.searchByCategory(readString("  Keyword")); break;
            case 4:
                Book b = bookService.findById(readString("  Book ID"));
                System.out.println(b != null ? "  " + b : "  ❌  Not found.");
                return;
            default: System.out.println("  ⚠  Invalid."); return;
        }
        if (result.isEmpty()) System.out.println("  No results found.");
        else result.forEach(b -> System.out.println("  " + b));
    }

    private void updateBook() {
        String id = readString("  Book ID to update");
        Book b = bookService.findById(id);
        if (b == null) { System.out.println("  ❌  Book not found."); return; }

        System.out.println("  Current: " + b);
        String title    = readStringDefault("  Title    [" + b.getTitle()    + "]", b.getTitle());
        String author   = readStringDefault("  Author   [" + b.getAuthor()   + "]", b.getAuthor());
        String category = readStringDefault("  Category [" + b.getCategory() + "]", b.getCategory());
        int    copies   = readIntDefault   ("  Copies   [" + b.getTotalCopies() + "]", b.getTotalCopies());

        boolean ok = bookService.updateBook(id, title, author, category, copies);
        System.out.println(ok ? "  ✅  Updated." : "  ❌  Update failed.");
    }

    private void deleteBook() {
        String id = readString("  Book ID to delete");
        boolean ok = bookService.deleteBook(id);
        if (ok) System.out.println("  ✅  Book deleted.");
        else    System.out.println("  ❌  Cannot delete (book not found or copies currently borrowed).");
    }

    private void viewAvailableBooks() {
        List<Book> books = bookService.getAvailableBooks();
        System.out.println("\n  📗  AVAILABLE BOOKS (" + books.size() + ")");
        printLine();
        if (books.isEmpty()) System.out.println("  No available books.");
        else books.forEach(b -> System.out.println("  " + b));
        printLine();
    }

    // ══════════════════════════════════════════════════════════════
    // MEMBER MENU
    // ══════════════════════════════════════════════════════════════

    private void memberMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  ┌─────────────────────────────────┐");
            System.out.println("  │       👥 MEMBER MANAGEMENT       │");
            System.out.println("  ├─────────────────────────────────┤");
            System.out.println("  │  1. Register New Member         │");
            System.out.println("  │  2. View All Members            │");
            System.out.println("  │  3. Search Member               │");
            System.out.println("  │  4. Update Member               │");
            System.out.println("  │  5. Delete Member               │");
            System.out.println("  │  6. View Member's Borrowed Books│");
            System.out.println("  │  7. Back                        │");
            System.out.println("  └─────────────────────────────────┘");

            switch (readInt("Choice")) {
                case 1: registerMember();     break;
                case 2: viewAllMembers();     break;
                case 3: searchMember();       break;
                case 4: updateMember();       break;
                case 5: deleteMember();       break;
                case 6: memberBorrowedBooks();break;
                case 7: back = true;          break;
                default: System.out.println("  ⚠  Invalid option.");
            }
        }
    }

    private void registerMember() {
        System.out.println("\n  ── Register New Member ──");
        String name  = readString("  Full Name");
        String email = readString("  Email");
        String phone = readString("  Phone");

        Member m = memberService.addMember(name, email, phone);
        if (m != null) System.out.println("  ✅  Member registered: " + m);
        else            System.out.println("  ❌  Email already registered.");
    }

    private void viewAllMembers() {
        List<Member> members = memberService.getAllMembers();
        printLine();
        System.out.printf("  %-6s %-20s %-25s %-13s %s%n",
                "ID", "Name", "Email", "Phone", "Books");
        printLine();
        if (members.isEmpty()) System.out.println("  No members registered.");
        else members.forEach(m -> System.out.println("  " + m));
        printLine();
        System.out.println("  Total: " + members.size() + " members");
    }

    private void searchMember() {
        System.out.println("\n  Search by: 1.Name  2.ID");
        int opt = readInt("  Option");
        if (opt == 1) {
            List<Member> result = memberService.searchByName(readString("  Name keyword"));
            if (result.isEmpty()) System.out.println("  No results.");
            else result.forEach(m -> System.out.println("  " + m));
        } else if (opt == 2) {
            Member m = memberService.findById(readString("  Member ID"));
            System.out.println(m != null ? "  " + m : "  ❌  Not found.");
        }
    }

    private void updateMember() {
        String id = readString("  Member ID to update");
        Member m = memberService.findById(id);
        if (m == null) { System.out.println("  ❌  Member not found."); return; }

        System.out.println("  Current: " + m);
        String name  = readStringDefault("  Name  [" + m.getName()  + "]", m.getName());
        String email = readStringDefault("  Email [" + m.getEmail() + "]", m.getEmail());
        String phone = readStringDefault("  Phone [" + m.getPhone() + "]", m.getPhone());

        boolean ok = memberService.updateMember(id, name, email, phone);
        System.out.println(ok ? "  ✅  Updated." : "  ❌  Update failed.");
    }

    private void deleteMember() {
        String id = readString("  Member ID to delete");
        boolean ok = memberService.deleteMember(id);
        if (ok) System.out.println("  ✅  Member removed.");
        else    System.out.println("  ❌  Cannot delete (not found or has active borrows).");
    }

    private void memberBorrowedBooks() {
        String id = readString("  Member ID");
        Member m  = memberService.findById(id);
        if (m == null) { System.out.println("  ❌  Member not found."); return; }

        System.out.println("\n  Books borrowed by " + m.getName() + ":");
        if (m.getBorrowedBookIds().isEmpty()) {
            System.out.println("  No books currently borrowed.");
        } else {
            m.getBorrowedBookIds().forEach(bid -> {
                Book b = bookService.findById(bid);
                System.out.println("    • " + (b != null ? b.getTitle() + " (" + bid + ")" : bid));
            });
        }
    }

    // ══════════════════════════════════════════════════════════════
    // BORROW / RETURN MENU
    // ══════════════════════════════════════════════════════════════

    private void borrowMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  ┌─────────────────────────────────┐");
            System.out.println("  │     🔄 BORROW / RETURN           │");
            System.out.println("  ├─────────────────────────────────┤");
            System.out.println("  │  1. Borrow a Book               │");
            System.out.println("  │  2. Return a Book               │");
            System.out.println("  │  3. View All Active Borrows     │");
            System.out.println("  │  4. View Member Borrow History  │");
            System.out.println("  │  5. View Overdue Books          │");
            System.out.println("  │  6. Back                        │");
            System.out.println("  └─────────────────────────────────┘");

            switch (readInt("Choice")) {
                case 1: borrowBook();         break;
                case 2: returnBook();         break;
                case 3: viewActiveBorrows();  break;
                case 4: memberHistory();      break;
                case 5: viewOverdue();        break;
                case 6: back = true;          break;
                default: System.out.println("  ⚠  Invalid option.");
            }
        }
    }

    private void borrowBook() {
        String memberId = readString("  Member ID");
        String bookId   = readString("  Book ID");

        BorrowRecord r = borrowService.borrowBook(memberId, bookId);
        if (r != null) {
            System.out.println("  ✅  Book issued successfully!");
            System.out.println("      Record ID : " + r.getRecordId());
            System.out.println("      Due Date  : " + r.getDueDate() +
                               " (" + BorrowRecord.LOAN_DAYS + " days)");
        } else {
            System.out.println("  ❌  " + borrowService.getLastError());
        }
    }

    private void returnBook() {
        String memberId = readString("  Member ID");
        String bookId   = readString("  Book ID");

        BorrowRecord r = borrowService.returnBook(memberId, bookId);
        if (r != null) {
            System.out.println("  ✅  Book returned successfully!");
            if (r.getFine() > 0)
                System.out.println("  💸  Fine charged: ₹" + r.getFine() +
                        " (returned " + Math.abs(r.daysRemaining()) + " days late)");
            else
                System.out.println("  🎉  Returned on time. No fine.");
        } else {
            System.out.println("  ❌  " + borrowService.getLastError());
        }
    }

    private void viewActiveBorrows() {
        List<BorrowRecord> active = borrowService.getActiveRecords();
        System.out.println("\n  📋  ACTIVE BORROWS (" + active.size() + ")");
        printLine();
        if (active.isEmpty()) System.out.println("  No active borrows.");
        else active.forEach(r -> System.out.println("  " + r));
        printLine();
    }

    private void memberHistory() {
        String memberId = readString("  Member ID");
        List<BorrowRecord> history = borrowService.getMemberHistory(memberId);
        System.out.println("\n  📜  Borrow history for " + memberId + ":");
        printLine();
        if (history.isEmpty()) System.out.println("  No history found.");
        else history.forEach(r -> System.out.println("  " + r));
        printLine();
    }

    private void viewOverdue() {
        reportService.printOverdueReport();
    }

    // ══════════════════════════════════════════════════════════════
    // REPORT MENU
    // ══════════════════════════════════════════════════════════════

    private void reportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n  ┌─────────────────────────────────┐");
            System.out.println("  │         📊 REPORTS               │");
            System.out.println("  ├─────────────────────────────────┤");
            System.out.println("  │  1. Dashboard Summary           │");
            System.out.println("  │  2. Inventory by Category       │");
            System.out.println("  │  3. Overdue Report              │");
            System.out.println("  │  4. Most Borrowed Books         │");
            System.out.println("  │  5. Back                        │");
            System.out.println("  └─────────────────────────────────┘");

            switch (readInt("Choice")) {
                case 1: reportService.printDashboard();             break;
                case 2: reportService.printInventoryByCategory();   break;
                case 3: reportService.printOverdueReport();         break;
                case 4: reportService.printMostBorrowedBooks();     break;
                case 5: back = true;                                break;
                default: System.out.println("  ⚠  Invalid option.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // MENUS & HELPERS
    // ══════════════════════════════════════════════════════════════

    private void printMainMenu() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║           MAIN MENU                  ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.println("  ║  1. 📚  Book Management              ║");
        System.out.println("  ║  2. 👥  Member Management            ║");
        System.out.println("  ║  3. 🔄  Borrow / Return              ║");
        System.out.println("  ║  4. 📊  Reports & Statistics         ║");
        System.out.println("  ║  5. 🚪  Exit                         ║");
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    private void printBanner() {
        System.out.println("\n  ╔══════════════════════════════════════════════╗");
        System.out.println("  ║                                              ║");
        System.out.println("  ║     📚  LIBRARY MANAGEMENT SYSTEM           ║");
        System.out.println("  ║         Built with Java | OOP Design        ║");
        System.out.println("  ║                                              ║");
        System.out.println("  ╚══════════════════════════════════════════════╝");
    }

    private void printLine() {
        System.out.println("  " + "─".repeat(75));
    }

    // ── Input Helpers ────────────────────────────────────────────

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                String s = scanner.nextLine().trim();
                if (!s.isEmpty()) return s;
                System.out.println("  ⚠  Cannot be empty.");
            } catch (Exception e) {
                System.out.println("  ⚠  Invalid input.");
            }
        }
    }

    private String readStringDefault(String prompt, String defaultVal) {
        System.out.print(prompt + ": ");
        try {
            String s = scanner.nextLine().trim();
            return s.isEmpty() ? defaultVal : s;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt + ": ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ⚠  Please enter a number.");
            }
        }
    }

    private int readIntDefault(String prompt, int defaultVal) {
        System.out.print(prompt + ": ");
        try {
            String s = scanner.nextLine().trim();
            return s.isEmpty() ? defaultVal : Integer.parseInt(s);
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
