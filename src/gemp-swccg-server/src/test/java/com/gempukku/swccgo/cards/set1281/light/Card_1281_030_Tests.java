package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("valorum", "1281_30");
                    put("valorumai", "1281_30ai");
                    put("yarua", "1116_21");
                    put("trooper", "1_28");
                    put("oldvalorum", "12_30");
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
    public void SupremeChancellorValorumVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("valorum").getBlueprint();

        assertEquals(Title.Valorum, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(3, card.getPolitics(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SupremeChancellorValorumVAlternateImageIdLoadsTheSameCard() {
        var scn = GetScenario();
        assertEquals(scn.GetLSCard("valorum").getBlueprint().getClass(), scn.GetLSCard("valorumai").getBlueprint().getClass());
    }

    @Test
    public void SupremeChancellorValorumVHasOrderAgendaAndMayBeTargetedByMightOfTheRepublic() {
        var scn = GetScenario();
        var valorum = scn.GetLSCard("valorum");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, valorum);
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), valorum, Agenda.ORDER));
        assertFalse(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), valorum, Agenda.JUSTICE));
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_BE_TARGETED_BY, valorum).isEmpty());
    }

    @Test
    public void SupremeChancellorValorumVCharactersWithPoliticsAreDeployMinusOneAndGainRepublicHere() {
        var scn = GetScenario();
        var valorum = scn.GetLSCard("valorum");
        var yarua = scn.GetLSCard("yarua");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        float yaruaCost = deployCostTo(scn, yarua, site);
        float trooperCost = deployCostTo(scn, trooper, site);
        scn.MoveCardsToLocation(site, valorum);
        assertEquals(yaruaCost - 1, deployCostTo(scn, yarua, site), scn.epsilon);
        assertEquals(trooperCost, deployCostTo(scn, trooper, site), scn.epsilon);

        assertFalse(scn.game().getModifiersQuerying().hasIcon(scn.gameState(), yarua, Icon.REPUBLIC));
        scn.MoveCardsToLocation(site, yarua, trooper);
        assertTrue(scn.game().getModifiersQuerying().hasIcon(scn.gameState(), yarua, Icon.REPUBLIC));
        assertFalse(scn.game().getModifiersQuerying().hasIcon(scn.gameState(), trooper, Icon.REPUBLIC));
    }

    @Test
    public void SupremeChancellorValorumVUsesOneForceToTakeAValorumCardIntoHand() {
        var scn = GetScenario();
        var valorum = scn.GetLSCard("valorum");
        var oldValorum = scn.GetLSCard("oldvalorum");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, valorum);
        scn.LSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(oldValorum);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(valorum, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(valorum, "Take card into hand from Reserve Deck");
        assertTrue(com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport.passUntil(scn, () -> scn.LSHasCardChoiceAvailable(oldValorum)));
        scn.LSChooseCard(oldValorum);
        scn.PassAllResponses();
        assertInHand(oldValorum);
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void SupremeChancellorValorumVTakesCardsOnlyDuringYourTurn() {
        var scn = GetScenario();
        var valorum = scn.GetLSCard("valorum");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, valorum);
        scn.LSActivateForceCheat(3);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertFalse(com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport.lsCardActionAvailable(scn, valorum, "Take card into hand from Reserve Deck"));
    }

    private static float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard card, com.gempukku.swccgo.game.PhysicalCard target) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
