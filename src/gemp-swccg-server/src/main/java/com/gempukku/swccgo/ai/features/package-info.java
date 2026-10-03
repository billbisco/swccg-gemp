/**
 * PR-A slice for InformationSetV1 (design-learned-policy-v1, 2026-10-02).
 *
 * <p>Landed here:
 * <ul>
 *   <li>{@link com.gempukku.swccgo.ai.features.FeatureLayoutV1} — packed float32[128] index map</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetV1} — hybrid DTO (packed + bags)</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetTracker} — cap, aggregates, no exact opponent list</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetEncoder} — walks {@code GameState} for phase,
 *       LF, pile sizes, own hand, public in-play; packs float32[128]; remaining multiset estimate from prior</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetGameStateListener} — destiny + interrupt →
 *       seenHistory / destinyRecycleAggregate / opponentRevealed</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.DecklistMultisets} — own-seat prior + destiny hints</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.SeededShuffleTool} — fixed seed for tests; training mode is random</li>
 * </ul>
 *
 * <p>Headless FEATURES traces: {@code -Dheadless.traces=true -Dheadless.traceLevel=FEATURES}
 * seeds own deck prior per seat, attaches destiny/interrupt listeners, and embeds {@code state}
 * (InformationSetV1 bags + packed, plus {@code bagHash} from {@code LinearPolicyAi.bagHash})
 * on each decision line. {@code seededFromExactOpponentDeck} stays false.
 *
 * <p>Still missing (do not treat this package as a trained bot):
 * <ul>
 *   <li>Examine / sabacc / peek reveal hooks</li>
 *   <li>Own reserve shuffle notification</li>
 *   <li>Used/Lost identity scrape for tighter remaining-destiny estimates</li>
 *   <li>GEMP shuffle is not routed through {@link com.gempukku.swccgo.ai.features.SeededShuffleTool}</li>
 *   <li>No {@code LinearPolicyAi}, no self-play update, no gate, no champ promotion</li>
 * </ul>
 */
package com.gempukku.swccgo.ai.features;
