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

}
