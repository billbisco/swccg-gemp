package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections Gold
 * Type: Effect
 * Title: Establish Secret Base & Ominous Rumors
 */
public class Card15_179 extends AbstractNormalEffect {
    public Card15_179() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Establish Secret Base & Ominous Rumors", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        addComboCardTitles("Establish Secret Base",Title.Ominous_Rumors);
        setGameText("Deploy on Endor system. Your Force drains at X other battleground systems are +1, where X = number of Endor sites you occupy. Your Force drains here are +2 while you control at least 2 Endor battleground sites with AT-STs and/or biker scouts. Place Effect in Used Pile if opponent controls this system or three Endor sites.");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new ForceDrainModifier(self, Filters.here(self),
                new ControlsWithCondition(self, self.getOwner(), 2, Filters.and(Filters.Endor_site, Filters.battleground),
                        Filters.or(Filters.biker_scout, Filters.and(Filters.piloted, Filters.AT_ST))), 2, self.getOwner()));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Endor_system;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.and(Filters.battleground_system, Filters.not(Filters.Endor_system)))) {
            int numSites = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Endor_site, Filters.occupies(playerId)));
            if (GameConditions.isNumTimesPerTurn(game, self, playerId, numSites, gameTextSourceCardId, gameTextActionId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add 1 to Force drain");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerTurnEffect(action, numSites));
                // Perform result(s)
                action.appendEffect(
                        new AddToForceDrainEffect(action, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && (GameConditions.controls(game, opponent, Filters.sameSystem(self))
                    || GameConditions.controls(game, opponent, 3, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Endor_site))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
