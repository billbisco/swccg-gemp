package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("r2a5", "1240_14");
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

    @Test
    public void R2A5StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("r2a5").getBlueprint();

        assertEquals("R2-A5 (Artoo-Ayfive)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.NAV_COMPUTER);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void R2A5MustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var r2a5 = scn.GetDSCard("r2a5");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2a5, trooper, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(r2a5));
        assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }

    @Test
    public void R2A5AtAConvertedSiteMayUseOneForceToRaiseYourSite() {
        var scn = GetConvertScenario();
        var r2a5 = scn.GetDSCard("r2a5");
        var lsCantina = scn.GetLSCard("lsCantina");
        var dsCantina = scn.GetDSCard("dsCantina");

        scn.StartGame();
        scn.MoveLocationToTable(dsCantina);
        scn.MoveLocationToTable(lsCantina);
        assertEquals(Zone.CONVERTED_LOCATIONS, dsCantina.getZone());
        scn.MoveCardsToLocation(lsCantina, r2a5);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSActivateForceCheat(2);

        assertTrue(scn.DSCardActionAvailable(r2a5, "Raise"));
        int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(r2a5, "Raise");
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetDSForcePileCount());
        assertEquals(Zone.LOCATIONS, dsCantina.getZone());
    }

    @Test
    public void R2A5WhenDeployedMayPlayDefensiveShieldFromStartingEffect() {
        var scn = GetShieldScenario();
        var r2a5 = scn.GetDSCard("r2a5");
        var starting = scn.GetDSCard("starting");
        var shield = scn.GetDSCard("shield");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.StackCardsOn(starting, shield);
        scn.MoveCardsToHand(r2a5);
        scn.DSActivateMaxForceAndPass();
        scn.PassControlActions();
        scn.DSActivateForceCheat(5);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(r2a5));
        scn.DSPlayCard(r2a5);
        scn.DSChooseCard(site);
        scn.PassResponses("Use");
        while (scn.LSDecisionAvailable("optional")) {
            scn.LSPass();
        }
        assertTrue("Defensive Shield must be offered on deploy; startingZone=" + starting.getZone()
                        + " shieldZone=" + shield.getZone()
                        + " decision=" + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText())
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.GetDSAvailableActions().toString().contains("Defensive Shield"));
        scn.DSChooseAction("Defensive Shield");
        scn.DSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
    }

    @Test
    public void R2A5DoesNotOfferRaiseAtAnUnconvertedSite() {
        var scn = GetScenario();
        var r2a5 = scn.GetDSCard("r2a5");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, r2a5);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardActionAvailable(r2a5, "Raise"));
    }

    private VirtualTableScenario GetShieldScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("r2a5", "1240_14");
                    put("starting", "13_69");
                    put("shield", "13_81");
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

    private VirtualTableScenario GetConvertScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lsCantina", "1_128");
                }},
                new HashMap<>() {{
                    put("r2a5", "1240_14");
                    put("dsCantina", "1_290");
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
}
