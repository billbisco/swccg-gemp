package com.gempukku.swccgo.cards.set1102.dark;

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

public class Card_1102_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("isd", "1102_36");
                    put("avenger", "4_166");
                    put("tie", "1_304");
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
    public void ImperialClassStarDestroyerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("isd").getBlueprint();

        assertEquals("Imperial-Class Star Destroyer", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(8, card.getDeployCost(), scn.epsilon);
        assertEquals(8, card.getPower(), scn.epsilon);
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
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialClassStarDestroyerPermanentPilotProvidesAbilityOfOne() {
        var scn = GetScenario();
        var isd = scn.GetDSCard("isd");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, isd);
        assertEquals(1, scn.GetBattleDestinyAbility(isd));
    }

    @Test
    public void ImperialClassStarDestroyerDeploysMinusThreeToSameLocationAsAUniqueStarDestroyer() {
        var scn = GetScenario();
        var isd = scn.GetDSCard("isd");
        var avenger = scn.GetDSCard("avenger");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, avenger);
        scn.MoveCardsToDSHand(isd);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(isd, system);
        assertAtLocation(system, isd);
        assertEquals(force - 5, scn.GetDSForcePileCount());
    }

    @Test
    public void ImperialClassStarDestroyerDeploysForEightWithoutAUniqueStarDestroyer() {
        var scn = GetScenario();
        var isd = scn.GetDSCard("isd");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(isd);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(isd, system);
        assertAtLocation(system, isd);
        assertEquals(force - 8, scn.GetDSForcePileCount());
    }

    @Test
    public void ImperialClassStarDestroyerDrawsOneBattleDestinyWhenAlone() {
        var scn = GetScenario();
        var isd = scn.GetDSCard("isd");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, isd, xwing);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(system);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }
}
