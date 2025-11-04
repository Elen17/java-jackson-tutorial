package org.example.guava_demo;

import com.google.common.collect.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuavaTester {

    public static void main(String args[]) {

        // Multiset An extension to Set interface to allow duplicate elements.
        // create a multiset collection

        // how Multiset actually stored =>  Map<E, Count> backingMap
        Multiset<String> multiset = HashMultiset.create();

        multiset.add("A", 12);
        // adding "B" 10 times; will return 0; as "B" is not present
        System.out.println(multiset.setCount("B", 10));

        multiset.add("a");
        multiset.add("b");
        multiset.add("c");
        multiset.add("d");
        multiset.add("a");
        multiset.add("b");
        multiset.add("c");
        multiset.add("b");
        multiset.add("b");
        multiset.add("b");
        multiset.add(null);

        multiset.setCount("a", 5);


        // If the multiset contains fewer than this number of occurrences to begin with, all occurrences will be removed.
        // will return 10; number of actually removed elements from multiset
        System.out.println(multiset.remove("B", 50));

        //print the occurrence of an element
        // Collections.frequency(multiset, "b");
        System.out.println("Occurrence of 'b' : " + multiset.count("b"));

        //print the total size of the multiset
        System.out.println("Total Size : " + multiset.size());

        System.out.println(multiset);



        // Tree Multiset An extension to Multiset interface to allow duplicate elements in sorted order.
        TreeMultiset<Integer> scores = TreeMultiset.create();
        scores.addAll(Arrays.asList(85, 90, 75, 90, 95, 85, 100));

// Get first and last
        Integer lowest = scores.firstEntry().getElement();  // 75
        Integer highest = scores.lastEntry().getElement(); // 100

// Range queries
        SortedMultiset<Integer> topScores = scores.subMultiset(
                90,
                BoundType.CLOSED,  // Include 90
                100,
                BoundType.CLOSED   // Include 100
        );  // [90, 95, 100]

        System.out.println("Count of students with scores between 90 and 100 : " + topScores.size()); // [90, 90, 95, 100]
        System.out.println("Count of top scores : " + topScores.elementSet().size()); // [90, 95, 100]


        // MultiMap; An extension to Map interface so that its keys can be mapped to multiple values at a time.
        // Multimap -> Map where multiple values can be mapped to the same key (Values are stored in a Collection).

        // how MultiMap actually stored => Map<K, Collection<V>> map;

        Multimap<String, String> multimap = createMultimap();

        List<String> lower = (List<String>) multimap.get("lower");
        // this will modify the multiMap values
        lower.add("w");
        lower.remove("a");

//        {lower=[b, c, d, e, w], upper=[A, B, C, D]}
        // a - removed from lower list
        // w - added to lower list

        System.out.println(multimap);
        // [lower x 5, upper x 4]
        System.out.println("Multimap Keys (MultiSet is used => occurrence tracked): "  + multimap.keys());
        // [lower, upper]
        System.out.println("Multimap keySet (only unique keys, no info about occurrence): "  + multimap.keySet());

        System.out.println("Lower : " + multimap.get("lower"));
        System.out.println("Upper : " + multimap.get("upper"));


        // BiMap -> An extension to Map interface to support inverse operations.

        BiMap<Integer, String> empIDNameMap = HashBiMap.create();

        empIDNameMap.put(101, "Mahesh");

        // java.lang.IllegalArgumentException: value already present: Mahesh
        // both key and value should be unique
        //❌ If the value already exists (mapped to a different key) — it throws an IllegalArgumentException.
        // empIDNameMap.put(104, "Mahesh");


        // To bypass that restriction — use forcePut()
        // this will not fail; as value is already associated with a key (101), so that entry will be deleted
        // and new entry will be added to the map (104, "Mahesh")
        empIDNameMap.forcePut(104, "Mahesh");
        //System.out.println(biMap); // {Bob=1}

        empIDNameMap.put(102, "Sohan");
        empIDNameMap.put(103, "Ramesh");

        //Emp Id of Employee "Mahesh"
        // getting value's key
        System.out.println(empIDNameMap);
        System.out.println(empIDNameMap.inverse().get("Mahesh"));




        //Table<R,C,V> == Map<R,Map<C,V>>
        /*
         *  Company: IBM, Microsoft, TCS
         *  IBM 		-> {101:Mahesh, 102:Ramesh, 103:Suresh}
         *  Microsoft 	-> {101:Sohan, 102:Mohan, 103:Rohan }
         *  TCS 		-> {101:Ram, 102: Shyam, 103: Sunil }
         *
         * */

        // ArrayListMultiMap vs LinkedListMultiMap

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

        System.out.println(userOrders.entries()); // [1=ORD-1001, 1=ORD-1003, 1=ORD-1001, 2=ORD-1002] - insertion order is saved only per key (full insertion order is not saved)

        Multimap<String, String> params = parseParams("tag=java&menu=main&tag=backend&tag=java");

        System.out.println(params.entries()); // [tag=java, menu=main, tag=backend, tag=java] - insertion order is saved (full insertion order is not saved, not only per key)

        // create a table

        Table<String, String, String> employeeTable = HashBasedTable.create();

        //initialize the table with employee details
        employeeTable.put("IBM", "101","Mahesh");
        employeeTable.put("IBM", "102","Ramesh");
        employeeTable.put("IBM", "103","Suresh");

        employeeTable.put("Microsoft", "111","Sohan");
        employeeTable.put("Microsoft", "112","Mohan");
        employeeTable.put("Microsoft", "113","Rohan");

        employeeTable.put("TCS", "121","Ram");
        employeeTable.put("TCS", "122","Shyam");
        employeeTable.put("TCS", "123","Sunil");

        //get Map corresponding to IBM
        Map<String,String> ibmEmployees =  employeeTable.row("IBM");

        System.out.println("List of IBM Employees");

        for(Map.Entry<String, String> entry : ibmEmployees.entrySet()) {
            System.out.println("Emp Id: " + entry.getKey() + ", Name: " + entry.getValue());
        }

    }

    private static Multimap<String, String> createMultimap() {
//        Multimap<String, String> multimap = HashMultimap.create();
        Multimap<String, String> multimap = ArrayListMultimap.create();
        ArrayListMultimap<String, String> listMultimap = ArrayListMultimap.create();


        multimap.put("lower", "a");
        multimap.put("lower", "b");
        multimap.put("lower", "c");
        multimap.put("lower", "d");
        multimap.put("lower", "e");

        multimap.put("upper", "A");
        multimap.put("upper", "B");
        multimap.put("upper", "C");
        multimap.put("upper", "D");


        return multimap;
    }

    public static Multimap<String, String> parseParams(String query) {
        Multimap<String, String> params = LinkedListMultimap.create();

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            params.put(parts[0], parts.length > 1 ? parts[1] : "");
        }

        return params;
    }


}
