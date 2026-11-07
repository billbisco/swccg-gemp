package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.JediTestCompletedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.List;

/** Strong Is Vader, final Legacy Light master p31. */
public class Card1261_087 extends AbstractEpicEventDeployable {
    public Card1261_087() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Strong_Is_Vader, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setGameText("If [Virtual Block 6] Mind What You Have Learned on table, deploy on table with up to 6 Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test. Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Mind_What_You_Have_Learned, Icon.LEGACY_BLOCK_6));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (result instanceof JediTestCompletedResult completed
                && self.getOwner().equals(completed.getJediTest().getOwner())
                && completed.getCompletedBy() != null) {
            return List.of(rememberApprentice(self, sourceId, completed));
        }
        if (!TriggerConditions.justDeployed(game, result, self)) {
            if (result instanceof PlayCardResult played
                    && TriggerConditions.justDeployed(game, result, self.getOwner(), Filters.character)) {
                return rebindCompletedTests(game, self, sourceId, played.getPlayedCard());
            }
            return List.of();
        }
        String player = self.getOwner();
        var state = game.getGameState();
        var tests = new ArrayList<>(Filters.filter(state.getOutsideOfDeck(player), game, Filters.Jedi_Test));
        if (tests.isEmpty()) return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(player);
        action.setText("Stack up to six Jedi Tests from outside your deck");
        action.appendEffect(new PlayoutDecisionEffect(action, player,
                new ArbitraryCardsSelectionDecision("Choose up to six Jedi Tests to stack", tests, 0, Math.min(6, tests.size())) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        for (var test : getSelectedCardsByResponse(result)) {
                            state.removeCardFromZone(test);
                            state.stackCard(test, self, true, false, false);
                        }
                    }
                }));
        return List.of(action);
    }

    private RequiredGameTextTriggerAction rememberApprentice(
            PhysicalCard self, int sourceId, JediTestCompletedResult completed) {
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Remember the completed Test's apprentice");
        action.skipInitialMessageAndAnimation();
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                self.getForRemainderOfGameData().put(completed.getJediTest().getPermanentCardId(),
                        new ForRemainderOfGameData(List.of(completed.getCompletedBy())));
            }
        });
        return action;
    }

    private List<RequiredGameTextTriggerAction> rebindCompletedTests(
            SwccgGame game, PhysicalCard self, int sourceId, PhysicalCard apprentice) {
        var state = game.getGameState();
        List<PhysicalCard> tests = new ArrayList<>();
        for (var entry : self.getForRemainderOfGameData().entrySet()) {
            var remembered = entry.getValue().getPhysicalCards();
            if (remembered.size() != 1 || !Filters.samePermanentCardId(remembered.getFirst()).accepts(game, apprentice)) continue;
            var test = state.findCardByPermanentId(entry.getKey());
            if (test != null && test.getZone() == Zone.SIDE_OF_TABLE && self.getOwner().equals(test.getOwner())
                    && Filters.completed_Jedi_Test.accepts(game, test)
                    && test.getTargetedCard(state, TargetId.JEDI_TEST_APPRENTICE) != apprentice) {
                tests.add(test);
            }
        }
        if (tests.isEmpty()) return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Resume completed Jedi Tests");
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                for (var test : tests) {
                    test.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, apprentice,
                            Filters.samePermanentCardId(apprentice));
                    state.reapplyAffectingForCard(game, test);
                }
            }
        });
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String player = self.getOwner();
        var yourTests = Filters.and(Filters.your(self), Filters.Jedi_Test);
        return List.of(new MayNotForceDrainAtLocationModifier(self, Filters.Dagobah_location, player),
                new MayNotDeployModifier(self, Filters.or(Filters.asteroid_sector, Icon.MAINTENANCE, Icon.PERMANENT_WEAPON), player),
                new SpecialFlagModifier(self, ModifierFlag.DRAW_TWO_AND_CHOOSE_ONE_FOR_TRAINING_DESTINY, player),
                new PlaceJediTestOnTableWhenCompletedModifier(self, yourTests, null),
                new JediTestSuspendedInsteadOfLostModifier(self, Filters.and(yourTests, Filters.completed_Jedi_Test), null));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(
            String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.jediTestCompletedBy(game, result, Filters.and(Filters.your(self), Filters.Jedi_Test), Filters.your(self))
                || !GameConditions.hasLostPile(game, playerId)) return List.of();
        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Retrieve 3 Force for completing a Jedi Test");
        action.appendEffect(new RetrieveForceEffect(action, playerId, 3));
        return List.of(action);
    }
}
