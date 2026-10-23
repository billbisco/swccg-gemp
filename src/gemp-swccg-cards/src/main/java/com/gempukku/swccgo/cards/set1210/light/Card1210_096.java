package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Droid
 * Title: IL-19
 */
public class Card1210_096 extends AbstractDroid {
    public Card1210_096() {
        super(Side.LIGHT, 4, 1, 2, 3, "IL-19", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setLore("Spy.");
        setGameText("While at a non-Hoth site, adds one [Light Side Force]. If just forfeited, may use 1 Force to place this droid in Used Pile.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), new AtCondition(self, Filters.and(Filters.site, Filters.not(Filters.Hoth_location))), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IL19__PLACE_IN_USED_PILE;
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, self, Filters.any)
                && GameConditions.canUseForce(game, playerId, 1)) {
            PhysicalCard justForfeitedCard = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(justForfeitedCard) + " in Used Pile");
            action.setActionMsg("Use 1 Force to place " + GameUtils.getCardLink(justForfeitedCard) + " in Used Pile");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justForfeitedCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
