package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: General Tagge
 */
public class Card1210_010 extends AbstractImperial {
    public Card1210_010() {
        super(Side.DARK, 1, 3, 3, 3, 4, Title.Tagge, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Oversees defense operations of Death Star. Outstanding tactician. No-nonsense leader. Member of the House of Tagge, a powerful noble family and corporate conglomerate.");
        setGameText("During your control phase, may use 1 Force to take an Imperial trooper without armor into hand from Reserve Deck; reshuffle. Once per game, may place all your non-[Immune to Alter] Effects in Used Pile.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId takeTrooper = GameTextActionId.LEGACY__GENERAL_TAGGE_V__TAKE_TROOPER;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, takeTrooper, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeTrooper)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeTrooper);
            action.setText("Take Imperial trooper into hand");
            action.setActionMsg("Take an Imperial trooper without armor into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Imperial, Filters.trooper, Filters.hasNoArmor), true));
            actions.add(action);
        }

        GameTextActionId placeEffects = GameTextActionId.LEGACY__GENERAL_TAGGE_V__PLACE_EFFECTS;
        Filter effectsFilter = Filters.and(Filters.your(playerId), Filters.Effect, Filters.not(Filters.immune_to_Alter));
        if (GameConditions.isOncePerGame(game, self, placeEffects)
                && GameConditions.canSpot(game, self, effectsFilter)) {

            Collection<PhysicalCard> effects = Filters.filterActive(game, self, effectsFilter);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, placeEffects);
            action.setText("Place Effects in Used Pile");
            action.setActionMsg("Place all your non-Immune to Alter Effects in Used Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action, playerId, effects));
            actions.add(action);
        }

        return actions;
    }
}
