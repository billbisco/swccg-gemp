package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Character
 * Subtype: Dark Jedi Master
 * Title: Lord Sidious
 */
public class Card1116_046 extends AbstractDarkJediMaster {
    public Card1116_046() {
        super(Side.DARK, 1, 6, 5, 7, 8, "Lord Sidious", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Whenever a Jedi is forfeited and you have exactly two Dark Jedi on table, retrieve 2 Force. While at your Senate and you have no senators here, Sidious is politics +4 and his game text may not be canceled. Immune to [Virtual Set 16] objective.");
        addIcons(Icon.EPISODE_I);
        addPersona(Persona.SIDIOUS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        AtCondition atSenate = new AtCondition(self, Filters.and(Filters.your(self), Filters.titleContains("Senate")));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PoliticsModifier(self, self, atSenate, 4));
        modifiers.add(new ImmuneToTitleModifier(self, Title.We_Have_A_Plan));
        return modifiers;
    }
}
