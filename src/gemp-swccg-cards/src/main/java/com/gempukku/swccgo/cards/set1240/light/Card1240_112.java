package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Starship
 * Subtype: Starfighter
 * Title: Infinity
 */
public class Card1240_112 extends AbstractStarfighter {
    public Card1240_112() {
        super(Side.LIGHT, 3, 3, 3, null, 3, 4, 4, "Infinity", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("");
        setGameText("May add 2 pilots. While BoShek piloting: maneuver and hyperspeed +2; immune to attrition < 4; and if at a battleground system, may add 1 to Force drains here.");
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var boShekPiloting = new HasPilotingCondition(self, Filters.persona(Persona.BOSHEK));
        return List.of(
                new ManeuverModifier(self, boShekPiloting, 2),
                new HyperspeedModifier(self, self, boShekPiloting, 2),
                new ImmuneToAttritionLessThanModifier(self, boShekPiloting, 4));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId,
                Filters.and(Filters.here(self), Filters.battleground_system))
                && GameConditions.hasPiloting(game, self, Filters.persona(Persona.BOSHEK))) {
            var action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(new AddToForceDrainEffect(action, 1));
            return List.of(action);
        }
        return null;
    }
}
