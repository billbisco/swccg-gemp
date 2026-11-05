package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacySpiceCards;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Alien
 * Title: Moruth Doole, Kessel Administrator (printed as Spice Mine Administrator)
 */
public class Card1273_009 extends AbstractAlien {
    static final Filter KESSEL = Filters.or(Filters.Kessel_system, Filters.partOfSystem(Title.Kessel));

    public Card1273_009() {
        super(Side.DARK, 3, 3, 3, 2, 3, "Moruth Doole, Kessel Administrator", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setGameText("Deploys -1 to Kessel. While at a Kessel battleground site, your Force drains are +1 at X related locations, where X = number of 'spice' cards on Spice Mine Operations, and once per turn, may use 3 Force to take a 'spice' card you own into hand.");
    }

    private static boolean atKesselBattlegroundSite(SwccgGame game, PhysicalCard self) {
        return GameConditions.isAtLocation(game, self, Filters.and(Filters.Kessel_site, Filters.battleground));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, KESSEL));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SPICE_MINE_ADMINISTRATOR__ADD_TO_FORCE_DRAIN;
        int x = LegacySpiceCards.countSpiceCards(game, self);
        if (x > 0
                && atKesselBattlegroundSite(game, self)
                && TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.or(Filters.sameLocation(self), Filters.relatedLocation(self)))
                && GameConditions.isNumTimesPerTurn(game, self, playerId, x, gameTextSourceCardId, gameTextActionId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to Force drain");
            action.setActionMsg("Add 1 to Force drain");
            action.appendUsage(new NumTimesPerTurnEffect(action, x));
            action.appendEffect(new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SPICE_MINE_ADMINISTRATOR__TAKE_SPICE_INTO_HAND;
        Filter yourSpice = Filters.owner(playerId);
        if (atKesselBattlegroundSite(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 3)
                && !LegacySpiceCards.spiceCards(game, self, yourSpice).isEmpty()) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a 'spice' card into hand");
            action.setActionMsg("Take a 'spice' card into hand");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 3));
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, LegacySpiceCards.SPICE_MINE_OPERATIONS_FILTER, yourSpice));
            return Collections.singletonList(action);
        }
        return null;
    }
}
