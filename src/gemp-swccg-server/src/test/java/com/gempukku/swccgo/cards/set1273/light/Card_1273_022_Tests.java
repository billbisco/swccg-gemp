package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1273_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dack", "1273_22");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void DackRalterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dack").getBlueprint();

        assertEquals("Dack Ralter", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROGUE_SQUADRON);
            add(Keyword.GUNNER);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.DACK);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DackRalterMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var dack = scn.GetLSCard("dack");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dack, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(dack));
        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
    }
}
