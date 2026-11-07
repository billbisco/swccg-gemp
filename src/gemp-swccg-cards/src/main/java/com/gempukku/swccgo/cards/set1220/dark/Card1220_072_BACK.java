package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Final Legacy Block 2 errata (72E), back face. */
public class Card1220_072_BACK extends AbstractObjective {
    private static final Filter RETRIEVABLE = Filters.not(Filters.or(Filters.Interrupt, Filters.vehicle));

    public Card1220_072_BACK() {
        super(Side.DARK, 7, Title.Imperial_Control, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, Revolution is canceled. Rebels are deploy +1 to Hoth sites. Your total battle destiny at Hoth system is +1 for each Imperial there. Unless opponent controls a Hoth location, your Force drain bonuses may not be canceled. Once per turn, when you deploy an AT-AT, may retrieve any one card (except an Interrupt or vehicle) into hand. During your deploy phase, may deploy a marker site from Reserve Deck; reshuffle. Flip this card if Hoth Blockade not on Hoth.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String owner = self.getOwner();
        return List.of(new CancelEffectsOfRevolutionModifier(self, null),
                new DeployCostToLocationModifier(self, Filters.Rebel, 1, Filters.Hoth_site),
                new TotalBattleDestinyModifier(self, Filters.Hoth_system,
                        new OnTableEvaluator(self, Filters.and(Filters.Imperial, Filters.at(Filters.Hoth_system))),
                        owner, true),
                new ForceDrainBonusesMayNotBeCanceledModifier(self,
                        new UnlessCondition(new ControlsCondition(game.getOpponent(owner), Filters.Hoth_location)),
                        Filters.your(owner)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int sourceId) {
        return Card1220_072.cancelAsPlayed(game, effect, self, sourceId, Filters.Revolution);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var actions = new ArrayList<>(Card1220_072.cancelOnTable(game, result, self, sourceId,
                Filters.Revolution, Title.Revolution));
        if (TriggerConditions.isTableChanged(game, result) && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, Card1220_072.BLOCKADE_ON_HOTH)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__IMPERIAL_CONTROL_V__RETRIEVE_INTO_HAND;
        if (!TriggerConditions.justDeployed(game, result, playerId, Filters.AT_AT)
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                || !GameConditions.canSearchLostPile(game, playerId, self, id))
            return null;
        var action = new OptionalGameTextTriggerAction(self, sourceId, id);
        action.setText("Retrieve a card into hand");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId, RETRIEVABLE));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__IMPERIAL_CONTROL_V__DEPLOY_MARKER;
        if (!GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Deploy a marker site from Reserve Deck");
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.marker_site, true));
        return List.of(action);
    }
}
