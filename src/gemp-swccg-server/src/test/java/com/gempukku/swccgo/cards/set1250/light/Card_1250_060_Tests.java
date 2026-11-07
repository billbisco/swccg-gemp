package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_060_Tests {
    private static final String SEARCH = "Take an A Jedi's card into hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("fear", "1250_60"); put("chamber", "12_76"); put("hut", "4_89");
            put("patience", "11_28"); put("resilience", "11_29"); put("fury", "221_43");
            put("other", "1_109"); put("decoy", "1_60");
        }}, new HashMap<>() {{
            put("wipe", "1109_50"); put("sidious", "217_24"); put("other", "12_127");
            put("alter", "1_234"); put("vader", "1_168");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalUnusualAmountOfFearHasExactMetadata() {
        var s = scenario(); var bp = s.GetLSCard("fear").getBlueprint();
        assertEquals("An Unusual Amount Of Fear", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(0, bp.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("The peacekeepers of the galaxy are not to be taken lightly.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private VirtualTableScenario deploy(String destination, boolean sidiousHere) {
        var s = scenario(); s.StartGame();
        var chamber = s.GetLSCard("chamber"); var hut = s.GetLSCard("hut"); var fear = s.GetLSCard("fear");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(hut);
        var site = s.GetLSCard(destination);
        s.AttachCardsTo(site, s.GetDSCard("other"));
        s.AttachCardsTo(s.GetDSStartingLocation(), s.GetDSCard("wipe"));
        // Exercise the location predicate using a real Sidious Effect as a site fixture.
        s.AttachCardsTo(sidiousHere ? site : s.GetDSStartingLocation(), s.GetDSCard("sidious"));
        s.MoveCardsToHand(fear); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.LSDeployAvailable(fear)); s.LSDeployCard(fear);
        s.PassAllResponses();
        assertTrue(s.LSHasCardChoicesAvailable(chamber)); assertTrue(s.LSHasCardChoicesAvailable(hut));
        assertFalse(s.LSHasCardChoicesAvailable(s.GetLSStartingLocation()));
        s.LSChooseCard(site); s.PassAllResponses();
        assertEquals(site, fear.getAttachedTo());
        return s;
    }

    @Test
    public void ChamberDeploymentCancelsTheSidiousEffectHereButNotOtherEffects() {
        var s = deploy("chamber", true);
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("sidious")));
        assertFalse(s.GetDSLostPile().contains(s.GetDSCard("other")));
        assertFalse(s.GetDSLostPile().contains(s.GetDSCard("wipe")));
    }

    @Test
    public void YodasHutDeploymentDoesNotCancelTheSameSidiousEffectElsewhere() {
        var s = deploy("hut", false);
        assertEquals(s.GetDSStartingLocation(), s.GetDSCard("sidious").getAttachedTo());
        assertFalse(s.GetDSLostPile().contains(s.GetDSCard("wipe")));
    }

    private boolean searchOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("fear"), SEARCH);
    }

    private VirtualTableScenario searchSetup(boolean fund) {
        var s = scenario(); s.StartGame();
        var chamber = s.GetLSCard("chamber"); s.MoveLocationToTable(chamber);
        s.AttachCardsTo(chamber, s.GetLSCard("fear"));
        s.SkipToLSTurn(Phase.CONTROL);
        if (fund && s.GetLSForcePileCount() < 3) {
            s.LSActivateForceCheat(3 - s.GetLSForcePileCount());
        }
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("other"), s.GetLSCard("patience"), s.GetLSCard("fury"),
                s.GetLSCard("resilience"));
        if (!fund) s.LSUseForceCheat(s.GetLSForcePileCount());
        s.SkipToPhase(Phase.MOVE);
        return s;
    }

    private void selectable(VirtualTableScenario s, String card, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(card).getBlueprintId(true));
        assertTrue(card, index >= 0);
        assertEquals(card, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    @Test
    public void PaidSearchTakesOneEligibleCardIntoHandReshufflesAndExcludesResilience() {
        var s = searchSetup(true); var marker = s.GetLSCard("resilience");
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        int initial = s.GetLSForcePileCount(); assertTrue(searchOffered(s));
        s.LSUseCardAction(s.GetLSCard("fear"), SEARCH); s.PassAllResponses();
        selectable(s, "fury", true); selectable(s, "patience", true);
        selectable(s, "resilience", false); selectable(s, "other", false);
        s.LSChooseCard(s.GetLSCard("patience")); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(s.GetLSCard("patience")));
        assertEquals(initial - 1, s.GetLSForcePileCount());
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
        assertFalse(searchOffered(s));
    }

    @Test
    public void CannotSearchWithoutThePrintedOneForce() {
        var s = searchSetup(false);
        assertEquals(0, s.GetLSForcePileCount()); assertFalse(searchOffered(s));
    }

    @Test
    public void SearchUseResetsOnNextTurn() {
        var s = searchSetup(true); assertTrue(searchOffered(s));
        s.LSUseCardAction(s.GetLSCard("fear"), SEARCH); s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("patience")); s.PassAllResponses();
        assertFalse(searchOffered(s));
        for (int i = 0; i < 100 && !(s.GetCurrentPlayer().equals(s.DS) && s.GetCurrentPhase() == Phase.CONTROL); i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) s.PlayerChooseYes(s.GetDecidingPlayer());
            else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                s.PlayerPass(s.GetDecidingPlayer());
            }
        }
        assertEquals(s.DS, s.GetCurrentPlayer()); assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        assertTrue(searchOffered(s));
        s.LSUseCardAction(s.GetLSCard("fear"), SEARCH); s.PassAllResponses();
        s.LSChooseCard(s.GetLSCard("fury")); s.PassAllResponses();
        assertTrue(s.GetLSHand().contains(s.GetLSCard("fury")));
    }

    @Test
    public void ActualAlterCannotChooseFearButCancelsAnotherEffect() {
        var s = scenario(); s.StartGame(); var chamber = s.GetLSCard("chamber");
        s.MoveLocationToTable(chamber); s.AttachCardsTo(chamber, s.GetLSCard("fear"));
        var decoy = s.GetLSCard("decoy");
        s.AttachCardsTo(s.GetLSStartingLocation(), decoy);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        s.MoveCardsToHand(s.GetDSCard("alter")); s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(3);
        assertTrue(s.DSCardPlayAvailable(s.GetDSCard("alter")));
        s.DSPlayCard(s.GetDSCard("alter"), "cancel Effect");
        if (s.DSDecisionAvailable("Choose Effect")) {
            assertFalse(s.DSHasCardChoicesAvailable(s.GetLSCard("fear"))); s.DSChooseCard(decoy);
        }
        if (s.DSDecisionAvailable("Choose a highest-ability character")) s.DSChooseCard(s.GetDSCard("vader"));
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(decoy)); assertEquals(chamber, s.GetLSCard("fear").getAttachedTo());
    }
}
