package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1274_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("oom", "1274_2");
                    put("aat", "14_121");      // Armored Attack Tank: [Theed Palace] vehicle
                    put("openfire", "14_99");  // Open Fire!
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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

    private static boolean isSelectable(VirtualTableScenario scn, String player, PhysicalCardImpl card) {
        String[] blueprints = scn.GetADParam(player, "blueprintId");
        String[] selectable = scn.GetADParam(player, "selectable");
        if (blueprints == null) {
            return false;
        }
        for (int i = 0; i < blueprints.length; i++) {
            if (card.getBlueprintId(true).equals(blueprints[i]) && (selectable == null || Boolean.parseBoolean(selectable[i]))) {
                return true;
            }
        }
        return false;
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Test
    public void OOMCommandBattleDroidStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("oom").getBlueprint();

        assertEquals("OOM Command Battle Droid", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getPower(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{ add(ModelType.BATTLE); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.PILOT);
            add(Icon.PRESENCE);
            add(Icon.EPISODE_I);
            add(Icon.THEED_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OOMCommandBattleDroidAddsTwoToPowerOfVehicleHePilots() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var aat = scn.GetDSCard("aat");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aat);
        scn.BoardAsPilot(aat, oom);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.IsAboardAsPilot(aat, oom));
        assertEquals(aat.getBlueprint().getPower() + 2, scn.GetPower(aat), scn.epsilon);
    }

    @Test
    public void OOMCommandBattleDroidAddsOneDarkSideIconWhilePilotingTheedPalaceVehicleAtBattlegroundSite() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var aat = scn.GetDSCard("aat");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aat);
        int before = scn.GetIconCount(site, Icon.DARK_FORCE);
        scn.BoardAsPilot(aat, oom);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(before + 1, scn.GetIconCount(site, Icon.DARK_FORCE));
    }

    @Test
    public void OOMCommandBattleDroidAddsNoIconWhenNotPiloting() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        int before = scn.GetIconCount(site, Icon.DARK_FORCE);
        scn.MoveCardsToLocation(site, oom);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(before, scn.GetIconCount(site, Icon.DARK_FORCE));
    }

    @Test
    public void OOMCommandBattleDroidMayAddOneToForceDrainHereWhilePilotingTheedPalaceVehicle() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var aat = scn.GetDSCard("aat");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aat);
        scn.BoardAsPilot(aat, oom);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(site));
        scn.DSForceDrainAt(site);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(oom, "Add 1 to Force drain"));
        scn.DSUseCardAction(oom, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertTrue(scn.IsActiveForceDrain());
        assertEquals(scn.GetIconCount(site, Icon.LIGHT_FORCE) + 1, scn.GetForceDrainTotal());
    }

    @Test
    public void OOMCommandBattleDroidMayNotAddToForceDrainWhenNotPiloting() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, oom);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(site);
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(oom, "Add 1 to Force drain"));
    }

    @Test
    public void OOMCommandBattleDroidOncePerGameTakesCardWithFireInTitleIntoHand() {
        var scn = GetScenario();
        var oom = scn.GetDSCard("oom");
        var openfire = scn.GetDSCard("openfire");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, oom);
        scn.MoveCardsToBottomOfDSReserveDeck(openfire, trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(oom, "Take card into hand from Reserve Deck"));
        scn.DSUseCardAction(oom, "Take card into hand from Reserve Deck");
        assertTrue(scn.DSHasCardChoiceAvailable(openfire));
        assertFalse(isSelectable(scn, scn.DS, trooper));
        scn.DSChooseCard(openfire);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, openfire);
        assertFalse(dsCan(scn, oom, "Take card into hand from Reserve Deck"));
    }
}
