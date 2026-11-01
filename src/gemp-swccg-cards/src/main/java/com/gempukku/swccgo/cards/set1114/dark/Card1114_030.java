package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sith Fury
 */
public class Card1114_030 extends AbstractUsedOrLostInterrupt {
    public Card1114_030() {
        super(Side.DARK, 4, "Sith Fury", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("USED: If you just drew a character for destiny, take that card into hand to cancel and redraw that destiny. LOST: Once per game, use 4 Force to deploy a Dark Jedi from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERE_IS_NO_TRY_V__DEPLOY_LASER_GATE_OR_POLITICAL_EFFECT;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 4)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Deploy a Dark Jedi from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 4));
            action.allowResponses("Deploy a Dark Jedi from Reserve Deck", new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Dark_Jedi, true));
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
