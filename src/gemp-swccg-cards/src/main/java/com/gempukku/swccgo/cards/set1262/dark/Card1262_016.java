package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import java.util.List;

/** Legacy Block 6.2: Rancor Mount. */
public class Card1262_016 extends AbstractCreatureVehicle {
    public Card1262_016() {
        super(Side.DARK, 2, 3, 3, 5, null, 2, 5, "Rancor Mount", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("Indigenous to Dathomir, but found on several dozen worlds throughout the galaxy. Vicious predator. Sometimes kept as pets by eccentrics and crime lords.");
        setGameText("May add a witch as 'rider' (passenger). Ability = 1/2. While ridden by a Dathomir Witch, power +2, cannot be attacked, your Force drains here are +1, and vehicle and 'rider' are immune to attrition < 4.");
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6);
        setPassengerCapacity(1);
    }

    @Override
    protected Filter getGameTextValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.character, Filters.or(Filters.titleContains("witch"), Filters.loreContains("witch")));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DefinedByGameTextAbilityModifier(self, 0.5));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter rider = Filters.and(Filters.character, Filters.aboardAsPassenger(self));
        Filter witchRider = Filters.and(Filters.title("Dathomir Witch"), rider);
        Condition ridden = (state, querying) -> state.getAttachedCards(self, false).stream()
                .anyMatch(card -> witchRider.accepts(state, querying, card));
        Filter protectedMount = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
                return card.getCardId() == self.getCardId() && ridden.isFulfilled(state, querying);
            }
        };
        return List.of(new PowerModifier(self, ridden, 2), new MayNotBeAttackedModifier(self, protectedMount),
                new ForceDrainModifier(self, Filters.here(self), ridden, 1, self.getOwner()),
                new ImmuneToAttritionLessThanModifier(self, Filters.or(self, rider), ridden, 4));
    }
}
