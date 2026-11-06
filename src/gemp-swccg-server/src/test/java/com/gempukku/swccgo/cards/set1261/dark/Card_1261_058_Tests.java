package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_058_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); }},
                new HashMap<>() {{ put("blizzard", "1261_58"); put("trooper", "8_100"); put("piett", "9_98"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BlizzardFourPrintedStatsCapacitiesAndNoMaintenanceAreCorrect() {
        var scn = scenario(); var b = scn.GetDSCard("blizzard").getBlueprint();
        assertEquals("Blizzard 4", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(6, b.getDeployCost(), scn.epsilon);
        assertEquals(6, b.getPower(), scn.epsilon); assertEquals(7, b.getArmor(), scn.epsilon);
        assertEquals(1, b.getLandspeed(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity()); assertEquals(6, b.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.VEHICLE); }});
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.AT_AT); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.ENCLOSED); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.VEHICLE); add(Icon.PILOT); add(Icon.SCOMP_LINK); add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void BlizzardFourPermanentPilotProvidesTwoAndImmunityIsBelowFour() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("blizzard");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), b);
        assertEquals(2, scn.GetBattleDestinyAbility(b));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), b), scn.epsilon);
    }

    @Test
    public void BlizzardFourDownloadsNonPilotImperialWarriorFromReserveDeckForFree() {
        var scn = scenario(); scn.StartGame(); var b = scn.GetDSCard("blizzard"); var trooper = scn.GetDSCard("trooper");
        var piett = scn.GetDSCard("piett"); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(b); scn.DSActivateForceCheat(10); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(trooper, piett); int force = scn.GetDSForcePileCount();
        assertTrue("Blizzard deployment needs six Force", force >= 6);
        scn.DSDeployCard(b);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        var windows = new ArrayList<String>();
        for (int i = 0; i < 20 && !scn.GetDSAvailableActions().stream().anyMatch(text -> text.contains("Deploy a non-pilot Imperial warrior from Reserve Deck")); i++) {
            windows.add(scn.GetDecidingPlayer() + ": " + scn.GetCurrentDecision().getText() + " actions=" + scn.GetDSAvailableActions());
            assertTrue("Missed download; windows=" + windows + "; reserve=" + scn.GetDSReserveDeckCount() + "; Blizzard zone=" + b.getZone() + "; trooper zone=" + trooper.getZone(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        scn.DSUseCardAction(b, "Deploy a non-pilot Imperial warrior from Reserve Deck"); scn.PassAllResponses();
        scn.PassCardAndForceUseResponses(); assertTrue(scn.DSHasCardChoiceAvailable(trooper)); assertFalse(isSelectable(scn, piett)); scn.DSChooseCard(trooper); scn.PassAllResponses();
        assertEquals(b, trooper.getAttachedTo()); assertTrue(trooper.isPassengerOf());
        assertEquals(Zone.RESERVE_DECK, piett.getZone()); assertEquals(force - 6, scn.GetDSForcePileCount());
    }

    private boolean isSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId");
        var allowed = scn.GetADParam(scn.DS, "selectable");
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].equals(card.getBlueprintId(true))) return allowed == null || Boolean.parseBoolean(allowed[i]);
        }
        return false;
    }

}
