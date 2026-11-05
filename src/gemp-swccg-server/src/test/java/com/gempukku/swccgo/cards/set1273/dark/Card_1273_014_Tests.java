package com.gempukku.swccgo.cards.set1273.dark;

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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.PlayerOrder;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1273_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("darktime", "1273_14");
                    put("control", "4_139"); // Control
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
    public void ADarkTimeForTheRebellionVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("darktime").getBlueprint();
        assertEquals("A Dark Time For The Rebellion", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private static void lightWentFirst(VirtualTableScenario scn) {
        try {
            Field field = GameState.class.getDeclaredField("_playerOrder");
            field.setAccessible(true);
            field.set(scn.gameState(), new PlayerOrder(new ArrayList<>(Arrays.asList(scn.LS, scn.DS))));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private int activateUpToTwo(boolean lightWentFirst) {
        var scn = GetScenario();
        var darktime = scn.GetDSCard("darktime");
        scn.StartGame();
        if (lightWentFirst) {
            lightWentFirst(scn);
        }
        scn.MoveCardsToDSHand(darktime);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1));
        scn.SkipToDSTurn(Phase.CONTROL);
        int forceBefore = scn.GetDSForcePileCount();
        int lostBefore = scn.GetDSLostPileCount();
        scn.DSPlayCard(darktime, "Activate up to 2 Force");
        scn.PassAllResponses();
        scn.DSDecided(2);
        scn.PassAllResponses();
        assertEquals(forceBefore + 2, scn.GetDSForcePileCount());
        return lostBefore - scn.GetDSLostPileCount();
    }

    @Test
    public void ADarkTimeForTheRebellionVActivatesUpToTwoForce() {
        assertEquals(0, activateUpToTwo(false));
    }

    @Test
    public void ADarkTimeForTheRebellionVRetrievesOneForceIfOpponentWentFirst() {
        assertEquals(1, activateUpToTwo(true));
    }

    @Test
    public void ADarkTimeForTheRebellionVTakesControlIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var darktime = scn.GetDSCard("darktime");
        var control = scn.GetDSCard("control");
        scn.StartGame();
        scn.MoveCardsToDSHand(darktime);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(control);
        scn.DSPlayCard(darktime, "Take Control into hand");
        scn.PassAllResponses();
        scn.DSChooseCard(control);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, control);
        assertInZone(Zone.USED_PILE, darktime);
    }

    @Test
    public void ADarkTimeForTheRebellionVPreventsBattleDestinyModificationUntilEndOfTurn() {
        var scn = GetScenario();
        var darktime = scn.GetDSCard("darktime");
        scn.StartGame();
        scn.MoveCardsToDSHand(darktime);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(darktime, "Affect battle destiny draws");
        scn.PassAllResponses();
        boolean mayNotModify = false;
        boolean mayNotCancel = false;
        for (Modifier modifier : scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.MAY_NOT_MODIFY_BATTLE_DESTINY)) {
            mayNotModify |= modifier.getSource(scn.gameState()) == darktime;
        }
        for (Modifier modifier : scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY_UNLESS_BEING_REDRAWN)) {
            mayNotCancel |= modifier.getSource(scn.gameState()) == darktime;
        }
        assertTrue(mayNotModify);
        assertTrue(mayNotCancel);
    }

    @Test
    public void ADarkTimeForTheRebellionVSubtractsOneFromOpponentsJustDrawnBattleDestiny() {
        var scn = GetScenario();
        var darktime = scn.GetDSCard("darktime");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSHand(darktime);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(4);
        scn.SkipToPowerSegment();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        if (scn.DSDecisionAvailable("battle destiny?")) {
            scn.DSChooseNo();
            scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.LSChooseYes();
        for (int i = 0; i < 6 && !scn.DSCardPlayAvailable(darktime, "Subtract 1 from destiny"); i++) {
            scn.PassResponses();
        }
        scn.DSPlayCard(darktime, "Subtract 1 from destiny");
        scn.PassAllResponses();
        assertEquals(3, scn.GetLSTotalDestiny());
    }
}
