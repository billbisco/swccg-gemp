package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Mind What You Have Learned / Save You It Can (V), final Legacy Light master p48. */
public class Card1261_108 extends AbstractObjective {
    static final ModifyGameTextType TARGETS_LEIA = ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE;

    public Card1261_108() {
        super(Side.LIGHT, 0, Title.Mind_What_You_Have_Learned, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Dagobah system (may not be converted) and Strong Is Vader. For remainder of game, only Luke may be an apprentice. Yoda and Luke deploy -3 to Dagobah. While this side up, may deploy At Peace, Luke, Luke's Backpack, [Dagobah] Yoda, and/or Yoda's Hope to Dagobah from Reserve Deck; reshuffle. Your cards on Dagobah may not contribute to Force retrieval (except with Strong Is Vader). Each player's Sense and Alter destiny draws are +4. Flip this card when Luke completes Jedi Test #5.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    static boolean targetsLeia(SwccgGame game, PhysicalCard self) {
        return GameConditions.hasGameTextModification(game, self, TARGETS_LEIA);
    }

    static Filter apprentice(SwccgGame game, PhysicalCard self) {
        return targetsLeia(game, self) ? Filters.Leia : Filters.Luke;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(
            String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        var action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Dagobah_system, true, false) {
            @Override public String getChoiceText() { return "Choose Dagobah system to deploy"; }
        });
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Strong_Is_Vader), true, false) {
            @Override public String getChoiceText() { return "Choose Strong Is Vader to deploy"; }
        });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(
            String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        var leia = new GameTextModificationCondition(self, TARGETS_LEIA);
        var onlyLuke = new NotCondition(leia);
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotBeConvertedModifier(self, Filters.Dagobah_system), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Jedi_Test), onlyLuke,
                        ModifyGameTextType.JEDI_TESTS__ONLY_LUKE_MAY_BE_APPRENTICE), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new DeployCostToTargetModifier(self, Filters.and(Filters.your(self), Filters.Yoda), -3, Filters.Deploys_on_Dagobah), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new DeployCostToTargetModifier(self, Filters.and(Filters.your(self), Filters.Luke), onlyLuke, -3, Filters.Deploys_on_Dagobah), null));
        action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                new DeployCostToTargetModifier(self, Filters.and(Filters.your(self), Filters.Leia), leia, -3, Filters.Deploys_on_Dagobah), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(
            String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__MIND_WHAT_YOU_HAVE_LEARNED__DEPLOY_TO_DAGOBAH;
        boolean leia = targetsLeia(game, self);
        if (!GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id, true, false)) return List.of();
        Filter people = leia ? Filters.Leia : Filters.or(Filters.Luke, Filters.Daughter_Of_Skywalker);
        var targets = Filters.or(people, Filters.and(Filters.Yoda, Icon.DAGOBAH), Filters.At_Peace, Filters.Lukes_Backpack, Filters.Yodas_Hope);
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Deploy card to Dagobah from Reserve Deck");
        action.appendEffect(new DeployCardToSystemFromReserveDeckEffect(action, targets, Title.Dagobah,
                0, Filters.any, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true) {
            @Override
            protected void cardDeployed(PhysicalCard card) {
                if (!leia && Filters.Daughter_Of_Skywalker.accepts(game, card)) {
                    game.getModifiersEnvironment().addUntilEndOfGameModifier(new ModifyGameTextModifier(card,
                            Filters.or(Filters.Mind_What_You_Have_Learned, Filters.Save_You_It_Can), TARGETS_LEIA));
                }
            }
        });
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var leia = new GameTextModificationCondition(self, TARGETS_LEIA);
        return List.of(new MayNotContributeToForceRetrievalModifier(self,
                        Filters.and(Filters.your(self), Filters.or(Filters.Dagobah_location, Filters.at(Title.Dagobah)))),
                new PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier(self, Filters.Dagobah_location, self.getOwner()),
                new DestinyDrawForActionSourceModifier(self, Filters.or(Filters.Sense, Filters.Alter), 4),
                stackTestFive(self, Filters.Luke, new NotCondition(leia)), stackTestFive(self, Filters.Leia, leia));
    }

    private Modifier stackTestFive(PhysicalCard self, Filter person, Condition condition) {
        return new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Jedi_Test_5,
                Filters.completed_Jedi_Test, Filters.jediTestTargetingApprentice(person)), condition,
                ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.jediTestCompletedBy(game, result, Filters.and(Filters.your(self), Filters.Jedi_Test_5), apprentice(game, self))
                || !GameConditions.canBeFlipped(game, self)) return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Flip");
        action.appendEffect(new FlipCardEffect(action, self));
        return List.of(action);
    }
}
