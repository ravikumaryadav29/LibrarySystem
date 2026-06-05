package service;

import model.Book;
import model.BorrowRecord;
import model.Member;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    public static final String BOOKS_FILE   = "data/books.csv";
    public static final String MEMBERS_FILE = "data/members.csv";
    public static final String RECORDS_FILE = "data/records.csv";

    // ── Books ────────────────────────────────────────────────────

    public List<Book> loadBooks() {
        List<Book> list = new ArrayList<>();
        for (String line : readLines(BOOKS_FILE)) {
            try { list.add(Book.fromCSV(line)); }
            catch (Exception e) { /* skip malformed line */ }
        }
        return list;
    }

    public void saveBooks(List<Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book b : books) lines.add(b.toCSV());
        writeLines(BOOKS_FILE, lines);
    }

    // ── Members ──────────────────────────────────────────────────

    public List<Member> loadMembers() {
        List<Member> list = new ArrayList<>();
        for (String line : readLines(MEMBERS_FILE)) {
            try { list.add(Member.fromCSV(line)); }
            catch (Exception e) { /* skip malformed line */ }
        }
        return list;
    }

    public void saveMembers(List<Member> members) {
        List<String> lines = new ArrayList<>();
        for (Member m : members) lines.add(m.toCSV());
        writeLines(MEMBERS_FILE, lines);
    }

    // ── Borrow Records ───────────────────────────────────────────

    public List<BorrowRecord> loadRecords() {
        List<BorrowRecord> list = new ArrayList<>();
        for (String line : readLines(RECORDS_FILE)) {
            try { list.add(BorrowRecord.fromCSV(line)); }
            catch (Exception e) { /* skip malformed line */ }
        }
        return list;
    }

    public void saveRecords(List<BorrowRecord> records) {
        List<String> lines = new ArrayList<>();
        for (BorrowRecord r : records) lines.add(r.toCSV());
        writeLines(RECORDS_FILE, lines);
    }

    // ── Private Helpers ──────────────────────────────────────────

    private List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Reading " + path + ": " + e.getMessage());
        }
        return lines;
    }

    private void writeLines(String path, List<String> lines) {
        // Ensure data/ directory exists
        new File("data").mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Writing " + path + ": " + e.getMessage());
        }
    }
}
