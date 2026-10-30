package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Objective
 * Title: Local Uprising / Liberation
 */
public class Card1104_011 extends AbstractObjective {
    public Card1104_011() {
        super(Side.LIGHT, 0, Title.Local_Uprising, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Hoth system and Main Power Generators. May deploy Echo Base Garrison and/or Maneuvering Flaps. For remainder of game, you may not deploy Hoth Sentry or non-Hoth locations (except Rendezvous Point). T-47 Battle Formation is an Out Of Play Interrupt and Immune to Sense. While this side up, during your deploy phase, may ▼ an exterior Hoth site or a non-unique Rebel to Hoth. Flip this card during your move phase if you control three exterior Hoth sites with T-47s and/or non-unique Rebels. Place out of play if Main Power Generators 'blown away.'");
        addIcons(Icon.HOTH);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Hoth_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Hoth system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Main_Power_Generators, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Main Power Generators to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title("Echo Base Garrison"), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Echo Base Garrison to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Maneuvering_Flaps, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Maneuvering Flaps to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Hoth_Sentry, Filters.and(Filters.location, Filters.not(Filters.Hoth_location), Filters.not(Filters.title(Title.Rendezvous_Point)))), playerId));
        modifiers.add(new ChangeCardSubtypeModifier(self, Filters.title(Title.T_47_Battle_Formation), CardSubtype.OUT_OF_PLAY));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.T_47_Battle_Formation), Title.Sense));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployActionId = GameTextActionId.OVS__LOCAL_UPRISING__DEPLOY_SITE_OR_REBEL;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, deployActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployActionId)) {

            final TopLevelGameTextAction siteAction = new TopLevelGameTextAction(self, gameTextSourceCardId, deployActionId);
            siteAction.setText("Deploy an exterior Hoth site from Reserve Deck");
            siteAction.setActionMsg("Deploy an exterior Hoth site from Reserve Deck");
            siteAction.appendUsage(
                    new OncePerPhaseEffect(siteAction));
            siteAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(siteAction, Filters.exterior_Hoth_site, true));
            actions.add(siteAction);

            final TopLevelGameTextAction rebelAction = new TopLevelGameTextAction(self, gameTextSourceCardId, deployActionId);
            rebelAction.setText("Deploy a non-unique Rebel to Hoth from Reserve Deck");
            rebelAction.setActionMsg("Deploy a non-unique Rebel to Hoth from Reserve Deck");
            rebelAction.appendUsage(
                    new OncePerPhaseEffect(rebelAction));
            rebelAction.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(rebelAction, Filters.and(Filters.non_unique, Filters.Rebel), Filters.Hoth_location, true));
            actions.add(rebelAction);
        }

        GameTextActionId flipActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, flipActionId, Phase.MOVE)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 3, Filters.exterior_Hoth_site, Filters.or(Filters.T_47, Filters.and(Filters.non_unique, Filters.Rebel)))) {
            final TopLevelGameTextAction flipAction = new TopLevelGameTextAction(self, gameTextSourceCardId, flipActionId);
            flipAction.setText("Flip");
            flipAction.appendUsage(
                    new OncePerPhaseEffect(flipAction));
            flipAction.appendEffect(
                    new FlipCardEffect(flipAction, self));
            actions.add(flipAction);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        GameTextActionId flipActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.MOVE, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, flipActionId, Phase.MOVE)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 3, Filters.exterior_Hoth_site, Filters.or(Filters.T_47, Filters.and(Filters.non_unique, Filters.Rebel)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, flipActionId);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Main_Power_Generators, true))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}
