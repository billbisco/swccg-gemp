package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Toryn Farr
 */
public class Card1220_105 extends AbstractRebel {
    public Card1220_105() {
        super(Side.LIGHT, 4, 2, 1, 2, 3, "Toryn Farr", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Chief Controller at Echo Command. Responsible for communicating orders to the troops. Personally gives firing orders to Ion Cannon Control.");
        setGameText("Adds 2 to power of anything she pilots. [Maintenance] cards may not participate in battle the turn they are deployed and are placed out of play when they leave the table.");
        addIcons(Icon.PILOT, Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.justDeployed(game, effectResult, Filters.icon(Icon.MAINTENANCE))) {
            PhysicalCard deployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (deployed != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Prevent " + GameUtils.getFullName(deployed) + " from battling this turn");
                action.setActionMsg("Prevent " + GameUtils.getCardLink(deployed) + " from participating in battle this turn");
                action.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action,
                                new MayNotParticipateInBattleModifier(self, deployed),
                                "May not participate in battle this turn"));
                actions.add(action);
            }
        }

        List<RequiredGameTextTriggerAction> leaveActions = getPlaceOutOfPlayAction(game, effectResult, self, gameTextSourceCardId);
        if (leaveActions != null) {
            actions.addAll(leaveActions);
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getPlaceOutOfPlayAction(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getPlaceOutOfPlayAction(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToLeaveTableExceptFromSourceCard(game, effectResult, Filters.icon(Icon.MAINTENANCE), self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, Filters.icon(Icon.MAINTENANCE))) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard leaving = result.getCardAboutToLeaveTable();
            if (leaving != null && Filters.icon(Icon.MAINTENANCE).accepts(game, leaving)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getFullName(leaving) + " out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(leaving) + " out of play");
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(leaving);
                                action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, leaving));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
