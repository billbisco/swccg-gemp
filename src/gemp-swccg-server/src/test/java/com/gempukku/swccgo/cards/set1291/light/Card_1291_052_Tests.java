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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_052_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("learn", "1291_52");
                    put("cody", "200_6");
                }},
                new HashMap<>() {{
                    put("workshop", "6_163");
                    put("slaving", "601_15");
                    put("dockingbay", "7_276");
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
    public void MuchToLearnYouStillHaveStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("learn").getBlueprint();

        assertEquals("Much to Learn, You Still Have", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MuchToLearnYouStillHaveDeploysOnALocation() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(learn);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(learn));
        scn.LSPlayCard(learn);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(Zone.ATTACHED, learn.getZone());
        assertEquals(site, learn.getAttachedTo());
    }

    @Test
    public void MuchToLearnYouStillHaveAddsForceIconsOnABattleground() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        int lightBefore = scn.GetIconCount(site, Icon.LIGHT_FORCE);
        int darkBefore = scn.GetIconCount(site, Icon.DARK_FORCE);
        scn.AttachCardsTo(site, learn);

        assertEquals(lightBefore + 1, scn.GetIconCount(site, Icon.LIGHT_FORCE));
        assertEquals(darkBefore + 1, scn.GetIconCount(site, Icon.DARK_FORCE));
    }

    @Test
    public void MuchToLearnYouStillHaveCancelsOpponentGameTextAndAddsForceGenerationAtNonBattleground() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var workshop = scn.GetDSCard("workshop");

        scn.StartGame();
        scn.MoveLocationToTable(workshop);
        float genBefore = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), workshop, scn.LS);
        scn.AttachCardsTo(workshop, learn);
        float genAfter = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), workshop, scn.LS);
        assertEquals(Math.round(genBefore) + 1, Math.round(genAfter));
    }

    @Test
    public void MuchToLearnYouStillHaveCloneArmyTroopersArePowerPlus1() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var cody = scn.GetLSCard("cody");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cody);
        int before = scn.GetPower(cody);
        scn.AttachCardsTo(site, learn);
        assertEquals(before + 1, scn.GetPower(cody));
    }

    private boolean marksDarkSideGameTextCanceled(VirtualTableScenario scn, PhysicalCardImpl location, PhysicalCardImpl learn) {
        return scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), location, scn.DS).contains(learn);
    }

    private float lsForceGeneration(VirtualTableScenario scn, PhysicalCardImpl location) {
        return scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), location, scn.LS);
    }

    /**
     * Positive control for the two exception tests below: an ordinary opponent's non-battleground planet site
     * (Jabba's Palace: Droid Workshop, 1 Dark icon only) has its Dark side game text canceled.
     */
    @Test
    public void MuchToLearnYouStillHaveCancelsOpponentGameTextAtOrdinaryNonBattlegroundPlanetSite() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var workshop = scn.GetDSCard("workshop");

        scn.StartGame();
        scn.MoveLocationToTable(workshop);
        assertFalse(scn.HasKeyword(workshop, Keyword.DOCKING_BAY));
        assertFalse(marksDarkSideGameTextCanceled(scn, workshop, learn));
        float genBefore = lsForceGeneration(scn, workshop);
        scn.AttachCardsTo(workshop, learn);
        assertTrue(marksDarkSideGameTextCanceled(scn, workshop, learn));
        assertEquals(Math.round(genBefore) + 1, Math.round(lsForceGeneration(scn, workshop)));
    }

    @Test
    public void MuchToLearnYouStillHaveDoesNotCancelGameTextAtOpponentsKashyyykLocation() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var slaving = scn.GetDSCard("slaving");

        scn.StartGame();
        scn.MoveLocationToTable(slaving);
        // Kashyyyk: Slaving Camp Headquarters is an opponent's non-battleground planet site (2 Dark icons, no Light icon)
        assertEquals(2, scn.GetIconCount(slaving, Icon.DARK_FORCE));
        assertEquals(0, scn.GetIconCount(slaving, Icon.LIGHT_FORCE));
        assertEquals("Kashyyyk", slaving.getPartOfSystem());
        float genBefore = lsForceGeneration(scn, slaving);
        scn.AttachCardsTo(slaving, learn);
        assertFalse(marksDarkSideGameTextCanceled(scn, slaving, learn));
        assertEquals(Math.round(genBefore), Math.round(lsForceGeneration(scn, slaving)));
    }

    @Test
    public void MuchToLearnYouStillHaveDoesNotCancelGameTextAtOpponentsDockingBay() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var dockingBay = scn.GetDSCard("dockingbay");

        scn.StartGame();
        scn.MoveLocationToTable(dockingBay);
        // Coruscant: Docking Bay is an opponent's non-battleground planet site (1 Dark icon only) that is a docking bay
        assertEquals(1, scn.GetIconCount(dockingBay, Icon.DARK_FORCE));
        assertEquals(0, scn.GetIconCount(dockingBay, Icon.LIGHT_FORCE));
        assertTrue(scn.HasKeyword(dockingBay, Keyword.DOCKING_BAY));
        float genBefore = lsForceGeneration(scn, dockingBay);
        scn.AttachCardsTo(dockingBay, learn);
        assertFalse(marksDarkSideGameTextCanceled(scn, dockingBay, learn));
        assertEquals(Math.round(genBefore), Math.round(lsForceGeneration(scn, dockingBay)));
    }
}
