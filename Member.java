package model;

import java.util.ArrayList;
import java.util.List;

public class Member {

    public static final int MAX_BORROW_LIMIT = 3; // max books at a time

    private String       memberId;
    private String       name;
    private String       email;
    private String       phone;
    private List<String> borrowedBookIds;

    public Member(String memberId, String name, String email, String phone) {
        this.memberId       = memberId;
        this.name           = name;
        this.email          = email;
        this.phone          = phone;
        this.borrowedBookIds = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────
    public String       getMemberId()       { return memberId; }
    public String       getName()           { return name; }
    public String       getEmail()          { return email; }
    public String       getPhone()          { return phone; }
    public List<String> getBorrowedBookIds(){ return borrowedBookIds; }

    // ── Setters ──────────────────────────────────────────────────
    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // ── Helpers ──────────────────────────────────────────────────
    public boolean canBorrow() {
        return borrowedBookIds.size() < MAX_BORROW_LIMIT;
    }

    public boolean hasBorrowed(String bookId) {
        return borrowedBookIds.contains(bookId);
    }

    public void addBorrowedBook(String bookId)    { borrowedBookIds.add(bookId); }
    public void removeBorrowedBook(String bookId) { borrowedBookIds.remove(bookId); }

    /** CSV format:  memberId,name,email,phone,bookId1;bookId2 */
    public String toCSV() {
        String books = String.join(";", borrowedBookIds);
        return memberId + "," + name + "," + email + "," + phone + "," + books;
    }

    public static Member fromCSV(String line) {
        String[] p = line.split(",", 5);
        Member m = new Member(p[0], p[1], p[2], p[3]);
        if (p.length == 5 && !p[4].isEmpty()) {
            for (String bid : p[4].split(";")) {
                if (!bid.isEmpty()) m.addBorrowedBook(bid);
            }
        }
        return m;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | %-25s | %-12s | Books: %d/%d",
                memberId, name, email, phone,
                borrowedBookIds.size(), MAX_BORROW_LIMIT);
    }
}
