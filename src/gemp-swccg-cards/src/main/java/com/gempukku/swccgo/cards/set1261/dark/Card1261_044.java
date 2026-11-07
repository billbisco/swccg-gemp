package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelCardResultEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.FailCostEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ForfeitReducedToZeroResult;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Legacy Block 6.1 Lana Dobreed & Sacrifice. */
public class Card1261_044 extends AbstractUsedInterrupt {
    private static final Filter DESTINATION = Filters.or(Filters.exterior_site, Filters.battleground_site);
    private static final Filter YODA_OR_OBIWAN = Filters.or(Filters.Yoda, Filters.ObiWan);

    public Card1261_044() {
        super(Side.DARK, 4, "Lana Dobreed & Sacrifice", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        addComboCardTitles(Title.Lana_Dobreed, Title.Sacrifice);
        setGameText("If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Lose 2 Force to cancel an attempt by Yoda or Obi-Wan to cancel your Interrupt or your just drawn weapon destiny. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile.");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if (!TriggerConditions.forfeitJustReducedToZero(game, effectResult, Filters.and(Filters.your(self), Filters.character)))
            return null;
        var character = ((ForfeitReducedToZeroResult) effectResult).getCard();
        boolean free = reducedByPermanentWeapon(game, character);
        if (!GameConditions.canTarget(game, self, character) || (!free && !GameConditions.canUseForce(game, playerId, 1)))
            return null;

        var action = new PlayInterruptAction(game, self);
        action.setText("Restore " + GameUtils.getFullName(character) + "'s forfeit to normal");
        action.setImmuneTo(Title.Sense);
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose character", character) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                action.addAnimationGroup(target);
                if (!free)
                    action.appendCost(new UseForceEffect(action, playerId, 1));
                action.allowResponses("Restore " + GameUtils.getCardLink(target) + "'s forfeit to normal",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new RestoreForfeitToNormalEffect(action,
                                        action.getPrimaryTargetCard(targetGroupId)));
                            }
                        });
            }
        });
        return List.of(action);
    }

    /** True if the character's forfeit was just reduced by a hit from a [Permanent Weapon] weapon now firing. */
    private static boolean reducedByPermanentWeapon(SwccgGame game, PhysicalCard character) {
        WeaponFiringState firing = game.getGameState().getWeaponFiringState();
        return firing != null && firing.getPermanentWeaponFiring() != null
                && firing.getTargets() != null && firing.getTargets().contains(character);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        boolean cancelingInterrupt = TriggerConditions.isAttemptToCancelCardBeingPlayed(game, effect, YODA_OR_OBIWAN,
                Filters.and(Filters.owner(playerId), Filters.Interrupt, Filters.not(self)));
        boolean cancelingWeaponDestiny = TriggerConditions.isAttemptToCancelDestiny(game, effect, YODA_OR_OBIWAN,
                playerId, DestinyType.WEAPON_DESTINY);
        if (!cancelingInterrupt && !cancelingWeaponDestiny)
            return null;

        var canceler = effect.getAction().getActionSource();
        var action = new PlayInterruptAction(game, self);
        action.setText("Cancel " + GameUtils.getFullName(canceler) + "'s attempt to cancel your "
                + (cancelingInterrupt ? "Interrupt" : "weapon destiny"));
        action.appendCost(new LoseForceEffect(action, playerId, 2, true));
        action.allowResponses("Cancel " + GameUtils.getCardLink(canceler) + "'s attempt to cancel",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new CancelCardResultEffect(action, effect));
                    }
                });
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.isDuringYourPhase(game, self, Phase.MOVE))
            return null;

        var destinations = Filters.filterTopLocationsOnTable(game, DESTINATION);
        var routes = new LinkedHashMap<PhysicalCard, Map<PhysicalCard, Collection<PhysicalCard>>>();
        for (var from : Filters.filterTopLocationsOnTable(game, Filters.site)) {
            var reachable = new LinkedHashMap<PhysicalCard, Collection<PhysicalCard>>();
            for (var to : destinations) {
                if (from == to)
                    continue;
                var eligible = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character,
                        Filters.atLocation(from), Filters.canBeRelocatedToLocation(to, true, 0)));
                if (!eligible.isEmpty())
                    reachable.put(to, eligible);
            }
            if (!reachable.isEmpty())
                routes.put(from, reachable);
        }
        if (routes.isEmpty())
            return null;

        var action = new PlayInterruptAction(game, self);
        action.setText("'Transport' characters");
        action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose site to 'transport' from",
                Filters.in(routes.keySet())) {
            @Override
            protected void cardSelected(PhysicalCard from) {
                chooseDestination(action, playerId, self, from, routes.get(from));
            }
        });
        return List.of(action);
    }

    private void chooseDestination(PlayInterruptAction action, String playerId, PhysicalCard self,
                                   PhysicalCard from, Map<PhysicalCard, Collection<PhysicalCard>> routes) {
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site to 'transport' to",
                Filters.in(routes.keySet())) {
            @Override
            protected void cardTargeted(int destinationId, PhysicalCard to) {
                action.appendTargeting(new TargetCardsOnTableEffect(action, playerId, "Choose characters to 'transport'",
                        1, Integer.MAX_VALUE, Filters.in(routes.get(to))) {
                    @Override
                    protected void cardsTargeted(int charactersId, Collection<PhysicalCard> characters) {
                        action.addAnimationGroup(characters);
                        action.addAnimationGroup(to);
                        payTransport(action, playerId, self, from, to, characters, destinationId, charactersId);
                    }
                });
            }
        });
    }

    private void payTransport(PlayInterruptAction action, String playerId, PhysicalCard self,
                              PhysicalCard from, PhysicalCard to, Collection<PhysicalCard> characters,
                              int destinationId, int charactersId) {
        action.appendCost(new RecordCardsBeingPlayedEffect(action, List.of(self)));
        action.appendCost(new DrawDestinyEffect(action, playerId) {
            @Override
            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> values, Float total) {
                if (total == null) {
                    failTransport(action, playerId, self);
                    return;
                }
                float cost = game.getModifiersQuerying().getRelocateBetweenLocationsCost(
                        game.getGameState(), characters, from, to, total);
                if (!GameConditions.canUseForce(game, playerId, cost)) {
                    failTransport(action, playerId, self);
                    return;
                }
                String question = cost == 0 ? "Do you want to 'transport'?"
                        : "Do you want to use " + GuiUtils.formatAsString(cost) + " Force to 'transport'?";
                action.appendCost(new PlayoutDecisionEffect(action, playerId, new YesNoDecision(question) {
                    @Override
                    protected void yes() {
                        if (cost > 0)
                            action.appendCost(new UseForceEffect(action, playerId, cost));
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new RelocateBetweenLocationsEffect(action,
                                        action.getPrimaryTargetCards(charactersId),
                                        action.getPrimaryTargetCard(destinationId)));
                            }
                        });
                    }

                    @Override
                    protected void no() {
                        failTransport(action, playerId, self);
                    }
                }));
            }
        });
    }

    private void failTransport(PlayInterruptAction action, String playerId, PhysicalCard self) {
        action.appendCost(new PutCardInVoidEffect(action, self));
        action.appendCost(new PutCardFromVoidInLostPileEffect(action, playerId, self));
        action.appendCost(new FailCostEffect(action));
    }
}
