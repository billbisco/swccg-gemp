package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Luke Skywalker, defense value (ability) 4
                }},
                new HashMap<>() {{
                    put("greedo", "1283_2");
                    put("luuke", "1283_4"); // an assassin
                    put("vader", "1_168"); // not an assassin
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
    public void GreedoWithBlasterPistolStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("greedo").getBlueprint();
        assertEquals("Greedo With Blaster Pistol", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.RODIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GreedoWithBlasterPistolMakesOtherAssassinsAtSameSitePowerPlus1() {
        var scn = GetScenario();
        var greedo = scn.GetDSCard("greedo");
        var luuke = scn.GetDSCard("luuke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luuke, vader);
        assertEquals(5, scn.GetPower(luuke));
        scn.MoveCardsToLocation(site, greedo);
        assertEquals(6, scn.GetPower(luuke));
        assertEquals(6, scn.GetPower(vader));
        assertEquals(3, scn.GetPower(greedo));
    }

    @Test
    public void GreedoWithBlasterPistolIsImmuneToDisarmed() {
        var scn = GetScenario();
        var greedo = scn.GetDSCard("greedo");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), greedo);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.game().getGameState(), greedo, Title.Disarmed));
    }

    private void initiateBattleWithGreedoAndLuke(VirtualTableScenario scn) {
        var greedo = scn.GetDSCard("greedo");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, greedo, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        if (scn.LSAnyDecisionsAvailable()) {
            scn.LSPass();
        }
    }

    @Test
    public void GreedoWithBlasterPistolFiresForFreeWhenBattleJustInitiatedAndHitsOnDestinyPlus1() {
        var scn = GetScenario();
        var greedo = scn.GetDSCard("greedo");
        var luke = scn.GetLSCard("luke");
        initiateBattleWithGreedoAndLuke(scn);
        assertTrue(scn.DSCardActionAvailable(greedo, "Fire Greedo's Blaster Pistol"));
        int forcePile = scn.GetDSForcePileCount();
        // destiny 4 + 1 = 5 > Luke's defense value 4
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(greedo, "Fire Greedo's Blaster Pistol");
        // Luke is the only target, so it is chosen automatically
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertEquals(forcePile, scn.GetDSForcePileCount());
    }

    @Test
    public void GreedoWithBlasterPistolMissesWhenDestinyPlus1NotAboveDefenseValue() {
        var scn = GetScenario();
        var greedo = scn.GetDSCard("greedo");
        var luke = scn.GetLSCard("luke");
        initiateBattleWithGreedoAndLuke(scn);
        // destiny 3 + 1 = 4, not > 4
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(greedo, "Fire Greedo's Blaster Pistol");
        // Luke is the only target, so it is chosen automatically
        scn.PassAllResponses();
        assertFalse(luke.isHit());
    }

    @Test
    public void GreedoWithBlasterPistolDoesNotFireInWeaponsSegment() {
        var scn = GetScenario();
        var greedo = scn.GetDSCard("greedo");
        initiateBattleWithGreedoAndLuke(scn);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(greedo, "Fire"));
    }
}
