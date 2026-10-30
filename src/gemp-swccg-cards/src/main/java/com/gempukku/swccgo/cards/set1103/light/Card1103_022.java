package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.UseCalculationForDeployCostModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: They're On Dantooine
 */
public class Card1103_022 extends AbstractNormalEffect {
    public Card1103_022() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Theyre_On_Dantooine, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You would prefer another target? A military target? Then name the system!'");
        setGameText("Deploy on table. Dantooine sites are immune to No Escape. At Dantooine sites, your Force generation is +1. While you control a Dantooine site, your squadrons may deploy without replacement for X Force, where X = squadron's power -3. May not be canceled. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition youControlDantooineSite = new ControlsCondition(self.getOwner(), Filters.Dantooine_site);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Dantooine_site, Title.No_Escape));
        modifiers.add(new ForceGenerationModifier(self, Filters.Dantooine_site, 1, self.getOwner()));
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        modifiers.add(new MayDeployToLocationModifier(self, Filters.and(Filters.your(self), Filters.squadron), youControlDantooineSite, Filters.location));
        modifiers.add(new UseCalculationForDeployCostModifier(self, Filters.and(Filters.your(self), Filters.squadron), youControlDantooineSite, new SubtractEvaluator(new PowerEvaluator(), 3)) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
                return "Deploy cost = power - 3";
            }
        });
        return modifiers;
    }
}
