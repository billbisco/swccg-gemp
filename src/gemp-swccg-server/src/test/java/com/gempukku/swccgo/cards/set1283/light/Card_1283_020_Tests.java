package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_1283_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("combo", "1283_20");
                    put("boshek", "1_4");
                    put("momaw", "1_20");
                    put("cctrooper", "5_3");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("alter", "1_234");
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

    /**
     * Starts the game with the combo Effect on table and, if requested, Quiet Mining Colony flipped to Independent Operation.
     */
    private VirtualTableScenario start(boolean independentOperation) {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("combo"));
        if (independentOperation) {
            PhysicalCardImpl objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, "109_4");
            scn.gameState().addCardToZone(objective, Zone.SIDE_OF_TABLE, scn.LS, true, false, false);
            objective.setObjectiveDeploymentComplete(true);
            scn.gameState().flipCard(scn.game(), objective, true);
            scn.gameState().reapplyAffectingForCard(scn.game(), objective);
            assertEquals(Title.Independent_Operation, objective.getBlueprint().getTitle());
        }
        return scn;
    }

    @Test
    public void AllMyUrchinsAndCloudCityCelebrationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("combo").getBlueprint();

        assertEquals("All My Urchins & Cloud City Celebration", card.getTitle());
        assertTrue(card.getTitles().contains(Title.All_My_Urchins));
        assertTrue(card.getTitles().contains(Title.Cloud_City_Celebration));
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SETUP);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ComboDeploysOnTable() {
        var scn = GetScenario();
        var combo = scn.GetLSCard("combo");

        scn.StartGame();
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(combo));
        scn.LSPlayCard(combo);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, combo);
    }

    private int lostPileAfterInitiatingBattle(VirtualTableScenario scn, PhysicalCardImpl... yourCharacters) {
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, yourCharacters);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3));
        scn.SkipToLSTurn(Phase.BATTLE);
        int before = scn.GetLSLostPileCount();
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        return before - scn.GetLSLostPileCount();
    }

    @Test
    public void ComboRetrievesTwoForceWhenYouInitiateBattleWithIndependentOperationAndOnlyAlienAbility() {
        var scn = start(true);
        assertEquals(2, lostPileAfterInitiatingBattle(scn, scn.GetLSCard("boshek"), scn.GetLSCard("momaw")));
    }

    @Test
    public void ComboDoesNothingWithoutIndependentOperation() {
        var scn = start(false);
        assertEquals(0, lostPileAfterInitiatingBattle(scn, scn.GetLSCard("boshek"), scn.GetLSCard("momaw")));
    }

    @Test
    public void ComboDoesNothingWhenSomeOfYourAbilityIsNotFromTheListedCards() {
        var scn = start(true);
        assertEquals(0, lostPileAfterInitiatingBattle(scn, scn.GetLSCard("boshek"), scn.GetLSCard("han")));
    }

    @Test
    public void ComboMakesYourTotalBattleDestinyPlusOneOrPlusTwoWithYourTrooperInBattle() {
        var scn = start(true);
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("boshek"), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(1, scn.game().getModifiersQuerying().getTotalBattleDestiny(scn.gameState(), scn.LS, 0), scn.epsilon);

        var scn2 = start(true);
        var site2 = scn2.GetLSStartingLocation();
        scn2.MoveCardsToLocation(site2, scn2.GetLSCard("boshek"), scn2.GetLSCard("cctrooper"), scn2.GetDSCard("trooper"));
        scn2.SkipToLSTurn(Phase.BATTLE);
        scn2.LSInitiateBattle(site2);
        scn2.PassAllResponses();
        assertEquals(2, scn2.game().getModifiersQuerying().getTotalBattleDestiny(scn2.gameState(), scn2.LS, 0), scn2.epsilon);
    }

    @Test
    public void CloudCityTroopersIgnorePowerMinusOneAndLocationDeploymentRestrictions() {
        var off = start(false);
        var trooperOff = off.GetLSCard("cctrooper");
        off.MoveCardsToLocation(off.GetDSStartingLocation(), trooperOff);
        assertEquals(1, off.GetPower(trooperOff));

        var scn = start(true);
        var cctrooper = scn.GetLSCard("cctrooper");
        var marketplace = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("boshek"));
        scn.MoveCardsToLocation(marketplace, scn.GetLSCard("momaw"));
        scn.MoveCardsToLSHand(cctrooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        scn.LSDeployCard(cctrooper);
        assertTrue(scn.LSHasCardChoiceAvailable(marketplace));
        scn.LSChooseCard(marketplace);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(marketplace, cctrooper));
        assertEquals(2, scn.GetPower(cctrooper));
    }

    @Test
    public void CloudCityTroopersStillRestrictedWithoutIndependentOperation() {
        var scn = start(false);
        var cctrooper = scn.GetLSCard("cctrooper");
        var marketplace = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(marketplace, scn.GetLSCard("momaw"));
        scn.MoveCardsToLSHand(cctrooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        scn.LSDeployCard(cctrooper);
        assertFalse(scn.LSHasCardChoiceAvailable(marketplace));
    }

    @Test
    public void ComboLimitsOpponentsImmunityToAttritionAtBespinLocationsToLessThanSix() {
        var scn = start(true);
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("boshek"));
        scn.MoveCardsToLocation(site, vader);
        scn.ApplyAdHocModifier(new ImmuneToAttritionLessThanModifier(vader, 8));
        assertEquals(6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        scn.MoveCardsToLocation(marketplace, vader);
        assertEquals(8, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
    }

    @Test
    public void ComboIsPlacedOnBottomOfForcePileWhenCanceled() {
        var scn = start(false);
        var combo = scn.GetLSCard("combo");
        var alter = scn.GetDSCard("alter");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToDSHand(alter);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(3);
        scn.PrepareDSDestiny(0);
        assertTrue(scn.DSCardPlayAvailable(alter, "cancel Effect"));
        scn.DSPlayCard(alter, "cancel Effect");
        if (scn.DSHasCardChoiceAvailable(combo)) {
            scn.DSChooseCard(combo);
        }
        if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(vader)) {
            scn.DSChooseCard(vader);
        }
        scn.PassAllResponses();
        assertInZone(Zone.FORCE_PILE, combo);
        var forcePile = scn.GetLSForcePile();
        assertEquals(combo, forcePile.get(forcePile.size() - 1));
        assertNotEquals(combo, scn.GetTopOfLSForcePile());
    }
}
