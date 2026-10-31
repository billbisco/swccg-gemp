package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.EndOfTurnLimitCounterNotReachedCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Character
 * Subtype: Droid
 * Title: Probe Droid
 */
public class Card1104_043 extends AbstractDroid {
    public Card1104_043() {
        super(Side.DARK, 2, 2, 2, 5, "Probe Droid", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setArmor(3);
        setLore("Arakyd Viper probe droid. Has sensors specifically designed to detect traces of Rebel activity. Equipped with an auto-destruct mechanism. A highly durable spy droid.");
        setGameText("When drawn for destiny, you may peek at opponent's hand. Provides presence. Thrice per turn, your Imperial may deploy -1 to same site. Your total battle destiny here is +1 for each Imperial here.");
        addModelType(ModelType.PROBE);
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayForceDrainModifier(self));
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Imperial),
                new EndOfTurnLimitCounterNotReachedCondition(self, 3), -1, Filters.sameSite(self)));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self),
                new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.Imperial)), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__PROBE_DROID_V__PEEK_AT_HAND;
        if (GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.hasHand(game, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at opponent's hand");
            action.appendEffect(
                    new PeekAtOpponentsHandEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployedToLocation(game, effectResult, self.getOwner(),
                Filters.and(Filters.your(self), Filters.Imperial), Filters.sameSite(self))) {
            game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, self.getOwner(), self.getCardId(),
                    GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(3, 1);
        }
        return null;
    }
}
