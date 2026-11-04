package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DepleteLifeForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("juod", "1281_11");
                    put("throne", "9_147"); // Death Star II: Throne Room
                    put("emperor", "9_109"); // Emperor Palpatine
                    put("vader", "1_168"); // Darth Vader
                    put("db94", "1_291"); // Tatooine: Docking Bay 94
                    put("rebellion", "9_127"); // Insignificant Rebellion
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    // Stand-in for the Virtual Block 6.2 Effect So Be It, Jedi (not coded on this master).
    private static AbstractNormalEffect soBeItJedi() {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "So Be It, Jedi", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    @Test
    public void JoinUsOrDieStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("juod").getBlueprint();
        assertEquals("Join Us Or Die", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JoinUsOrDieDeploysOnYourThroneRoom() {
        var scn = GetScenario();
        var juod = scn.GetDSCard("juod");
        var throne = scn.GetDSCard("throne");
        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToHand(juod);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(juod));
        scn.DSDeployCard(juod);
        if (scn.DSHasCardChoiceAvailable(throne)) {
            scn.DSChooseCard(throne);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(throne, juod));
    }

    @Test
    public void JoinUsOrDieMayNotDeployWithoutYourThroneRoom() {
        var scn = GetScenario();
        var juod = scn.GetDSCard("juod");
        scn.StartGame();
        scn.MoveCardsToHand(juod);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(juod));
    }

    @Test
    public void JoinUsOrDieDeploysADockingBayFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var juod = scn.GetDSCard("juod");
        var throne = scn.GetDSCard("throne");
        var db94 = scn.GetDSCard("db94");
        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.AttachCardsTo(throne, juod);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(db94);
        assertTrue(scn.DSCardActionAvailable(juod, "Deploy card from Reserve Deck"));
        scn.DSUseCardAction(juod, "Deploy card from Reserve Deck");
        scn.DSChooseCard(db94);
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, db94.getZone());
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(juod, "Deploy card from Reserve Deck"));
    }

    @Test
    public void JoinUsOrDieDeploysSoBeItJediFromReserveDeck() {
        var scn = GetScenario();
        var juod = scn.GetDSCard("juod");
        var throne = scn.GetDSCard("throne");
        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.AttachCardsTo(throne, juod);
        scn.SkipToDSTurn(Phase.DEPLOY);
        var soBeIt = MissionTestSupport.putInReserve(scn, 98301, scn.DS, soBeItJedi());
        scn.DSUseCardAction(juod, "Deploy card from Reserve Deck");
        scn.DSChooseCard(soBeIt);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, soBeIt);
    }

    private PhysicalCardImpl setUpTakeYourFathersPlace(VirtualTableScenario scn, boolean emperorAndVaderHere, int id) {
        var juod = scn.GetDSCard("juod");
        var throne = scn.GetDSCard("throne");
        var emperor = scn.GetDSCard("emperor");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.AttachCardsTo(throne, juod);
        scn.MoveCardsToLocation(throne, vader, luke);
        if (emperorAndVaderHere) {
            scn.MoveCardsToLocation(throne, emperor);
        }
        PhysicalCardImpl objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "9_151");
        scn.gameState().addCardToZone(objective, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        scn.gameState().flipCard(scn.game(), objective, true);
        assertEquals(Title.Take_Your_Fathers_Place, objective.getBlueprint().getTitle());
        return objective;
    }

    /** Has Take Your Father's Place make Dark Side lose 1 Force (its "lose 1 Force at end of each of your turns" result). */
    private int ForceLostFromTakeYourFathersPlace(VirtualTableScenario scn, PhysicalCardImpl tyfp) {
        scn.SkipToDSTurn(Phase.DEPLOY);
        int before = scn.GetDSLostPile().size();
        var action = new TopLevelGameTextAction(tyfp, scn.DS, tyfp.getCardId());
        action.appendEffect(new LoseForceEffect(action, scn.DS, 1));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        for (int i = 0; i < 20 && !scn.AwaitingLSDeployPhaseActions() && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayRemainingForceLossFromReserveDeck();
            }
            else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            }
            else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            }
        }
        return scn.GetDSLostPile().size() - before;
    }

    @Test
    public void JoinUsOrDieTakeYourFathersPlaceCausesNoForceLossWhileEmperorAndVaderHere() {
        var scn = GetScenario();
        var tyfp = setUpTakeYourFathersPlace(scn, true, 98302);
        assertEquals(0, ForceLostFromTakeYourFathersPlace(scn, tyfp));
    }

    @Test
    public void JoinUsOrDieTakeYourFathersPlaceStillCausesForceLossWithoutEmperorHere() {
        var scn = GetScenario();
        var tyfp = setUpTakeYourFathersPlace(scn, false, 98303);
        assertEquals(1, ForceLostFromTakeYourFathersPlace(scn, tyfp));
    }

    @Test
    public void JoinUsOrDieTakeYourFathersPlaceDoesNotDepleteLifeForceWhileEmperorAndVaderHere() {
        var scn = GetScenario();
        var tyfp = setUpTakeYourFathersPlace(scn, true, 98304);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(GameConditions.hasGameTextModification(scn.game(), tyfp, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__DOES_NOT_DEPLETE_LIFE_FORCE));
        int lsLifeForce = scn.GetPlayerLifeForceRemaining(scn.LS);
        var action = new TopLevelGameTextAction(tyfp, scn.DS, tyfp.getCardId());
        action.appendEffect(new DepleteLifeForceEffect(action, scn.LS));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        scn.PassAllResponses();
        assertEquals(lsLifeForce, scn.GetPlayerLifeForceRemaining(scn.LS));
        assertFalse(scn.game().isFinished());
    }

    @Test
    public void JoinUsOrDieTakeYourFathersPlaceMayDepleteLifeForceWithoutEmperorHere() {
        var scn = GetScenario();
        var tyfp = setUpTakeYourFathersPlace(scn, false, 98305);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(GameConditions.hasGameTextModification(scn.game(), tyfp, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__DOES_NOT_DEPLETE_LIFE_FORCE));
    }

    @Test
    public void JoinUsOrDieTreatsBattleOnInsignificantRebellionAsBattleOrDuel() {
        var scn = GetScenario();
        var juod = scn.GetDSCard("juod");
        var throne = scn.GetDSCard("throne");
        var rebellion = scn.GetDSCard("rebellion");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(rebellion);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(GameConditions.hasGameTextModification(scn.game(), rebellion, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__BATTLE_IS_BATTLE_OR_DUEL));
        scn.MoveLocationToTable(throne);
        scn.AttachCardsTo(throne, juod);
        scn.DSPass();
        assertTrue(GameConditions.hasGameTextModification(scn.game(), rebellion, ModifyGameTextType.LEGACY__JOIN_US_OR_DIE__BATTLE_IS_BATTLE_OR_DUEL));
    }
}
