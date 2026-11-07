package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelCardResultEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeOperatedOnModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.FailCostEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ForfeitReducedToZeroResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Odin Nesloor & First Aid, final Light master page 35. */
public class Card1261_105 extends AbstractUsedInterrupt {
    public Card1261_105() {
        super(Side.LIGHT, 4, "Odin Nesloor & First Aid", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent(Title.Odin_Nesloor, Uniqueness.UNIQUE), new TitleComponent(Title.First_Aid, Uniqueness.UNIQUE));
        setGameText("If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Cancel an attempt to 'operate' on a character; character is protected from any further attempts until start of opponent's next turn. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            final Filter exteriorOrBGSites = Filters.or(Filters.exterior_site, Filters.battleground_site);
            Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.atLocation(Filters.any), Filters.canBeRelocatedToLocation(exteriorOrBGSites, true, 0)))));

            if (!fromSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("'Transport' characters");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to 'transport' from", Filters.in(fromSites)) {
                            @Override
                            protected void cardSelected(final PhysicalCard fromSite) {
                                final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                        Filters.and(Filters.your(self), Filters.character, Filters.atLocation(fromSite), Filters.canBeRelocatedToLocation(exteriorOrBGSites, true, 0)));
                                Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(exteriorOrBGSites, Filters.not(fromSite)));
                                Collection<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
                                // Figure out which sites any of the cards can be relocated to
                                for (PhysicalCard otherSite : otherSites) {
                                    for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                        if (Filters.canBeRelocatedToLocation(otherSite, true, 0).accepts(game, characterToRelocate)) {
                                            validSites.add(otherSite);
                                            break;
                                        }
                                    }
                                }
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose site to 'transport' to", Filters.in(validSites)) {
                                            @Override
                                            protected void cardTargeted(final int siteTargetingGroupId, final PhysicalCard toSite) {
                                                Collection<PhysicalCard> validCharactersToRelocate = new LinkedList<PhysicalCard>();
                                                // Figure out which characters can be relocated to the other site
                                                for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                                    if (Filters.canBeRelocatedToLocation(toSite, true, 0).accepts(game, characterToRelocate)) {
                                                        validCharactersToRelocate.add(characterToRelocate);
                                                    }
                                                }
                                                action.appendTargeting(
                                                        new TargetCardsOnTableEffect(action, playerId, "Choose characters to 'transport'", 1, Integer.MAX_VALUE, Filters.in(validCharactersToRelocate)) {
                                                            @Override
                                                            protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                                action.addAnimationGroup(cardsToRelocate);
                                                                action.addAnimationGroup(toSite);

                                                                // At this point, mark the card as 'played'. If the subsequent costs
                                                                // fail, we need to make sure we still can't play the card again this turn.
                                                                // (by default, gemp records cards played only after all costs have been successfully paid)
                                                                action.appendCost(new RecordCardsBeingPlayedEffect(action, Collections.singleton(self)));

                                                                // Pay cost(s)
                                                                // Draw destiny to determine cost to 'transport'
                                                                action.appendCost(
                                                                        new DrawDestinyEffect(action, playerId) {
                                                                            @Override
                                                                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                final GameState gameState = game.getGameState();
                                                                                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                                if (totalDestiny == null) {
                                                                                    gameState.sendMessage("Result: 'Transport' cost not paid due to failed destiny draw");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                final float moveCost = modifiersQuerying.getRelocateBetweenLocationsCost(gameState, cardsToRelocate, fromSite, toSite, totalDestiny);
                                                                                gameState.sendMessage("'Transport' cost: " + GuiUtils.formatAsString(moveCost));

                                                                                if (!GameConditions.canUseForce(game, playerId, moveCost)) {

                                                                                    gameState.sendMessage("Result: Player unable to pay 'transport' cost");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                gameState.sendMessage("Result: Player may choose to 'transport'");
                                                                                // Ask player to use Force for transport or Interrupt is lost
                                                                                action.appendCost(
                                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                                new YesNoDecision((moveCost > 0) ? "Do you want to use " + GuiUtils.formatAsString(moveCost) + " Force to 'transport'?" : "Do you want to 'transport'?") {
                                                                                                    @Override
                                                                                                    protected void yes() {
                                                                                                        gameState.sendMessage(playerId + " chooses to 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        if (moveCost > 0) {
                                                                                                            action.appendCost(
                                                                                                                    new UseForceEffect(action, playerId, moveCost));
                                                                                                        }
                                                                                                        // Allow response(s)
                                                                                                        action.allowResponses("'Transport' " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite),
                                                                                                                new RespondablePlayCardEffect(action) {
                                                                                                                    @Override
                                                                                                                    protected void performActionResults(Action targetingAction) {
                                                                                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                                                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                                                                                        Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                                                                                                        PhysicalCard finalToSite = action.getPrimaryTargetCard(siteTargetingGroupId);

                                                                                                                        // Perform result(s)
                                                                                                                        action.appendEffect(
                                                                                                                                new RelocateBetweenLocationsEffect(action, finalCharacters, finalToSite));
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                                    }

                                                                                                    @Override
                                                                                                    protected void no() {
                                                                                                        gameState.sendMessage(playerId + " chooses to not 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardInVoidEffect(action, self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                                        action.appendCost(
                                                                                                                new FailCostEffect(action));
                                                                                                    }
                                                                                                }
                                                                                        )
                                                                                );
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Mode 1: restore your character's forfeit that was just reduced to 0
        if (TriggerConditions.forfeitJustReducedToZero(game, effectResult, Filters.and(Filters.your(self), Filters.character))) {
            final PhysicalCard cardToRestore = ((ForfeitReducedToZeroResult) effectResult).getCard();
            final boolean free = forfeitReducedByPermanentWeapon(game, cardToRestore);
            if ((free || GameConditions.canUseForce(game, playerId, 1))
                    && GameConditions.canTarget(game, self, cardToRestore)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Restore " + GameUtils.getFullName(cardToRestore) + "'s forfeit to normal");
                action.setImmuneTo(Title.Sense);
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", cardToRestore) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Pay cost(s)
                                if (!free) {
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 1));
                                }
                                // Allow response(s)
                                action.allowResponses("Restore " + GameUtils.getCardLink(cardTargeted) + "'s forfeit to normal",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RestoreForfeitToNormalEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * A character's forfeit counts as reduced by a [Permanent Weapon] weapon when the character is 'hit' and a forfeit
     * reset/reduction affecting it comes from a card firing its permanent weapon (the source is the card carrying the
     * permanent weapon, not a separate weapon card).
     */
    private static boolean forfeitReducedByPermanentWeapon(SwccgGame game, PhysicalCard character) {
        if (!character.isHit())
            return false;
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        for (ModifierType type : new ModifierType[]{ModifierType.UNMODIFIABLE_FORFEIT_VALUE, ModifierType.FORFEIT_VALUE}) {
            for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, type, character)) {
                PhysicalCard source = modifier.getSource(gameState);
                if (source != null
                        && !Filters.weapon.accepts(game, source)
                        && Filters.hasPermanentWeapon.accepts(game, source)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Mode 2: cancel an attempt to 'operate' on a character
        if (TriggerConditions.isPerformingGameTextActionType(game, effect, Filters.any, GameTextActionId.ANY_CARD__OPERATE_ON_CHARACTER)) {
            final PhysicalCard patient = operatedOnCharacter(game, effect.getAction());
            if (patient != null) {
                final String opponent = game.getOpponent(playerId);
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel attempt to 'operate' on " + GameUtils.getFullName(patient));
                action.addAnimationGroup(patient);
                // Allow response(s)
                action.allowResponses("Cancel attempt to 'operate' on " + GameUtils.getCardLink(patient),
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelCardResultEffect(action, effect));
                                action.appendEffect(
                                        new AddUntilStartOfPlayersNextTurnModifierEffect(action, opponent,
                                                new MayNotTargetToBeOperatedOnModifier(self, Filters.sameCardId(patient)),
                                                "Protects " + GameUtils.getCardLink(patient) + " from attempts to 'operate'"));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Gets the character targeted to be 'operated' on by the specified action.
     */
    private static PhysicalCard operatedOnCharacter(SwccgGame game, Action operateAction) {
        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> primaryTargets = operateAction.getAllPrimaryTargetCards();
        if (primaryTargets == null)
            return null;
        for (Map<PhysicalCard, Set<TargetingReason>> targets : primaryTargets.values()) {
            for (Map.Entry<PhysicalCard, Set<TargetingReason>> target : targets.entrySet()) {
                if (Filters.character.accepts(game, target.getKey())) {
                    return target.getKey();
                }
            }
        }
        return null;
    }
}
