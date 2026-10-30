package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Debris Zone
 */
public class Card1104_035 extends AbstractUsedInterrupt {
    public Card1104_035() {
        super(Side.DARK, 5, Title.Debris_Zone, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("A reactor core explosion in a destroyed AT-AT causes a plasma release which shatters its armor, showering the immediate area with molten projectiles.");
        setGameText("Once per turn, may take one Crash Landing or Weapon Malfunction into hand from Reserve Deck; reshuffle. OR if you have a piloted AT-AT on Hoth, target artillery weapon at a related site is lost. (Immune to Sense.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Sense);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId takeId = GameTextActionId.LEGACY__DEBRIS_ZONE_V__TAKE_INTERRUPT;

        if (GameConditions.isOncePerTurn(game, self, playerId, self.getCardId(), takeId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take Crash Landing or Weapon Malfunction into hand");
            action.setImmuneTo(Title.Sense);
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.allowResponses("Take Crash Landing or Weapon Malfunction into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.or(Filters.Crash_Landing, Filters.title(Title.Weapon_Malfunction)), true));
                        }
                    }
            );
            actions.add(action);
        }

        Filter atAtFilter = Filters.and(Filters.your(self), Filters.AT_AT, Filters.piloted, Filters.on(Title.Hoth));
        Filter artilleryFilter = Filters.and(Filters.artillery_weapon, Filters.at(Filters.relatedSiteTo(self, atAtFilter)));
        if (GameConditions.canSpot(game, self, atAtFilter)
                && GameConditions.canTarget(game, self, artilleryFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make artillery weapon lost");
            action.setImmuneTo(Title.Sense);
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose artillery weapon", artilleryFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
