package com.gempukku.swccgo.rules.liberation;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LiberatedSystemModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class LiberatedSystemsTests {
    private VirtualTableScenario setup() {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("yavin", "1_135"); put("bespin", "5_76"); put("restore", "1271_46"); put("marker", "1_19");
        }}, new HashMap<>() {{
            put("vanto", "223_12"); put("tie", "1_304");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("yavin"));
        scn.MoveLocationToTable(scn.GetLSCard("bespin"));
        scn.AttachCardsTo(scn.GetLSCard("yavin"), scn.GetLSCard("restore"));
        scn.MoveCardsToLocation(scn.GetLSCard("bespin"), scn.GetDSCard("vanto"));
        return scn;
    }

    private PhysicalCardImpl addLiberationEffect(VirtualTableScenario scn) {
        var effect = MissionTestSupport.putOnTable(scn, 9614, scn.LS,
                new AbstractNormalEffect(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "Test Liberation Effect",
                        Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
                    @Override
                    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
                        PhysicalCard system = self.getAttachedTo();
                        return system == null ? List.of() : List.of(new LiberatedSystemModifier(self, system));
                    }
                });
        scn.AttachCardsTo(scn.GetLSCard("yavin"), effect);
        return effect;
    }

    @Test
    public void ActiveEffectLiberatesOnlyItsSystemForLegacyAndModernConsumers() {
        var scn = setup();
        var yavin = scn.GetLSCard("yavin");
        var bespin = scn.GetLSCard("bespin");
        var vanto = scn.GetDSCard("vanto");
        var query = scn.game().getModifiersQuerying();
        float yavinDrain = query.getForceDrainAmount(scn.gameState(), yavin, scn.LS);
        float bespinDrain = query.getForceDrainAmount(scn.gameState(), bespin, scn.LS);
        assertEquals(4, query.getImmunityToAttritionLessThan(scn.gameState(), vanto), 0);

        var effect = addLiberationEffect(scn);
        assertTrue(LegacyFilters.liberatedSystem.accepts(scn.game(), yavin));
        assertFalse(LegacyFilters.liberatedSystem.accepts(scn.game(), bespin));
        assertFalse(LegacyFilters.canBeLiberated().accepts(scn.game(), yavin));
        assertTrue(scn.GetStackedCards(yavin).isEmpty());
        assertEquals(yavinDrain + 1, query.getForceDrainAmount(scn.gameState(), yavin, scn.LS), 0);
        assertEquals(bespinDrain, query.getForceDrainAmount(scn.gameState(), bespin, scn.LS), 0);
        assertEquals(6, query.getImmunityToAttritionLessThan(scn.gameState(), vanto), 0);

        scn.MoveOutOfPlay(effect);
        assertFalse(LegacyFilters.liberatedSystem.accepts(scn.game(), yavin));
        assertEquals(yavinDrain, query.getForceDrainAmount(scn.gameState(), yavin, scn.LS), 0);
        assertEquals(4, query.getImmunityToAttritionLessThan(scn.gameState(), vanto), 0);
    }

    @Test
    public void OpponentDrainMovesOnlyARealStackedCardAndLeavesActiveLiberation() {
        var scn = setup();
        var yavin = scn.GetLSCard("yavin");
        var marker = scn.GetLSCard("marker");
        var effect = addLiberationEffect(scn);
        scn.StackCardsOn(yavin, marker);
        marker.setLiberationCard(true);
        scn.MoveCardsToLocation(yavin, scn.GetDSCard("tie"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(yavin));
        scn.DSForceDrainAt(yavin);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, marker);
        assertTrue(scn.GetStackedCards(yavin).isEmpty());
        assertTrue(scn.IsAttachedTo(yavin, effect));
        assertTrue(LegacyFilters.liberatedSystem.accepts(scn.game(), yavin));
        scn.MoveOutOfPlay(effect);
        assertFalse(LegacyFilters.liberatedSystem.accepts(scn.game(), yavin));
    }

    @Test
    public void StackedLiberationRemainsAfterTheEffectLeaves() {
        var scn = setup();
        var yavin = scn.GetLSCard("yavin");
        var marker = scn.GetLSCard("marker");
        var effect = addLiberationEffect(scn);
        scn.StackCardsOn(yavin, marker);
        marker.setLiberationCard(true);
        scn.MoveOutOfPlay(effect);
        assertTrue(LegacyFilters.liberatedSystem.accepts(scn.game(), yavin));
        assertEquals(List.of(marker), scn.GetStackedCards(yavin));
    }
}
