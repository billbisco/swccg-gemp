package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Diversionary Tactics
 */
public class Card1112_008 extends AbstractUsedOrLostInterrupt {
    public Card1112_008() {
        super(Side.LIGHT, 4, Title.Diversionary_Tactics, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rebel pilots understand that what they lack in numbers, they must make up for in strategy. They often use diversions to remove dangerous opponents from a conflict.");
        setGameText("USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have two unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. Exclude one starship from that battle if destiny < 4.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter pilotedAtSystem = Filters.and(Filters.starship, Filters.piloted, Filters.at(Filters.system), Filters.canBeTargetedBy(self));
        if (GameConditions.canTarget(game, self, pilotedAtSystem)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 1 from hyperspeed");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a piloted starship", pilotedAtSystem) {
                @Override
                protected void cardTargeted(int targetGroupId, final PhysicalCard starship) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                    new HyperspeedModifier(self, Filters.sameCardId(starship), -1),
                                    "makes " + starship.getBlueprint().getTitle() + " hyperspeed -1"));
                        }
                    });
                }
            });
            actions.add(action);
        }

        Filter yourUniqueFighter = Filters.and(Filters.your(self), Filters.unique, Filters.piloted, Filters.starfighter,
                Filters.or(Filters.Rebel, Icon.REPUBLIC), Filters.participatingInBattle, Filters.at(Filters.system));
        if (GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.canSpot(game, self, 2, yourUniqueFighter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Draw destiny to exclude a starship");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null || totalDestiny >= 4)
                                return;
                            Filter excludeFilter = Filters.and(Filters.starship, Filters.participatingInBattle, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE));
                            Collection<PhysicalCard> choices = Filters.filterActive(game, self, excludeFilter);
                            if (choices.isEmpty())
                                return;
                            action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose starship to exclude", choices) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.appendEffect(new ExcludeFromBattleEffect(action, selectedCard));
                                }
                            });
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
