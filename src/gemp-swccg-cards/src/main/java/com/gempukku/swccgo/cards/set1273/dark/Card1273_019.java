package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Mission
 * Subtype: Critical
 * Title: Spice Mine Operations
 */
public class Card1273_019 extends AbstractMission {
    public Card1273_019() {
        super(Side.DARK, 0, "Spice Mine Operations", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V, true);
        setGameText("If your [Virtual Block 7] Kessel office on table, deploy on table. Program Trap is canceled. Cards stacked here are 'spice' cards. Once per turn, if you control Extraction Facility, may place a 'spice' card in owner's Lost Pile to draw up to 2 cards from Reserve Deck. Once during your turn, may: use 2 Force to stack a card from Lost Pile here, deploy a non-character card with 'Kessel' in title, and/or 'mine' (opponent draws destiny; draw up to X destiny, where X = 1 plus number of Kessel locations you occupy; if any of the printed destiny numbers match, stack opponent's destiny or top card of your Lost Pile here).");
        addIcons(Icon.SETUP);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.titleContains("Kessel")));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Program_Trap)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Program_Trap)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Program_Trap, Title.Program_Trap);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.controls(game, playerId, Filters.title("Extraction Facility"))
                && GameConditions.hasStackedCards(game, self)
                && GameConditions.hasReserveDeck(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place spice in Lost Pile to draw up to 2");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
            actions.add(action);
        }
        GameTextActionId stackId = GameTextActionId.SPICE_MINE_OPERATIONS__STACK_FROM_LOST_PILE;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canSearchLostPile(game, playerId, self, stackId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, stackId);
            action.setText("Use 2 Force to stack a card from Lost Pile");
            action.appendUsage(new com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(new StackCardFromLostPileEffect(action, self, Filters.any, false));
            actions.add(action);
        }
        GameTextActionId deployId = GameTextActionId.SPICE_MINE_OPERATIONS__DEPLOY_KESSEL_CARD;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, deployId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployId);
            action.setText("Deploy a non-character Kessel card");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.character), Filters.titleContains("Kessel")), true));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }
}
