package com.gempukku.swccgo.cards.set1110.light;

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
import com.gempukku.swccgo.logic.GameUtils;
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
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Interrupt
 * Subtype: Used
 * Title: Mandalorian Mishap
 */
public class Card1110_006 extends AbstractUsedInterrupt {
    public Card1110_006() {
        super(Side.LIGHT, 5, "Mandalorian Mishap", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most space-faring adventurers meet their fate with a calm, quiet dignity.");
        setGameText("Suspend all Effects with 'gangster' in game text for remainder of turn. OR Target a bounty hunter and/or gangster at a site. For remainder of turn, targets may not fire weapons and their game text is canceled.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter gangsterEffects = Filters.and(Filters.Effect, Filters.gameTextContains("gangster"));

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
            final PlayInterruptAction targetAction = new PlayInterruptAction(game, self);
            targetAction.setText("Target a bounty hunter or gangster");
            targetAction.appendTargeting(
                    new TargetCardOnTableEffect(targetAction, playerId, "Choose bounty hunter or gangster", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            targetAction.addAnimationGroup(targetedCard);
                            targetAction.allowResponses("Cancel game text of " + GameUtils.getCardLink(targetedCard) + " and prevent firing weapons",
                                    new RespondablePlayCardEffect(targetAction) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = targetAction.getPrimaryTargetCard(targetGroupId);
                                            targetAction.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(targetAction,
                                                            new MayNotFireWeaponsModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " not fire weapons"));
                                            targetAction.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(targetAction, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(targetAction);
        }
        return actions;
    }
}
