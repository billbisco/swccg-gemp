package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CrossedOverResult;

import java.util.List;

/** Save You It Can (V), final Legacy Light master p48. */
public class Card1261_108_BACK extends AbstractObjective {
    public Card1261_108_BACK() {
        super(Side.LIGHT, 7, Title.Save_You_It_Can, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Immediately place destiny card from Jedi Test #5 on that Jedi Test. While this side up, during your move phase, may use 3 Force to take Luke into hand from a location you control (cards on Luke go to owner's Used Pile). Luke may ignore location deployment restrictions listed in his game text. Opponent may not play Sense or Alter. Place out of play if Luke is placed out of play or crosses over.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        var apprentice = Card1261_108.apprentice(game, self);
        // The native result follows the owner and persona change. Inspect the printed persona.
        Persona trainedPersona = Card1261_108.targetsLeia(game, self) ? Persona.LEIA : Persona.LUKE;
        boolean crossed = result instanceof CrossedOverResult crossover
                && crossover.getCharacter().getBlueprint().getPersonas().contains(trainedPersona);
        if (!crossed && !TriggerConditions.justPlacedOutOfPlayFromTable(game, result, Filters.and(Filters.your(self), apprentice))) return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Place out of play");
        action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, self));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(
            String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        var filter = Filters.and(Filters.your(self), Card1261_108.apprentice(game, self), Filters.at(Filters.controls(playerId)));
        if (!GameConditions.isOnceDuringYourPhase(game, self, playerId, sourceId, Phase.MOVE)
                || !GameConditions.canUseForce(game, playerId, 3) || !GameConditions.canTarget(game, self, filter)) return List.of();
        var action = new TopLevelGameTextAction(self, sourceId);
        String name = Card1261_108.targetsLeia(game, self) ? "Leia" : "Luke";
        action.setText("Take " + name + " into hand");
        action.appendUsage(new OncePerPhaseEffect(action));
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose " + name + " to take into hand", filter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard card) {
                action.appendCost(new UseForceEffect(action, playerId, 3));
                action.appendEffect(new ReturnCardToHandFromTableEffect(action, card, Zone.USED_PILE));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var leia = new GameTextModificationCondition(self, Card1261_108.TARGETS_LEIA);
        return List.of(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Jedi_Test_5),
                        ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5),
                new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self,
                        Filters.and(Filters.your(self), Filters.Luke), new NotCondition(leia)),
                new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self,
                        Filters.and(Filters.your(self), Filters.Leia), leia),
                new MayNotPlayModifier(self, Filters.or(Filters.Sense, Filters.Alter), game.getOpponent(self.getOwner())));
    }
}
