package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Vehicle
 * Subtype: Combat
 * Title: Cyclone 1
 */
public class Card1115_042 extends AbstractCombatVehicle {
    public Card1115_042() {
        super(Side.DARK, 1, 7, 5, 7, null, 1, 7, "Cyclone 1", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLore("Enclosed armored transport adapted for hot climates by replacing tertiary weapons systems with additional Sienar t23 heat dissipation units.");
        setGameText("Power +1 for each Sandtrooper present. Permanent pilots provide total ability of 2. May add 7 passengers. Once per game, may deploy a vehicle site or vehicle weapon aboard from Reserve Deck; reshuffle. Immune to attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.SPECIAL_EDITION, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addKeywords(Keyword.ENCLOSED);
        setPassengerCapacity(7);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentEvaluator(self, Filters.sandtrooper)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CYCLONE_1__DEPLOY_ABOARD;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy vehicle site or vehicle weapon");
            action.setActionMsg("Deploy a vehicle site or vehicle weapon aboard from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.vehicle_site, Filters.vehicle_weapon), true));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
