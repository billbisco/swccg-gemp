/**
 * PR-A slice for InformationSetV1 (design-learned-policy-v1, 2026-10-02).
 *
 * <p>Landed here:
 * <ul>
 *   <li>{@link com.gempukku.swccgo.ai.features.FeatureLayoutV1} — packed float32[128] index map</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetV1} — hybrid DTO (packed + bags)</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetTracker} — cap, aggregates, no exact opponent list</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.InformationSetEncoder} — packs a DTO; does not read {@code GameState}</li>
 *   <li>{@link com.gempukku.swccgo.ai.features.SeededShuffleTool} — fixed seed for tests; training mode is random</li>
 * </ul>
 *
 * <p>Not landed (do not treat this package as a trained bot or as FEATURES logging):
 * <ul>
 *   <li>No {@code GameState} walk (hand, piles, in-play, destiny listeners)</li>
 *   <li>{@code HeadlessDecisionTraceWriter} still writes compact rows only; no traceLevel=FEATURES</li>
 *   <li>GEMP shuffle is not routed through {@link com.gempukku.swccgo.ai.features.SeededShuffleTool}</li>
 *   <li>No {@code LinearPolicyAi}, no self-play update, no gate, no champ promotion</li>
 * </ul>
 */
package com.gempukku.swccgo.ai.features;
