package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Alien
 * Title: Watto (V)
 */
public class Card1116_067 extends AbstractAlien {
    public Card1116_067() {
        super(Side.DARK, 3, 2, 3, 2, 4, "Watto", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("'Free' on You're A Slave? is treated as '2 Force.' Once per turn, may shuffle Reserve Deck and draw destiny. If destiny is odd, for remainder of turn your Force drains at [Episode I] sites are +1; if even, may take card drawn into hand.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Shuffle and draw destiny");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new ShuffleReserveDeckEffect(action));
            action.appendEffect(new DrawDestinyEffect(action, playerId) {
                @Override
                protected void destinyDraws(SwccgGame game, java.util.List<PhysicalCard> destinyCardDraws, java.util.List<Float> destinyDrawValues, Float totalDestiny) {
                    if (totalDestiny == null) {
                        return;
                    }
                    if (((int) totalDestiny.floatValue()) % 2 != 0) {
                        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                new ForceDrainModifier(self, Filters.and(Filters.site, com.gempukku.swccgo.common.Icon.EPISODE_I), 1, playerId),
                                "Force drains at Episode I sites are +1"));
                    } else if (!destinyCardDraws.isEmpty()) {
                        action.appendEffect(new TakeDestinyCardIntoHandEffect(action));
                    }
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
