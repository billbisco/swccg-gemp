package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.logic.effects.CaptureCharacterFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Special Delivery
 */
public class Card1220_045 extends AbstractNormalEffect {
    public Card1220_045() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Special_Delivery, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Because bounty hunters are untrustworthy, the Empire relies on its troopers for efficient prisoner delivery.");
        setGameText("Deploy on table. During your deploy phase, if Carbonite Chamber Console on table, may use 1 Force to deploy an Effect on table from Reserve Deck; reshuffle. If opponent's just lost character is about to be removed from Lost Pile, may lose 1 Force to capture (imprison or let escape) that character instead. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SPECIAL_DELIVERY_V__DEPLOY_EFFECT;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.Carbonite_Chamber_Console)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an Effect from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Effect, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isAboutToRemoveJustLostCardFromLostPile(game, effectResult, opponent,
                Filters.and(Filters.opponents(self), Filters.character))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final AboutToRemoveJustLostCardFromLostPileResult result = (AboutToRemoveJustLostCardFromLostPileResult) effectResult;
            final PhysicalCard character = result.getCardToRemoveFromLostPile();
            PhysicalCard location = Filters.findFirstFromTopLocationsOnTable(game, Filters.prison);
            if (location == null) {
                location = Filters.findFirstFromTopLocationsOnTable(game, Filters.any);
            }
            if (location == null) {
                return null;
            }
            final PhysicalCard captureLocation = location;
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Capture " + GameUtils.getFullName(character));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(
                    new PreventEffectOnCardEffect(action, result.getPreventableCardEffect(), character,
                            "Captures " + GameUtils.getCardLink(character) + " instead"));
            action.appendEffect(
                    new CaptureCharacterFromLostPileEffect(action, character, captureLocation));
            return Collections.singletonList(action);
        }
        return null;
    }
}
