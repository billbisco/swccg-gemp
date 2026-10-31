package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1106_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("redemption", "1106_19");
                    put("bacta", "3_32");
                }},
                new HashMap<>(),
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RedemptionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("redemption").getBlueprint();

        assertEquals("Redemption", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.CLOUD_CITY);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedemptionPermanentPilotProvidesAbilityOfTwo() {
        var scn = GetScenario();
        var redemption = scn.GetLSCard("redemption");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, redemption);
        assertEquals(2, scn.GetBattleDestinyAbility(redemption));
    }

    @Test
    public void RedemptionMakesBactaTankDeployForFree() {
        var scn = GetScenario();
        var redemption = scn.GetLSCard("redemption");
        var bacta = scn.GetLSCard("bacta");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, redemption);
        scn.MoveCardsToLSHand(bacta);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDeployCost(bacta));
        int usedBefore = scn.GetLSUsedPileCount();
        assertTrue(scn.LSDeployAvailable(bacta));
        scn.LSDeployCard(bacta);
        scn.PassAllResponses();
        assertEquals(usedBefore, scn.GetLSUsedPileCount());
        assertTrue(bacta.getZone() == Zone.SIDE_OF_TABLE || bacta.getZone() == Zone.AT_LOCATION);
    }

    @Test
    public void RedemptionMakesXOnBactaTankMinusTwo() {
        var scn = GetScenario();
        var redemption = scn.GetLSCard("redemption");
        var bacta = scn.GetLSCard("bacta");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, redemption);
        scn.MoveCardsToLSSideOfTable(bacta);
        assertEquals(2, scn.game().getModifiersQuerying().getVariableValue(
                scn.gameState(), bacta, Variable.X, 4), scn.epsilon);
    }

    @Test
    public void RedemptionMakesBactaTankLostWhenRedemptionIsLost() {
        var scn = GetScenario();
        var redemption = scn.GetLSCard("redemption");
        var bacta = scn.GetLSCard("bacta");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, redemption);
        scn.MoveCardsToLSSideOfTable(bacta);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(redemption, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(redemption, redemption.getCardId()), redemption));
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, redemption);
        assertInZone(Zone.LOST_PILE, bacta);
    }
}
