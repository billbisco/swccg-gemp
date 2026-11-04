package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("het", "1281_29");
                    put("jawa", "1_12");
                    put("desert", "6_85");
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
    public void HetNkikVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("het").getBlueprint();

        assertEquals("Het Nkik", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.JAWA, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.HET);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HetNkikVDeploysOnlyOnTatooine() {
        var scn = GetScenario();
        var het = scn.GetLSCard("het");
        var marketplace = scn.GetDSStartingLocation();
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(het);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(het));
        scn.LSDeployCard(het);
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        assertFalse(scn.LSHasCardChoiceAvailable(walkway));
        scn.LSChooseCard(marketplace);
        scn.PassAllResponses();
        assertAtLocation(marketplace, het);
    }

    @Test
    public void HetNkikVForceDrainsAndBonusesHereMayNotBeCanceledWhileWithAnotherJawa() {
        var scn = GetScenario();
        var het = scn.GetLSCard("het");
        var jawa = scn.GetLSCard("jawa");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(marketplace, het);
        assertFalse(scn.game().getModifiersQuerying().cantCancelForceDrainAtLocation(scn.gameState(), marketplace, null, scn.DS, scn.LS));
        assertFalse(bonusProtected(scn, het, marketplace));
        scn.MoveCardsToLocation(marketplace, jawa);
        assertTrue(scn.game().getModifiersQuerying().cantCancelForceDrainAtLocation(scn.gameState(), marketplace, null, scn.DS, scn.LS));
        assertTrue(bonusProtected(scn, het, marketplace));
    }

    private static boolean bonusProtected(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCard yourCard, com.gempukku.swccgo.game.PhysicalCard location) {
        for (Modifier modifier : scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.FORCE_DRAIN_BONUSES_MAY_NOT_BE_CANCELED, yourCard)) {
            if (((ForceDrainBonusesMayNotBeCanceledModifier) modifier).isForceDrainAtLocationFilter(scn.game(), location))
                return true;
        }
        return false;
    }

    @Test
    public void HetNkikVDrawsOneCardForEachDesertYouControlWithAJawa() {
        var scn = GetScenario();
        var het = scn.GetLSCard("het");
        var jawa = scn.GetLSCard("jawa");
        var desert = scn.GetLSCard("desert");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(desert);
        scn.MoveCardsToLocation(marketplace, het);
        scn.MoveCardsToLocation(desert, jawa);
        scn.SkipToLSTurn(Phase.CONTROL);
        int hand = scn.GetLSHandCount();
        assertTrue(scn.LSCardActionAvailable(het, "Draw 1 card from Reserve Deck"));
        scn.LSUseCardAction(het, "Draw 1 card from Reserve Deck");
        scn.PassAllResponses();
        assertEquals(hand + 1, scn.GetLSHandCount());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, het, "Draw"));
    }

    @Test
    public void HetNkikVDrawsNothingWithoutADesertControlledWithAJawa() {
        var scn = GetScenario();
        var het = scn.GetLSCard("het");
        var desert = scn.GetLSCard("desert");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(desert);
        scn.MoveCardsToLocation(marketplace, het);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(het, "Draw"));
    }
}
