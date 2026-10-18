package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.TotalInfluenceAtConferenceRoomEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Dark Collaboration
 */
public class Card1272_008 extends AbstractUsedInterrupt {
    public Card1272_008() {
        super(Side.DARK, 5, Title.Dark_Collaboration, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial leaders are ruthlessly effective when working together. Tarkin, Motti and Tagge presented a deadly combination as they plotted to 'crush the Rebellion.'");
        setGameText("Use 1 Force to take into hand an Imperial Council Member (or Bewil) or deploy a site with 'ISB' in title or game text from Reserve Deck; reshuffle. OR During battle, your total power is +X, where X = your total influence at Conference Room (limit +6).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeActionId = GameTextActionId.LEGACY__DARK_COLLABORATION_V__TAKE_COUNCIL_MEMBER_OR_BEWIL;
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeActionId);
            action.setText("Take Council Member or Bewil into hand");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.or(Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.titleContains("Bewil")), true));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId deployActionId = GameTextActionId.LEGACY__DARK_COLLABORATION_V__DEPLOY_ISB_SITE;
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployActionId, true, false)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, deployActionId);
            action.setText("Deploy an ISB site from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action,
                                            Filters.and(Filters.site, Filters.or(Filters.titleContains("ISB"), Filters.gameTextContains("ISB"))),
                                            true));
                        }
                    });
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add influence to total power");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            float amount = Math.min(6, TotalInfluenceAtConferenceRoomEvaluator.get(game, self, playerId));
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new TotalPowerModifier(self, Filters.battleLocation, amount, playerId),
                                            "Your total power is +X (limit +6)"));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
