package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Epic Event
 * Title: Insidious Prisoner
 */
public class Card1273_013 extends AbstractEpicEventDeployable {
    static final Filter PALPATINES_QUARTERS = Filters.title("Coruscant: Palpatine's Quarters");
    static final Filter BLOCKADE_FLAGSHIP_PRISON = Filters.title("Blockade Flagship: Prison");

    public Card1273_013() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Insidious_Prisoner, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setGameText("If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); opponent may deploy a Jedi and/or a lightsaber here. Evil Is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site.");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.A_Stunning_Move);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return PALPATINES_QUARTERS;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter sameSite = Filters.and(Filters.site, Filters.sameLocation(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, sameSite, opponent));
        modifiers.add(new IconModifier(self, sameSite, Icon.DARK_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Used for determining the first character that moves from Insidious Prisoner's site in a turn
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // While [Virtual Block 5] We Have A Plan on table, always relocate to Palpatine (even if a captive)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.We_Have_A_Plan, Icon.LEGACY_BLOCK_5))) {
            final PhysicalCard palpatine = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.Palpatine);
            if (palpatine != null && self.getAttachedTo() != palpatine) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
                action.setSingletonTrigger(true);
                action.setText("Relocate to Palpatine");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(palpatine));
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, palpatine));
                return Collections.singletonList(action);
            }
        }

        // If just lost, relocate to Blockade Flagship: Prison
        if (TriggerConditions.isAboutToBeLost(game, effectResult, self)) {
            final PhysicalCard prison = Filters.findFirstFromTopLocationsOnTable(game, BLOCKADE_FLAGSHIP_PRISON);
            if (prison != null) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
                action.setText("Relocate to Blockade Flagship: Prison");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(prison));
                action.addAnimationGroup(prison);
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(self);
                                for (PhysicalCard attachedCard : game.getGameState().getAllAttachedRecursively(self)) {
                                    result.getPreventableCardEffect().preventEffectOnCard(attachedCard);
                                }
                            }
                        });
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, prison));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return followCharacterMovingFromInsidiousPrisoner(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // When deployed, opponent may deploy a Jedi and/or a lightsaber here from Reserve Deck (even outside their deploy phase)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (site != null) {
                GameTextActionId jediActionId = GameTextActionId.LEGACY__INSIDIOUS_PRISONER__DEPLOY_JEDI;
                if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, jediActionId, true, false)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, jediActionId);
                    action.setText("Deploy a Jedi here from Reserve Deck");
                    action.setActionMsg("Deploy a Jedi to " + GameUtils.getCardLink(site) + " from Reserve Deck");
                    action.appendEffect(
                            new DeployCardToTargetFromReserveDeckEffect(action, Filters.Jedi, Filters.sameCardId(site), true));
                    actions.add(action);
                }
                GameTextActionId lightsaberActionId = GameTextActionId.LEGACY__INSIDIOUS_PRISONER__DEPLOY_LIGHTSABER;
                Filter yourCharacterHere = Filters.and(Filters.your(playerId), Filters.character, Filters.atLocation(site));
                if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, lightsaberActionId, true, false)
                        && GameConditions.canSpot(game, self, yourCharacterHere)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, lightsaberActionId);
                    action.setText("Deploy a lightsaber here from Reserve Deck");
                    action.setActionMsg("Deploy a lightsaber to " + GameUtils.getCardLink(site) + " from Reserve Deck");
                    action.appendEffect(
                            new DeployCardToTargetFromReserveDeckEffect(action, Filters.lightsaber, yourCharacterHere, true));
                    actions.add(action);
                }
            }
        }

        List<OptionalGameTextTriggerAction> follow = followCharacterMovingFromInsidiousPrisoner(playerId, game, effectResult, self, gameTextSourceCardId);
        if (follow != null) {
            actions.addAll(follow);
        }
        return actions;
    }

    private List<OptionalGameTextTriggerAction> followCharacterMovingFromInsidiousPrisoner(String playerMoving, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (self.getAttachedTo() != null && Filters.Palpatine.accepts(game, self.getAttachedTo())) {
            return null;
        }
        // [Virtual Block 7] A Stunning Move: while that side up, only follows opponent's characters moving to Palpatine's Quarters
        boolean stunningMoveSideUp = GameConditions.canSpot(game, self, Filters.and(Filters.A_Stunning_Move, Icon.LEGACY_BLOCK_7));
        if (stunningMoveSideUp && playerMoving.equals(self.getOwner())) {
            return null;
        }
        Filter destination = stunningMoveSideUp ? PALPATINES_QUARTERS : Filters.and(Filters.site, Filters.not(Filters.shielded_location));
        Filter charactersMoving = Filters.and(Filters.character, Filters.not(Filters.undercover_spy), Filters.your(playerMoving));
        Filter insidiousPrisonersSite = Filters.sameSiteAs(self, Filters.sameCardId(self));
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.movingFromLocation(game, effectResult, charactersMoving, insidiousPrisonersSite)
                && TriggerConditions.movingToLocation(game, effectResult, charactersMoving, destination)
                && GameConditions.controls(game, playerMoving, insidiousPrisonersSite)
                && (effectResult.getType() == EffectResult.Type.MOVING_USING_LANDSPEED || effectResult.getType() == EffectResult.Type.DOCKING_BAY_TRANSITING)
                && self.getWhileInPlayData() == null) {
            MovingResult movingResult = (MovingResult) effectResult;
            self.setWhileInPlayData(new WhileInPlayData(movingResult.getCardMoving()));
        }

        if (GameConditions.cardHasWhileInPlayDataSet(self)) {
            PhysicalCard cardMoved = self.getWhileInPlayData().getPhysicalCard();
            if (cardMoved != null
                    && TriggerConditions.moved(game, effectResult, playerMoving, cardMoved)
                    && GameConditions.isOncePerTurn(game, self, playerMoving, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, cardMoved)) {
                MovedResult movedResult = (MovedResult) effectResult;
                if (movedResult.isMoveComplete()) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerMoving, gameTextSourceCardId, gameTextActionId);
                    action.setText("Follow character moving from same site");
                    action.setActionMsg("Have " + GameUtils.getCardLink(self) + " follow " + GameUtils.getCardLink(movedResult.getMovedCards().iterator().next()));
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    action.appendEffect(
                            new AttachCardFromTableEffect(action, self, movedResult.getMovedTo()));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
