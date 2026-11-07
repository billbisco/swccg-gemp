package com.gempukku.swccgo.cards.set1250.light;

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
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 We'll Handle This (V), front of printing 82. */
public class Card1250_082 extends AbstractObjective {
    public Card1250_082() {
        super(Side.LIGHT, 0, Title.Well_Handle_This, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, [New Republic] characters or starships, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's [Maintenance] is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Council_Chamber, true, false) {
            @Override
            public String getChoiceText() {
                return "Choose Jedi Council Chamber to deploy";
            }
        });
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new MayNotDeployModifier(self, Filters.or(Filters.title("Affect Mind"), Filters.Galactic_Senate,
                        Filters.and(Icon.NEW_REPUBLIC, Filters.or(Filters.character, Filters.starship)),
                        Filters.Rebel_starship, Filters.and(Filters.Rebel, Filters.abilityMoreThan(2))), playerId),
                "May not deploy Affect Mind, Senate, New Republic characters or starships, Rebel starships, or Rebels of ability > 2"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new NoForceLossFromCardModifier(self, Filters.or(Filters.Vengeance_Of_The_Dark_Prince, Filters.Your_Destiny), playerId),
                "Lose no Force to Vengeance Of The Dark Prince or Your Destiny"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new UsedInterruptModifier(self, Filters.title("Speak With The Jedi Council")),
                "Speak With The Jedi Council is a Used Interrupt"));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeployCostToLocationModifier(self, Filters.Yoda, -3, Filters.Jedi_Council_Chamber),
                new ForceDrainModifier(self, -1, self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result) || !GameConditions.canBeFlipped(game, self)) return null;

        var here = Filters.at(Filters.Jedi_Council_Chamber);
        if (!Filters.canSpot(game, self, Filters.and(here, Filters.Yoda))
                || Filters.countActive(game, self, Filters.and(here, Filters.Jedi, Filters.not(Filters.Yoda))) < 2) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Flip");
        action.appendEffect(new FlipCardEffect(action, self));
        return List.of(action);
    }
}
