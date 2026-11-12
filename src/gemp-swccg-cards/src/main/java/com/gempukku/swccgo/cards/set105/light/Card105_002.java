package com.gempukku.swccgo.cards.set105.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardsAwayEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: First Anthology
 * Type: Interrupt
 * Subtype: Lost
 * Title: Hit And Run
 * <p>
 * Text as printed on the white-border First Anthology preview card (May 1997), which differs from the later Special Edition printing (Bill 2026-11-12; wiki: First Anthology). "Weapons phase" is the 1997 name of the weapons segment of a battle; a move away is paid at normal use of the Force, so the card plays as the earlier implementation.
 */
public class Card105_002 extends AbstractLostInterrupt {
    public Card105_002() {
        super(Side.LIGHT, 3, "Hit And Run", Uniqueness.UNIQUE, ExpansionSet.FIRST_ANTHOLOGY, Rarity.PV);
        setLore("Many pilots for the Rebellion learned their skills using modified T-47s and other airspeeders. Being able to weave in and out of combat has become second nature to them.");
        setGameText("Just after the weapons phase of a battle, you may move any or all of your starships there away, at normal use of the Force. ('Hit' cards must still be lost.)");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.starship, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.justAfterWeaponsSegmentOfBattle(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(filter, Filters.movableAsMoveAway(playerId, false, 0, Filters.any)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Move starships away");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new MoveCardsAwayEffect(action, playerId, filter, false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
