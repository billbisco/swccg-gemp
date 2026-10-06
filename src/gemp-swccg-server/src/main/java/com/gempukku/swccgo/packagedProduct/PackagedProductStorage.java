package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackagedProductStorage {
    private Map<String, PackagedCardProduct> _packagedProducts = new HashMap<String, PackagedCardProduct>();

    public void addPackagedProduct(String productName, PackagedCardProduct packagedProduct) {
        _packagedProducts.put(productName, packagedProduct);
    }

    public List<CardCollection.Item> openPackagedProduct(String productName) {
        PackagedCardProduct packagedProduct = _packagedProducts.get(productName);
        if (packagedProduct == null)
            return null;
        return packagedProduct.openPackage();
    }

    public List<CardCollection.Item> openPackagedProductWithExclusions(String productName, List<String> exclusions) {
        PackagedCardProduct packagedProduct = _packagedProducts.get(productName);
        if (packagedProduct == null)
            return null;
        return packagedProduct.openPackageWithExclusions(exclusions);
    }

    /**
     * How many auto-openable card-yielding packs this product represents.
     * A booster is 1. A 60-pack box is 60. Choice packs are 0.
     * Does not roll random boosters; only peeks deterministic containers (boxes, sealed kits).
     */
    public int countCardYieldingPacks(String productName) {
        return countCardYieldingPacks(productName, new HashSet<String>());
    }

    private int countCardYieldingPacks(String productName, Set<String> visiting) {
        if (productName == null || productName.startsWith("(S)"))
            return 0;
        if (!visiting.add(productName))
            return 0;
        PackagedCardProduct product = _packagedProducts.get(productName);
        if (product == null || !isPackContainer(product))
            return 1;
        List<CardCollection.Item> items = product.openPackage();
        if (items == null)
            return 1;
        int inner = 0;
        boolean hasCards = false;
        for (CardCollection.Item item : items) {
            if (item.getType() == CardCollection.Item.Type.CARD)
                hasCards = true;
            else if (item.getType() == CardCollection.Item.Type.PACK)
                inner += item.getCount() * countCardYieldingPacks(item.getBlueprintId(), visiting);
        }
        int total = (hasCards ? 1 : 0) + inner;
        return total > 0 ? total : 1;
    }

    public boolean isPackContainer(String productName) {
        PackagedCardProduct product = _packagedProducts.get(productName);
        return product != null && isPackContainer(product);
    }

    private boolean isPackContainer(PackagedCardProduct product) {
        String className = product.getClass().getSimpleName();
        return className.endsWith("BoosterBox")
                || className.endsWith("AnthologyBox")
                || className.endsWith("SealedDeck")
                || className.endsWith("TwoPlayerGameBox");
    }
}
