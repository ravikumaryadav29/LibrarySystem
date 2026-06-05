package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {

    public static final int LOAN_DAYS     = 14;  // 2 weeks
    public static final int FINE_PER_DAY  = 2;   // ₹2 per day overdue

    private String    recordId;
    private String    memberId;
    private String    bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null = not yet returned
    private int       fine;

    public BorrowRecord(String recordId, String memberId, String bookId,
                        LocalDate borrowDate) {
        this.recordId   = recordId;
        this.memberId   = memberId;
        this.bookId     = bookId;
        this.borrowDate = borrowDate;
        this.dueDate    = borrowDate.plusDays(LOAN_DAYS);
        this.returnDate = null;
        this.fine       = 0;
    }

    // ── Getters ──────────────────────────────────────────────────
    public String    getRecordId()   { return recordId; }
    public String    getMemberId()   { return memberId; }
    public String    getBookId()     { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int       getFine()       { return fine; }
    public boolean   isReturned()    { return returnDate != null; }

    /**
     * Marks the book as returned on today's date.
     * Calculates fine if returned late.
     */
    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        if (returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            this.fine = (int) daysLate * FINE_PER_DAY;
        }
    }

    /** Days remaining until due date (negative = overdue). */
    public long daysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /** CSV format for file storage. */
    public String toCSV() {
        return recordId + "," + memberId + "," + bookId + "," +
               borrowDate + "," + dueDate + "," +
               (returnDate != null ? returnDate : "NULL") + "," + fine;
    }

    public static BorrowRecord fromCSV(String line) {
        String[] p = line.split(",", 7);
        BorrowRecord r = new BorrowRecord(p[0], p[1], p[2], LocalDate.parse(p[3]));
        // Override dueDate from file
        r.dueDate = LocalDate.parse(p[4]);
        if (!p[5].equals("NULL")) {
            r.returnDate = LocalDate.parse(p[5]);
        }
        r.fine = Integer.parseInt(p[6]);
        return r;
    }

    @Override
    public String toString() {
        String status = isReturned()
                ? "Returned on " + returnDate
                : "Due: " + dueDate + (daysRemaining() < 0
                    ? "  ⚠ OVERDUE by " + Math.abs(daysRemaining()) + " days"
                    : "  (" + daysRemaining() + " days left)");
        String fineStr = fine > 0 ? "  Fine: ₹" + fine : "";
        return String.format("[%s] Member:%-6s Book:%-6s Borrowed:%s | %s%s",
                recordId, memberId, bookId, borrowDate, status, fineStr);
    }
}
