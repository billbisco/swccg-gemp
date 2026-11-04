package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Alien
 * Title: Aayla Secura
 */
public class Card1283_015 extends AbstractAlien {
    public Card1283_015() {
        super(Side.LIGHT, 1, 5, 4, 6, 7, "Aayla Secura", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Female Twi'lek.");
        setGameText("Power +1 for each of opponent's characters here. When with two of your aliens (or clones), adds one battle destiny. May not be targeted by weapons unless each of your other aliens and clones present are 'hit.' Immune to You Are Beaten and attrition < 5.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.TWILEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, 2,
                Filters.and(Filters.your(self), Filters.or(Filters.alien, Filters.clone))), 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new PresentCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.or(Filters.alien, Filters.clone), Filters.not(Filters.hit)))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
