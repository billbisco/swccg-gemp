package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: I'm Sorry
 */
public class Card1112_033 extends AbstractNormalEffect {
    public Card1112_033() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Im_Sorry, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I'm sorry, too.'");
        setGameText("Deploy on table. Floating Refinery is suspended during opponent's turn. During your deploy phase, may deploy one Floating Refinery, a non-unique cloud sector, or an Obsidian squadron TIE from Reserve Deck; reshuffle. During your turn, if you control 2 cloud sectors with TIEs, opponent loses 1 Force. Immune to Alter.");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Floating_Refinery, new PlayersTurnCondition(opponent)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IM_SORRY_V__DEPLOY_FLOATING_REFINERY_CLOUD_SECTOR_OR_TIE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction deployRefinery = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            deployRefinery.setText("Deploy Floating Refinery from Reserve Deck");
            deployRefinery.appendUsage(
                    new OncePerPhaseEffect(deployRefinery));
            deployRefinery.appendEffect(
                    new DeployCardFromReserveDeckEffect(deployRefinery, Filters.Floating_Refinery, true));
            actions.add(deployRefinery);

            final TopLevelGameTextAction deploySector = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            deploySector.setText("Deploy a non-unique cloud sector from Reserve Deck");
            deploySector.appendUsage(
                    new OncePerPhaseEffect(deploySector));
            deploySector.appendEffect(
                    new DeployCardFromReserveDeckEffect(deploySector, Filters.and(Filters.cloud_sector, Filters.non_unique), true));
            actions.add(deploySector);

            final TopLevelGameTextAction deployTIE = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            deployTIE.setText("Deploy Obsidian squadron TIE from Reserve Deck");
            deployTIE.appendUsage(
                    new OncePerPhaseEffect(deployTIE));
            deployTIE.appendEffect(
                    new DeployCardFromReserveDeckEffect(deployTIE, Filters.and(Filters.TIE, Keyword.OBSIDIAN_SQUADRON), true));
            actions.add(deployTIE);
        }

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.cloud_sector, Filters.TIE)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check if reached end of turn and the lose-1 was not performed yet.
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.cloud_sector, Filters.TIE)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
