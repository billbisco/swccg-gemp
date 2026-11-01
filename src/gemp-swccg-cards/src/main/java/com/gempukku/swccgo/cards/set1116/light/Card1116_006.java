package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Interrupt
 * Subtype: Lost
 * Title: Ambush
 */
public class Card1116_006 extends AbstractLostInterrupt {
    public Card1116_006() {
        super(Side.LIGHT, 3, "Ambush", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Well done. Hold them in the security tower, and keep it quiet. Move.'");
        setGameText("Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
        addIcons(Icon.CLOUD_CITY, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERON_NETT_V__DEPLOY_ABOARD;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy up to two clones");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardsFromReserveDeckEffect(action, Filters.clone, 1, 2, true, true));
                }
            });
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.clone, Filters.participatingInBattle))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                            new AddsBattleDestinyModifier(self, 1), "Add one battle destiny"));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
