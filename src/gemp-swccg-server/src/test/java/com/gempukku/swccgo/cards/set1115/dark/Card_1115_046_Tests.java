package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1115_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dewback", "1115_46");
                    put("trample", "3_138");
                    put("premiere", "7_312");
                    put("stormtrooper", "1_194");
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

    protected VirtualTableScenario GetOffTatooineScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dewback", "1115_46");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DSStartingLocation("1_284"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void DewbackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dewback").getBlueprint();

        assertEquals("Dewback", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertNull(card.getManeuver());
        assertEquals(2, card.getLandspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.CREATURE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEWBACK);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.SPECIAL_EDITION);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DewbackDeploysToTatooineMarketplace() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("dewback");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(dewback);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(dewback));
        scn.DSPlayCard(dewback);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, dewback.getAtLocation());
    }

    @Test
    public void DewbackMayNotDeployToDeathStar() {
        var scn = GetOffTatooineScenario();
        var dewback = scn.GetDSCard("dewback");

        scn.StartGame();
        scn.MoveCardsToDSHand(dewback);
        scn.EnsureDSForcePile(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(dewback));
    }

    @Test
    public void DewbackHasAbilityOneQuarter() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("dewback");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dewback);
        assertEquals(0.25, scn.game().getModifiersQuerying().getAbility(scn.gameState(), dewback), scn.epsilon);
    }

    @Test
    public void DewbackDoesNotEnableTrample() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("dewback");
        var trample = scn.GetDSCard("trample");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(trample);
        scn.MoveCardsToLocation(site, dewback, luke);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(trample));
    }

    @Test
    public void DewbackPremierePrintingDoesNotEnableTrample() {
        var scn = GetScenario();
        var premiere = scn.GetDSCard("premiere");
        var trample = scn.GetDSCard("trample");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(trample);
        scn.MoveCardsToLocation(site, premiere, luke);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(trample));
    }

    @Test
    public void DewbackRoarMakesACharacterHereDefenseValueMinusOne() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("dewback");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dewback, stormtrooper, luke);
        int defense = scn.GetDefense(luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassResponses("optional");
        String roar = scn.DSCardActionAvailable(dewback, "'Roar'") ? "'Roar'" : "Roar";
        assertTrue(scn.DSCardActionAvailable(dewback, roar));
        scn.DSUseCardAction(dewback, roar);
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(defense - 1, scn.GetDefense(luke));
    }
}
