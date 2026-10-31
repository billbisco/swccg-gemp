package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import static org.junit.Assert.assertTrue;

public class Card_1112_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jedi", "1_21");
                    put("chamber", "12_076");
                }},
                new HashMap<>() {{
                    put("aurra", "1112_24");
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
    public void AurraSingMayRelocateToAJedisSiteDuringOpponentsActivate() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var blueprint = aurra.getBlueprint();
        var jedi = scn.GetLSCard("jedi");
        var chamber = scn.GetLSCard("chamber");
        var site = scn.GetDSStartingLocation();

        assertEquals("Aurra Sing", blueprint.getTitle());
        assertTrue(blueprint.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, blueprint.getUniqueness());
        assertEquals(Side.DARK, blueprint.getSide());
        assertEquals(1, blueprint.getDestiny(), scn.epsilon);
        assertEquals(4, blueprint.getDeployCost(), scn.epsilon);
        assertEquals(4, blueprint.getPower(), scn.epsilon);
        assertEquals(4, blueprint.getAbility(), scn.epsilon);
        assertEquals(3, blueprint.getForfeit(), scn.epsilon);
        assertTrue(blueprint.hasPersona(Persona.AURRA));
        scn.BlueprintCardTypeCheck(blueprint, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(blueprint, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintIconCheck(blueprint, new ArrayList<>() {{
            add(Icon.TATOOINE);
            add(Icon.EPISODE_I);
            add(Icon.WARRIOR);
            add(Icon.ALIEN);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, blueprint.getExpansionSet());
        assertEquals(Rarity.V, blueprint.getRarity());

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLocation(chamber, jedi);
        scn.MoveCardsToLocation(site, aurra);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertTrue(scn.AwaitingLSActivatePhaseActions());
        scn.LSPass();
        if (scn.LSDecisionAvailable("You have not activated Force")) {
            scn.LSChooseYes();
        }
        assertTrue(scn.DSCardActionAvailable(aurra, "Relocate to a Jedi's site"));
        scn.DSUseCardAction(aurra, "Relocate to a Jedi's site");
        assertTrue(scn.DSHasCardChoiceAvailable(chamber));
        scn.DSChooseCard(chamber);
        scn.PassAllResponses();
        assertEquals(chamber, aurra.getAtLocation());
    }
}
