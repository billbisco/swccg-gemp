package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;
import java.util.Map;

/** Legacy Block 5 Duel Of The Fates (V), back of printing 82. */
public class Card1250_082_BACK extends AbstractObjective {
    public Card1250_082_BACK() {
        super(Side.LIGHT, 7, Title.Duel_Of_The_Fates, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, Jedi are immune to attrition < 5 and ignore location deployment restrictions listed in their game text. Your lightsaber Force drain bonuses may not be canceled. You may not Force drain at Jedi Council Chamber. Whenever you initiate a battle with a Jedi, may retrieve 2 Force. Jedi Test #2 may deploy on any Jedi (as if completed), but applies only at that site. Flip this card (and place all Jedi Tests in Used Pile) if less than two Jedi at Jedi Council Chamber (unless Yoda there).");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionLessThanModifier(self, Filters.Jedi, 5),
                new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, Filters.Jedi),
                new ForceDrainBonusesMayNotBeCanceledModifier(self, Filters.and(Filters.your(self), Filters.lightsaber)),
                new MayNotForceDrainAtLocationModifier(self, Filters.Jedi_Council_Chamber, self.getOwner()),
                new ModifyGameTextModifier(self, Filters.Jedi_Test_2,
                        ModifyGameTextType.JEDI_TEST_2__MAY_DEPLOY_ON_JEDI_AS_IF_COMPLETED_APPLIES_ONLY_AT_THAT_SITE));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.battleInitiated(game, result, playerId)
                || !GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.Jedi))
                || !GameConditions.hasLostPile(game, playerId)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Retrieve 2 Force");
        action.appendEffect(new RetrieveForceEffect(action, playerId, 2));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result) || !GameConditions.canBeFlipped(game, self)) return null;

        var here = Filters.at(Filters.Jedi_Council_Chamber);
        if (Filters.canSpot(game, self, Filters.and(here, Filters.Yoda))
                || Filters.countActive(game, self, Filters.and(here, Filters.Jedi)) >= 2) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Flip and place Jedi Tests in Used Pile");
        action.appendEffect(new FlipCardEffect(action, self));
        var tests = Filters.filterActive(game, self, Map.of(InactiveReason.SUSPENDED, true), Filters.Jedi_Test);
        if (!tests.isEmpty()) {
            action.appendEffect(new PlaceCardsInUsedPileFromTableEffect(action, tests, false, Zone.LOST_PILE, true));
        }
        return List.of(action);
    }
}
