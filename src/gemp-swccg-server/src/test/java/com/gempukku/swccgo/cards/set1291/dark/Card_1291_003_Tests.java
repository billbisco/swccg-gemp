package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Luke Skywalker: ability 4, forfeit 7
                    put("rex", "200_3"); // Rex ([Clone Army])
                }},
                new HashMap<>() {{
                    put("dsd", "1291_3");
                    put("dsd2", "1291_3");
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
    public void DSD1DwarfSpiderDroidStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dsd").getBlueprint();
        assertEquals("DSD1 Dwarf Spider Droid", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.SPIDER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.PRESENCE);
            add(Icon.SEPARATIST);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleAtDSSite(VirtualTableScenario scn) {
        var site = scn.GetDSStartingLocation();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
    }

    @Test
    public void DSD1DwarfSpiderDroidDrawsBattleDestinyWhileWithCloneArmyCard() {
        var scn = GetScenario();
        var dsd = scn.GetDSCard("dsd");
        var rex = scn.GetLSCard("rex");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dsd, rex);
        battleAtDSSite(scn);
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DSD1DwarfSpiderDroidDrawsBattleDestinyWhileWithAnotherSpiderDroid() {
        var scn = GetScenario();
        var dsd = scn.GetDSCard("dsd");
        var dsd2 = scn.GetDSCard("dsd2");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dsd, dsd2, luke);
        battleAtDSSite(scn);
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DSD1DwarfSpiderDroidDrawsNoBattleDestinyAloneWithoutCloneArmyCard() {
        var scn = GetScenario();
        var dsd = scn.GetDSCard("dsd");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dsd, luke);
        battleAtDSSite(scn);
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }

    private void fireSelfDestructAtLuke(VirtualTableScenario scn, int destiny) {
        var dsd = scn.GetDSCard("dsd");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, dsd, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(dsd, "Fire"));
        scn.PrepareDSDestiny(destiny);
        scn.DSUseCardAction(dsd, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
    }

    @Test
    public void DSD1DwarfSpiderDroidSelfDestructHitsTargetWithForfeitMinus4AndAlsoHitsItself() {
        var scn = GetScenario();
        var dsd = scn.GetDSCard("dsd");
        var luke = scn.GetLSCard("luke");
        // destiny 3 + 2 = 5 > Luke's defense value 4
        fireSelfDestructAtLuke(scn, 3);
        assertTrue(luke.isHit());
        assertEquals(3, scn.GetForfeit(luke));
        assertTrue(dsd.isHit());
    }

    @Test
    public void DSD1DwarfSpiderDroidSelfDestructMissesWhenDestinyPlus2NotAboveDefenseValue() {
        var scn = GetScenario();
        var dsd = scn.GetDSCard("dsd");
        var luke = scn.GetLSCard("luke");
        // destiny 2 + 2 = 4, not > Luke's defense value 4
        fireSelfDestructAtLuke(scn, 2);
        assertFalse(luke.isHit());
        assertFalse(dsd.isHit());
    }
}
