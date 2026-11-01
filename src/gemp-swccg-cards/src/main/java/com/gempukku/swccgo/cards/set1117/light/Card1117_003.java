package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: An Unusual Amount Of Fear
 */
public class Card1117_003 extends AbstractNormalEffect {
    public Card1117_003() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "An Unusual Amount Of Fear", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with \"A Jedi's\" in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Set 9] Wipe Them Out, All Of Them and opponent's [Virtual Set 16] Effects here are canceled. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    private Filter expansion(final ExpansionSet set) {
        return new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == set;
            }
        };
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Jedi_Council_Chamber, Filters.titleContains("Yoda's Hut"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.title("Wipe Them Out, All Of Them"), expansion(ExpansionSet.VIRTUAL_ORIGINAL_SET_9))));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Effect, Filters.here(self), expansion(ExpansionSet.VIRTUAL_ORIGINAL_SET_16))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__UNUSUAL_FEAR__TAKE_A_JEDIS_CARD;
        Filter jedisCard = Filters.and(Filters.titleContains("A Jedi's"), Filters.not(Filters.title("A Jedi's Resilience")));
        if (GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card with \"A Jedi's\" into hand");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, jedisCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
