package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Objective
 * Title: We'll Handle This / Duel Of The Fates
 */
public class Card1117_010 extends AbstractObjective {
    public Card1117_010() {
        super(Side.LIGHT, 0, Title.Well_Handle_This, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's maintenance is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Council_Chamber, true, false) {
            @Override
            public String getChoiceText() {
                return "Choose Jedi Council Chamber to deploy";
            }
        });
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotDeployModifier(self, Filters.or(
                        Filters.title("Affect Mind"),
                        Filters.title("Senate"),
                        Filters.title("Coruscant: Galactic Senate"),
                        Filters.Rebel_starship,
                        Filters.and(Filters.Rebel, Filters.character, Filters.abilityMoreThan(2))), playerId),
                "May not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new NoForceLossFromCardModifier(self, Filters.or(Filters.title("Vengeance Of The Dark Prince"), Filters.title("Your Destiny")), playerId),
                "Lose no Force to Vengeance Of The Dark Prince or Your Destiny"));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Yoda, -3, Filters.Jedi_Council_Chamber));
        modifiers.add(new ForceDrainModifier(self, -1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        int jediAtChamber = Filters.countActive(game, self, Filters.and(Filters.Jedi, Filters.at(Filters.Jedi_Council_Chamber)));
        boolean yodaAtChamber = Filters.canSpot(game, self, Filters.and(Filters.Yoda, Filters.at(Filters.Jedi_Council_Chamber)));
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && yodaAtChamber
                && jediAtChamber >= 3) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
