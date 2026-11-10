package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.set1250.light.Card1250_082;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelIconModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Objective
 * Title: We'll Handle This (V)
 * Pre-errata version of 1250_82 (front): the Virtual Block 5 release sheet text (Dec 2009), without the
 * "[New Republic] characters or starships" deployment restriction (added by AR 2.0 Appendix A, 2012-08-27).
 * Its back side is 2250_82_BACK. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_082 extends Card1250_082 {
    public Card2250_082() {
        setGameText("Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's [Maintenance] is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber.");
        setLegacyVersion(new LegacyVersion("1250_82", "",
                "Virtual Block 5 release sheet (2009-12-07), card #79; superseded by the PC forum errata (2012-08-04)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 8, 4)));
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
                        Filters.Rebel_starship, Filters.and(Filters.Rebel, Filters.abilityMoreThan(2))), playerId),
                "May not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new NoForceLossFromCardModifier(self, Filters.or(Filters.Vengeance_Of_The_Dark_Prince, Filters.Your_Destiny), playerId),
                "Lose no Force to Vengeance Of The Dark Prince or Your Destiny"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new CancelIconModifier(self, Filters.title("Jedi Survivor"), Icon.MAINTENANCE),
                "Jedi Survivor's maintenance is canceled"));
        action.appendRequiredEffect(new AddUntilEndOfGameModifierEffect(action,
                new UsedInterruptModifier(self, Filters.title("Speak With The Jedi Council")),
                "Speak With The Jedi Council is a Used Interrupt"));
        return action;
    }
}
