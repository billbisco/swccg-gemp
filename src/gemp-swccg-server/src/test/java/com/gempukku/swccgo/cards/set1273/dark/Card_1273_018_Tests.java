package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lightkessel", "1_126"); // Kessel (Light system)
                }},
                new HashMap<>() {{
                    put("office", "1273_18");
                    put("operations", "1273_19");
                    put("kessel", "1_288"); // Kessel (Dark system)
                    put("trooper", "1_194");
                    put("vader", "1_168");
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
    public void AdministratorsOfficeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("office").getBlueprint();
        assertEquals("Kessel: Spice Mines - Administrator's Office", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.INTERIOR_SITE);
            add(Icon.SCOMP_LINK);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AdministratorsOfficeKesselMayNotBeConverted() {
        var scn = GetScenario();
        var kessel = scn.GetDSCard("kessel");
        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
        scn.MoveLocationToTable(kessel);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_CONVERTED, kessel).isEmpty());
        scn.MoveLocationToTable(scn.GetDSCard("office"));
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_CONVERTED, kessel).isEmpty());
    }

    @Test
    public void AdministratorsOfficeLightLosesOneForceToPlaceSpiceInUsedPileAndDarkTakesACard() {
        var scn = GetScenario();
        var office = scn.GetDSCard("office");
        var operations = scn.GetDSCard("operations");
        var spice = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        scn.StartGame();
        MissionTestSupport.ensurePlanetLayout(scn, "Kessel", 8);
        scn.MoveLocationToTable(office);
        scn.MoveCardsToDSSideOfTable(operations);
        scn.StackCardsOn(operations, spice);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(vader);
        int lsLostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(office, "Place a 'spice' card"));
        scn.LSUseCardAction(office, "Place a 'spice' card");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.GetDSUsedPile().contains(spice));
        assertTrue(scn.GetDSUsedPile().contains(spice));
        assertEquals(lsLostBefore + 1, scn.GetLSLostPileCount());
        LegacyVb73DarkTestSupport.passUntilChoice(scn, scn.DS, "Take any one card into hand");
        scn.DSChoose("Take any one card into hand from Reserve Deck");
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, vader);
    }
}
