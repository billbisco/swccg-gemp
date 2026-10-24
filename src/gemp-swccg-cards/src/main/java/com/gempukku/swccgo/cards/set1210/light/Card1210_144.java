package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TakeCardFromVoidIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Advance Preparation
 */
public class Card1210_144 extends AbstractUsedOrStartingInterrupt {
    public Card1210_144() {
        super(Side.LIGHT, 6, Title.Advance_Preparation, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Early warning and careful planning allow the Rebels to achieve maximum readiness while still maintaining optimal flexibility.");
        setGameText("USED: Cancel Uncertain Is The Future  STARTING: If Massassi Base Operations on table, deploy from Reserve Deck Death Star Schematics, Massassi War Room, and up to two Effects that deploy for free and are [Immune to Alter]. Place this Interrupt in hand.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Uncertain_Is_The_Future)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (GameConditions.canTargetToCancel(game, self, Filters.Uncertain_Is_The_Future)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Uncertain_Is_The_Future, Title.Uncertain_Is_The_Future);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (!GameConditions.canSpot(game, self, Filters.Massassi_Base_Operations)) {
            return null;
        }

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ADVANCE_PREPARATION_V__STARTING_DEPLOY;
        final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        action.allowResponses("Deploy Death Star Schematics, Massassi War Room, and up to two Effects that deploy for free and are always immune to Alter",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Death_Star_Schematics), true, false));
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.Massassi_War_Room, true, false));
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action,
                                        Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter),
                                        0, 2, true, false));
                        action.appendEffect(
                                new TakeCardFromVoidIntoHandEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}
