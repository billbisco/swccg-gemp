package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Eisley
 */
public class Card1115_065 extends AbstractSite {
    public Card1115_065() {
        super(Side.DARK, Title.Mos_Eisley, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Once per turn, when a player's Landing Craft takes off or lands here, that player may retrieve 1 Force");
        setLocationLightSideGameText("Immune to Revolution. If a stormtrooper here, you must first use 2 Force to deploy a weapon or device at same or adjacent site.");
        setGameText("Dark: Once per turn, when a player's Landing Craft takes off or lands here, that player may retrieve 1 Force  Light: Immune to Revolution. If a stormtrooper here, you must first use 2 Force to deploy a weapon or device at same or adjacent site.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addImmuneToCardTitle(Title.Revolution);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.weapon, Filters.device)),
                new HereCondition(self, Filters.stormtrooper),
                new ConstantEvaluator(2),
                Filters.or(self, Filters.adjacentSite(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getLandingCraftRetrieveAction(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getLandingCraftRetrieveAction(game, effectResult, self, gameTextSourceCardId);
    }

    private List<OptionalGameTextTriggerAction> getLandingCraftRetrieveAction(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MOS_EISLEY_V__RETRIEVE_FORCE;
        PhysicalCard landingCraft = getLandingCraftThatTookOffOrLandedHere(game, effectResult, self);
        if (landingCraft == null) {
            return null;
        }
        final String retrievingPlayer = landingCraft.getOwner();
        if (!GameConditions.isOncePerTurn(game, self, retrievingPlayer, gameTextSourceCardId, gameTextActionId)) {
            return null;
        }

        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, retrievingPlayer, gameTextSourceCardId, gameTextActionId);
        action.setText("Retrieve 1 Force");
        action.appendUsage(
                new OncePerTurnEffect(action));
        action.appendEffect(
                new RetrieveForceEffect(action, retrievingPlayer, 1));
        return Collections.singletonList(action);
    }

    private PhysicalCard getLandingCraftThatTookOffOrLandedHere(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.justLandedAt(game, effectResult, Filters.Landing_Craft, self)) {
            MovedResult movedResult = (MovedResult) effectResult;
            for (PhysicalCard moved : movedResult.getMovedCards()) {
                if (Filters.Landing_Craft.accepts(game, moved)) {
                    return moved;
                }
            }
        }
        if (TriggerConditions.justTookOff(game, effectResult, Filters.Landing_Craft)) {
            MovedResult movedResult = (MovedResult) effectResult;
            if (self.equals(movedResult.getMovedFrom())) {
                for (PhysicalCard moved : movedResult.getMovedCards()) {
                    if (Filters.Landing_Craft.accepts(game, moved)) {
                        return moved;
                    }
                }
            }
        }
        return null;
    }
}
