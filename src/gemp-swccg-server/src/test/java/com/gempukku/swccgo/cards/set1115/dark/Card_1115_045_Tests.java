package com.gempukku.swccgo.cards.set1115.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1115_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("devastator", "1115_45");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void DevastatorStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("devastator").getBlueprint();

        assertEquals("Devastator", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(8, card.getDeployCost(), scn.epsilon);
        assertEquals(9, card.getPower(), scn.epsilon);
        assertEquals(6, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(9, card.getForfeit(), scn.epsilon);
        assertEquals(6, card.getPilotCapacity());
        assertEquals(4, card.getStarfighterOrTIECapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DevastatorPermanentPilotProvidesAbilityOfOneAndImmunityLessThanFour() {
        var scn = GetScenario();
        var devastator = scn.GetDSCard("devastator");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, devastator);
        assertEquals(1, scn.GetBattleDestinyAbility(devastator));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), devastator), scn.epsilon);
    }

    @Test
    public void DevastatorFollowsAnOpponentsStarshipThatJustMovedFromSameSystem() {
        var scn = GetScenario();
        var devastator = scn.GetDSCard("devastator");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var dantooine = scn.GetDSStartingLocation();
        var tibrin = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dantooine, devastator, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.EnsureLSForcePile(2);
        scn.EnsureDSForcePile(2);
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.AwaitingLSMovePhaseActions());
        scn.LSMoveCard(falcon, tibrin);
        scn.PassForceUseResponses();
        boolean choseFollow = false;
        String last = "none";
        for (int i = 0; i < 12; i++) {
            if (scn.DSGetDecision() != null) {
                last = "DS=" + scn.DSGetDecision().getText()
                        + " actions=" + scn.DSGetADParamAsList("actionText")
                        + " force=" + scn.GetDSForcePileCount()
                        + " falconAt=" + (falcon.getAtLocation() == null ? "null" : falcon.getAtLocation().getTitle());
                try {
                    var actions = scn.DSGetADParamAsList("actionText");
                    if (actions != null) {
                        for (String a : actions) {
                            if (a != null && a.toLowerCase().contains("follow")) {
                                scn.DSChooseAction("Follow");
                                choseFollow = true;
                                break;
                            }
                        }
                    }
                } catch (RuntimeException ignored) {
                }
                if (choseFollow) {
                    break;
                }
                if (scn.DSDecisionAvailable("optional")) {
                    scn.DSPass();
                    continue;
                }
            }
            if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(last, choseFollow);
        scn.PassAllResponses();
        assertAtLocation(tibrin, devastator);
        assertAtLocation(tibrin, falcon);
    }
}
