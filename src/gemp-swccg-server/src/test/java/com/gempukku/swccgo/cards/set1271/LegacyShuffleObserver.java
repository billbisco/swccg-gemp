package com.gempukku.swccgo.cards.set1271;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Observes completed game shuffles without adding actions or synthesizing results. */
public final class LegacyShuffleObserver extends AbstractActionProxy {
    private final Set<ShufflingResult> shuffles = new HashSet<>();

    public static LegacyShuffleObserver observe(VirtualTableScenario scenario) {
        LegacyShuffleObserver observer = new LegacyShuffleObserver();
        scenario.ApplyAdHocAction(observer);
        return observer;
    }

    @Override
    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result) {
        if (result instanceof ShufflingResult)
            shuffles.add((ShufflingResult) result);
        return null;
    }

    public boolean sawOwnedShuffle(String playerId, Zone pile) {
        return shuffles.stream().anyMatch(result -> playerId.equals(result.getPlayerId())
                && playerId.equals(result.getZoneOwner()) && result.getCardPile() == pile);
    }
}
