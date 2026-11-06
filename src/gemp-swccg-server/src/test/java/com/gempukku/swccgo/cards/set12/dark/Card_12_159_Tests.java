package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_12_159_Tests {
    private VirtualTableScenario setup(boolean modified, boolean guardAtSenate) {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{ put("enemyGuard", "12_3"); }},
                new HashMap<>() {{
                    put("delegates", "12_159"); put("senate", "12_167");
                    put("guard", "12_100"); put("awayGuard", "12_100");
                    put("senator", "12_110"); put("senator2", "12_123");
                    put("lost1", "1_194"); put("lost2", "1_194"); put("lost3", "1_194");
                }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        var senate = scn.GetDSCard("senate");
        var delegates = scn.GetDSCard("delegates");
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, scn.GetDSCard("senator"), scn.GetDSCard("senator2"), scn.GetLSCard("enemyGuard"));
        scn.MoveCardsToLocation(guardAtSenate ? senate : scn.GetDSStartingLocation(), scn.GetDSCard("guard"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("awayGuard"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("lost1"), scn.GetDSCard("lost2"), scn.GetDSCard("lost3"));
        scn.MoveCardsToHand(delegates);
        if (modified)
            scn.ApplyAdHocModifier(new ModifyGameTextModifier(senate, Filters.title("Squabbling Delegates"),
                    ModifyGameTextType.LEGACY__SQUABBLING_DELEGATES__SENATORS_ARE_CORUSCANT_GUARDS));
        scn.SkipToPhase(Phase.DEPLOY);
        if (modified)
            assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), delegates,
                    ModifyGameTextType.LEGACY__SQUABBLING_DELEGATES__SENATORS_ARE_CORUSCANT_GUARDS));
        return scn;
    }

    @Test
    public void SquabblingDelegatesNormallyRetrievesForSenatorsInsteadOfGuards() {
        var scn = setup(false, true);
        int before = scn.GetDSLostPileCount();
        scn.DSPlayCard(scn.GetDSCard("delegates"), "Retrieve 2 Force");
        scn.PassAllResponses();
        assertEquals(before - 2, scn.GetDSLostPileCount());
    }

    @Test
    public void SquabblingDelegatesModifiedRetrievesOnlyForYourGuardsAtSenate() {
        var scn = setup(true, true);
        int before = scn.GetDSLostPileCount();
        scn.DSPlayCard(scn.GetDSCard("delegates"), "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(before - 1, scn.GetDSLostPileCount());
    }

    @Test
    public void SquabblingDelegatesModifiedDoesNotRetrieveForSenatorsOrGuardsElsewhere() {
        var scn = setup(true, false);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("delegates"), "Retrieve"));
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("delegates"), "Take card"));
    }
}
