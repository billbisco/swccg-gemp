package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.LimitForceGenerationModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1263_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("assault", "1_113");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("storm", "1263_16");
                    put("bespin", "5_164");
                    put("chamber", "5_166");
                    put("tie", "1271_29");
                    put("pilot", "1271_6");
                    put("premiertie", "1_304");
                    put("trooper", "1_194");
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
    public void StormCloudsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("storm").getBlueprint();

        assertEquals("Storm Clouds", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SECTOR, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLOUD_SECTOR);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.CLOUD_CITY);
            add(Icon.DARK_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void putStormOnBespin(VirtualTableScenario scn) {
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("storm"));
        scn.MoveLocationToTable(scn.GetDSCard("chamber"));
    }

    @Test
    public void StormCloudsForceDrainsPlusOneWhenDarkControlsRelatedBattlegroundSite() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var chamber = scn.GetDSCard("chamber");
        var tie = scn.GetDSCard("premiertie");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, tie);
        scn.MoveCardsToLocation(chamber, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSForceDrainAvailable(storm));
        scn.DSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void StormCloudsForceDrainsUnmodifiedWithoutRelatedBattlegroundControl() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var tie = scn.GetDSCard("premiertie");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, tie);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSForceDrainAvailable(storm));
        scn.DSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void StormCloudsForceDrainMayNotBeCanceledWhenDarkControlsRelatedBattlegroundSite() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var chamber = scn.GetDSCard("chamber");
        var tie = scn.GetDSCard("premiertie");
        var trooper = scn.GetDSCard("trooper");
        var assault = scn.GetLSCard("assault");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, tie);
        scn.MoveCardsToLocation(chamber, trooper);
        scn.MoveCardsToLSHand(assault);
        scn.EnsureLSForcePile(2);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        assertFalse(scn.LSCardPlayAvailable(assault, "Cancel Force drain"));
    }

    @Test
    public void StormCloudsForceDrainMayBeCanceledWithoutRelatedBattlegroundControl() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var tie = scn.GetDSCard("premiertie");
        var assault = scn.GetLSCard("assault");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, tie);
        scn.MoveCardsToLSHand(assault);
        scn.EnsureLSForcePile(2);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        assertTrue(scn.LSCardPlayAvailable(assault, "Cancel Force drain"));
    }

    @Test
    public void StormCloudsForceGenerationAtRelatedSystemMayNotBeLimited() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var bespin = scn.GetDSCard("bespin");
        var tie = scn.GetDSCard("premiertie");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, tie);
        float unlimited = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), bespin, scn.DS);
        scn.ApplyAdHocModifier(new LimitForceGenerationModifier(trooper, Filters.sameCardId(bespin), 1, scn.DS));
        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), bespin, scn.DS);
        assertEquals(unlimited, after, scn.epsilon);
    }

    @Test
    public void StormCloudsLightForceDrainsMinusOneUnlessLightControlsARelatedSite() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(storm));
        scn.LSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    @Test
    public void StormCloudsLightForceDrainsUnmodifiedWhenLightControlsARelatedSite() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var chamber = scn.GetDSCard("chamber");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        putStormOnBespin(scn);
        scn.MoveCardsToLocation(storm, xwing);
        scn.MoveCardsToLocation(chamber, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(storm));
        scn.LSForceDrainAt(storm);
        assertTrue(scn.IsActiveForceDrain());
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void StormCloudsSubtractsOneFromLightTotalBattleDestinyPerRestrictedThreeTIEPresent() {
        var scn = GetScenario();
        var storm = scn.GetDSCard("storm");
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        putStormOnBespin(scn);
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(storm, tie, xwing, luke);
        scn.BoardAsPilot(tie, scn.GetDSCard("pilot"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(4);
        scn.LSInitiateBattle(storm);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("optional");
        scn.PassResponses("BEFORE_BATTLE_DESTINY");
        scn.PassDestinyDrawResponses();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(3, scn.GetLSTotalDestiny(), scn.epsilon);
    }
}
