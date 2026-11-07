package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.List;

/** Legacy Block 5 Jedi Survivor, printing 41. */
public class Card1250_041 extends AbstractAlien {
    public Card1250_041() {
        super(Side.LIGHT, 3, 5, 5, 6.0, 5, "Jedi Survivor", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Female.");
        setGameText("When deployed, you may not initiate battle here this turn. While at opponent's site, adds one [Light Side Force] and one [Dark Side Force]. May 'swing' lightsabers for free. May never be won in sabacc. Immune to attrition < 5. End of your turn: Use 1 Force to maintain OR Lose 1 Force to place in Used Pile OR Place out of play.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.WARRIOR, Icon.MAINTENANCE);
        addKeyword(Keyword.FEMALE);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.justDeployed(game, result, self)) return null;

        PhysicalCard location = ((PlayCardResult) result).getToLocation();
        if (location == null) return null;
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("May not initiate battle here this turn");
        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                new MayNotInitiateBattleAtLocationModifier(self, location, self.getOwner()),
                "May not initiate battle at the deployment location this turn"));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var opposingSite = new AtCondition(self, Filters.and(Filters.site, Filters.opponents(self)));
        return List.of(new IconModifier(self, Filters.here(self), opposingSite, Icon.LIGHT_FORCE),
                new IconModifier(self, Filters.here(self), opposingSite, Icon.DARK_FORCE),
                new FireWeaponFiredByForFreeModifier(self, Filters.lightsaber),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextMaintenanceCostAction(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!game.getModifiersQuerying().hasIcon(game.getGameState(), self, Icon.MAINTENANCE)) return null;
        return super.getGameTextMaintenanceCostAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, String playerId) {
        return new UseForceEffect(action, playerId, 1);
    }

    @Override
    protected StandardEffect getGameTextMaintenanceRecycleCost(Action action, String playerId) {
        return new LoseForceEffect(action, playerId, 1, true);
    }
}
