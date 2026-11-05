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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("evacuate", "1273_15");
                    put("vader", "1_168"); // Darth Vader, ability 6
                    put("trooper", "1_194"); // ability 1
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
    public void EvacuateVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("evacuate").getBlueprint();
        assertEquals("Evacuate?", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
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

    @Test
    public void EvacuateVUsedTakesAloneCharacterIntoHandDuringMovePhase() {
        var scn = GetScenario();
        var evacuate = scn.GetDSCard("evacuate");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        scn.MoveCardsToDSHand(evacuate);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(evacuate, "Take character into hand"));
        scn.SkipToPhase(Phase.MOVE);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSPlayCard(evacuate, "Take character into hand");
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, vader);
        assertInZone(Zone.USED_PILE, evacuate);
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
    }

    private boolean usedAvailable(String... darkCharacters) {
        var scn = GetScenario();
        var evacuate = scn.GetDSCard("evacuate");
        scn.StartGame();
        scn.MoveCardsToDSHand(evacuate);
        for (String key : darkCharacters) {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard(key));
        }
        scn.SkipToDSTurn(Phase.MOVE);
        return scn.DSCardPlayAvailable(evacuate, "Take character into hand");
    }

    @Test
    public void EvacuateVTargetsOnlyAbilityGreaterThanThreeAlone() {
        assertTrue(usedAvailable("vader"));
        assertFalse(usedAvailable("trooper"));
        assertFalse(usedAvailable("vader", "trooper"));
    }

    @Test
    public void EvacuateVLostPlacesTargetInUsedPileWhenOpponentHasExactlyOneNonSpyThere() {
        var scn = GetScenario();
        var evacuate = scn.GetDSCard("evacuate");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSHand(evacuate);
        scn.MoveCardsToLocation(site, vader, luke);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(evacuate, "Place character in Used Pile"));
        scn.DSPlayCard(evacuate, "Place character in Used Pile");
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, vader);
        assertInZone(Zone.LOST_PILE, evacuate);
    }

    @Test
    public void EvacuateVLostNotAvailableWhenOpponentHasTwoNonSpiesThere() {
        var scn = GetScenario();
        var evacuate = scn.GetDSCard("evacuate");
        scn.StartGame();
        scn.MoveCardsToDSHand(evacuate);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"), scn.GetLSCard("luke"), scn.GetLSCard("obiwan"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(evacuate, "Place character in Used Pile"));
    }
}
