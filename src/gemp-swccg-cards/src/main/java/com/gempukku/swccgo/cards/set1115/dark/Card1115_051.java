package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Pilot
 */
public class Card1115_051 extends AbstractImperial {
    public Card1115_051() {
        super(Side.DARK, 3, 1, 0, 2, 2, "Imperial Pilot", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Among the Empire's best pilots. Loyal and fearless. Use superior numbers to overwhelm opponents. Trained under combat conditions. Wear sealed, high-gravity flight suits.");
        setGameText("Adds 2 to power of anything she pilots. Matching pilot for TIE Avenger. While piloting a TIE: it is maneuver +2, it moves for free, and when this character is just lost, may use 1 Force to place her in Used Pile. Corvettes are deploy +1 here.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.FEMALE);
        setMatchingStarshipFilter(Filters.TIE_Avenger);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingTIE = new PilotingCondition(self, Filters.TIE);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.TIE, Filters.hasPiloting(self)), pilotingTIE, 2));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.TIE, Filters.hasPiloting(self)), pilotingTIE));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.corvette, 1, Filters.sameLocation(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromAttachedTo(game, effectResult, self, Filters.TIE)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Use 1 Force to place " + GameUtils.getCardLink(self) + " in Used Pile");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
