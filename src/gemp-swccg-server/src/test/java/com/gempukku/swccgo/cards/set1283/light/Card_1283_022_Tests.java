package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("oon", "1283_22");
                    put("han", "1_11");
                    put("boshek", "1_4");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("bluffs", "2_150");
                    put("sense", "1_267");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void OutOfNowhereVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("oon").getBlueprint();

        assertEquals("Out Of Nowhere", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WithoutOutOfNowhereYouMayNotDeployToASiteWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToLSHand(han);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(han);
        assertFalse(scn.LSHasCardChoiceAvailable(bluffs));
    }

    @Test
    public void OutOfNowhereUsesTwoForceToLetYouDeployToTargetWithoutPresenceOrForceIcons() {
        var scn = GetScenario();
        var oon = scn.GetLSCard("oon");
        var han = scn.GetLSCard("han");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToLSHand(oon, han);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(8);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(oon));
        scn.LSPlayCard(oon);
        scn.LSChooseCard(bluffs);
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertInZone(Zone.LOST_PILE, oon);
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }

        scn.LSDeployCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(bluffs));
        scn.LSChooseCard(bluffs);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(bluffs, han));
    }

    @Test
    public void OutOfNowhereTargetingIsImmuneToSense() {
        var scn = GetScenario();
        var oon = scn.GetLSCard("oon");
        var bluffs = scn.GetDSCard("bluffs");
        var sense = scn.GetDSCard("sense");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToLSHand(oon);
        scn.MoveCardsToDSHand(sense);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(oon);
        scn.LSChooseCard(bluffs);
        if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
            scn.LSPass();
        }
        assertTrue(scn.DSAnyDecisionsAvailable());
        assertFalse(scn.DSCardPlayAvailable(sense));
    }

    @Test
    public void OutOfNowhereTakesAnyCardFromUsedPileAfterNonJediDeploysWhereOpponentOccupies() {
        var scn = GetScenario();
        var oon = scn.GetLSCard("oon");
        var han = scn.GetLSCard("han");
        var boshek = scn.GetLSCard("boshek");
        var trooper = scn.GetDSCard("trooper");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(marketplace, trooper);
        scn.MoveCardsToLSHand(oon, han);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSUsedPile(boshek);
        scn.LSDeployCard(han);
        scn.LSChooseCard(marketplace);
        assertTrue(usedPileOptionOffered(scn, oon));
        scn.LSPlayCard(oon, "Used Pile");
        scn.PassAllResponses();
        scn.LSChooseCard(boshek);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, boshek);
    }

    @Test
    public void OutOfNowhereDoesNotTakeFromUsedPileWhenDeployingWhereOpponentIsAbsent() {
        var scn = GetScenario();
        var oon = scn.GetLSCard("oon");
        var han = scn.GetLSCard("han");
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(oon, han);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSUsedPile(boshek);
        scn.LSDeployCard(han);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        assertFalse(usedPileOptionOffered(scn, oon));
    }

    /**
     * Passes Dark Side's responses to the just-completed deployment until Light Side may respond, then reports whether
     * the Used Pile option of Out Of Nowhere (V) is playable.
     */
    private static boolean usedPileOptionOffered(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl oon) {
        for (int i = 0; i < 8; i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                if (scn.LSCardPlayAvailable(oon, "Used Pile")) {
                    return true;
                }
                if (scn.GetCurrentPhase() == Phase.DEPLOY && scn.AwaitingLSDeployPhaseActions()) {
                    return false;
                }
                scn.LSPass();
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            } else {
                return false;
            }
        }
        return false;
    }
}
