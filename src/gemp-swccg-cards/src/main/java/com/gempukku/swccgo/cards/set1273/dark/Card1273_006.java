package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Droid
 * Title: IG-100 MagnaGuard
 */
public class Card1273_006 extends AbstractDroid {
    public Card1273_006() {
        super(Side.DARK, 3, 4, 3, 4, "IG-100 MagnaGuard", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setArmor(4);
        setGameText("Shockstaff deploys and fires for free on this droid. While on Blockade Flagship or Coruscant (or with Cyborg Commander), this droid is Heroic and power +2. Your characters here are immune to Ambush, Clash Of Sabers, and attrition < 4.");
        addIcons(Icon.WARRIOR, Icon.PRESENCE);
        addModelType(ModelType.GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter shockstaff = Filters.and(Filters.weapon, Filters.titleContains("Shockstaff"));
        // Cyborg Commander is Grievous's printed title on the Virtual Block 7 sheets
        Condition onFlagshipOrCoruscantOrWithGrievous = new OrCondition(
                new AtCondition(self, Filters.or(Filters.siteOfStarshipOrVehicle(Persona.BLOCKADE_FLAGSHIP, false), Filters.Coruscant_location)),
                new AboardCondition(self, Filters.Blockade_Flagship),
                new WithCondition(self, Filters.Grievous));
        Filter yourCharactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, shockstaff, self));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(shockstaff, Filters.attachedTo(self))));
        modifiers.add(new KeywordModifier(self, self, onFlagshipOrCoruscantOrWithGrievous, Keyword.HEROIC));
        modifiers.add(new PowerModifier(self, onFlagshipOrCoruscantOrWithGrievous, 2));
        modifiers.add(new ImmuneToTitleModifier(self, yourCharactersHere, Title.Ambush));
        modifiers.add(new ImmuneToTitleModifier(self, yourCharactersHere, Title.Clash_Of_Sabers));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, yourCharactersHere, 4));
        return modifiers;
    }
}
