package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.WidenIncomKoensayrScopeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Objective
 * Title: Hidden Base / Systems Will Slip Through Your Fingers
 */
public class Card1115_012_BACK extends AbstractObjective {
    public Card1115_012_BACK() {
        super(Side.LIGHT, 7, Title.Systems_Will_Slip_Through_Your_Fingers, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText(Card1115_012.PACK_GAMETEXT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter atRelevantLocation = Filters.at(Filters.or(Filters.titleContains("Rebel Cell"), Filters.sector, Filters.battleground_system));
        Filter affected = Filters.and(Filters.your(playerId), Filters.or(
                Filters.and(Filters.character, Filters.abilityLessThan(4)),
                Filters.and(Icon.HOTH, Filters.artillery_weapon),
                Filters.starship), atRelevantLocation);
        Filter alreadyHasImmunity = Filters.alreadyHasImmunityToAttrition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.battleground_system, 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(affected, Filters.not(alreadyHasImmunity)), 4));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(affected, alreadyHasImmunity), 2));
        modifiers.add(new WidenIncomKoensayrScopeModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId battlegroundActionId = GameTextActionId.LEGACY__HIDDEN_BASE_V__DOWNLOAD_BATTLEGROUND_SYSTEM;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, battlegroundActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, battlegroundActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, battlegroundActionId);
            action.setText("Deploy battleground system from Reserve Deck");
            action.setActionMsg("Deploy a battleground system from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.battleground_system, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.and(Filters.location, Filters.opponents(self)))
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force drain");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, opponent, 2, Filters.titleContains("Rebel Cell"))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
