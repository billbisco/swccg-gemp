package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_040_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("guardian", "1250_40"); put("inner", "13_24"); put("objective", "1117_10");
            put("saber", "1_155"); put("blaster", "1_156"); put("otherJedi", "1_19");
        }}, new HashMap<>() {{
            put("trooper", "1_194"); put("vader", "1_168");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void JediGuardianMetadataMatchesOfficialImage() {
        var scn = scenario();
        var card = scn.GetLSCard("guardian").getBlueprint();
        assertEquals("Jedi Guardian", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC); add(Icon.WARRIOR); add(Icon.DAGOBAH);
            add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void JediGuardianDeploysForEightWithoutEitherObjectiveTitle() {
        deployGuardian(false);
    }

    @Test
    public void JediGuardianDeploysForFiveWithWellHandleThis() {
        deployGuardian(true);
    }

    private void deployGuardian(boolean objectivePresent) {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        if (objectivePresent) {
            var objective = scn.GetLSCard("objective");
            scn.MoveCardsToLSSideOfTable(objective);
            objective.setObjectiveDeploymentComplete(true);
        }
        scn.MoveCardsToHand(guardian);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int cost = objectivePresent ? 5 : 8;
        assertEquals(cost, scn.GetDeployCost(guardian));
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(guardian);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, guardian.getAtLocation());
        assertEquals(force - cost, scn.GetLSForcePileCount());
    }

    @Test
    public void JediGuardianDuelOfTheFatesAlsoRemovesDeployPenalty() {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        var objective = scn.GetLSCard("objective");
        scn.StartGame();
        scn.MoveCardsToHand(guardian);
        assertEquals(8, scn.GetDeployCost(guardian));
        scn.MoveCardsToLSSideOfTable(objective);
        objective.setObjectiveDeploymentComplete(true);
        scn.gameState().flipCard(scn.game(), objective, true);
        assertEquals("Duel Of The Fates", objective.getBlueprint().getTitle());
        assertEquals(5, scn.GetDeployCost(guardian));
        scn.MoveOutOfPlay(objective);
        assertEquals(8, scn.GetDeployCost(guardian));
    }

    @Test
    public void JediGuardianNeverDeploysWhileInnerStrengthOnTableEvenWithObjective() {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("inner"), scn.GetLSCard("objective"));
        scn.GetLSCard("objective").setObjectiveDeploymentComplete(true);
        scn.MoveCardsToHand(guardian);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(5, scn.GetDeployCost(guardian));
        assertFalse(scn.LSDeployAvailable(guardian));
    }

    @Test
    public void JediGuardianLightsaberHitMakesOpponentLoseOneForce() {
        checkWeaponHit(false, false);
    }

    @Test
    public void JediGuardianLightsaberMissDoesNotLoseForce() {
        checkWeaponHit(true, false);
    }

    @Test
    public void JediGuardianAnotherJedisLightsaberHitDoesNotLoseForce() {
        checkWeaponHit(false, true);
    }

    @Test
    public void JediGuardianNonLightsaberHitDoesNotLoseForce() {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, guardian, trooper);
        scn.AttachCardsTo(guardian, blaster);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(4);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(trooper.isHit());
        assertEquals(lifeForce, scn.GetDSLifeForceRemaining());
    }

    private void checkWeaponHit(boolean miss, boolean otherJediFires) {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        var otherJedi = scn.GetLSCard("otherJedi");
        var saber = scn.GetLSCard("saber");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, guardian, trooper);
        if (otherJediFires) {
            scn.MoveCardsToLocation(site, otherJedi);
        }
        scn.AttachCardsTo(otherJediFires ? otherJedi : guardian, saber);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(miss ? 0 : 3);
        scn.PrepareLSDestiny(miss ? 1 : 4);
        int lifeForce = scn.GetDSLifeForceRemaining();
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw(2);
        if (!miss && !otherJediFires) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
        }
        scn.PassAllResponses();
        assertEquals(!miss, trooper.isHit());
        assertEquals(lifeForce - (!miss && !otherJediFires ? 1 : 0), scn.GetDSLifeForceRemaining());
    }

    @Test
    public void JediGuardianMayIgnoreThreeAttrition() {
        checkAttrition(3);
    }

    @Test
    public void JediGuardianMustForfeitAtFourAttrition() {
        checkAttrition(4);
    }

    private void checkAttrition(int attrition) {
        var scn = scenario();
        var guardian = scn.GetLSCard("guardian");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, guardian, scn.GetDSCard("vader"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(7);
        scn.PrepareDSDestiny(attrition);
        scn.SkipToDamageSegment(true);
        assertEquals(0, scn.gameState().getBattleState().getBattleDamageRemaining(scn.game(), scn.LS), scn.epsilon);
        assertEquals(attrition < 4 ? 0 : 1, scn.LSGetChoiceMin());
        if (attrition < 4) {
            scn.LSChooseCards();
            scn.PassAllResponses();
            assertEquals(site, guardian.getAtLocation());
        }
        else {
            scn.LSChooseCard(guardian);
            scn.PassAllResponses();
            assertTrue(scn.GetLSLostPile().contains(guardian));
        }
    }
}
