Guava Collections Questions

1. What is the primary purpose of Guava collections utilities?

   Collection manipulation
   Guava collections utilities are designed to provide enhanced capabilities for collection manipulation.

2. Which of the following is a feature of Guava collections?

   Immutable collections
   Guava collections include immutable collections, which cannot be modified after creation.

3. What does the 'Multiset' interface in Guava represent?

   A set that allows duplicate elements
   The 'Multiset' interface allows for the representation of a set that can contain duplicate elements.

4. Which collection type in Guava is used for key-value pairs?

   Table
   The 'Table' collection in Guava is designed to store key-value pairs, allowing for two keys per value.

5. What is a 'BiMap' in Guava?

   A bidirectional map
   'BiMap' is a special type of map that maintains a one-to-one correspondence between keys and values.

--------------------------------------------------------------

Guava Multiset Questions

1. What is a Multiset in Guava?

   A set that allows duplicate elements
   A Multiset is a set that allows duplicate elements, making it different from a standard set.

2. Which interface does Guava's Multiset implement?

   java.util.Collection
   Guava's Multiset implements the Collection interface.

3. How does a Multiset count the occurrences of an element?

   By maintaining a frequency count
   A Multiset maintains a frequency count for each element to track how many times it appears. Stores in the Map<T, Count> object.
   Count - class that holds the number of occurrences of the element in the collection.

4. Which method would you use to get the count of a specific element in a Multiset?

   count()
   The method count() is used to get the count of a specific element in a Multiset.

5. What is the primary advantage of using a Multiset over a traditional Set?

   Ability to handle duplicates
   The primary advantage of using a Multiset is its ability to handle duplicates.

--------------------------------------------------------------

Multimap Questions

1. Map<K,Collection<V>> asMap()

   Returns a view of this multimap as a Map from each distinct key to the nonempty collection of that key's associated values.

2. What is a Multimap in Guava?

   A map that allows duplicate keys
   A Multimap in Guava is a map that allows duplicate keys, where each key can map to multiple values.

3. Which interface does Guava's Multimap implement?

   java.util.Map
   Guava's Multimap implements the Map interface.

4. How does a Multimap handle duplicate keys?

   By storing multiple values for each key
   A Multimap stores multiple values for each key, allowing for duplicate keys.

5. What is the primary advantage of using a Multimap over a traditional Map?

   Ability to handle duplicate keys
   The primary advantage of using a Multimap is its ability to handle duplicate keys.

6. Which class is used to create a Multimap in Guava?

   АrrayListMultimap
   HashMultimap
   TreeMultimap

7. How do you retrieve all values associated with a key in a Multimap?
   
   get()
   You use the get() method to retrieve all values associated with a key in a Multimap.

8. What will happen if you put a new value for an existing key in a Multimap?

   It adds the new value to the list of values
   In a Multimap, if you put a new value for an existing key, it adds the new value to the list of values for that key.

--------------------------------------------------------------

BiMap Questions

1. What is a BiMap in Guava?
   
   A bidirectional map that allows one-to-one mapping
   A BiMap is a bidirectional map that allows one-to-one mapping between keys and values.

2. Which class in Guava is used to create a BiMap?
   
   HashBiMap
   TreeBiMap
   LinkedHashBiMap
   Guava provides several implementations of BiMap including HashBiMap, TreeBiMap, and LinkedHashBiMap.

3. What will happen if you try to put a key-value pair in a BiMap where the value already exists?

   It will throw an exception
   In a BiMap, adding a key-value pair with an existing value will overwrite the existing mapping.

4. Which method is used to get the key associated with a value in a BiMap?

   inverse().get()
   You can use the inverse() method followed by get() to retrieve the key associated with a given value in a BiMap.
   By inverting the map, you get a map where keys are values and values are keys.

--------------------------------------------------------------

   Table Questions

1. What is a Table in Guava?
   
   A map that allows two keys per value
   A Table in Guava is a map that allows two keys per value, making it different from a standard map. (Table<R, C, V> is a Map<R, Map<C, V>>)
   A Table in Guava is a data structure that allows you to store key-value pairs in a two-dimensional way.

   R - row key
   C - column key
   V - value

2. How do you retrieve a value from a Guava Table?

   Using get(rowKey, columnKey)
   Values in a Guava Table are retrieved using the get(rowKey, columnKey) method.

3. What is the purpose of the 'Table' interface in Guava?

   To represent a matrix of values
   The 'Table' interface in Guava is intended to represent a matrix or a two-dimensional array of values.

4. Which interface is used to create a Guava Table?

   HashBasedTable
   TreeBasedTable

--------------------------------------------------------------

This is an excellent critique from the Guava wiki. Let me break down each concern and then compare with Guava's
approach.


Guava introduces many advanced collections based on developers' experience in application development works. Given below is a list of useful collections −

Useful Collections
1	Multiset
An extension to Set interface to allow duplicate elements. -> can be used in a key value pair cases, where value is a occurrence of the key

2	Multimap
An extension to Map interface so that its keys can be mapped to multiple values at a time.

3	BiMap
An extension to Map interface to support inverse operations.

4	Table
Table represents a special map where two keys can be specified in combined fashion to refer to a single value.

## Problems with `Collections.unmodifiableXXX`

### 1. **Unwieldy and Verbose**

```java
// JDK approach - verbose defensive copying
public class Library {
    private final List<Book> books = new ArrayList<>();

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public void setBooks(List<Book> newBooks) {
        this.books.clear();
        this.books.addAll(newBooks); // Still need to copy defensively
    }
}

// Guava approach - cleaner
public class Library {
    private final ImmutableList<Book> books;

    public Library(List<Book> books) {
        this.books = ImmutableList.copyOf(books);
    }

    public ImmutableList<Book> getBooks() {
        return books; // No wrapper needed!
    }
}
```

### 2. **Unsafe: Not Truly Immutable**

This is the **critical flaw**:

```java
// JDK - DANGEROUS!
List<String> original = new ArrayList<>(Arrays.asList("A", "B", "C"));
List<String> unmodifiable = Collections.unmodifiableList(original);

System.out.

println(unmodifiable); // [A, B, C]

// Someone with a reference to 'original' can still modify it!
original.

add("D");
System.out.

println(unmodifiable); // [A, B, C, D] - Changed!

// Guava - SAFE
List<String> original2 = new ArrayList<>(Arrays.asList("A", "B", "C"));
ImmutableList<String> immutable = ImmutableList.copyOf(original2);

original2.

add("D");
System.out.

println(immutable); // [A, B, C] - Unchanged!
```

### 3. **Inefficient: Mutable Collection Overhead**

```java
// JDK unmodifiable collections are just wrappers
// They still carry:
// - Concurrent modification counters (modCount)
// - Extra capacity in ArrayList (unused space)
// - Hash table load factors and resize logic
// - Synchronization overhead in some cases

List<String> list = new ArrayList<>(100);
list.

add("A");

// ArrayList has capacity for 100 elements, but only 1 used
List<String> unmod = Collections.unmodifiableList(list);
// Still wastes space for 99 unused slots!

// Guava creates optimized structures
ImmutableList<String> immutable = ImmutableList.of("A");
// Compact, specialized implementation with minimal overhead
```

## Guava Immutable Collections: Key Advantages

### **True Immutability**

```java
// Guava guarantees immutability through copying
ImmutableList<String> list = ImmutableList.of("A", "B", "C");
// No one can modify this, period

ImmutableSet<Integer> set = ImmutableSet.copyOf(Arrays.asList(1, 2, 3));
// Even the original can't affect this

ImmutableMap<String, Integer> map = ImmutableMap.of(
        "one", 1,
        "two", 2
);
// Completely immutable
```

### **Better Performance**

```java
// Optimized implementations
ImmutableList.of("A") // Uses SingletonImmutableList
ImmutableList.

of("A","B") // Uses RegularImmutableList (compact array)
ImmutableSet.

of("A") // Uses SingletonImmutableSet (no hash table!)

// Compare to JDK:
Collections.

unmodifiableList(Arrays.asList("A"))
// Wrapper → Arrays.ArrayList → array
// Multiple layers of indirection
```

### **Convenient Factory Methods**

```java
// Guava - expressive and convenient
ImmutableList<String> list = ImmutableList.of("A", "B", "C");
ImmutableSet<Integer> set = ImmutableSet.of(1, 2, 3, 4, 5);
ImmutableMap<String, Integer> map = ImmutableMap.of("a", 1, "b", 2);

// Builder pattern for complex construction
ImmutableList<String> computed = ImmutableList.<String>builder()
        .add("first")
        .addAll(someCollection)
        .add("last")
        .build();

// JDK (pre-Java 9) - awkward
List<String> list = Collections.unmodifiableList(
        new ArrayList<>(Arrays.asList("A", "B", "C"))
);
```

### **Thread-Safety by Default**

```java
// Guava: immutable = thread-safe, no synchronization needed
private final ImmutableList<Task> tasks = ImmutableList.copyOf(initialTasks);

public ImmutableList<Task> getTasks() {
    return tasks; // Safe to share across threads
}

// JDK: need to be careful
private final List<Task> tasks =
        Collections.unmodifiableList(new ArrayList<>(initialTasks));
// Still need to ensure the underlying list isn't accessed elsewhere
```

## Java 9+ Improvement: `List.of()`, `Set.of()`, `Map.of()`

Java 9 addressed some of these concerns:

```java
// Java 9+ - similar to Guava
List<String> list = List.of("A", "B", "C"); // Truly immutable
Set<Integer> set = Set.of(1, 2, 3);
Map<String, Integer> map = Map.of("a", 1, "b", 2);
```

However, **Guava still offers advantages**:

- More flexible builders
- Richer API (sorting, filtering methods)
- `copyOf()` methods for safe defensive copying
- Specialized types like `ImmutableSortedSet`, `ImmutableMultiset`
- Null-hostile (prevents nulls by default)

## Summary

| Feature               | `Collections.unmodifiable` | Guava Immutable     | Java 9+ `List.of()` |
|-----------------------|----------------------------|---------------------|---------------------|
| **True Immutability** | ❌ (wrapper only)           | ✅ (deep copy)       | ✅ (new structure)   |
| **Performance**       | ❌ (mutable overhead)       | ✅ (optimized)       | ✅ (optimized)       |
| **Convenience**       | ❌ (verbose)                | ✅ (factory methods) | ✅ (factory methods) |
| **Builder Pattern**   | ❌                          | ✅                   | ❌                   |
| **Specialized Types** | ❌                          | ✅                   | Limited             |

**Bottom line**: `Collections.unmodifiableXXX` was never meant to create truly immutable collections—just to prevent
accidental modification. For real immutability, use Guava's `ImmutableXXX` or Java 9+'s `List.of()` family.



-----

Great question! Let's clarify **defensive copying** and the type of copy `ImmutableList.copyOf()` performs.

## What is Defensive Copying?

**Defensive copying** is a programming practice where you create a copy of an object to protect against unwanted
modification. You're "defending" your class's internal state from external changes.

```java
// WITHOUT defensive copy - DANGEROUS
public class Library {
    private List<Book> books;

    public Library(List<Book> books) {
        this.books = books; // Just storing the reference!
    }

    public List<Book> getBooks() {
        return books; // Exposing internal reference!
    }
}

// Caller can break the Library
List<Book> myBooks = new ArrayList<>();
myBooks.

add(new Book("1984"));
Library library = new Library(myBooks);

myBooks.

clear(); // OOPS! Library's books are now empty too!
library.

getBooks().

add(new Book("Bad Book")); // OOPS! Direct modification!


// WITH defensive copy - SAFE
public class Library {
    private final ImmutableList<Book> books;

    public Library(List<Book> books) {
        this.books = ImmutableList.copyOf(books); // Defensive copy!
    }

    public ImmutableList<Book> getBooks() {
        return books; // Safe to return - it's immutable
    }
}

// Now the caller can't break anything
List<Book> myBooks = new ArrayList<>();
myBooks.

add(new Book("1984"));
Library library = new Library(myBooks);

myBooks.

clear(); // Library is unaffected!
```

## Shallow vs Deep Copy in `ImmutableList.copyOf()`

**`ImmutableList.copyOf()` performs a SHALLOW copy.**

Here's what that means:

```java
class Book {
    private String title;

    public Book(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

// Creating an immutable list
List<Book> original = new ArrayList<>();
Book book1 = new Book("1984");
Book book2 = new Book("Animal Farm");
original.

add(book1);
original.

add(book2);

ImmutableList<Book> immutable = ImmutableList.copyOf(original);

// The LIST structure is copied, but the Book OBJECTS are shared
System.out.

println(immutable.get(0).

getTitle()); // "1984"

// You can't modify the list itself
// immutable.add(new Book("New")); // CompilationError/UnsupportedOperationException

// You CAN'T add/remove from the list
        original.

add(new Book("Brave New World"));
        System.out.

println(immutable.size()); // Still 2 - list is protected

// But you CAN still modify the Book objects themselves!
        book1.

setTitle("Modified Title");
System.out.

println(immutable.get(0).

getTitle()); // "Modified Title" - CHANGED!
```

## Visual Representation

```
SHALLOW COPY (what ImmutableList.copyOf does):

Original List:                 ImmutableList:
┌─────────────┐               ┌─────────────┐
│ ArrayList   │               │ Immutable   │
├─────────────┤               │   List      │
│ [0] ───┐    │               ├─────────────┤
│ [1] ───┼──┐ │               │ [0] ───┐    │
└─────────┼──┼─┘               │ [1] ───┼──┐ │
          │  │                 └─────────┼──┼─┘
          │  │                           │  │
          ↓  ↓                           ↓  ↓
       ┌──────────┐                 ┌──────────┐
       │ Book("A")│←────────────────┤ SAME     │
       └──────────┘                 │ objects! │
       ┌──────────┐                 └──────────┘
       │ Book("B")│←────────────────┘
       └──────────┘

DEEP COPY (not what happens):

Original List:                 New List:
┌─────────────┐               ┌─────────────┐
│ ArrayList   │               │   List      │
├─────────────┤               ├─────────────┤
│ [0] ───┐    │               │ [0] ───┐    │
│ [1] ───┼──┐ │               │ [1] ───┼──┐ │
└─────────┼──┼─┘               └─────────┼──┼─┘
          │  │                           │  │
          ↓  ↓                           ↓  ↓
       ┌──────────┐                 ┌──────────┐
       │ Book("A")│                 │ Book("A")│ ← COPIES
       └──────────┘                 └──────────┘
       ┌──────────┐                 ┌──────────┐
       │ Book("B")│                 │ Book("B")│ ← COPIES
       └──────────┘                 └──────────┘
```

## Why Shallow Copy?

1. **Performance**: Deep copying is expensive, especially for large object graphs
2. **Complexity**: How deep should you go? What about circular references?
3. **Practical**: Most objects in Java are designed to be immutable (String, Integer, etc.) or should be treated as
   immutable

## How to Achieve True Immutability

**Make the elements immutable too:**

```java
// Use immutable objects
ImmutableList<String> strings = ImmutableList.of("A", "B", "C");
// Strings are immutable, so this is truly immutable

ImmutableList<Integer> numbers = ImmutableList.of(1, 2, 3);
// Integers are immutable, so this is truly immutable

// Or use immutable value objects
@Value // Lombok annotation making class immutable
class ImmutableBook {
    String title;
    String author;
}

ImmutableList<ImmutableBook> books = ImmutableList.of(
        new ImmutableBook("1984", "Orwell"),
        new ImmutableBook("Animal Farm", "Orwell")
);
// Now this is TRULY immutable - neither list nor books can change
```

**If you need mutable elements, be careful:**

```java
// Pattern 1: Don't expose methods that modify elements
public class Library {
    private final ImmutableList<Book> books;

    public Library(List<Book> books) {
        this.books = ImmutableList.copyOf(books);
    }

    // Only expose immutable data
    public ImmutableList<String> getBookTitles() {
        return books.stream()
                .map(Book::getTitle)
                .collect(ImmutableList.toImmutableList());
    }
}

// Pattern 2: Deep copy when necessary
public class Library {
    private final ImmutableList<Book> books;

    public Library(List<Book> books) {
        // Deep copy each book
        this.books = books.stream()
                .map(book -> new Book(book.getTitle(), book.getAuthor()))
                .collect(ImmutableList.toImmutableList());
    }
}
```

## Summary

| Aspect             | Details                                                                   |
|--------------------|---------------------------------------------------------------------------|
| **Defensive Copy** | Creating a copy to protect from external modification                     |
| **Type of Copy**   | **Shallow** - copies the collection structure, not the elements           |
| **Protection**     | ✅ List structure protected<br>❌ Element objects NOT protected             |
| **Best Practice**  | Use immutable elements (String, Integer, etc.) or immutable value objects |

The key insight: **`ImmutableList.copyOf()` makes the list structure immutable, but elements are still shared
references.** For true immutability throughout, your elements need to be immutable too!

### Important: Each of the Guava immutable collection implementations rejects null values. We did an exhaustive study on
Google's internal code base that indicated that null elements were allowed in collections about 5% of the time, and the
other 95% of cases were best served by failing fast on nulls. If you need to use null values, consider using
Collections.unmodifiableList and its friends on a collection implementation that permits null. 


--------------------------------------------------------------------------------------------------------------


Perfect 👍 — let’s create a **complete, production-level Guava `Table` cheat sheet** — with clear explanations, real code usage, and common best practices.

---

## 🧭 Guava `Table` Cheat Sheet (with Full Explanation + Code Examples)

---

### 🧩 What Is a `Table`?

`Table<R, C, V>` represents a **two-dimensional mapping**, like a spreadsheet or database table, where each cell has:

| Concept            | Description                                        | Example             |
| ------------------ | -------------------------------------------------- | ------------------- |
| **R (Row Key)**    | The primary dimension (like an ID or entity)       | `"user1"`           |
| **C (Column Key)** | The secondary dimension (attribute of that entity) | `"email"`           |
| **V (Value)**      | The stored value                                   | `"user1@gmail.com"` |

So each record is like `(rowKey, columnKey, value)`.

---

### 🏗️ Available Implementations

| Implementation   | Description                              | When to Use                |
| ---------------- | ---------------------------------------- | -------------------------- |
| `HashBasedTable` | Backed by nested hash maps (most common) | Default, fast lookup       |
| `TreeBasedTable` | Sorted by row & column keys              | When you need sorted order |
| `ImmutableTable` | Read-only, memory-efficient              | For constant datasets      |

---

### 🚀 Basic Example

```java
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TableBasicExample {
    public static void main(String[] args) {
        Table<String, String, Integer> table = HashBasedTable.create();

        // Inserting values
        table.put("row1", "A", 10);
        table.put("row1", "B", 20);
        table.put("row2", "A", 30);

        // Printing the table
        System.out.println(table);
        // {row1={A=10, B=20}, row2={A=30}}
    }
}
```

---

### 🧱 Core Operations

#### ✅ `put(R rowKey, C columnKey, V value)`

Insert or update a cell.

```java
table.put("row3", "C", 50);
```

---

#### ✅ `get(R rowKey, C columnKey)`

Retrieve the value for a specific cell.

```java
System.out.println(table.get("row1", "A")); // 10
```

---

#### ✅ `contains(R, C)` / `containsRow(R)` / `containsColumn(C)`

Check existence.

```java
table.contains("row1", "A");     // true
table.containsRow("row2");       // true
table.containsColumn("B");       // true
```

---

#### ✅ `remove(R, C)`

Remove a single cell.

```java
table.remove("row2", "A");
```

---

### 🧮 Working with Rows & Columns

#### 📋 Get all columns in a row

```java
System.out.println(table.row("row1"));
// Output: {A=10, B=20}
```

#### 📋 Get all rows for a column

```java
System.out.println(table.column("A"));
// Output: {row1=10}
```

#### 📋 Iterate over rows and columns

```java
for (Table.Cell<String, String, Integer> cell : table.cellSet()) {
    System.out.printf("Row=%s, Column=%s, Value=%d%n",
            cell.getRowKey(), cell.getColumnKey(), cell.getValue());
}
```

Output:

```
Row=row1, Column=A, Value=10
Row=row1, Column=B, Value=20
Row=row3, Column=C, Value=50
```

---

### 🧰 Views and Sets

| Method           | Returns                  | Description                 |
| ---------------- | ------------------------ | --------------------------- |
| `rowKeySet()`    | Set<R>                   | All row keys                |
| `columnKeySet()` | Set<C>                   | All column keys             |
| `cellSet()`      | Set<Table.Cell<R, C, V>> | All cells                   |
| `values()`       | Collection<V>            | All values                  |
| `rowMap()`       | Map<R, Map<C, V>>        | Entire table as nested maps |
| `columnMap()`    | Map<C, Map<R, V>>        | Column-oriented view        |

---

### 🧑‍💻 Example — Row & Column Views

```java
System.out.println("All Rows: " + table.rowKeySet());
System.out.println("All Columns: " + table.columnKeySet());
System.out.println("All Values: " + table.values());
System.out.println("Row Map: " + table.rowMap());
System.out.println("Column Map: " + table.columnMap());
```

Output:

```
All Rows: [row1, row3]
All Columns: [A, B, C]
All Values: [10, 20, 50]
Row Map: {row1={A=10, B=20}, row3={C=50}}
Column Map: {A={row1=10}, B={row1=20}, C={row3=50}}
```

---

### ⚙️ Replacing and Updating

You can overwrite a value by reusing the same `(row, column)`:

```java
table.put("row1", "A", 999);
System.out.println(table.get("row1", "A")); // 999
```

---

### 🧩 Example: Representing a Real Dataset

#### Use case: Employee performance per quarter

```java
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class EmployeePerformance {
    public static void main(String[] args) {
        Table<String, String, Double> performance = HashBasedTable.create();

        performance.put("Alice", "Q1", 8.5);
        performance.put("Alice", "Q2", 9.0);
        performance.put("Bob", "Q1", 7.8);
        performance.put("Bob", "Q2", 8.2);

        // Get Alice's all scores
        System.out.println("Alice: " + performance.row("Alice"));

        // Get all Q1 scores
        System.out.println("Q1 Scores: " + performance.column("Q1"));
    }
}
```

Output:

```
Alice: {Q1=8.5, Q2=9.0}
Q1 Scores: {Alice=8.5, Bob=7.8}
```

---

### 🧱 Example: Building a Lookup Table (Production Use)

Use `Table` when you have **compound keys** but don’t want to create a custom key class.

Example: Caching results by `(userId, region)`.

```java
Table<String, String, Integer> userRegionCache = HashBasedTable.create();

// Store request counts
userRegionCache.put("u123", "US", 15);
userRegionCache.put("u123", "EU", 7);
userRegionCache.put("u789", "US", 3);

// Fetch region-specific data
System.out.println("u123 (US): " + userRegionCache.get("u123", "US"));
System.out.println("u123 all regions: " + userRegionCache.row("u123"));
System.out.println("All US users: " + userRegionCache.column("US"));
```

---

### 🧠 Best Practices for Production

✅ **Use `ImmutableTable`** for static configuration or precomputed data:

```java
import com.google.common.collect.ImmutableTable;

ImmutableTable<String, String, Integer> config = ImmutableTable.<String, String, Integer>builder()
    .put("A", "X", 1)
    .put("A", "Y", 2)
    .put("B", "X", 3)
    .build();
```

✅ **Prefer `TreeBasedTable`** if you need sorted row/column iteration:

```java
import com.google.common.collect.TreeBasedTable;

Table<String, String, Integer> sortedTable = TreeBasedTable.create();
```

✅ **Avoid nulls** — Guava collections generally don’t allow them.
✅ **Thread safety:** Wrap with `Collections.synchronizedMap()` if shared across threads.
✅ **Serialization:** `HashBasedTable` is serializable; use it for cached lookups.

---

### 🧾 Summary

| Task            | Method                     | Example                   |
| --------------- | -------------------------- | ------------------------- |
| Add cell        | `put(row, col, val)`       | `table.put("r1", "A", 5)` |
| Get cell        | `get(row, col)`            | `table.get("r1", "A")`    |
| Remove cell     | `remove(row, col)`         | `table.remove("r1", "A")` |
| Get full row    | `row(rowKey)`              | `table.row("r1")`         |
| Get full column | `column(colKey)`           | `table.column("A")`       |
| All rows        | `rowKeySet()`              | `[r1, r2]`                |
| All columns     | `columnKeySet()`           | `[A, B]`                  |
| Iterate all     | `cellSet()`                | For `(r, c, v)` entries   |
| Nested map view | `rowMap()` / `columnMap()` | Grouped structure         |

---

Excellent 👏 — this is exactly the kind of question senior engineers love during interviews or design reviews:
**“When should I use `ArrayListMultimap` vs `HashMultimap` vs `LinkedHashMultimap`, etc.?”**

Let’s go deep — with **realistic production scenarios**, **why** each type is best there, and **example code** for each case.

---

# 🧩 Big Picture: Why Use a Multimap at All?

You use a `Multimap<K, V>` when a **single key can map to multiple values**.
Without it, you'd manually manage `Map<K, List<V>>` or `Map<K, Set<V>>`, cluttering your code.

Guava’s Multimaps:
✅ simplify this pattern
✅ enforce correct collection types
✅ and add powerful utilities (`filter`, `invert`, `asMap`, `keys`, etc.)

---

# ⚙️ 1️⃣ `ArrayListMultimap` — Preserve Order + Allow Duplicates

**Best for:** Logging, grouping, or batching where *duplicates matter* and *order matters.*

### 🧠 Real-World Example: Grouping messages per user (with duplicates)

```java
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

ListMultimap<String, String> userMessages = ArrayListMultimap.create();

userMessages.put("alice", "Login success");
userMessages.put("alice", "Viewed dashboard");
userMessages.put("bob", "Login failed");
userMessages.put("alice", "Login success"); // duplicate allowed

System.out.println(userMessages.get("alice"));
// [Login success, Viewed dashboard, Login success]
```

✅ **Why it’s perfect here:**

* You care about **insertion order** (to replay or audit events)
* Duplicates make sense (multiple identical log messages)
* Fast insertions and retrievals
* Good for **batch processing, audit trails, request tracking**

❌ **When not to use:**
If duplicates are meaningless or harmful (e.g., tag lists, categories).

---

# ⚙️ 2️⃣ `LinkedListMultimap` — Maintain Global Insertion Order

**Best for:** Scenarios where you need to preserve **exact insertion order** across all entries, not just per key.

### 🧠 Example: Processing pipeline steps in order

```java
import com.google.common.collect.LinkedListMultimap;

LinkedListMultimap<String, String> pipeline = LinkedListMultimap.create();
pipeline.put("extract", "Load CSV");
pipeline.put("transform", "Normalize data");
pipeline.put("extract", "Read API");
pipeline.put("load", "Insert DB");

System.out.println(pipeline.entries());
// [extract=Load CSV, transform=Normalize data, extract=Read API, load=Insert DB]
```

✅ **Why:**

* Maintains *global insertion order* → useful for **ETL jobs**, **chained tasks**, or **event pipelines**
* Easy to replay events in the same sequence
* Duplicates allowed

❌ **Avoid:** when you don’t need order or when performance/memory is critical (linked structure = more overhead).

---

# ⚙️ 3️⃣ `HashMultimap` — Unique Values, Fast Lookup

**Best for:** Tagging, indexing, permissions, or mapping sets of unique values.

### 🧠 Example: User → Roles

```java
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

SetMultimap<String, String> userRoles = HashMultimap.create();
userRoles.put("alice", "ADMIN");
userRoles.put("alice", "EDITOR");
userRoles.put("alice", "ADMIN"); // duplicate ignored
userRoles.put("bob", "VIEWER");

System.out.println(userRoles);
// {alice=[ADMIN, EDITOR], bob=[VIEWER]}
```

✅ **Why it’s ideal:**

* Uniqueness enforced automatically
* O(1) average insertion/lookup
* Perfect for **permissions**, **tag systems**, **search indexes**

❌ **Avoid:** when you need insertion order (values come unordered).

---

# ⚙️ 4️⃣ `LinkedHashMultimap` — Unique + Order-Preserving

**Best for:** Ordered sets per key (e.g., filters, ordered preferences)

### 🧠 Example: Product → Enabled Filters

```java
import com.google.common.collect.LinkedHashMultimap;

LinkedHashMultimap<String, String> filters = LinkedHashMultimap.create();
filters.put("phone", "brand");
filters.put("phone", "color");
filters.put("phone", "price");
filters.put("phone", "brand"); // ignored (duplicate)

System.out.println(filters.get("phone"));
// [brand, color, price]
```

✅ **Why:**

* Uniqueness per key
* Preserves insertion order
* Useful for **user preferences**, **filter order**, **feature configurations**

❌ **Avoid:** if sorting is needed → use `TreeMultimap`.

---

# ⚙️ 5️⃣ `TreeMultimap` — Sorted Keys and Values

**Best for:** Data that needs **sorted order** (reports, leaderboards, stats).

### 🧠 Example: Department → Sorted Employee IDs

```java
import com.google.common.collect.TreeMultimap;

TreeMultimap<String, Integer> deptEmployees = TreeMultimap.create();
deptEmployees.put("HR", 12);
deptEmployees.put("HR", 5);
deptEmployees.put("Finance", 2);
deptEmployees.put("Finance", 10);

System.out.println(deptEmployees);
// {Finance=[2, 10], HR=[5, 12]}
```

✅ **Why:**

* Both keys and values automatically sorted
* Great for **sorted output**, **rankings**, or **display tables**

❌ **Avoid:** if performance is critical — `TreeMap` and `TreeSet` have O(log n) inserts.

---

# ⚙️ 6️⃣ Immutable Multimaps — Read-Only, Safe Sharing

**Best for:** Constant configuration data or defensive copies.

### 🧠 Example: Static configuration

```java
import com.google.common.collect.ImmutableListMultimap;

ImmutableListMultimap<String, String> config =
    ImmutableListMultimap.of(
        "mode", "prod",
        "mode", "debug",
        "region", "us-east"
    );

System.out.println(config);
// {mode=[prod, debug], region=[us-east]}
```

✅ **Why:**

* Immutable → thread-safe, no accidental modification
* Used for **static maps**, **predefined lookups**, or **config constants**

❌ **Avoid:** for frequently updated data (rebuilding immutables repeatedly is costly).

---

# 🧭 Choosing Guide (Cheat Sheet)

| Use Case                 | Need Duplicates? | Need Order? | Need Sorted Keys/Values? | Recommended Multimap                             |
| ------------------------ | ---------------- | ----------- | ------------------------ | ------------------------------------------------ |
| Logging / Batching       | ✅                | ✅           | ❌                        | `ArrayListMultimap`                              |
| Pipeline / Replay Events | ✅                | ✅ (global)  | ❌                        | `LinkedListMultimap`                             |
| Roles / Tags / Indexing  | ❌                | ❌           | ❌                        | `HashMultimap`                                   |
| Ordered Unique Filters   | ❌                | ✅           | ❌                        | `LinkedHashMultimap`                             |
| Leaderboards / Reports   | ❌                | ✅           | ✅                        | `TreeMultimap`                                   |
| Static Configuration     | ✅ or ❌           | ✅           | ✅                        | `ImmutableListMultimap` / `ImmutableSetMultimap` |

---

Perfect — now we’re getting into the **real power** of Guava’s `Multimap` design.
Let’s go step-by-step through the **two major categories** of `Multimap` implementations — `ListMultimap` and `SetMultimap` — and the key classes you’ll actually use in production.

---

## 🧩 1️⃣ The Two Big Interfaces

Guava defines **two main specialized subinterfaces** of `Multimap`:

| Interface                | Values Type | Allows Duplicates per Key? | Preserves Order?              | Example Implementation                               |
| ------------------------ | ----------- | -------------------------- | ----------------------------- | ---------------------------------------------------- |
| **`ListMultimap<K, V>`** | `List<V>`   | ✅ Yes                      | ✅ Yes (insertion order)       | `ArrayListMultimap`, `LinkedListMultimap`            |
| **`SetMultimap<K, V>`**  | `Set<V>`    | ❌ No                       | Depends (some preserve order) | `HashMultimap`, `LinkedHashMultimap`, `TreeMultimap` |

---

## 🧠 Why Two Kinds?

Because sometimes you care about **duplicates and order**, and sometimes you don’t.

* `ListMultimap` behaves like a `Map<K, List<V>>`
* `SetMultimap` behaves like a `Map<K, Set<V>>`

They share the same conceptual foundation (`Multimap`), but enforce different semantics for the value collections.

---

## 🧰 2️⃣ `ListMultimap` — Keep All, In Order

### ✅ Allows duplicates and preserves insertion order

Useful when:

* you want to preserve **insertion order** of values, or
* you might have **duplicate** values per key.

### Example: `ArrayListMultimap`

```java
ListMultimap<String, String> listMulti = ArrayListMultimap.create();

listMulti.put("fruit", "apple");
listMulti.put("fruit", "apple");
listMulti.put("fruit", "orange");

System.out.println(listMulti);
// {fruit=[apple, apple, orange]}

System.out.println(listMulti.get("fruit"));
// [apple, apple, orange]
```

* **Backed by:** `Map<K, Collection<V>>` with each value as an `ArrayList<V>`
* **Duplicates:** Allowed
* **Order:** Maintained
* **Performance:** Fast inserts/iteration, higher memory footprint.

### Example: `LinkedListMultimap`

* Maintains **global insertion order** of all entries (not just per key).
* Iterating over `entries()` gives you exactly the order you inserted them in.

```java
LinkedListMultimap<String, String> linked = LinkedListMultimap.create();
linked.put("x", "1");
linked.put("y", "2");
linked.put("x", "3");
System.out.println(linked.entries()); // [x=1, y=2, x=3]
```

---

## 🧱 3️⃣ `SetMultimap` — Unique Values Only

### ✅ Prevents duplicates per key

Useful when:

* you want each `(key, value)` pair to be unique
* you don’t care about duplicate values for the same key

### Example: `HashMultimap`

```java
SetMultimap<String, String> setMulti = HashMultimap.create();

setMulti.put("fruit", "apple");
setMulti.put("fruit", "apple");
setMulti.put("fruit", "orange");

System.out.println(setMulti);
// {fruit=[apple, orange]}
```

* **Backed by:** `Map<K, Set<V>>` with `HashSet<V>`
* **Duplicates:** Automatically ignored
* **Order:** Unspecified (hash-based)

---

### Example: `LinkedHashMultimap`

Like `HashMultimap`, but preserves **insertion order** of values *per key*.

```java
SetMultimap<String, Integer> linked = LinkedHashMultimap.create();
linked.put("a", 3);
linked.put("a", 1);
linked.put("a", 2);
System.out.println(linked.get("a")); // [3, 1, 2]
```

---

### Example: `TreeMultimap`

* Stores keys and values in **sorted order** (using natural order or custom `Comparator`).
* Useful when you want deterministic iteration order.

```java
TreeMultimap<String, Integer> sorted = TreeMultimap.create();
sorted.put("b", 3);
sorted.put("a", 2);
sorted.put("b", 1);

System.out.println(sorted);
// {a=[2], b=[1, 3]}
```

* **Backed by:** `TreeMap<K, TreeSet<V>>`
* **Duplicates:** No
* **Order:** Sorted by key and value

---

## 🪄 4️⃣ Immutable Multimaps

Guava also provides immutable versions for when you want read-only safety:

| Immutable Type          | Interface      | Example                                                                             |
| ----------------------- | -------------- | ----------------------------------------------------------------------------------- |
| `ImmutableListMultimap` | `ListMultimap` | `ImmutableListMultimap.of("fruit", "apple", "fruit", "orange")`                     |
| `ImmutableSetMultimap`  | `SetMultimap`  | `ImmutableSetMultimap.of("fruit", "apple", "fruit", "apple") // duplicates ignored` |

Immutable multimaps are great for constant data or defensive copies, and — just like other `ImmutableXXX` — they’re *smart about reusing data*.

---

## ⚙️ 5️⃣ Conversion and Views

Every `Multimap` can be viewed as:

```java
Map<K, Collection<V>> asMap = multimap.asMap();
Collection<Map.Entry<K,V>> entries = multimap.entries();
Collection<V> values = multimap.values();
```

You can also convert between `ListMultimap` and `SetMultimap`:

```java
SetMultimap<K, V> unique = Multimaps.forSetMultimap(listMultimap);
```

Or create filtered/synchronized versions with `Multimaps.filterKeys()`, `Multimaps.synchronizedMultimap()`, etc.

---

## 🧭 Quick Summary

| Type                    | Backing Collection | Duplicates per Key | Order         | Use Case                          |
| ----------------------- | ------------------ | ------------------ | ------------- | --------------------------------- |
| `ArrayListMultimap`     | `ArrayList`        | ✅                  | ✅ (insertion) | Keep duplicates, fast access      |
| `LinkedListMultimap`    | `LinkedList`       | ✅                  | ✅ (global)    | Maintain full insertion order     |
| `HashMultimap`          | `HashSet`          | ❌                  | ❌             | Unique values, fast lookup        |
| `LinkedHashMultimap`    | `LinkedHashSet`    | ❌                  | ✅             | Unique + preserve insertion order |
| `TreeMultimap`          | `TreeSet`          | ❌                  | ✅ (sorted)    | Unique + sorted order             |
| `ImmutableListMultimap` | immutable list     | ✅                  | ✅             | Read-only, duplicates allowed     |
| `ImmutableSetMultimap`  | immutable set      | ❌                  | ✅             | Read-only, unique values          |

---

# 🧠 Pro Tip: Integration Use-Cases

* **Database joins:** map parent → multiple children
* **REST endpoints:** collect headers or parameters with same name
* **ETL pipelines:** map stage → multiple tasks
* **Cache grouping:** region → cached objects
* **Search indexing:** keyword → documents

---

Perfect 😎 — let’s go all-in with **real backend-style production use-cases** for each `Multimap` type.

We’ll use examples that mimic real-world problems:

* database result grouping
* REST parameter parsing
* permissions caching
* config loading
* analytics reporting

---

## 🧱 1️⃣ `ArrayListMultimap` — Grouping Database Query Results

**💡 Scenario:**
You query a database that returns `(userId, orderId)` pairs.
You want to group all `orderId`s by each `userId`.

```java
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;
import java.util.Map;

public class OrderGrouper {
    public static void main(String[] args) {
        ListMultimap<Integer, String> userOrders = ArrayListMultimap.create();

        // Simulating DB rows
        List<Map.Entry<Integer, String>> rows = List.of(
            Map.entry(1, "ORD-1001"),
            Map.entry(2, "ORD-1002"),
            Map.entry(1, "ORD-1003"),
            Map.entry(1, "ORD-1001") // duplicate allowed
        );

        for (var row : rows) {
            userOrders.put(row.getKey(), row.getValue());
        }

        System.out.println(userOrders.get(1)); // [ORD-1001, ORD-1003, ORD-1001]
    }
}
```

✅ **Why it fits:**

* Preserves order of DB results
* Duplicates allowed (same order multiple times)
* Easy to stream later into response objects or JSON

💡 **Best used for:** Query grouping, audit/event lists, logs, or message batching.

---

## 🧾 2️⃣ `LinkedListMultimap` — REST Request Parameter Parser

**💡 Scenario:**
In REST APIs, query params like `?tag=java&tag=backend&tag=java` can repeat.
Order may matter for some endpoints.

```java
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class QueryParamParser {
    public static Multimap<String, String> parseParams(String query) {
        Multimap<String, String> params = LinkedListMultimap.create();

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            params.put(parts[0], parts.length > 1 ? parts[1] : "");
        }

        return params;
    }

    public static void main(String[] args) {
        Multimap<String, String> params = parseParams("tag=java&tag=backend&tag=java");
        System.out.println(params.get("tag")); // [java, backend, java]
        System.out.println(params.entries());  // Preserves full insertion order
    }
}
```

✅ **Why it fits:**

* Query param order is preserved
* Duplicates allowed (e.g., repeated `tag`)
* Great for **REST parsing**, **HTTP headers**, or **URL decoding**

---

## 🔐 3️⃣ `HashMultimap` — Role-Based Access Caching

**💡 Scenario:**
A user can have multiple unique roles.
You don’t want duplicates — you just want unique associations.

```java
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class AccessCache {
    public static void main(String[] args) {
        SetMultimap<String, String> roleCache = HashMultimap.create();

        roleCache.put("alice", "ADMIN");
        roleCache.put("alice", "EDITOR");
        roleCache.put("alice", "ADMIN"); // ignored

        System.out.println(roleCache.get("alice")); // [ADMIN, EDITOR]
    }
}
```

✅ **Why it fits:**

* Fast insertions and lookups
* No duplicates per key
* Perfect for **permission/role caching**, **tagging**, or **index building**

---

## ⚙️ 4️⃣ `LinkedHashMultimap` — Ordered Filters or Feature Toggles

**💡 Scenario:**
A user has enabled filters in a specific order for a search or dashboard.
You must keep that order, but avoid duplicates.

```java
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

public class UserFilters {
    public static void main(String[] args) {
        SetMultimap<String, String> filters = LinkedHashMultimap.create();
        filters.put("user123", "category");
        filters.put("user123", "price");
        filters.put("user123", "date");
        filters.put("user123", "category"); // ignored

        System.out.println(filters.get("user123")); // [category, price, date]
    }
}
```

✅ **Why it fits:**

* Unique values per key
* Preserves insertion order
* Ideal for **UI filter preferences**, **ordered rules**, **feature toggles**

---

## 📊 5️⃣ `TreeMultimap` — Sorted Reports and Analytics

**💡 Scenario:**
You’re generating a report grouped by department with sorted employees.

```java
import com.google.common.collect.TreeMultimap;

public class SortedReport {
    public static void main(String[] args) {
        TreeMultimap<String, Integer> deptEmployees = TreeMultimap.create();
        deptEmployees.put("Finance", 12);
        deptEmployees.put("Finance", 5);
        deptEmployees.put("HR", 20);
        deptEmployees.put("HR", 15);

        System.out.println(deptEmployees);
        // {Finance=[5, 12], HR=[15, 20]}
    }
}
```

✅ **Why it fits:**

* Natural sorting of keys and values
* Great for **reports**, **leaderboards**, **ranked listings**

💡 Can also pass comparators:

```java
TreeMultimap<String, Integer> custom = TreeMultimap.create(
    String.CASE_INSENSITIVE_ORDER,
    Comparator.reverseOrder()
);
```

---

## 🧱 6️⃣ `ImmutableListMultimap` — Static Configuration or Constant Mapping

**💡 Scenario:**
System constants or endpoint → supported methods mapping.

```java
import com.google.common.collect.ImmutableListMultimap;

public class Config {
    public static final ImmutableListMultimap<String, String> ENDPOINT_METHODS =
        ImmutableListMultimap.of(
            "/users", "GET",
            "/users", "POST",
            "/orders", "GET"
        );

    public static void main(String[] args) {
        System.out.println(ENDPOINT_METHODS);
        // {/users=[GET, POST], /orders=[GET]}
    }
}
```

✅ **Why it fits:**

* Immutable → thread-safe and zero risk of mutation
* Used in **configs**, **static registries**, **policy mappings**

---

# ⚡ 7️⃣ Mixed Example — Real REST Layer Integration

Here’s a mini real-life scenario showing how `Multimap` types complement each other.

```java
public class ApiExample {
    // user -> roles
    private static final SetMultimap<String, String> userRoles = HashMultimap.create();
    // endpoint -> allowed methods
    private static final ImmutableListMultimap<String, String> endpointMethods =
        ImmutableListMultimap.of("/users", "GET", "/users", "POST");

    public static void main(String[] args) {
        userRoles.put("alice", "ADMIN");
        userRoles.put("bob", "VIEWER");

        System.out.println("Allowed Methods: " + endpointMethods.get("/users"));
        System.out.println("Alice Roles: " + userRoles.get("alice"));
    }
}
```

✅ **Shows:**

* Immutable for config
* HashMultimap for role caching
* Both thread-safe and concise

---

## 🎯 Choosing Summary (Real World)

| Problem                | Needs Duplicates | Needs Order | Needs Sorted | Recommended                                      |
| ---------------------- | ---------------- | ----------- | ------------ | ------------------------------------------------ |
| Grouping DB results    | ✅                | ✅           | ❌            | `ArrayListMultimap`                              |
| Parsing HTTP params    | ✅                | ✅           | ❌            | `LinkedListMultimap`                             |
| Role-based caching     | ❌                | ❌           | ❌            | `HashMultimap`                                   |
| Ordered unique filters | ❌                | ✅           | ❌            | `LinkedHashMultimap`                             |
| Sorted analytics       | ❌                | ✅           | ✅            | `TreeMultimap`                                   |
| Static config          | ✅/❌              | ✅           | ✅            | `ImmutableListMultimap` / `ImmutableSetMultimap` |

---

Excellent 🔥 Let’s go full **production-level deep dive** now — comparing **Guava Multimap implementations** by **performance, memory trade-offs, and best usage patterns** 👇

---

# ⚙️ Guava Multimap — Performance & Design Comparison

| Implementation            | Internal Structure                   | Duplicates | Key Order             | Value Order     | Immutability | Typical Use Case                                                |
| ------------------------- | ------------------------------------ | ---------- | --------------------- | --------------- | ------------ | --------------------------------------------------------------- |
| **ArrayListMultimap**     | `Map<K, List<V>>`                    | ✅ Yes      | ❌ Unordered (HashMap) | ✅ Insertion     | Mutable      | Preserve duplicates and order (e.g. HTTP headers)               |
| **HashMultimap**          | `Map<K, Set<V>>`                     | ❌ No       | ❌ Unordered           | ❌ Unordered     | Mutable      | Unique values, fast lookups (e.g. permissions)                  |
| **LinkedHashMultimap**    | `LinkedHashMap<K, LinkedHashSet<V>>` | ❌ No       | ✅ Insertion           | ✅ Insertion     | Mutable      | Deterministic iteration, no duplicates (e.g. ordered caches)    |
| **TreeMultimap**          | `TreeMap<K, TreeSet<V>>`             | ❌ No       | ✅ Sorted              | ✅ Sorted        | Mutable      | Sorted keys & values (e.g. alphabetical or range-based lookups) |
| **ImmutableListMultimap** | Immutable structure of Lists         | ✅ Yes      | ✅ Deterministic       | ✅ Deterministic | ✅ Immutable  | Read-only API data (e.g. grouped results)                       |
| **ImmutableSetMultimap**  | Immutable structure of Sets          | ❌ No       | ✅ Deterministic       | ✅ Deterministic | ✅ Immutable  | Constant mappings (e.g. static configs, roles)                  |

---

# ⚡ Performance Comparison

| Operation             | ArrayListMultimap                | HashMultimap | LinkedHashMultimap | TreeMultimap   | ImmutableListMultimap | ImmutableSetMultimap |
| --------------------- | -------------------------------- | ------------ | ------------------ | -------------- | --------------------- | -------------------- |
| `put(key, value)`     | **O(1)** amortized               | **O(1)** avg | **O(1)** avg       | **O(log n)**   | ❌ Immutable           | ❌ Immutable          |
| `get(key)`            | **O(1)**                         | **O(1)**     | **O(1)**           | **O(log n)**   | **O(1)**              | **O(1)**             |
| `remove(key, value)`  | **O(nᵥ)** per key                | **O(1)** avg | **O(1)** avg       | **O(log n)**   | ❌ Immutable           | ❌ Immutable          |
| `containsEntry(k, v)` | **O(nᵥ)**                        | **O(1)**     | **O(1)**           | **O(log n)**   | **O(1)**              | **O(1)**             |
| `iteration`           | **O(n)** (preserves value order) | **O(n)**     | **O(n)**           | **O(n log n)** | **O(n)**              | **O(n)**             |

> 🧮 `nᵥ` = number of values per key

✅ **HashMultimap** and **LinkedHashMultimap** are typically fastest for large collections.
✅ **TreeMultimap** trades performance for sorting guarantees.
✅ **Immutable*** versions are great for concurrency and stability.

---

# 💾 Memory Characteristics

| Implementation                          | Memory Overhead | Reason                                                      |
| --------------------------------------- | --------------- | ----------------------------------------------------------- |
| **ArrayListMultimap**                   | 🔺 High         | Each key maps to a List (extra object refs + resizing cost) |
| **HashMultimap**                        | ⚖️ Medium       | Uses `HashSet` per key (hash structure overhead)            |
| **LinkedHashMultimap**                  | 🔺 High         | LinkedHashMap + LinkedHashSet keep ordering pointers        |
| **TreeMultimap**                        | 🔺🔺 Very High  | TreeMap + TreeSet maintain sort trees                       |
| **ImmutableListMultimap / SetMultimap** | ⚡ Low           | Compact, no resizing, shared immutable backing arrays       |

---

# 🧩 When to Use Which — Real Production Scenarios

### 1️⃣ `ArrayListMultimap`

> **Use for preserving order and allowing duplicates**

✅ Example: collecting **query parameters** in a URL or **form submissions**

```java
ArrayListMultimap<String, String> params = ArrayListMultimap.create();
params.put("filter", "active");
params.put("filter", "recent"); // duplicate allowed
params.put("sort", "asc");
```

📌 *Best for:* request/response mapping, header storage, logging

---

### 2️⃣ `HashMultimap`

> **Use when you need uniqueness and speed**

✅ Example: **user-to-role** mapping in an RBAC system

```java
SetMultimap<String, String> userRoles = HashMultimap.create();
userRoles.put("alice", "ADMIN");
userRoles.put("alice", "ADMIN"); // ignored
userRoles.put("bob", "USER");
```

📌 *Best for:* permissions, tag sets, membership systems

---

### 3️⃣ `LinkedHashMultimap`

> **Use when iteration order must be stable**

✅ Example: tracking **recent operations per user**

```java
LinkedHashMultimap<String, String> userActions = LinkedHashMultimap.create();
userActions.put("john", "login");
userActions.put("john", "upload");
userActions.put("john", "login"); // ignored
```

📌 *Best for:* ordered caches, audit trails, sequence tracking

---

### 4️⃣ `TreeMultimap`

> **Use when natural or custom sorting is required**

✅ Example: **price range to products**

```java
TreeMultimap<Double, String> priceIndex = TreeMultimap.create();
priceIndex.put(9.99, "Mug");
priceIndex.put(5.99, "Pen");
priceIndex.put(9.99, "T-shirt");
```

📌 *Best for:* sorted indexes, range-based queries, reporting systems

---

### 5️⃣ `ImmutableListMultimap`

> **Use for thread-safe, immutable collections that allow duplicates**

✅ Example: **grouping API responses**

```java
ImmutableListMultimap<String, String> productsByCategory =
    ImmutableListMultimap.<String, String>builder()
        .put("Books", "Effective Java")
        .put("Books", "Clean Code")
        .put("Electronics", "Mouse")
        .build();
```

📌 *Best for:* returning grouped data safely, caching immutable results

---

### 6️⃣ `ImmutableSetMultimap`

> **Use for unique, immutable, thread-safe mappings**

✅ Example: **static role permissions**

```java
ImmutableSetMultimap<String, String> rolePermissions =
    ImmutableSetMultimap.<String, String>builder()
        .put("admin", "read")
        .put("admin", "write")
        .put("user", "read")
        .build();
```

📌 *Best for:* constants, configs, role-based permissions, static caches

---

# 🧭 Summary Decision Tree

```
Need duplicates? → Yes → ArrayListMultimap / ImmutableListMultimap
                 → No  → (Next)
Need order?      → Yes → LinkedHashMultimap / ImmutableSetMultimap
Need sorting?    → Yes → TreeMultimap
                 → No  → HashMultimap (fastest)
Need immutability? → Yes → Immutable*Multimap
```

---

ClassToInstanceMap -> extends Map<Class<? extends B>, B>

Sometimes, your map keys aren't all of the same type: they are types, and you want to map them to values of that type. Guava provides ClassToInstanceMap for this purpose.

In addition to extending the Map interface, ClassToInstanceMap provides the methods T getInstance(Class<T>) and T putInstance(Class<T>, T), which eliminate the need for unpleasant casting while enforcing type safety.

ClassToInstanceMap has a single type parameter, typically named B, representing the upper bound on the types managed by the map. For example:

ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
Technically, ClassToInstanceMap<B> implements Map<Class<? extends B>, B> -- or in other words, a map from subclasses of B to instances of B. This can make the generic types involved in ClassToInstanceMap mildly confusing, but just remember that B is always the upper bound on the types in the map -- usually, B is just Object.

Guava provides implementations helpfully named MutableClassToInstanceMap and ImmutableClassToInstanceMap.

Important: Like any other Map<Class, Object>, a ClassToInstanceMap may contain entries for primitive types, and a primitive type and its corresponding wrapper type may map to different values.


---Use Case: ClassToInstanceMap

for example if you want to provide default values for different types of objects, that have the same interface, but different implementations.

```java
ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
numberDefaults.putInstance(Long.class, Long.valueOf(0));
numberDefaults.putInstance(Double.class, Double.valueOf(0));
numberDefaults.putInstance(Float.class, Float.valueOf(0));
numberDefaults.putInstance(BigDecimal.class, BigDecimal.ZERO);
numberDefaults.putInstance(BigInteger.class, BigInteger.ZERO);
```


--------------

## 🧠 Concept: `ImmutableRangeSet`

An `ImmutableRangeSet<C extends Comparable>` is a **set of non-overlapping ranges** (each represented by a `Range<C>`).
It’s immutable (cannot be changed once created), so all operations return new instances.

Internally, it maintains **disjoint and ordered ranges**, ensuring no ambiguity when you check containment or intersection.

---

## ⚙️ Key Property: Non-Overlapping Ranges

If you try to create or add a range that **overlaps** with an existing one,
Guava will throw an `IllegalArgumentException`.

### 🧩 Example — Illegal overlap

```java
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;

public class RangeDemo {
    public static void main(String[] args) {
        ImmutableRangeSet<Integer> rangeSet = ImmutableRangeSet.<Integer>builder()
                .add(Range.closed(1, 5))
                .add(Range.closed(10, 15))
                .build();

        // ❌ This will throw IllegalArgumentException:
        ImmutableRangeSet.<Integer>builder()
                .add(Range.closed(1, 5))
                .add(Range.closed(3, 8)) // Overlaps with [1‥5]
                .build();
    }
}
```

💥 Output:

```
Exception in thread "main" java.lang.IllegalArgumentException: Overlapping ranges not permitted: [1‥5] overlaps [3‥8]
```

---

## ✅ Allowed Case — Disjoint Ranges

If the ranges do not overlap, all is fine:

```java
ImmutableRangeSet<Integer> disjoint = ImmutableRangeSet.<Integer>builder()
        .add(Range.closed(1, 5))
        .add(Range.closed(6, 9)) // disjoint
        .add(Range.closed(15, 20))
        .build();
```

✅ Works perfectly
✅ Ranges remain ordered internally
✅ No need for runtime merging

---

## ⚠️ "Touching" Ranges Are Considered Overlapping for Closed Boundaries

This is subtle but important:

```java
ImmutableRangeSet.<Integer>builder()
    .add(Range.closed(1, 5))
    .add(Range.closed(5, 10)); // ❌ IllegalArgumentException — they touch!
```

Guava treats **“touching” ranges** (i.e., `end == start`) as **overlapping** because for closed ranges `[1,5]` and `[5,10]`,
the value `5` belongs to both.

✅ To avoid that, you can make them **open** on one side:

```java
ImmutableRangeSet.<Integer>builder()
    .add(Range.closed(1, 5))
    .add(Range.open(5, 10)) // ✅ Allowed
    .build();
```

---

## 🧩 Why This Design?

Because the goal of `RangeSet` is to provide:

* **Unambiguous containment** (`contains(value)` always gives a single, deterministic result)
* **Efficient queries** — since ranges are guaranteed disjoint and sorted, lookups are logarithmic.
* **No merging ambiguity** — you never have to decide how to merge overlapping intervals.

---

## ⚙️ Practical Production Example

### Example: Reserving Time Slots

Suppose you manage **meeting room reservations**, and each booking is a time range.
Overlaps are not allowed.

```java
ImmutableRangeSet<Integer> bookings = ImmutableRangeSet.<Integer>builder()
        .add(Range.closedOpen(9, 11))   // 9:00–11:00
        .add(Range.closedOpen(13, 15))  // 13:00–15:00
        // .add(Range.closedOpen(10, 12)) ❌ would overlap -> IllegalArgumentException
        .build();

System.out.println(bookings.contains(10)); // true
System.out.println(bookings.contains(12)); // false
```

📌 Use case examples:

* **Scheduling / booking systems**
* **Rate or tax intervals**
* **Numeric or date-range filters**
* **Partitioned datasets**

---

## 🧮 Key Rule of Thumb

| Scenario                      | Result                       |
| ----------------------------- | ---------------------------- |
| Overlapping ranges            | ❌ `IllegalArgumentException` |
| Touching closed ranges        | ❌ Treated as overlap         |
| Touching open/closed properly | ✅ Allowed                    |
| Disjoint ranges               | ✅ Allowed                    |

---

Excellent question 💡 — and you’ve hit one of the **most important distinctions** in the Guava `RangeSet` hierarchy.

Let’s clarify this precisely 👇

---

## 🧩 Short Answer

✅ **Yes** — you can use a **mutable** builder-like version:
that’s **`TreeRangeSet`**, **not** `ImmutableRangeSet.Builder`.

---

## ⚙️ Difference in Mutability

| Type                            | Mutability  | Merges Overlaps | Allows Modifications | Throws on Overlaps                  |
| ------------------------------- | ----------- | --------------- | -------------------- | ----------------------------------- |
| **`TreeRangeSet`**              | ✅ Mutable   | ✅ Yes           | ✅ Add/remove ranges  | ❌ No (it merges them)               |
| **`ImmutableRangeSet.Builder`** | ❌ Immutable | ❌ No            | ❌ Build-once         | ✅ Throws `IllegalArgumentException` |

---

## 🧠 Think of It Like This

* `TreeRangeSet` → “**mutable range builder**”
  You can add, remove, and merge ranges dynamically.

* `ImmutableRangeSet` → “**frozen snapshot**”
  Once built, cannot be changed; guarantees internal consistency and immutability.

---

## 🧩 Example — Using `TreeRangeSet` as a Mutable Builder

```java
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class MutableRangeSetExample {
    public static void main(String[] args) {
        RangeSet<Integer> rangeSet = TreeRangeSet.create();

        rangeSet.add(Range.closed(1, 5));
        rangeSet.add(Range.closed(10, 15));
        rangeSet.add(Range.closed(4, 12)); // ✅ overlaps → automatically merged

        System.out.println(rangeSet); 
        // Output: [[1‥15]]
    }
}
```

✅ **No exception**
✅ Automatically merges overlapping `[1‥5]`, `[10‥15]`, and `[4‥12]`
✅ Result: single continuous range `[1‥15]`

---

## ⚙️ Building an `ImmutableRangeSet` from a Mutable One

Once you’re done adding and merging dynamically,
you can create an immutable, thread-safe snapshot:

```java
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.collect.Range;

public class BuildImmutableRangeSet {
    public static void main(String[] args) {
        TreeRangeSet<Integer> builder = TreeRangeSet.create();
        builder.add(Range.closed(1, 5));
        builder.add(Range.closed(7, 9));
        builder.add(Range.closed(3, 8)); // merged to [1‥9]

        ImmutableRangeSet<Integer> immutable = ImmutableRangeSet.copyOf(builder);
        System.out.println(immutable); // [[1‥9]]
    }
}
```

✅ `TreeRangeSet` for flexible mutation
✅ `ImmutableRangeSet.copyOf()` for immutability

---

## 🧭 When to Use Which

| Use Case                            | Recommended Type                         |
| ----------------------------------- | ---------------------------------------- |
| Building ranges dynamically         | `TreeRangeSet`                           |
| Merging and updating ranges         | `TreeRangeSet`                           |
| Finalizing ranges for read-only use | `ImmutableRangeSet.copyOf()`             |
| Predefined constant sets            | `ImmutableRangeSet.of()` or `.builder()` |

---

## ⚙️ Bonus — RangeSet Utility Operations

`RangeSet` (and `TreeRangeSet`) provides extra smart methods:

```java
RangeSet<Integer> rs = TreeRangeSet.create();
rs.add(Range.closed(1, 10));
rs.remove(Range.open(4, 6));

System.out.println(rs); // [[1‥4], [6‥10]]
System.out.println(rs.contains(5)); // false
System.out.println(rs.rangeContaining(8)); // [6‥10]
System.out.println(rs.complement()); // (-∞‥1), (4‥6), (10‥+∞)
```

---

### 🧩 Summary

| Builder Type                | Mutable | Overlap Handling       | Typical Usage             |
| --------------------------- | ------- | ---------------------- | ------------------------- |
| `TreeRangeSet`              | ✅ Yes   | ✅ Merges automatically | Dynamic construction      |
| `ImmutableRangeSet.Builder` | ❌ No    | ❌ Throws exception     | Static, predefined ranges |

---

Perfect 👌 — this is an excellent deep-dive point.
Let’s build a **complete practical guide** to Guava’s `RangeSet` and its **views** + **queries**, with **realistic examples** and explanations that make sense in **production** (not just demos).

---

## 🧩 1. Setup

```java
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class RangeSetExample {
    public static void main(String[] args) {
        RangeSet<Integer> rangeSet = TreeRangeSet.create();

        rangeSet.add(Range.closed(1, 5));    // [1,5]
        rangeSet.add(Range.open(10, 15));    // (10,15)
        rangeSet.add(Range.closed(20, 25));  // [20,25]

        System.out.println("RangeSet: " + rangeSet);
    }
}
```

✅ Output:

```
RangeSet: [[1‥5], (10‥15), [20‥25]]
```

---

## 🧱 2. Views

### (a) `complement()`

> Returns all ranges *not* covered by the RangeSet.

```java
RangeSet<Integer> complement = rangeSet.complement();
System.out.println("Complement: " + complement);
```

🧠 Since our ranges are finite, complement gives **everything outside** those ranges.

✅ Output:

```
Complement: ((-∞‥1), (5‥10], [15‥20), (25‥+∞))
```

🪄 **Use case:**
When you want to find **available** or **unallocated** time slots, IDs, or resource intervals.

---

### (b) `subRangeSet(Range<C>)`

> Restrict to a **window** inside another range.

```java
RangeSet<Integer> subRangeSet = rangeSet.subRangeSet(Range.closed(0, 22));
System.out.println("SubRangeSet(0–22): " + subRangeSet);
```

✅ Output:

```
SubRangeSet(0–22): [[1‥5], (10‥15), [20‥22]]
```

🪄 **Use case:**
Filter time slots or number intervals within a specific boundary (like user request window or calendar day).

---

### (c) `asRanges()`

> View the internal representation as a `Set<Range<C>>`.

```java
System.out.println("asRanges():");
for (Range<Integer> r : rangeSet.asRanges()) {
    System.out.println("  " + r);
}
```

✅ Output:

```
asRanges():
  [1‥5]
  (10‥15)
  [20‥25]
```

🪄 **Use case:**
Iterate through defined segments, for example: reserved table time ranges, ID allocations, allowed ports.

---

### (d) `asSet(DiscreteDomain<C>)` (ImmutableRangeSet only)

This converts the entire `RangeSet` into a **set of actual values**, if the domain is *discrete* (like integers).

```java
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableRangeSet;

ImmutableRangeSet<Integer> immutableSet = ImmutableRangeSet.copyOf(rangeSet);
System.out.println("asSet view: " + immutableSet.asSet(DiscreteDomain.integers()));
```

✅ Output:

```
asSet view: [1, 2, 3, 4, 5, 11, 12, 13, 14, 21, 22, 23, 24, 25]
```

🪄 **Use case:**
Useful when you actually need all integers for batch processing or validation, e.g., *“generate all allocated seat numbers.”*

---

## 🔍 3. Query Operations

### (a) `contains(C)`

> Checks if any range includes the given element.

```java
System.out.println("contains(3): " + rangeSet.contains(3));
System.out.println("contains(9): " + rangeSet.contains(9));
```

✅ Output:

```
contains(3): true
contains(9): false
```

🪄 **Use case:**
Check if a user-provided value (e.g., time, port, score) falls in any valid configured range.

---

### (b) `rangeContaining(C)`

> Returns the range that contains a value, or `null` if none.

```java
System.out.println("rangeContaining(4): " + rangeSet.rangeContaining(4));
System.out.println("rangeContaining(12): " + rangeSet.rangeContaining(12));
System.out.println("rangeContaining(8): " + rangeSet.rangeContaining(8));
```

✅ Output:

```
rangeContaining(4): [1‥5]
rangeContaining(12): (10‥15)
rangeContaining(8): null
```

🪄 **Use case:**
To find *which* segment or category an item belongs to — for instance, determining pricing tier or region bucket.

---

### (c) `encloses(Range<C>)`

> Checks if any of the existing ranges **fully contains** another range.

```java
System.out.println("encloses([2,4]): " + rangeSet.encloses(Range.closed(2, 4))); // ✅ inside [1,5]
System.out.println("encloses([2,10]): " + rangeSet.encloses(Range.closed(2, 10))); // ❌ spans multiple
```

✅ Output:

```
encloses([2,4]): true
encloses([2,10]): false
```

🪄 **Use case:**
Determine if a requested resource allocation fits entirely within an already reserved region.

---

### (d) `span()`

> Returns the **minimal** range that encloses *all* the ranges.

```java
System.out.println("span(): " + rangeSet.span());
```

✅ Output:

```
span(): [1‥25]
```

🪄 **Use case:**
Determine total covered region (e.g., the full date/time or ID range used).

---

## ⚙️ Summary Table

| Method                  | Return Type        | Description           | Typical Use Case      |
| ----------------------- | ------------------ | --------------------- | --------------------- |
| `complement()`          | RangeSet           | Uncovered portions    | Available slots       |
| `subRangeSet(Range)`    | RangeSet           | Restrict to boundary  | Windowing/filtering   |
| `asRanges()`            | Set<Range>         | Underlying range view | Iteration, display    |
| `asSet(DiscreteDomain)` | ImmutableSortedSet | Discrete values       | Expand to full values |
| `contains(C)`           | boolean            | Value containment     | Validation            |
| `rangeContaining(C)`    | Range              | Find enclosing range  | Categorization        |
| `encloses(Range)`       | boolean            | Full coverage check   | Allocation fit        |
| `span()`                | Range              | Minimal enclosing     | Summary / boundaries  |

---

Exactly — this is a really insightful part of Guava’s design philosophy 💡

Guava not only gives you ready-made collections like `ImmutableList`, `Multimap`, and `RangeSet`, but also **tools to build your own specialized or “smart” collections** — when the standard ones aren’t enough.

Let’s break this idea down clearly 👇

---

## 🧠 Why You’d Write Your Own Collection Extension

Sometimes you need more than just data storage — you need **behavior**.

For example:

* ✅ Logging every time an item is added or removed
* ✅ Validating elements before insertion
* ✅ Lazily fetching data (like streaming from a DB query)
* ✅ Maintaining relationships between multiple collections
* ✅ Automatically syncing to another system (cache, DB, UI)

---

## ⚙️ Guava Utilities That Help You Extend Collections

Guava provides *helper base classes* to simplify the creation of custom collections without reimplementing all boilerplate logic (like iterators, size counting, contains checks, etc.).

Let’s look at the key extension helpers 👇

---

### 🧩 1. `ForwardingCollection` and Family

These are **decorator helpers**.

Guava provides:

* `ForwardingCollection<E>`
* `ForwardingList<E>`
* `ForwardingSet<E>`
* `ForwardingMap<K,V>`
* `ForwardingMultimap<K,V>`
* etc.

They let you **wrap** another collection and **override only the methods you care about** — forwarding all others automatically.

#### ✅ Example: LoggingList

```java
import com.google.common.collect.ForwardingList;
import java.util.ArrayList;
import java.util.List;

class LoggingList<E> extends ForwardingList<E> {
    private final List<E> delegate = new ArrayList<>();

    @Override
    protected List<E> delegate() {
        return delegate; // Guava forwards all un-overridden methods here
    }

    @Override
    public boolean add(E element) {
        System.out.println("Adding: " + element);
        return super.add(element);
    }

    @Override
    public boolean remove(Object object) {
        System.out.println("Removing: " + object);
        return super.remove(object);
    }
}
```

**Usage:**

```java
LoggingList<String> list = new LoggingList<>();
list.add("apple");
list.add("banana");
list.remove("apple");
```

✅ Output:

```
Adding: apple
Adding: banana
Removing: apple
```

🧠 You didn’t need to implement dozens of `List` methods — `ForwardingList` handles all forwarding automatically.

---

### 🧩 2. `ForwardingIterator`, `ForwardingQueue`, `ForwardingMapEntry`, etc.

Each has the same idea — you can decorate existing behavior with custom logic.

#### Example: Auditing Map

```java
import com.google.common.collect.ForwardingMap;
import java.util.HashMap;
import java.util.Map;

class AuditingMap<K, V> extends ForwardingMap<K, V> {
    private final Map<K, V> delegate = new HashMap<>();

    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }

    @Override
    public V put(K key, V value) {
        System.out.println("Audit: inserting key=" + key + ", value=" + value);
        return super.put(key, value);
    }
}
```

---

### 🧩 3. `AbstractIterator<T>`

A **powerful helper** for building **custom lazy iterators** — especially when data comes from external sources (files, APIs, DB).

Instead of manually managing `hasNext()` / `next()` logic, you just implement one method: `computeNext()`.

#### ✅ Example: Database-Like Iterator

```java
import com.google.common.collect.AbstractIterator;

import java.util.List;

class QueryIterator extends AbstractIterator<String> {
    private final List<String> rows;
    private int index = 0;

    QueryIterator(List<String> rows) {
        this.rows = rows;
    }

    @Override
    protected String computeNext() {
        if (index >= rows.size()) {
            return endOfData(); // signals no more elements
        }
        return rows.get(index++);
    }
}
```

**Usage:**

```java
QueryIterator iterator = new QueryIterator(List.of("row1", "row2", "row3"));
iterator.forEachRemaining(System.out::println);
```

✅ Output:

```
row1
row2
row3
```

🧠 No need to manage flags, exceptions, or boilerplate — Guava’s `AbstractIterator` handles iteration state.

---

### 🧩 4. `AbstractSequentialIterator<T>`

Useful for generating **sequences** (e.g., Fibonacci, linked lists, tree traversals).

#### ✅ Example: Fibonacci Sequence

```java
import com.google.common.collect.AbstractSequentialIterator;

class FibonacciIterator extends AbstractSequentialIterator<int[]>(int[] start) {
    FibonacciIterator() {
        super(new int[]{0, 1});
    }

    @Override
    protected int[] computeNext(int[] previous) {
        int next = previous[0] + previous[1];
        return next > 100 ? null : new int[]{previous[1], next};
    }
}
```

---

### 🧩 5. `AbstractMapBasedMultimap` (advanced)

If you’re building your own custom multimap with specialized constraints or backing data structure, you can extend this to get most of the complex behavior done for you.

---

## 🏗️ Real Production Scenarios

| Scenario                                 | Extension Type                            | Example                                |
| ---------------------------------------- | ----------------------------------------- | -------------------------------------- |
| Logging or metrics when elements change  | `ForwardingCollection` / `ForwardingList` | Log API inputs or usage                |
| Validation before insertion              | `ForwardingSet`                           | Validate IDs, prevent duplicates, etc. |
| Cached data retrieval                    | `AbstractIterator`                        | Lazy-fetch DB pages                    |
| Range-limited queries                    | `AbstractSequentialIterator`              | Generate time windows dynamically      |
| Wrap existing map to track modifications | `ForwardingMap`                           | Auditing, dirty tracking               |
| Combine multiple sources                 | Custom Iterable + `AbstractIterator`      | Stream from multiple APIs              |

---

## 🧩 Summary

| Utility                                 | Type                             | Purpose                                      |
| --------------------------------------- | -------------------------------- | -------------------------------------------- |
| `ForwardingList`, `ForwardingMap`, etc. | Decorator                        | Add behavior around existing collections     |
| `AbstractIterator`                      | Iterator helper                  | Simplify lazy iteration                      |
| `AbstractSequentialIterator`            | Iterator helper                  | Sequential / linked iteration                |
| `ForwardingMultimap`, `ForwardingTable` | Decorator                        | Add custom logic to multi-valued collections |
| `AbstractMapBasedMultimap`              | Base                             | Build your own efficient multimap            |
| `ForwardingObject`                      | Base class for all `Forwarding*` | Minimal forwarding boilerplate               |

---

Perfect 😎 — let’s build a **realistic production-style custom collection** that uses Guava’s extension utilities:
a `DbBackedList<E>` — a **list that transparently syncs with a database**, automatically loading new elements on demand and writing changes back.

---

## 💼 Use Case

Imagine you have a database table `user_events` storing event logs.
You want to:

* lazily **iterate** through events (to avoid loading all at once)
* **add** new events → automatically insert them into DB
* **remove** events → delete from DB
* but still use it **as a normal Java List**

---

## 🧩 Implementation Plan

We'll combine:

* `AbstractIterator<E>` → for lazy DB loading
* `ForwardingList<E>` → to decorate an in-memory cache list and intercept mutations (`add`, `remove`)

---

## 🧠 Step 1. Mock “Database”

```java
import java.util.*;

class MockDatabase {
    private final List<String> storage = new ArrayList<>(List.of(
            "Login", "ViewDashboard", "DownloadReport"
    ));

    public List<String> fetchAll() {
        System.out.println("📡 Fetching events from DB...");
        return new ArrayList<>(storage);
    }

    public void insert(String event) {
        System.out.println("💾 Inserting into DB: " + event);
        storage.add(event);
    }

    public void delete(String event) {
        System.out.println("🗑️ Deleting from DB: " + event);
        storage.remove(event);
    }
}
```

---

## 🧠 Step 2. Lazy Database Iterator (for reading)

```java
import com.google.common.collect.AbstractIterator;

class DbIterator extends AbstractIterator<String> {
    private final Iterator<String> dbIterator;

    DbIterator(MockDatabase db) {
        this.dbIterator = db.fetchAll().iterator();
    }

    @Override
    protected String computeNext() {
        if (dbIterator.hasNext()) {
            return dbIterator.next();
        }
        return endOfData();
    }
}
```

This is a **lazy** iterator — it only fetches elements when you start iterating.

---

## 🧠 Step 3. ForwardingList Wrapper (for write-through)

```java
import com.google.common.collect.ForwardingList;
import java.util.*;

class DbBackedList extends ForwardingList<String> {
    private final List<String> cache;
    private final MockDatabase db;

    DbBackedList(MockDatabase db) {
        this.db = db;
        // Load initial data lazily via iterator
        this.cache = new ArrayList<>();
        new DbIterator(db).forEachRemaining(cache::add);
    }

    @Override
    protected List<String> delegate() {
        return cache; // all read methods delegate here
    }

    @Override
    public boolean add(String element) {
        db.insert(element);
        return super.add(element);
    }

    @Override
    public boolean remove(Object element) {
        db.delete((String) element);
        return super.remove(element);
    }
}
```

---

## ✅ Step 4. Demo

```java
public class DbBackedListDemo {
    public static void main(String[] args) {
        MockDatabase db = new MockDatabase();
        DbBackedList events = new DbBackedList(db);

        System.out.println("Initial events: " + events);

        events.add("Logout");
        events.remove("DownloadReport");

        System.out.println("Updated events: " + events);
    }
}
```

### 🧾 Output

```
📡 Fetching events from DB...
Initial events: [Login, ViewDashboard, DownloadReport]
💾 Inserting into DB: Logout
🗑️ Deleting from DB: DownloadReport
Updated events: [Login, ViewDashboard, Logout]
```

---

## 🧩 What Happened Here

| Action               | Mechanism                            | Behavior                          |
| -------------------- | ------------------------------------ | --------------------------------- |
| `new DbBackedList()` | Uses `AbstractIterator`              | Lazily loads DB rows              |
| `add(element)`       | `ForwardingList.add()` overridden    | Adds to in-memory list **and** DB |
| `remove(element)`    | `ForwardingList.remove()` overridden | Removes from both                 |
| `iterator()`         | Comes from `ForwardingList`          | Uses cache transparently          |

---

## ⚙️ Production Value

This exact pattern can be used for:

* caching DB rows locally
* proxying REST or external API collections
* adding metrics or audit logging
* maintaining eventual consistency between systems
* validating or transforming data before persistence

---

## 🧠 Summary

| Utility Used          | Role                                                 |
| --------------------- | ---------------------------------------------------- |
| `ForwardingList`      | Wraps a base list with extra logic (DB sync)         |
| `AbstractIterator`    | Simplifies lazy DB iteration                         |
| Custom `MockDatabase` | Simulates persistence layer                          |
| Result                | Clean, readable, easily extendable custom collection |

---
