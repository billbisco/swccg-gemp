package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Into The Garbage Chute, Flyboy
 */
public class Card1210_155 extends AbstractUsedInterrupt {
    public Card1210_155() {
        super(Side.LIGHT, 6, Title.Into_The_Garbage_Chute_Flyboy, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leia led an unorthodox escape into a 'garbage masher' on the detention level. 'What an incredible smell you've discovered!'");
        setGameText("Add one battle destiny at Incinerator, Trash Compactor, Watto's Junkyard, or a swamp.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter locationFilter = Filters.or(Filters.Incinerator, Filters.title(Title.Trash_Compactor), Filters.title(Title.Wattos_Junkyard), Filters.swamp);
        if (GameConditions.isDuringBattleAt(game, locationFilter)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            action.allowResponses("Add one battle destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
