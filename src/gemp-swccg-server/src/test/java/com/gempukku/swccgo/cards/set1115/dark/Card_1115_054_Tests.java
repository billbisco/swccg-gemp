package com.gempukku.swccgo.cards.set1115.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_054_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("looksir", "1115_54");
                    put("trooper", "1_194");
                    put("probe", "3_90");
                    put("presence", "12_118");
                    put("battleorder", "8_118");
                    put("bikergear", "8_119");
                    put("crossfire", "8_121");
                    put("moseisley", "1_295");
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
    public void LookSirDroidsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("looksir").getBlueprint();

        assertEquals("Look Sir, Droids", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LookSirDroidsPlacesACardFromHandOnUsedPile() {
        var scn = GetScenario();
        var looksir = scn.GetDSCard("looksir");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSPass();
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToDSHand(looksir, trooper);
        scn.SkipToPhase(Phase.BATTLE);

        assertTrue(scn.DSCardPlayAvailable(looksir, "Place a card"));
        scn.DSPlayCard(looksir, "Place a card");
        scn.PassAllResponses();
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, trooper);
        assertInZone(Zone.USED_PILE, looksir);
    }

    @Test
    public void LookSirDroidsTakesNonPresenceNonPermanentWeaponDroidFromReserve() {
        var scn = GetScenario();
        var looksir = scn.GetDSCard("looksir");
        var probe = scn.GetDSCard("probe");
        var presence = scn.GetDSCard("presence");

        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSPass();
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToDSHand(looksir);
        scn.MoveCardsToTopOfDSReserveDeck(probe, presence);
        scn.SkipToPhase(Phase.BATTLE);

        assertTrue(scn.DSCardPlayAvailable(looksir, "Take droid"));
        scn.DSPlayCard(looksir, "Take droid");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to take into hand"));
        assertTrue(scn.DSHasCardChoiceAvailable(probe));
        String[] blueprintIds = scn.DSGetADParam("blueprintId");
        String[] selectable = scn.DSGetADParam("selectable");
        boolean presenceSelectable = false;
        for (int i = 0; i < blueprintIds.length; i++) {
            if (presence.getBlueprintId(true).equals(blueprintIds[i])
                    && "true".equalsIgnoreCase(selectable[i])) {
                presenceSelectable = true;
            }
        }
        assertFalse(presenceSelectable);
        scn.DSChooseCard(probe);
        scn.PassAllResponses();

        assertInHand(probe);
        assertInZone(Zone.USED_PILE, looksir);
        assertTrue(presence.getZone() == Zone.RESERVE_DECK || presence.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }

    @Test
    public void LookSirDroidsStartingDeploysTwoAlterImmuneEffectsAndMosEisley() {
        var scn = GetScenario();
        var looksir = scn.GetDSCard("looksir");
        var battleorder = scn.GetDSCard("battleorder");
        var bikergear = scn.GetDSCard("bikergear");
        var crossfire = scn.GetDSCard("crossfire");
        var moseisley = scn.GetDSCard("moseisley");

        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSChooseCard(looksir);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(battleorder));
        assertTrue(scn.DSHasCardChoiceAvailable(bikergear));
        assertTrue(scn.DSHasCardChoiceAvailable(crossfire));
        scn.DSChooseCard(battleorder);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(bikergear));
        scn.DSChooseCard(bikergear);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("which side"));
        scn.DSChoose("Left");
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, battleorder);
        assertInZone(Zone.SIDE_OF_TABLE, bikergear);
        assertEquals(Zone.LOCATIONS, moseisley.getZone());
        assertInHand(looksir);
    }
}
