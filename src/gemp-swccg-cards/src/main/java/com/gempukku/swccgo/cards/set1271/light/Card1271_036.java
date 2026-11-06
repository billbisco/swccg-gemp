package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromTableResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Rebel
 * Title: Wedge Antilles, Legendary Rogue
 */
public class Card1271_036 extends AbstractRebel {
    public Card1271_036() {
        super(Side.LIGHT, 4, 2, 2, 2, 6, "Wedge Antilles, Legendary Rogue", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Highly decorated Corellian. Piloted Red 2 at the Battle of Yavin. A wealthy orphan, he bought a freighter with his inheritance.");
        setGameText("[Pilot] 3. While piloting [A New Hope] Red 2: it gains [Nav Computer], it is maneuver and immunity to attrition +2, draws one battle destiny if not able to otherwise, and, if Wedge just forfeited, may use 1 Force to place him in Used Pile.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.RED_SQUADRON, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Red_2);
    }

    private Filter matchingRedTwo() { return Filters.and(Filters.Red_2, Icon.A_NEW_HOPE); }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition piloting = new PilotingCondition(self, matchingRedTwo());
        Filter ship = Filters.hasPiloting(self);
        return Arrays.asList(
                new AddsPowerToPilotedBySelfModifier(self, 3),
                new IconModifier(self, ship, piloting, Icon.NAV_COMPUTER),
                new ManeuverModifier(self, ship, piloting, 2),
                new ImmunityToAttritionChangeModifier(self, ship, piloting, 2),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, ship, piloting, 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE
                && TriggerConditions.justForfeited(game, effectResult, playerId, self)
                && GameConditions.canUseForce(game, playerId, 1)) {
            PhysicalCard ship = ((ForfeitedCardToLostPileFromTableResult) effectResult).getFromAttachedTo();
            // The printed A New Hope Red 2 has one pilot slot and one astromech slot, no passenger slots.
            if (ship != null && matchingRedTwo().accepts(game, ship)) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId,
                        GameTextActionId.LEGACY__WEDGE_LEGENDARY_ROGUE__PLACE_IN_USED_PILE);
                action.setText("Place Wedge in Used Pile");
                action.appendCost(new UseForceEffect(action, playerId, 1));
                action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
