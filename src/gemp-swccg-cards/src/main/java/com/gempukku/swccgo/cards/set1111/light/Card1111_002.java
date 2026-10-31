package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Verrack
 */
public class Card1111_002 extends AbstractRebel {
    public Card1111_002() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Captain Verrack", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mon Calamari computer technician. Expert in power systems. His skills are desperately needed by Rebel fleet. Gunner.");
        setGameText("Adds 1 to power of anything he pilots (2 if any Star Cruiser). While aboard a Star Cruiser or with Admiral Ackbar, opponent's weapon and battle destiny draws at same system are -2. Other Mon Calamari at same system are forfeit +2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.GUNNER, Keyword.CAPTAIN);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        OrCondition aboardCruiserOrWithAckbar = new OrCondition(new AboardCondition(self, Filters.Star_Cruiser), new WithCondition(self, Filters.Ackbar));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(1, 2, Filters.Star_Cruiser)));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.sameSystem(self), aboardCruiserOrWithAckbar, -2, opponent, true));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.at(Filters.sameSystem(self))), aboardCruiserOrWithAckbar, -2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Mon_Calamari_character, Filters.not(self), Filters.atSameSystem(self)), 2));
        return modifiers;
    }
}
