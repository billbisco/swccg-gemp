package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Legacy Virtual Block 5
 * Type: Effect
 * Title: Wesa Ready To Do Our-sa Part
 */
public class Card1250_061 extends AbstractNormalEffect {
    public Card1250_061() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Wesa Ready To Do Our-sa Part", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Every Gungan that died on the battle plains gave Amidala and her troops that much more time to capture the Viceroy.");
        setGameText("Deploy on Endor system or Naboo system. Your total power here is +1 for each Ewok or Gungan at related sites. Once per turn, may place your just lost Ewok or Gungan in Used Pile. (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.system, Filters.or(Filters.title(Title.Endor), Filters.title(Title.Naboo)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Evaluator perNative = new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return count(gameState);
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return count(gameState);
            }
            private float count(GameState gameState) {
                return Filters.countActive(gameState.getGame(), self, Filters.and(Filters.or(Filters.Ewok, Filters.Gungan), Filters.at(Filters.relatedSite(self))));
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), perNative, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.Ewok, Filters.Gungan)))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, com.gempukku.swccgo.common.GameTextActionId.OTHER_CARD_ACTION_1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, com.gempukku.swccgo.common.GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Place lost Ewok or Gungan in Used Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            PhysicalCard justLost = ((LostFromTableResult) effectResult).getCard();
            action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, justLost, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
