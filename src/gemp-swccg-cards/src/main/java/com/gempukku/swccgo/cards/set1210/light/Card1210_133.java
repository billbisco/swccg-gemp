package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeExcludedFromBattle;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Obi-Wan's Cape
 */
public class Card1210_133 extends AbstractNormalEffect {
    public Card1210_133() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Obi-Wan's Cape", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("A symbol of the noble Jedi and his mysterious powers.");
        setGameText("Deploy on Obi-Wan. Obi-Wan is defense value +1 and may not be excluded from battle. During opponent's turn, may place this Effect in Lost Pile to cancel a just drawn duel, lightsaber combat, or weapon destiny draw here.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.ObiWan;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.ObiWan;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, attachedTo, 1));
        modifiers.add(new MayNotBeExcludedFromBattle(self, attachedTo));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOpponentsTurn(game, playerId)
                && TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.canCancelDestiny(game, playerId)
                && effectResult instanceof DestinyDrawnResult) {
            DestinyType destinyType = ((DestinyDrawnResult) effectResult).getDestinyType();
            if (destinyType == DestinyType.DUEL_DESTINY
                    || destinyType == DestinyType.LIGHTSABER_COMBAT_DESTINY
                    || destinyType == DestinyType.WEAPON_DESTINY) {
                boolean here = Filters.here(self).accepts(game, game.getGameState().getBattleLocation());
                if (destinyType == DestinyType.WEAPON_DESTINY) {
                    here = TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.here(self))
                            || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.here(self));
                }
                else if (game.getGameState().getDuelState() != null && game.getGameState().getDuelState().getLocation() != null) {
                    here = Filters.here(self).accepts(game, game.getGameState().getDuelState().getLocation());
                }
                else if (game.getGameState().getLightsaberCombatState() != null && game.getGameState().getLightsaberCombatState().getLocation() != null) {
                    here = Filters.here(self).accepts(game, game.getGameState().getLightsaberCombatState().getLocation());
                }
                if (here) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Cancel destiny");
                    action.appendCost(
                            new PlaceCardInLostPileFromTableEffect(action, self));
                    action.appendEffect(
                            new CancelDestinyEffect(action));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
