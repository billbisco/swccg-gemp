package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;

import java.util.HashSet;
import java.util.Set;

/**
 * Opens every auto-openable pack in a collection in memory, including nested boxes,
 * up to a card-yielding pack cap. Choice packs (S) stay closed.
 */
public final class OpenAllPacks {
    public static final int DEFAULT_CAP = 240;

    private OpenAllPacks() {
    }

    public static final class Result {
        public final int opened;
        public final int remaining;

        public Result(int opened, int remaining) {
            this.opened = opened;
            this.remaining = remaining;
        }
    }

    public static Result open(MutableCardCollection collection, PackagedProductStorage storage, int cap) {
        if (collection == null || storage == null)
            return new Result(0, 0);
        if (cap < 0)
            cap = 0;

        int budget = cap;
        int opened = 0;
        Set<String> skip = new HashSet<String>();
        while (budget > 0) {
            String packId = nextOpenablePack(collection, skip, storage);
            if (packId == null)
                break;
            CardCollection contents = collection.openPack(packId, null, storage);
            if (contents == null) {
                skip.add(packId);
                continue;
            }
            if (yieldsCards(contents)) {
                budget--;
                opened++;
            }
        }
        int remaining = countRemaining(collection, storage);
        return new Result(opened, remaining);
    }

    public static int countRemaining(CardCollection collection, PackagedProductStorage storage) {
        if (collection == null || storage == null)
            return 0;
        int remaining = 0;
        for (CardCollection.Item item : collection.getAll().values()) {
            if (item.getType() != CardCollection.Item.Type.PACK)
                continue;
            String packId = item.getBlueprintId();
            if (packId == null || packId.startsWith("(S)"))
                continue;
            remaining += item.getCount() * storage.countCardYieldingPacks(packId);
        }
        return remaining;
    }

    private static String nextOpenablePack(CardCollection collection, Set<String> skip,
                                           PackagedProductStorage storage) {
        String containerId = null;
        for (CardCollection.Item item : collection.getAll().values()) {
            if (item.getType() != CardCollection.Item.Type.PACK)
                continue;
            String packId = item.getBlueprintId();
            if (packId == null || packId.startsWith("(S)") || skip.contains(packId) || item.getCount() <= 0)
                continue;
            if (storage.isPackContainer(packId)) {
                if (containerId == null)
                    containerId = packId;
            } else {
                return packId;
            }
        }
        return containerId;
    }

    private static boolean yieldsCards(CardCollection contents) {
        if (contents == null)
            return false;
        for (CardCollection.Item item : contents.getAll().values()) {
            if (item.getType() == CardCollection.Item.Type.CARD)
                return true;
        }
        return false;
    }
}
