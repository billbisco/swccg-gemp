package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Subtype: Immediate
 * Title: Redeemed Apprentice
 */
public class Card1281_033 extends AbstractImmediateEffect {
    public Card1281_033() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Redeemed Apprentice", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("'I know there is good in you. The Emperor hasn't driven it from you fully.'");
        setGameText("If you just forfeited a character (or opponent just drew destiny for Elis Helrot) deploy on table. Whenever you must lose Force from a (Virtual Block 4) objective targeting your non-jedi character, reduce that loss by 1. Elis Herlot and Nabrun Leids are canceled. (Immune to Control).");
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.justForfeited(game, effectResult, playerId, Filters.character)
                || TriggerConditions.isDestinyJustDrawnFor(game, effectResult, opponent, Filters.Elis_Helrot)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, null, null);
            if (action != null) {
                action.setText("Deploy due to " + GameUtils.getFullName(self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Elis_Helrot, Filters.Nabrun_Leids)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REDEEMED_APPRENTICE__REDUCE_FORCE_LOSS;
        if (TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.and(Filters.Objective, Icon.LEGACY_BLOCK_4))
                && GameConditions.canReduceForceLoss(game)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reduce Force loss by 1");
            action.appendEffect(new ReduceForceLossEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
