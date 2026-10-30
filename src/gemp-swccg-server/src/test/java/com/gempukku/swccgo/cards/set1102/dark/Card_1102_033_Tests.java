package com.gempukku.swccgo.cards.set1102.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("tagge", "1102_33");
                    put("stormtrooper", "1_194");
                    put("expand", "1_215");
                    put("decree", "5_120");
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
    public void GeneralTaggeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tagge").getBlueprint();

        assertEquals("General Tagge", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GENERAL);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GeneralTaggeTakesImperialTrooperWithoutArmorDuringControlPhase() {
        var scn = GetScenario();
        var tagge = scn.GetDSCard("tagge");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tagge);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(stormtrooper);

        assertTrue(scn.DSCardActionAvailable(tagge, "Take Imperial trooper into hand"));
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSUseCardAction(tagge, "Take Imperial trooper into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(stormtrooper);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, stormtrooper.getZone());
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Take Imperial trooper into hand"));
    }

    @Test
    public void GeneralTaggePlacesNonImmuneToAlterEffectsInUsedPileOncePerGame() {
        var scn = GetScenario();
        var tagge = scn.GetDSCard("tagge");
        var expand = scn.GetDSCard("expand");
        var decree = scn.GetDSCard("decree");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tagge);
        scn.AttachCardsTo(site, expand);
        scn.MoveCardsToDSSideOfTable(decree);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(tagge, "Place Effects in Used Pile"));
        scn.DSUseCardAction(tagge, "Place Effects in Used Pile");
        scn.PassAllResponses();
        assertTrue(expand.getZone() == Zone.USED_PILE || expand.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(Zone.SIDE_OF_TABLE, decree.getZone());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Place Effects in Used Pile"));
    }
}
