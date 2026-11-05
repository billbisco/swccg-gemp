package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacySpiceCards;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Device
 * Title: Kessel Surveillance System
 */
public class Card1273_010 extends AbstractLocationDevice {
    /** Spice Mine Administrator is Moruth Doole's printed title on the Virtual Block 7.3 sheet. */
    static final Filter SPICE_MINE_ADMINISTRATOR = Filters.titleContains("Moruth Doole");

    public Card1273_010() {
        super(Side.DARK, 0, "Kessel Surveillance System", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Remote surveillance viewers with droid controllers supplement security. Can activate alarms and automated weapons when needed, bringing help to endangered locations.");
        setGameText("Deploy on a Kessel site. Opponent's Force drains are -1 here. Once per turn, if you are about to lose Force from Reserve Deck, may place a 'spice' card in owner's Lost Pile to make that Force satisfy 1 additional Force loss. Once per game, may deploy Spice Mine Administrator here from Reserve Deck; reshuffle.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Kessel_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameLocation(self), -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // A Force loss of 2 or more: one card lost from Reserve Deck satisfies 2 of it
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KESSEL_SURVEILLANCE_SYSTEM__SPICE_SATISFIES_FORCE_LOSS;
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && LegacySpiceCards.countSpiceCards(game, self) > 0) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place a 'spice' card in Lost Pile to satisfy 1 additional Force loss");
            action.setActionMsg("Place a 'spice' card in owner's Lost Pile so that 1 Force lost from Reserve Deck satisfies 2 Force loss");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, LegacySpiceCards.SPICE_MINE_OPERATIONS_FILTER) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendCost(
                                    new PutStackedCardInLostPileEffect(action, selectedCard.getOwner(), selectedCard, false));
                            action.appendEffect(
                                    new ReduceForceLossEffect(action, playerId, 2));
                            action.appendEffect(
                                    new LoseForceFromReserveDeckEffect(action, playerId, 1, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KESSEL_SURVEILLANCE_SYSTEM__DEPLOY_SPICE_MINE_ADMINISTRATOR;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Spice Mine Administrator here from Reserve Deck");
            action.setActionMsg("Deploy Spice Mine Administrator to " + GameUtils.getCardLink(self.getAttachedTo()) + " from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, SPICE_MINE_ADMINISTRATOR, Filters.sameLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
