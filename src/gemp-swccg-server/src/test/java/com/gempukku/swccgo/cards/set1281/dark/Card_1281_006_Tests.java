package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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

public class Card_1281_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("rtic", "1281_6");
                    put("jawablaster", "2_159"); // Jawa Blaster
                    put("trooper1", "1_194"); // Stormtrooper, deploy 1
                    put("trooper2", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, // Tatooine: Marketplace (battleground)
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RticHweeiStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rtic").getBlueprint();
        assertEquals("R'tic H'weei", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.JAWA, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RticHweeiTakesJawaBlasterIntoHandFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var rtic = scn.GetDSCard("rtic");
        var jawablaster = scn.GetDSCard("jawablaster");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), rtic);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(jawablaster);
        assertTrue(scn.DSCardActionAvailable(rtic, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(rtic, "Take card into hand from Reserve Deck");
        scn.DSChooseCard(jawablaster);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, jawablaster);
        scn.MoveCardsToTopOfOwnReserveDeck(jawablaster);
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(rtic, "Take card into hand from Reserve Deck"));
    }

    @Test
    public void RticHweeiUsesOneForceInOpponentsForcePileOncePerTurnWhenPresentAtTatooineBattleground() {
        var scn = GetScenario();
        var rtic = scn.GetDSCard("rtic");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, rtic);
        scn.MoveCardsToHand(trooper1, trooper2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        scn.LSActivateForceCheat(4);
        int lsForce = scn.GetLSForcePile().size();
        int dsForce = scn.GetDSForcePile().size();
        scn.DSDeployCard(trooper1);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        if (scn.LSDecisionAvailable("Optional responses")) {
            scn.LSPass();
        }
        assertTrue(scn.DSActionAvailable("Use 1 Force in opponent's Force Pile"));
        scn.DSChooseAction("Use 1 Force in opponent's Force Pile");
        scn.PassAllResponses();
        assertEquals(lsForce - 1, scn.GetLSForcePile().size());
        assertEquals(dsForce, scn.GetDSForcePile().size());

        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        scn.DSDeployCard(trooper2);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        if (scn.LSDecisionAvailable("Optional responses")) {
            scn.LSPass();
        }
        assertFalse(scn.DSActionAvailable("Use 1 Force in opponent's Force Pile"));
    }
}
