package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_054_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("wookiee", "1250_54"); put("other", "7_50"); put("rebel", "1_28"); put("leia", "1_17");
            put("bowcaster", "2_77"); put("binoculars", "1_35");
            put("forest", "1116_16"); put("cantina", "1_128"); put("db94", "1_129");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("snoova", "10_48"); put("emperor", "9_109"); put("lightning", "9_136"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WookieeMetadataMatchesFinalPrinted54EImage() {
        var scn = scenario(); var bp = scn.GetLSCard("wookiee").getBlueprint();
        assertEquals("Wookiee", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon); assertEquals(1, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Species.WOOKIEE, bp.getSpecies());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{
            add(Icon.ALIEN); add(Icon.SPECIAL_EDITION); add(Icon.WARRIOR); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    @Test
    public void WookieePaysFourToDeployOnKashyyykWithoutOriginalVirtualDiscount() {
        var scn = scenario(); scn.StartGame();
        var site = scn.GetLSCard("forest"); var wookiee = scn.GetLSCard("wookiee");
        scn.MoveLocationToTable(site); scn.MoveCardsToHand(wookiee);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(wookiee); scn.LSChooseCard(site); scn.PassAllResponses();
        assertEquals(site, wookiee.getAtLocation()); assertEquals(force - 4, scn.GetLSForcePileCount());
        assertEquals(4, scn.GetPower(wookiee));
    }

    @Test
    public void WookieeAloneCannotDrawBattleDestiny() { checkDestiny(false, false, false, false, 0); }
    @Test
    public void WookieeWithFriendlyWookieeDrawsOneBelowFourAbility() { checkDestiny(true, false, false, false, 3); }
    @Test
    public void WookieeWithOpponentsWookieeAlsoDrawsOne() { checkDestiny(false, true, false, false, 3); }
    @Test
    public void WookieeWithNonWookieeDoesNotGainBattleDestiny() { checkDestiny(false, false, true, false, 0); }
    @Test
    public void WookieeDoesNotCountAnotherWookieeAtDifferentSite() { checkDestiny(false, false, false, true, 0); }

    private void checkDestiny(boolean friendly, boolean enemy, boolean nonWookiee, boolean elsewhere, int total) {
        var scn = scenario(); scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("wookiee"), scn.GetDSCard("vader"));
        if (friendly) scn.MoveCardsToLocation(site, scn.GetLSCard("other"));
        if (enemy) scn.MoveCardsToLocation(site, scn.GetDSCard("snoova"));
        if (nonWookiee) scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"));
        if (elsewhere) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("other"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3); scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(total, scn.GetLSTotalDestiny());
    }

    @Test
    public void WookieeMovesAsReactToBattleAndPaysNormalMoveCost() {
        var scn = scenario(); scn.StartGame();
        var from = scn.GetLSCard("cantina"); var to = scn.GetLSCard("db94");
        var wookiee = scn.GetLSCard("wookiee");
        scn.MoveLocationToTable(from); scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, wookiee);
        scn.MoveCardsToLocation(to, scn.GetLSCard("rebel"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.LSActivateForceCheat(3);
        int force = scn.GetLSForcePileCount(); scn.DSInitiateBattle(to);
        assertTrue(scn.LSCardActionAvailable(wookiee, "react"));
        scn.LSUseCardAction(wookiee, "react"); scn.LSChooseCard(to); scn.PassAllResponses();
        assertEquals(to, wookiee.getAtLocation()); assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void WookieeDoesNotAddExtraDestinyWhenAlreadyAbleToDraw() {
        var scn = scenario(); scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("wookiee"), scn.GetLSCard("other"), scn.GetLSCard("leia"), scn.GetDSCard("vader"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.GetLSAbilityAtLocation(site) >= 4);
        scn.PrepareLSDestiny(3); scn.PrepareDSDestiny(1);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void WookieeMovesAsReactToForceDrainAndCancelsItByPresence() {
        var scn = scenario(); scn.StartGame();
        var from = scn.GetLSCard("cantina"); var to = scn.GetLSCard("db94");
        var wookiee = scn.GetLSCard("wookiee");
        scn.MoveLocationToTable(from); scn.MoveLocationToTable(to);
        scn.MoveCardsToLocation(from, wookiee); scn.MoveCardsToLocation(to, scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.LSActivateForceCheat(3);
        int force = scn.GetLSForcePileCount(); int life = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(to);
        assertTrue(scn.LSCardActionAvailable(wookiee, "react"));
        scn.LSUseCardAction(wookiee, "react"); scn.LSChooseCard(to); scn.PassAllResponses();
        assertEquals(to, wookiee.getAtLocation()); assertEquals(force - 1, scn.GetLSForcePileCount());
        assertFalse(scn.IsActiveForceDrain()); assertEquals(life, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void WookieeCanDrawBeforeActualNonForfeitLossFromForceLightning() {
        var scn = scenario(); scn.StartGame();
        var wookiee = scn.GetLSCard("wookiee"); var lightning = scn.GetDSCard("lightning");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), wookiee, scn.GetDSCard("emperor"));
        scn.MoveCardsToHand(lightning); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(3);
        int hand = scn.GetLSHandCount(); var top = scn.GetTopOfLSReserveDeck();
        scn.DSPlayCard(lightning, "Make target lost");
        if (scn.DSDecisionAvailable("Choose Emperor")) scn.DSChooseCard(scn.GetDSCard("emperor"));
        if (scn.DSDecisionAvailable("Choose character")) scn.DSChooseCard(wookiee);
        scn.PassDestinyDrawResponses();
        assertTrue(passToSaveAction(scn, wookiee));
        scn.LSUseCardAction(wookiee, "Save attached cards");
        assertTrue(scn.LSDecisionAvailable("Draw top card")); scn.LSChooseYes(); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(wookiee));
        assertEquals(hand + 1, scn.GetLSHandCount()); assertTrue(scn.GetLSHand().contains(top));
        assertTrue(scn.GetDSLostPile().contains(lightning));
    }

    @Test
    public void WookieeAboutToBeForfeitedSavesAllAttachedCardsAndDrawsOne() { checkForfeit(2, true); }
    @Test
    public void WookieeAboutToBeForfeitedMaySaveOnlyOneAttachmentAndDeclineDraw() { checkForfeit(1, false); }
    @Test
    public void WookieeAboutToBeForfeitedMayDrawWithoutSavingAnyAttachment() { checkForfeit(0, true); }

    @Test
    public void WookieeMaySaveAttachmentWithEmptyReserveWithoutOfferingDraw() {
        var scn = scenario(); scn.StartGame();
        var wookiee = scn.GetLSCard("wookiee"); var weapon = scn.GetLSCard("bowcaster");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), wookiee, scn.GetDSCard("vader"));
        scn.AttachCardsTo(wookiee, weapon);
        scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        scn.PrepareDSDestiny(3); scn.SkipToDamageSegment(true);
        for (var card : new ArrayList<>(scn.GetLSReserveDeck())) scn.MoveCardsToTopOfLSForcePile((PhysicalCardImpl) card);
        assertEquals(0, scn.GetLSReserveDeckCount());
        scn.LSChooseCard(wookiee); assertTrue(passToSaveAction(scn, wookiee));
        int hand = scn.GetLSHandCount();
        scn.LSUseCardAction(wookiee, "Save attached cards"); scn.LSChooseCard(weapon); scn.PassAllResponses();
        assertFalse(scn.LSDecisionAvailable("Draw top card"));
        assertEquals(hand, scn.GetLSHandCount());
        assertTrue(scn.GetLSUsedPile().contains(weapon)); assertTrue(scn.GetLSLostPile().contains(wookiee));
    }

    private boolean passToSaveAction(VirtualTableScenario scn, PhysicalCardImpl wookiee) {
        for (int i = 0; i < 32; i++) {
            if (scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                    && scn.LSCardActionAvailable(wookiee, "Save attached cards")) return true;
            if (scn.DSDecisionAvailable("optional")) scn.DSPass();
            else if (scn.LSDecisionAvailable("optional")) scn.LSPass();
            else return false;
        }
        return false;
    }

    @Test
    public void WookieeMayDrawBeforeForfeitWithoutAttachments() { checkForfeit(0, true, false); }

    @Test
    public void WookieeMayDeclineBothRescueAndDraw() { checkForfeit(0, false); }

    private void checkForfeit(int saved, boolean draw) { checkForfeit(saved, draw, true); }

    private void checkForfeit(int saved, boolean draw, boolean attached) {
        var scn = scenario(); scn.StartGame();
        var wookiee = scn.GetLSCard("wookiee"); var weapon = scn.GetLSCard("bowcaster");
        var device = scn.GetLSCard("binoculars");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), wookiee, scn.GetDSCard("vader"));
        if (attached) scn.AttachCardsTo(wookiee, weapon, device);
        scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        scn.PrepareDSDestiny(3); scn.SkipToDamageSegment(true);
        assertTrue(scn.LSDecisionAvailable("card from battle to forfeit"));
        scn.LSChooseCard(wookiee);
        assertTrue(passToSaveAction(scn, wookiee));
        var top = scn.GetTopOfLSReserveDeck(); int hand = scn.GetLSHandCount();
        scn.LSUseCardAction(wookiee, "Save attached cards");
        if (attached) {
            assertTrue(scn.LSDecisionAvailable("Choose cards on Wookiee"));
            if (saved == 0) scn.LSChooseCards();
            else if (saved == 1) scn.LSChooseCard(weapon);
            else scn.LSChooseCards(weapon, device);
            scn.PassAllResponses();
            if (saved == 2 && scn.LSDecisionAvailable("Choose card to place")) {
                scn.LSChooseCard(weapon); scn.PassAllResponses();
            }
        }
        assertTrue(scn.LSDecisionAvailable("Draw top card"));
        if (draw) scn.LSChooseYes(); else scn.LSChooseNo();
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card to put on Lost Pile")) {
            scn.LSChooseCard(wookiee); scn.PassAllResponses();
        }
        if (scn.LSDecisionAvailable("Choose card to put on Lost Pile")) {
            scn.LSChooseCard(weapon); scn.PassAllResponses();
        }
        assertTrue(scn.GetLSLostPile().contains(wookiee));
        assertEquals(hand + (draw ? 1 : 0), scn.GetLSHandCount());
        assertEquals(draw, scn.GetLSHand().contains(top));
        if (attached) {
            assertEquals(saved > 0, scn.GetLSUsedPile().contains(weapon));
            assertEquals(saved > 1, scn.GetLSUsedPile().contains(device));
            assertEquals(saved == 0, scn.GetLSLostPile().contains(weapon));
            assertEquals(saved < 2, scn.GetLSLostPile().contains(device));
        }
    }
}
