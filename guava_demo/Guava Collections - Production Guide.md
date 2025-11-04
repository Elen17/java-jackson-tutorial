# Guava Collections - Production Code Guide

A comprehensive guide to Google Guava collections with real-world production examples.

## Table of Contents
- [Immutable Collections](#immutable-collections)
- [New Collection Types](#new-collection-types)
- [Collection Utilities](#collection-utilities)
- [Best Practices](#best-practices)

---

## Immutable Collections

### ImmutableList

**Purpose**: Thread-safe, immutable ordered collection with guaranteed iteration order.

**Production Use Cases**:

```java
// 1. Configuration Management
public class DatabaseConfig {
    private final ImmutableList<String> allowedHosts;
    
    public DatabaseConfig(List<String> hosts) {
        this.allowedHosts = ImmutableList.copyOf(hosts);
    }
    
    public ImmutableList<String> getAllowedHosts() {
        return allowedHosts; // Safe to return - no defensive copy needed
    }
}

// 2. API Response DTOs
@RestController
public class UserController {
    
    @GetMapping("/users/{id}/roles")
    public ImmutableList<String> getUserRoles(@PathVariable Long id) {
        return userService.getRoles(id); // Guaranteed immutable
    }
}

// 3. Domain Model with Ordered Items
public class ShoppingCart {
    private final ImmutableList<CartItem> items;
    
    public ShoppingCart(List<CartItem> items) {
        this.items = ImmutableList.copyOf(items);
    }
    
    public BigDecimal getTotalPrice() {
        return items.stream()
            .map(CartItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// 4. Builder Pattern for Complex Objects
public class Report {
    private final String title;
    private final ImmutableList<Section> sections;
    
    public static class Builder {
        private final ImmutableList.Builder<Section> sections = ImmutableList.builder();
        private String title;
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder addSection(Section section) {
            sections.add(section);
            return this;
        }
        
        public Builder addAllSections(Iterable<Section> sections) {
            this.sections.addAll(sections);
            return this;
        }
        
        public Report build() {
            return new Report(title, sections.build());
        }
    }
}
```

---

### ImmutableSet

**Purpose**: Thread-safe, immutable collection with no duplicates and fast lookups.

**Production Use Cases**:

```java
// 1. Feature Flags / Permissions
public class UserPermissions {
    private final ImmutableSet<Permission> permissions;
    
    public UserPermissions(Set<Permission> permissions) {
        this.permissions = ImmutableSet.copyOf(permissions);
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission); // O(1) lookup
    }
    
    public boolean hasAllPermissions(Permission... required) {
        return permissions.containsAll(Arrays.asList(required));
    }
}

// 2. Allowed/Blocked Lists
public class ContentModerator {
    private static final ImmutableSet<String> PROFANITY = ImmutableSet.of(
        "badword1", "badword2", "badword3"
    );
    
    public boolean containsProfanity(String text) {
        return Arrays.stream(text.toLowerCase().split("\\s+"))
            .anyMatch(PROFANITY::contains);
    }
}

// 3. Valid Enum Values as Constants
public class OrderService {
    private static final ImmutableSet<OrderStatus> MODIFIABLE_STATUSES = 
        ImmutableSet.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);
    
    public void updateOrder(Order order, OrderStatus newStatus) {
        if (!MODIFIABLE_STATUSES.contains(order.getStatus())) {
            throw new IllegalStateException(
                "Cannot modify order in status: " + order.getStatus()
            );
        }
        order.setStatus(newStatus);
    }
}

// 4. Deduplication in Data Processing
public class DataProcessor {
    public ImmutableSet<String> extractUniqueEmails(List<User> users) {
        return users.stream()
            .map(User::getEmail)
            .collect(ImmutableSet.toImmutableSet());
    }
}
```

---

### ImmutableMap

**Purpose**: Thread-safe, immutable key-value mappings with fast lookups.

**Production Use Cases**:

```java
// 1. Configuration Constants
public class AppConfig {
    public static final ImmutableMap<String, String> DEFAULT_HEADERS = 
        ImmutableMap.<String, String>builder()
            .put("Content-Type", "application/json")
            .put("Accept", "application/json")
            .put("X-API-Version", "v1")
            .build();
    
    public static final ImmutableMap<Environment, String> DATABASE_URLS = 
        ImmutableMap.of(
            Environment.DEV, "jdbc:postgresql://dev-db:5432/myapp",
            Environment.STAGING, "jdbc:postgresql://staging-db:5432/myapp",
            Environment.PROD, "jdbc:postgresql://prod-db:5432/myapp"
        );
}

// 2. Error Code Mappings
public class ErrorCodeMapper {
    private static final ImmutableMap<String, Integer> ERROR_TO_HTTP_STATUS = 
        ImmutableMap.<String, Integer>builder()
            .put("INVALID_INPUT", 400)
            .put("UNAUTHORIZED", 401)
            .put("FORBIDDEN", 403)
            .put("NOT_FOUND", 404)
            .put("INTERNAL_ERROR", 500)
            .build();
    
    public int getHttpStatus(String errorCode) {
        return ERROR_TO_HTTP_STATUS.getOrDefault(errorCode, 500);
    }
}

// 3. Price Lookup Tables
public class PricingService {
    private final ImmutableMap<String, BigDecimal> productPrices;
    
    public PricingService(Map<String, BigDecimal> prices) {
        this.productPrices = ImmutableMap.copyOf(prices);
    }
    
    public BigDecimal getPrice(String productId) {
        return productPrices.getOrDefault(productId, BigDecimal.ZERO);
    }
}

// 4. Dependency Injection Configuration
@Configuration
public class ServiceConfig {
    
    @Bean
    public ImmutableMap<String, MessageProcessor> messageProcessors(
            EmailProcessor emailProcessor,
            SmsProcessor smsProcessor,
            PushProcessor pushProcessor) {
        
        return ImmutableMap.of(
            "EMAIL", emailProcessor,
            "SMS", smsProcessor,
            "PUSH", pushProcessor
        );
    }
}
```

---

## New Collection Types

### Multiset

**Purpose**: A collection that allows duplicate elements and tracks their counts. Like a `Map<E, Integer>` but with collection semantics.

**Production Use Cases**:

```java
// 1. Inventory Management
public class WarehouseInventory {
    private final Multiset<String> stockLevels = HashMultiset.create();
    
    public void addStock(String productId, int quantity) {
        stockLevels.add(productId, quantity);
    }
    
    public void removeStock(String productId, int quantity) {
        stockLevels.remove(productId, quantity);
    }
    
    public int getStockLevel(String productId) {
        return stockLevels.count(productId);
    }
    
    public Map<String, Integer> getLowStockProducts(int threshold) {
        return stockLevels.entrySet().stream()
            .filter(entry -> entry.getCount() < threshold)
            .collect(Collectors.toMap(
                Multiset.Entry::getElement,
                Multiset.Entry::getCount
            ));
    }
}

// 2. Word Frequency Analysis
public class TextAnalyzer {
    public ImmutableMultiset<String> analyzeWordFrequency(String text) {
        Multiset<String> wordCounts = HashMultiset.create();
        
        Arrays.stream(text.toLowerCase().split("\\W+"))
            .filter(word -> !word.isEmpty())
            .forEach(wordCounts::add);
        
        return ImmutableMultiset.copyOf(wordCounts);
    }
    
    public List<String> getTopWords(String text, int topN) {
        Multiset<String> wordCounts = analyzeWordFrequency(text);
        
        return Multisets.copyHighestCountFirst(wordCounts)
            .elementSet()
            .stream()
            .limit(topN)
            .collect(Collectors.toList());
    }
}

// 3. Vote Counting System
public class VotingSystem {
    private final Multiset<String> votes = ConcurrentHashMultiset.create();
    
    public void castVote(String candidate) {
        votes.add(candidate);
    }
    
    public String getWinner() {
        return votes.entrySet().stream()
            .max(Comparator.comparingInt(Multiset.Entry::getCount))
            .map(Multiset.Entry::getElement)
            .orElse(null);
    }
    
    public Map<String, Integer> getResults() {
        return votes.entrySet().stream()
            .collect(Collectors.toMap(
                Multiset.Entry::getElement,
                Multiset.Entry::getCount
            ));
    }
}

// 4. Event Tracking
public class EventTracker {
    private final Multiset<String> eventCounts = HashMultiset.create();
    
    public void trackEvent(String eventType) {
        eventCounts.add(eventType);
    }
    
    public Map<String, Integer> getEventStatistics() {
        ImmutableMap.Builder<String, Integer> stats = ImmutableMap.builder();
        for (Multiset.Entry<String> entry : eventCounts.entrySet()) {
            stats.put(entry.getElement(), entry.getCount());
        }
        return stats.build();
    }
}
```

---

### Multimap

**Purpose**: A map that can associate multiple values with a single key. Like `Map<K, Collection<V>>` but cleaner.

**Production Use Cases**:

```java
// 1. User Role Management
public class RoleService {
    private final Multimap<String, String> userRoles = HashMultimap.create();
    
    public void assignRole(String userId, String role) {
        userRoles.put(userId, role);
    }
    
    public void removeRole(String userId, String role) {
        userRoles.remove(userId, role);
    }
    
    public Collection<String> getUserRoles(String userId) {
        return userRoles.get(userId);
    }
    
    public boolean hasRole(String userId, String role) {
        return userRoles.containsEntry(userId, role);
    }
}

// 2. Tag-Based Search Index
public class ArticleIndex {
    private final Multimap<String, Article> tagIndex = HashMultimap.create();
    
    public void indexArticle(Article article) {
        for (String tag : article.getTags()) {
            tagIndex.put(tag.toLowerCase(), article);
        }
    }
    
    public Collection<Article> findByTag(String tag) {
        return tagIndex.get(tag.toLowerCase());
    }
    
    public Collection<Article> findByAnyTag(Set<String> tags) {
        return tags.stream()
            .flatMap(tag -> tagIndex.get(tag.toLowerCase()).stream())
            .distinct()
            .collect(Collectors.toList());
    }
}

// 3. Email Distribution Lists
public class EmailService {
    private final Multimap<String, String> distributionLists = 
        ArrayListMultimap.create();
    
    public void addToList(String listName, String email) {
        distributionLists.put(listName, email);
    }
    
    public void sendToList(String listName, String message) {
        Collection<String> recipients = distributionLists.get(listName);
        for (String email : recipients) {
            sendEmail(email, message);
        }
    }
    
    private void sendEmail(String email, String message) {
        // Email sending logic
    }
}

// 4. Request Parameter Handling
public class RequestParser {
    public Multimap<String, String> parseQueryParams(String queryString) {
        Multimap<String, String> params = LinkedHashMultimap.create();
        
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        
        for (String param : queryString.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2) {
                params.put(
                    URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                );
            }
        }
        
        return params;
    }
}

// 5. Grouping Database Results
public class ReportService {
    public Multimap<String, Order> groupOrdersByCustomer(List<Order> orders) {
        Multimap<String, Order> grouped = LinkedListMultimap.create();
        
        for (Order order : orders) {
            grouped.put(order.getCustomerId(), order);
        }
        
        return grouped;
    }
    
    public Map<String, BigDecimal> calculateTotalsByCustomer(List<Order> orders) {
        Multimap<String, Order> grouped = groupOrdersByCustomer(orders);
        
        return grouped.asMap().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            ));
    }
}
```

---

### BiMap

**Purpose**: Bidirectional map that maintains both key-to-value and value-to-key mappings. Ensures 1:1 relationship.

**Production Use Cases**:

```java
// 1. User ID to Username Mapping
public class UserRegistry {
    private final BiMap<Long, String> userIdToUsername = HashBiMap.create();
    
    public void registerUser(Long userId, String username) {
        if (userIdToUsername.containsValue(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        userIdToUsername.put(userId, username);
    }
    
    public String getUsername(Long userId) {
        return userIdToUsername.get(userId);
    }
    
    public Long getUserId(String username) {
        return userIdToUsername.inverse().get(username);
    }
}

// 2. Currency Code Mapping
public class CurrencyService {
    private static final BiMap<String, String> CURRENCY_CODES = 
        ImmutableBiMap.of(
            "USD", "US Dollar",
            "EUR", "Euro",
            "GBP", "British Pound",
            "JPY", "Japanese Yen"
        );
    
    public String getCurrencyName(String code) {
        return CURRENCY_CODES.get(code);
    }
    
    public String getCurrencyCode(String name) {
        return CURRENCY_CODES.inverse().get(name);
    }
}

// 3. Session Token to User Mapping
public class SessionManager {
    private final BiMap<String, Long> tokenToUserId = 
        Maps.synchronizedBiMap(HashBiMap.create());
    
    public String createSession(Long userId) {
        // Invalidate existing session for user
        tokenToUserId.inverse().remove(userId);
        
        String token = generateToken();
        tokenToUserId.put(token, userId);
        return token;
    }
    
    public Long getUserIdByToken(String token) {
        return tokenToUserId.get(token);
    }
    
    public void invalidateSession(Long userId) {
        tokenToUserId.inverse().remove(userId);
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}

// 4. External ID Mapping (Integration)
public class ExternalSystemIntegration {
    private final BiMap<String, String> internalToExternalId = HashBiMap.create();
    
    public void mapIds(String internalId, String externalId) {
        internalToExternalId.forcePut(internalId, externalId);
    }
    
    public String getExternalId(String internalId) {
        return internalToExternalId.get(internalId);
    }
    
    public String getInternalId(String externalId) {
        return internalToExternalId.inverse().get(externalId);
    }
}
```

---

### Table

**Purpose**: A collection with two keys (row and column) mapping to a value. Like `Map<R, Map<C, V>>` but cleaner.

**Production Use Cases**:

```java
// 1. Pricing Matrix (Product × Region)
public class PricingService {
    private final Table<String, String, BigDecimal> pricingTable = 
        HashBasedTable.create();
    
    public void setPrice(String productId, String region, BigDecimal price) {
        pricingTable.put(productId, region, price);
    }
    
    public BigDecimal getPrice(String productId, String region) {
        return pricingTable.get(productId, region);
    }
    
    public Map<String, BigDecimal> getPricesForProduct(String productId) {
        return pricingTable.row(productId);
    }
    
    public Map<String, BigDecimal> getPricesInRegion(String region) {
        return pricingTable.column(region);
    }
}

// 2. Feature Matrix (User × Feature)
public class FeatureToggleService {
    private final Table<String, String, Boolean> featureMatrix = 
        HashBasedTable.create();
    
    public void enableFeature(String userId, String featureName) {
        featureMatrix.put(userId, featureName, true);
    }
    
    public boolean isFeatureEnabled(String userId, String featureName) {
        return featureMatrix.contains(userId, featureName) 
            && Boolean.TRUE.equals(featureMatrix.get(userId, featureName));
    }
    
    public Set<String> getEnabledFeatures(String userId) {
        return featureMatrix.row(userId).entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
}

// 3. Distance/Cost Matrix
public class RouteOptimizer {
    private final Table<String, String, Double> distanceMatrix = 
        HashBasedTable.create();
    
    public void setDistance(String fromCity, String toCity, double distance) {
        distanceMatrix.put(fromCity, toCity, distance);
    }
    
    public Double getDistance(String fromCity, String toCity) {
        return distanceMatrix.get(fromCity, toCity);
    }
    
    public String findNearestCity(String fromCity) {
        Map<String, Double> distances = distanceMatrix.row(fromCity);
        return distances.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}

// 4. Permission Matrix (User × Resource)
public class AccessControlService {
    private final Table<String, String, Permission> accessControl = 
        HashBasedTable.create();
    
    public void grantAccess(String userId, String resourceId, Permission permission) {
        accessControl.put(userId, resourceId, permission);
    }
    
    public boolean canAccess(String userId, String resourceId, Permission required) {
        Permission granted = accessControl.get(userId, resourceId);
        return granted != null && granted.includes(required);
    }
    
    public Map<String, Permission> getUserPermissions(String userId) {
        return accessControl.row(userId);
    }
}

// 5. Configuration Matrix (Environment × Service)
public class ConfigurationService {
    private final Table<Environment, String, String> configs = 
        TreeBasedTable.create();
    
    public void setConfig(Environment env, String serviceName, String configValue) {
        configs.put(env, serviceName, configValue);
    }
    
    public String getConfig(Environment env, String serviceName) {
        return configs.get(env, serviceName);
    }
    
    public Map<String, String> getAllConfigsForEnvironment(Environment env) {
        return configs.row(env);
    }
}
```

---

### RangeSet and RangeMap

**Purpose**: Collections for working with ranges of values (intervals).

**Production Use Cases**:

```java
// 1. Business Hours / Availability Scheduling
public class AvailabilityService {
    private final RangeSet<LocalTime> businessHours = TreeRangeSet.create();
    
    public AvailabilityService() {
        // Monday-Friday: 9 AM - 5 PM
        businessHours.add(Range.closed(
            LocalTime.of(9, 0),
            LocalTime.of(17, 0)
        ));
    }
    
    public boolean isBusinessHours(LocalTime time) {
        return businessHours.contains(time);
    }
    
    public void addBreak(LocalTime start, LocalTime end) {
        businessHours.remove(Range.closed(start, end));
    }
}

// 2. IP Address Range Management
public class IpWhitelistService {
    private final RangeSet<Long> allowedIpRanges = TreeRangeSet.create();
    
    public void addIpRange(String startIp, String endIp) {
        long start = ipToLong(startIp);
        long end = ipToLong(endIp);
        allowedIpRanges.add(Range.closed(start, end));
    }
    
    public boolean isAllowed(String ip) {
        return allowedIpRanges.contains(ipToLong(ip));
    }
    
    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        return (Long.parseLong(parts[0]) << 24) +
               (Long.parseLong(parts[1]) << 16) +
               (Long.parseLong(parts[2]) << 8) +
               Long.parseLong(parts[3]);
    }
}

// 3. Price Tier Management (RangeMap)
public class ShippingCostCalculator {
    private final RangeMap<Double, BigDecimal> shippingCosts = TreeRangeMap.create();
    
    public ShippingCostCalculator() {
        shippingCosts.put(Range.closedOpen(0.0, 25.0), new BigDecimal("9.99"));
        shippingCosts.put(Range.closedOpen(25.0, 50.0), new BigDecimal("4.99"));
        shippingCosts.put(Range.closedOpen(50.0, 100.0), new BigDecimal("2.99"));
        shippingCosts.put(Range.atLeast(100.0), BigDecimal.ZERO);
    }
    
    public BigDecimal getShippingCost(double orderTotal) {
        return shippingCosts.get(orderTotal);
    }
}

// 4. Age-Based Pricing
public class TicketPricingService {
    private final RangeMap<Integer, BigDecimal> ticketPrices = TreeRangeMap.create();
    
    public TicketPricingService() {
        ticketPrices.put(Range.closedOpen(0, 5), BigDecimal.ZERO);      // Free
        ticketPrices.put(Range.closedOpen(5, 18), new BigDecimal("10")); // Child
        ticketPrices.put(Range.closedOpen(18, 65), new BigDecimal("20")); // Adult
        ticketPrices.put(Range.atLeast(65), new BigDecimal("15"));       // Senior
    }
    
    public BigDecimal getTicketPrice(int age) {
        return ticketPrices.get(age);
    }
}

// 5. Date Range Booking System
public class HotelBookingService {
    private final RangeSet<LocalDate> bookedDates = TreeRangeSet.create();
    
    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut) {
        Range<LocalDate> requestedRange = Range.closedOpen(checkIn, checkOut);
        return !bookedDates.intersects(requestedRange);
    }
    
    public void book(LocalDate checkIn, LocalDate checkOut) {
        Range<LocalDate> bookingRange = Range.closedOpen(checkIn, checkOut);
        if (bookedDates.intersects(bookingRange)) {
            throw new IllegalStateException("Dates already booked");
        }
        bookedDates.add(bookingRange);
    }
    
    public Set<Range<LocalDate>> getAvailableRanges(LocalDate start, LocalDate end) {
        RangeSet<LocalDate> searchRange = TreeRangeSet.create();
        searchRange.add(Range.closed(start, end));
        searchRange.removeAll(bookedDates);
        return searchRange.asRanges();
    }
}
```

---

## Collection Utilities

### Lists Utility

```java
// 1. Partitioning for Batch Processing
public class BatchProcessor {
    public void processBatch(List<Order> orders) {
        int batchSize = 100;
        
        for (List<Order> batch : Lists.partition(orders, batchSize)) {
            processBatchInDatabase(batch);
        }
    }
    
    private void processBatchInDatabase(List<Order> batch) {
        // Database batch insert
    }
}

// 2. Reverse Iteration
public class AuditLogService {
    public List<AuditEntry> getRecentEntries(List<AuditEntry> allEntries, int count) {
        return Lists.reverse(allEntries).stream()
            .limit(count)
            .collect(Collectors.toList());
    }
}

// 3. Cartesian Product
public class ProductVariantGenerator {
    public List<List<String>> generateCombinations() {
        List<String> sizes = ImmutableList.of("S", "M", "L", "XL");
        List<String> colors = ImmutableList.of("Red", "Blue", "Green");
        
        return Lists.cartesianProduct(sizes, colors);
        // [[S, Red], [S, Blue], [S, Green], [M, Red], ...]
    }
}
```

---

### Sets Utility

```java
// 1. Set Operations for Access Control
public class PermissionService {
    public Set<String> getEffectivePermissions(
            Set<String> userPermissions,
            Set<String> rolePermissions) {
        return Sets.union(userPermissions, rolePermissions);
    }
    
    public Set<String> getRevokedPermissions(
            Set<String> previousPermissions,
            Set<String> currentPermissions) {
        return Sets.difference(previousPermissions, currentPermissions);
    }
    
    public Set<String> getCommonPermissions(
            Set<String> user1Permissions,
            Set<String> user2Permissions) {
        return Sets.intersection(user1Permissions, user2Permissions);
    }
}

// 2. Power Set for Combinations
public class MenuPlannerService {
    public Set<Set<String>> getAllMenuCombinations(Set<String> ingredients) {
        // Warning: power set grows exponentially (2^n)
        if (ingredients.size() > 10) {
            throw new IllegalArgumentException("Too many ingredients");
        }
        return Sets.powerSet(ingredients);
    }
}

// 3. Filtering with Predicate
public class ProductService {
    public Set<Product> getActiveProducts(Set<Product> allProducts) {
        return Sets.filter(allProducts, Product::isActive);
    }
}
```

---

### Maps Utility

```java
// 1. Map Difference for Change Detection
public class ConfigurationMonitor {
    public void detectChanges(
            Map<String, String> oldConfig,
            Map<String, String> newConfig) {
        
        MapDifference<String, String> diff = Maps.difference(oldConfig, newConfig);
        
        // Log changes
        diff.entriesOnlyOnLeft().forEach((key, value) -> 
            log.info("Removed: {} = {}", key, value));
        
        diff.entriesOnlyOnRight().forEach((key, value) -> 
            log.info("Added: {} = {}", key, value));
        
        diff.entriesDiffering().forEach((key, valueDiff) -> 
            log.info("Changed: {} from {} to {}", 
                key, valueDiff.leftValue(), valueDiff.rightValue()));
    }
}

// 2. Transform Map Values
public class CurrencyConverter {
    public Map<String, BigDecimal> convertPrices(
            Map<String, BigDecimal> pricesInUSD,
            BigDecimal exchangeRate) {
        
        return Maps.transformValues(pricesInUSD, 
            price -> price.multiply(exchangeRate));
    }
}

// 3. Filter Map Entries
public class ReportService {
    public Map<String, Double> getHighValueTransactions(
            Map<String, Double> allTransactions,
            double threshold) {
        
        return Maps.filterValues(allTransactions, 
            amount -> amount > threshold);
    }
}

// 4. Unique Index Creation
public class UserCache {
    private final Map<String, User> usersByEmail;
    
    public UserCache(List<User> users) {
        this.usersByEmail = Maps.uniqueIndex(users, User::getEmail);
    }
    
    public User findByEmail(String email) {
        return usersByEmail.get(email);
    }
}
```

---

### Multimaps Utility

```java
// 1. Index for Fast Lookups
public class ProductCatalog {
    private final Multimap<String, Product> productsByCategory;
    
    public ProductCatalog(List<Product> products) {
        this.productsByCategory = Multimaps.index(products, Product::getCategory);
    }
    
    public Collection<Product> getProductsByCategory(String category) {
        return productsByCategory.get(category);
    }
}

// 2. Filtering Multimap
public class NotificationService {
    private final Multimap<String, Notification> notifications;
    
    public Multimap<String, Notification> getUnreadNotifications() {
        return Multimaps.filterValues(notifications, 
            notif -> !notif.isRead());
    }
}
```

---

## Best Practices

### 1. Prefer Immutable Collections for APIs

```java
// ❌ BAD - Mutable return
public List<String> getUserRoles() {
    return userRoles; // Can be modified by caller!
}

// ✅ GOOD - Immutable return
public ImmutableList<String> getUserRoles() {
    return ImmutableList.copyOf(userRoles);
}
```

### 2. Use Builders for Complex Collections

```java
// ❌ BAD - Temporary mutable collection
public ImmutableList<Item> buildItems() {
    List<Item> temp = new ArrayList<>();
    temp.add(item1);
    temp.add(item2);
    temp.addAll(moreItems);
    return ImmutableList.copyOf(temp);
}

// ✅ GOOD - Use builder directly
public ImmutableList<Item> buildItems() {
    return ImmutableList.<Item>builder()
        .add(item1)
        .add(item2)
        .addAll(moreItems)
        .build();
}
```

### 3. Choose the Right Multimap Implementation

```java
// For preserving insertion order
Multimap<String, String> ordered = LinkedHashMultimap.create();

// For sorted values
Multimap<String, String> sorted = TreeMultimap.create();

// For allowing duplicate values
Multimap<String, String> duplicates = ArrayListMultimap.create();

// For unique values only
Multimap<String, String> unique = HashMultimap.create();
```

### 4. Use Collections2.filter() for Lazy Filtering

```java
// ✅ GOOD - Lazy evaluation, no intermediate collection
Collection<Product> activeProducts = 
    Collections2.filter(allProducts, Product::isActive);

// Only creates filtered collection when iterated
for (Product p : activeProducts) {
    // Process...
}
```

### 5. Multiset vs Map<E, Integer>

```java
// ❌ BAD - Manual count management
Map<String, Integer> wordCounts = new HashMap<>();
for (String word : words) {
    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
}

// ✅ GOOD - Use Multiset
Multiset<String> wordCounts = HashMultiset.create();
wordCounts.addAll(words);
```

### 6. BiMap for Bidirectional Lookups

```java
// ❌ BAD - Maintain two maps manually
Map<String, Integer> nameToId = new HashMap<>();
Map<Integer, String> idToName = new HashMap<>();

nameToId.put("Alice", 1);
idToName.put(1, "Alice"); // Error-prone!

// ✅ GOOD - Use BiMap
BiMap<String, Integer> nameIdMap = HashBiMap.create();
nameIdMap.put("Alice", 1);
Integer id = nameIdMap.get("Alice");
String name = nameIdMap.inverse().get(1);
```

### 7. RangeMap for Range-Based Logic

```java
// ❌ BAD - Multiple if-else statements
public BigDecimal getTaxRate(BigDecimal income) {
    if (income.compareTo(new BigDecimal("10000")) < 0) {
        return new BigDecimal("0.10");
    } else if (income.compareTo(new BigDecimal("50000")) < 0) {
        return new BigDecimal("0.20");
    } else {
        return new BigDecimal("0.30");
    }
}

// ✅ GOOD - Use RangeMap
private static final RangeMap<BigDecimal, BigDecimal> TAX_RATES;
static {
    RangeMap<BigDecimal, BigDecimal> map = TreeRangeMap.create();
    map.put(Range.lessThan(new BigDecimal("10000")), new BigDecimal("0.10"));
    map.put(Range.closedOpen(new BigDecimal("10000"), new BigDecimal("50000")), 
            new BigDecimal("0.20"));
    map.put(Range.atLeast(new BigDecimal("50000")), new BigDecimal("0.30"));
    TAX_RATES = map;
}

public BigDecimal getTaxRate(BigDecimal income) {
    return TAX_RATES.get(income);
}
```

### 8. Thread Safety Considerations

```java
// Immutable collections are always thread-safe
private final ImmutableList<String> config = ImmutableList.of("A", "B", "C");

// For concurrent modifications, use concurrent implementations
private final Multiset<String> concurrentCounts = ConcurrentHashMultiset.create();

// Or synchronize mutable collections
private final BiMap<String, Integer> syncMap = 
    Maps.synchronizedBiMap(HashBiMap.create());
```

### 9. Memory Efficiency

```java
// ✅ For small, known collections at compile time
ImmutableList<String> small = ImmutableList.of("A", "B", "C");

// ✅ For collections built dynamically
ImmutableList<String> dynamic = ImmutableList.copyOf(sourceList);

// ✅ For large collections with known size
ImmutableList.Builder<String> builder = 
    ImmutableList.builderWithExpectedSize(10000);
```

### 10. Avoid Common Pitfalls

```java
// ❌ BAD - BiMap doesn't allow duplicate values
BiMap<String, String> map = HashBiMap.create();
map.put("key1", "value");
map.put("key2", "value"); // Throws IllegalArgumentException!

// ✅ GOOD - Use forcePut if you want to overwrite
map.forcePut("key2", "value"); // Removes key1->value mapping

// ❌ BAD - Modifying source after creating immutable copy
List<String> source = new ArrayList<>(Arrays.asList("A", "B"));
ImmutableList<String> immutable = ImmutableList.copyOf(source);
source.clear(); // OK - immutable is unaffected

// But if elements are mutable...
List<User> users = new ArrayList<>();
users.add(new User("Alice"));
ImmutableList<User> immutableUsers = ImmutableList.copyOf(users);
users.get(0).setName("Bob"); // ⚠️ Affects immutableUsers!

// ✅ GOOD - Use immutable elements or deep copy
```

---

## Performance Characteristics

### Time Complexity

| Collection Type | Add | Remove | Contains | Get by Key |
|----------------|-----|--------|----------|------------|
| ImmutableList | N/A | N/A | O(n) | O(1) |
| ImmutableSet | N/A | N/A | O(1) | N/A |
| ImmutableMap | N/A | N/A | O(1) | O(1) |
| HashMultiset | O(1) | O(1) | O(1) | O(1) count |
| HashMultimap | O(1) | O(1) | O(1) | O(1) |
| BiMap | O(1) | O(1) | O(1) | O(1) both ways |
| Table | O(1) | O(1) | O(1) | O(1) |
| RangeSet | O(log n) | O(log n) | O(log n) | N/A |
| RangeMap | O(log n) | O(log n) | O(log n) | O(log n) |

### Space Complexity

- **ImmutableList**: More compact than ArrayList (no extra capacity)
- **ImmutableSet/Map**: More compact than HashSet/HashMap (optimized for read-only)
- **Multiset**: Similar to HashMap space usage
- **Multimap**: Space for keys + all values
- **BiMap**: 2× space of HashMap (maintains both directions)
- **Table**: Space for row map + column maps
- **RangeSet/RangeMap**: O(number of ranges), not size of domain

---

## Integration Examples

### Spring Boot Configuration

```java
@Configuration
public class GuavaConfig {
    
    @Bean
    public ImmutableMap<String, String> errorMessages() {
        return ImmutableMap.<String, String>builder()
            .put("USER_NOT_FOUND", "User does not exist")
            .put("INVALID_CREDENTIALS", "Invalid username or password")
            .put("ACCESS_DENIED", "You don't have permission")
            .build();
    }
    
    @Bean
    public RangeMap<Integer, String> httpStatusCategories() {
        RangeMap<Integer, String> categories = TreeRangeMap.create();
        categories.put(Range.closedOpen(200, 300), "Success");
        categories.put(Range.closedOpen(300, 400), "Redirection");
        categories.put(Range.closedOpen(400, 500), "Client Error");
        categories.put(Range.closedOpen(500, 600), "Server Error");
        return categories;
    }
}
```

### JPA Entity Collections

```java
@Entity
public class Order {
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
    
    // Return immutable copy to prevent external modification
    public ImmutableList<OrderItem> getItems() {
        return ImmutableList.copyOf(items);
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
```

### Jackson JSON Serialization

```java
@JsonSerialize
public class UserResponse {
    private final String username;
    private final ImmutableList<String> roles;
    private final ImmutableMap<String, String> metadata;
    
    public UserResponse(String username, 
                       Collection<String> roles,
                       Map<String, String> metadata) {
        this.username = username;
        this.roles = ImmutableList.copyOf(roles);
        this.metadata = ImmutableMap.copyOf(metadata);
    }
    
    // Jackson handles Guava collections automatically
    public String getUsername() { return username; }
    public ImmutableList<String> getRoles() { return roles; }
    public ImmutableMap<String, String> getMetadata() { return metadata; }
}
```

### Caching with Guava

```java
@Service
public class ProductService {
    private final LoadingCache<String, ImmutableList<Product>> categoryCache;
    
    public ProductService(ProductRepository repository) {
        this.categoryCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, ImmutableList<Product>>() {
                @Override
                public ImmutableList<Product> load(String category) {
                    return ImmutableList.copyOf(
                        repository.findByCategory(category)
                    );
                }
            });
    }
    
    public ImmutableList<Product> getProductsByCategory(String category) {
        return categoryCache.getUnchecked(category);
    }
}
```

---

## Migration Guide

### From JDK Collections to Guava

```java
// Before: JDK
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
List<String> unmodifiable = Collections.unmodifiableList(list);

// After: Guava
ImmutableList<String> immutable = ImmutableList.of("A", "B");

// Before: Map with List values
Map<String, List<String>> map = new HashMap<>();
map.computeIfAbsent("key", k -> new ArrayList<>()).add("value");

// After: Multimap
Multimap<String, String> multimap = ArrayListMultimap.create();
multimap.put("key", "value");

// Before: Counting occurrences
Map<String, Integer> counts = new HashMap<>();
for (String word : words) {
    counts.merge(word, 1, Integer::sum);
}

// After: Multiset
Multiset<String> counts = HashMultiset.create(words);
```

### Gradual Migration Strategy

1. **Start with new code**: Use Guava collections in all new classes
2. **Refactor APIs**: Change public APIs to return Guava immutable types
3. **Internal refactoring**: Convert internal data structures gradually
4. **Update tests**: Ensure tests work with both old and new collections

---

## Common Patterns

### Pattern 1: DTO with Immutable Collections

```java
@Value
@Builder
public class OrderDTO {
    String orderId;
    ImmutableList<OrderItemDTO> items;
    ImmutableMap<String, String> metadata;
    
    public static OrderDTO from(Order order) {
        return OrderDTO.builder()
            .orderId(order.getId())
            .items(order.getItems().stream()
                .map(OrderItemDTO::from)
                .collect(ImmutableList.toImmutableList()))
            .metadata(ImmutableMap.copyOf(order.getMetadata()))
            .build();
    }
}
```

### Pattern 2: Configuration Registry

```java
@Component
public class FeatureRegistry {
    private final ImmutableMap<String, Feature> features;
    
    public FeatureRegistry(List<Feature> featureList) {
        this.features = Maps.uniqueIndex(featureList, Feature::getName);
    }
    
    public Optional<Feature> getFeature(String name) {
        return Optional.ofNullable(features.get(name));
    }
    
    public ImmutableSet<String> getAllFeatureNames() {
        return features.keySet();
    }
}
```

### Pattern 3: Event Aggregation

```java
@Service
public class EventAggregator {
    private final Multimap<String, Event> eventsByType = 
        ArrayListMultimap.create();
    private final Multiset<String> eventCounts = 
        HashMultiset.create();
    
    public void recordEvent(Event event) {
        eventsByType.put(event.getType(), event);
        eventCounts.add(event.getType());
    }
    
    public Collection<Event> getEventsByType(String type) {
        return ImmutableList.copyOf(eventsByType.get(type));
    }
    
    public Map<String, Integer> getEventStatistics() {
        return eventCounts.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(
                Multiset.Entry::getElement,
                Multiset.Entry::getCount
            ));
    }
}
```

### Pattern 4: Multi-Level Index

```java
@Repository
public class ProductIndex {
    private final Table<String, String, Set<Product>> index = 
        HashBasedTable.create();
    
    public void indexProduct(Product product) {
        for (String tag : product.getTags()) {
            for (String category : product.getCategories()) {
                index.row(tag)
                     .computeIfAbsent(category, k -> new HashSet<>())
                     .add(product);
            }
        }
    }
    
    public Set<Product> findProducts(String tag, String category) {
        return index.contains(tag, category) 
            ? ImmutableSet.copyOf(index.get(tag, category))
            : ImmutableSet.of();
    }
}
```

---

## Testing with Guava Collections

```java
@Test
public void testImmutableCollections() {
    ImmutableList<String> list = ImmutableList.of("A", "B", "C");
    
    // Verify immutability
    assertThrows(UnsupportedOperationException.class, () -> {
        list.add("D");
    });
    
    // Test with AssertJ
    assertThat(list)
        .hasSize(3)
        .containsExactly("A", "B", "C");
}

@Test
public void testMultiset() {
    Multiset<String> multiset = HashMultiset.create();
    multiset.add("A", 3);
    multiset.add("B", 2);
    
    assertThat(multiset.count("A")).isEqualTo(3);
    assertThat(multiset.count("B")).isEqualTo(2);
    assertThat(multiset.elementSet()).containsExactlyInAnyOrder("A", "B");
}

@Test
public void testBiMap() {
    BiMap<String, Integer> biMap = HashBiMap.create();
    biMap.put("Alice", 1);
    biMap.put("Bob", 2);
    
    assertThat(biMap.get("Alice")).isEqualTo(1);
    assertThat(biMap.inverse().get(1)).isEqualTo("Alice");
}
```

---

## Maven Dependency

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>33.0.0-jre</version>
</dependency>
```

## Gradle Dependency

```gradle
implementation 'com.google.guava:guava:33.0.0-jre'
```

---

## Additional Resources

- **Official Wiki**: https://github.com/google/guava/wiki
- **API Documentation**: https://guava.dev/releases/snapshot-jre/api/docs/
- **Source Code**: https://github.com/google/guava

---

## Summary

Guava Collections provide:
- ✅ **True immutability** with defensive copying
- ✅ **Better performance** through optimized implementations
- ✅ **Cleaner APIs** with convenient factory methods and builders
- ✅ **New collection types** for common patterns (Multiset, Multimap, BiMap, Table)
- ✅ **Thread safety** by default for immutable collections
- ✅ **Powerful utilities** for transforming and filtering collections

Use Guava collections in production for:
- Public APIs and DTOs
- Configuration and constants
- Domain models with complex relationships
- Caching and indexing
- Any scenario requiring guaranteed immutability




--------------------------------------------------------------


Great question! Let's dive deep into creating instances of **Multiset** and **Multimap** with all the different ways and their use cases.

## Multiset - Creating Instances

### 1. **HashMultiset** (Most Common)

```java
// Empty multiset
Multiset<String> multiset = HashMultiset.create();

// With initial capacity
Multiset<String> multiset = HashMultiset.create(100);

// From existing collection
List<String> words = Arrays.asList("apple", "banana", "apple", "cherry");
Multiset<String> multiset = HashMultiset.create(words);

// Usage
multiset.add("apple");
multiset.add("apple", 5); // Add 5 occurrences
System.out.println(multiset.count("apple")); // 7
```

**When to use**: Default choice for most scenarios. Provides O(1) operations, no ordering guarantees.

### 2. **LinkedHashMultiset** (Insertion Order)

```java
// Maintains insertion order of elements
Multiset<String> multiset = LinkedHashMultiset.create();

multiset.add("zebra");
multiset.add("apple");
multiset.add("banana");

// Iteration order: zebra, apple, banana
for (String element : multiset.elementSet()) {
    System.out.println(element);
}

// From existing collection (preserves order)
Multiset<String> ordered = LinkedHashMultiset.create(
    Arrays.asList("first", "second", "first", "third")
);
```

**When to use**: When you need predictable iteration order (insertion order).

### 3. **TreeMultiset** (Sorted)

```java
// Natural ordering
Multiset<String> sorted = TreeMultiset.create();

sorted.add("zebra");
sorted.add("apple");
sorted.add("banana");

// Iteration order: apple, banana, zebra (alphabetical)
for (String element : sorted.elementSet()) {
    System.out.println(element);
}

// With custom comparator
Multiset<String> reverseOrder = TreeMultiset.create(
    Comparator.reverseOrder()
);

// From existing collection
Multiset<Integer> numbers = TreeMultiset.create(
    Arrays.asList(5, 2, 8, 2, 1)
);
// Sorted: 1, 2, 8
```

**When to use**: When you need sorted iteration, or operations like `first()`, `last()`, range queries.

**Advanced TreeMultiset operations**:
```java
TreeMultiset<Integer> scores = TreeMultiset.create();
scores.addAll(Arrays.asList(85, 90, 75, 90, 95, 85, 100));

// Get first and last
Integer lowest = scores.firstEntry().getElement();  // 75
Integer highest = scores.lastEntry().getElement(); // 100

// Range queries
Set<Integer> topScores = scores.subMultiset(
    90, 
    BoundType.CLOSED,  // Include 90
    100, 
    BoundType.CLOSED   // Include 100
).elementSet();  // [90, 95, 100]
```

### 4. **ConcurrentHashMultiset** (Thread-Safe)

```java
// Thread-safe multiset
Multiset<String> concurrent = ConcurrentHashMultiset.create();

// Safe for concurrent access
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 1000; i++) {
    executor.submit(() -> concurrent.add("item"));
}
executor.shutdown();
executor.awaitTermination(1, TimeUnit.MINUTES);

System.out.println(concurrent.count("item")); // 1000

// From existing collection
Multiset<String> threadSafe = ConcurrentHashMultiset.create(
    existingCollection
);
```

**When to use**: When multiple threads are adding/removing elements concurrently.

### 5. **ImmutableMultiset** (Immutable)

```java
// Using of() - up to 5 elements
ImmutableMultiset<String> small = ImmutableMultiset.of(
    "apple", "banana", "apple"
);

// Using copyOf()
List<String> source = Arrays.asList("x", "y", "x", "z");
ImmutableMultiset<String> immutable = ImmutableMultiset.copyOf(source);

// Using builder
ImmutableMultiset<String> built = ImmutableMultiset.<String>builder()
    .add("a")
    .add("b", 3)           // Add "b" three times
    .addAll(Arrays.asList("c", "c"))
    .build();

System.out.println(built.count("b")); // 3

// From another multiset (preserves counts)
Multiset<String> mutable = HashMultiset.create();
mutable.add("item", 5);
ImmutableMultiset<String> frozen = ImmutableMultiset.copyOf(mutable);
System.out.println(frozen.count("item")); // 5
```

**When to use**: When you need an immutable multiset (thread-safe, safe to share, no defensive copying needed).

### 6. **EnumMultiset** (For Enums)

```java
enum Status { PENDING, ACTIVE, COMPLETED, FAILED }

// Optimized for enum types
Multiset<Status> statusCounts = EnumMultiset.create(Status.class);

statusCounts.add(Status.PENDING, 5);
statusCounts.add(Status.ACTIVE, 10);
statusCounts.add(Status.COMPLETED, 3);

System.out.println(statusCounts.count(Status.ACTIVE)); // 10

// From existing collection
List<Status> statuses = Arrays.asList(
    Status.PENDING, Status.ACTIVE, Status.PENDING
);
Multiset<Status> counts = EnumMultiset.create(statuses);
```

**When to use**: When working exclusively with enum types. More memory-efficient than HashMultiset.

---

## Multimap - Creating Instances

### 1. **HashMultimap** (Set Values, No Duplicates)

```java
// Empty multimap with Set<V> values
Multimap<String, String> multimap = HashMultimap.create();

// With expected keys and values per key
Multimap<String, String> sized = HashMultimap.create(
    10,  // expected keys
    5    // expected values per key
);

// From existing multimap
Multimap<String, String> copy = HashMultimap.create(existingMultimap);

// Usage
multimap.put("user1", "ADMIN");
multimap.put("user1", "USER");
multimap.put("user1", "ADMIN"); // Duplicate - NOT added!

System.out.println(multimap.get("user1")); // [ADMIN, USER]
```

**When to use**: When you don't want duplicate values for the same key (like a `Map<K, Set<V>>`).

### 2. **LinkedHashMultimap** (Set Values, Insertion Order)

```java
// Maintains insertion order for both keys and values
Multimap<String, String> ordered = LinkedHashMultimap.create();

ordered.put("fruits", "apple");
ordered.put("fruits", "banana");
ordered.put("vegetables", "carrot");
ordered.put("fruits", "cherry");

// Keys iterate in insertion order: fruits, vegetables
// Values for each key also in insertion order
for (String key : ordered.keySet()) {
    System.out.println(key + ": " + ordered.get(key));
}
// Output:
// fruits: [apple, banana, cherry]
// vegetables: [carrot]

// With capacity hints
Multimap<String, String> sized = LinkedHashMultimap.create(10, 5);
```

**When to use**: When you need predictable iteration order for both keys and values, no duplicate values.

### 3. **ArrayListMultimap** (List Values, Allows Duplicates)

```java
// Values are stored in ArrayList (allows duplicates)
Multimap<String, String> multimap = ArrayListMultimap.create();

// With capacity hints
Multimap<String, String> sized = ArrayListMultimap.create(
    10,  // expected keys
    5    // expected values per key
);

multimap.put("user1", "ADMIN");
multimap.put("user1", "USER");
multimap.put("user1", "ADMIN"); // Duplicate IS added!

System.out.println(multimap.get("user1")); // [ADMIN, USER, ADMIN]

// Order is preserved
multimap.put("tags", "first");
multimap.put("tags", "second");
multimap.put("tags", "third");
System.out.println(multimap.get("tags")); // [first, second, third]
```

**When to use**: When you need duplicate values and/or care about value order (like a `Map<K, List<V>>`).

### 4. **LinkedListMultimap** (List Values, Special Properties)

```java
// Values stored in LinkedList
Multimap<String, String> multimap = LinkedListMultimap.create();

// With capacity hint
Multimap<String, Integer> sized = LinkedListMultimap.create(10);

multimap.put("queue", "first");
multimap.put("queue", "second");
multimap.put("queue", "third");

// Maintains insertion order, allows duplicates
System.out.println(multimap.get("queue")); // [first, second, third]

// Special: Keys iterate in order of FIRST insertion of each key
multimap.put("b", "value");
multimap.put("a", "value");
multimap.put("b", "another");
// Key order: b, a (b was inserted first)
```

**When to use**:
- When you need duplicates and ordering
- When you frequently add/remove from ends (LinkedList is efficient for this)
- When you need the key iteration order to reflect first insertion

### 5. **TreeMultimap** (Sorted Keys and Values)

```java
// Both keys and values are sorted
Multimap<String, Integer> sorted = TreeMultimap.create();

sorted.put("scores", 85);
sorted.put("scores", 90);
sorted.put("scores", 75);
sorted.put("ages", 25);
sorted.put("ages", 30);

// Keys are sorted: ages, scores
// Values are also sorted for each key
System.out.println(sorted.get("scores")); // [75, 85, 90]

// With custom comparators
Multimap<String, String> custom = TreeMultimap.create(
    String.CASE_INSENSITIVE_ORDER,  // Key comparator
    Comparator.reverseOrder()       // Value comparator
);

custom.put("Names", "Zebra");
custom.put("Names", "Apple");
custom.put("names", "Banana"); // Same key as "Names" (case-insensitive)

System.out.println(custom.get("Names")); // [Zebra, Banana, Apple] (reversed)

// Natural ordering
Multimap<Integer, String> natural = TreeMultimap.create();
```

**When to use**: When you need both keys and values sorted. No duplicate values per key.

### 6. **ImmutableMultimap** (Immutable)

```java
// Using of() - up to 5 entries
ImmutableMultimap<String, String> small = ImmutableMultimap.of(
    "key1", "value1",
    "key1", "value2",
    "key2", "value3"
);

// Using copyOf()
Multimap<String, String> mutable = HashMultimap.create();
mutable.put("a", "1");
mutable.put("a", "2");
ImmutableMultimap<String, String> immutable = ImmutableMultimap.copyOf(mutable);

// Using builder
ImmutableMultimap<String, Integer> built = ImmutableMultimap.<String, Integer>builder()
    .put("evens", 2)
    .put("evens", 4)
    .put("odds", 1)
    .put("odds", 3)
    .putAll("primes", Arrays.asList(2, 3, 5, 7))
    .build();

System.out.println(built.get("evens")); // [2, 4]

// ImmutableListMultimap - preserves order and duplicates
ImmutableListMultimap<String, String> listBased = 
    ImmutableListMultimap.<String, String>builder()
        .put("key", "first")
        .put("key", "second")
        .put("key", "first")  // Duplicate allowed
        .build();

// ImmutableSetMultimap - no duplicates
ImmutableSetMultimap<String, String> setBased = 
    ImmutableSetMultimap.<String, String>builder()
        .put("key", "first")
        .put("key", "second")
        .put("key", "first")  // Duplicate ignored
        .build();
```

**When to use**: For immutable multimaps (thread-safe, safe to share in APIs).

---

## Comparison Table

### Multiset Implementations

| Type | Ordering | Duplicates | Thread-Safe | Mutable | Use Case |
|------|----------|------------|-------------|---------|----------|
| **HashMultiset** | ❌ None | ✅ Yes | ❌ No | ✅ Yes | Default choice, fast operations |
| **LinkedHashMultiset** | ✅ Insertion | ✅ Yes | ❌ No | ✅ Yes | Need predictable order |
| **TreeMultiset** | ✅ Sorted | ✅ Yes | ❌ No | ✅ Yes | Need sorting, range queries |
| **ConcurrentHashMultiset** | ❌ None | ✅ Yes | ✅ Yes | ✅ Yes | Concurrent access |
| **ImmutableMultiset** | Varies | ✅ Yes | ✅ Yes | ❌ No | Immutable, thread-safe |
| **EnumMultiset** | ✅ Enum order | ✅ Yes | ❌ No | ✅ Yes | Enum types only |

### Multimap Implementations

| Type | Value Type | Duplicates | Key Order | Value Order | Use Case |
|------|------------|------------|-----------|-------------|----------|
| **HashMultimap** | Set | ❌ No | ❌ None | ❌ None | Unique values per key |
| **LinkedHashMultimap** | Set | ❌ No | ✅ Insertion | ✅ Insertion | Predictable order, unique values |
| **ArrayListMultimap** | List | ✅ Yes | ❌ None | ✅ Insertion | Duplicates allowed, ordered |
| **LinkedListMultimap** | List | ✅ Yes | ✅ Special* | ✅ Insertion | Duplicates, frequent add/remove |
| **TreeMultimap** | Set | ❌ No | ✅ Sorted | ✅ Sorted | Sorted keys and values |
| **ImmutableMultimap** | Varies | Varies | Varies | Varies | Immutable |

*LinkedListMultimap: Keys ordered by first insertion

---

## Real Production Examples

### Multiset Examples

```java
// 1. Counting Events
public class EventCounter {
    private final Multiset<String> eventCounts = ConcurrentHashMultiset.create();
    
    public void recordEvent(String eventType) {
        eventCounts.add(eventType);
    }
    
    public int getCount(String eventType) {
        return eventCounts.count(eventType);
    }
}

// 2. Word Frequency with Order
public class DocumentAnalyzer {
    public Multiset<String> analyzeWithOrder(String text) {
        Multiset<String> words = LinkedHashMultiset.create();
        Arrays.stream(text.split("\\s+"))
            .forEach(words::add);
        return words;
    }
}

// 3. Top-K Elements
public class TopKFinder {
    public List<String> findTopK(List<String> items, int k) {
        Multiset<String> counts = HashMultiset.create(items);
        
        return Multisets.copyHighestCountFirst(counts)
            .elementSet()
            .stream()
            .limit(k)
            .collect(Collectors.toList());
    }
}
```

### Multimap Examples

```java
// 1. User Permissions (no duplicates)
public class PermissionService {
    private final Multimap<String, String> userPermissions = HashMultimap.create();
    
    public void grantPermission(String userId, String permission) {
        userPermissions.put(userId, permission);
    }
    
    public Set<String> getPermissions(String userId) {
        return (Set<String>) userPermissions.get(userId);
    }
}

// 2. Event History (with duplicates and order)
public class EventHistory {
    private final Multimap<String, Event> history = ArrayListMultimap.create();
    
    public void recordEvent(String userId, Event event) {
        history.put(userId, event);
    }
    
    public List<Event> getHistory(String userId) {
        return (List<Event>) history.get(userId);
    }
}

// 3. Tag Index (sorted)
public class TagIndex {
    private final Multimap<String, String> tagToArticles = TreeMultimap.create();
    
    public void index(String tag, String articleId) {
        tagToArticles.put(tag, articleId);
    }
    
    public SortedSet<String> getArticles(String tag) {
        return (SortedSet<String>) tagToArticles.get(tag);
    }
}

// 4.MultiMap Builder Usage (Best Approach)

// creates a ListMultimap with tree keys and array list values
ListMultimap<String, Integer> treeListMultimap =
        MultimapBuilder.treeKeys().arrayListValues().build();

// creates a SetMultimap with hash keys and enum set values
SetMultimap<Integer, MyEnum> hashEnumMultimap =
        MultimapBuilder.hashKeys().enumSetValues(MyEnum.class).build();
```

The key is choosing the right implementation based on your needs for ordering, duplicates, and thread-safety!
