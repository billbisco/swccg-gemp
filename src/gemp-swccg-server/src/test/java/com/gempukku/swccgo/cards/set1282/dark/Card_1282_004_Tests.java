package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21"); // Obi-Wan Kenobi, immune to attrition < 5
                    put("wolfman", "1_30"); // Shistavanen Wolfman: may move to an adjacent site as a 'react'
                    put("projection", "6_56");
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("db94", "1_129"); // Tatooine: Docking Bay 94
                }},
                new HashMap<>() {{
                    put("fury", "1282_4");
                    put("vader", "1_168");
                    put("saber", "1_324"); // Vader's Lightsaber
                    put("trooper", "1_194"); // Stormtrooper (a character to draw for destiny)
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

    /** Test-only Light character with immunity to attrition < 9 (to show the "< 5" limit). */
    private static AbstractRebel immuneRebel() {
        return new AbstractRebel(Side.LIGHT, 1, 1, 1, 1, 1, "Test Immune Rebel", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            @Override
            protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
                return Collections.singletonList(new ImmuneToAttritionLessThanModifier(self, 9));
            }
        };
    }

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 30; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean dsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardActionAvailable(card, text);
    }

    private static float immunity(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }

    @Test
    public void SithFuryAndEndThisDestructiveConflictStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fury").getBlueprint();
        assertEquals("Sith Fury & End This Destructive Conflict", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertTrue(scn.GetDSCard("fury").getTitles().contains(Title.Sith_Fury));
        assertTrue(scn.GetDSCard("fury").getTitles().contains(Title.End_This_Destructive_Conflict));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleWithVaderAt(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl... targets) {
        var fury = scn.GetDSCard("fury");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        scn.MoveCardsToLocation(site, vader);
        scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToLocation(site, targets);
        scn.MoveCardsToHand(fury);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
    }

    @Test
    public void SithFuryInsteadOfFiringMakesTargetImmunityMinusThreeAtOpponentsSite() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var saber = scn.GetDSCard("saber");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        battleWithVaderAt(scn, scn.GetLSStartingLocation(), obiwan);
        assertEquals(5, immunity(scn, obiwan), scn.epsilon);

        assertTrue(scn.DSCardPlayAvailable(fury, "Reduce immunity to attrition instead of firing"));
        scn.DSPlayCard(fury, "Reduce immunity to attrition instead of firing");
        scn.DSChooseCard(saber);
        scn.DSChooseCard(obiwan);
        scn.PassAllResponses();

        assertEquals(2, immunity(scn, obiwan), scn.epsilon);
        assertInZone(Zone.USED_PILE, fury);
    }

    @Test
    public void SithFuryInsteadOfFiringLimitsHighImmunityToLessThanFive() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var saber = scn.GetDSCard("saber");
        scn.StartGame();
        var rebel = MissionTestSupport.putInHand(scn, 128241, scn.LS, immuneRebel());
        battleWithVaderAt(scn, scn.GetLSStartingLocation(), rebel);
        assertEquals(9, immunity(scn, rebel), scn.epsilon);

        scn.DSPlayCard(fury, "Reduce immunity to attrition instead of firing");
        scn.DSChooseCard(saber);
        scn.DSChooseCard(rebel);
        scn.PassAllResponses();

        assertEquals(5, immunity(scn, rebel), scn.epsilon);
    }

    @Test
    public void SithFuryInsteadOfFiringAvailableAtYourNonDiamondSite() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        battleWithVaderAt(scn, scn.GetDSStartingLocation(), obiwan);
        assertTrue(scn.DSCardPlayAvailable(fury, "Reduce immunity to attrition instead of firing"));
    }

    @Test
    public void SithFuryInsteadOfFiringNotAvailableAtYourDiamondSite() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        var desert = MissionTestSupport.putLocationOnTable(scn, 128242, scn.DS, VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("7_281"));
        battleWithVaderAt(scn, desert, obiwan);
        assertTrue(scn.IsBattleStarted());
        assertFalse(scn.GetDSAvailableActions().contains("Reduce immunity to attrition instead of firing"));
    }

    @Test
    public void SithFuryTakesJustDrawnCharacterDestintyIntoHandAndRedraws() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, obiwan);
        scn.MoveCardsToHand(fury);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        var redraw = scn.GetDSDestiny(3);
        scn.MoveCardsToTopOfDSReserveDeck(redraw);
        scn.MoveCardsToTopOfDSReserveDeck(trooper);
        scn.DSInitiateBattle(site);
        passUntil(scn, () -> scn.DSDecisionAvailable("Do you want to draw"));
        scn.DSChooseYes();
        passUntil(scn, () -> dsHasAction(scn, fury, "Take destiny card into hand and cause re-draw"));

        assertTrue(dsHasAction(scn, fury, "Take destiny card into hand and cause re-draw"));
        scn.DSPlayCard(fury, "Take destiny card into hand and cause re-draw");
        passUntil(scn, () -> scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER") || scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));

        assertInHand(trooper);
        // the redraw drew the next card of Reserve Deck
        assertFalse(redraw.getZone() == Zone.RESERVE_DECK);
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));
    }

    @Test
    public void SithFuryCancelsAReact() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var vader = scn.GetDSCard("vader");
        var wolfman = scn.GetLSCard("wolfman");
        var obiwan = scn.GetLSCard("obiwan");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, wolfman);
        scn.MoveCardsToLocation(db94, vader, obiwan);
        scn.MoveCardsToHand(fury);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.EnsureLSForcePile(4);
        scn.DSInitiateBattle(db94);

        assertTrue(scn.LSCardActionAvailable(wolfman, "react"));
        scn.LSUseCardAction(wolfman, "react");
        scn.LSChooseCard(db94);
        passUntil(scn, () -> dsHasAction(scn, fury, "Cancel 'react'"));
        assertTrue(dsHasAction(scn, fury, "Cancel 'react'"));
        scn.DSPlayCard(fury, "Cancel 'react'");
        scn.PassAllResponses();

        assertEquals(cantina, wolfman.getAtLocation());
        assertInZone(Zone.USED_PILE, fury);
    }

    @Test
    public void SithFuryPlacesProjectionOfASkywalkerInOpponentsUsedPile() {
        var scn = GetScenario();
        var fury = scn.GetDSCard("fury");
        var projection = scn.GetLSCard("projection");
        scn.StartGame();
        scn.AttachCardsTo(scn.GetLSStartingLocation(), projection);
        scn.MoveCardsToHand(fury);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);

        assertTrue(scn.DSCardPlayAvailable(fury, "Place Projection Of A Skywalker in Used Pile"));
        scn.DSPlayCard(fury, "Place Projection Of A Skywalker in Used Pile");
        scn.DSChooseCard(projection);
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, projection);
        assertEquals(scn.LS, projection.getZoneOwner());
    }
}
