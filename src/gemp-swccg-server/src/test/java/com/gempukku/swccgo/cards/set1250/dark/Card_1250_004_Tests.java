package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1250_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("guard", "1250_4");
                    put("trooper", "1_194");
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
    public void CoruscantGuardStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("guard").getBlueprint();

        assertEquals("Coruscant Guard", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CORUSCANT_GUARD);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CORUSCANT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantGuardMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var guard = scn.GetDSCard("guard");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, guard, trooper, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(guard));
        assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }
}
