package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Objective
 * Title: We Have A Plan / They Will Be Lost And Confused
 */
public class Card1116_011_BACK extends AbstractObjective {
    public Card1116_011_BACK() {
        super(Side.LIGHT, 7, "They Will Be Lost And Confused", ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Immediately take any one card into hand from Reserve Deck; reshuffle. While this side up, whenever a card of ability > 2 is lost, its owner loses 1 Force. While you control two battlegrounds with clones, opponent must have two cards with ability (or two [Presence] droids) to initiate a Force drain. Once per turn, may deploy a clone from Lost Pile. Clones are immune to attrition < 4. Flip this card if Palpatine is about to be captured, lost, missing, or removed from table.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.clone, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERON_NETT_V__DEPLOY_ABOARD;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a clone from Lost Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromLostPileEffect(action, Filters.clone, false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.justLost(game, effectResult, Filters.abilityMoreThan(2))) {
            PhysicalCard lost = ((LostFromTableResult) effectResult).getCard();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Owner loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, lost.getOwner(), 1));
            actions.add(action);
        }
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.titleContains("Palpatine"))
                && GameConditions.canBeFlipped(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.appendEffect(new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}
