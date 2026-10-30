package com.gempukku.swccgo.cards.set1103.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("come", "1103_29");
                    put("vader", "1_168");
                    put("pilot", "1_180");
                    put("ozzel", "3_82");
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
    public void ComeWithMeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("come").getBlueprint();

        assertEquals("Come With Me", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ComeWithMeDeploysOnVader() {
        var scn = GetScenario();
        var come = scn.GetDSCard("come");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        scn.MoveCardsToDSHand(come);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(come));
        scn.DSPlayCard(come);
        if (scn.DSHasCardChoiceAvailable(vader)) {
            scn.DSChooseCard(vader);
        }
        scn.PassAllResponses();

        assertEquals(vader, come.getAttachedTo());
    }

    @Test
    public void ComeWithMeDeploysAnImperialPilotHereFromReserveDeck() {
        var scn = GetScenario();
        var come = scn.GetDSCard("come");
        var vader = scn.GetDSCard("vader");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        scn.AttachCardsTo(vader, come);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(pilot);

        assertTrue(scn.DSCardActionAvailable(come, "Deploy Imperial pilot"));
        scn.DSUseCardAction(come, "Deploy Imperial pilot");
        completeDsSearchChoice(scn, pilot);

        assertAtLocation(site, pilot);
    }

    @Test
    public void ComeWithMePlacesThisEffectInUsedPileToTakeAnImperialPilotIntoHand() {
        var scn = GetScenario();
        var come = scn.GetDSCard("come");
        var vader = scn.GetDSCard("vader");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, pilot);
        scn.AttachCardsTo(vader, come);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(come, "Take Imperial pilot into hand"));
        scn.DSUseCardAction(come, "Take Imperial pilot into hand");
        scn.DSChooseCard(pilot);
        scn.PassAllResponses();

        assertInZone(Zone.HAND, pilot);
        assertInZone(Zone.USED_PILE, come);
    }

    private void completeDsSearchChoice(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        for (int i = 0; i < 20; i++) {
            if (card.getZone() == Zone.AT_LOCATION || card.getZone() == Zone.SIDE_OF_TABLE
                    || card.getZone() == Zone.ATTACHED) {
                return;
            }
            if (scn.DSHasCardChoiceAvailable(card)) {
                scn.DSChooseCard(card);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.PassAllResponses();
                continue;
            }
            break;
        }
    }
}
