package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: The Emperor
 */
public class Card1230_019 extends AbstractDarkJediMasterImperial {
    public Card1230_019() {
        super(Side.DARK, 1, 5, 4, 7, 9, Title.The_Emperor, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setPolitics(4);
        setLore("Leader. Secretive manipulator of the galaxy. Played Darth Vader and Prince Xizor off against one another in his relentless pursuit of 'young Skywalker'.");
        setGameText("Never deploys or moves (even if carried) to a non-Senate site opponent occupies. Each turn, may lose 1 Force (free in battle) to target a character present with him. Draw destiny. Target lost if destiny +1 > defense value. Gains [Republic] at opponent's Senate.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.REFLECTIONS_II);
        addKeywords(Keyword.LEADER);
    }

    private Filter nonSenateSiteOpponentOccupies(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.site, Filters.not(Filters.Galactic_Senate), Filters.occupies(game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, nonSenateSiteOpponentOccupies(game, self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)),
                nonSenateSiteOpponentOccupies(game, self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, new AtCondition(self, Filters.and(Filters.Galactic_Senate, Filters.occupies(opponent))), Icon.REPUBLIC));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_EMPEROR_V__TARGET_CHARACTER;
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter targetFilter = Filters.and(Filters.character, Filters.presentWith(self), Filters.canBeTargetedBy(self, targetingReason));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {
            final boolean freeInBattle = GameConditions.isDuringBattle(game) && Filters.participatingInBattle.accepts(game, self);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target a character present");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            if (!freeInBattle) {
                                action.appendCost(
                                        new LoseForceEffect(action, playerId, 1));
                            }
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(gameState, finalTarget);
                                                            if (totalDestiny != null) {
                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                                if ((totalDestiny + 1) > defenseValue) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed Destiny Draw.");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
