package com.gempukku.swccgo.cards.set1117.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: If The Trace Was Correct
 */
public class Card1117_018 extends AbstractNormalEffect {
    public Card1117_018() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "If The Trace Was Correct", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Galen's Fighter may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle).");
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationModifier(self, Filters.title("Galen's Fighter"), Filters.site));
        modifiers.add(new MovesForFreeModifier(self, Filters.title("Galen's Fighter")));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.title("Galen's Fighter"), 4));
        return modifiers;
    }
}
