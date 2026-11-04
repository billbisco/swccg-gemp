package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Mandalorian Mishap & Jedi Mind Trick
 */
public class Card1292_011 extends AbstractUsedOrLostInterrupt {
    public Card1292_011() {
        super(Side.LIGHT, 4, "Mandalorian Mishap & Jedi Mind Trick", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        addComboCardTitles("Mandalorian Mishap", "Jedi Mind Trick");
        setGameText("USED: Use 1 Force to suspend [Virtual Block 3] Hutt Bounty or Scum And Villainy for remainder of turn. OR Target a planet site you occupy (even at the beginning of a turn). For remainder of turn, add one [Light Side Force] icon to that site. LOST: If your character of ability > 4 is with opponent's character of lesser ability (except Jabba or Watto), reveal opponent's hand. Select a card there; if opponent does not use or lose 2 Force, it is lost.");
    }

    private static Filter suspendTargets() {
        return Filters.or(Filters.and(Filters.Hutt_Bounty, Icon.LEGACY_BLOCK_3), Filters.Scum_And_Villainy);
    }

    private static Filter planetSiteYouOccupy(String playerId) {
        return Filters.and(Filters.planet_site, Filters.occupies(playerId));
    }

    private PlayInterruptAction addIconAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter siteFilter = planetSiteYouOccupy(playerId);
        if (!GameConditions.canTarget(game, self, siteFilter)) {
            return null;
        }
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Add one [Light Side Force] icon to a planet site");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Target a planet site you occupy", siteFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard site) {
                        action.addAnimationGroup(site);
                        // Allow response(s)
                        action.allowResponses("Add one [Light Side Force] icon to " + GameUtils.getCardLink(site),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action,
                                                        new IconModifier(self, Filters.sameCardId(finalSite), Icon.LIGHT_FORCE, 1),
                                                        "Adds one [Light Side Force] icon to " + GameUtils.getCardLink(finalSite)));
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }

    private boolean isYourCharacterWithLesserAbility(final String playerId, final SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        for (PhysicalCard yourCharacter : Filters.filterActive(game, self, Filters.and(Filters.owner(playerId), Filters.character, Filters.abilityMoreThan(4)))) {
            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), yourCharacter);
            if (Filters.canSpot(game, self, Filters.and(Filters.owner(opponent), Filters.character,
                    Filters.not(Filters.or(Filters.Jabba, Filters.Watto)), Filters.with(yourCharacter), Filters.abilityLessThan(ability)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // USED: Use 1 Force to suspend Hutt Bounty [VB3] or Scum And Villainy
        final Filter suspendFilter = suspendTargets();
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_SUSPENDED, suspendFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Suspend Hutt Bounty or Scum And Villainy");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Hutt Bounty or Scum And Villainy", TargetingReason.TO_BE_SUSPENDED, suspendFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Suspend " + GameUtils.getCardLink(targetedCard) + " for remainder of turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SuspendCardUntilEndOfTurnEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // USED: Add one [Light Side Force] icon to a planet site you occupy
        PlayInterruptAction iconAction = addIconAction(playerId, game, self);
        if (iconAction != null) {
            actions.add(iconAction);
        }

        // LOST: Jedi Mind Trick
        if (GameConditions.hasHand(game, opponent)
                && isYourCharacterWithLesserAbility(playerId, game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal opponent's hand");
            // Allow response(s)
            action.allowResponses("Reveal opponent's hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsRevealed(final List<PhysicalCard> revealedCards) {
                                            if (revealedCards.isEmpty()) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new ChooseArbitraryCardsEffect(action, playerId, "Select a card in opponent's hand", revealedCards, 1, 1) {
                                                        @Override
                                                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                            if (selectedCards.isEmpty()) {
                                                                return;
                                                            }
                                                            final PhysicalCard selectedCard = selectedCards.iterator().next();
                                                            game.getGameState().sendMessage(playerId + " selects " + GameUtils.getCardLink(selectedCard));
                                                            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
                                                            effectsToChoose.add(new UseForceEffect(action, opponent, 2));
                                                            effectsToChoose.add(new LoseForceEffect(action, opponent, 2));
                                                            effectsToChoose.add(new LoseCardFromHandEffect(action, selectedCard) {
                                                                @Override
                                                                public String getText(SwccgGame game) {
                                                                    return "Allow " + GameUtils.getCardLink(selectedCard) + " to be lost";
                                                                }
                                                            });
                                                            action.appendEffect(
                                                                    new ChooseEffectEffect(action, opponent, effectsToChoose));
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // "even at the beginning of a turn"
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            PlayInterruptAction iconAction = addIconAction(playerId, game, self);
            if (iconAction != null) {
                return Collections.singletonList(iconAction);
            }
        }
        return null;
    }
}
