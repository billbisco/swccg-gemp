package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Encampment
 */
public class Card1220_119 extends AbstractNormalEffect {
    public Card1220_119() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Encampment, Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Pirates, smugglers and Rebels operate from tiny camps which can be set up quickly and relocated at a moment's notice. A surprise visitor made Luke an unhappy camper.");
        setGameText("Deploy on a non-docking bay exterior planet site you occupy. While you occupy this site, at same and related sites, your characters may deploy regardless of location deployment restrictions. Once per game, your character may deploy -3 here. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.exterior_planet_site, Filters.not(Filters.docking_bay), Filters.occupies(self.getOwner()));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupyHere = new OccupiesCondition(playerId, Filters.here(self));
        Condition unusedDeployMinus3 = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.isOncePerGame(game, self, GameTextActionId.LEGACY__ENCAMPMENT_V__DEPLOY_MINUS_3);
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self,
                Filters.and(Filters.your(self), Filters.character), occupyHere, Filters.sameOrRelatedSite(self)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.character),
                unusedDeployMinus3, -3, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ENCAMPMENT_V__DEPLOY_MINUS_3;
        if (TriggerConditions.justDeployedToLocation(game, effectResult, Filters.and(Filters.your(self), Filters.character), Filters.here(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.skipInitialMessageAndAnimation();
            action.appendUsage(
                    new OncePerGameEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
