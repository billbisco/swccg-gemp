package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
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

public class Card_1281_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cantina", "1_128"); // Tatooine: Cantina (battleground)
                    put("db94", "1_129"); // Tatooine: Docking Bay 94 (adjacent to the Cantina)
                    put("luke", "1_19"); // Luke Skywalker, ability 4
                    put("obi", "1_21"); // Obi-Wan Kenobi, ability 6 (Jedi)
                }},
                new HashMap<>() {{
                    put("deathmark", "1281_10");
                    put("onthehunt", "1281_12");
                    put("bane", "1281_2"); // Bane Malar, Spice Addict: assassin, power 2
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    // Stand-in for the Virtual Block 8.1 Objective Contract Killers (On The Hunt deploys on it).
    static AbstractNormalEffect contractKillers() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Contract Killers", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    private void putOnTheHuntOnTable(VirtualTableScenario scn, int standInId) {
        var host = MissionTestSupport.putOnTable(scn, standInId, scn.DS, contractKillers());
        scn.AttachCardsTo(host, scn.GetDSCard("onthehunt"));
    }

    @Test
    public void DeathMarkAndHuttBountyStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("deathmark").getBlueprint();
        assertEquals("Death Mark & Hutt Bounty", card.getTitle());
        assertTrue(card.getTitles().contains(Title.Death_Mark));
        assertTrue(card.getTitles().contains(Title.Hutt_Bounty));
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DeathMarkMayNotDeployUnlessOnTheHuntOnTable() {
        var scn = GetScenario();
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        var bane = scn.GetDSCard("bane");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, luke);
        scn.MoveCardsToLocation(db94, bane);
        scn.MoveCardsToHand(deathmark);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(deathmark));
    }

    private VirtualTableScenario DeployDeathMarkOn(String markKey, int standInId) {
        var scn = GetScenario();
        var deathmark = scn.GetDSCard("deathmark");
        var mark = scn.GetLSCard(markKey);
        var bane = scn.GetDSCard("bane");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        scn.StartGame();
        putOnTheHuntOnTable(scn, standInId);
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, mark);
        scn.MoveCardsToLocation(db94, bane);
        scn.MoveCardsToHand(deathmark);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(deathmark));
        scn.DSDeployCard(deathmark);
        assertTrue(scn.DSHasCardChoiceAvailable(mark));
        assertFalse(scn.DSHasCardChoiceAvailable(bane));
        scn.DSChooseCard(mark);
        assertTrue(scn.DSHasCardChoiceAvailable(bane));
        scn.DSChooseCard(bane);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(mark, deathmark));
        assertEquals(bane, deathmark.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        return scn;
    }

    private void MoveBaneToMarkDuringMovePhase(VirtualTableScenario scn, int dsDestiny, int lsDestiny) {
        var bane = scn.GetDSCard("bane");
        var cantina = scn.GetLSCard("cantina");
        scn.SkipToPhase(Phase.MOVE);
        scn.PrepareLSDestiny(lsDestiny);
        scn.PrepareLSDestiny(lsDestiny);
        scn.PrepareDSDestiny(dsDestiny);
        assertTrue(scn.DSMoveAvailable(bane));
        scn.DSMoveCard(bane, cantina);
        for (int i = 0; i < 150; i++) {
            if (scn.LSGetDecision() != null && scn.LSGetCardChoices() != null && !scn.LSGetCardChoices().isEmpty()
                    && scn.LSGetDecision().getText().toLowerCase().contains("destiny")) {
                scn.LSDecided(scn.LSGetCardChoices().getFirst());
                continue;
            }
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            if (scn.DSDecisionAvailable("in Used Pile?")) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.AwaitingDSMovePhaseActions()) {
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
        }
    }

    @Test
    public void DeathMarkDeploysOnOpponentsCharacterAtBattlegroundTargetingAssassinAtAnotherSite() {
        DeployDeathMarkOn("luke", 98101);
    }

    @Test
    public void DeathMarkAssassinatesMarkWhenYourTotalIsHigher() {
        var scn = DeployDeathMarkOn("luke", 98102);
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        int lsLost = scn.GetLSLostPile().size();
        // DS: 6 + Bane's power 2 = 8 > LS: 1 + Luke's defense value 4 = 5
        MoveBaneToMarkDuringMovePhase(scn, 6, 1);
        assertInZone(Zone.LOST_PILE, luke);
        assertEquals(Zone.SIDE_OF_TABLE, deathmark.getZone());
        assertEquals(scn.DS, deathmark.getZoneOwner());
        assertEquals(lsLost + 1 + 2, scn.GetLSLostPile().size()); // Luke and 2 Force
    }

    @Test
    public void DeathMarkAssassinatingAJediCostsOpponentFourForce() {
        var scn = DeployDeathMarkOn("obi", 98103);
        var obi = scn.GetLSCard("obi");
        int lsLost = scn.GetLSLostPile().size();
        // DS: 7 + 2 = 9 > LS: 1 + Obi-Wan's defense value 6 = 7
        MoveBaneToMarkDuringMovePhase(scn, 7, 1);
        assertInZone(Zone.LOST_PILE, obi);
        assertEquals(lsLost + 1 + 4, scn.GetLSLostPile().size()); // Obi-Wan and 4 Force
    }

    @Test
    public void DeathMarkGoesToUsedPileWithAssassinWhenTotalIsNotHigher() {
        var scn = DeployDeathMarkOn("luke", 98104);
        var deathmark = scn.GetDSCard("deathmark");
        var luke = scn.GetLSCard("luke");
        var bane = scn.GetDSCard("bane");
        var cantina = scn.GetLSCard("cantina");
        // DS: 1 + 2 = 3, LS: 5 + 4 = 9
        MoveBaneToMarkDuringMovePhase(scn, 1, 5);
        assertInZone(Zone.USED_PILE, deathmark);
        assertInZone(Zone.USED_PILE, bane);
        assertEquals(cantina, luke.getAtLocation());
    }

    @Test
    public void DeathMarkMayNotBeCanceled() {
        var scn = DeployDeathMarkOn("luke", 98105);
        var deathmark = scn.GetDSCard("deathmark");
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), deathmark));
    }
}
