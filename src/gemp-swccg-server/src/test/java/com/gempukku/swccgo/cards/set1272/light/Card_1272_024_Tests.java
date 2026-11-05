package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1272_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tactics", "1272_24");
                    put("alderaan", "1_121");
                    put("karie", "9_18");
                    put("tycho", "9_30");
                    put("hobbie", "3_5");
                    put("tatooine", "1_127");
                    put("red1", "1_144");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void RogueSquadronTacticsStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tactics").getBlueprint();

        assertEquals("Rogue Squadron Tactics", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RogueSquadronTacticsIsImmuneToAlter() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(tactics);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), tactics, Title.Alter));
    }

    @Test
    public void RogueSquadronTacticsOncePerGameMayTakeAlderaanAndDeathStarIIRogueSquadronPilot() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var alderaan = scn.GetLSCard("alderaan");
        var karie = scn.GetLSCard("karie");
        var tycho = scn.GetLSCard("tycho");
        var hobbie = scn.GetLSCard("hobbie");

        //test1: a non-[Virtual Block 1] Alderaan may be taken
        //test2: a [Death Star II] Rogue Squadron pilot may be taken, but not Tycho or a non-[Death Star II] one
        //test3: once per game
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(tactics);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(alderaan, karie, tycho, hobbie);
        assertTrue(scn.LSCardActionAvailable(tactics, "Take cards"));
        scn.LSUseCardAction(tactics, "Take cards");
        scn.LSChooseYes();
        assertTrue(lsReserveSearchSelectable(scn, alderaan)); //test1
        assertFalse(lsReserveSearchSelectable(scn, karie)); //test1
        scn.ChooseCardBPFromSelection(scn.LS, alderaan);
        scn.PassAllResponses();
        scn.LSChooseYes();
        assertTrue(lsReserveSearchSelectable(scn, karie)); //test2
        assertFalse(lsReserveSearchSelectable(scn, tycho)); //test2
        assertFalse(lsReserveSearchSelectable(scn, hobbie)); //test2
        scn.ChooseCardBPFromSelection(scn.LS, karie);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, alderaan, karie); //test1 //test2
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(tactics, "Take cards")); //test3
    }

    @Test
    public void RogueSquadronTacticsMayDrawTopCardOfReserveDeckAfterForceDrainAtBattlegroundSystem() {
        var scn = GetScenario();
        var tactics = scn.GetLSCard("tactics");
        var tatooine = scn.GetLSCard("tatooine");
        var red1 = scn.GetLSCard("red1");
        var luke = scn.GetLSCard("luke");
        var alderaan = scn.GetLSCard("alderaan");

        //test1: the top card of Reserve Deck goes to hand
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLSSideOfTable(tactics);
        scn.MoveCardsToLocation(tatooine, red1);
        scn.BoardAsPilot(red1, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(alderaan);
        assertTrue(scn.LSForceDrainAvailable(tatooine));
        scn.LSForceDrainAt(tatooine);
        scn.PassForceDrainStartResponses();
        assertTrue(scn.AwaitingDSForceLossPayment());
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(tactics, "Draw top card"));
        scn.LSUseCardAction(tactics, "Draw top card");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, alderaan); //test1
    }

    private boolean lsReserveSearchSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] blueprintIds = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        if (blueprintIds == null || selectable == null)
            return false;
        String id = card.getBlueprintId(true);
        for (int i = 0; i < blueprintIds.length; i++) {
            if (id.equals(blueprintIds[i]) && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }
}
