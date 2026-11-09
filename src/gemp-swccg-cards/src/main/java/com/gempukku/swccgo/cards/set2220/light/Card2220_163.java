package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.InterruptPlacedOutOfPlayWhenCompletedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Legacy Block 2; final Light master p47
 * Type: Objective
 * Title: Local Uprising / Liberation
 *
 * Original version of 1220_163 (Virtual Block 2 release sheet, Dec 2009, card #164E): T-47 Battle Formation is an
 * Out Of Play Interrupt (and immune to Sense) for the rest of the game, where the Virtual Cards Master List (2013-04-27)
 * lets it be played only once per game. The back side (2220_163_BACK) is unchanged.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_163 extends AbstractObjective {
    public Card2220_163() {
        super(Side.LIGHT, 0, Title.Local_Uprising, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Hoth system and [Virtual Block 6] Main Power Generators. May deploy Echo Base Garrison and/or Maneuvering Flaps. For remainder of game, you may not deploy Hoth Sentry or non-Hoth locations (except Rendezvous Point). T-47 Battle Formation is an Out Of Play Interrupt and immune to Sense. While this side up, during your deploy phase, may deploy an exterior Hoth site or a non-unique Rebel to Hoth from Reserve Deck; reshuffle. Flip this card during your move phase if you control three exterior Hoth sites with T-47s and/or non-unique Rebels. Place out of play if Main Power Generators 'blown away.'");
        setLegacyVersion(new LegacyVersion("1220_163", "",
                "Virtual Block 2 release sheet (2009-12-07), card #164", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 11, 19)));
        addIcons(Icon.SPECIAL_EDITION);
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
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Main_Power_Generators, Icon.LEGACY_BLOCK_6), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Main Power Generators to deploy";
                    }
                });
        offerSetupEffect(action, playerId, "Echo Base Garrison", Filters.title("Echo Base Garrison"));
        offerSetupEffect(action, playerId, "Maneuvering Flaps", Filters.Maneuvering_Flaps);
        var formation = Filters.title(Title.T_47_Battle_Formation);
        var forbidden = Filters.or(Filters.Hoth_Sentry,
                Filters.and(Filters.location, Filters.not(Filters.Hoth_location), Filters.not(Filters.title(Title.Rendezvous_Point))));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotPlayModifier(self, forbidden, playerId), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ChangeCardSubtypeModifier(self, formation, CardSubtype.OUT_OF_PLAY), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new InterruptPlacedOutOfPlayWhenCompletedModifier(self, formation, CardSubtype.LOST), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new InterruptPlacedOutOfPlayWhenCompletedModifier(self, formation, CardSubtype.USED), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, formation, Title.Sense), null));
        return action;
    }

    private void offerSetupEffect(ObjectiveDeployedTriggerAction action, String playerId, String title, Filter filter) {
        action.appendOptionalEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy " + title + "?") {
            @Override
            protected void yes() {
                action.insertEffect(new DeployCardFromReserveDeckEffect(action, filter, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose " + title + " to deploy";
                    }
                });
            }
        }));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployActionId = GameTextActionId.LEGACY__LOCAL_UPRISING__DEPLOY_SITE_OR_REBEL;
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
