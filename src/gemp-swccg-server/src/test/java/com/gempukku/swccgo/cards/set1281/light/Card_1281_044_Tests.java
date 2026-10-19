package com.gempukku.swccgo.cards.set1281.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_044_Tests {
    StartingSetup InfiltrationObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("infiltration", "1281_44");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.LSDecisionAvailable("starting")) {
                scn.LSChooseCard(scn.GetLSCard("infiltration"));
            }
            for (int i = 0; i < 30; i++) {
                boolean progressed = false;
                if (scn.LSDecisionAvailable("Nar Shaddaa")
                        || scn.LSDecisionAvailable("Undercity")
                        || scn.LSDecisionAvailable("Scoundrel's Luck")
                        || scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck")
                        || scn.LSDecisionAvailable("Choose cards to stack")
                        || scn.LSDecisionAvailable("Stack three")) {
                    PhysicalCardImpl narshaddaa = scn.GetLSCard("narshaddaa");
                    PhysicalCardImpl undercity = scn.GetLSCard("undercity");
                    PhysicalCardImpl luck = scn.GetLSCard("luck");
                    PhysicalCardImpl bravado = scn.GetLSCard("bravado");
                    PhysicalCardImpl charm = scn.GetLSCard("charm");
                    PhysicalCardImpl ingenuity = scn.GetLSCard("ingenuity");
                    if (scn.LSHasCardChoiceAvailable(narshaddaa)) {
                        scn.LSChooseCard(narshaddaa);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoiceAvailable(undercity)) {
                        scn.LSChooseCard(undercity);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoiceAvailable(luck)) {
                        scn.LSChooseCard(luck);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoicesAvailable(bravado, charm, ingenuity)) {
                        scn.LSChooseCards(bravado, charm, ingenuity);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoiceAvailable(bravado)) {
                        scn.LSChooseCard(bravado);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoiceAvailable(charm)) {
                        scn.LSChooseCard(charm);
                        progressed = true;
                    }
                    else if (scn.LSHasCardChoiceAvailable(ingenuity)) {
                        scn.LSChooseCard(ingenuity);
                        progressed = true;
                    }
                }
                else if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                    progressed = true;
                }
                if (!progressed) {
                    break;
                }
            }
        }
    };

    protected VirtualTableScenario GetScenario() {
        return GetScenario(new HashMap<>() {{
            put("trooper", "1_194");
        }});
    }

    protected VirtualTableScenario GetScenario(HashMap<String, String> extraDS) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("narshaddaa", "200_58");
                    put("undercity", "1281_41");
                    put("luck", "1281_37");
                    put("bravado", "1281_34");
                    put("charm", "1281_35");
                    put("ingenuity", "1281_36");
                    put("rest", "1281_39");
                    put("street", "1281_40");
                    put("charts", "1281_32");
                    put("han", "1281_27");
                    put("kyle", "1281_28");
                    put("leia", "1_17");
                    put("lando", "601_50");
                    put("ladyluck", "601_49");
                    put("nightfall", "1_56");
                    put("junk", "1_4");
                }},
                extraDS,
                10, 10,
                InfiltrationObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void InfiltrationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("infiltration").getBlueprint();

        assertEquals("Infiltration", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UnlikelyAlliesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        scn.StartGame();
        var card = scn.GetLSCard("infiltration").getOtherSideBlueprint();

        assertEquals("Unlikely Allies", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void InfiltrationDeploysNarShaddaaUndercityAndScoundrelsLuck() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var undercity = scn.GetLSCard("undercity");
        var luck = scn.GetLSCard("luck");

        scn.StartGame();

        assertEquals(Zone.SIDE_OF_TABLE, infiltration.getZone());
        assertEquals("Infiltration", infiltration.getBlueprint().getTitle());
        assertEquals(Zone.LOCATIONS, narshaddaa.getZone());
        assertEquals(Zone.LOCATIONS, undercity.getZone());
        assertTrue(scn.IsAttachedTo(infiltration, luck));
    }

    @Test
    public void InfiltrationMakesSpiesScoundrels() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var narshaddaa = scn.GetLSCard("narshaddaa");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveCardsToLocation(rest, leia);
        scn.SkipToPhase(Phase.CONTROL);

        float cost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), leia, rest, street, false, 0);
        assertEquals(0, cost, scn.epsilon);
        assertEquals("Nar Shaddaa", narshaddaa.getTitle());
    }

    @Test
    public void InfiltrationMayNotDeployNightfallOrHanSoloInnocentScoundrel() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var nightfall = scn.GetLSCard("nightfall");
        var lando = scn.GetLSCard("lando");
        var ladyluck = scn.GetLSCard("ladyluck");
        var narshaddaa = scn.GetLSCard("narshaddaa");

        scn.StartGame();
        scn.MoveCardsToLSHand(han, nightfall, lando, ladyluck);
        scn.LSActivateForceCheat(12);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(han));
        assertFalse(scn.LSDeployAvailable(nightfall));
        assertTrue(scn.LSDeployAvailable(lando));
        assertTrue(scn.LSDeployAvailable(ladyluck));
        assertEquals(Zone.LOCATIONS, narshaddaa.getZone());
    }

    @Test
    public void InfiltrationDeploysABattlegroundSiteToNarShaddaaOncePerTurn() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var rest = scn.GetLSCard("rest");

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rest);

        assertTrue(scn.LSCardActionAvailable(infiltration, "Deploy battleground site"));
        scn.LSUseCardAction(infiltration, "Deploy battleground site");
        assertTrue(scn.LSHasCardChoiceAvailable(rest));
        scn.LSChooseCard(rest);
        scn.LSChoose("Left");
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, rest.getZone());
        assertEquals("Nar Shaddaa", rest.getPartOfSystem());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(infiltration, "Deploy battleground site"));
    }

    @Test
    public void InfiltrationForceDrainsAtSystemsAreMinusOne() {
        var scn = GetScenario();
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var ladyluck = scn.GetLSCard("ladyluck");

        scn.StartGame();
        scn.MoveCardsToLocation(narshaddaa, ladyluck);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), narshaddaa, scn.LS), scn.epsilon);
    }

    @Test
    public void InfiltrationTreatsLukeOnOpponentsReflectionsIIObjectiveAsKyleKatarn() {
        var scn = GetScenario(new HashMap<>() {{
            put("agents", "10_29");
            put("xizor", "10_45");
        }});
        var agents = scn.GetDSCard("agents");
        var xizor = scn.GetDSCard("xizor");
        var kyle = scn.GetLSCard("kyle");
        scn.MoveOutOfPlay(agents);
        scn.MoveOutOfPlay(xizor);

        scn.StartGame();
        var lsSite = scn.GetLSCard("undercity");
        scn.MoveCardsToDSSideOfTable(agents);
        scn.MoveCardsToLocation(lsSite, xizor, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertEquals(Zone.SIDE_OF_TABLE, agents.getZone());
        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(
                scn.gameState(), agents, ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_KYLE_INSTEAD_OF_LUKE));
        assertEquals("Agents Of Black Sun", agents.getBlueprint().getTitle());
    }

    @Test
    public void InfiltrationFlipsWhenImperialNavigationChartsIsStolen() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertEquals("Infiltration", infiltration.getBlueprint().getTitle());
        assertTrue(scn.LSCardActionAvailable(charts, "Steal"));
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, charts.getZone());
        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
    }

    @Test
    public void UnlikelyAlliesScoundrelsAreForfeitPlusOne() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        int landoForfeit = scn.GetForfeit(lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();

        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
        assertEquals(landoForfeit + 1, scn.GetForfeit(lando));
    }

    @Test
    public void UnlikelyAlliesRetrievesOneForceDuringControlPhase() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.SkipToLSTurn(Phase.CONTROL);

        int life = scn.GetLSLifeForceRemaining();
        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
        assertTrue(scn.LSCardActionAvailable(infiltration, "Retrieve 1 Force"));
        scn.LSUseCardAction(infiltration, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(life + 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void UnlikelyAlliesPlacesACardFromHandInUsedPileShufflesAndTakesTop() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();
        var nightfall = scn.GetLSCard("nightfall");
        scn.MoveCardsToLSHand(junk, nightfall);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
        assertEquals(Zone.HAND, junk.getZone());
        assertTrue(scn.LSCardActionAvailable(infiltration, "Place a card from hand"));
        scn.LSUseCardAction(infiltration, "Place a card from hand");
        scn.LSChoose("Used Pile");
        assertTrue(scn.LSHasCardChoiceAvailable(junk));
        scn.LSChooseCard(junk);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, junk.getZone());
    }

    @Test
    public void UnlikelyAlliesFlipsBackWhenChartsLeaveTable() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();
        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());

        scn.MoveCardsToTopOfLSUsedPile(charts);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();

        assertEquals("Infiltration", infiltration.getBlueprint().getTitle());
        assertInZone(Zone.USED_PILE, charts);
    }

    @Test
    public void UnlikelyAlliesTreatsOnceOnScoundrelsLuckAsTwice() {
        var scn = GetScenario();
        var infiltration = scn.GetLSCard("infiltration");
        var luck = scn.GetLSCard("luck");
        var charts = scn.GetLSCard("charts");
        var rest = scn.GetLSCard("rest");
        var street = scn.GetLSCard("street");
        var undercity = scn.GetLSCard("undercity");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var kyle = scn.GetLSCard("kyle");

        scn.StartGame();
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.AttachCardsTo(rest, charts);
        scn.MoveCardsToLocation(rest, lando);
        scn.MoveCardsToLocation(street, leia);
        scn.MoveCardsToLocation(undercity, kyle);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(charts, "Steal");
        scn.PassAllResponses();
        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());

        scn.SkipToLSTurn(Phase.DEPLOY);
        var bravado = scn.GetLSCard("bravado");
        assertTrue(scn.LSCardActionAvailable(luck, "Deploy a [Scoundrel] Effect from here"));
        scn.LSUseCardAction(luck, "Deploy a [Scoundrel] Effect from here");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(bravado));
        scn.LSChooseCard(bravado);
        assertTrue(scn.LSHasCardChoiceAvailable(lando));
        scn.LSChooseCard(lando);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(luck, "Deploy a [Scoundrel] Effect from here"));
    }
}
