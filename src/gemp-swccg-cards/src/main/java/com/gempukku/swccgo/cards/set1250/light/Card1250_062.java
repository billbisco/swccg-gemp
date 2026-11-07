package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasStackedCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.PlaceStackedCardOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.List;

/** Legacy Block 5 Grrrghrrrgh!, assigned printing 62. */
public class Card1250_062 extends AbstractNormalEffect {
    public Card1250_062() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Grrrghrrrgh!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("\"It's not wise to upset a Wookiee.\" \"But sir, nobody worries about upsetting a droid.\" \"That's 'cause a droid don't pull people's arms out of their sockets when they lose.\"");
        setGameText("Deploy on table. If your Wookiee just lost, may stack it here. You may not add more than one destiny to total power in battle. Wookiees are deploy -1 and, while at least one stacked here, they are armor = 4 and immune to attrition < 3. During any battle, may place a card here out of play to add one destiny to total power. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var hasStack = new HasStackedCondition(self, Filters.any);
        return List.of(new DeployCostModifier(self, Filters.Wookiee, -1),
                new ResetArmorModifier(self, Filters.Wookiee, hasStack, 4),
                new ImmuneToAttritionLessThanModifier(self, Filters.Wookiee, hasStack, 3));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.justLost(game, result, Filters.and(Filters.your(self), Filters.Wookiee))) return null;

        PhysicalCard lost = ((LostFromTableResult) result).getCard();
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Stack the lost Wookiee");
        action.appendEffect(new StackOneCardFromLostPileEffect(action, lost, self, false, false, true));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isDuringBattle(game) || !GameConditions.hasStackedCards(game, self)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place a stacked card out of play to add a power destiny");
        action.appendCost(new PlaceStackedCardOutOfPlayEffect(action, playerId, self, Filters.any));
        action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
        return List.of(action);
    }
}
