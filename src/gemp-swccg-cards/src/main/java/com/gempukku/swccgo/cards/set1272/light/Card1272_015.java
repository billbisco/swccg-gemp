package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Rebel
 * Title: Rahm Kota, Blind Jedi
 */
public class Card1272_015 extends AbstractRebel {
    public Card1272_015() {
        super(Side.LIGHT, 2, 5, 4, 6, 7, "Rahm Kota, Blind Jedi", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setLore("Insurgent leader.");
        setGameText("[Episode I] Jedi are lost. At battlegrounds, Insurgents are immune to attrition < 5. Once per turn, may peek at top card of your Reserve Deck (or if Galen on table, any Reserve Deck); may place that card on top of owner's Used Pile. May not carry weapons.");
        addKeywords(Keyword.INSURGENT, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Keyword.INSURGENT, Filters.at(Filters.battleground)), 5));
        modifiers.add(new MayNotCarryModifier(self, self, Filters.weapon));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toLose = Filters.filterActive(game, self, Filters.and(Filters.Jedi, Icon.EPISODE_I));
            if (!toLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make " + GameUtils.getAppendedNames(toLose) + " lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__RAHM_KOTA__PEEK_AT_RESERVE_DECK;
        String opponent = game.getOpponent(playerId);
        boolean anyReserveDeck = GameConditions.canSpot(game, self, Filters.Galen);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.hasReserveDeck(game, playerId) || (anyReserveDeck && GameConditions.hasReserveDeck(game, opponent)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            if (anyReserveDeck) {
                // Choose target(s)
                action.appendTargeting(
                        new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                            @Override
                            protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                                peekAndMaybePlaceOnUsedPile(action, playerId, cardPileOwner);
                            }
                        });
            } else {
                peekAndMaybePlaceOnUsedPile(action, playerId, playerId);
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    private static void peekAndMaybePlaceOnUsedPile(final TopLevelGameTextAction action, final String playerId, final String cardPileOwner) {
        action.setActionMsg("Peek at top card of " + cardPileOwner + "'s Reserve Deck");
        // Perform result(s)
        action.appendEffect(
                new PeekAtTopCardOfReserveDeckEffect(action, playerId, cardPileOwner) {
                    @Override
                    protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                        action.appendEffect(
                                new PlayoutDecisionEffect(action, playerId,
                                        new YesNoDecision("Do you want to place " + GameUtils.getCardLink(peekedAtCard) + " on owner's Used Pile?") {
                                            @Override
                                            protected void yes() {
                                                action.appendEffect(
                                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, peekedAtCard, Zone.USED_PILE, true));
                                            }
                                        }));
                    }
                });
    }
}
