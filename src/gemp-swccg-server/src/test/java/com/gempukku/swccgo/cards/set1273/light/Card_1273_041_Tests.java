package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1273_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("coruscant", "1273_41");
                }},
                new HashMap<>() {{
                    put("dscoruscant", "12_165");
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
    public void CoruscantVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("coruscant").getBlueprint();

        assertEquals(Title.Coruscant, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        assertEquals(0, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantVDarkSideForceGenerationPlusOneIfYourCoruscantWasConverted() {
        var scn = GetScenario();
        var coruscant = scn.GetLSCard("coruscant");
        var dscoruscant = scn.GetDSCard("dscoruscant");

        scn.StartGame();
        scn.MoveCardsToDSHand(dscoruscant);
        scn.MoveCardsToLSHand(coruscant);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployLocation(dscoruscant);
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.DEPLOY);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), dscoruscant, scn.DS);
        scn.LSDeployLocation(coruscant);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, coruscant.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dscoruscant.getZone());
        // Coruscant (V) prints 1 Dark Side icon; +1 because the Dark Side Coruscant system was converted
        assertEquals(2, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), coruscant, scn.DS), scn.epsilon);
        assertTrue(before >= 1);
    }

    @Test
    public void CoruscantVIfJustConvertedMayRaiseThisSystemToTheTop() {
        var scn = GetScenario();
        var coruscant = scn.GetLSCard("coruscant");
        var dscoruscant = scn.GetDSCard("dscoruscant");

        scn.StartGame();
        scn.MoveCardsToDSHand(dscoruscant);
        scn.MoveCardsToLSHand(coruscant);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployLocation(coruscant);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, coruscant.getZone());

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployLocation(dscoruscant);
        Vb81RestTestSupport.passUntil(scn, () -> scn.LSActionAvailable("Raise Coruscant (V) to the top"));
        assertEquals(Zone.CONVERTED_LOCATIONS, coruscant.getZone());
        assertTrue(scn.LSActionAvailable("Raise Coruscant (V) to the top"));
        scn.LSChooseAction("Raise Coruscant (V) to the top");
        if (scn.LSHasCardChoiceAvailable(coruscant)) {
            scn.LSChooseCard(coruscant);
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, coruscant.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dscoruscant.getZone());
    }
}
