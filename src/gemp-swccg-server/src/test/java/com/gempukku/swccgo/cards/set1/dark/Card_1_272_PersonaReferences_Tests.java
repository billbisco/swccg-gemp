package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1_272_PersonaReferences_Tests {
    private VirtualTableScenario search(boolean modified) {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>() {{
            put("interrupt", "1_272"); put("maul", "11_54"); put("mara", "110_10");
            put("vader", "1_168"); put("trooper", "1_194");
        }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        var interrupt = scn.GetDSCard("interrupt");
        scn.MoveCardsToHand(interrupt);
        for (String key : new String[]{"maul", "mara", "vader", "trooper"})
            scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard(key));
        scn.EnsureDSForcePile(6);
        scn.DrainDSForcePileTo(6);
        if (modified)
            scn.ApplyAdHocModifier(new ModifyGameTextModifier(scn.GetDSStartingLocation(), Filters.title("The Empire's Back"),
                    ModifyGameTextType.LEGACY__INTERRUPTS__VADER_MAY_BE_MAUL_OR_MARA_JADE));
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(interrupt, "Take card into hand");
        scn.PassAllResponses();
        return scn;
    }

    private void selectable(VirtualTableScenario scn, String key, boolean expected) {
        int index = scn.DSGetADParamAsList("blueprintId").indexOf(scn.GetDSCard(key).getBlueprintId(true));
        assertTrue(index >= 0);
        assertEquals(expected, Boolean.parseBoolean(scn.DSGetADParam("selectable")[index]));
    }

    @Test
    public void TheEmpiresBackUnmodifiedSearchRetainsVaderAndExcludesMaulAndMara() {
        var scn = search(false);
        selectable(scn, "vader", true);
        selectable(scn, "maul", false);
        selectable(scn, "mara", false);
        selectable(scn, "trooper", false);
        scn.DSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("vader"));
    }

    private void modifiedSearch(String chosen) {
        var scn = search(true);
        selectable(scn, "vader", true);
        selectable(scn, "maul", true);
        selectable(scn, "mara", true);
        selectable(scn, "trooper", false);
        scn.DSChooseCard(scn.GetDSCard(chosen));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard(chosen));
        assertEquals(2, scn.GetDSForcePileCount());
    }

    @Test
    public void TheEmpiresBackModifiedSearchTakesMaulForItsNormalForceCost() {
        modifiedSearch("maul");
    }

    @Test
    public void TheEmpiresBackModifiedSearchTakesMaraForItsNormalForceCost() {
        modifiedSearch("mara");
    }
}
