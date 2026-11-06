package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_097_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("uhoh", "1230_97"); put("figrin", "1_9"); put("neb", "7_34"); put("nalan", "2_13");
            put("luke", "1_19"); put("clakdor", "2_60"); put("xwing", "1_146");
            put("hut", "1_134"); put("lost1", "1_30"); put("lost2", "1_31"); put("lost3", "1_32");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("opponentShip", "1_303"); put("tech", "2_105"); }},
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void UhOhStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var card = scn.GetLSCard("uhoh").getBlueprint();
        assertEquals("Uh-oh!", card.getTitle()); assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(4, card.getDestiny(), scn.epsilon); assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_3); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void UhOhDeploysOnTableForFree() {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSHand(scn.GetLSCard("uhoh")); scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSPlayCard(scn.GetLSCard("uhoh")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("uhoh")); assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void UhOhControlledClakdorAddsTwoToYourMusiciansGlobally() {
        var scn = BonusesReady();
        assertEquals(3, scn.GetPower(scn.GetLSCard("figrin")));
        assertEquals(6, scn.GetForfeit(scn.GetLSCard("figrin")));
        assertEquals(3, scn.GetPower(scn.GetLSCard("luke")));
        assertEquals(7, scn.GetForfeit(scn.GetLSCard("luke")));
        assertEquals(1, scn.GetPower(scn.GetDSCard("tech")));
        scn.MoveCardsToLocation(scn.GetLSCard("clakdor"), scn.GetDSCard("opponentShip"));
        assertEquals(1, scn.GetPower(scn.GetLSCard("figrin")));
        assertEquals(4, scn.GetForfeit(scn.GetLSCard("figrin")));
    }

    @Test
    public void UhOhOccupationWithoutClakdorControlDoesNotGrantBonuses() {
        var scn = BonusesReady();
        scn.MoveCardsToLocation(scn.GetLSCard("clakdor"), scn.GetDSCard("opponentShip"));
        assertEquals(1, scn.GetPower(scn.GetLSCard("figrin")));
        assertEquals(4, scn.GetForfeit(scn.GetLSCard("figrin")));
    }

    private VirtualTableScenario BonusesReady() {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("uhoh")); scn.MoveLocationToTable(scn.GetLSCard("clakdor"));
        scn.MoveCardsToLocation(scn.GetLSCard("clakdor"), scn.GetLSCard("xwing"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("figrin"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tech"));
        return scn;
    }

    @Test
    public void UhOhDownloadsClakdorDuringControlPhaseForOneForce() {
        var scn = SearchReady(1);
        scn.LSUseCardAction(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"); scn.PassAllResponses();
        AssertDownloadSelectable(scn, "clakdor", true);
        AssertDownloadSelectable(scn, "figrin", false);
        AssertDownloadSelectable(scn, "neb", false);
        AssertDownloadSelectable(scn, "luke", false);
        scn.LSChooseCard(scn.GetLSCard("clakdor")); scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, scn.GetLSCard("clakdor")); assertEquals(0, scn.GetLSForcePileCount());
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    @Test
    public void UhOhDownloadsMusicianForOnePlusNormalDeployCost() {
        var scn = SearchReady(3); DownloadCharacter(scn, "figrin");
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void UhOhDownloadsNebDuloDespiteNotBeingMusician() {
        var scn = SearchReady(4); assertFalse(scn.GetLSCard("neb").getBlueprint().hasKeyword(Keyword.MUSICIAN));
        DownloadCharacter(scn, "neb"); assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void UhOhCannotDownloadWithoutOneForce() {
        var scn = SearchReady(0);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"));
    }

    @Test
    public void UhOhDownloadIsOncePerTurnAndResetsOnOpponentsTurn() {
        var scn = SearchReady(8); DownloadCharacter(scn, "figrin");
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass();
        assertTrue(scn.GetLSForcePileCount() >= 4);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"));
        DownloadCharacter(scn, "neb");
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"));
    }

    private VirtualTableScenario SearchReady(int force) {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("uhoh"));
        for (String key : new String[]{"figrin", "neb", "clakdor", "luke"}) scn.MoveOutOfPlay(scn.GetLSCard(key));
        scn.LSChooseAction("Activate Force");
        int activated = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax()); scn.LSDecided(activated);
        if (scn.DSDecisionAvailable("Choose amount of Force to allow opponent")) scn.DSDecided(activated);
        scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        scn.PassAllResponses(); scn.PassActivateActions(); scn.PassAllResponses();
        for (String key : new String[]{"figrin", "neb", "clakdor", "luke"}) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard(key));
        assertEquals(force, scn.GetLSForcePileCount()); assertTrue(scn.AwaitingLSControlPhaseActions());
        return scn;
    }

    private void DownloadCharacter(VirtualTableScenario scn, String key) {
        scn.LSUseCardAction(scn.GetLSCard("uhoh"), "Deploy card from Reserve Deck"); scn.PassAllResponses();
        AssertDownloadSelectable(scn, key, true); AssertDownloadSelectable(scn, "luke", false);
        scn.LSChooseCard(scn.GetLSCard(key)); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard(key));
    }

    private void AssertDownloadSelectable(VirtualTableScenario scn, String key, boolean expected) {
        var ids = scn.LSGetADParamAsList("blueprintId"); int index = ids.indexOf(scn.GetLSCard(key).getBlueprintId(true));
        assertTrue("Expected displayed card: " + key, index >= 0);
        assertEquals(key, expected, Boolean.parseBoolean(scn.LSGetADParam("selectable")[index]));
    }

    @Test
    public void UhOhRetrievesOnePerControlledBattlegroundSiteWithMusician() {
        var scn = RetrievalReady();
        int lost = scn.GetLSLostPileCount(); int used = scn.GetLSUsedPileCount();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve 2 Force"));
        scn.LSUseCardAction(scn.GetLSCard("uhoh"), "Retrieve 2 Force"); scn.PassAllResponses();
        assertEquals(lost - 2, scn.GetLSLostPileCount()); assertEquals(used + 2, scn.GetLSUsedPileCount());
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve 2 Force"));
    }

    @Test
    public void UhOhRetrievalDoesNotCountContestedOrNonBattlegroundSites() {
        var scn = RetrievalReady(true, false);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve 1 Force"));
        scn = RetrievalReady(true, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve"));
    }

    @Test
    public void UhOhRetrievalCountsSitesRatherThanNumberOfMusicians() {
        var scn = RetrievalReady(false, false, true);
        int lost = scn.GetLSLostPileCount(); int used = scn.GetLSUsedPileCount();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve 1 Force"));
        scn.LSUseCardAction(scn.GetLSCard("uhoh"), "Retrieve 1 Force"); scn.PassAllResponses();
        assertEquals(lost - 1, scn.GetLSLostPileCount()); assertEquals(used + 1, scn.GetLSUsedPileCount());
    }

    @Test
    public void UhOhRetrievalRequiresYourControlPhase() {
        var scn = RetrievalReady(); scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("uhoh"), "Retrieve"));
    }

    private VirtualTableScenario RetrievalReady() { return RetrievalReady(false, false); }

    private VirtualTableScenario RetrievalReady(boolean contested, boolean nonBattleground) {
        return RetrievalReady(contested, nonBattleground, false);
    }

    private VirtualTableScenario RetrievalReady(boolean contested, boolean nonBattleground, boolean together) {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("uhoh"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("figrin"));
        scn.MoveCardsToLocation(together ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation(), scn.GetLSCard("nalan"));
        if (contested) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        if (nonBattleground) {
            scn.MoveLocationToTable(scn.GetLSCard("hut"));
            scn.MoveCardsToLocation(scn.GetLSCard("hut"), scn.GetLSCard("figrin"));
        }
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("lost1"), scn.GetLSCard("lost2"), scn.GetLSCard("lost3"));
        scn.SkipToPhase(Phase.CONTROL); return scn;
    }
}
