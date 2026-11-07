package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediTest;
import com.gempukku.swccgo.common.JediTestStatus;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.JediTestCompletedCondition;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.OptionalRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayOnlyMoveUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.rules.JediTestAttemptRule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Jedi Test
 * Title: A Jedi's Strength
 */
public class Card4_075 extends AbstractJediTest {
    public Card4_075() {
        super(Side.LIGHT, 2, "A Jedi's Strength", ExpansionSet.DAGOBAH, Rarity.U);
        setGameText("Deploy on an unoccupied Dagobah site. At the beginning of each of your move phases, opponent may relocate this Jedi Test to an adjacent site. Target a mentor on Dagobah and an apprentice who has completed Jedi Test #1. Apprentice may move only by using personal landspeed. Attempt when apprentice is present at the beginning of your control phase. Draw training destiny. If destiny + apprentice's ability > 13, test completed: Place on apprentice. Apprentice is power +1 and may move normally. Total ability of 6 or more is required for opponent to draw battle destiny.");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.JEDI_TEST_2);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter filter = Filters.and(Filters.unoccupied, Filters.Dagobah_site);
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TEST_2__MAY_DEPLOY_ON_JEDI_AS_IF_COMPLETED_APPLIES_ONLY_AT_THAT_SITE)) {
            filter = Filters.or(filter, Filters.and(Filters.your(self), Filters.Jedi));
        }
        return filter;
    }

    /**
     * Determines if this Jedi Test is being deployed (or was deployed) directly on a Jedi 'as if completed'.
     */
    private static boolean isDeployOnJediTarget(SwccgGame game, PhysicalCard deployTarget) {
        return deployTarget != null && Filters.character.accepts(game, deployTarget);
    }

    @Override
    public Filter getValidJediTestMentorTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        // Deployed on a Jedi 'as if completed': there is no mentor, and that Jedi is the apprentice
        if (isDeployOnJediTarget(game, deployTarget)) {
            return Filters.sameCardId(deployTarget);
        }
        return super.getValidJediTestMentorTargetFilter(playerId, game, self, deployTarget);
    }

    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(final Action action, String playerId, final SwccgGame game, final PhysicalCard self, final PhysicalCard target, PlayCardOption playCardOption) {
        if (!isDeployOnJediTarget(game, target)) {
            return super.getTargetCardsWhenDeployedEffects(action, playerId, game, self, target, playCardOption);
        }
        final Filter jediFilter = Filters.sameCardId(target);
        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose Jedi (apprentice)", jediFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard jedi) {
                action.addAnimationGroup(jedi);
                self.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, targetGroupId, jedi, jediFilter);
            }
        };
        return Collections.singletonList(targetingEffect);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Deployed directly on a Jedi: treat as completed (applies only at that Jedi's site)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && self.getJediTestStatus() != JediTestStatus.COMPLETED
                && isDeployOnJediTarget(game, self.getAttachedTo())) {
            final PhysicalCard jedi = self.getAttachedTo();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Treat as completed");
            action.setActionMsg(GameUtils.getCardLink(self) + " is deployed on " + GameUtils.getCardLink(jedi) + " as if completed");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setJediTestStatus(JediTestStatus.COMPLETED);
                            self.setWhileInPlayData(new WhileInPlayData(true));
                            game.getModifiersQuerying().completedJediTest(self, jedi);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidMentorFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        return Filters.on(Title.Dagobah);
    }

    @Override
    protected Filter getGameTextValidApprenticeFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        Filter apprenticeFilter = Filters.apprenticeTargetedByJediTest(Filters.and(Filters.completed_Jedi_Test, Filters.Jedi_Test_1));

        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__ONLY_LUKE_MAY_BE_APPRENTICE))
        {
            apprenticeFilter = Filters.and(apprenticeFilter, Filters.Luke);
        }

        return apprenticeFilter;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition jediTestCompleted = new JediTestCompletedCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayOnlyMoveUsingLandspeedModifier(self, Filters.apprenticeTargetedByJediTest(self), new NotCondition(jediTestCompleted)));
        modifiers.add(new PowerModifier(self, Filters.apprenticeTargetedByJediTest(self), jediTestCompleted, 1));
        final int permCardId = self.getPermanentCardId();
        Condition deployedOnJedi = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return card != null && card.getWhileInPlayData() != null && card.getWhileInPlayData().getBooleanValue();
            }
        };
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, Filters.any, new AndCondition(jediTestCompleted, new NotCondition(deployedOnJedi)), 6, opponent));
        // Deployed on a Jedi 'as if completed': applies only at that site
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, Filters.sameSite(self), new AndCondition(jediTestCompleted, deployedOnJedi), 6, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.MOVE)) {
            Filter adjacentSiteFilter = Filters.adjacentSite(self);
            if (!GameConditions.isJediTestCompleted(game, self)
                    && GameConditions.canSpotLocation(game, adjacentSiteFilter)
                    && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TEST_2__MAY_NOT_MOVE)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to adjacent site");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose where to relocate " + GameUtils.getCardLink(self), adjacentSiteFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard site) {
                                action.addAnimationGroup(site);
                                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(site));
                                // Perform result(s)
                                action.appendEffect(
                                        new AttachCardFromTableEffect(action, self, site));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        boolean normalTiming = TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL);
        boolean specialTiming = TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.DEPLOY) && GameConditions.hasGameTextModification(game, self, ModifyGameTextType.JEDI_TESTS__MAY_ATTEMPT_IN_OPPONENTS_DEPLOY_PHASE);
        boolean timingSatisfied = normalTiming || specialTiming;
        
        // Check condition(s)
        if (timingSatisfied
                && !GameConditions.isJediTestCompleted(game, self)) {
            final PhysicalCard apprentice = Filters.findFirstActive(game, self, Filters.and(Filters.mayAttemptJediTest(self), Filters.present(self)));
            if (apprentice != null) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Attempt Jedi Test #2");
                // Update usage limit(s)
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                game.getModifiersQuerying().attemptedJediTest(self, apprentice);
                            }
                        });
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, 1, DestinyType.TRAINING_DESTINY) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(apprentice);
                            }
                            @Override
                            protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, final DrawDestinyState drawDestinyState) {
                                ActionProxy actionProxy = new AbstractActionProxy() {
                                    @Override
                                    public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                        // Check condition(s)
                                        if (TriggerConditions.isDestinyJustDrawn(game, effectResult, drawDestinyState)
                                                && playerId2.equals(playerId)) {
                                            int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Dagobah_site, Filters.not(Filters.generic)));
                                            if (numSites > 0) {

                                                OptionalRuleTriggerAction action1 = new OptionalRuleTriggerAction(new JediTestAttemptRule(), self);
                                                action1.setText("Add 1 to destiny for each Dagobah site");
                                                // Perform result(s)
                                                action1.appendEffect(
                                                        new ModifyDestinyEffect(action1, numSites));
                                                actions.add(action1);
                                            }
                                        }
                                        return actions;
                                    }
                                };
                                return Collections.singletonList(actionProxy);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                GameState gameState = game.getGameState();
                                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    return;
                                }

                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                float ability = modifiersQuerying.getAbility(gameState, apprentice);
                                gameState.sendMessage("Apprentice's ability: " + GuiUtils.formatAsString(ability));
                                float total = totalDestiny + ability;
                                gameState.sendMessage("Total: " + GuiUtils.formatAsString(total));

                                if (total > 13) {
                                    gameState.sendMessage("Result: Succeeded");
                                    action.appendEffect(
                                            new CompleteJediTestEffect(action, self));
                                }
                                else {
                                    gameState.sendMessage("Result: Failed");
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
