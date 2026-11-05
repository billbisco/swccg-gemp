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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Alien
 * Title: Mercenary Shadow Pirate
 */
public class Card1263_006 extends AbstractAlien {
    public Card1263_006() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Mercenary Shadow Pirate", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("ISB. Female.");
        setGameText("Adds 2 to power of anything she pilots. Opponent may not add destiny draws to power or attrition here. Ice Storm and Sandwhirl are canceled at same and related locations.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ISB_AGENT, Keyword.FEMALE);
    }

    private Filter cardsToCancel(PhysicalCard self) {
        return Filters.and(Filters.or(Filters.Ice_Storm, Filters.Sandwhirl), Filters.at(Filters.sameOrRelatedLocation(self)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition duringBattleHere = new DuringBattleAtCondition(Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotAddDestinyDrawsToPowerModifier(self, duringBattleHere, opponent));
        modifiers.add(new MayNotAddDestinyDrawsToAttritionModifier(self, duringBattleHere, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter toCancel = cardsToCancel(self);
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, toCancel)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, toCancel, "Ice Storm or Sandwhirl");
            return Collections.singletonList(action);
        }
        return null;
    }
}
