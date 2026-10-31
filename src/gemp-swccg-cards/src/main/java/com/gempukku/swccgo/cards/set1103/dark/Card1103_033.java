package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Alien
 * Title: Hem Dazon
 */
public class Card1103_033 extends AbstractAlien {
    public Card1103_033() {
        super(Side.DARK, 4, 2, 2, 2, 4, "Hem Dazon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male scout from Cona. As with many Arconas, Hem has succumbed to the power of salt, and addiction indicated by his gold eye color.");
        setGameText("During your deploy phase, may stack 'salt' (a card from hand) face down under Hem. Your total battle destiny here is +1 for each 'salt' card. During your control phase (or if Hem lost), may place all 'salt' cards in your Used Pile.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.ARCONA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new StackedEvaluator(self), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Stack 'salt' from hand");
            action.setActionMsg("Stack a card from hand face down under Hem as 'salt'");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new StackCardFromHandEffect(action, playerId, self, Filters.any, true, false, false, false));
            actions.add(action);
        }

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasStackedCards(game, self)) {

            Collection<PhysicalCard> salt = game.getGameState().getStackedCards(self);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place 'salt' in Used Pile");
            action.setActionMsg("Place all 'salt' cards in Used Pile");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new PutStackedCardsInUsedPileEffect(action, playerId, salt, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self))
                && GameConditions.hasStackedCards(game, self)) {

            Collection<PhysicalCard> salt = game.getGameState().getStackedCards(self);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place 'salt' in Used Pile");
            action.setActionMsg("Place all 'salt' cards in Used Pile");
            action.appendEffect(
                    new PutStackedCardsInUsedPileEffect(action, playerId, salt, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
