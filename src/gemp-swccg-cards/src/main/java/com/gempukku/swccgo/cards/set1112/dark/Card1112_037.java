package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: Maneuver Check
 */
public class Card1112_037 extends AbstractNormalEffect {
    public Card1112_037() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Maneuver Check", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Luke, at that speed will you be able to pull out in time?'");
        setGameText("Deploy on table. Once per game, if Alderaan 'blown away,' may target a system; unless opponent has a capital (or [Episode I]) starship there, Objectives treat that system as a Subjugated planet. Starships may not move the turn they are deployed (or carried) to Death Star. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        String planet = game.getGameState().getSubjugatedPlanet();
        if (planet != null) {
            return "Subjugated planet is " + planet;
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MANEUVER_CHECK_V__TARGET_SYSTEM;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isBlownAway(game, Filters.Alderaan_system)
                && GameConditions.canTarget(game, self, Filters.system)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target a system as Subjugated");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a system", Filters.system) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Treat " + GameUtils.getCardLink(targetedCard) + " as a Subjugated planet",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            String opponent = game.getOpponent(playerId);
                                            Filter blockingStarship = Filters.and(Filters.opponents(self), Filters.starship,
                                                    Filters.or(Filters.capital_starship, Icon.EPISODE_I), Filters.at(targetedCard));
                                            if (!GameConditions.canSpot(game, self, blockingStarship)
                                                    && game.getGameState().getSubjugatedPlanet() == null) {
                                                String systemName = targetedCard.getBlueprint().getSystemName();
                                                if (systemName != null) {
                                                    game.getGameState().setSubjugatedPlanet(systemName);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        Filter deathStar = Filters.Death_Star_system;

        if (TriggerConditions.justDeployedToLocation(game, effectResult, Filters.starship, deathStar)) {
            PhysicalCard starship = ((PlayCardResult) effectResult).getPlayedCard();
            if (starship != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Prevent " + GameUtils.getFullName(starship) + " from moving");
                action.setActionMsg("Prevent " + GameUtils.getCardLink(starship) + " from moving this turn");
                action.appendEffect(
                        new MayNotMoveUntilEndOfTurnEffect(action, starship));
                actions.add(action);
            }
        }

        if (TriggerConditions.movedToLocation(game, effectResult, Filters.starship, deathStar)) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> starships = Filters.filter(movedResult.getMovedCards(), game, Filters.starship);
            for (PhysicalCard starship : starships) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Prevent " + GameUtils.getFullName(starship) + " from moving");
                action.setActionMsg("Prevent " + GameUtils.getCardLink(starship) + " from moving this turn");
                action.appendEffect(
                        new MayNotMoveUntilEndOfTurnEffect(action, starship));
                actions.add(action);
            }
        }

        return actions;
    }
}
