package com.gempukku.swccgo.cards.set1263.dark;

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

public class Card_1263_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("sandwhirl", "6_57");
                }},
                new HashMap<>() {{
                    put("pirate", "1263_6");
                    put("atat", "3_155");
                    put("db94", "1_291");
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
    public void MercenaryShadowPirateStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pirate").getBlueprint();

        assertEquals("Mercenary Shadow Pirate", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ISB_AGENT);
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MercenaryShadowPirateAddsTwoToPowerOfAnythingShePilots() {
        var scn = GetScenario();
        var pirate = scn.GetDSCard("pirate");
        var atat = scn.GetDSCard("atat");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atat);
        int power = scn.GetPower(atat);
        scn.BoardAsPilot(atat, pirate);
        assertEquals(power + 2, scn.GetPower(atat));
    }

    @Test
    public void MercenaryShadowPirateOpponentMayNotAddDestinyDrawsToPowerOrAttritionHere() {
        var scn = GetScenario();
        var pirate = scn.GetDSCard("pirate");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        //test1: opponent may not add destiny draws to power or attrition during battle here
        //test2: Dark Side still may
        scn.StartGame();
        scn.MoveCardsToLocation(site, pirate, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToPower(scn.gameState(), scn.LS));
        scn.DSInitiateBattle(site);
        assertTrue(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToPower(scn.gameState(), scn.LS)); //test1
        assertTrue(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToAttrition(scn.gameState(), scn.LS)); //test1
        assertFalse(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToPower(scn.gameState(), scn.DS)); //test2
        assertFalse(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToAttrition(scn.gameState(), scn.DS)); //test2
    }

    @Test
    public void MercenaryShadowPirateDoesNotStopDestinyDrawsInBattleElsewhere() {
        var scn = GetScenario();
        var pirate = scn.GetDSCard("pirate");
        var luke = scn.GetLSCard("luke");
        var atat = scn.GetDSCard("atat");
        var site = scn.GetDSStartingLocation();
        var otherSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, pirate);
        scn.MoveCardsToLocation(otherSite, atat, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(otherSite);
        assertFalse(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToPower(scn.gameState(), scn.LS));
        assertFalse(scn.game().getModifiersQuerying().mayNotAddDestinyDrawsToAttrition(scn.gameState(), scn.LS));
    }

    @Test
    public void MercenaryShadowPirateCancelsSandwhirlAtRelatedLocation() {
        var scn = GetScenario();
        var pirate = scn.GetDSCard("pirate");
        var sandwhirl = scn.GetLSCard("sandwhirl");
        var db94 = scn.GetDSCard("db94");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, pirate);
        scn.AttachCardsTo(marketplace, sandwhirl);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertInZone(Zone.LOST_PILE, sandwhirl);
    }

    @Test
    public void MercenaryShadowPirateDoesNotCancelSandwhirlAtUnrelatedLocation() {
        var scn = GetScenario();
        var pirate = scn.GetDSCard("pirate");
        var sandwhirl = scn.GetLSCard("sandwhirl");
        var marketplace = scn.GetDSStartingLocation();
        var cloudCity = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(cloudCity, pirate);
        scn.AttachCardsTo(marketplace, sandwhirl);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(marketplace, sandwhirl.getAttachedTo());
    }
}
