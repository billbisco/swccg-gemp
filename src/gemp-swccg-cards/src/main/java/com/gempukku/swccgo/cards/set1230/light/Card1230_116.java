package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Mon Calamari Star Cruiser
 */
public class Card1230_116 extends AbstractCapitalStarship {
    public Card1230_116() {
        super(Side.LIGHT, 1, 8, 7, 5, null, 3, 9, "Mon Calamari Star Cruiser", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mon Cal MC80 cruiser. Originally a civilian ship. Converted to military use following the liberation of Mon Calamari from the Empire.");
        setGameText("Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(5);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setStarfighterCapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return reduction(gameState);
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return reduction(gameState);
            }
            private float reduction(GameState gameState) {
                int imperial = Filters.countActive(gameState.getGame(), self, Filters.Imperial_starship);
                return -Math.min(imperial, 5);
            }
        }));
        return modifiers;
    }
}
