package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Subtype: Alien
 * Title: Booster Terrik
 */
public class Card1274_014 extends AbstractAlien {
    public Card1274_014() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, "Booster Terrik", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("Corellian information broker.");
        setGameText("Adds 2 to the power of anything he pilots. Starships here with a Rogue Squadron pilot aboard may move as a 'react.' Star Destroyers he pilots are hyperspeed +2. Unless Corellia on table, once per game, may steal an Imperial-class Star Destroyer from opponent's Lost Pile into hand.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter starshipsHereWithRoguePilot = Filters.and(Filters.your(self), Filters.starship, Filters.atSameLocation(self),
                Filters.hasAboard(self, Filters.Rogue_Squadron_pilot));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", playerId,
                starshipsHereWithRoguePilot, Filters.any));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.Star_Destroyer, Filters.hasPiloting(self)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BOOSTER_TERRIK__STEAL_STAR_DESTROYER;

        // Check condition(s)
        if (!GameConditions.canSpotLocation(game, Filters.Corellia_system)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal from opponent's Lost Pile");
            action.setActionMsg("Steal an Imperial-class Star Destroyer into hand from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.Imperial_class_Star_Destroyer));
            return Collections.singletonList(action);
        }
        return null;
    }
}
