package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.InterruptPersonaReferences;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1115_067_Integration_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("enemyInterrupt", "5_49"); put("enemySkill", "1_64"); put("luke", "1_50");
        }}, new HashMap<>() {{
            put("trained", "1115_67"); put("skill", "1_209"); put("trooper", "1_194");
            put("maul", "11_54"); put("mara", "110_10"); put("duel", "1110_13");
            put("modern", "221_41"); put("gold", "15_290"); put("decipher", "1_272");
            put("original", "1102_48");
            put("vader", "1_168");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("trained"));
        return scn;
    }

    @Test
    public void ActualEffectGrantsAllErasOnlyYourInterruptsAndPermissionEndsWhenItLeaves() {
        var scn = scenario();
        for (String key : new String[]{"decipher", "original", "modern", "gold", "duel"})
            assertTrue(key, InterruptPersonaReferences.mayTreatVaderAsMaulOrMara(scn.game(), scn.GetDSCard(key)));
        assertFalse(InterruptPersonaReferences.mayTreatVaderAsMaulOrMara(scn.game(), scn.GetLSCard("enemyInterrupt")));
        assertFalse(InterruptPersonaReferences.mayTreatVaderAsMaulOrMara(scn.game(), scn.GetDSCard("trained")));
        for (String key : new String[]{"duel", "gold"}) {
            var duel = scn.GetDSCard(key);
            var filter = duel.getBlueprint().getValidDuelParticipant(Side.DARK, scn.game(), duel);
            assertTrue(filter.accepts(scn.game(), scn.GetDSCard("maul")));
            assertTrue(filter.accepts(scn.game(), scn.GetDSCard("mara")));
            assertFalse(filter.accepts(scn.game(), scn.GetDSCard("trooper")));
        }
        scn.MoveOutOfPlay(scn.GetDSCard("trained"));
        assertFalse(InterruptPersonaReferences.mayTreatVaderAsMaulOrMara(scn.game(), scn.GetDSCard("modern")));
    }

    @Test
    public void SkillEffectOnlyModifiesOwnDrainsAtItsLocation() {
        var scn = scenario();
        var here = scn.GetDSStartingLocation();
        var elsewhere = scn.GetLSStartingLocation();
        var query = scn.game().getModifiersQuerying();
        float hereBase = query.getForceDrainAmount(scn.gameState(), here, scn.DS);
        float elsewhereBase = query.getForceDrainAmount(scn.gameState(), elsewhere, scn.DS);
        float enemyBase = query.getForceDrainAmount(scn.gameState(), here, scn.LS);
        scn.MoveCardsToLocation(here, scn.GetDSCard("trooper"));
        scn.AttachCardsTo(scn.GetDSCard("trooper"), scn.GetDSCard("skill"));
        assertEquals(hereBase + 1, query.getForceDrainAmount(scn.gameState(), here, scn.DS), 0);
        assertEquals(elsewhereBase, query.getForceDrainAmount(scn.gameState(), elsewhere, scn.DS), 0);
        assertEquals(enemyBase, query.getForceDrainAmount(scn.gameState(), here, scn.LS), 0);
        scn.MoveOutOfPlay(scn.GetDSCard("skill"));
        scn.MoveCardsToLocation(here, scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("enemySkill"));
        assertEquals(hereBase, query.getForceDrainAmount(scn.gameState(), here, scn.DS), 0);
    }

    private void battleDestiny(boolean atSkillEffect) {
        var scn = scenario();
        var here = scn.GetDSStartingLocation();
        var battleSite = atSkillEffect ? here : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(here, scn.GetDSCard("trooper"));
        scn.AttachCardsTo(scn.GetDSCard("trooper"), scn.GetDSCard("skill"));
        scn.MoveCardsToLocation(battleSite, scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(here, 1));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(battleSite);
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        scn.PrepareLSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(atSkillEffect ? 7 : 5, scn.GetDSTotalDestiny());
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void SkillEffectAddsOneToEachOfTwoActualBattleDestinyDrawsRatherThanOnlyTotal() {
        battleDestiny(true);
    }

    @Test
    public void SkillEffectDoesNotModifyActualBattleDestinyAtAnotherLocation() {
        battleDestiny(false);
    }
}
