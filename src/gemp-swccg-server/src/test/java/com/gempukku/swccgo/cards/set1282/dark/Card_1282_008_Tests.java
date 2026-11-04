package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("echo", "3_57"); // Hoth: Echo Command Center
                    put("trench", "3_63"); // Hoth: Snow Trench (2nd Marker)
                    put("oldmpg", "3_61"); // Hoth: Main Power Generators (1st Marker), Hoth
                }},
                new HashMap<>() {{
                    put("mpg", "1282_8");
                    put("perimeter", "3_144"); // Hoth: Defensive Perimeter (3rd Marker)
                    put("northridge", "3_149"); // Hoth: North Ridge (4th Marker)
                    put("blizzard2", "3_155"); // Blizzard 2 (AT-AT, permanent pilot ability 2)
                    put("cannon", "3_158"); // AT-AT Cannon
                    put("ttmg", "3_115"); // Target The Main Generator
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

    /** Stand-in for [Virtual Block 2] Imperial Occupation (1220_72, not coded yet): matched by title + [Virtual Block 2] icon. */
    private static AbstractNormalEffect vb2ImperialOccupation() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Occupation, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V) {
        };
    }

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 40; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean underShield(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().isLocationUnderHothEnergyShield(scn.gameState(), site);
    }

    private static final String ATTEMPT = "Attempt to 'blow away' Main Power Generators";

    @Test
    public void HothMainPowerGeneratorsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("mpg").getBlueprint();
        assertEquals(Title.Main_Power_Generators, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MARKER_1);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.LIGHT_FORCE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.HOTH);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HothMainPowerGeneratorsHothEnergyShieldRulesCoverEchoSitesAndFirstThroughThirdMarkers() {
        var scn = GetScenario();
        var mpg = scn.GetDSCard("mpg");
        var echo = scn.GetLSCard("echo");
        var trench = scn.GetLSCard("trench");
        var perimeter = scn.GetDSCard("perimeter");
        var northridge = scn.GetDSCard("northridge");
        scn.StartGame();
        scn.MoveLocationToTable(mpg);
        scn.MoveLocationToTable(trench);
        scn.MoveLocationToTable(perimeter);
        scn.MoveLocationToTable(northridge);
        scn.MoveLocationToTable(echo);

        assertTrue(underShield(scn, mpg));
        assertTrue(underShield(scn, echo));
        assertTrue(underShield(scn, trench));
        assertTrue(underShield(scn, perimeter));
        assertFalse(underShield(scn, northridge));
    }

    @Test
    public void HothMainPowerGeneratorsShieldCoversOnlyThisSiteAndEchoSitesWithVirtualBlock2ImperialOccupation() {
        var scn = GetScenario();
        var mpg = scn.GetDSCard("mpg");
        var echo = scn.GetLSCard("echo");
        var trench = scn.GetLSCard("trench");
        var perimeter = scn.GetDSCard("perimeter");
        scn.StartGame();
        scn.MoveLocationToTable(mpg);
        scn.MoveLocationToTable(trench);
        scn.MoveLocationToTable(perimeter);
        scn.MoveLocationToTable(echo);
        MissionTestSupport.putOnTable(scn, 128281, scn.DS, vb2ImperialOccupation());

        assertTrue(underShield(scn, mpg));
        assertTrue(underShield(scn, echo));
        assertFalse(underShield(scn, trench));
        assertFalse(underShield(scn, perimeter));
    }

    @Test
    public void HothMainPowerGeneratorsNonVirtualImperialOccupationDoesNotShrinkTheShield() {
        var scn = GetScenario();
        var mpg = scn.GetDSCard("mpg");
        var trench = scn.GetLSCard("trench");
        scn.StartGame();
        scn.MoveLocationToTable(mpg);
        scn.MoveLocationToTable(trench);
        MissionTestSupport.putOnTable(scn, 128282, scn.DS, VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("7_298"));
        assertTrue(underShield(scn, trench));
    }

    private void cannonReadyAt(VirtualTableScenario scn, PhysicalCardImpl mainPowerGenerators, PhysicalCardImpl atatSite) {
        var blizzard2 = scn.GetDSCard("blizzard2");
        scn.MoveLocationToTable(mainPowerGenerators);
        scn.MoveLocationToTable(atatSite);
        scn.MoveCardsToLocation(atatSite, blizzard2);
        scn.AttachCardsTo(blizzard2, scn.GetDSCard("cannon"));
        scn.MoveCardsToHand(scn.GetDSCard("ttmg"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(6);
    }

    @Test
    public void HothMainPowerGeneratorsAllowsTargetTheMainGeneratorFromThirdMarker() {
        var scn = GetScenario();
        scn.StartGame();
        cannonReadyAt(scn, scn.GetDSCard("mpg"), scn.GetDSCard("perimeter"));
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("ttmg"), ATTEMPT));
    }

    @Test
    public void HothMainPowerGeneratorsPreventsHothEpicEventsFromBeyondThirdMarker() {
        var scn = GetScenario();
        scn.StartGame();
        cannonReadyAt(scn, scn.GetDSCard("mpg"), scn.GetDSCard("northridge"));
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.GetDSAvailableActions().contains(ATTEMPT));
    }

    @Test
    public void TargetTheMainGeneratorMayBeAttemptedFromFourthMarkerWithoutThisSite() {
        var scn = GetScenario();
        scn.StartGame();
        cannonReadyAt(scn, scn.GetLSCard("oldmpg"), scn.GetDSCard("northridge"));
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("ttmg"), ATTEMPT));
    }

    @Test
    public void HothMainPowerGeneratorsBlownAwayMakesLightSideLoseThreeForce() {
        var scn = GetScenario();
        var mpg = scn.GetDSCard("mpg");
        var ttmg = scn.GetDSCard("ttmg");
        scn.StartGame();
        cannonReadyAt(scn, mpg, scn.GetDSCard("perimeter"));
        // destiny 7 + X 2 (permanent pilot) + Y 1 (Defensive Perimeter controlled) = 10 > 8
        scn.PrepareDSDestiny(7);
        int lsLifeForce = scn.GetLSLifeForceRemaining();

        scn.DSPlayCard(ttmg, ATTEMPT);
        if (scn.DSDecisionAvailable("Choose AT-AT Cannon")) {
            scn.DSChooseCard(scn.GetDSCard("cannon"));
        }
        if (scn.DSDecisionAvailable("Choose AT-AT pilot")) {
            scn.DSChooseCard(scn.GetDSCard("blizzard2"));
        }
        passUntil(scn, () -> scn.AwaitingLSForceLossPayment() || scn.AwaitingDSControlPhaseActions());
        while (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayForceLossFromReserveDeck();
            passUntil(scn, () -> scn.AwaitingLSForceLossPayment() || scn.AwaitingDSControlPhaseActions());
        }
        passUntil(scn, scn::AwaitingDSControlPhaseActions);

        assertTrue(mpg.isBlownAway());
        assertEquals(lsLifeForce - 3, scn.GetLSLifeForceRemaining());
    }
}
