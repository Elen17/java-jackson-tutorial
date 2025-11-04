package org.example.guava_demo;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class RangeSetDemo {
    public static void main(String[] args) {
        // !!! RangeSets work with Comparable objects
        //  creating a mutable range set

        /*
          Overlapping → merged.
          Touching → merged.
          Disjoint (non-touching) → kept separate.
         */


        ///  Ranges while adding are merged, only if they are overlap
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10)); // [1, 10]
        rangeSet.add(Range.closedOpen(20, 30)); // merging with already existing range => {[1, 10], [20, 30)}
        rangeSet.remove(Range.closed(5, 25)); // removing range [5, 15] => {[1, 5), (26, 30)}

        System.out.println(rangeSet); // [[1..5), (25..30)]


        RangeSet<Integer> set = TreeRangeSet.create();
        set.add(Range.closed(0, 2));
        set.add(Range.closed(2, 5));  // They TOUCH at 2
        System.out.println(set);      // [0‥5] => as they share both 2

        // creating an immutable range set
        RangeSet<Integer> immutableRangeSet
//                = ImmutableRangeSet.<Integer>builder().add(Range.closed(0, 2)).build();
         = ImmutableRangeSet.<Integer>copyOf(rangeSet);

        rangeSet.remove(Range.closed(0, 2));
        System.out.println("immutableRangeSet: " + immutableRangeSet); // [[1..5), (25..30)] -> not mutated after removal
        System.out.println("rangeSet: " + rangeSet); // [(2..5), (25..30)]

        try {
            immutableRangeSet.add(Range.closed(2, 4));
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
        }
        System.out.println(immutableRangeSet); // [[0, 2]]


        // Non-Overlapping Ranges
        //
        //If you try to create or add a range that overlaps with an existing one,
        //Guava will throw an IllegalArgumentException.
        ImmutableRangeSet<Integer> regularImmutableRangeSet = ImmutableRangeSet.<Integer>builder()
                .add(Range.closed(1, 5))
                .add(Range.closed(10, 15))
                .build(); // [1..5], [10..15]

        // ❌ This will throw IllegalArgumentException:
        try {
            ImmutableRangeSet.<Integer>builder()
                    .add(Range.closed(1, 5))
                    .add(Range.closed(3, 8)) // Overlaps with [1‥5]
                    .build();

            ImmutableRangeSet.<Integer>builder()
                    .add(Range.closed(1, 5))
                    .add(Range.closed(5, 10)); // ❌ IllegalArgumentException — they touch!


            /**
             * 🧩 Why This Design?
             *
             * Because the goal of RangeSet is to provide:
             *
             * Unambiguous containment (contains(value) always gives a single, deterministic result)
             *
             * Efficient queries — since ranges are guaranteed disjoint and sorted, lookups are logarithmic.
             *
             * No merging ambiguity — you never have to decide how to merge overlapping intervals.
             */
        } catch (IllegalArgumentException e) {
            System.out.println(e); // Ranges should not cross
        }

        // ranges can disjoint
        ImmutableRangeSet<Integer> disjoint = ImmutableRangeSet.<Integer>builder()
                .add(Range.closed(1, 5))
                .add(Range.closed(6, 9)) // disjoint
                .add(Range.closed(15, 20))
                .build();

        System.out.println(disjoint); // [[1..5], [6..9], [15..20]]


        RangeSet<Integer> numberRangeSet = TreeRangeSet.create();

        numberRangeSet.add(Range.closed(0, 2));
        numberRangeSet.add(Range.closed(3, 5));
        numberRangeSet.add(Range.closed(6, 8)); // [0‥2], [3‥5], [6‥8] -> the ranges do not overlap and are not connected, that's why they are not connected to [0, 8]

       // complement(дополнение) - the set off all elements that are not in the original

        RangeSet<Integer> numberRangeComplementSet
                = numberRangeSet.complement(); // numberRangeSet: [0‥2], [3‥5], [6‥8] -> complement: [(-∞..0), (2..3), (5..6), (8..+∞)]

        System.out.printf("numberRangeSet: %s\n", numberRangeSet);
        System.out.printf("numberRangeComplementSet: %s\n", numberRangeComplementSet);

//        assertTrue(numberRangeComplementSet.contains(-1000));
//        assertFalse(numberRangeComplementSet.contains(2));
//        assertFalse(numberRangeComplementSet.contains(3));
//        assertTrue(numberRangeComplementSet.contains(1000));


    }
}
