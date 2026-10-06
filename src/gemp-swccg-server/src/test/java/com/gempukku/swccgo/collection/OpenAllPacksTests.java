package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.packagedProduct.PackagedCardProduct;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpenAllPacksTests {

    @Test
    public void OpensAllBoostersUnderCap() {
        PackagedProductStorage storage = boosterStorage();
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Test Booster", 5);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 240);

        assertEquals(5, result.opened);
        assertEquals(0, result.remaining);
        assertEquals(0, collection.getItemCount("Test Booster"));
        assertEquals(5, collection.getItemCount("1_1"));
        assertEquals(5, collection.getItemCount("1_2"));
    }

    @Test
    public void StopsAtCapAndLeavesRemaining() {
        PackagedProductStorage storage = boosterStorage();
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Test Booster", 10);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 3);

        assertEquals(3, result.opened);
        assertEquals(7, result.remaining);
        assertEquals(7, collection.getItemCount("Test Booster"));
        assertEquals(3, collection.getItemCount("1_1"));
    }

    @Test
    public void UnwrapsBoxAndCountsInnerPacksTowardCap() {
        PackagedProductStorage storage = boxStorage(60);
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Test Booster Box", 2);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 240);

        assertEquals(120, result.opened);
        assertEquals(0, result.remaining);
        assertEquals(0, collection.getItemCount("Test Booster Box"));
        assertEquals(0, collection.getItemCount("Test Booster"));
        assertEquals(120, collection.getItemCount("1_1"));
    }

    @Test
    public void FiveBoxesStopAtFourBoxesWorth() {
        PackagedProductStorage storage = boxStorage(60);
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Test Booster Box", 5);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, OpenAllPacks.DEFAULT_CAP);

        assertEquals(240, result.opened);
        assertEquals(60, result.remaining);
        assertEquals(1, collection.getItemCount("Test Booster Box"));
        assertEquals(0, collection.getItemCount("Test Booster"));
        assertEquals(240, collection.getItemCount("1_1"));
    }

    @Test
    public void SelectionPacksStayClosed() {
        PackagedProductStorage storage = boosterStorage();
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("(S)Choice Pack", 2);
        collection.addItem("Test Booster", 1);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 240);

        assertEquals(1, result.opened);
        assertEquals(0, result.remaining);
        assertEquals(2, collection.getItemCount("(S)Choice Pack"));
        assertEquals(1, collection.getItemCount("1_1"));
    }

    @Test
    public void UnknownProductIsSkippedWithoutLooping() {
        PackagedProductStorage storage = boosterStorage();
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Missing Product", 3);
        collection.addItem("Test Booster", 1);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 240);

        assertEquals(1, result.opened);
        assertTrue(result.remaining >= 3);
        assertEquals(3, collection.getItemCount("Missing Product"));
        assertEquals(0, collection.getItemCount("Test Booster"));
    }

    @Test
    public void SealedKitChargesItselfAndInnerPacks() {
        PackagedProductStorage storage = sealedStorage();
        DefaultCardCollection collection = new DefaultCardCollection();
        collection.addItem("Test Sealed Deck", 1);

        OpenAllPacks.Result result = OpenAllPacks.open(collection, storage, 240);

        assertEquals(6, result.opened);
        assertEquals(0, result.remaining);
        assertEquals(0, collection.getItemCount("Test Sealed Deck"));
        assertEquals(0, collection.getItemCount("Test Booster"));
        assertEquals(1, collection.getItemCount("9_1"));
        assertEquals(5, collection.getItemCount("1_1"));
    }

    private static PackagedProductStorage boosterStorage() {
        PackagedProductStorage storage = new PackagedProductStorage();
        storage.addPackagedProduct("Test Booster", new TestBooster());
        return storage;
    }

    private static PackagedProductStorage boxStorage(int packs) {
        PackagedProductStorage storage = boosterStorage();
        storage.addPackagedProduct("Test Booster Box", new TestBoosterBox(packs));
        return storage;
    }

    private static PackagedProductStorage sealedStorage() {
        PackagedProductStorage storage = boosterStorage();
        storage.addPackagedProduct("Test Sealed Deck", new TestSealedDeck());
        return storage;
    }

    private static class TestBooster implements PackagedCardProduct {
        @Override
        public String getProductName() {
            return "Test Booster";
        }

        @Override
        public float getProductPrice() {
            return 0;
        }

        @Override
        public List<CardCollection.Item> openPackage() {
            List<CardCollection.Item> items = new ArrayList<CardCollection.Item>();
            items.add(CardCollection.Item.createItem("1_1", 1));
            items.add(CardCollection.Item.createItem("1_2", 1));
            return items;
        }

        @Override
        public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
            return openPackage();
        }
    }

    private static class TestBoosterBox implements PackagedCardProduct {
        private final int _packs;

        private TestBoosterBox(int packs) {
            _packs = packs;
        }

        @Override
        public String getProductName() {
            return "Test Booster Box";
        }

        @Override
        public float getProductPrice() {
            return 0;
        }

        @Override
        public List<CardCollection.Item> openPackage() {
            return Collections.singletonList(CardCollection.Item.createItem("Test Booster", _packs));
        }

        @Override
        public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
            return openPackage();
        }
    }

    private static class TestSealedDeck implements PackagedCardProduct {
        @Override
        public String getProductName() {
            return "Test Sealed Deck";
        }

        @Override
        public float getProductPrice() {
            return 0;
        }

        @Override
        public List<CardCollection.Item> openPackage() {
            List<CardCollection.Item> items = new ArrayList<CardCollection.Item>();
            items.add(CardCollection.Item.createItem("Test Booster", 5));
            items.add(CardCollection.Item.createItem("9_1", 1));
            return items;
        }

        @Override
        public List<CardCollection.Item> openPackageWithExclusions(List<String> exclusions) {
            return openPackage();
        }
    }
}
