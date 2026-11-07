package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

/** Virtual Block 6.3: Justifier. */
public class Card1263_018 extends AbstractCapitalStarship {
    public Card1263_018() {
        super(Side.DARK, 1, 9, 8, 6, null, 3, 10, "Justifier", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setGameText("Lost if your Endor or your other capital starship on table. May add 1 admiral as a pilot. Permanent pilots aboard provide total ability of 4. Immune to attrition < 7 (< 10 if your admiral aboard).");
        addIcon(Icon.PILOT, 2);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.DAGOBAH, Icon.LEGACY_BLOCK_6);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(1);
        setAsLegacy(true);
    }
    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.admiral;
    }
    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return List.of(new AbstractPermanentPilot(2) {}, new AbstractPermanentPilot(2) {});
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionLessThanModifier(self,
                new ConditionEvaluator(7, 10, new HasAboardCondition(self, Filters.and(Filters.your(self), Filters.admiral)))));
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && Filters.canSpotFromAllOnTable(game, Filters.and(Filters.your(self),
                Filters.or(Filters.Endor_system, Filters.and(Filters.other(self), Filters.capital_starship))))) {
            var action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make Justifier lost");
            action.appendEffect(new LoseCardFromTableEffect(action, self));
            return List.of(action);
        }
        return null;
    }
}
