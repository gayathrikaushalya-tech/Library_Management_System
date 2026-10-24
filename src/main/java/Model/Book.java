package Model;

public class Book {
    private int id;
    private String bookCode;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private int quantity;
    private int availableQuantity;

    public Book() {
    }

    public Book(String title, String author, String category,
                String isbn, int quantity) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.isbn = isbn;
        this.quantity = quantity;
        this.availableQuantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBookCode() { return bookCode; }
    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}