package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.IncreaseForceLossEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossToAmountEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.List;

/** Legacy Block 6.1 Tatooine: Mos Espa (V). */
public class Card1261_055 extends AbstractSite {
    public Card1261_055() {
        super(Side.DARK, Title.Mos_Espa, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("'Free' on You're A Slave? is treated as '2 Force.' Your Force loss from You're A Slave? is doubled.");
        setLocationLightSideGameText("Endor Shield is canceled. Once per game, may reduce Force loss from You're A Slave? to zero. Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerId,
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isAboutToLoseForceFromCard(game, result, playerId, Filters.Youre_A_Slave)
                || !GameConditions.isOncePerForceLoss(game, self, sourceId))
            return null;
        int amount = Math.round(((AboutToLoseForceResult) result).getForceLossAmount(game));
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Double Force loss from You're A Slave?");
        action.appendUsage(new OncePerForceLossEffect(action));
        action.appendEffect(new IncreaseForceLossEffect(action, playerId, amount));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerId,
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__MOS_ESPA_V__PREVENT_SLAVE_LOSS;
        if (!TriggerConditions.isAboutToLoseForceFromCard(game, result, playerId, Filters.Youre_A_Slave)
                || !GameConditions.canReduceForceLoss(game) || !GameConditions.isOncePerGame(game, self, id))
            return null;
        var action = new OptionalGameTextTriggerAction(self, playerId, sourceId, id);
        action.setText("Reduce Force loss from You're A Slave? to zero");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new ReduceForceLossToAmountEffect(action, playerId, 0));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerId, SwccgGame game,
            PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Title.Revolution));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredBeforeTriggers(String playerId,
            SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Endor_Shield)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect))
            return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerId,
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)
                || !GameConditions.canTargetToCancel(game, self, Filters.Endor_Shield))
            return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardAction(action, Filters.Endor_Shield, Title.Endor_Shield);
        return List.of(action);
    }
}
