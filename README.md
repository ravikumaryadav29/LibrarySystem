# 📚 Library Management System — Java

A complete, professional-grade Library Management System with OOP design.

---

## 📁 Project Structure

```
LibrarySystem/
├── src/
│   ├── model/
│   │   ├── Book.java           ← Book entity (id, title, author, copies)
│   │   ├── Member.java         ← Member entity (id, name, borrowed books)
│   │   └── BorrowRecord.java   ← Transaction (borrow date, due date, fine)
│   ├── service/
│   │   ├── FileService.java    ← All CSV file read/write
│   │   ├── BookService.java    ← Book CRUD + search + inventory
│   │   ├── MemberService.java  ← Member CRUD + search
│   │   ├── BorrowService.java  ← Borrow, return, overdue, history
│   │   └── ReportService.java  ← Dashboard, stats, reports
│   ├── ui/
│   │   └── ConsoleUI.java      ← All menus, input handling
│   └── main/
│       └── Main.java           ← Entry point, wires everything
└── data/
    ├── books.csv               ← Book inventory (auto-managed)
    ├── members.csv             ← Registered members (auto-managed)
    └── records.csv             ← Borrow/return history (auto-created)
```

---

## ▶️ Compile & Run (VS Code Terminal)

```bash
# Step 1 — Go to project folder
cd LibrarySystem

# Step 2 — Create output folder
mkdir out

# Step 3 — Compile all files
javac -d out src/model/Book.java src/model/Member.java src/model/BorrowRecord.java src/service/FileService.java src/service/BookService.java src/service/MemberService.java src/service/BorrowService.java src/service/ReportService.java src/ui/ConsoleUI.java src/main/Main.java

# Step 4 — Run
java -cp out main.Main
```

---

## ✅ Features

| Feature | Details |
|---|---|
| Book Management | Add, Edit, Delete, Search (title/author/category/ID) |
| Member Management | Register, Update, Delete, Search |
| Borrow System | Issue book with due date (14 days) |
| Return System | Accept return, calculate fine (₹2/day) |
| Inventory Tracking | Available copies tracked in real-time |
| Borrow Limit | Max 3 books per member at a time |
| Overdue Detection | Highlights late returns with fine amount |
| History | Full transaction history per member |
| Reports | Dashboard, category stats, most borrowed, overdue |
| File Persistence | All data saved in CSV files automatically |
| Input Validation | try-catch on all inputs, empty check, duplicate check |

---

## 🧠 Java Concepts Used

| Concept | Where |
|---|---|
| OOP (Encapsulation) | All model classes |
| Abstraction | Service layer hides logic from UI |
| Collections (List, Map) | All services |
| File I/O (BufferedReader/Writer) | FileService |
| Java Streams + Lambda | Search, filter, groupingBy |
| Exception Handling | All input + file operations |
| LocalDate (java.time) | Borrow/return/due date calculations |
| String.format | Formatted table output |

---

## 🚀 Resume Improvements

1. **Add login system** — Admin vs Member roles
2. **JDBC + MySQL** — Replace CSV with a real database
3. **Spring Boot REST API** — Make it a web service
4. **JavaFX GUI** — Visual interface with tables
5. **Email notifications** — Alert members before due date
6. **JUnit tests** — Unit test BorrowService and BookService
7. **Export to PDF** — Generate reports as PDF using iText
