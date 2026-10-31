package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Character
 * Subtype: Alien
 * Title: Neb Dulo
 */
public class Card1105_013 extends AbstractAlien {
    public Card1105_013() {
        super(Side.LIGHT, 3, 1, 2, 2, 3, "Neb Dulo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Disciple of Davrilat, a complicated religion based on the sanctity of harmonics. Originally from the desert planet Tocoya. Strong protective instincts.");
        setGameText("Your aliens here are defense value +2. During battle with your Rep, the Rep may not be targeted by weapons.");
        addIcons(Icon.WARRIOR, Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.alien, Filters.here(self)), 2));

        Filter yourRep = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard rep = gameState.getRep(self.getOwner());
                return rep != null && Filters.sameCardId(rep).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, yourRep, new DuringBattleWithParticipantCondition(self)));
        return modifiers;
    }
}
