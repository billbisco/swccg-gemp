package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1263_008_Tests {
    private VirtualTableScenario scenario() { return scenario("legacy"); }

    private VirtualTableScenario scenario(String format) {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("r2", "2_14"); }},
                new HashMap<>() {{ put("droid", "1263_8"); put("trooper", "1_194"); put("vader", "1_168"); put("heroic", "1116_51"); put("rifle", "1_317"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, format);
    }
    @Test
    public void SecurityBattleDroidStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("droid").getBlueprint();
        assertEquals("Security Battle Droid", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon); assertEquals(3, bp.getArmor(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.DROID); add(Icon.EPISODE_I); add(Icon.PRESENCE); add(Icon.CORUSCANT); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.SECURITY_BATTLE_DROID); }});
        scn.BlueprintModelTypeCheck(bp, new ArrayList<>() {{ add(ModelType.BATTLE); }});
    }

    @Test
    public void SecurityBattleDroidRemovesHeroicFromBothPlayersCharactersAtItsSite() {
        var scn = scenario(); var site = scn.GetDSStartingLocation(); var droid = scn.GetDSCard("droid");
        var hero = scn.GetDSCard("heroic"); var r2 = scn.GetLSCard("r2");
        scn.StartGame(); scn.MoveCardsToLocation(site, droid, hero, r2);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.HasKeyword(hero, Keyword.HEROIC)); assertFalse(scn.HasKeyword(r2, Keyword.HEROIC));
        scn.MoveCardsToDSHand(droid);
        assertTrue(scn.HasKeyword(hero, Keyword.HEROIC)); assertTrue(scn.HasKeyword(r2, Keyword.HEROIC));
        scn.MoveCardsToLocation(site, droid);
        assertFalse(scn.HasKeyword(hero, Keyword.HEROIC)); assertFalse(scn.HasKeyword(r2, Keyword.HEROIC));
    }

    @Test
    public void SecurityBattleDroidDoesNotRemoveHeroicAtAnotherSite() {
        var scn = scenario(); var droid = scn.GetDSCard("droid"); var hero = scn.GetDSCard("heroic");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), hero, scn.GetLSCard("r2"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.HasKeyword(hero, Keyword.HEROIC)); assertTrue(scn.HasKeyword(scn.GetLSCard("r2"), Keyword.HEROIC));
    }

    @Test
    public void SecurityBattleDroidAlsoRemovesHeroicGrantedToItself() {
        var scn = scenario(); var droid = scn.GetDSCard("droid");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid);
        scn.ApplyAdHocModifier(new KeywordModifier(droid, Keyword.HEROIC)); scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.HasKeyword(droid, Keyword.HEROIC));
    }

    private VirtualTableScenario weapon(boolean withDroid) {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("r2"), scn.GetLSCard("luke"));
        if (withDroid) scn.MoveCardsToLocation(site, scn.GetDSCard("droid"));
        scn.AttachCardsTo(scn.GetDSCard("trooper"), scn.GetDSCard("rifle"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.DSUseCardAction(scn.GetDSCard("rifle"), "Fire");
        return scn;
    }
    @Test
    public void SecurityBattleDroidLetsWeaponsTargetNonHeroicAlongsideFormerHeroic() {
        var scn = weapon(true);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("r2")));
    }
    @Test
    public void SecurityBattleDroidAbsentLeavesHeroicTargetingRestrictionInForce() {
        var scn = weapon(false);
        assertTrue(scn.DSHasCardChoiceNotAvailable(scn.GetLSCard("luke")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("r2")));
    }
    private VirtualTableScenario battle(String companion) {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetLSCard("luke"));
        if (companion != null) scn.MoveCardsToLocation(site, scn.GetDSCard(companion));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareDSDestiny(3); scn.PrepareLSDestiny(1);
        scn.LSInitiateBattle(site); scn.SkipToEndOfPowerSegment(true);
        return scn;
    }
    @Test
    public void SecurityBattleDroidDrawsOneDestinyWithCompanionDespiteInsufficientAbility() {
        var scn = battle("trooper"); assertEquals(3, scn.GetDSTotalDestiny(), scn.epsilon);
    }
    @Test
    public void SecurityBattleDroidDrawsNoDestinyWhenAloneDespiteEnemyPresent() {
        var scn = battle(null); assertEquals(0, scn.GetDSTotalDestiny(), scn.epsilon);
    }
    @Test
    public void SecurityBattleDroidDoesNotAddAnotherDestinyWhenAlreadyAbleToDraw() {
        var scn = battle("vader"); assertEquals(3, scn.GetDSTotalDestiny(), scn.epsilon);
    }

    @Test
    public void SecurityBattleDroidRemovesPrintedHeroicInOpenAsWellAsLegacy() {
        var scn = scenario("open"); var hero = scn.GetDSCard("heroic");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), hero);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.HasKeyword(hero, Keyword.HEROIC));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("droid"));
        assertFalse(scn.HasKeyword(hero, Keyword.HEROIC));
    }
}
