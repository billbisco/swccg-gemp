package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Well Guarded (V)
 */
public class Card1281_015 extends AbstractNormalEffect {
    public Card1281_015() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Well_Guarded, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most of Jabba's guards had been sold to the Hutt and were too scared (or too dumb) to leave. Jabba assigned his best guards to watch over his most prized possessions.");
        setGameText("Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        Filter yourNonUniqueAliens = Filters.and(Filters.owner(playerId), Filters.non_unique, Filters.alien);
        final Filter yourAliensInBattle = Filters.and(Filters.owner(playerId), Filters.alien, Filters.participatingInBattle);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, yourNonUniqueAliens, new CardsInHandFewerThanCondition(playerId, 13), Title.Grimtaash));
        modifiers.add(new ImmuneToTitleModifier(self, yourNonUniqueAliens, Title.The_Camp));
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, yourNonUniqueAliens, Filters.The_Camp, Filters.any));
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, yourNonUniqueAliens, Filters.Watch_Your_Step, Filters.any));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.owner(playerId), Filters.alien, Filters.at(Filters.battleLocation)),
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        if (!gameState.isDuringBattle()) {
                            return false;
                        }
                        PhysicalCard rep = gameState.getRep(playerId);
                        if (rep == null || rep.getBlueprint().getSpecies() == null) {
                            return false;
                        }
                        Species repSpecies = rep.getBlueprint().getSpecies();
                        return Filters.countActive(gameState.getGame(), self, Filters.and(yourAliensInBattle, Filters.non_unique, Filters.species(repSpecies))) >= 2;
                    }
                }));
        return modifiers;
    }
}
