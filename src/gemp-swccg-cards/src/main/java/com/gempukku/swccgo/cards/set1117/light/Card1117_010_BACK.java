package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Objective
 * Title: We'll Handle This / Duel Of The Fates
 */
public class Card1117_010_BACK extends AbstractObjective {
    public Card1117_010_BACK() {
        super(Side.LIGHT, 7, Title.Duel_Of_The_Fates, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, Jedi are immune to attrition < 5 and ignore location deployment restrictions listed in their game text. Your lightsaber Force drain bonuses may not be canceled. You may not Force drain at Jedi Council Chamber. Whenever you initiate battle with a Jedi, may retrieve 2 Force. Jedi Test #2 may deploy on any Jedi (as if completed), but applies only at that site. Flip this card (and place all Jedi Tests in Used Pile) if less than two Jedi at Jedi Council Chamber (unless Yoda there).");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Jedi, 5));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Jedi_Council_Chamber, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        int jediAtChamber = Filters.countActive(game, self, Filters.and(Filters.Jedi, Filters.at(Filters.Jedi_Council_Chamber)));
        boolean yodaAtChamber = Filters.canSpot(game, self, Filters.and(Filters.Yoda, Filters.at(Filters.Jedi_Council_Chamber)));
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && jediAtChamber < 2
                && !yodaAtChamber) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
