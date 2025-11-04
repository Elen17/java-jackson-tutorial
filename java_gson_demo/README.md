GSON

GSON is Google's JSON parser and generator for Java. Google developed GSON for internal use but open sourced it later.
GSON it reasonably easy to use, but in my opinion not as elegant as Jackson or Boon (the winner in my opinion). In this
GSON tutorial I will take you through how to use GSON to parse JSON into Java objects, and serialize Java objects into
JSON.

GSON contains multiple APIs which you can use to work with JSON. This tutorial covers the Gson component which parses
JSON into Java objects, or generates JSON from Java objects. In addition to the Gson component GSON also has a pull
parser in the GSON JsonReader component.

By default, the Gson object does not serialize fields with null values to JSON. If a field in a Java object is null,
Gson
excludes it.

Custom Instance Creators in GSON
By default GSON will try to create an instance of a given class by calling the no-arg constructor of that class.
However, if a given class does not have a default constructor, or you want to do some default configuration of the
instance, or if you want to create an instance of a subclass instead, you need to create and register your own instance
creator.

Excellent question — this is a **key GSON interview and assessment topic**, and understanding the difference between *
*TypeAdapters** and **custom serializers/deserializers** shows deep knowledge of how GSON works internally.

Let’s break it down clearly:

---

### 🧩 **1. Overview**

GSON provides **two main ways** to customize (de)serialization:

1. **Custom Serializer/Deserializer** – using `JsonSerializer<T>` and `JsonDeserializer<T>`.
2. **TypeAdapter<T>** – a lower-level and more powerful API.

---

### ⚖️ **2. When to Use Each**

| Feature                         | Custom Serializer/Deserializer                 | TypeAdapter                                   |
|---------------------------------|------------------------------------------------|-----------------------------------------------|
| **API Level**                   | High-level                                     | Low-level (closer to JSON streaming)          |
| **Ease of Use**                 | Easier to implement                            | More complex                                  |
| **Performance**                 | Slightly slower (uses tree model)              | Faster (uses streaming API directly)          |
| **Control over JSON structure** | Works with entire object as `JsonElement` tree | Works token by token (fine-grained control)   |
| **Best for**                    | Simple conversions or minor field tweaks       | Performance-critical or complex mapping logic |

---

### 💡 **3. When to Use `TypeAdapter`**

You should use a **TypeAdapter** instead of a serializer/deserializer when:

1. **You need performance optimization**

    * TypeAdapters use GSON’s **streaming API** (`JsonReader` / `JsonWriter`), which avoids building intermediate
      `JsonElement` trees.
    * This is critical for large datasets or performance-sensitive applications.

2. **You need full control over parsing or writing JSON**

    * For example, handling malformed JSON, skipping unknown fields, or writing out only certain properties
      conditionally.
    * You can decide how to read/write tokens in any order.

3. **You want to control both serialization and deserialization in one class**

    * `TypeAdapter` handles **both directions** in a single implementation, unlike separate `JsonSerializer` and
      `JsonDeserializer`.

4. **You need to interoperate with existing streaming logic**

    * If your application already uses GSON’s `JsonReader`/`JsonWriter`, a `TypeAdapter` integrates seamlessly.

---

### 📘 **4. Example Comparison**

#### ✅ Using `JsonSerializer` / `JsonDeserializer`

Good for simple transformations.

```java
class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("user_name", src.getName());
        return obj;
    }
}
```

#### 🚀 Using `TypeAdapter`

Better for performance or advanced control.

```java
class UserTypeAdapter extends TypeAdapter<User> {
    @Override
    public void write(JsonWriter out, User user) throws IOException {
        out.beginObject();
        out.name("user_name").value(user.getName());
        out.endObject();
    }

    @Override
    public User read(JsonReader in) throws IOException {
        User user = new User();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "user_name":
                    user.setName(in.nextString());
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return user;
    }
}
```

---

### 🧠 **5. Quick Summary for Exams or Interviews**

| Situation                                                              | Use                                   |
|------------------------------------------------------------------------|---------------------------------------|
| Simple transformations or renaming fields                              | `JsonSerializer` / `JsonDeserializer` |
| Complex parsing logic                                                  | `TypeAdapter`                         |
| Performance-critical (avoid tree model)                                | `TypeAdapter`                         |
| You want to handle both serialization and deserialization in one class | `TypeAdapter`                         |

---
Perfect — this is a **high-yield Gson assessment topic**.
When dealing with **Gson**, understanding which Java types need **custom adapters or (de)serializers** is crucial —
especially for *date/time*, *collections*, and *special objects*.

Let’s go over this systematically 👇

---

## 🕒 1. Date and Time Types — the Most Common Failure Point

Gson **does not support the Java 8+ date/time API** (`java.time.*`) out of the box.
It only supports **legacy java.util.Date**, **Calendar**, and **Timestamp** with limited flexibility.

So for **these types**, you must provide a custom adapter or serializer/deserializer:

| Type                                         | Reason             | Notes                                                     |
|----------------------------------------------|--------------------|-----------------------------------------------------------|
| `java.time.LocalDate`                        | No default adapter | Needs custom format, e.g. `"yyyy-MM-dd"`                  |
| `java.time.LocalDateTime`                    | No default adapter | Gson can’t parse or format automatically                  |
| `java.time.ZonedDateTime` / `OffsetDateTime` | No default adapter | Gson doesn’t understand time zones or offsets             |
| `java.time.Instant`                          | No default adapter | Can’t map epoch millis without custom logic               |
| `java.sql.Date`, `java.sql.Timestamp`        | Partial support    | Works, but better to define format explicitly             |
| `java.util.Date`                             | Has basic support  | Can be configured via `setDateFormat()` or custom adapter |

✅ Example:

```java
Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
```

Or simply use a **modern replacement library**, like `gson-javatime-serialisers`:

```java
Gson gson = Converters.registerAll(new GsonBuilder()).create();
```

---

## 🧩 2. Other Types Commonly Requiring Custom Adapters

| Type Category                     | Example                                                    | Why                                             | Recommended Action                                       |
|-----------------------------------|------------------------------------------------------------|-------------------------------------------------|----------------------------------------------------------|
| **Enums with custom names**       | `enum Status { OK, ERROR }` but JSON has `"ok"`, `"error"` | Gson uses `name()` by default                   | Use `@SerializedName` or custom adapter                  |
| **Interfaces / Abstract classes** | `Shape`, `Animal`, etc.                                    | Gson can’t know which subclass to instantiate   | Use runtime type adapter (`RuntimeTypeAdapterFactory`)   |
| **Polymorphic objects**           | e.g. list of `Vehicle` containing `Car` and `Bike`         | Gson doesn’t serialize/deserialize subtype info | Register type adapter factory                            |
| **Optional types**                | `java.util.Optional`                                       | Gson doesn’t support Optional                   | Custom adapter needed                                    |
| **BigDecimal / BigInteger**       | Supported, but may require strict precision control        | Sometimes format or rounding needed             | Custom serializer if precision or string format required |
| **UUID**                          | Sometimes serialized as binary/string mismatch             | Custom adapter if strict format required        |                                                          |
| **byte[]**                        | By default Base64-encoded                                  | OK, but override if you need hex or raw bytes   |                                                          |
| **Custom collections**            | e.g. `Multimap`, `ImmutableList` (Guava)                   | Not natively handled                            | Register adapter if using Guava or custom container      |

---

## ⚙️ 3. Practical GsonBuilder Setup Example

A solid, production-ready `GsonBuilder` often looks like this:

```java
Gson gson = new GsonBuilder()
        // Common Java 8+ time types
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())

        // Optionals and enums
        .registerTypeAdapter(Optional.class, new OptionalAdapter<>())
        .enableComplexMapKeySerialization()

        // If working with interfaces
        //.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(...))

        // Global settings
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .serializeNulls()
        .create();
```

---

## 📋 4. Quick “Assessment Answer”

> Gson natively supports `java.util.Date` and primitive types, but not Java 8 time types such as `LocalDate`,
`LocalDateTime`, `Instant`, or `ZonedDateTime`.
> For these and other complex or abstract types (like interfaces, Optional, or polymorphic hierarchies), you must
> register a custom `TypeAdapter` or `JsonSerializer/Deserializer` in your `GsonBuilder`.

---

Perfect — this is exactly the kind of **deeper Gson understanding** that shows up in **real assessments and advanced
interviews**.

Let’s go step-by-step 👇

---

## 🧩 **2. Other Types Commonly Requiring Custom Adapters (with examples and reasons)**

Below, you’ll see **why each type needs one**, plus a **short code example**.

---

### 🟢 **1. Enums with Custom Names**

**Why:**
By default, Gson serializes enums using their **`name()`**.
If your JSON uses **different labels**, you either use `@SerializedName` or a custom adapter.

**Example:**

```java
enum Status {
    @SerializedName("ok") OK,
    @SerializedName("error") ERROR
}
```

✅ Gson will now map `"ok"` ↔ `Status.OK`.

If you **can’t modify the enum**, use a `TypeAdapter`:

```java
class StatusAdapter extends TypeAdapter<Status> {
    public void write(JsonWriter out, Status value) throws IOException {
        out.value(value == Status.OK ? "ok" : "error");
    }

    public Status read(JsonReader in) throws IOException {
        return "ok".equals(in.nextString()) ? Status.OK : Status.ERROR;
    }
}
```

---

### 🟣 **2. Interfaces or Abstract Classes**

**Why:**
Gson doesn’t know *which concrete class* to instantiate.

**Example:**

```java
interface Shape {
}

class Circle implements Shape {
    double radius;
}

class Square implements Shape {
    double side;
}
```

You can’t just do:

```java
Shape shape = gson.fromJson("{\"radius\":5}", Shape.class);
```

❌ Gson throws an error: *abstract class/interface cannot be instantiated.*

✅ Solution — use **RuntimeTypeAdapterFactory** (custom type adapter):

```java
RuntimeTypeAdapterFactory<Shape> shapeAdapter =
        RuntimeTypeAdapterFactory.of(Shape.class, "type")
                .registerSubtype(Circle.class, "circle")
                .registerSubtype(Square.class, "square");

Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(shapeAdapter)
        .create();
```

Now Gson includes `"type": "circle"` when serializing, and uses it when deserializing.

---

### 🟠 **3. Polymorphic Objects (Inheritance)**

**Why:**
Similar to above — Gson doesn’t store type information, so it loses subclass identity.

**Example:**

```java
class Animal {
    String name;
}

class Dog extends Animal {
    boolean bark;
}
```

Serializing a `Dog` gives:

```json
{
  "name": "Rex",
  "bark": true
}
```

When deserialized to `Animal`, `bark` is lost.

✅ Solution:
Use a **TypeAdapterFactory** (same as above) to embed type metadata:

```json
{
  "type": "dog",
  "name": "Rex",
  "bark": true
}
```

---

### 🟤 **4. Optional Types**

**Why:**
Gson doesn’t know how to handle `Optional<T>` — it’s neither a primitive nor a normal object.

**Example:**

```java
class User {
    Optional<String> nickname;
}
```

❌ Without adapter → fails: `java.lang.UnsupportedOperationException: Attempted to serialize java.util.Optional`.

✅ Custom adapter:

```java
class OptionalAdapter<T> extends TypeAdapter<Optional<T>> {
    private final TypeAdapter<T> valueAdapter;

    OptionalAdapter(Gson gson, Type type) {
        valueAdapter = gson.getAdapter(TypeToken.get(type));
    }

    public void write(JsonWriter out, Optional<T> value) throws IOException {
        if (value == null || !value.isPresent()) out.nullValue();
        else valueAdapter.write(out, value.get());
    }

    public Optional<T> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return Optional.empty();
        }
        return Optional.ofNullable(valueAdapter.read(in));
    }
}
```

Register it:

```java
gsonBuilder.registerTypeAdapter(Optional .class, new OptionalAdapter<>(gson, String .class));
```

---

### 🔵 **5. BigDecimal / BigInteger**

**Why:**
Gson supports them, but sometimes you need strict control (e.g. preserve as string to avoid rounding).

**Example:**

```java
class Product {
    BigDecimal price;
}
```

✅ Custom adapter to serialize as string:

```java
class BigDecimalAsStringAdapter extends TypeAdapter<BigDecimal> {
    public void write(JsonWriter out, BigDecimal value) throws IOException {
        out.value(value.toPlainString());
    }

    public BigDecimal read(JsonReader in) throws IOException {
        return new BigDecimal(in.nextString());
    }
}
```

---

### 🔶 **6. UUID**

**Why:**
`UUID` serializes fine as a string by default, but sometimes you need custom binary, hyphen-free, or Base64 encoding.

**Example:**

```java
class UuidAdapter extends TypeAdapter<UUID> {
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(value.toString().replace("-", "")); // store compact form
    }

    public UUID read(JsonReader in) throws IOException {
        String s = in.nextString();
        return UUID.fromString(s.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));
    }
}
```

---

### 🟡 **7. byte[]**

**Why:**
By default, Gson encodes `byte[]` to Base64, but if you need custom encoding (e.g. hex), you’ll need your own adapter.

**Example:**

```java
class ByteArrayHexAdapter extends TypeAdapter<byte[]> {
    public void write(JsonWriter out, byte[] bytes) throws IOException {
        out.value(javax.xml.bind.DatatypeConverter.printHexBinary(bytes));
    }

    public byte[] read(JsonReader in) throws IOException {
        return javax.xml.bind.DatatypeConverter.parseHexBinary(in.nextString());
    }
}
```

---

### 🟢 **8. Custom or Immutable Collections (Guava)**

**Why:**
`ImmutableList`, `ImmutableMap`, etc. are not directly supported because Gson tries to call a no-arg constructor.

**Example:**

```java
ImmutableList<String> names = ImmutableList.of("Alice", "Bob");
```

❌ Gson can’t deserialize into immutable collections.

✅ Use **Guava’s Gson TypeAdapters**:

```java
Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory())
        .create();
```

---

## ⚙️ **About `.enableComplexMapKeySerialization()`**

By default, Gson can only serialize maps with **simple (String) keys**.

```java
Map<String, Integer> map = Map.of("a", 1); // ✅ works
Map<Point, Integer> map = Map.of(new Point(1, 2), 3); // ❌ fails
```

If you enable this:

```java
Gson gson = new GsonBuilder()
        .enableComplexMapKeySerialization()
        .create();
```

Then Gson will handle **complex (non-String) map keys** by converting them to JSON objects.

Example:

```java
record Point(int x, int y) {
}

Map<Point, String> map = new HashMap<>();
map.

put(new Point(1, 2), "A");
        System.out.

println(gson.toJson(map));
```

✅ Output with `enableComplexMapKeySerialization()`:

```json
{
  "{\"x\":1,\"y\":2}": "A"
}
```

Without it, you get an exception or lossy serialization.

---

## 📋 **Summary Table**

| Type                           | Why Needs Adapter           | Example Adapter                       |
|--------------------------------|-----------------------------|---------------------------------------|
| Enum (custom naming)           | JSON names differ           | `StatusAdapter`                       |
| Interface / Abstract           | No instantiation possible   | `RuntimeTypeAdapterFactory`           |
| Polymorphic                    | Subtype info lost           | `TypeAdapterFactory`                  |
| Optional                       | Not natively supported      | `OptionalAdapter`                     |
| BigDecimal                     | Precision or format control | `BigDecimalAsStringAdapter`           |
| UUID                           | Custom format or encoding   | `UuidAdapter`                         |
| byte[]                         | Custom binary encoding      | `ByteArrayHexAdapter`                 |
| Immutable / Custom collections | Gson can’t construct them   | Custom or Guava adapters              |
| Complex Map Keys               | Not supported by default    | `.enableComplexMapKeySerialization()` |

---

Excellent — let’s break this down clearly 👇

---

## 🧩 What is `JsonReader`?

`JsonReader` is a **streaming JSON parser** provided by **Gson** (in `com.google.gson.stream.JsonReader`).

It reads JSON **token by token**, instead of loading the entire JSON document into memory at once.
This makes it **memory-efficient and faster** for **large JSON files or streams** — similar to how `XMLStreamReader`
works for XML.

---

## 💡 Why and When to Use `JsonReader`

You’d use `JsonReader` when:

1. You’re dealing with **very large JSON files** (e.g., logs, large datasets).
2. You **don’t need to map everything** into objects — you might only want a few fields.
3. You need **fine-grained control** over parsing, or performance is critical.

By contrast, `Gson.fromJson()` reads and maps the entire JSON tree into memory, which is easier but heavier.

---

## ⚙️ Example: Simple Usage

Let’s say we have this JSON:

```json
{
  "name": "Alice",
  "age": 28,
  "languages": [
    "Java",
    "Python",
    "Kotlin"
  ]
}
```

We can parse it with `JsonReader` like this:

```java
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.io.IOException;

public class JsonReaderExample {
    public static void main(String[] args) throws IOException {
        String json = """
                {
                  "name": "Alice",
                  "age": 28,
                  "languages": ["Java", "Python", "Kotlin"]
                }
                """;

        JsonReader reader = new JsonReader(new StringReader(json));

        reader.beginObject(); // Start reading the JSON object

        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "name" -> System.out.println("Name: " + reader.nextString());
                case "age" -> System.out.println("Age: " + reader.nextInt());
                case "languages" -> {
                    System.out.println("Languages:");
                    reader.beginArray();
                    while (reader.hasNext()) {
                        System.out.println(" - " + reader.nextString());
                    }
                    reader.endArray();
                }
                default -> reader.skipValue(); // Skip unexpected fields
            }
        }

        reader.endObject();
        reader.close();
    }
}
```

### Output:

```
Name: Alice
Age: 28
Languages:
 - Java
 - Python
 - Kotlin
```

---

## ⚡ Key Methods in `JsonReader`

| Method                                                       | Description                                                                                                                                                                                                                   |
|--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `beginObject()` / `endObject()`                              | Marks the start and end of `{}`                                                                                                                                                                                               |
| `beginArray()` / `endArray()`                                | Marks the start and end of `[]`                                                                                                                                                                                               |
| `nextName()`                                                 | Returns the next property name                                                                                                                                                                                                |
| `nextString()`, `nextInt()`, `nextBoolean()`, `nextDouble()` | Reads values                                                                                                                                                                                                                  |
| `hasNext()`                                                  | Checks if there are more tokens                                                                                                                                                                                               |
| `skipValue()`                                                | Skips an unwanted value                                                                                                                                                                                                       |
| `peek()`                                                     | Lets you inspect the next token type (without consuming it) <br/>The JsonReader peek() method returns the next JSON token, but without moving over it. Multiple calls to peek() subsequently will return the same JSON token. |
|                                                              |                                                                                                                                                                                                                               |

---

## 🚀 Use Case Example: Large JSON Log Reader

If you had a 1GB log file like:

```json
[
  {
    "id": 1,
    "type": "INFO",
    "message": "Server started"
  },
  {
    "id": 2,
    "type": "ERROR",
    "message": "Connection failed"
  },
  ...
]
```

You could process it efficiently line by line:

```java
JsonReader reader = new JsonReader(new FileReader("logs.json"));
reader.

beginArray();

while(reader.

hasNext()){
        reader.

beginObject();

String type = null;
String message = null;

    while(reader.

hasNext()){
String name = reader.nextName();
        switch(name){
        case"type"->type =reader.

nextString();
            case"message"->message =reader.

nextString();

default ->reader.

skipValue();
        }
                }

                if("ERROR".

equals(type)){
        System.out.

println("Error log: "+message);
    }

            reader.

endObject();
}

        reader.

endArray();
reader.

close();
```

This approach doesn’t load the whole file into memory.

---

## 🆚 Comparison with Other Gson Parsers

| Approach     | Class                       | Description                                  |
|--------------|-----------------------------|----------------------------------------------|
| Streaming    | `JsonReader`                | Low-level, fastest, least memory usage       |
| Tree model   | `JsonElement`, `JsonObject` | Medium-level, easier to traverse             |
| Data binding | `Gson.fromJson()`           | High-level, easiest (maps directly to POJOs) |

---

Perfect 👌 — let’s complete the Gson “low-level trio”:
👉 `JsonReader`, `JsonWriter`, and `JsonParser`.

Each serves a different purpose and level of abstraction when working with JSON in **Gson** (`com.google.gson.*`).

---

## 🧩 1. `JsonReader` — Streaming JSON *Reader*

We covered this one already, but quick recap:

* **Package:** `com.google.gson.stream.JsonReader`
* **Purpose:** Efficiently *read* JSON tokens (name–value pairs, arrays, etc.) one by one.
* **Used for:** Large JSON input streams or selective parsing.

✅ **You pull data** from JSON manually — *you are in control*.

---

## 🧾 2. `JsonWriter` — Streaming JSON *Writer*

* **Package:** `com.google.gson.stream.JsonWriter`
* **Purpose:** Efficiently *write* JSON output in a streaming way.
* **Use case:** When generating large JSON data or serializing partial data manually.
* **Analogy:** It’s like `JsonReader`, but for output.

Instead of creating an object tree and letting Gson serialize it automatically, you can use `JsonWriter` to write JSON token by token.

---

### ⚙️ Example: Using `JsonWriter`

```java
import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import java.io.IOException;

public class JsonWriterExample {
    public static void main(String[] args) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);

        writer.beginObject();          // {
        writer.name("name").value("Alice");
        writer.name("age").value(28);

        writer.name("languages");
        writer.beginArray();           // "languages": [
        writer.value("Java");
        writer.value("Python");
        writer.value("Kotlin");
        writer.endArray();             // ]

        writer.endObject();            // }
        writer.close();

        System.out.println(stringWriter.toString());
    }
}
```

### 🧮 Output:

```json
{"name":"Alice","age":28,"languages":["Java","Python","Kotlin"]}
```

---

### 💡 Notes:

* You must **explicitly** call `beginObject()`, `beginArray()`, and their matching `end...()` methods.
* You can **nest** writers (objects within objects).
* It’s perfect for writing **streams, large files, or partial JSON data**.
* Gson’s own serializer (`Gson.toJson`) internally uses `JsonWriter`.

---

## 🧠 3. `JsonParser` — Tree-based Parser (Intermediate Layer)

* **Package:** `com.google.gson.JsonParser`
* **Purpose:** Parse JSON text into Gson’s **tree model** — `JsonElement`.
* **Use case:** When you want to **inspect or modify** JSON dynamically (without a POJO class).

✅ Unlike `JsonReader`, it reads the **whole document** and gives you a navigable object tree.

---

### ⚙️ Example: Using `JsonParser`

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class JsonParserExample {
    public static void main(String[] args) {
        String json = """
        {
          "name": "Alice",
          "age": 28,
          "languages": ["Java", "Python", "Kotlin"]
        }
        """;

        JsonElement element = JsonParser.parseString(json);
        JsonObject obj = element.getAsJsonObject();

        String name = obj.get("name").getAsString();
        int age = obj.get("age").getAsInt();
        JsonArray langs = obj.getAsJsonArray("languages");

        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Languages:");
        langs.forEach(lang -> System.out.println(" - " + lang.getAsString()));
    }
}
```

### ✅ Output:

```
Name: Alice
Age: 28
Languages:
 - Java
 - Python
 - Kotlin
```

---

### 🌳 JsonParser Tree Classes

| Class           | Description                                              | Example                       |
| --------------- | -------------------------------------------------------- | ----------------------------- |
| `JsonElement`   | Base class (can be an object, array, primitive, or null) | —                             |
| `JsonObject`    | `{}` — key–value pairs                                   | `jsonObject.get("key")`       |
| `JsonArray`     | `[]` — ordered elements                                  | `jsonArray.get(0)`            |
| `JsonPrimitive` | single value (string, number, boolean)                   | `jsonPrimitive.getAsString()` |
| `JsonNull`      | represents null                                          | —                             |

---

## ⚡ Comparing All Three

| Feature          | `JsonReader`                | `JsonWriter`                | `JsonParser`                         |
| ---------------- | --------------------------- | --------------------------- | ------------------------------------ |
| **Direction**    | Read                        | Write                       | Read                                 |
| **Type**         | Streaming (low-level)       | Streaming (low-level)       | Tree-based (mid-level)               |
| **Performance**  | 🏎️ Fastest                 | 🏎️ Fastest                 | 🐢 Slower (loads full JSON)          |
| **Memory usage** | 💧 Low                      | 💧 Low                      | 💥 High (whole tree in memory)       |
| **Ease of use**  | ⚙️ Manual                   | ⚙️ Manual                   | 😊 Easy                              |
| **Use case**     | Parse large or partial JSON | Write large or partial JSON | Inspect/modify full JSON dynamically |

---

## 🧩 Real-world Example Combining Them

You can even **read with `JsonReader`**, **modify using `JsonParser`**, and **write out with `JsonWriter`** —
for example, when filtering a large dataset without loading everything.

---
Perfect 🔥 this will show how **all three — `JsonReader`, `JsonParser`, and `JsonWriter` — work together** in a practical, real-world data-processing pipeline.

Let’s imagine a **large JSON log file**, but we’ll simulate it inline for simplicity.

---

## 🎯 Goal

1. **Read** each JSON object from a big JSON array using `JsonReader`.
2. **Modify** the JSON objects using `JsonParser` (e.g., add a new field, or update one).
3. **Write** the modified objects back to a new JSON file using `JsonWriter`.

---

## ⚙️ Full Example

```java
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;

public class JsonPipelineExample {
    public static void main(String[] args) {
        String inputJson = """
        [
          {"id": 1, "type": "INFO", "message": "Server started"},
          {"id": 2, "type": "ERROR", "message": "Connection failed"},
          {"id": 3, "type": "INFO", "message": "User logged in"}
        ]
        """;

        try (
            JsonReader reader = new JsonReader(new StringReader(inputJson));
            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = new JsonWriter(stringWriter)
        ) {
            // Step 1: Begin reading input array
            reader.beginArray();
            writer.beginArray(); // Prepare output array

            while (reader.hasNext()) {
                // Step 2: Parse current object using JsonParser
                JsonElement element = JsonParser.parseReader(reader);
                JsonObject obj = element.getAsJsonObject();

                // Step 3: Modify the object
                modifyLogEntry(obj);

                // Step 4: Write modified object to output
                Gson gson = new Gson();
                gson.toJson(obj, writer); // Writes JSON directly using writer
            }

            reader.endArray();
            writer.endArray();
            writer.close();

            // Output final transformed JSON
            System.out.println("✅ Modified JSON output:");
            System.out.println(stringWriter.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void modifyLogEntry(JsonObject obj) {
        // Example modifications:
        // 1. Add timestamp field
        obj.addProperty("timestamp", System.currentTimeMillis());

        // 2. Enrich ERROR messages with severity
        if ("ERROR".equals(obj.get("type").getAsString())) {
            obj.addProperty("severity", "HIGH");
        }

        // 3. Transform message text
        String message = obj.get("message").getAsString();
        obj.addProperty("message", message.toUpperCase());
    }
}
```

---

## 🧮 Output

```json
[
  {
    "id": 1,
    "type": "INFO",
    "message": "SERVER STARTED",
    "timestamp": 1730273812345
  },
  {
    "id": 2,
    "type": "ERROR",
    "message": "CONNECTION FAILED",
    "timestamp": 1730273812345,
    "severity": "HIGH"
  },
  {
    "id": 3,
    "type": "INFO",
    "message": "USER LOGGED IN",
    "timestamp": 1730273812345
  }
]
```

---

## 🧠 Breakdown

| Step | Component                          | Purpose                                                             |
| ---- | ---------------------------------- | ------------------------------------------------------------------- |
| 1️⃣  | **`JsonReader`**                   | Reads one JSON object at a time — efficient for large arrays        |
| 2️⃣  | **`JsonParser`**                   | Converts the current object into a `JsonObject` so we can modify it |
| 3️⃣  | **Modify method**                  | Adds fields, edits strings, etc.                                    |
| 4️⃣  | **`JsonWriter` + `Gson.toJson()`** | Writes the updated object directly into an output JSON array        |

---

## 💡 Key Advantages

✅ **Memory Efficient:**
Only one object is in memory at a time — not the entire array.

✅ **Flexible:**
You can modify, remove, or enrich objects easily via `JsonParser` + `JsonObject`.

✅ **Streaming-Friendly:**
This pattern works even for **gigabyte-sized files** using `FileReader`/`FileWriter`.

---

## 🚀 Real-World Use Case Examples

* Processing massive JSON logs or datasets for analytics.
* Filtering and cleaning imported data before saving to a database.
* Streaming transformations in ETL (Extract–Transform–Load) pipelines.

---


