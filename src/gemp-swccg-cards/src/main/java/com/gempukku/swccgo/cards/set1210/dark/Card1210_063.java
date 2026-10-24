package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.BattleEffect;
import com.gempukku.swccgo.logic.effects.PayInitiateBattleCostEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Lost
 * Title: Local Trouble
 */
public class Card1210_063 extends AbstractLostInterrupt {
    public Card1210_063() {
        super(Side.DARK, 4, Title.Local_Trouble, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Look like somebody's beginning to take an interest in your handiwork.' Imperial stormtroopers coerce local residents to assist them in the apprehension of Rebel scum.");
        setGameText("At the beginning of your battle phase, use 1 Force to initate a battle between two Imperials and an opponent's character present at same interior site. For remainder of turn, no other battles may occur here and opponent takes no battle damage here.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter locationFilter = localTroubleLocationFilter(game, self);
        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.BATTLE, playerId)
                && !GameConditions.isDuringBattle(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpotLocation(game, locationFilter)) {

            PlayInterruptAction action = createLocalTroubleAction(playerId, game, self);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private Filter localTroubleLocationFilter(SwccgGame game, PhysicalCard self) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LEGACY__LOCAL_TROUBLE__MAY_PLAY_AT_EXTERIOR_TATOOINE_SITES)) {
            return Filters.or(Filters.interior_site, Filters.and(Filters.exterior_site, Filters.Tatooine_site));
        }
        return Filters.interior_site;
    }

    private PlayInterruptAction createLocalTroubleAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PhysicalCard> playableLocations = new LinkedList<PhysicalCard>();
        for (PhysicalCard location : Filters.filterTopLocationsOnTable(game, localTroubleLocationFilter(game, self))) {
            if (isLocalTroublePlayableAt(playerId, game, self, location)) {
                playableLocations.add(location);
            }
        }
        if (playableLocations.isEmpty()) {
            return null;
        }
        if (playableLocations.size() == 1) {
            return createLocalTroubleActionAt(playerId, game, self, playableLocations.get(0));
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Initiate Local Trouble battle");
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose location", Filters.in(playableLocations)) {
                    @Override
                    protected void cardTargeted(final int locationTargetGroupId, PhysicalCard targetedLocation) {
                        appendLocalTroubleBattle(action, playerId, game, self, targetedLocation);
                    }
                });
        return action;
    }

    private boolean isLocalTroublePlayableAt(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard location) {
        if (game.getModifiersQuerying().mayNotInitiateBattleAtLocation(game.getGameState(), location, playerId)) {
            return false;
        }

        float battleCost = game.getModifiersQuerying().getInitiateBattleCost(game.getGameState(), location, playerId, false);
        if (battleCost > 0 && !GameConditions.canUseForce(game, playerId, battleCost)) {
            return false;
        }

        return GameConditions.canSpot(game, self, 2, localTroubleImperialFilter(self, playerId, location))
                && GameConditions.canTarget(game, self, localTroubleOpponentFilter(self, playerId, location));
    }

    private Filter localTroubleImperialFilter(PhysicalCard self, String playerId, PhysicalCard location) {
        return Filters.and(Filters.your(self), Filters.Imperial, Filters.at(location),
                Filters.canParticipateInBattleAt(location, playerId), Filters.canBeTargetedBy(self));
    }

    private Filter localTroubleOpponentFilter(PhysicalCard self, String playerId, PhysicalCard location) {
        final Filter notMayNotBeBattled = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !modifiersQuerying.mayNotBeBattled(gameState, physicalCard);
            }
        };
        return Filters.and(Filters.opponents(self), Filters.character, Filters.at(location),
                Filters.or(Filters.mayBeBattled, Filters.hasAbility, Filters.icon(Icon.PRESENCE)),
                notMayNotBeBattled,
                Filters.canParticipateInBattleAt(location, playerId), Filters.canBeTargetedBy(self));
    }

    private PlayInterruptAction createLocalTroubleActionAt(final String playerId, final SwccgGame game, final PhysicalCard self, final PhysicalCard battleLocation) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Initiate Local Trouble battle");
        appendLocalTroubleBattle(action, playerId, game, self, battleLocation);
        return action;
    }

    private void appendLocalTroubleBattle(final PlayInterruptAction action, final String playerId, final SwccgGame game, final PhysicalCard self, final PhysicalCard battleLocation) {
        final int battleLocationPermId = battleLocation.getPermanentCardId();
        final Filter imperialFilter = localTroubleImperialFilter(self, playerId, battleLocation);
        final Filter opponentFilter = localTroubleOpponentFilter(self, playerId, battleLocation);
        final String opponent = game.getOpponent(playerId);

        action.appendTargeting(
                new TargetCardsOnTableEffect(action, playerId, "Choose two Imperials", 2, 2, imperialFilter) {
                    @Override
                    protected void cardsTargeted(final int imperialTargetGroupId, Collection<PhysicalCard> targetedImperials) {
                        action.addAnimationGroup(targetedImperials);
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentFilter) {
                                    @Override
                                    protected void cardTargeted(final int opponentTargetGroupId, PhysicalCard targetedOpponent) {
                                        action.addAnimationGroup(targetedOpponent);
                                        action.appendCost(
                                                new PayInitiateBattleCostEffect(action, battleLocation, playerId, false));
                                        action.allowResponses("Allow " + GameUtils.getAppendedNames(targetedImperials) + " to battle " + GameUtils.getCardLink(targetedOpponent),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        Collection<PhysicalCard> finalImperials = action.getPrimaryTargetCards(imperialTargetGroupId);
                                                        PhysicalCard finalOpponent = action.getPrimaryTargetCard(opponentTargetGroupId);
                                                        PhysicalCard currentBattleLocation = game.findCardByPermanentId(battleLocationPermId);
                                                        if (finalImperials == null || finalImperials.size() != 2 || finalOpponent == null || currentBattleLocation == null) {
                                                            return;
                                                        }

                                                        Collection<PhysicalCard> participants = new LinkedList<PhysicalCard>();
                                                        participants.addAll(finalImperials);
                                                        participants.add(finalOpponent);

                                                        action.appendEffect(
                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                        new MayNotInitiateBattleAtLocationModifier(self, Filters.sameCardId(currentBattleLocation)),
                                                                        "No other battles may occur at " + GameUtils.getCardLink(currentBattleLocation) + " this turn"));
                                                        action.appendEffect(
                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                        new NoBattleDamageModifier(self, Filters.sameCardId(currentBattleLocation), opponent),
                                                                        "Opponent takes no battle damage here"));
                                                        action.appendEffect(
                                                                new BattleEffect(action, currentBattleLocation, true, participants, Collections.emptyList()));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }
}
