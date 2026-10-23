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
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1210_079_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bantha", "1210_79");
                    put("tusken", "1_196");
                    put("herd", "7_221");
                    put("greedo", "2_089");
                    put("scum", "6_149");
                    put("dewback", "1210_80");
                    put("retrieve1", "1_194");
                    put("retrieve2", "1_307");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DSStartingLocation("6_162"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetMarketplaceScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bantha", "1210_79");
                    put("tusken", "1_196");
                    put("herd", "7_221");
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
    public void BanthaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetMarketplaceScenario();
        var card = scn.GetDSCard("bantha").getBlueprint();

        assertEquals("Bantha", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertNull(card.getArmor());
        assertEquals(2, card.getManeuver(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.CREATURE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BANTHA);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BanthaHasAbilityOneHalfAndMayMoveAsReact() {
        var scn = GetMarketplaceScenario();
        var bantha = scn.GetDSCard("bantha");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bantha);
        assertEquals(0.5, scn.game().getModifiersQuerying().getAbility(scn.gameState(), bantha), scn.epsilon);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(
                scn.gameState(), ModifierType.MAY_MOVE_AS_REACT_TO_LOCATION, bantha).isEmpty());
    }

    @Test
    public void BanthaMakesTuskenRaidersHerePowerPlusOne() {
        var scn = GetMarketplaceScenario();
        var bantha = scn.GetDSCard("bantha");
        var tusken = scn.GetDSCard("tusken");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tusken);
        assertEquals(1, scn.GetPower(tusken));
        scn.MoveCardsToLocation(site, bantha);
        assertEquals(2, scn.GetPower(tusken));
    }

    @Test
    public void BanthaDeploysBanthaHerdFromReserveDeckDuringDeploy() {
        var scn = GetMarketplaceScenario();
        var bantha = scn.GetDSCard("bantha");
        var herd = scn.GetDSCard("herd");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bantha);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(herd);
        assertTrue(scn.DSCardActionAvailable(bantha, "Deploy Bantha Herd"));
        scn.DSUseCardAction(bantha, "Deploy Bantha Herd");
        scn.PassAllResponses();
        scn.DSChooseCard(herd);
        scn.PassAllResponses();
        if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                && scn.DSGetDecision().getText().toLowerCase().contains("direction")) {
            scn.DSChoose("Left");
            scn.PassAllResponses();
        }
        assertTrue(herd.getAttachedTo() != null || herd.getAtLocation() != null);
        assertAtLocation(site, bantha);
    }

    @Test
    public void BanthaLetsScumAndVillainyIgnoreCreatureVehiclesWhenRetrieving() {
        var scn = GetScenario();
        var bantha = scn.GetDSCard("bantha");
        var greedo = scn.GetDSCard("greedo");
        var scum = scn.GetDSCard("scum");
        var retrieve1 = scn.GetDSCard("retrieve1");
        var retrieve2 = scn.GetDSCard("retrieve2");
        var luke = scn.GetLSCard("luke");
        var ac = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(ac, scum);
        scn.MoveCardsToLocation(ac, bantha, greedo, luke);
        scn.MoveCardsToTopOfDSLostPile(retrieve1, retrieve2);
        assertEquals(2, scn.GetDSLostPileCount());
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(ac);
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSLostPileCount());
        assertTrue(retrieve1.getZone() == Zone.USED_PILE || retrieve1.getZone() == Zone.TOP_OF_USED_PILE
                || retrieve2.getZone() == Zone.USED_PILE || retrieve2.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void BanthaAbsenceLeavesDewbackAbilityBreakingScumRetrieval() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("dewback");
        var greedo = scn.GetDSCard("greedo");
        var scum = scn.GetDSCard("scum");
        var retrieve1 = scn.GetDSCard("retrieve1");
        var retrieve2 = scn.GetDSCard("retrieve2");
        var luke = scn.GetLSCard("luke");
        var ac = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(ac, scum);
        scn.MoveCardsToLocation(ac, dewback, greedo, luke);
        scn.MoveCardsToTopOfDSLostPile(retrieve1, retrieve2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(ac);
        scn.PassAllResponses();
        assertEquals(2, scn.GetDSLostPileCount());
    }
}
