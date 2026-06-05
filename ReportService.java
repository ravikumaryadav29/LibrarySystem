package service;

import model.Book;
import model.BorrowRecord;
import model.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReportService — generates summary reports and statistics.
 */
public class ReportService {

    private final BookService   bookService;
    private final MemberService memberService;
    private final BorrowService borrowService;

    public ReportService(BookService bookService, MemberService memberService,
                         BorrowService borrowService) {
        this.bookService   = bookService;
        this.memberService = memberService;
        this.borrowService = borrowService;
    }

    // ── Dashboard ────────────────────────────────────────────────

    public void printDashboard() {
        List<Book>         allBooks   = bookService.getAllBooks();
        List<Member>       allMembers = memberService.getAllMembers();
        List<BorrowRecord> active     = borrowService.getActiveRecords();
        List<BorrowRecord> overdue    = borrowService.getOverdueRecords();

        int totalBooks     = allBooks.stream().mapToInt(Book::getTotalCopies).sum();
        int availableBooks = allBooks.stream().mapToInt(Book::getAvailableCopies).sum();
        int borrowedBooks  = totalBooks - availableBooks;

        System.out.println("\n  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║           📚  LIBRARY DASHBOARD                  ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf ("  ║  📖  Total Book Titles   : %-6d                 ║%n", allBooks.size());
        System.out.printf ("  ║  📦  Total Copies        : %-6d                 ║%n", totalBooks);
        System.out.printf ("  ║  ✅  Available Copies    : %-6d                 ║%n", availableBooks);
        System.out.printf ("  ║  📤  Currently Borrowed  : %-6d                 ║%n", borrowedBooks);
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf ("  ║  👥  Total Members       : %-6d                 ║%n", allMembers.size());
        System.out.printf ("  ║  🔄  Active Borrows      : %-6d                 ║%n", active.size());
        System.out.printf ("  ║  ⚠   Overdue Books       : %-6d                 ║%n", overdue.size());
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    // ── Inventory by Category ────────────────────────────────────

    public void printInventoryByCategory() {
        Map<String, List<Book>> byCategory = bookService.getAllBooks().stream()
                .collect(Collectors.groupingBy(Book::getCategory));

        System.out.println("\n  📊  INVENTORY BY CATEGORY");
        System.out.println("  " + "─".repeat(55));
        System.out.printf ("  %-20s %10s %10s%n", "Category", "Total", "Available");
        System.out.println("  " + "─".repeat(55));

        byCategory.forEach((cat, books) -> {
            int total     = books.stream().mapToInt(Book::getTotalCopies).sum();
            int available = books.stream().mapToInt(Book::getAvailableCopies).sum();
            System.out.printf("  %-20s %10d %10d%n", cat, total, available);
        });
        System.out.println("  " + "─".repeat(55));
    }

    // ── Overdue Report ───────────────────────────────────────────

    public void printOverdueReport() {
        List<BorrowRecord> overdue = borrowService.getOverdueRecords();
        System.out.println("\n  ⚠  OVERDUE BOOKS REPORT");
        System.out.println("  " + "─".repeat(70));
        if (overdue.isEmpty()) {
            System.out.println("  ✅  No overdue books! Great.");
        } else {
            for (BorrowRecord r : overdue) {
                Member m = memberService.findById(r.getMemberId());
                Book   b = bookService.findById(r.getBookId());
                long   daysLate = Math.abs(r.daysRemaining());
                System.out.printf("  %-8s | %-20s | %-25s | %d days late | Fine: ₹%d%n",
                        r.getRecordId(),
                        m != null ? m.getName() : r.getMemberId(),
                        b != null ? b.getTitle() : r.getBookId(),
                        daysLate,
                        daysLate * BorrowRecord.FINE_PER_DAY);
            }
        }
        System.out.println("  " + "─".repeat(70));
    }

    // ── Most Borrowed Books ──────────────────────────────────────

    public void printMostBorrowedBooks() {
        Map<String, Long> freq = borrowService.getAllRecords().stream()
                .collect(Collectors.groupingBy(BorrowRecord::getBookId, Collectors.counting()));

        System.out.println("\n  🏆  MOST BORROWED BOOKS (Top 5)");
        System.out.println("  " + "─".repeat(55));

        freq.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e -> {
                    Book book = bookService.findById(e.getKey());
                    String title = book != null ? book.getTitle() : e.getKey();
                    System.out.printf("  %-35s → %d times%n", title, e.getValue());
                });

        if (freq.isEmpty()) System.out.println("  No borrow history yet.");
        System.out.println("  " + "─".repeat(55));
    }
}
