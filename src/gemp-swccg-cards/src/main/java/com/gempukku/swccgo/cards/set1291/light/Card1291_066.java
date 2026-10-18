package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Hunt Down The Dark Acolyte
 */
public class Card1291_066 extends AbstractMission {
    public Card1291_066() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Hunt Down The Dark Acolyte", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on Begun, The Clone War Has. Opponent's characters of ability > 4 are 'acolytes.' Once during your move phase, if no card here, may target Anakin (or Obi-Wan) and initiate a duel with an 'acolyte' present. Each player draws two destiny (those destinies may not be canceled). Add ability. If your total higher, stack 'acolyte' here face down (Mission complete). Otherwise, place target in your Used Pile.\n{Reward}: May immediately take a non-character card into hand from Reserve Deck; reshuffle.\n{Turn the Tide}: If a card with the same persona as card here is deployed, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId rewardId = GameTextActionId.HUNT_DOWN_THE_DARK_ACOLYTE__TAKE_NON_CHARACTER;
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, rewardId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, rewardId);
            action.setText("Take a non-character into hand from Reserve Deck");
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.not(Filters.character), true));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }
}
