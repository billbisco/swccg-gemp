package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Praji
 */
public class Card1210_004 extends AbstractImperial {
    public Card1210_004() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Commander Praji", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Vader's aide on the Devastator. Personally supervised search for Death Star plans on Tatooine by Vader's order. Was graduated with honors from Imperial Navy Academy on Carida.");
        setGameText("Adds 2 to power of anything he pilots. Once during your control phase (twice if on Devastator) may use 1 Force to take any Imperial pilot of ability < 3 into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER);
        addPersona(Persona.PRAJI);
        setMatchingStarshipFilter(Filters.Devastator);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDER_PRAJI_V__TAKE_IMPERIAL_PILOT;
        int numTimes = GameConditions.isAboard(game, self, Filters.Devastator) ? 2 : 1;

        if (GameConditions.isNumTimesDuringYourPhase(game, self, playerId, numTimes, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Imperial pilot into hand");
            action.setActionMsg("Take an Imperial pilot of ability < 3 into hand from Reserve Deck");
            action.appendUsage(
                    new NumTimesPerPhaseEffect(action, numTimes));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Imperial, Filters.pilot, Filters.abilityLessThan(3)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
