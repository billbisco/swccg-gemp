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
        return new VirtualTableScenario(new HashMap<>(),
                new HashMap<>(Map.of("racks", "1261_31", "droid", "12_104", "nute", "12_112",
                        "vehicle", "14_120", "trooper", "1_194")),
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
        lose(scn, droid);
        assertTrue(scn.DSCardActionAvailable(racks, "Stack"));
        scn.DSUseCardAction(racks, "Stack");
        scn.PassAllResponses();
        assertEquals(Zone.STACKED, droid.getZone());
        assertSame(racks, droid.getStackedOn());

        scn.DrainDSForcePileTo(1);
        assertFalse(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
        scn.EnsureDSForcePile(4);
        assertTrue(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
        int before = scn.GetDSForcePileCount();
        scn.DSUseCardAction(racks, MOVE_TO_USED);
        if (scn.DSHasCardChoiceAvailable(droid))
            scn.DSChooseCard(droid);
        scn.PassAllResponses();
        assertEquals(before - 2, scn.GetDSForcePileCount());
        assertEquals(Zone.USED_PILE, droid.getZone());
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
            assertEquals(Zone.LOST_PILE, racks.getZone());
        }
    }

    @Test(timeout = 10000)
    public void cannotRecycleInDeployPhaseEvenWithAStackedDestroyerDroid() {
        var scn = scenario(true);
        var racks = scn.GetDSCard("racks");
        var droid = scn.GetDSCard("droid");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSCard("swamp"), droid);
        scn.SkipToDSTurn(Phase.DEPLOY);
        lose(scn, droid);
        assertTrue(scn.DSCardActionAvailable(racks, "Stack"));
        scn.DSUseCardAction(racks, "Stack");
        scn.PassAllResponses();
        scn.EnsureDSForcePile(4);
        assertTrue(scn.GetStackedCards(racks).contains(droid));
        assertFalse(scn.DSCardActionAvailable(racks, MOVE_TO_USED));
    }

    private void lose(VirtualTableScenario scn, PhysicalCardImpl card) {
        scn.DSExecuteAdHocEffect(card, new LoseCardFromTableEffect(new TopLevelGameTextAction(card, card.getCardId()), card));
    }
}
