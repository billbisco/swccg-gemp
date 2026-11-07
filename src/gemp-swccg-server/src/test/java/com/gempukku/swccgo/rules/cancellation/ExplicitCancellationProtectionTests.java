package com.gempukku.swccgo.rules.cancellation;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ExplicitCancellationProtectionTests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("shield", "13_37"); put("effect", "7_81");
        }}, new HashMap<>(), 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("shield"), scn.GetLSCard("effect"));
        return scn;
    }

    @Test
    public void IntrinsicShieldPlayTypeDoesNotCreateBlanketCancellationProtection() {
        var scn = scenario();
        var shield = scn.GetLSCard("shield");
        assertTrue(shield.getBlueprint().isCardTypeMayNotBeCanceled());
        assertFalse(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), shield));
        assertTrue(GameConditions.canBeCanceled(scn.game(), shield));
    }

    @Test
    public void ExplicitModifierProtectsTheAffectedShieldAndLeavesOtherCardsCancelable() {
        var scn = scenario();
        var shield = scn.GetLSCard("shield");
        scn.ApplyAdHocModifier(new MayNotBeCanceledModifier(scn.GetLSStartingLocation(), Filters.sameCardId(shield)));
        assertTrue(shield.getBlueprint().isCardTypeMayNotBeCanceled());
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), shield));
        assertFalse(GameConditions.canBeCanceled(scn.game(), shield));
        assertTrue(GameConditions.canBeCanceled(scn.game(), scn.GetLSCard("effect")));
    }

    @Test
    public void ExplicitModifierStillProtectsOrdinaryEffects() {
        var scn = scenario();
        var effect = scn.GetLSCard("effect");
        assertFalse(effect.getBlueprint().isCardTypeMayNotBeCanceled());
        scn.ApplyAdHocModifier(new MayNotBeCanceledModifier(scn.GetLSStartingLocation(), Filters.sameCardId(effect)));
        assertFalse(GameConditions.canBeCanceled(scn.game(), effect));
        assertTrue(GameConditions.canBeCanceled(scn.game(), scn.GetLSCard("shield")));
    }
}
