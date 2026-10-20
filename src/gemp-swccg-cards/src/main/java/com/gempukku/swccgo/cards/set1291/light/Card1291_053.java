package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Interrupt
 * Subtype: Used
 * Title: This Is More Like It
 */
public class Card1291_053 extends AbstractUsedInterrupt {
    public Card1291_053() {
        super(Side.LIGHT, 3, Title.This_Is_More_Like_It, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You like me because I'm a scoundrel. There aren't enough scoundrels in your life.' 'I happen to like nice men.' 'I'm a nice man.' 'No you're not, you're ..'");
        setGameText("Cancel an attempt to target a scoundrel (or a starship or vehicle piloted by a scoundrel) with a weapon. OR At any time during your turn, deploy a [Scoundrel] Effect from under Scoundrel's Luck (or transfer a [Scoundrel] Effect to another scoundrel on table).");
        addIcons(Icon.DAGOBAH);
    }

    private Filter scoundrelOrPilotedByScoundrel(PhysicalCard self) {
        return Filters.or(Filters.scoundrel,
                Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.hasPiloting(self, Filters.scoundrel)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isTargetedByWeapon(game, effect, scoundrelOrPilotedByScoundrel(self), Filters.weapon)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel weapon targeting");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new CancelWeaponTargetingEffect(action));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (!GameConditions.isDuringYourTurn(game, playerId)) {
            return actions;
        }

        final PhysicalCard luck = Filters.findFirstFromAllOnTable(game, Filters.title("Scoundrel's Luck"));
        Filter scoundrelEffect = Filters.and(Icon.SCOUNDREL, Filters.Effect);
        if (luck != null && GameConditions.hasStackedCards(game, luck, scoundrelEffect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy a [Scoundrel] Effect from under Scoundrel's Luck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new DeployStackedCardEffect(action, luck, Filters.and(Icon.SCOUNDREL, Filters.Effect), false));
                        }
                    });
            actions.add(action);
        }

        Filter effectOnTable = Filters.and(scoundrelEffect, Filters.attachedTo(Filters.scoundrel));
        if (GameConditions.canTarget(game, self, effectOnTable)
                && GameConditions.canSpot(game, self, 2, Filters.scoundrel)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Transfer a [Scoundrel] Effect to another scoundrel");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose [Scoundrel] Effect to transfer", effectOnTable) {
                        @Override
                        protected void cardTargeted(final int effectGroupId, final PhysicalCard effectToTransfer) {
                            Filter anotherScoundrel = Filters.and(Filters.scoundrel, Filters.not(Filters.hasAttached(effectToTransfer)));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose scoundrel to transfer to", anotherScoundrel) {
                                        @Override
                                        protected void cardTargeted(final int scoundrelGroupId, PhysicalCard newHost) {
                                            action.allowResponses("Transfer " + GameUtils.getCardLink(effectToTransfer) + " to " + GameUtils.getCardLink(newHost),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            PhysicalCard finalEffect = action.getPrimaryTargetCard(effectGroupId);
                                                            PhysicalCard finalHost = action.getPrimaryTargetCard(scoundrelGroupId);
                                                            action.appendEffect(new AttachCardFromTableEffect(action, finalEffect, finalHost));
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}
