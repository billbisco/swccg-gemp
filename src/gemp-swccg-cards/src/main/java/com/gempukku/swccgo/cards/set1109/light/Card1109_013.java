package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Scout
 */
public class Card1109_013 extends AbstractRebel {
    public Card1109_013() {
        super(Side.LIGHT, 1, 6, 6, 6, 9, Title.Luke_Skywalker_Rebel_Scout, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Resourceful scout and member of the Rebel infiltration team assembled to destroy the shield generator. Surrendered to his father so that he would not endanger the mission.");
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to a forest. His weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II, Icon.REFLECTIONS_II);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.forest));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1));
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(Filters.opponents(self), Filters.atSameSite(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.DRAW)
                && GameConditions.isAlone(game, self)
                && Filters.on(Title.Endor).accepts(game, self)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
