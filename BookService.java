package service;

import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private List<Book>  books;
    private FileService fileService;
    private int         nextId;

    public BookService(FileService fileService) {
        this.fileService = fileService;
        this.books       = fileService.loadBooks();
        this.nextId      = computeNextId();
    }

    // ── Add ──────────────────────────────────────────────────────

    public Book addBook(String title, String author, String category, int copies) {
        String id = String.format("B%03d", nextId++);
        Book book = new Book(id, title, author, category, copies);
        books.add(book);
        save();
        return book;
    }

    // ── Update ───────────────────────────────────────────────────

    public boolean updateBook(String bookId, String title, String author,
                              String category, int totalCopies) {
        Book b = findById(bookId);
        if (b == null) return false;

        int diff = totalCopies - b.getTotalCopies();
        b.setTitle(title);
        b.setAuthor(author);
        b.setCategory(category);
        b.setTotalCopies(totalCopies);
        // Adjust available copies proportionally
        b.setAvailableCopies(Math.max(0, b.getAvailableCopies() + diff));
        save();
        return true;
    }

    // ── Delete ───────────────────────────────────────────────────

    /**
     * Deletes a book only if all copies are available
     * (no copy currently borrowed).
     */
    public boolean deleteBook(String bookId) {
        Book b = findById(bookId);
        if (b == null) return false;
        if (b.getAvailableCopies() < b.getTotalCopies()) return false; // copies out
        books.remove(b);
        save();
        return true;
    }

    // ── Search ───────────────────────────────────────────────────

    public Book findById(String bookId) {
        return books.stream()
                .filter(b -> b.getBookId().equalsIgnoreCase(bookId))
                .findFirst().orElse(null);
    }

    public List<Book> searchByTitle(String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public List<Book> searchByCategory(String keyword) {
        String kw = keyword.toLowerCase();
        return books.stream()
                .filter(b -> b.getCategory().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // ── Inventory Views ──────────────────────────────────────────

    public List<Book> getAllBooks()       { return new ArrayList<>(books); }

    public List<Book> getAvailableBooks() {
        return books.stream().filter(Book::isAvailable).collect(Collectors.toList());
    }

    public List<Book> getUnavailableBooks() {
        return books.stream().filter(b -> !b.isAvailable()).collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void save() { fileService.saveBooks(books); }

    private int computeNextId() {
        return books.stream()
                .mapToInt(b -> {
                    try { return Integer.parseInt(b.getBookId().substring(1)); }
                    catch (Exception e) { return 0; }
                })
                .max().orElse(0) + 1;
    }
}
