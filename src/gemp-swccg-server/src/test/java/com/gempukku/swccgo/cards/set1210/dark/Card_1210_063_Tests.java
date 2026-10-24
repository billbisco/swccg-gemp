package com.gempukku.swccgo.cards.set1210.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_063_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trouble", "1210_63");
                    put("st1", "1_194");
                    put("st2", "1_194");
                    put("garindan", "1_177");
                    put("cantina", "1_290");
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

    private boolean LocalTroubleOffered(VirtualTableScenario scn) {
        return scn.DSAnyDecisionsAvailable()
                && (scn.DSActionAvailable("Initiate Local Trouble battle")
                || scn.DSActionAvailable("Local Trouble")
                || scn.DSCardPlayAvailable(scn.GetDSCard("trouble")));
    }

    private void PlayLocalTrouble(VirtualTableScenario scn, PhysicalCardImpl st1, PhysicalCardImpl st2, PhysicalCardImpl opponent) {
        var trouble = scn.GetDSCard("trouble");
        if (scn.DSCardPlayAvailable(trouble)) {
            scn.DSPlayCard(trouble);
        } else {
            scn.DSUseCardAction(trouble);
        }
        if (scn.DSHasCardChoiceAvailable(st1) || scn.DSHasCardChoiceAvailable(st2)) {
            scn.DSChooseCards(st1, st2);
        }
        if (scn.DSHasCardChoiceAvailable(opponent)) {
            scn.DSChooseCard(opponent);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }
    }

    @Test
    public void LocalTroubleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("trouble").getBlueprint();

        assertEquals("Local Trouble", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LocalTroubleInitiatesInteriorBattleWithTwoImperialsAndNoOpponentBattleDamage() {
        var scn = GetScenario();
        var trouble = scn.GetDSCard("trouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveCardsToDSHand(trouble);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.SkipToPhase(Phase.BATTLE);

        assertTrue(LocalTroubleOffered(scn));
        int forceBefore = scn.GetDSForcePileCount();
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(forceBefore - scn.GetDSForcePileCount() >= 1);
        assertTrue(scn.gameState().isDuringBattle());
        assertTrue(scn.IsParticipatingInBattle(st1));
        assertTrue(scn.IsParticipatingInBattle(st2));
        assertTrue(scn.IsParticipatingInBattle(rebel));
        assertTrue(scn.game().getModifiersQuerying().isTakesNoBattleDamage(scn.gameState(), scn.LS));
        assertTrue(trouble.getZone() == Zone.VOID || trouble.getZone() == Zone.LOST_PILE
                || trouble.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void LocalTroubleExcludesUnchosenImperialAndBlocksLaterBattlesHere() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var garindan = scn.GetDSCard("garindan");
        var rebel = scn.GetLSCard("rebel");
        var trouble = scn.GetDSCard("trouble");

        scn.StartGame();
        scn.MoveCardsToDSHand(trouble);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, st1, st2, garindan, rebel);
        scn.SkipToPhase(Phase.BATTLE);
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
        assertFalse(scn.IsParticipatingInBattle(garindan));

        if (scn.gameState().isDuringBattle()) {
            scn.SkipToDamageSegment(false);
            if (scn.AwaitingLSBattleDamagePayment()) {
                scn.LSPayRemainingBattleDamageFromReserveDeck();
            }
            if (scn.AwaitingDSBattleDamagePayment()) {
                scn.DSPayRemainingBattleDamageFromReserveDeck();
            }
            scn.PassAllResponses();
        }
        String ds = scn.gameState().getDarkPlayer();
        assertTrue(scn.game().getModifiersQuerying().mayNotInitiateBattleAtLocation(scn.gameState(), cantina, ds));
        assertInZone(Zone.LOST_PILE, trouble);
    }

    @Test
    public void LocalTroubleIsNotOfferedWithOnlyOneImperial() {
        var scn = GetScenario();
        var trouble = scn.GetDSCard("trouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveCardsToDSHand(trouble);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, st1, rebel);
        scn.SkipToPhase(Phase.BATTLE);

        assertFalse(LocalTroubleOffered(scn));
    }

    @Test
    public void LocalTroubleIsNotOfferedAtExteriorSite() {
        var scn = GetScenario();
        var trouble = scn.GetDSCard("trouble");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(trouble);
        scn.MoveCardsToLocation(marketplace, st1, st2, rebel);
        scn.SkipToPhase(Phase.BATTLE);

        assertFalse(LocalTroubleOffered(scn));
    }
}
