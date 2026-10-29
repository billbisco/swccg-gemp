package com.gempukku.swccgo.cards.set15.light;

/** Runs the established Sabotage gameplay suite through the remastered combination blueprint. */
public class ReflectionsGoldSabotageGameplayTests extends com.gempukku.swccgo.cards.set2.light.Card_2_056_Tests {
    @Override protected String sabotageBlueprint() { return "15_127"; }
    @Override @org.junit.Test public void SabotageStatsAndKeywordsAreCorrect() {
        new ReflectionsGoldLightParallelSecondStatsTests().SabotageAndSlightWeaponsMalfunctionBothUnique();
    }
}
