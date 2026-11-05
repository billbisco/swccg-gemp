package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("surveillance", "1273_10");
                    put("office", "1273_18");
                    put("doole", "1273_9");
                    put("operations", "1273_19");
                    put("spice", "1_194");
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
    public void KesselSurveillanceSystemStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("surveillance").getBlueprint();
        assertEquals("Kessel Surveillance System", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private VirtualTableScenario onOffice() {
        var scn = GetScenario();
        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
        var office = scn.GetDSCard("office");
        scn.MoveLocationToTable(office);
        scn.AttachCardsTo(office, scn.GetDSCard("surveillance"));
        return scn;
    }

    @Test
    public void KesselSurveillanceSystemOpponentsForceDrainsMinusOneHere() {
        var scn = onOffice();
        var office = scn.GetDSCard("office");
        scn.MoveCardsToLocation(office, scn.GetLSCard("luke"));
        // Light drains on the two Dark icons, minus one
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), office, scn.LS), scn.epsilon);
    }

    @Test
    public void KesselSurveillanceSystemDeploysSpiceMineAdministratorHereOncePerGame() {
        var scn = onOffice();
        var doole = scn.GetDSCard("doole");
        var surveillance = scn.GetDSCard("surveillance");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(doole);
        scn.DSUseCardAction(surveillance, "Deploy Spice Mine Administrator here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> doole.getZone() == Zone.AT_LOCATION);
        assertEquals(scn.GetDSCard("office"), doole.getAtLocation());
    }

    @Test
    public void KesselSurveillanceSystemSpiceMakesReserveDeckForceSatisfyAnAdditionalForceLoss() {
        var scn = onOffice();
        var operations = scn.GetDSCard("operations");
        var spice = scn.GetDSCard("spice");
        var office = scn.GetDSCard("office");
        scn.MoveCardsToDSSideOfTable(operations);
        scn.StackCardsOn(operations, spice);
        scn.SkipToDSTurn(Phase.CONTROL);
        int reserveBefore = scn.GetDSReserveDeckCount();
        int forcePileBefore = scn.GetDSForcePileCount();
        int usedBefore = scn.GetDSUsedPileCount();
        scn.DSExecuteAdHocEffect(office, new LoseForceEffect(new TopLevelGameTextAction(office, scn.DS, office.getCardId()), scn.DS, 2));
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.DS, "Place a 'spice' card in Lost Pile");
        scn.DSChooseAction("Place a 'spice' card in Lost Pile");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.GetDSLostPile().contains(spice));
        // the 2 Force loss was satisfied by one card from Reserve Deck
        assertEquals(reserveBefore - 1, scn.GetDSReserveDeckCount());
        assertEquals(forcePileBefore, scn.GetDSForcePileCount());
        assertEquals(usedBefore, scn.GetDSUsedPileCount());
    }
}
