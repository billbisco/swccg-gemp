package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.InterruptPersonaReferences;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1115_067_Integration_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>() {{
            put("enemyInterrupt", "5_49"); put("enemySkill", "1_64"); put("luke", "1_19");
            put("obi", "1_21");
        }}, new HashMap<>() {{
            put("trained", "1115_67"); put("skill", "1_209"); put("trooper", "1_194");
            put("maul", "11_54"); put("mara", "110_10"); put("duel", "1110_13");
            put("modern", "221_41"); put("gold", "15_290"); put("decipher", "1_272");
            put("original", "1102_48");
            put("vader", "1_168");
            put("modernSearch", "213_20"); put("sister", "213_12");
            put("brother", "213_6");
            put("legacySearch", "601_98");
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
        scn.MoveCardsToLocation(battleSite, scn.GetDSCard("maul"), scn.GetLSCard("luke"));
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(here, 1));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(battleSite);
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        assertTrue(scn.GetCurrentDecision().getText(), scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(scn.GetCurrentDecision().getText(), scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
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

    private void originalVirtualDuel(String character) {
        var scn = scenario();
        var interrupt = scn.GetDSCard("duel");
        var duelist = scn.GetDSCard(character);
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToHand(interrupt);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), duelist, obi);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(5));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(0), scn.GetLSDestiny(1));
        assertTrue(scn.DSCardPlayAvailable(interrupt, "Initiate epic duel"));
        scn.DSPlayCard(interrupt, "Initiate epic duel");
        scn.DSChooseCard(obi);
        if (scn.DSDecisionAvailable("Choose Vader"))
            scn.DSChooseCard(duelist);
        scn.PassAllResponses();
        scn.PassAllResponses();
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, obi);
        assertInZone(Zone.AT_LOCATION, duelist);
    }

    @Test
    public void OriginalVirtualVadersAngerActuallyDuelsWithMaulUnderOriginalVirtualEffect() {
        originalVirtualDuel("maul");
    }

    @Test
    public void OriginalVirtualVadersAngerActuallyDuelsWithMaraUnderOriginalVirtualEffect() {
        originalVirtualDuel("mara");
    }

    @Test
    public void OriginalVirtualVadersAngerCannotUseMaulWithoutTheGrantingEffect() {
        var scn = scenario();
        scn.MoveOutOfPlay(scn.GetDSCard("trained"));
        scn.MoveOutOfPlay(scn.GetDSCard("vader"));
        scn.MoveCardsToHand(scn.GetDSCard("duel"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("maul"), scn.GetLSCard("obi"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("duel"), "Initiate epic duel"));
    }

    @Test
    public void ModernVirtualInterruptActuallyLosesMaulFromHandAsCostAndSearchesInquisitor() {
        var scn = scenario();
        var interrupt = scn.GetDSCard("modernSearch");
        var maul = scn.GetDSCard("maul");
        var sister = scn.GetDSCard("sister");
        var brother = scn.GetDSCard("brother");
        scn.MoveOutOfPlay(scn.GetDSCard("vader"));
        scn.MoveOutOfPlay(scn.GetDSCard("mara"));
        scn.MoveOutOfPlay(sister);
        scn.MoveOutOfPlay(brother);
        scn.MoveCardsToHand(interrupt, maul);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(sister, brother);
        scn.DSPlayCard(interrupt, "Take inquisitors into hand from Reserve Deck");
        if (scn.GetDSHand().contains(maul))
            scn.DSChooseCard(maul);
        scn.PassAllResponses();
        scn.DSChooseCard(sister);
        scn.PassAllResponses();
        scn.DSChooseCard(brother);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, maul);
        assertInZone(Zone.HAND, sister);
        assertInZone(Zone.HAND, brother);
        assertEquals(scn.GetCurrentDecision().getText(), Zone.LOST_PILE, interrupt.getZone());
    }

    private void legacyDeploymentSearch(boolean trained) {
        var scn = scenario();
        if (!trained)
            scn.MoveOutOfPlay(scn.GetDSCard("trained"));
        var interrupt = scn.GetDSCard("legacySearch");
        scn.MoveCardsToHand(interrupt);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);
        assertEquals(trained, scn.DSCardPlayAvailable(interrupt, "Deploy Vader from Reserve Deck"));
    }

    @Test
    public void LegacyDeploymentSearchCanStillBeOfferedWhenVaderUniquenessBlocksVader() {
        legacyDeploymentSearch(true);
        legacyDeploymentSearch(false);
    }
}
