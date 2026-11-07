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

public class Card_1240_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("dinner", "1240_24");
                    put("letspass", "1240_45");
                    put("bones", "1240_29");
                    put("yaggle", "3_142");
                    put("womp", "7_215");
                    put("trooper", "1_194");
                    put("bogwing", "4_108"); // parasite
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
    public void FrozenDinnerVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dinner").getBlueprint();

        assertEquals("Frozen Dinner", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardCategory.EFFECT, card.getCardCategory());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FrozenDinnerVMakesLetsPassOnThatDestinyPlusTwo() {
        var scn = GetScenario();
        var dinner = scn.GetDSCard("dinner");
        var letspass = scn.GetDSCard("letspass");
        var yaggle = scn.GetDSCard("yaggle");

        scn.StartGame();
        scn.MoveCardsToHand(letspass, yaggle);
        int before = scn.GetDestiny(letspass);
        int yaggleBefore = scn.GetDestiny(yaggle);
        scn.MoveCardsToSideOfTable(dinner);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(before + 2, scn.GetDestiny(letspass));
        assertEquals(yaggleBefore, scn.GetDestiny(yaggle));
    }

    @Test
    public void FrozenDinnerVOncePerGameDeploysKraytDragonBonesFromReserveDeck() {
        var scn = GetScenario();
        var dinner = scn.GetDSCard("dinner");
        var bones = scn.GetDSCard("bones");

        scn.StartGame();
        scn.MoveCardsToSideOfTable(dinner);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfOwnReserveDeck(bones);

        assertTrue(scn.DSCardActionAvailable(dinner, "Deploy Krayt Dragon Bones from Reserve Deck"));
        scn.DSUseCardAction(dinner, "Deploy Krayt Dragon Bones from Reserve Deck");
        Block4DarkTestSupport.advanceUntil(scn, () -> scn.DSHasCardChoiceAvailable(bones) || bones.getZone() == Zone.SIDE_OF_TABLE, 10);
        if (scn.DSHasCardChoiceAvailable(bones)) {
            scn.DSChooseCard(bones);
        }
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, bones.getZone());
        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingDSDeployPhaseActions, 10);
        assertFalse(scn.DSCardActionAvailable(dinner, "Deploy Krayt Dragon Bones from Reserve Deck"));
    }

    private void setUpLostBattle(VirtualTableScenario scn, boolean withDinner) {
        setUpLostBattle(scn, withDinner, "womp");
    }

    private void setUpLostBattle(VirtualTableScenario scn, boolean withDinner, String creatureKey) {
        var dinner = scn.GetDSCard("dinner");
        var yaggle = scn.GetDSCard("yaggle");
        var womp = scn.GetDSCard(creatureKey);
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        if (withDinner)
            scn.MoveCardsToSideOfTable(dinner);
        scn.MoveCardsToHand(yaggle);
        scn.MoveCardsToLocation(site, trooper, womp, luke, leia, han);
        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.DSInitiateBattle(site);
        scn.SkipToEndOfPowerSegment(false);
    }

    @Test
    public void FrozenDinnerVLetsYaggleGakklePlaceCreatureInLostPileToReduceBattleDamageByFive() {
        var scn = GetScenario();
        setUpLostBattle(scn, true);
        var yaggle = scn.GetDSCard("yaggle");
        var womp = scn.GetDSCard("womp");

        assertTrue(Block4DarkTestSupport.describe(scn), Block4DarkTestSupport.advanceUntil(scn,
                () -> Block4DarkTestSupport.dsActionAvailable(scn, yaggle, "Place creature in Lost Pile"), 10));
        float damageBefore = scn.gameState().getBattleState().getBattleDamageRemaining(scn.game(), scn.DS);
        assertTrue(damageBefore > 0);
        scn.DSPlayCard(yaggle, "Place creature in Lost Pile");
        if (scn.DSHasCardChoiceAvailable(womp)) {
            scn.DSChooseCard(womp);
        }
        scn.PassAllResponses();

        assertTrue(scn.GetDSLostPile().contains(womp));
        assertEquals(Math.max(0, damageBefore - 5), scn.gameState().getBattleState().getBattleDamageRemaining(scn.game(), scn.DS), scn.epsilon);
    }

    @Test
    public void YaggleGakkleAlternatePlayNotAvailableWithoutFrozenDinnerV() {
        var scn = GetScenario();
        setUpLostBattle(scn, false);
        var yaggle = scn.GetDSCard("yaggle");

        assertFalse(Block4DarkTestSupport.advanceUntil(scn,
                () -> Block4DarkTestSupport.dsActionAvailable(scn, yaggle, "Place creature in Lost Pile"), 10));
    }

    @Test
    public void FrozenDinnerVYaggleGakkleCannotPlaceAParasite() {
        var scn = GetScenario();
        setUpLostBattle(scn, true, "bogwing");
        var yaggle = scn.GetDSCard("yaggle");

        assertFalse(Block4DarkTestSupport.advanceUntil(scn,
                () -> Block4DarkTestSupport.dsActionAvailable(scn, yaggle, "Place creature in Lost Pile"), 10));
    }
}
