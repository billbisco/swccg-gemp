package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DefendingBattleAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.BattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Torent (V)
 */
public class Card1271_007 extends AbstractImperial {
    public Card1271_007() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Sergeant Torent", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior watch trooper at Death Star's command center. Monitors external sensor data, scanning for Rebel activity. Vigilant in his duties.");
        setGameText("Power +2 when defending a battle at a mobile site or prison. Once per turn, if you use We Have A Prisoner to capture a character, reduce battle damage and attrition against you by that captive's forfeit.");
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEATH_STAR_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new PowerModifier(self,
                new DefendingBattleAtCondition(self, Filters.or(Filters.mobile_site, Filters.prison)), 2));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (effectResult.getType() != EffectResult.Type.CAPTURED || !game.getGameState().isDuringBattle())
            return null;
        CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
        GameTextActionId id = GameTextActionId.LEGACY__SERGEANT_TORENT_V__REDUCE_BATTLE_DAMAGE;
        if (!playerId.equals(result.getPerformingPlayerId())
                || result.getSourceCard() == null
                || !Filters.We_Have_A_Prisoner.accepts(game, result.getSourceCard())
                || !GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id))
            return null;

        float amount = Math.max(0, game.getModifiersQuerying().getForfeit(game.getGameState(), result.getCapturedCard()));
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, id);
        action.setText("Reduce battle damage and attrition by captive's forfeit");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                new BattleDamageModifier(self, -amount, playerId), null));
        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                new AttritionModifier(self, -amount, playerId), null));
        return Collections.singletonList(action);
    }
}
