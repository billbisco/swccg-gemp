package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardToLocationEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMakeAdditionalMovesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Subtype: Immediate
 * Title: The Time For Our Attack Has Come (V)
 */
public class Card1108_020 extends AbstractImmediateEffect {
    public Card1108_020() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "The Time For Our Attack Has Come", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"With the Imperial Fleet spread throughout the galaxy in a vain effort to engage us, it is relatively unprotected.\"");
        setGameText("If you just deployed or moved a second Rebel starfighter to a system, deploy a [Death Star II] starship there from under your Starting Effect; deploy on that starship: it is power and hyperspeed +2 and immune to attrition < 5. Once per game this starship may make an additional move. (Immune to Control.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starship, Icon.DEATH_STAR_II);
    }

    private static Filter yourRebelStarfighter(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.Rebel, Filters.starfighter);
    }

    private static Filter systemWithTwoRebelStarfighters(final PhysicalCard self) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                return Filters.countActive(gameState.getGame(), self, Filters.and(yourRebelStarfighter(self), Filters.at(physicalCard))) >= 2;
            }
        };
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter rebelStarfighter = yourRebelStarfighter(self);
        boolean secondAtSystem = (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, rebelStarfighter, Filters.system)
                || TriggerConditions.movedToLocationBy(game, effectResult, playerId, rebelStarfighter, Filters.system))
                && GameConditions.canSpot(game, self, systemWithTwoRebelStarfighters(self));
        if (!secondAtSystem) {
            return null;
        }

        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        Filter ds2Starship = Filters.and(Filters.starship, Icon.DEATH_STAR_II);
        PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0,
                Filters.and(Filters.your(self), ds2Starship, Filters.at(systemWithTwoRebelStarfighters(self))), null);
        if (action != null) {
            if (startingEffect != null && GameConditions.hasStackedCards(game, startingEffect, ds2Starship)) {
                action.appendCost(
                        new DeployStackedCardToLocationEffect(action, startingEffect, ds2Starship, systemWithTwoRebelStarfighters(self)));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter host = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, host, 2));
        modifiers.add(new HyperspeedModifier(self, host, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, host, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__TIME_FOR_OUR_ATTACK__ADDITIONAL_MOVE;
        Filter host = Filters.hasAttached(self);
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, host)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make attached starship move again");
            action.setActionMsg("This starship may make an additional move");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayMakeAdditionalMovesModifier(self, host, 1),
                            "Attached starship may make an additional move"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
