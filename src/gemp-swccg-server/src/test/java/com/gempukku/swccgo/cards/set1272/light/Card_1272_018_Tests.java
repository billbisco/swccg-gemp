package com.gempukku.swccgo.cards.set1272.light;

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

public class Card_1272_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tarfful", "1272_18");
                    put("wookiee", "7_50");
                    put("luke", "1_19");
                    put("forest", "215_13");
                    put("junk", "1_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void TarffulWookieeInsurgentStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tarfful").getBlueprint();

        assertEquals("Tarfful, Wookiee Insurgent", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("Leader", card.getLore());
    }

    @Test
    public void TarffulOccupyOnForestDepthsIsTreatedAsControl() {
        var scn = GetScenario();
        var tarfful = scn.GetLSCard("tarfful");
        var forest = scn.GetLSCard("forest");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, tarfful, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(forest));
        scn.LSForceDrainAt(forest);
        scn.PassForceDrainStartResponses();
        assertTrue(scn.GetForceDrainTotal() >= 1);
    }

    @Test
    public void TarffulOccupyOnForestDepthsIsTreatedAsControlWhileTarffulIsElsewhere() {
        var scn = GetScenario();
        var tarfful = scn.GetLSCard("tarfful");
        var forest = scn.GetLSCard("forest");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, luke, trooper);
        scn.MoveCardsToLocation(site, tarfful);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(forest));
    }

    @Test
    public void OccupyOnForestDepthsIsNotControlWithoutTarffulOnTable() {
        var scn = GetScenario();
        var forest = scn.GetLSCard("forest");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(forest, luke, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(forest));
    }

    @Test
    public void TarffulOccupyAsControlIsOnlyForestDepths() {
        var scn = GetScenario();
        var tarfful = scn.GetLSCard("tarfful");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarfful, trooper);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(site));
    }

    @Test
    public void TarffulNonUniqueWookieesHereAreHeroic() {
        var scn = GetScenario();
        var tarfful = scn.GetLSCard("tarfful");
        var wookiee = scn.GetLSCard("wookiee");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarfful, wookiee, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), wookiee, Keyword.HEROIC));
        assertFalse(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), luke, Keyword.HEROIC));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(wookiee));
        assertFalse(scn.DSHasCardChoiceAvailable(luke));
    }

    @Test
    public void TarffulWhenDeployedMayRoarToTakeOneCardFromUsedPileAndDoesNotOfferStackWithoutGrrrghrrrgh() {
        var scn = GetScenario();
        var tarfful = scn.GetLSCard("tarfful");
        var junk = scn.GetLSCard("junk");
        var wookiee = scn.GetLSCard("wookiee");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(tarfful, wookiee);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSUsedPile(junk, luke);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(tarfful));
        scn.LSDeployCard(tarfful);
        scn.LSChooseCard(site);
        scn.PassResponses("Use");
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(tarfful, "Roar"));
        scn.LSUseCardAction(tarfful, "Roar");
        assertTrue(scn.LSDecisionAvailable("Take one card into hand from Used Pile"));
        scn.LSChooseYes();
        assertTrue(scn.LSHasCardChoiceAvailable(junk));
        scn.LSChooseCard(junk);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, junk);
        assertFalse(scn.LSDecisionAvailable("Stack a Wookiee"));
    }
}
