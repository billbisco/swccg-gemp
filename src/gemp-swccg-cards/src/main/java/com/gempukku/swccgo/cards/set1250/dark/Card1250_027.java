package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5: Sith Fury (V). */
public class Card1250_027 extends AbstractUsedOrLostInterrupt {
    public Card1250_027() {
        super(Side.DARK, 4, Title.Sith_Fury, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("At his peak, no one could stand up to the Dark Lord of the Sith. His superior tactics devastated those who opposed him.");
        setGameText("USED: If you just drew a character for destiny, take that card into hand to cancel and redraw that destiny. LOST: Once per game, use 4 Force to deploy a Dark Jedi from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.LEGACY_BLOCK_5);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Preserve the OVS deployment sequence, with this card's own search/once-game identity.
        GameTextActionId id = GameTextActionId.LEGACY__SITH_FURY_V__DEPLOY_DARK_JEDI;
        if (GameConditions.isOncePerGame(game, self, id)
                && GameConditions.canUseForce(game, playerId, 4)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, id, CardSubtype.LOST);
            action.setText("Deploy a Dark Jedi from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 4));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Dark_Jedi, true));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.character)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Take character destiny into hand and redraw");
            action.appendCost(new TakeDestinyCardIntoHandEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelDestinyAndCauseRedrawEffect(action));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
