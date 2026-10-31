package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo)
 */
public class Card1103_016 extends AbstractDroid {
    public Card1103_016() {
        super(Side.LIGHT, 2, 3, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setAlternateDestiny(5);
        setVirtualSuffix(true);
        setLore("Feisty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("During a battle with C-3PO, may not be targeted by weapons. While aboard a starfighter, it is power, maneuver, and hyperspeed +3. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.NAV_COMPUTER, Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new InBattleWithCondition(self, Filters.C3PO)));
        modifiers.add(new PowerModifier(self, starfighterAboard, 3));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, 3));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, 3));
        modifiers.add(new MayNotBePlacedInUsedPileModifier(self, Filters.inHand(playerId),
                new NotCondition(new CardsInHandEqualToOrMoreThanCondition(playerId, 13))));
        return modifiers;
    }
}
