package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_031_Tests {
    private static final String MOVE_TO_USED = "Place a destroyer droid in Used Pile";
    private static final StartingSetup INVASION = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>(Map.of("invasion", "14_113", "naboo", "12_169",
                    "flagship", "14_114", "swamp", "12_171"));
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (String key : List.of("invasion", "naboo", "flagship", "swamp", "racks")) {
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard(key)))
                    scn.DSChooseCard(scn.GetDSCard(key));
            }
        }
    };

    private VirtualTableScenario scenario(boolean invasion) {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("racks", "1261_31", "droid", "12_104", "nute", "12_112",
                        "vehicle", "14_120", "trooper", "1_194", "droidekas", "1250_24", "oldDroidekas", "1114_33")),
                23, 23, StartingSetup.DefaultLSGroundLocation,
                invasion ? INVASION : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedDroidRacksIdentityAndAlterImmunity() {
        var scn = scenario(false);
        var bp = scn.GetDSCard("racks").getBlueprint();
        assertEquals("Droid Racks", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(PlayCardZoneOption.ATTACHED, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I,
                Icon.THEED_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void realInvasionSetupAttachesAndRacksCannotDeployWithoutInvasion() {
        var with = scenario(true);
        with.StartGame();
        assertSame(with.GetDSCard("invasion"), with.GetDSCard("racks").getAttachedTo());

        var without = scenario(false);
        var racks = without.GetDSCard("racks");
        without.StartGame();
        without.MoveCardsToDSHand(racks);
        without.SkipToDSTurn(Phase.DEPLOY);
        without.EnsureDSForcePile(8);
        assertFalse(without.DSDeployAvailable(racks));
    }

    @Test(timeout = 10000)
    public void physicalDestroyerDroidCanStackAfterLossThenRecycleOnlyInOwnControlForTwo() {
        var scn = scenario(true);
        var racks = scn.GetDSCard("racks");
        var droid = scn.GetDSCard("droid");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSCard("swamp"), droid, scn.GetDSCard("nute"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(4);
        lose(scn, droid);
        assertTrue(scn.DSCardActionAvailable(racks, "Stack"));
        scn.DSUseCardAction(racks, "Stack");
        scn.PassAllResponses();
        assertEquals(Zone.STACKED, droid.getZone());
        assertSame(racks, droid.getStackedOn());

        if (scn.AwaitingLSControlPhaseActions())
            scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
        int before = scn.GetDSForcePileCount();
        scn.DSUseCardAction(racks, MOVE_TO_USED);
        if (scn.DSHasCardChoiceAvailable(droid))
            scn.DSChooseCard(droid);
        scn.PassAllResponses();
        assertEquals(before - 2, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSUsedPile().contains(droid));
        if (scn.AwaitingLSControlPhaseActions())
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
    }

    @Test
    public void qualifyingNeimoidianAndDroidMayRemainButAnUnqualifiedCharacterOrVehicleLosesRacks() {
        for (String key : List.of("trooper", "vehicle")) {
            var scn = scenario(true);
            var racks = scn.GetDSCard("racks");
            var site = scn.GetDSCard("swamp");
            scn.StartGame();
            scn.MoveCardsToLocation(site, scn.GetDSCard("droid"), scn.GetDSCard("nute"));
            scn.SkipToDSTurn(Phase.CONTROL);
            assertTrue(racks.getZone().isInPlay());
            scn.MoveCardsToLocation(site, scn.GetDSCard(key));
            scn.SkipToPhase(Phase.DEPLOY);
            scn.PassAllResponses();
            assertTrue(scn.GetDSLostPile().contains(racks));
        }
    }

    @Test(timeout = 10000)
    public void cannotRecycleInDeployPhaseEvenWithAStackedDestroyerDroid() {
        var scn = scenario(true);
        var racks = scn.GetDSCard("racks");
        var droid = scn.GetDSCard("droid");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSCard("swamp"), droid);
        scn.StackCardsOn(racks, droid);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);
        assertTrue(scn.GetStackedCards(racks).contains(droid));
        assertFalse(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
    }

    @Test
    public void whereAreThoseDroidekasIgnoresNeimoidiansOnlyWhileRacksIsActive() {
        for (String key : List.of("droidekas", "oldDroidekas")) {
            var scn = scenario(true);
            var racks = scn.GetDSCard("racks");
            var site = scn.GetDSCard("swamp");
            var luke = scn.GetLSCard("luke");
            scn.StartGame();
            scn.MoveCardsToLocation(site, luke, scn.GetDSCard("droid"));
            scn.SkipToDSTurn(Phase.CONTROL);
            int base = (int) scn.GetDefense(luke);
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard(key));
            assertEquals(key, base - 1, scn.GetDefense(luke), scn.epsilon);

            // A Neimoidian at a site is ignored while Droid Racks (V) is on table.
            scn.MoveCardsToLocation(site, scn.GetDSCard("nute"));
            assertTrue(racks.getZone().isInPlay());
            assertEquals(key, base - 1, scn.GetDefense(luke), scn.epsilon);

            // Without Droid Racks (V), the Neimoidian breaks "all your characters at sites are destroyer droids".
            scn.MoveCardsToDSHand(racks);
            assertEquals(key, base, scn.GetDefense(luke), scn.epsilon);
        }
    }

    @Test
    public void racksDoesNotMakeWhereAreThoseDroidekasIgnoreOtherNonDestroyerCharacters() {
        var scn = scenario(true);
        var racks = scn.GetDSCard("racks");
        var site = scn.GetDSCard("swamp");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, scn.GetDSCard("droid"), scn.GetDSCard("nute"));
        scn.SkipToDSTurn(Phase.CONTROL);
        int base = (int) scn.GetDefense(luke);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("droidekas"));
        assertEquals(base - 1, scn.GetDefense(luke), scn.epsilon);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        assertTrue(racks.getZone().isInPlay());
        assertEquals(base, scn.GetDefense(luke), scn.epsilon);
    }

    private void lose(VirtualTableScenario scn, PhysicalCardImpl card) {
        scn.DSExecuteAdHocEffect(card, new LoseCardFromTableEffect(new TopLevelGameTextAction(card, card.getCardId()), card));
        // Yield earlier loss/leave-table windows without passing the actual stack response.
        for (int i = 0; i < 8; i++) {
            var ds = scn.DSGetDecision();
            var ls = scn.LSGetDecision();
            if (ds != null && ds.getText().toLowerCase().contains("optional response")
                    && scn.DSCardActionAvailable(scn.GetDSCard("racks"), "Stack"))
                return;
            if (ls != null && ls.getText().toLowerCase().contains("optional response"))
                scn.LSPass();
            else if (ds != null && ds.getText().toLowerCase().contains("optional response"))
                scn.DSPass();
            else
                fail("No stack response after the real loss; DS=" + ds + ", LS=" + ls);
        }
        fail("Stack response did not appear after passing the preceding windows");
    }
}
