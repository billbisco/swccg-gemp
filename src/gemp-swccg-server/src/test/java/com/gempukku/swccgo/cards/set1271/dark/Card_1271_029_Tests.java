package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1271_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("tie", "1271_29");
                    put("pilot", "1271_6");
                    put("cannon", "9_181");
                    put("tatooine", "1_289");
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
    public void SaberSquadronTIEStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tie").getBlueprint();

        assertEquals("Saber Squadron TIE", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.TIE_INTERCEPTOR);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.NO_HYPERDRIVE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(1, card.getPilotCapacity());
    }

    @Test
    public void SaberSquadronTIEPowerPlusOneWhilePresentWithAnXwing() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetDSCard("tatooine");

        var pilot = scn.GetDSCard("pilot");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, pilot);
        assertEquals(5, scn.GetPower(tie));
        scn.MoveCardsToLocation(tatooine, xwing);
        assertEquals(6, scn.GetPower(tie));
    }

    @Test
    public void SaberSquadronTIEDuringBattleMayFireOneStarshipWeaponTwice() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");
        var pilot = scn.GetDSCard("pilot");
        var cannon = scn.GetDSCard("cannon");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie, xwing);
        scn.BoardAsPilot(tie, pilot);
        scn.AttachCardsTo(tie, cannon);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cannon, "Fire"));
        scn.DSUseCardAction(cannon, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(xwing));
        scn.DSChooseCard(xwing);
        scn.PrepareDSDestiny(1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.PassResponses("optional");
        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cannon, "Fire"));
        scn.DSUseCardAction(cannon, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(xwing));
        scn.DSChooseCard(xwing);
        scn.PrepareDSDestiny(1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.PassResponses("optional");
        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(cannon, "Fire"));
    }

    @Test
    public void SaberSquadronTIEImmuneToAttritionLessThanFourWhenMatchingPilotAboard() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");
        var pilot = scn.GetDSCard("pilot");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tie), scn.epsilon);
        scn.BoardAsPilot(tie, pilot);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tie), scn.epsilon);
    }
}
