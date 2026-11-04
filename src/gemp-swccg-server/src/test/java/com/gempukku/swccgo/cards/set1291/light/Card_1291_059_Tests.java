package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_059_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rocky", "1291_59");
                    put("cody", "200_6");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dsrocky", "1291_18");
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
    public void GeonosisRockyPlainsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rocky").getBlueprint();

        assertEquals("Geonosis: Rocky Plains", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void GeonosisRockyPlainsLightTotalPowerIsPlus1ForEachCloneArmyCardPresent() {
        var scn = GetScenario();
        var rocky = scn.GetLSCard("rocky");
        var cody = scn.GetLSCard("cody");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(rocky);
        scn.MoveCardsToLocation(rocky, cody, luke, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(rocky);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        int printed = scn.GetPower(cody) + scn.GetPower(luke);
        assertEquals(printed + 1, scn.GetLSTotalPower());
    }

    @Test
    public void GeonosisRockyPlainsDarkMayRaiseConvertedRockyPlainsWhenControlling() {
        var scn = GetScenario();
        var rocky = scn.GetLSCard("rocky");
        var dsRocky = scn.GetDSCard("dsrocky");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        convertDarkRockyPlainsWithLightCopy(scn);
        scn.MoveCardsToLocation(rocky, vader);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());

        assertTrue(scn.DSCardActionAvailable(rocky, "Raise converted Rocky Plains"));
        scn.DSUseCardAction(rocky, "Raise converted Rocky Plains");
        scn.DSChooseCard(rocky);
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, dsRocky.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, rocky.getZone());
    }

    @Test
    public void GeonosisRockyPlainsDarkMayNotRaiseConvertedRockyPlainsWhenNotControlling() {
        var scn = GetScenario();
        var rocky = scn.GetLSCard("rocky");
        var dsRocky = scn.GetDSCard("dsrocky");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        convertDarkRockyPlainsWithLightCopy(scn);
        scn.MoveCardsToLocation(rocky, vader, luke);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());

        assertFalse(scn.DSActionAvailable("Raise converted Rocky Plains"));
        assertEquals(Zone.CONVERTED_LOCATIONS, dsRocky.getZone());
    }

    private void convertDarkRockyPlainsWithLightCopy(VirtualTableScenario scn) {
        var rocky = scn.GetLSCard("rocky");
        var dsRocky = scn.GetDSCard("dsrocky");
        scn.MoveLocationToTable(dsRocky);
        scn.MoveCardsToHand(rocky);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(rocky));
        scn.LSDeployCard(rocky);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, rocky.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dsRocky.getZone());
    }
}
