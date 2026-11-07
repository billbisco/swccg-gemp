package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.set1116.dark.Card1116_041;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5: Coruscant: Chancellor's Office. */
public class Card1250_030 extends Card1116_041 {
    public Card1250_030() {
        setLocationDarkSideGameText("Once per game, may deploy [Virtual Block 5] Sidious here from Reserve Deck; reshuffle.");
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        // The Original Virtual restriction on canceling Dark drains is absent from this printing.
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerId, SwccgGame game,
                                                                            PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__CHANCELLORS_OFFICE__DEPLOY_SIDIOUS;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            return null;
        }
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Deploy Block 5 Sidious from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Sidious, Icon.LEGACY_BLOCK_5),
                Filters.sameCardId(self), 0, true));
        return List.of(action);
    }
}
