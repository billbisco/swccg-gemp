package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Imperial
 * Title: Janus Greejatus
 */
public class Card1240_007 extends AbstractImperial {
    public Card1240_007() {
        super(Side.DARK, 2, 3, 2, 4, 5, "Janus Greejatus", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setInfluence(2);
        setLore("Imperial council member. Very cruel at heart. Has lusted for power his entire life. Predictable and controllable.");
        setGameText("Agendas: logistics, rule. Once per turn, if at Conference Room and you just drew a non-Epic Event destiny < your total Influence here, may use 2 Force to cancel and redraw that destiny or use 4 Force to add 2 to that destiny.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.LOGISTICS, Agenda.RULE));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if (!(effectResult instanceof DestinyDrawnResult)
                || !TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                || !GameConditions.isAtLocation(game, self, Filters.Death_Star_Conference_Room)
                || !GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            return null;
        }

        DestinyDrawnResult drawn = (DestinyDrawnResult) effectResult;
        DestinyType type = drawn.getDestinyType();
        if (type == DestinyType.EPIC_EVENT_DESTINY || type == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
            return null;
        }
        if (drawn.getCard() == null) {
            return null;
        }

        PhysicalCard here = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
        if (here == null) {
            return null;
        }
        float influence = game.getModifiersQuerying().getTotalInfluenceAtLocation(game.getGameState(), playerId, here);
        float destiny = drawn.getCard().getDestinyValueToUse();
        if (!(destiny < influence)) {
            return null;
        }

        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel and redraw destiny");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }

        if (GameConditions.canUseForce(game, playerId, 4)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 2 to destiny");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            action.appendEffect(
                    new ModifyDestinyEffect(action, 2));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
