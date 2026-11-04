package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("warroom", "1281_43");
                    put("yavin", "1_135");
                    put("handcard", "1_28");
                    put("forcecard", "1_17");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("dantooine", "1_282");
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
    public void Yavin4MassassiWarRoomVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("warroom").getBlueprint();

        assertEquals(Title.Yavin_4_War_Room, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.WAR_ROOM);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Yavin4MassassiWarRoomVOncePerGameDeploysRebelAcesAndExchangesACard() {
        var scn = GetScenario();
        var warroom = scn.GetLSCard("warroom");
        var yavin = scn.GetLSCard("yavin");
        var handcard = scn.GetLSCard("handcard");
        var forcecard = scn.GetLSCard("forcecard");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(warroom);
        var aces = MissionTestSupport.putInReserve(scn, 98401, scn.LS, Vb81RestTestSupport.lightEffect("Rebel Aces"));
        scn.MoveCardsToLSHand(handcard);
        scn.LSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSForcePile(forcecard);

        assertTrue(scn.LSCardActionAvailable(warroom, "Deploy Rebel Aces and/or exchange card"));
        scn.LSUseCardAction(warroom, "Deploy Rebel Aces and/or exchange card");
        scn.LSChooseOption("Both");
        for (int i = 0; i < 20; ++i) {
            if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(aces)) {
                scn.LSChooseCard(aces);
            }
            else if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(handcard)) {
                scn.LSChooseCard(handcard);
            }
            else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                break;
            }
        }
        assertEquals(Zone.SIDE_OF_TABLE, aces.getZone());
        assertInHand(forcecard);
        assertTrue(Vb81RestTestSupport.inZone(handcard, Zone.FORCE_PILE));
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, warroom, "Deploy Rebel Aces and/or exchange card"));
    }

    @Test
    public void Yavin4MassassiWarRoomVLimitsDarkSideForceLossFromDrainsAtNonLiberatedSystems() {
        var scn = GetScenario();
        var warroom = scn.GetLSCard("warroom");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, yavin).stream().anyMatch(m -> m.isForPlayer(scn.DS)));
        scn.MoveLocationToTable(warroom);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, yavin).stream().anyMatch(m -> m.isForPlayer(scn.DS)));
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, warroom).stream().anyMatch(m -> m.isForPlayer(scn.DS)));
    }
}
