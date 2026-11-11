package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_016_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "jungle", "1_137")),
                new HashMap<>(Map.of("cecius", "1261_16", "tactical", "3_136", "trooper", "1_194",
                        "vader", "1_168", "city", "5_166", "other", "5_169", "trooper2", "1_194")),
                23, 23, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedCeciusIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("cecius").getBlueprint();
        assertEquals("Lieutenant Cecius", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR,
                Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void onlyOpposingCharactersDeployForOneMoreToTheSameMobileSite() {
        for (boolean mobile : List.of(true, false)) {
            var scn = scenario();
            var luke = scn.GetLSCard("luke");
            var vader = scn.GetDSCard("vader");
            var city = scn.GetDSCard("city");
            var other = scn.GetDSCard("other");
            scn.StartGame();
            scn.MoveLocationToTable(city);
            scn.MoveLocationToTable(other);
            var same = mobile ? city : scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(same, scn.GetDSCard("cecius"));
            scn.MoveCardsToLSHand(luke);
            scn.MoveCardsToDSHand(vader);
            assertEquals(luke.getBlueprint().getDeployCost() + (mobile ? 1 : 0), cost(scn, luke, same), scn.epsilon);
            assertEquals(luke.getBlueprint().getDeployCost(), cost(scn, luke, other), scn.epsilon);
            assertEquals(vader.getBlueprint().getDeployCost(), cost(scn, vader, same), scn.epsilon);
        }
    }

    @Test(timeout = 10000)
    public void realTacticalSupportResolvesIntoUsedOnlyWhenCeciusIsActive() {
        for (boolean active : List.of(false, true)) {
            var scn = scenario();
            var tactical = scn.GetDSCard("tactical");
            var trooper = scn.GetDSCard("trooper");
            scn.StartGame();
            if (active)
                scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("cecius"));
            scn.MoveCardsToDSHand(tactical);
            scn.SkipToDSTurn(Phase.CONTROL);
            scn.EnsureDSForcePile(7);
            scn.MoveCardsToTopOfDSReserveDeck(trooper);
            scn.DSPlayCard(tactical, "Take troopers into hand");
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose Force to lose"))
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            scn.PassAllResponses();
            if (scn.DSHasCardChoiceAvailable(trooper))
                scn.DSChooseCard(trooper);
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose card to take into hand"))
                scn.DSChooseCards();
            scn.PassAllResponses();
            assertTrue(scn.GetDSHand().contains(trooper));
            String state = "active=" + active + "; Tactical Support=" + tactical.getZone()
                    + "; decision=" + scn.GetCurrentDecision().getText();
            assertEquals(state, active ? Zone.USED_PILE : Zone.LOST_PILE,
                    GameUtils.getZoneFromZoneTop(tactical.getZone()));
        }
    }

    @Test
    public void forceDrainBonusNeedsUniqueAndNonUniqueImperialPresentAtABattleground() {
        var scn = scenario();
        var cecius = scn.GetDSCard("cecius");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var vader = scn.GetDSCard("vader");
        var jungle = scn.GetLSCard("jungle");
        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        var site = scn.GetDSStartingLocation();
        float base = drain(scn, site);
        float jungleBase = drain(scn, jungle);

        scn.MoveCardsToLocation(site, cecius);
        assertEquals("unique Imperial only", base, drain(scn, site), scn.epsilon);

        scn.MoveCardsToLocation(site, trooper);
        assertEquals("unique and non-unique Imperial present", base + 1, drain(scn, site), scn.epsilon);

        // Another unique Imperial (Vader) with the non-unique Imperial still qualifies, without Cecius there
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), cecius);
        assertEquals("Cecius elsewhere, no unique Imperial here", base, drain(scn, site), scn.epsilon);
        scn.MoveCardsToLocation(site, vader);
        assertEquals("Vader is the unique Imperial", base + 1, drain(scn, site), scn.epsilon);

        // Not a battleground: Cecius's modifier does not reach it (the only bonus there is the Jungle's own
        // "if you control, Force drain +1 here")
        scn.MoveCardsToLocation(site, cecius);
        scn.MoveCardsToLocation(jungle, vader, trooper2);
        assertTrue(ceciusModifiesDrainAt(scn, cecius, site));
        assertFalse(ceciusModifiesDrainAt(scn, cecius, jungle));
        assertEquals("non-battleground site", jungleBase + 1, drain(scn, jungle), scn.epsilon);
    }

    @Test
    public void forceDrainBonusAppliesOnlyIfNoOtherForceDrainBonus() {
        var scn = scenario();
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var trooper2 = scn.GetDSCard("trooper2");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        float base = drain(scn, site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("cecius"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader, trooper2, luke);
        assertEquals(base + 1, drain(scn, site), scn.epsilon);

        // A Force drain penalty is not a bonus, so Cecius still adds +1
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new ForceDrainModifier(luke, site, -1, scn.DS));
        assertEquals(base, drain(scn, site), scn.epsilon);

        // One other Force drain bonus: only that bonus applies
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new ForceDrainModifier(vader, site, 1, scn.DS));
        assertEquals(base - 1 + 1, drain(scn, site), scn.epsilon);
    }

    @Test
    public void otherForceDrainBonusAtAnotherLocationDoesNotStopCecius() {
        var scn = scenario();
        var vader = scn.GetDSCard("vader");
        var city = scn.GetDSCard("city");
        scn.StartGame();
        scn.MoveLocationToTable(city);
        var site = scn.GetDSStartingLocation();
        float base = drain(scn, site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("cecius"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(city, vader);
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new ForceDrainModifier(vader, city, 1, scn.DS));
        assertEquals(base + 1, drain(scn, site), scn.epsilon);
    }

    @Test
    public void twoOnlyIfNoOtherBonusesTogetherApplyOnlyTheLargest() {
        for (int otherAmount : List.of(1, 2)) {
            var scn = scenario();
            var vader = scn.GetDSCard("vader");
            scn.StartGame();
            var site = scn.GetDSStartingLocation();
            float base = drain(scn, site);
            scn.MoveCardsToLocation(site, scn.GetDSCard("cecius"), scn.GetDSCard("trooper"));
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader, scn.GetDSCard("trooper2"));
            Modifier other = new ForceDrainModifier(vader, site, otherAmount, scn.DS);
            other.setOnlyIfNoOtherBonus(true);
            scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(other);
            assertEquals("other conditional bonus +" + otherAmount, base + otherAmount, drain(scn, site), scn.epsilon);

            // An ordinary bonus turns off both conditional bonuses
            scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                    new ForceDrainModifier(scn.GetDSCard("trooper2"), site, 1, scn.DS));
            assertEquals(base + 1, drain(scn, site), scn.epsilon);
        }
    }

    private boolean ceciusModifiesDrainAt(VirtualTableScenario scn, PhysicalCardImpl cecius, PhysicalCardImpl location) {
        return scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                ModifierType.FORCE_DRAIN_AMOUNT, location).stream()
                .anyMatch(m -> m.getSource(scn.gameState()) == cecius);
    }

    private float drain(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
    }

    private float cost(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, site,
                false, null, false, 0, null, false);
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1) is the latest text (same play).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_16")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other Force drain bonus), your Force drains are +1. Tactical Support is a Used Interrupt.", text);
        org.junit.Assert.assertFalse(text.contains("no other Force drain bonus"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
