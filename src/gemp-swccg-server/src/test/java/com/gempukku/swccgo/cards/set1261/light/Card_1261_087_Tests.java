package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_087_Tests {
    @Test public void strongIsVaderUsesTheExactLegacyEpicEventPrinting() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("strong", "1261_87")), new HashMap<>(),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        var b = scn.GetLSCard("strong").getBlueprint();
        assertEquals(Title.Strong_Is_Vader, b.getTitle()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EPIC_EVENT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EPIC_EVENT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }


    @Test public void aDifferentPrintingOfTheObjectiveDoesNotAllowStrongToDeploy() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("otherObjective", "7_138", "strong", "1261_87")),
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        var other = scn.GetLSCard("otherObjective");
        scn.MoveCardsToLSSideOfTable(other);
        assertEquals(Title.Mind_What_You_Have_Learned, other.getBlueprint().getTitle());
        assertFalse(Filters.icon(Icon.LEGACY_BLOCK_6).accepts(scn.game(), other));
        var strong = scn.GetLSCard("strong");
        scn.MoveCardsToLSHand(strong); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(strong));
        assertEquals(Zone.HAND, strong.getZone());
    }


    // GEMP offers Strong Is Vader through the objective's deployment rather than from hand, so these check the
    // printed deploy requirement ("If [Virtual] Mind What You Have Learned on table") directly.
    @Test public void theSet25VirtualObjectiveSatisfiesTheDeployRequirement() { assertTrue(requirementWith("225_53")); }
    @Test public void theLegacyVirtualObjectiveSatisfiesTheDeployRequirement() { assertTrue(requirementWith("1261_108")); }
    @Test public void theOriginalSpecialEditionObjectiveDoesNotSatisfyTheDeployRequirement() { assertFalse(requirementWith("7_138")); }
    @Test public void noObjectiveOnTableDoesNotSatisfyTheDeployRequirement() { assertFalse(requirementWith(null)); }

    private boolean requirementWith(String objective) {
        var ls = new HashMap<String, String>(Map.of("strong", "1261_87"));
        if (objective != null) ls.put("objective", objective);
        var scn = new VirtualTableScenario(ls,
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        if (objective != null) {
            var card = scn.GetLSCard("objective");
            scn.MoveCardsToLSSideOfTable(card);
            assertEquals(Title.Mind_What_You_Have_Learned, card.getBlueprint().getTitle());
        }
        var strong = scn.GetLSCard("strong");
        return new Card1261_087().checkGameTextDeployRequirements(scn.LS, scn.game(), strong, null, false);
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E2, by 2012-07-21) is the latest text, printed slips included ("have", "chose"); same play.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(java.util.Map.of("c", "1261_87")), new java.util.HashMap<>(), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetLSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("If [Virtual] Mind What You have Learned on table, deploy on table with up to 6 Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and chose one. You may retrieve 3 Force whenever you complete a Jedi Test. Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table.", text);
        org.junit.Assert.assertFalse(text.contains("draw two and choose one"));
        org.junit.Assert.assertNull(scn.GetLSCard("c").getBlueprint().getLegacyVersion());
    }
}
