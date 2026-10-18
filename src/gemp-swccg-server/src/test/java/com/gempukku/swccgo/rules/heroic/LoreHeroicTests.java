package com.gempukku.swccgo.rules.heroic;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.timing.rules.LoreHeroicRule;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoreHeroicTests {

    @Test
    public void LoreHeroicRuleIsOnForLegacyAndAnythingGoes() {
        assertTrue(VirtualTableScenario._formatLibrary.getFormat("legacy").hasLoreHeroicRule());
        assertTrue(VirtualTableScenario._formatLibrary.getFormat("anything_goes").hasLoreHeroicRule());
        assertFalse(VirtualTableScenario._formatLibrary.getFormat("open").hasLoreHeroicRule());
    }

    @Test
    public void R2D2IsHeroicWhenLoreHeroicRuleIsOn() {
        var scn = scenario("legacy", "2_14");
        var r2 = scn.GetLSCard("r2");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), r2, luke);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(Filters.and(Keyword.HEROIC).accepts(scn.game(), r2));
        assertFalse(Filters.and(Keyword.HEROIC).accepts(scn.game(), luke));
    }

    @Test
    public void R2D2VIsHeroicWhenLoreHeroicRuleIsOn() {
        assertHeroicPrinting("legacy", "201_6");
    }

    @Test
    public void R2D2AlternateImageIsHeroicWhenLoreHeroicRuleIsOn() {
        assertHeroicPrinting("legacy", "201_6^");
    }

    @Test
    public void LoreHeroicListIncludesLegacyR2D2VPrinting() {
        assertTrue(LoreHeroicRule.LORE_HEROIC_BLUEPRINT_IDS.contains("1210_109"));
    }

    @Test
    public void R2D2IsNotHeroicWhenLoreHeroicRuleIsOff() {
        var scn = scenario("open", "2_14");
        var r2 = scn.GetLSCard("r2");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, r2, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(Filters.and(Keyword.HEROIC).accepts(scn.game(), r2));

        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        assertTrue(scn.DSHasCardChoiceAvailable(r2));
    }

    @Test
    public void WeaponMustTargetR2D2BeforeNonHeroic() {
        var scn = scenario("legacy", "2_14");
        var r2 = scn.GetLSCard("r2");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue("Fire not offered. actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(r2));
        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
    }

    private static void assertHeroicPrinting(String format, String blueprintId) {
        var scn = scenario(format, blueprintId);
        PhysicalCardImpl r2 = scn.GetLSCard("r2");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), r2);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(blueprintId, Filters.and(Keyword.HEROIC).accepts(scn.game(), r2));
    }

    private static VirtualTableScenario scenario(String format, String r2Id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("r2", r2Id);
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                format
        );
    }
}
