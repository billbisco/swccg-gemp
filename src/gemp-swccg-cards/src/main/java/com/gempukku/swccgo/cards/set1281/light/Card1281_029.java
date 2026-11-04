package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Het Nkik (V)
 */
public class Card1281_029 extends AbstractAlien {
    public Card1281_029() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Het Nkik", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jawa scout. Wants to avenge deaths of relatives killed in stormtrooper assault on sandcrawler. Reegesk pilfered the powerpack from his blaster at a critical moment.");
        setGameText("Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = number of deserts you control with a Jawa.");
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.JAWA);
        addPersona(Persona.HET);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition withAnotherJawa = new WithCondition(self, Filters.and(Filters.not(self), Filters.Jawa));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), withAnotherJawa, playerId));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self, withAnotherJawa, Filters.your(playerId), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HET_NKIK_V__DRAW_CARDS;
        final int numToDraw = Filters.countTopLocationsOnTable(game,
                Filters.and(Filters.desert, Filters.controlsWith(playerId, self, Filters.Jawa)));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && numToDraw > 0
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw " + numToDraw + " card" + (numToDraw > 1 ? "s" : "") + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numToDraw));
            return Collections.singletonList(action);
        }
        return null;
    }
}
