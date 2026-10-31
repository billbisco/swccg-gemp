package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LightSideGoesFirstEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachDuelDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Epic Event
 * Title: It Is The Future You See
 */
public class Card1112_013 extends AbstractEpicEventDeployable {
    public Card1112_013() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.It_Is_The_Future_You_See, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Yoda opened himself to the flow of the Force, seeing what was and what would be.");
        setGameText("If you deployed only 1 (non-[Coruscant], non-[Special Edition]) location, play this Epic Event instead of a Starting Interrupt. Deploy Battle Plan, Do, Or Do Not, and an [Immune to Alter] Effect from Reserve Deck. Insert cards and your [Permanent Weapon] and [Maintenance] cards are lost. You go first. Choose one: Jedi: Deploy on table. At start of your turn, turn all cards here face up. During your turn, may use 1 Force for each card stacked here to stack a card from hand face up here. Instead of using X Force, may turn X cards here face down. Agent of Evil: Take Luke and a site into hand from Reserve Deck. Deploy on opponent's side of table; their total battle and duel destiny is +3.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    public boolean playableAsStartingInterrupt(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Collection<PhysicalCard> locations = Filters.filterAllOnTable(game, Filters.and(Filters.owner(playerId), Filters.location));
        if (locations.size() != 1)
            return false;
        PhysicalCard only = locations.iterator().next();
        return !Filters.or(Icon.CORUSCANT, Icon.SPECIAL_EDITION).accepts(game, only);
    }

    @Override
    public PlayCardAction getStartingInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Play It Is The Future You See");
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Battle_Plan, true));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Do_Or_Do_Not, true));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immuneToCardTitle(Title.Alter)), true));
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                List<PhysicalCard> lost = new LinkedList<PhysicalCard>();
                for (PhysicalCard card : new LinkedList<PhysicalCard>(game.getGameState().getReserveDeck(playerId))) {
                    if (Filters.or(Filters.insertCard, Icon.PERMANENT_WEAPON, Icon.MAINTENANCE).accepts(game, card))
                        lost.add(card);
                }
                if (!lost.isEmpty()) {
                    game.getGameState().removeCardsFromZone(lost);
                    for (PhysicalCard card : lost) {
                        game.getGameState().addCardToZone(card, Zone.LOST_PILE, playerId);
                    }
                }
            }
        });
        action.appendEffect(new LightSideGoesFirstEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Luke, true));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.site, true));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        if (self.getZone() == Zone.SIDE_OF_TABLE && self.getZoneOwner() != null && !self.getOwner().equals(self.getZoneOwner())) {
            String opponent = game.getOpponent(self.getOwner());
            modifiers.add(new TotalBattleDestinyModifier(self, 3, opponent));
            modifiers.add(new EachDuelDestinyModifier(self, 3, opponent));
        }
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        if (self.getZone() == Zone.SIDE_OF_TABLE && playerId.equals(self.getZoneOwner())
                && TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
                && !Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED_FACE_DOWN).isEmpty()) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Turn stacked cards face up");
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    Collection<PhysicalCard> faceDown = Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED_FACE_DOWN);
                    for (PhysicalCard card : faceDown) {
                        game.getGameState().removeCardsFromZone(Collections.singleton(card));
                        game.getGameState().stackCard(card, self, false, false, false);
                    }
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (!playerId.equals(self.getOwner()) || self.getZone() != Zone.SIDE_OF_TABLE || !playerId.equals(self.getZoneOwner()))
            return actions;
        if (!GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && !GameConditions.isDuringYourTurn(game, playerId))
            return actions;

        int stacked = game.getGameState().getStackedCards(self).size();
        if (GameConditions.hasHand(game, playerId)
                && (stacked == 0 || GameConditions.canUseForce(game, playerId, stacked))) {
            final int cost = stacked;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Stack a card from hand");
            if (cost > 0)
                action.appendCost(new UseForceEffect(action, playerId, cost));
            action.appendEffect(new StackCardFromHandEffect(action, playerId, self));
            actions.add(action);
        }

        Collection<PhysicalCard> faceUp = Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED);
        if (!faceUp.isEmpty()) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Turn a stacked card face down to activate 1 Force");
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    PhysicalCard card = Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED).iterator().next();
                    game.getGameState().removeCardsFromZone(Collections.singleton(card));
                    game.getGameState().stackCard(card, self, true, false, false);
                }
            });
            action.appendEffect(new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }
        return actions;
    }
}
