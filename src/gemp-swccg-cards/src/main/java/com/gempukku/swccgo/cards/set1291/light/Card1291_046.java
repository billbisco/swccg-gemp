package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.StealCardIntoOwnLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.MayPlayToCancelForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.EffectResult.Type;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;
import com.gempukku.swccgo.logic.actions.TriggerAction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Alien
 * Title: Max Rebo (V)
 */
public class Card1291_046 extends AbstractAlien {
    public Card1291_046() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, "Max Rebo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Ortolan musician and gambler. Leader of The Max Rebo Band. Signed a lifetime contract to Jabba in exchange for unlimited food.");
        setGameText("If just lost (even from Life Force), opponent 'steals' Max into Lost Pile. While at a battleground site: you may play Bith Shuffle or Ommni Box to cancel a Force drain at a related location, they are Lost Interrupts, and once per turn may take one of them into hand from Reserve Deck; reshuffle.");
        addKeywords(Keyword.MUSICIAN, Keyword.GAMBLER, Keyword.LEADER);
        setSpecies(Species.ORTOLAN);
    }

    private static Filter yourBithShuffleOrOmmniBox(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Bith_Shuffle, Filters.Ommni_Box));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atBattlegroundSite = new AtCondition(self, Filters.battleground_site);
        Filter bithShuffleOrOmmniBox = yourBithShuffleOrOmmniBox(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, bithShuffleOrOmmniBox, atBattlegroundSite, Filters.relatedLocation(self)));
        modifiers.add(new LostInterruptModifier(self, bithShuffleOrOmmniBox, atBattlegroundSite));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MAX_REBO_V__TAKE_INTERRUPT;

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.battleground_site)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Bith Shuffle or Ommni Box into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Bith_Shuffle, Filters.Ommni_Box), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {
            return Collections.singletonList(getStealIntoLostPileAction(game, self, gameTextSourceCardId));
        }
        return null;
    }

    @Override
    public List<TriggerAction> getLostFromLifeForceRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (effectResult.getType() == Type.FORCE_LOST
                && ((LostForceResult) effectResult).getCardLost() == self) {
            return Collections.singletonList(getStealIntoLostPileAction(game, self, self.getCardId()));
        }
        return null;
    }

    private RequiredGameTextTriggerAction getStealIntoLostPileAction(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Opponent 'steals' Max into Lost Pile");
        action.setPerformingPlayer(opponent);
        // Perform result(s)
        action.appendEffect(
                new StealCardIntoOwnLostPileEffect(action, opponent, self));
        return action;
    }
}
