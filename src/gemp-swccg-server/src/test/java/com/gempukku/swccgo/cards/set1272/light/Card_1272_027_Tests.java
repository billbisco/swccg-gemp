package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_1272_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("clinging", "1272_27");
                    put("luke", "1_19");
                    put("saber", "1_155");
                    put("blaster", "1_152");
                    put("throne", "12_83");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void ClingingToTheEdgeVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("clinging").getBlueprint();

        assertEquals("Clinging To The Edge", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ClingingToTheEdgeVUsedActivatesTwoForceDuringOpponentsDeployPhase() {
        var scn = GetScenario();
        var clinging = scn.GetLSCard("clinging");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        //test1: 2 Force is activated
        //test2: the Interrupt goes to Used Pile
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToLSHand(clinging);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSPlayUsedInterruptAvailable(clinging));
        scn.LSPlayUsedInterrupt(clinging);
        scn.PassAllResponses();
        assertEquals(force + 2, scn.GetLSForcePileCount()); //test1
        assertInZone(Zone.USED_PILE, clinging); //test2
    }

    @Test
    public void ClingingToTheEdgeVUsedRequiresBothPlayersAtTheSameBattleground() {
        var scn = GetScenario();
        var clinging = scn.GetLSCard("clinging");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.MoveCardsToLSHand(clinging);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertFalse(scn.LSPlayUsedInterruptAvailable(clinging));
    }

    @Test
    public void ClingingToTheEdgeVUsedIsNotPlayableDuringYourOwnDeployPhase() {
        var scn = GetScenario();
        var clinging = scn.GetLSCard("clinging");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToLSHand(clinging);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSPlayUsedInterruptAvailable(clinging));
    }

    @Test
    public void ClingingToTheEdgeVLostDeploysLightsaberFromLostPileOnYourCharacterAtInteriorNabooBattleground() {
        var scn = GetScenario();
        var clinging = scn.GetLSCard("clinging");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var throne = scn.GetLSCard("throne");

        //test1: the lightsaber is deployed from Lost Pile on your character
        //test2: the Interrupt goes to Lost Pile
        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToLocation(throne, luke);
        scn.MoveCardsToLSHand(clinging);
        scn.MoveCardsToTopOfLSLostPile(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSPlayLostInterruptAvailable(clinging));
        scn.LSPlayLostInterrupt(clinging);
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(saber));
        scn.ChooseCardBPFromSelection(scn.LS, saber);
        scn.PassAllResponses();
        scn.PassAllResponses();
        assertEquals(luke, saber.getAttachedTo()); //test1
        assertInZone(Zone.LOST_PILE, clinging); //test2
    }

    @Test
    public void ClingingToTheEdgeVLostRequiresInteriorNabooBattleground() {
        var scn = GetScenario();
        var clinging = scn.GetLSCard("clinging");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), luke, trooper);
        scn.MoveCardsToLSHand(clinging);
        scn.MoveCardsToTopOfLSLostPile(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSPlayLostInterruptAvailable(clinging));
    }
}
