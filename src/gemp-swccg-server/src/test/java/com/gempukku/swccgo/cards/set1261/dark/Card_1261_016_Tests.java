package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_016_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("cecius", "1261_16", "tactical", "3_136", "trooper", "1_194",
                        "vader", "1_168", "city", "5_166", "other", "5_169")),
                23, 23, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedCeciusIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("cecius").getBlueprint();
        assertEquals("Lieutenant Cecius", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR,
                Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void onlyOpposingCharactersDeployForOneMoreToTheSameMobileSite() {
        for (boolean mobile : List.of(true, false)) {
            var scn = scenario();
            var luke = scn.GetLSCard("luke");
            var vader = scn.GetDSCard("vader");
            var city = scn.GetDSCard("city");
            var other = scn.GetDSCard("other");
            scn.StartGame();
            scn.MoveLocationToTable(city);
            scn.MoveLocationToTable(other);
            var same = mobile ? city : scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(same, scn.GetDSCard("cecius"));
            scn.MoveCardsToLSHand(luke);
            scn.MoveCardsToDSHand(vader);
            assertEquals(luke.getBlueprint().getDeployCost() + (mobile ? 1 : 0), cost(scn, luke, same), scn.epsilon);
            assertEquals(luke.getBlueprint().getDeployCost(), cost(scn, luke, other), scn.epsilon);
            assertEquals(vader.getBlueprint().getDeployCost(), cost(scn, vader, same), scn.epsilon);
        }
    }

    @Test(timeout = 10000)
    public void realTacticalSupportResolvesIntoUsedOnlyWhenCeciusIsActive() {
        for (boolean active : List.of(false, true)) {
            var scn = scenario();
            var tactical = scn.GetDSCard("tactical");
            var trooper = scn.GetDSCard("trooper");
            scn.StartGame();
            if (active)
                scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("cecius"));
            scn.MoveCardsToDSHand(tactical);
            scn.SkipToDSTurn(Phase.CONTROL);
            scn.EnsureDSForcePile(7);
            scn.MoveCardsToTopOfDSReserveDeck(trooper);
            scn.DSPlayCard(tactical, "Take troopers into hand");
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose Force to lose"))
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            scn.PassAllResponses();
            if (scn.DSHasCardChoiceAvailable(trooper))
                scn.DSChooseCard(trooper);
            scn.PassAllResponses();
            assertTrue(scn.GetDSHand().contains(trooper));
            String state = "active=" + active + "; Tactical Support=" + tactical.getZone()
                    + "; decision=" + scn.GetCurrentDecision().getText();
            assertEquals(state, active ? Zone.USED_PILE : Zone.LOST_PILE,
                    GameUtils.getZoneFromZoneTop(tactical.getZone()));
        }
    }

    private float cost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, site,
                false, null, false, 0, null, false);
    }
}
