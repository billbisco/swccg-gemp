package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("foul", "1291_41");
                    put("coruscantdb", "12_74"); // Coruscant: Docking Bay
                    put("specialist", "1291_38"); // Clone Specialist: a clone, power 1
                    put("nien", "1291_47"); // Nien Nunb: a non-Jedi alien
                    put("luke", "1_19"); // Luke Skywalker: power 3
                    put("chewie", "2_3"); // Chewbacca: power 6
                }},
                new HashMap<>() {{
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

    @Test
    public void FoulMoudamaStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("foul").getBlueprint();
        assertEquals("Foul Moudama", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals("Talz.", card.getLore());
        assertEquals(Species.TALZ, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private boolean lightSideGoesFirstFlag(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().hasFlagActive(scn.gameState(), ModifierFlag.LIGHT_SIDE_GOES_FIRST);
    }

    @Test
    public void FoulMoudamaAtYourPrisonMakesLightSideGoFirst() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.ApplyAdHocModifier(new KeywordModifier(site, site, Keyword.PRISON));
        assertFalse(lightSideGoesFirstFlag(scn));
        scn.MoveCardsToLocation(site, foul);
        assertTrue(lightSideGoesFirstFlag(scn));
    }

    @Test
    public void FoulMoudamaNotAtAPrisonDoesNotMakeLightSideGoFirst() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), foul);
        assertFalse(lightSideGoesFirstFlag(scn));
    }

    @Test
    public void FoulMoudamaAtOpponentsPrisonDoesNotMakeLightSideGoFirst() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        var dsSite = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.ApplyAdHocModifier(new KeywordModifier(dsSite, dsSite, Keyword.PRISON));
        scn.MoveCardsToLocation(dsSite, foul);
        assertFalse(lightSideGoesFirstFlag(scn));
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }

    @Test
    public void FoulMoudamaDeploysMinusTwoToCoruscant() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        var coruscantdb = scn.GetLSCard("coruscantdb");
        scn.StartGame();
        scn.MoveLocationToTable(coruscantdb);
        scn.MoveCardsToHand(foul);
        assertEquals(4, deployCostTo(scn, foul, coruscantdb), scn.epsilon);
        assertEquals(6, deployCostTo(scn, foul, scn.GetDSStartingLocation()), scn.epsilon);
    }

    @Test
    public void FoulMoudamaMayCarryACharacterOfPowerLessThanFour() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        var luke = scn.GetLSCard("luke");
        var chewie = scn.GetLSCard("chewie");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), foul, luke, chewie);
        var mq = scn.game().getModifiersQuerying();
        assertTrue(scn.GetPower(luke) < 4);
        assertTrue(scn.GetPower(chewie) >= 4);
        assertTrue(mq.canCarryPassengerAsIfCreatureVehicle(scn.gameState(), foul, luke));
        assertFalse(mq.canCarryPassengerAsIfCreatureVehicle(scn.gameState(), foul, chewie));
    }

    @Test
    public void FoulMoudamaLetsGuardiansLightsaberDeployOnHim() {
        // Guardian's Lightsaber is not in GEMP yet: check the granted deploy target by title.
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), foul);
        int granted = 0;
        for (Modifier modifier : scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.MAY_DEPLOY_TO_TARGET)) {
            if (modifier.getSource(scn.gameState()) == foul
                    && modifier.isAffectedTarget(scn.gameState(), scn.game().getModifiersQuerying(), foul)
                    && !modifier.isAffectedTarget(scn.gameState(), scn.game().getModifiersQuerying(), scn.GetLSCard("luke"))) {
                ++granted;
            }
        }
        assertEquals(1, granted);
    }

    @Test
    public void FoulMoudamaOncePerGameTakesACloneOrNonJediAlienIntoHand() {
        var scn = GetScenario();
        var foul = scn.GetLSCard("foul");
        var specialist = scn.GetLSCard("specialist");
        var nien = scn.GetLSCard("nien");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), foul);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(specialist, nien, luke);
        assertTrue(scn.LSCardActionAvailable(foul, "Take a clone or non-Jedi alien"));
        scn.LSUseCardAction(foul, "Take a clone or non-Jedi alien");
        assertTrue(scn.LSHasCardChoiceAvailable(specialist));
        assertTrue(scn.LSHasCardChoiceAvailable(nien));
        assertFalse(Vb91LightTestSupport.lsSelectable(scn, luke)); // Luke (not a clone or alien) is shown but not selectable
        assertTrue(Vb91LightTestSupport.lsSelectable(scn, nien));
        scn.LSChooseCard(specialist);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, specialist);
        Vb91LightTestSupport.reach(scn, scn::AwaitingLSDeployPhaseActions);
        assertFalse(scn.LSCardActionAvailable(foul, "Take a clone or non-Jedi alien"));
    }
}
