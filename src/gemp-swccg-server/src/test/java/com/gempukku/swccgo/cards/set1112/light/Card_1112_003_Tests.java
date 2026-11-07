package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1112_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("odds", "1112_3");
                    put("luke", "1_19");
                    put("blaster", "1_152");
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
    public void ChangingTheOddsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("odds").getBlueprint();

        assertEquals(Title.Changing_The_Odds, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.TATOOINE);
            add(Icon.EPISODE_I);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChangingTheOddsMakesTheDrawingPlayerActivateForceAndShuffleBeforeWeaponDestiny() {
        var scn = GetScenario();
        var odds = scn.GetLSCard("odds");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, blaster);
        scn.MoveCardsToLSHand(odds);
        scn.PrepareLSDestiny(1);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        int forceBefore = -1;
        boolean played = false;
        for (int i = 0; i < 12 && !played; i++) {
            if (scn.LSAnyDecisionsAvailable() && scn.LSGetADParam("actionText") != null
                    && scn.LSCardPlayAvailable(odds, "Activate 1 Force and shuffle Reserve Deck")) {
                forceBefore = scn.GetLSForcePileCount();
                scn.LSPlayCard(odds, "Activate 1 Force and shuffle Reserve Deck");
                played = true;
                break;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            if (decision.getText() != null && decision.getText().toLowerCase().contains("force")
                    && scn.LSHasCardChoiceAvailable(scn.GetTopOfLSForcePile())) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
                continue;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(played);
        scn.PassAllResponses();
        assertEquals(forceBefore + 1, scn.GetLSForcePileCount());
        assertTrue(odds.getZone() == Zone.USED_PILE || odds.getZone() == Zone.TOP_OF_USED_PILE);
    }

    private static final String REDRAW = "Cancel and redraw both battle destinies";

    private VirtualTableScenario redrawScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("odds", "1112_3"); put("luke", "1_19"); put("guard", "1_26");
        }}, new HashMap<>() {{
            put("djas", "1_171");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private boolean redrawOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardPlayAvailable(s.GetLSCard("odds"), REDRAW);
    }

    @Test
    public void ChangingTheOddsRedrawnBattleDestiniesCountTowardBattleTotals() {
        var s = redrawScenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetDSCard("djas"), s.GetLSCard("luke"), s.GetLSCard("guard"));
        s.MoveCardsToHand(s.GetLSCard("odds")); s.DSActivateForceCheat(6);
        s.SkipToDSTurn(Phase.BATTLE);
        // Redraws come second in each deck; the first draw comes from the top.
        s.PrepareDSDestiny(5); s.PrepareDSDestiny(2);
        s.PrepareLSDestiny(6); s.PrepareLSDestiny(1);
        s.DSInitiateBattle(site); s.SkipToPowerSegment();
        s.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(s.DSDecisionAvailable("battle destiny")); s.DSChooseYes(); s.PassDestinyDrawResponses();
        s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(s.LSDecisionAvailable("battle destiny")); s.LSChooseYes(); s.PassDestinyDrawResponses();
        for (int i = 0; i < 20 && !redrawOffered(s)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(2, s.GetDSTotalDestiny()); assertEquals(1, s.GetLSTotalDestiny());
        assertTrue(s.GetCurrentDecision().getText(), redrawOffered(s));
        var odds = s.GetLSCard("odds");
        var battle = s.gameState().getBattleState();
        var lightDraw = (PhysicalCardImpl) battle.getBattleDestinyDraws(s.LS).get(0);
        var darkDraw = (PhysicalCardImpl) battle.getBattleDestinyDraws(s.DS).get(0);
        var lightRedraw = s.GetTopOfLSReserveDeck(); var darkRedraw = s.GetTopOfDSReserveDeck();
        s.LSPlayCard(odds, REDRAW); s.PassAllResponses();
        if (s.LSDecisionAvailable("to cancel")) { s.LSChooseCard(lightDraw); s.PassAllResponses(); }
        if (s.LSDecisionAvailable("to cancel")) { s.LSChooseCard(darkDraw); }
        for (int i = 0; i < 40 && !battle.getBattleDestinyDraws(s.DS).contains(darkRedraw)
                && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(List.of(lightRedraw), battle.getBattleDestinyDraws(s.LS));
        assertEquals(List.of(darkRedraw), battle.getBattleDestinyDraws(s.DS));
        assertEquals(5, s.GetDSTotalDestiny()); assertEquals(6, s.GetLSTotalDestiny());
        assertTrue(s.GetLSUsedPile().contains(odds));
    }
}
