package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Oo-ta Goo-ta, Solo?
 */
public class Card1103_041 extends AbstractUsedOrLostInterrupt {
    public Card1103_041() {
        super(Side.DARK, 5, Title.Oota_Goota_Solo, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Greedo cheskopokuta klees ruya Solo. Hoko yanee boopa gush Cantina. Cheeco wa Solo's anye nyuma Greedo vakee. Jabba kul steeka et en anpaw.");
        setGameText("USED: During battle at a site where your alien bounty hunter (except Boba Fett) is with opponent's smuggler, opponent may not draw more than one battle destiny. LOST: During battle at a site where you have an alien bounty hunter, add one battle destiny.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        Filter yourAlienBountyHunterExceptBobaFett = Filters.and(Filters.your(self), Filters.alien, Filters.bounty_hunter,
                Filters.not(Filters.Boba_Fett), Filters.participatingInBattle,
                Filters.with(self, Filters.and(Filters.opponents(self), Filters.smuggler)));

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canSpot(game, self, yourAlienBountyHunterExceptBobaFett)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Limit opponent to one battle destiny");
            // Allow response(s)
            action.allowResponses("Make opponent draw no more than one battle destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotDrawMoreThanBattleDestinyModifier(self, 1, opponent),
                                            "Opponent may not draw more than one battle destiny"));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.alien, Filters.bounty_hunter))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
