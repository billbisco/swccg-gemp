package com.gempukku.swccgo.cards.set1106.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Interrupt
 * Subtype: Used
 * Title: Levitation Attack (V)
 */
public class Card1106_043 extends AbstractUsedInterrupt {
    public Card1106_043() {
        super(Side.DARK, 4, Title.Levitation_Attack, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader used his mastery of the Force to levitate objects from around the room. This calculated attack was designed to break Luke’s spirit as well as his body.");
        setGameText("Deploy up to two devices (for free) from under your Starting Effect.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        Filter stackedFilter = Filters.device;

        if (startingEffect != null
                && GameConditions.hasStackedCards(game, startingEffect, stackedFilter)) {
            final PhysicalCard stackedOn = startingEffect;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy devices from under Starting Effect");
            action.allowResponses("Deploy up to two devices for free from under Starting Effect",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployStackedCardsEffect(action, stackedOn, Filters.device, 0, 2, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
