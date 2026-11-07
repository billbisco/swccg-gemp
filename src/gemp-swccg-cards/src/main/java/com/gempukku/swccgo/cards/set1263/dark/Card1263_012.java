package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.3: Operational As Planned (V). */
public class Card1263_012 extends AbstractUsedInterrupt {
    private static final Filter GROTTO_OR_IMMEDIATE = Filters.or(
            Filters.and(Filters.title("Grotto Werribee"), Icon.LEGACY_BLOCK_6), Filters.Immediate_Effect);
    private static final Filter FREE_EFFECT = Filters.and(Filters.Effect, Filters.deploysForFree);

    public Card1263_012() {
        super(Side.DARK, 5, Title.Operational_As_Planned, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"We shall double our efforts.\"");
        setGameText("Once per turn, if taken into hand using Protocol Failure, may immediately reveal this Interrupt to return Protocol Failure from Lost Pile to hand. Use 1 Force to take [Virtual Block 6] Grotto Werribee or an Immediate Effect into hand from Reserve Deck; reshuffle. OR Use 2 Force to take an Effect that deploys for free into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        var actions = new ArrayList<PlayInterruptAction>();
        addSearch(actions, playerId, game, self, GameTextActionId.LEGACY__OPERATIONAL_AS_PLANNED__TAKE_GROTTO_OR_IMMEDIATE,
                1, GROTTO_OR_IMMEDIATE, "Take Block 6 Grotto Werribee or an Immediate Effect");
        addSearch(actions, playerId, game, self, GameTextActionId.LEGACY__OPERATIONAL_AS_PLANNED__TAKE_FREE_EFFECT,
                2, FREE_EFFECT, "Take an Effect that deploys for free");
        return actions;
    }

    private void addSearch(List<PlayInterruptAction> actions, String playerId, SwccgGame game, PhysicalCard self,
                           GameTextActionId id, int cost, Filter targets, String text) {
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, cost)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) return;
        var action = new PlayInterruptAction(game, self, id);
        action.setText(text);
        action.appendCost(new UseForceEffect(action, playerId, cost));
        action.allowResponses(text + " from Reserve Deck", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, targets, true));
            }
        });
        actions.add(action);
    }
}
