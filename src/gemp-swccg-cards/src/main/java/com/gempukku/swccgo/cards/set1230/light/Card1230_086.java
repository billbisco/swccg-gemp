package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Count Me In
 */
public class Card1230_086 extends AbstractNormalEffect {
    public Card1230_086() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Count_Me_In, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leia couldn't let Han out of her sight.");
        setGameText("Use 2 Force to deploy on Leia (even if a captive) or Back Door. Attrition against opponent is +1. Once per game, may place this Effect out of play to cancel a Force drain at same or related site.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS, Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return SpotOverride.INCLUDE_CAPTIVE;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Leia, Filters.title(Title.Back_Door));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.Leia, Filters.title(Title.Back_Door));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, 1, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COUNT_ME_IN_V__CANCEL_FORCE_DRAIN;

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.sameOrRelatedSite(self))
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place out of play to cancel Force drain");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
