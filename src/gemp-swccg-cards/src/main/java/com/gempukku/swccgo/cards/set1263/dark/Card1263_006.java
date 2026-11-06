package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Virtual Block 6.3: Mercenary Shadow Pirate. */
public class Card1263_006 extends AbstractAlien {
    public Card1263_006() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Mercenary Shadow Pirate", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("ISB. Female.");
        setGameText("Adds 2 to power of anything she pilots. Opponent may not add destiny draws to power or attrition here. Ice Storm and Sandwhirl are canceled at same and related locations.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.ISB_AGENT, Keyword.FEMALE);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var battleHere = new DuringBattleAtCondition(Filters.sameLocation(self));
        String opponent = game.getOpponent(self.getOwner());
        return List.of(
                new AddsPowerToPilotedBySelfModifier(self, 2),
                new MayNotAddDestinyDrawsToPowerModifier(self, battleHere, opponent),
                new MayNotAddDestinyDrawsToAttritionModifier(self, battleHere, opponent));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCardTargeting(game, effect,
                Filters.or(Filters.Ice_Storm, Filters.Sandwhirl), Filters.sameOrRelatedLocation(self))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter weather = Filters.and(Filters.or(Filters.Ice_Storm, Filters.Sandwhirl),
                Filters.at(Filters.sameOrRelatedLocation(self)));
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, weather)) {
            var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, weather, "Ice Storm or Sandwhirl");
            return Collections.singletonList(action);
        }
        return null;
    }
}
