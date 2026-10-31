package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Chief Chirpa
 */
public class Card1109_003 extends AbstractAlien {
    public Card1109_003() {
        super(Side.LIGHT, 2, 3, 0, 2, 2, Title.Chief_Chirpa, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader of Wicket's Ewok tribe for 42 seasons. Head of the Council of Elders. Decided to spare Han's Rebels and join their fight against the Empire.");
        setGameText("Deploys only on Endor. Ewoks on Endor are power +1. 'Vehicle with armor' on Ewok Catapult may be treated as 'non-Dark Jedi character' ('crashes' is then treated as 'lost'). May not be targeted by opponent.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.EWOK);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Endor;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Ewok, Filters.on(Title.Endor)), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Ewok_Catapult), ModifyGameTextType.LEGACY__CHIRPA_V__EWOK_CATAPULT_TARGETS_CHARACTERS));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.opponents(self)));
        return modifiers;
    }
}
