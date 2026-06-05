package model;

public class Book {

    private String bookId;
    private String title;
    private String author;
    private String category;
    private int    totalCopies;
    private int    availableCopies;

    public Book(String bookId, String title, String author,
                String category, int totalCopies) {
        this.bookId          = bookId;
        this.title           = title;
        this.author          = author;
        this.category        = category;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }

    // ── Getters ──────────────────────────────────────────────────
    public String getBookId()          { return bookId; }
    public String getTitle()           { return title; }
    public String getAuthor()          { return author; }
    public String getCategory()        { return category; }
    public int    getTotalCopies()     { return totalCopies; }
    public int    getAvailableCopies() { return availableCopies; }

    // ── Setters ──────────────────────────────────────────────────
    public void setTitle(String title)       { this.title = title; }
    public void setAuthor(String author)     { this.author = author; }
    public void setCategory(String cat)      { this.category = cat; }
    public void setTotalCopies(int n)        { this.totalCopies = n; }
    public void setAvailableCopies(int n)    { this.availableCopies = n; }

    // ── Helpers ──────────────────────────────────────────────────
    public boolean isAvailable() { return availableCopies > 0; }

    /** Called when a member borrows this book. */
    public void borrow() {
        if (availableCopies > 0) availableCopies--;
    }

    /** Called when a member returns this book. */
    public void returnBook() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    /** CSV format for saving to file. */
    public String toCSV() {
        return bookId + "," + title + "," + author + "," +
               category + "," + totalCopies + "," + availableCopies;
    }

    /** Create Book object from a CSV line. */
    public static Book fromCSV(String line) {
        String[] p = line.split(",", 6);
        Book b = new Book(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]));
        b.setAvailableCopies(Integer.parseInt(p[5]));
        return b;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s | %-20s | %-15s | Available: %d/%d",
                bookId, title, author, category, availableCopies, totalCopies);
    }
}
