package service;

import model.Book;
import model.BorrowRecord;
import model.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowService {

    private List<BorrowRecord> records;
    private final BookService  bookService;
    private final MemberService memberService;
    private final FileService   fileService;
    private int                 nextId;

    public BorrowService(BookService bookService, MemberService memberService,
                         FileService fileService) {
        this.bookService   = bookService;
        this.memberService = memberService;
        this.fileService   = fileService;
        this.records       = fileService.loadRecords();
        this.nextId        = computeNextId();
    }

    // ── Borrow ───────────────────────────────────────────────────

    /**
     * Issues a book to a member.
     *
     * Checks:
     *   1. Member exists
     *   2. Book exists and is available
     *   3. Member hasn't already borrowed this book
     *   4. Member hasn't reached borrow limit
     *
     * Returns the new BorrowRecord on success, null on failure.
     * Sets errorMessage with the reason for failure.
     */
    private String lastError = "";

    public BorrowRecord borrowBook(String memberId, String bookId) {
        Member member = memberService.findById(memberId);
        if (member == null) { lastError = "Member not found: " + memberId; return null; }

        Book book = bookService.findById(bookId);
        if (book == null)    { lastError = "Book not found: " + bookId; return null; }

        if (!book.isAvailable()) {
            lastError = "No copies available for: " + book.getTitle(); return null;
        }
        if (member.hasBorrowed(bookId)) {
            lastError = "Member has already borrowed this book."; return null;
        }
        if (!member.canBorrow()) {
            lastError = "Member has reached the borrow limit of " +
                        Member.MAX_BORROW_LIMIT + " books.";
            return null;
        }

        // All checks passed — create record
        String id = String.format("R%04d", nextId++);
        BorrowRecord record = new BorrowRecord(id, memberId, bookId, LocalDate.now());
        records.add(record);

        // Update book and member state
        book.borrow();
        member.addBorrowedBook(bookId);

        save();
        return record;
    }

    // ── Return ───────────────────────────────────────────────────

    /**
     * Processes a book return.
     * Returns the BorrowRecord (with fine if any) on success, null if not found.
     */
    public BorrowRecord returnBook(String memberId, String bookId) {
        // Find the open (not-yet-returned) record
        BorrowRecord record = records.stream()
                .filter(r -> r.getMemberId().equals(memberId)
                          && r.getBookId().equals(bookId)
                          && !r.isReturned())
                .findFirst().orElse(null);

        if (record == null) {
            lastError = "No active borrow record found for member " +
                        memberId + " and book " + bookId;
            return null;
        }

        Member member = memberService.findById(memberId);
        Book   book   = bookService.findById(bookId);

        record.markReturned(LocalDate.now());

        if (member != null) member.removeBorrowedBook(bookId);
        if (book   != null) book.returnBook();

        save();
        return record;
    }

    // ── Queries ──────────────────────────────────────────────────

    public List<BorrowRecord> getAllRecords() { return new ArrayList<>(records); }

    public List<BorrowRecord> getActiveRecords() {
        return records.stream().filter(r -> !r.isReturned()).collect(Collectors.toList());
    }

    public List<BorrowRecord> getOverdueRecords() {
        return records.stream()
                .filter(r -> !r.isReturned() && r.daysRemaining() < 0)
                .collect(Collectors.toList());
    }

    public List<BorrowRecord> getMemberHistory(String memberId) {
        return records.stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public String getLastError() { return lastError; }

    // ── Helpers ──────────────────────────────────────────────────

    private void save() {
        fileService.saveBooks(bookService.getAllBooks());
        fileService.saveMembers(memberService.getAllMembers());
        fileService.saveRecords(records);
    }

    private int computeNextId() {
        return records.stream()
                .mapToInt(r -> {
                    try { return Integer.parseInt(r.getRecordId().substring(1)); }
                    catch (Exception e) { return 0; }
                })
                .max().orElse(0) + 1;
    }
}
