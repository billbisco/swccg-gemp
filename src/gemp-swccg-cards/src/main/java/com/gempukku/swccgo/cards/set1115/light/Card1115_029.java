package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresDeploymentRestrictionsFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayRetrieveForceWithCardOnlyOncePerTurnModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Talon Karrde (V)
 */
public class Card1115_029 extends AbstractAlien {
    public Card1115_029() {
        super(Side.LIGHT, 3, 3, 2, 3, 4, "Talon Karrde", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Smuggler and information broker. Operates intelligence network. Pragmatic businessman. Strong sense of personal honor. Bounty placed on his head by Grand Admiral Thrawn.");
        setGameText("Adds 2 to power of anything he pilots. If your [Virtual Set 15] objective on table: aliens are forfeit +2 at Rebel Cell sites; Yerka Mig may deploy on a Rebel Cell site, ignores Jedi, is Immune to Alter, and for remainder of game, you retrieve Force with it only once per turn.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER);
    }

    private Filter ovsObjective(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Objective, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_15;
            }
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition objectiveOnTable = new OnTableCondition(self, ovsObjective(self));
        Condition retrieveLimit = new OrCondition(objectiveOnTable, new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return self.getWhileInPlayData() != null;
            }
        });
        Filter yerka = Filters.title(Title.Yerka_Mig);
        Filter yourAliensAtRebelCell = Filters.and(Filters.your(self), Filters.alien, Filters.at(Filters.titleContains("Rebel Cell")));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, yourAliensAtRebelCell, objectiveOnTable, 2));
        modifiers.add(new MayDeployToLocationModifier(self, yerka, objectiveOnTable, Filters.titleContains("Rebel Cell")));
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardModifier(self, yerka, objectiveOnTable, self.getOwner(), Filters.Jedi));
        modifiers.add(new ImmuneToTitleModifier(self, yerka, objectiveOnTable, Title.Alter));
        modifiers.add(new MayRetrieveForceWithCardOnlyOncePerTurnModifier(self, yerka, retrieveLimit));
        return modifiers;
    }

    @Override
    protected List<com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (self.getWhileInPlayData() == null && GameConditions.canSpot(game, self, ovsObjective(self))) {
            self.setWhileInPlayData(new WhileInPlayData(Boolean.TRUE));
        }
        return null;
    }
}
