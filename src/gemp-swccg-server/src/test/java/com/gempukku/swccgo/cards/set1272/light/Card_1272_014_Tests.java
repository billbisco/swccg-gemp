package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bail", "1272_14");
                    put("liana", "12_11");
                    put("defer", "12_43");
                    put("mothma", "204_10");
                    put("luke", "1_19");
                    put("senate", "12_75");
                    put("lost", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void BailOrganaFatherOfRebellionStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bail").getBlueprint();

        assertEquals("Bail Organa, Father Of Rebellion", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getPolitics(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BAIL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SENATOR);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.REPUBLIC);
            add(Icon.EPISODE_I);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BailOrganaFatherOfRebellionHasRebellionAgenda() {
        var scn = GetScenario();
        var bail = scn.GetLSCard("bail");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), bail);
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), bail, Agenda.REBELLION));
    }

    @Test
    public void BailOrganaFatherOfRebellionMakesYourCoruscantPoliticalEffectsAndSenatorsLost() {
        var scn = GetScenario();
        var bail = scn.GetLSCard("bail");
        var liana = scn.GetLSCard("liana");
        var defer = scn.GetLSCard("defer");
        var mothma = scn.GetLSCard("mothma");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, liana, mothma);
        scn.MoveCardsToLSSideOfTable(defer);
        scn.MoveCardsToLSHand(bail);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);

        scn.LSDeployCard(bail);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card to be lost")) {
            scn.LSChooseCard(liana);
            scn.PassAllResponses();
        }

        assertTrue(inLostPile(liana));
        assertTrue(inLostPile(defer));
        assertEquals(site, mothma.getAtLocation());
        assertEquals(site, bail.getAtLocation());
    }

    @Test
    public void BailOrganaFatherOfRebellionRetrievesForceWhenYouInitiateBattleWithARebel() {
        var scn = GetScenario();
        var bail = scn.GetLSCard("bail");
        var luke = scn.GetLSCard("luke");
        var lost = scn.GetLSCard("lost");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), bail);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToTopOfLSLostPile(lost);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(site);
        assertTrue(scn.LSCardActionAvailable(bail, "Retrieve 1 Force"));
        scn.LSUseCardAction(bail, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertFalse(inLostPile(lost));
    }

    @Test
    public void BailOrganaFatherOfRebellionTakesCardFromForcePileWhileInSenateMajority() {
        var scn = GetScenario();
        var bail = scn.GetLSCard("bail");
        var senate = scn.GetLSCard("senate");

        scn.StartGame();
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, bail);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        int handBefore = scn.GetLSHandCount();
        int forcePileBefore = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(bail, "Take card into hand from Force Pile"));
        scn.LSUseCardAction(bail, "Take card into hand from Force Pile");
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();
        assertEquals(handBefore + 1, scn.GetLSHandCount());
        assertEquals(forcePileBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void BailOrganaFatherOfRebellionMayNotTakeCardFromForcePileOutsideSenateMajority() {
        var scn = GetScenario();
        var bail = scn.GetLSCard("bail");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), bail);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(bail, "Take card into hand from Force Pile"));
    }

    private static boolean inLostPile(com.gempukku.swccgo.game.PhysicalCardImpl card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }
}
