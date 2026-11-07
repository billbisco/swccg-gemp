package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: The Force Unleashed
 */
public class Card1240_041 extends AbstractNormalEffect {
    private static final String CHOKE = "Choke";
    private static final String DRAIN = "Drain of life";

    public Card1240_041() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Force Unleashed",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("");
        setGameText("Deploy on table. During battle, you may play Lightsaber Parry, Tarkin's Orders, and There Is No Conflict as follows: 'If Vader or Galen present: USED: Target a vehicle or starship present. Draw destiny. If destiny > 2, target 'thrown' (hit and adds its printed defense value to your total power). LOST: Target opponent's character present and choose (then draw destiny): If destiny > ability, target 'choked' (lost). OR If destiny +1 > ability, target 'drained of life' (hit and opponent loses 2 Force).'");
        addIcons(Icon.CLOUD_CITY);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter interrupts = Filters.and(Filters.your(self), Filters.Interrupt,
                Filters.or(Filters.title("Lightsaber Parry"), Filters.title("Tarkin's Orders"),
                        Filters.title("There Is No Conflict")));
        return List.of(
                new LegacyMayPlayInterruptAsModifier(self, interrupts, CardSubtype.USED,
                        new LegacyMayPlayInterruptAsModifier.PlayMode() {
                            @Override
                            public PlayInterruptAction createAction(String playerId, SwccgGame game,
                                                                    PhysicalCard source, PhysicalCard interrupt) {
                                return createThrow(playerId, game, source, interrupt);
                            }
                        }),
                new LegacyMayPlayInterruptAsModifier(self, interrupts, CardSubtype.LOST,
                        new LegacyMayPlayInterruptAsModifier.PlayMode() {
                            @Override
                            public PlayInterruptAction createAction(String playerId, SwccgGame game,
                                                                    PhysicalCard source, PhysicalCard interrupt) {
                                return createCharacterPlay(playerId, game, source, interrupt);
                            }
                        }));
    }

    private static Filter presenceInBattle(String playerId, SwccgGame game, PhysicalCard source) {
        if (!GameConditions.isDuringBattle(game) || !GameConditions.canDrawDestiny(game, playerId)) {
            return null;
        }
        Filter present = Filters.present(game.getGameState().getBattleState().getBattleLocation());
        return GameConditions.canSpot(game, source, Filters.and(Filters.or(Filters.Vader, Filters.Galen), present))
                ? present : null;
    }

    private static PlayInterruptAction createThrow(String playerId, SwccgGame game, PhysicalCard source,
                                                    PhysicalCard interrupt) {
        Filter present = presenceInBattle(playerId, game, source);
        if (present == null) {
            return null;
        }
        Filter targets = Filters.and(present, Filters.or(Filters.vehicle, Filters.starship),
                Filters.canBeTargetedBy(interrupt, TargetingReason.TO_BE_HIT));
        if (!GameConditions.canSpot(game, interrupt, targets)) {
            return null;
        }

        PlayInterruptAction action = new PlayInterruptAction(game, interrupt, CardSubtype.USED);
        action.setText("Throw a vehicle or starship");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose vehicle or starship",
                TargetingReason.TO_BE_HIT, targets) {
            @Override
            protected void cardTargeted(int group, PhysicalCard card) {
                action.addAnimationGroup(card);
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard target = action.getPrimaryTargetCard(group);
                        action.appendEffect(new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards,
                                                         List<Float> values, Float total) {
                                if (total != null && total > 2) {
                                    action.appendEffect(new HitCardEffect(action, target, interrupt));
                                    action.appendEffect(new ModifyTotalPowerUntilEndOfBattleEffect(action,
                                            printedDefense(target), playerId,
                                            "Add the thrown card's printed defense value to total power"));
                                }
                            }
                        });
                    }
                });
            }
        });
        return action;
    }

    private static float printedDefense(PhysicalCard target) {
        var blueprint = target.getBlueprint();
        Float maneuver = blueprint.getManeuver();
        Float armor = blueprint.getArmor();
        return Math.max(maneuver == null ? 0 : maneuver, armor == null ? 0 : armor);
    }

    private static PlayInterruptAction createCharacterPlay(String playerId, SwccgGame game, PhysicalCard source,
                                                            PhysicalCard interrupt) {
        Filter present = presenceInBattle(playerId, game, source);
        if (present == null) {
            return null;
        }
        Filter canChoke = Filters.canBeTargetedBy(interrupt, TargetingReason.TO_BE_LOST);
        Filter canDrain = Filters.canBeTargetedBy(interrupt, TargetingReason.TO_BE_HIT);
        Filter targets = Filters.and(present, Filters.opponents(playerId), Filters.character,
                Filters.or(canChoke, canDrain));
        if (!GameConditions.canSpot(game, interrupt, targets)) {
            return null;
        }

        PlayInterruptAction action = new PlayInterruptAction(game, interrupt, CardSubtype.LOST);
        action.setText("Choke or drain an opponent's character");
        action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose opponent's character", targets) {
            @Override
            protected void cardSelected(PhysicalCard card) {
                List<String> choices = new ArrayList<>(2);
                if (canChoke.accepts(game, card)) {
                    choices.add(CHOKE);
                }
                if (canDrain.accepts(game, card)) {
                    choices.add(DRAIN);
                }
                action.appendTargeting(new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Choose before drawing destiny", choices.toArray(String[]::new), 0) {
                            @Override
                            protected void validDecisionMade(int index, String choice) {
                                targetCharacter(playerId, action, interrupt, card, CHOKE.equals(choice));
                            }
                        }));
            }
        });
        return action;
    }

    private static void targetCharacter(String playerId, PlayInterruptAction action, PhysicalCard interrupt,
                                        PhysicalCard card, boolean choke) {
        TargetingReason reason = choke ? TargetingReason.TO_BE_LOST : TargetingReason.TO_BE_HIT;
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target the chosen character",
                reason, Filters.sameCardId(card)) {
            @Override
            protected boolean getUseShortcut() {
                return true;
            }

            @Override
            protected void cardTargeted(int group, PhysicalCard chosen) {
                action.addAnimationGroup(chosen);
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard target = action.getPrimaryTargetCard(group);
                        action.appendEffect(new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return List.of(target);
                            }

                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards,
                                                         List<Float> values, Float total) {
                                if (total == null) {
                                    return;
                                }
                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), target);
                                if (choke && total > ability) {
                                    action.appendEffect(new LoseCardFromTableEffect(action, target));
                                } else if (!choke && total + 1 > ability) {
                                    action.appendEffect(new HitCardEffect(action, target, interrupt));
                                    action.appendEffect(new LoseForceEffect(action, game.getOpponent(playerId), 2));
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
