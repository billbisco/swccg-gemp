package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1230_106_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("mishap", "1230_106"); put("han", "1_11"); put("armor", "1107_7");
        }}, new HashMap<>() {{
            put("boba", "5_91"); put("jabba", "6_109"); put("trooper", "1_194");
            put("dengar", "4_100"); put("influence", "7_231"); put("rifle", "1_312"); put("otherRifle", "1_312");
            put("hutt", "221_21"); put("ket", "1_221"); put("slavers", "601_20"); put("slave", "5_177"); put("system", "1_282");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void MandalorianMishapStatsIconsKeywordsAndHostLoreAreCorrect() {
        var s = scenario(); var c = s.GetLSCard("mishap").getBlueprint();
        assertEquals("Mandalorian Mishap", c.getTitle()); assertTrue(c.hasVirtualSuffix());
        assertEquals(Side.LIGHT, c.getSide()); assertEquals(Uniqueness.UNIQUE, c.getUniqueness());
        assertEquals(5, c.getDestiny(), s.epsilon); assertEquals(CardSubtype.USED, c.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, c.getExpansionSet()); assertEquals(Rarity.V, c.getRarity());
        assertEquals("Most space-faring adventurers meet their fate with a calm, quiet dignity.", c.getLore());
        s.BlueprintCardTypeCheck(c, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        s.BlueprintIconCheck(c, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.JABBAS_PALACE); add(Icon.LEGACY_BLOCK_3); }});
        s.BlueprintKeywordCheck(c, new ArrayList<Keyword>());
    }

    private VirtualTableScenario ready(String... characters) {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        for (String k : new String[]{"boba", "jabba", "trooper", "dengar", "influence", "rifle", "otherRifle", "hutt", "ket", "slavers", "slave", "system"}) s.MoveOutOfPlay(s.GetDSCard(k));
        s.MoveOutOfPlay(s.GetLSCard("armor"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("han"));
        for (String k : characters) s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard(k));
        s.SkipToLSTurn(Phase.CONTROL); return s;
    }

    private void playSingle(VirtualTableScenario s, String target) {
        s.LSPlayCard(s.GetLSCard("mishap"), "Target a bounty hunter or gangster");
        if (s.LSHasCardChoiceAvailable(s.GetDSCard(target))) s.LSChooseCard(s.GetDSCard(target));
        s.PassAllResponses(); assertInZone(Zone.USED_PILE, s.GetLSCard("mishap"));
    }

    private void assertStopped(VirtualTableScenario s, String target) {
        assertTrue(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(), s.GetDSCard(target)));
        assertFalse(s.game().getModifiersQuerying().notProhibitedFromFiringWeapons(s.gameState(), s.GetDSCard(target)));
    }

    @Test
    public void MandalorianMishapMayTargetOnlyABountyHunter() {
        var s = ready("boba", "trooper"); playSingle(s, "boba"); assertStopped(s, "boba");
        assertFalse(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(), s.GetDSCard("trooper")));
        assertTrue(s.game().getModifiersQuerying().notProhibitedFromFiringWeapons(s.gameState(), s.GetDSCard("trooper")));
    }

    @Test
    public void MandalorianMishapMayTargetOnlyAGangster() {
        var s = ready("jabba"); playSingle(s, "jabba"); assertStopped(s, "jabba");
    }

    @Test
    public void MandalorianMishapMayTargetABountyHunterAndAGangsterTogether() {
        var s = ready("boba", "jabba", "trooper");
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter and gangster"));
        s.LSPlayCard(s.GetLSCard("mishap"), "Target a bounty hunter and gangster");
        if (s.LSHasCardChoiceAvailable(s.GetDSCard("boba"))) s.LSChooseCard(s.GetDSCard("boba"));
        if (s.LSHasCardChoiceAvailable(s.GetDSCard("jabba"))) s.LSChooseCard(s.GetDSCard("jabba"));
        s.PassAllResponses(); assertStopped(s, "boba"); assertStopped(s, "jabba");
        assertFalse(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(), s.GetDSCard("trooper")));
        assertInZone(Zone.USED_PILE, s.GetLSCard("mishap"));
    }

    @Test
    public void MandalorianMishapCannotPairCharactersAtDifferentSites() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("boba"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("jabba"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter and gangster"));
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter or gangster"));
    }

    @Test
    public void MandalorianMishapCannotUseTwoBountyHuntersAsThePair() {
        var s = ready("boba", "dengar");
        assertTrue(com.gempukku.swccgo.filters.Filters.bounty_hunter.accepts(s.game(), s.GetDSCard("dengar")));
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter and gangster"));
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter or gangster"));
    }

    @Test
    public void MandalorianMishapCannotTargetAnUnrelatedCharacter() {
        var s = ready("trooper");
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter"));
    }

    private void nextTurn(VirtualTableScenario s) {
        for (int i = 0; i < 40 && s.gameState().getCurrentPlayerId().equals(s.LS); i++) {
            if (s.GetCurrentDecision().getText().toLowerCase().contains("required responses")) {
                if (s.LSAnyDecisionsAvailable()) s.LSChooseAction(s.LSGetADParam("actionText")[0]);
                else s.DSChooseAction(s.DSGetADParam("actionText")[0]);
            } else if (s.LSAnyDecisionsAvailable()) s.LSPass(); else s.DSPass();
        }
        s.SkipToPhase(Phase.CONTROL); assertEquals(s.DS, s.gameState().getCurrentPlayerId());
    }

    @Test
    public void MandalorianMishapTargetRestrictionsExpireAtEndOfTurn() {
        var s = ready("boba"); playSingle(s, "boba"); assertStopped(s, "boba"); nextTurn(s);
        assertFalse(s.game().getModifiersQuerying().isGameTextCanceled(s.gameState(), s.GetDSCard("boba")));
        assertTrue(s.game().getModifiersQuerying().notProhibitedFromFiringWeapons(s.gameState(), s.GetDSCard("boba")));
    }

    @Test
    public void MandalorianMishapRequiresAnEffectContainingGangsterForSuspensionMode() {
        var s = ready("boba");
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Suspend Effects"));
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter"));
    }

    @Test
    public void MandalorianMishapSuspendsAGangsterEffectButNotANonEffectWithThatWord() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("boba"), s.GetLSCard("han"));
        s.AttachCardsTo(s.GetDSCard("boba"), s.GetDSCard("influence"));
        s.AttachCardsTo(s.GetLSCard("han"), s.GetLSCard("armor")); s.SkipToLSTurn(Phase.CONTROL);
        assertFalse(s.GetDSCard("influence").isSuspended());
        s.LSPlayCard(s.GetLSCard("mishap"), "Suspend Effects"); s.PassAllResponses();
        assertTrue(s.GetDSCard("influence").isSuspended()); assertFalse(s.GetLSCard("armor").isSuspended());
        assertInZone(Zone.USED_PILE, s.GetLSCard("mishap")); nextTurn(s);
        assertFalse(s.GetDSCard("influence").isSuspended());
    }

    @Test
    public void MandalorianMishapStopsTheTargetsActualWeaponActionWhileAnotherWarriorMayFire() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("han"), s.GetDSCard("boba"), s.GetDSCard("trooper"));
        s.AttachCardsTo(s.GetDSCard("boba"), s.GetDSCard("rifle"));
        s.AttachCardsTo(s.GetDSCard("trooper"), s.GetDSCard("otherRifle")); s.SkipToLSTurn(Phase.CONTROL);
        playSingle(s, "boba"); s.SkipToPhase(Phase.BATTLE); assertStopped(s, "boba");
        s.LSInitiateBattle(s.GetLSStartingLocation()); s.PassAllResponses(); s.LSPass();
        assertTrue(s.AwaitingDSWeaponsSegmentActions());
        assertFalse(s.DSCardActionAvailable(s.GetDSCard("rifle"), "Fire"));
        assertTrue(s.DSCardActionAvailable(s.GetDSCard("otherRifle"), "Fire"));
    }
    @Test
    public void MandalorianMishapCannotTargetABountyHunterAboardAStarshipAtASystem() {
        var s = ready(); s.MoveLocationToTable(s.GetDSCard("system"));
        s.MoveCardsToLocation(s.GetDSCard("system"), s.GetDSCard("slave"));
        s.BoardAsPilot(s.GetDSCard("slave"), s.GetDSCard("boba"));
        s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("mishap"), "Target a bounty hunter"));
    }

    @Test
    public void MandalorianMishapSuspendsAllSingularAndPluralMatchesButNotLoreOnlyMatches() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("boba"), s.GetDSCard("jabba"));
        s.AttachCardsTo(s.GetDSCard("boba"), s.GetDSCard("influence"));
        s.AttachCardsTo(s.GetDSCard("jabba"), s.GetDSCard("ket"));
        s.MoveCardsToDSSideOfTable(s.GetDSCard("hutt"));
        s.AttachCardsTo(s.GetLSStartingLocation(), s.GetDSCard("slavers"));
        s.SkipToLSTurn(Phase.CONTROL);
        assertEquals(9, s.GetForfeit(s.GetDSCard("jabba")), s.epsilon);
        s.LSPlayCard(s.GetLSCard("mishap"), "Suspend Effects"); s.PassAllResponses();
        assertEquals(7, s.GetForfeit(s.GetDSCard("jabba")), s.epsilon);
        for (String key : new String[]{"influence", "hutt", "slavers"}) assertTrue(s.GetDSCard(key).isSuspended());
        assertFalse(s.GetDSCard("ket").isSuspended()); nextTurn(s);
        assertEquals(9, s.GetForfeit(s.GetDSCard("jabba")), s.epsilon);
        for (String key : new String[]{"influence", "hutt", "slavers"}) assertFalse(s.GetDSCard(key).isSuspended());
    }

    @Test
    public void MandalorianMishapAlsoSuspendsAMatchingEffectDeployedLaterThatTurn() {
        var s = scenario(); s.StartGame(); s.MoveCardsToLSHand(s.GetLSCard("mishap"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("boba"), s.GetDSCard("jabba"), s.GetLSCard("han"));
        s.AttachCardsTo(s.GetDSCard("boba"), s.GetDSCard("influence"));
        s.MoveCardsToDSHand(s.GetDSCard("slavers")); s.SkipToDSTurn(Phase.DEPLOY); s.DSPass();
        s.LSPlayCard(s.GetLSCard("mishap"), "Suspend Effects"); s.PassAllResponses();
        s.DSPlayCard(s.GetDSCard("slavers"));
        if (s.DSHasCardChoiceAvailable(s.GetLSStartingLocation())) s.DSChooseCard(s.GetLSStartingLocation());
        s.PassAllResponses(); assertTrue(s.GetDSCard("slavers").isSuspended());
        assertTrue(s.GetDSCard("influence").isSuspended());
    }

}
