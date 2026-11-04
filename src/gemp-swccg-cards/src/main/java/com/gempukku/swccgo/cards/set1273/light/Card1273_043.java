package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyBunkerBlowAwayAttempt;
import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.CardType;
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
import com.gempukku.swccgo.game.state.DeactivateTheShieldGeneratorState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextEpicEventTriggerAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: Rebel Strike Team / Garrison Destroyed (V)
 */
public class Card1273_043 extends AbstractObjective {
    public static final String SHEET_GAMETEXT = "Rebel Strike Team: Deploy Endor system, Back Door, and [Virtual Block 7] The Shield Is Down!. For remainder of game, your [Episode I] characters and [Virtual Block 3] rifles are lost. Your Force generation is +1 at your Endor sites. May use 1 Force to take an [Endor] Epic Event into hand from Reserve Deck; reshuffle. While this side up, your Force drains on Endor are -1. Your Bunker gains one [Light Side Force]. If you occupy Bunker at the end of a battle you just won there, may use your [Endor] Epic Event to attempt to 'blow away' Bunker. Flip this card if Bunker 'blown away.' Garrison Destroyed: While this side up, Endor Shield is canceled. May raise your converted Endor to the top. If Establish Secret Base on table, Force generation at Endor locations may not be canceled. At Endor sites you occupy with a scout, your Force drains may not be modified by opponent. Whenever you initiate a battle with a scout, may retrieve 2 Force. Once per game, may deploy from Reserve Deck (or deploy from hand) a Rebel starship for half its printed deploy cost (round down); reshuffle.";

    public Card1273_043() {
        super(Side.LIGHT, 0, Title.Rebel_Strike_Team, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText(SHEET_GAMETEXT);
        addIcons(Icon.ENDOR);
    }

    /**
     * Your [Episode I] characters and [Virtual Block 3] rifles.
     */
    static Filter cardsThatAreLost(String playerId) {
        return Filters.and(Filters.your(playerId),
                Filters.or(Filters.and(Filters.character, Icon.EPISODE_I), Filters.and(Filters.rifle, Icon.LEGACY_BLOCK_3)));
    }

    /**
     * For remainder of game: your [Episode I] characters and [Virtual Block 3] rifles are lost.
     */
    static RequiredGameTextTriggerAction loseCardsAction(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toLose = Filters.filterActive(game, self, cardsThatAreLost(self.getOwner()));
            if (!toLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make [Episode I] characters and [Virtual Block 3] rifles lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toLose));
                return action;
            }
        }
        return null;
    }

    /**
     * For remainder of game: may use 1 Force to take an [Endor] Epic Event into hand from Reserve Deck; reshuffle.
     */
    static TopLevelGameTextAction takeEpicEventAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REBEL_STRIKE_TEAM_V__UPLOAD_EPIC_EVENT;
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [Endor] Epic Event into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(CardType.EPIC_EVENT, Icon.ENDOR), true));
            return action;
        }
        return null;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Endor_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Endor system to deploy";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.Back_Door, Title.Endor, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Back Door to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.title(LegacyVb73Light.THE_SHIELD_IS_DOWN), Icon.LEGACY_BLOCK_7), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose The Shield Is Down! to deploy";
                                    }
                                });
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ForceGenerationModifier(self, Filters.and(Filters.Endor_site, Filters.your(playerId)), 1, playerId), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.Endor_location, -1, playerId));
        modifiers.add(new IconModifier(self, Filters.and(Filters.Bunker, Filters.owner(playerId)), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        TopLevelGameTextAction action = takeEpicEventAction(playerId, game, self, gameTextSourceCardId);
        return action != null ? Collections.singletonList(action) : null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // If you occupy Bunker at the end of a battle you just won there, may use your [Endor] Epic Event to attempt to 'blow away' Bunker
        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.Bunker)
                && ((BattleEndedResult) effectResult).getBattleState().isWinner(playerId)
                && GameConditions.occupies(game, playerId, Filters.Bunker)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard epicEvent = Filters.findFirstActive(game, self,
                    Filters.and(Filters.your(playerId), Filters.Deactivate_The_Shield_Generator, Icon.ENDOR));
            if (epicEvent != null) {
                OptionalGameTextEpicEventTriggerAction action = new OptionalGameTextEpicEventTriggerAction(self, gameTextSourceCardId,
                        GameTextActionId.LEGACY__REBEL_STRIKE_TEAM_V__BLOW_AWAY_BUNKER, new DeactivateTheShieldGeneratorState(epicEvent));
                action.setText("Attempt to 'blow away' Bunker");
                // Perform result(s)
                LegacyBunkerBlowAwayAttempt.appendAttempt(action, playerId, epicEvent);
                return Collections.<OptionalGameTextTriggerAction>singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        RequiredGameTextTriggerAction loseAction = loseCardsAction(game, effectResult, self, gameTextSourceCardId);
        if (loseAction != null) {
            actions.add(loseAction);
        }

        // Flip this card if Bunker 'blown away'
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Bunker, true))
                && GameConditions.canBeFlipped(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}
