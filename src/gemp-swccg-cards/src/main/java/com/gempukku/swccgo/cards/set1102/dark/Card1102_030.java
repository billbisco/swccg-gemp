package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Character
 * Subtype: Alien
 * Title: Djas Puhr
 */
public class Card1102_030 extends AbstractAlien {
    public Card1102_030() {
        super(Side.DARK, 1, 4, 1, 4, 4, "Djas Puhr", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Sakiyan, a race often employed as assassins. Bounty hunter. Incredible infrared peripheral vision. Has excellent aural and olfactory senses. Often tracks by scent.");
        setGameText("Power +3 under 'nighttime conditions'. Blaster Rifle deploys and fires for free on (and is a matching weapon for) Djas Puhr. Immune to attrition < 3 (< 5 while armed with a weapon).");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.SAKIYAN);
        setMatchingWeaponFilter(Filters.blaster_rifle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter blasterRifleOnDjas = Filters.and(Filters.blaster_rifle, Filters.attachedTo(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new UnderNighttimeConditionConditions(self), 3));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.blaster_rifle, self));
        modifiers.add(new FiresForFreeModifier(self, blasterRifleOnDjas));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, new ArmedWithCondition(self, Filters.weapon))));
        return modifiers;
    }
}
