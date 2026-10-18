package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron 7
 */
public class Card1230_113 extends AbstractStarfighter {
    public Card1230_113() {
        super(Side.LIGHT, 3, 2, 3, null, 3, 5, 4, "Red Squadron 7", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Incom starfighter flown by Keir Santage. Flew twelve sorties against Imperial shipyards at Fondor.");
        setGameText("Heroic. May add 1 pilot. Once per game may place a non-[Immune to Alter] Effect at same system in owner's Used Pile. Corran Horn and Keir Santage deploy -1 aboard; while either piloting, immune to attrition < 4.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.DEATH_STAR_II);
        addKeywords(Keyword.RED_SQUADRON, Keyword.HEROIC);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Corran_Horn, Filters.Keir_Santage));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter pilots = Filters.or(Filters.Corran_Horn, Filters.Keir_Santage);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, pilots, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter pilots = Filters.or(Filters.Corran_Horn, Filters.Keir_Santage);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, pilots, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(
                new HasPilotingCondition(self, Filters.Corran_Horn),
                new HasPilotingCondition(self, Filters.Keir_Santage)), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RED_SQUADRON_7_V__PLACE_EFFECT_IN_USED_PILE;
        Filter effectFilter = Filters.and(Filters.Effect, Filters.atSameLocation(self),
                Filters.not(Filters.immune_to_Alter));

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.system)
                && GameConditions.canTarget(game, self, effectFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place an Effect in Used Pile");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose Effect", effectFilter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, selectedCard));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
