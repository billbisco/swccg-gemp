package com.gempukku.swccgo.game;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The server shares one card library between request handlers, game threads, deck validation and the bots,
 * and the library loads blueprints lazily. These tests load a fresh library from many threads at once.
 */
public class SwccgCardBlueprintLibraryConcurrencyTests {
    private static final int THREADS = 16;
    private static final int ROUNDS = 5;

    private static List<String> idsToLoad() {
        List<String> ids = new ArrayList<>();
        for (int set = 1; set <= 7; set++)
            for (int n = 1; n <= 324; n++)
                ids.add(set + "_" + n);
        return ids;
    }

    /** Every thread asks for every id, in its own order, all starting at the same moment. */
    private static List<Map<String, SwccgCardBlueprint>> loadFromManyThreads(SwccgCardBlueprintLibrary library, List<String> ids) throws Exception {
        CyclicBarrier go = new CyclicBarrier(THREADS);
        List<Map<String, SwccgCardBlueprint>> seen = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        try {
            List<Future<?>> results = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                int seed = t;
                results.add(executor.submit(() -> {
                    List<String> mine = new ArrayList<>(ids);
                    Collections.shuffle(mine, new Random(seed));
                    Map<String, SwccgCardBlueprint> got = new HashMap<>();
                    go.await();
                    for (String id : mine)
                        got.put(id, library.getSwccgoCardBlueprint(id));
                    seen.add(got);
                    return null;
                }));
            }
            for (Future<?> result : results)
                result.get(2, TimeUnit.MINUTES);
        } finally {
            executor.shutdownNow();
        }
        return seen;
    }

    @Test
    public void CardLibraryBuildsEachBlueprintOnceWhenManyThreadsLoadIt() throws Exception {
        List<String> ids = idsToLoad();
        int differentObjects = 0;
        List<String> examples = new ArrayList<>();
        for (int round = 0; round < ROUNDS; round++) {
            SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
            List<Map<String, SwccgCardBlueprint>> seen = loadFromManyThreads(library, ids);
            assertEquals(THREADS, seen.size());

            //test1: every thread got the very same blueprint object for each id, and so does a later caller
            for (String id : ids) {
                SwccgCardBlueprint later = library.getSwccgoCardBlueprint(id);
                for (Map<String, SwccgCardBlueprint> got : seen) {
                    if (got.get(id) != later) {
                        differentObjects++;
                        if (examples.size() < 5)
                            examples.add(id);
                        break;
                    }
                }
            }
        }
        assertEquals("card ids that came back as different blueprint objects, e.g. " + examples, 0, differentObjects);
    }

    @Test
    public void CardLibraryStillAnswersTheSameAfterConcurrentLoading() throws Exception {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        List<String> ids = idsToLoad();
        ids.add("1_999");
        ids.add("1_1*^");
        loadFromManyThreads(library, ids);

        //test1: a real card loads, and a foil or alternate-image id is the same blueprint
        SwccgCardBlueprint first = library.getSwccgoCardBlueprint("1_1");
        assertNotNull(first);
        assertSame(first, library.getSwccgoCardBlueprint("1_1*"));
        assertSame(first, library.getSwccgoCardBlueprint("1_1^"));

        //test2: an id with no card is null, every time
        assertNull(library.getSwccgoCardBlueprint("1_999"));
        assertNull(library.getSwccgoCardBlueprint("1_999"));

        //test3: more than one suffix is still refused
        assertNull(library.getSwccgoCardBlueprint("1_1*^"));

        //test4: the alternate-id mapping read at startup is intact (501_27 is a reprint of 1272_11)
        assertEquals("1272_11", library.getBaseBlueprintId("501_27"));
        assertTrue(library.getAllAlternates("1272_11").contains("501_27"));
        assertNotNull(library.getSwccgoCardBlueprint("501_27"));
    }

    @Test
    public void CardLibraryCanNameEveryBlueprintItLoadedFromManyThreads() throws Exception {
        List<String> ids = idsToLoad();
        int unnamed = 0;
        int misnamed = 0;
        List<String> examples = new ArrayList<>();
        for (int round = 0; round < ROUNDS; round++) {
            SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
            List<Map<String, SwccgCardBlueprint>> seen = loadFromManyThreads(library, ids);

            //test1: every blueprint any thread was handed can be named again, which is what a save needs
            //test2: and it is named by the id it was loaded under
            for (Map<String, SwccgCardBlueprint> got : seen) {
                for (Map.Entry<String, SwccgCardBlueprint> entry : got.entrySet()) {
                    if (entry.getValue() == null)
                        continue;
                    String name = library.getLoadedBlueprintId(entry.getValue());
                    if (name == null)
                        unnamed++;
                    else if (!name.equals(entry.getKey()))
                        misnamed++;
                    else
                        continue;
                    if (examples.size() < 5)
                        examples.add(entry.getKey() + "->" + name);
                }
            }
        }
        assertEquals("blueprints with no id, e.g. " + examples, 0, unnamed);
        assertEquals("blueprints named by the wrong id, e.g. " + examples, 0, misnamed);
    }
}
