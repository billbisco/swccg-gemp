package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Interrupt
 * Subtype: Lost
 * Title: I'm On The Leader
 */
public class Card1103_035 extends AbstractLostInterrupt {
    public Card1103_035() {
        super(Side.DARK, 3, Title.Im_On_The_Leader, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("In the defense of the Death Star at the Battle of Yavin, Darth Vader took the lead attacker, supported by his wingmen Black 2 and Black 3.");
        setGameText("Cancel the game text of a character at same system as Vader for remainder of turn.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.and(Filters.character, Filters.at(Filters.sameSystemAs(self, Filters.Vader)));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.at(Filters.system)))
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel a character's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
