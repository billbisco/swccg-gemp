package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Effect
 * Title: Imperial Detainment
 */
public class Card1115_049 extends AbstractNormalEffect {
    public Card1115_049() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Detainment", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLore("'There'll be no escape for the princess this time.'");
        setGameText("Deploy on table. We Have A Prisoner is a Used Interrupt. TIEs are deploy +1 to Launch Bay. During your deploy phase, may deploy from Reserve Deck (or retrieve) one Besieged or a Launch Bay; reshuffle. Once per turn, when your Imperial captures a character, opponent loses 1 Force. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, Filters.We_Have_A_Prisoner));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.TIE, 1, Filters.launch_bay));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter besiegedOrLaunchBay = Filters.or(Filters.Besieged, Filters.launch_bay);

        GameTextActionId deployId = GameTextActionId.LEGACY__IMPERIAL_DETAINMENT__DEPLOY_BESIEGED_OR_LAUNCH_BAY;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployId);
            action.setText("Deploy Besieged or Launch Bay");
            action.setActionMsg("Deploy a Besieged or a Launch Bay from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, besiegedOrLaunchBay, true));
            actions.add(action);
        }

        GameTextActionId retrieveId = GameTextActionId.LEGACY__IMPERIAL_DETAINMENT__RETRIEVE_BESIEGED_OR_LAUNCH_BAY;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSearchLostPile(game, playerId, self, retrieveId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, retrieveId);
            action.setText("Retrieve Besieged or Launch Bay");
            action.setActionMsg("Retrieve a Besieged or a Launch Bay");
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, besiegedOrLaunchBay));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.capturedBy(game, effectResult, Filters.character, Filters.and(Filters.your(self), Filters.Imperial))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
