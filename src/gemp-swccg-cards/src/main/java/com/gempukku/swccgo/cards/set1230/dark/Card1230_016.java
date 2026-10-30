package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Nizuc Bek
 */
public class Card1230_016 extends AbstractAlien {
    public Card1230_016() {
        super(Side.DARK, 3, 1, 2, 1, 3, Title.Nizuc_Bek, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Guard from Corulag. Former bouncer at the Mos Eisley cantina. Assigned by Jabba to guard celebrities visiting Jabba's palace. Friend of Wuher. Loves juri juice.");
        setGameText("Whenever opponent Force drains at a non-battleground, may retrieve 1 Force. During your deploy phase, may deploy an Effect with 'matching' in game text from Reserve Deck; reshuffle. Once per turn, may place Juri Juice from hand on Force Pile to cancel a Force drain.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        GameTextActionId retrieveId = GameTextActionId.LEGACY__NIZUC_BEK_V__RETRIEVE_FORCE;
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.non_battleground_location)) {
            final OptionalGameTextTriggerAction retrieve = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, retrieveId);
            retrieve.setText("Retrieve 1 Force");
            retrieve.appendEffect(
                    new RetrieveForceEffect(retrieve, playerId, 1));
            actions.add(retrieve);
        }

        GameTextActionId juiceId = GameTextActionId.LEGACY__NIZUC_BEK_V__PLACE_JURI_JUICE;
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, juiceId)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.hasInHand(game, playerId, Filters.title(Title.Juri_Juice))) {
            final OptionalGameTextTriggerAction juice = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, juiceId);
            juice.setText("Place Juri Juice on Force Pile to cancel Force drain");
            juice.appendUsage(
                    new OncePerTurnEffect(juice));
            juice.appendCost(
                    new PutCardFromHandOnForcePileEffect(juice, playerId, Filters.title(Title.Juri_Juice), false));
            juice.appendEffect(
                    new CancelForceDrainEffect(juice));
            actions.add(juice);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NIZUC_BEK_V__DEPLOY_MATCHING_EFFECT;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Effect from Reserve Deck");
            action.setActionMsg("Deploy an Effect with 'matching' in game text from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.gameTextContains("matching")), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
