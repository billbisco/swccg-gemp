package com.gempukku.swccgo.cards.set701.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Modern replaced
 * Type: Character
 * Subtype: Rebel
 * Title: Jabba's Prize (V)
 * Former PC id: 200_16 until 20 April 2022
 */
public class Card701_016 extends AbstractRebel {
    public Card701_016() {
        super(Side.LIGHT, 0, 0, 0, 0, 0, Title.Jabbas_Prize, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_MODERN_REPLACED, Rarity.PM);
        setVirtualSuffix(true);
        setGameText("Jabba's Prize is a Light Side card and does not count towards your deck limit. Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1.");
        setDoesNotCountTowardDeckLimit(true);
        setMayNotBePlacedInReserveDeck(true);
        addPersona(Persona.CORRAN_HORN);
        setCharacterPersonaOnlyWhileOnTable(true);
        addIcons(Icon.REFLECTIONS_II, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return self.getZone() == Zone.OUTSIDE_OF_DECK && GameConditions.isEitherPlayersPhase(game, Phase.PLAY_STARTING_CARDS);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckBeforeTriggers(
            SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        String playerId = self.getOwner();
        if (!TriggerConditions.isPlayingCard(game, effect, playerId, Filters.Starting_Effect)) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Reveal");
        action.skipInitialMessageAndAnimation();
        action.appendEffect(new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(self)));
        action.appendEffect(new ShowCardOnScreenEffect(action, self));
        return Collections.singletonList(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isStartingLocationsAndObjectivesCompletedStep(game, result)) {
            return null;
        }
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        final String playerId = self.getOwner();
        action.setText("Resolve Jabba's Prize starting setup");
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotDeployModifier(self, Filters.and(Icon.MAINTENANCE, Filters.Falcon), playerId), null));

        final PhysicalCard tower = Filters.findFirstFromTopLocationsOnTable(game, Filters.Security_Tower);
        if (tower == null) {
            action.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(action, self));
            return Collections.singletonList(action);
        }
        PhysicalCard opponentPrize = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.opponents(self),
                Filters.Jabbas_Prize, Filters.imprisonedIn(Filters.sameLocationId(tower))));
        if (opponentPrize == null) {
            action.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(action, self));
        } else {
            action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, opponentPrize));
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    PlayCardAction deploy = self.getBlueprint().getPlayCardAction(playerId, game, self, self, true, 0, null,
                            DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(),
                            DeployAsCaptiveOption.deployAsImprisonedFrozenCaptive(), null, null, false, 0,
                            Filters.sameLocationId(tower), null);
                    if (deploy != null) {
                        action.appendEffect(new StackActionEffect(action, deploy));
                    } else {
                        action.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(action, self));
                    }
                }
            });
        }
        return Collections.singletonList(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        boolean leaving = TriggerConditions.isAboutToLeaveTable(game, result, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, result, self);
        if (!leaving && !TriggerConditions.released(game, result, self)) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Place out of play");
        if (leaving) {
            final AboutToLeaveTableResult pending = (AboutToLeaveTableResult) result;
            action.appendEffect(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    pending.getPreventableCardEffect().preventEffectOnCard(self);
                    for (PhysicalCard card : game.getGameState().getAllAttachedRecursively(self)) {
                        pending.getPreventableCardEffect().preventEffectOnCard(card);
                    }
                }
            });
        }
        action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, self));
        return Collections.singletonList(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new EachBattleDestinyModifier(self, Filters.Audience_Chamber,
                new AtCondition(self, Filters.Audience_Chamber), 1, game.getOpponent(self.getOwner()), true));
    }
}
