package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Device
 * Title: Comlink
 */
public class Card1114_021 extends AbstractDevice {
    public Card1114_021() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Comlink", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Hand-held communication device. Military comlinks have scrambling and variable frequency programming for secure communication during battles or emergencies.");
        setGameText("Deploy on an Imperial. While present at a battleground (or deployed on Emperor), Imperials lost from Life Force satisfy an additional 2 Force loss. If Luke deploys using your objective, he deploys to Vader's site (if possible). Once per game, may take Rise, My Friend into hand from Reserve Deck; reshuffle.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Imperial;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Rise, My Friend into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Rise, My Friend"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
