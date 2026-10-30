package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Trooper
 */
public class Card1102_019 extends AbstractRebel {
    public Card1102_019() {
        super(Side.LIGHT, 1, 1, 1, 1, 2, "Rebel Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian Corvette trooper Ensign Chad Hilse, an Alderaanian, typifies the loyal Rebel volunteers dedicated to defeating the Empire. Trained in starship and ground combat.");
        setGameText("When drawn for weapon or battle destiny, destiny +2. While present at a site and armed with a blaster rifle, forfeit +2 and draws one battle destiny if unable to otherwise.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, 2));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentArmed = new AndCondition(new PresentAtCondition(self, Filters.site), new ArmedWithCondition(self, Filters.blaster_rifle));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, presentArmed, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, presentArmed, 1));
        return modifiers;
    }
}
