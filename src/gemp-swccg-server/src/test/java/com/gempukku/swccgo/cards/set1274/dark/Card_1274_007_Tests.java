package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.logic.modifiers.MayNotDrawDestinyExceptBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("scum", "4_70");   // We Don't Need Their Scum: draws two destiny during a battle
                }},
                new HashMap<>() {{
                    put("territory", "1274_7");
                    put("trooper", "1_194");
                    put("dengar", "4_100");
                    put("vader", "1_168");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean lsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean passUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 80; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (scn.LSDecisionAvailable("Do you want to draw")) {
                scn.LSChooseYes();
            } else if (scn.DSDecisionAvailable("Do you want to draw")) {
                scn.DSChooseYes();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    @Test
    public void OccupiedTerritoryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("territory").getBlueprint();

        assertEquals("Occupied Territory", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.DEPLOYS_ON_LOCATION); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OccupiedTerritoryDeploysOnABattleground() {
        var scn = GetScenario();
        var territory = scn.GetDSCard("territory");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(territory);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(territory));
        scn.DSPlayCard(territory);
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(site);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(site, territory));
    }

    @Test
    public void OccupiedTerritoryDuringBattleHereMayPlaceInUsedPileToDrawTwoCards() {
        var scn = GetScenario();
        var territory = scn.GetDSCard("territory");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(site, territory);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(territory, "draw 2 cards"));

        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        int handBefore = scn.GetDSHandCount();
        assertTrue(scn.DSCardActionAvailable(territory, "draw 1 card"));
        assertTrue(scn.DSCardActionAvailable(territory, "draw 2 cards"));
        scn.DSUseCardAction(territory, "draw 2 cards");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, territory);
        assertEquals(handBefore + 2, scn.GetDSHandCount());
    }

    @Test
    public void OccupiedTerritoryPreventsOpponentFromDrawingDestinyDuringPowerSegment() {
        var scn = GetScenario();
        var territory = scn.GetDSCard("territory");
        var trooper = scn.GetDSCard("trooper");
        var dengar = scn.GetDSCard("dengar");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var scum = scn.GetLSCard("scum");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, dengar, vader);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(site, territory);
        scn.MoveCardsToLSHand(scum);
        scn.EnsureDSForcePile(5);
        scn.EnsureLSForcePile(5);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.DSUseCardAction(territory, "prevent");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, territory);

        assertTrue(passUntil(scn, () -> scn.gameState().getBattleState() != null && scn.IsReachedPowerSegment() && lsCan(scn, scum, null)));
        assertTrue(MayNotDrawDestinyExceptBattleDestinyModifier.mayNotDrawDestiny(scn.game(), scn.LS, DestinyType.DESTINY_TO_REDUCE_ATTRITION_POWER));
        assertFalse(MayNotDrawDestinyExceptBattleDestinyModifier.mayNotDrawDestiny(scn.game(), scn.LS, DestinyType.BATTLE_DESTINY));
        assertFalse(MayNotDrawDestinyExceptBattleDestinyModifier.mayNotDrawDestiny(scn.game(), scn.DS, DestinyType.DESTINY_TO_REDUCE_ATTRITION_POWER));
        int reserveBefore = scn.GetLSReserveDeckCount();
        scn.LSUseCardAction(scum);
        scn.PassAllResponses();
        assertEquals(reserveBefore, scn.GetLSReserveDeckCount());
    }

    @Test
    public void OccupiedTerritoryWithoutPreventionOpponentDrawsPowerSegmentDestiny() {
        var scn = GetScenario();
        var territory = scn.GetDSCard("territory");
        var trooper = scn.GetDSCard("trooper");
        var dengar = scn.GetDSCard("dengar");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var scum = scn.GetLSCard("scum");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, dengar, vader);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(site, territory);
        scn.MoveCardsToLSHand(scum);
        scn.EnsureDSForcePile(5);
        scn.EnsureLSForcePile(5);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(passUntil(scn, () -> scn.gameState().getBattleState() != null && scn.IsReachedPowerSegment() && lsCan(scn, scum, null)));
        int reserveBefore = scn.GetLSReserveDeckCount();
        scn.LSUseCardAction(scum);
        scn.PassAllResponses();
        assertEquals(reserveBefore - 2, scn.GetLSReserveDeckCount());
    }
}
