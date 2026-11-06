package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used
 * Title: Mandalorian Mishap
 */
public class Card1230_106 extends AbstractUsedInterrupt {
    public Card1230_106() {
        super(Side.LIGHT, 5, "Mandalorian Mishap", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most space-faring adventurers meet their fate with a calm, quiet dignity.");
        setGameText("Suspend all Effects with 'gangster' in game text for remainder of turn. OR Target a bounty hunter and/or gangster at a site. For remainder of turn, targets may not fire weapons and their game text is canceled.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        // Standard word references also match plurals and possessives.
        final Filter gangsterEffects = Filters.and(Filters.Effect, Filters.or(Filters.gameTextContains("gangster"), Filters.gameTextContains("gangsters")));

        if (GameConditions.canSpot(game, self, gangsterEffects)) {
            final PlayInterruptAction suspend = new PlayInterruptAction(game, self);
            suspend.setText("Suspend Effects with gangster in game text");
            suspend.allowResponses("Suspend all Effects with 'gangster' in game text for remainder of turn",
                    new RespondablePlayCardEffect(suspend) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            Collection<PhysicalCard> onTable = Filters.filterActive(game, self, gangsterEffects);
                            for (PhysicalCard card : onTable) {
                                suspend.appendEffect(
                                        new SuspendCardUntilEndOfTurnEffect(suspend, card));
                            }
                            suspend.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(suspend,
                                            new SuspendsCardModifier(self, gangsterEffects),
                                            "Suspends Effects with 'gangster' in game text"));
                        }
                    }
            );
            actions.add(suspend);
        }

        Filter targetFilter = Filters.and(Filters.or(Filters.bounty_hunter, Filters.gangster), Filters.at(Filters.site));
        if (GameConditions.canTarget(game, self, targetFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a bounty hunter or gangster");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose bounty hunter or gangster", targetFilter) {
                @Override
                protected void cardTargeted(int group, PhysicalCard card) {
                    finishTargeting(action, self, group);
                }
            });
            actions.add(action);
        }

        final PlayInterruptAction pair = new PlayInterruptAction(game, self);
        pair.setText("Target a bounty hunter and gangster");
        final int[] gangsterGroup = {-1};
        Filter bountyHunter = Filters.and(Filters.bounty_hunter, Filters.at(Filters.site), new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
                Filter gangster = Filters.and(Filters.gangster, Filters.not(card), Filters.atSameSite(card), Filters.canBeTargetedBy(self));
                if (gangsterGroup[0] >= 0) {
                    PhysicalCard target = pair.getPrimaryTargetCard(gangsterGroup[0]);
                    return target != null && gangster.accepts(state, modifiers, target);
                }
                return Filters.canSpot(game, self, gangster);
            }
        });
        if (GameConditions.canTarget(game, self, bountyHunter)) {
            pair.appendTargeting(new TargetCardOnTableEffect(pair, playerId, "Choose bounty hunter", bountyHunter) {
                @Override
                protected void cardTargeted(final int bountyGroup, PhysicalCard card) {
                    Filter gangster = Filters.and(Filters.gangster, Filters.at(Filters.site), new Filter() {
                        @Override
                        public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard candidate) {
                            PhysicalCard hunter = pair.getPrimaryTargetCard(bountyGroup);
                            return hunter != null && candidate != hunter && Filters.atSameSite(hunter).accepts(state, modifiers, candidate);
                        }
                    });
                    pair.appendTargeting(new TargetCardOnTableEffect(pair, playerId, "Choose gangster at the same site", gangster) {
                        @Override
                        protected void cardTargeted(int group, PhysicalCard targeted) {
                            gangsterGroup[0] = group;
                            finishTargeting(pair, self, bountyGroup, group);
                        }
                    });
                }
            });
            actions.add(pair);
        }
        return actions;
    }

    private void finishTargeting(final PlayInterruptAction action, final PhysicalCard self, final int... groups) {
        action.allowResponses("Cancel target game text and prevent firing weapons for remainder of turn", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                for (int group : groups) {
                    PhysicalCard target = targetingAction.getPrimaryTargetCard(group);
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotFireWeaponsModifier(self, target), "Target may not fire weapons"));
                    action.appendEffect(new CancelGameTextUntilEndOfTurnEffect(action, target));
                }
            }
        });
    }

}
