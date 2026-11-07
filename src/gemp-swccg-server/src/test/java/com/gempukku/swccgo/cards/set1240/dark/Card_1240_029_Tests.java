package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("bones", "1240_29");
                    put("womp", "7_215");
                    put("onearm", "1104_42");
                    put("bogwing", "4_108");
                    put("dewback", "7_312");
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void KraytDragonBonesVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bones").getBlueprint();

        assertEquals(Title.Krayt_Dragon_Bones, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardCategory.EFFECT, card.getCardCategory());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KraytDragonBonesVWompRatsAreNotSelectiveCreatures() {
        var scn = GetScenario();
        var bones = scn.GetDSCard("bones");
        var womp = scn.GetDSCard("womp");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, womp);
        assertEquals(1, scn.GetIconCount(womp, Icon.SELECTIVE_CREATURE));

        scn.MoveCardsToSideOfTable(bones);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetIconCount(womp, Icon.SELECTIVE_CREATURE));
    }

    @Test
    public void KraytDragonBonesVYourFerociousCreaturesAreDeployMinusTwoAndFerocityPlusOne() {
        var scn = GetScenario();
        var bones = scn.GetDSCard("bones");
        var onearm = scn.GetDSCard("onearm");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(onearm);
        int deployBefore = scn.GetDeployCost(onearm);

        scn.MoveCardsToSideOfTable(bones);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(deployBefore - 2, scn.GetDeployCost(onearm));

        scn.MoveCardsToLocation(site, onearm);
        assertEquals(4, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), onearm, null), scn.epsilon);
    }

    @Test
    public void KraytDragonBonesVOncePerTurnDeploysCreatureExceptParasiteOrDewbackFromReserveDeck() {
        var scn = GetScenario();
        var bones = scn.GetDSCard("bones");
        var womp = scn.GetDSCard("womp");
        var bogwing = scn.GetDSCard("bogwing");
        var dewback = scn.GetDSCard("dewback");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToSideOfTable(bones);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfOwnReserveDeck(womp, bogwing, dewback);

        assertTrue(scn.DSCardActionAvailable(bones, "Deploy creature or dewback from Reserve Deck"));
        scn.DSUseCardAction(bones, "Deploy creature or dewback from Reserve Deck");
        Block4DarkTestSupport.advanceUntil(scn, () -> scn.DSHasCardChoiceAvailable(dewback), 10);
        assertTrue(Block4DarkTestSupport.dsSelectable(scn, dewback));
        assertTrue(Block4DarkTestSupport.dsSelectable(scn, womp));
        assertFalse(Block4DarkTestSupport.dsSelectable(scn, bogwing));
        scn.DSChooseCard(dewback);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        Block4DarkTestSupport.advanceUntil(scn, () -> dewback.getZone() == Zone.AT_LOCATION, 10);
        assertEquals(Zone.AT_LOCATION, dewback.getZone());
        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingDSDeployPhaseActions, 10);

        assertFalse(scn.DSCardActionAvailable(bones, "Deploy creature or dewback from Reserve Deck"));
    }
}
