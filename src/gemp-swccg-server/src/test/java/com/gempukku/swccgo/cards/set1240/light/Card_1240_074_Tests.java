package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_074_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("doallyn", "1240_74");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void SergeantDoallynStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("doallyn").getBlueprint();

        assertEquals("Sergeant Doallyn", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GAMBLER);
            add(Keyword.MERCENARY);
            add(Keyword.SMUGGLER);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.DOALLYN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SergeantDoallynMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(doallyn));
        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
    }

    @Test
    public void SergeantDoallynIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn);

        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), doallyn), scn.epsilon);
    }

    @Test
    public void SergeantDoallynVibroAxIsMatchingWeapon() {
        var scn = GetAxScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var ax = scn.GetLSCard("ax");
        var blaster = scn.GetLSCard("lsBlaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn);
        scn.AttachCardsTo(doallyn, ax);

        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), doallyn, ax));
        assertFalse(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), doallyn, blaster));
    }

    @Test
    public void SergeantDoallynVibroAxDeploysForFreeOnHim() {
        var scn = GetAxScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var ax = scn.GetLSCard("ax");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn);

        assertEquals(0, deployCostTo(scn, ax, doallyn), scn.epsilon);
    }

    @Test
    public void SergeantDoallynVibroAxFiresForFree() throws DecisionResultInvalidException {
        var scn = GetAxScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var ax = scn.GetLSCard("ax");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn, trooper);
        scn.AttachCardsTo(doallyn, ax);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(ax, "Fire"));
        scn.LSUseCardAction(ax, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void SergeantDoallynWhenDeployedMayPlayDefensiveShieldFromStartingEffect() {
        var scn = GetShieldScenario();
        var doallyn = scn.GetLSCard("doallyn");
        var starting = scn.GetLSCard("starting");
        var shield = scn.GetLSCard("shield");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.StackCardsOn(starting, shield);
        scn.MoveCardsToHand(doallyn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }

        assertTrue("must be LS deploy; ls=" + scn.LSGetDecision() + " ds=" + scn.DSGetDecision(),
                scn.AwaitingLSDeployPhaseActions());
        assertTrue("Doallyn must be playable; hand=" + doallyn.getZone()
                        + " force=" + scn.GetLSForcePileCount()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.LSCardPlayAvailable(doallyn));
        scn.LSPlayCard(doallyn);
        scn.LSChooseCard(site);
        scn.PassResponses("Use");
        while (scn.DSDecisionAvailable("optional")) {
            scn.DSPass();
        }
        assertTrue("Defensive Shield must be offered on deploy; startingZone=" + starting.getZone()
                        + " shieldZone=" + shield.getZone()
                        + " decision=" + (scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText())
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.GetLSAvailableActions().toString().contains("Defensive Shield"));
        scn.LSChooseAction("Defensive Shield");
        scn.LSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
    }

    private VirtualTableScenario GetAxScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("doallyn", "1240_74");
                    put("ax", "6_90");
                    put("lsBlaster", "1_152");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    private VirtualTableScenario GetShieldScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("doallyn", "1240_74");
                    put("starting", "13_5");
                    put("shield", "13_1");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
