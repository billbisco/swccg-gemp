package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Execution Arena (Pit)
 */
public class Card1240_052 extends AbstractSite {
    public Card1240_052() {
        super(Side.DARK, "Execution Arena (Pit)", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, Endor or Hoth. May deploy Dark Waters here from Reserve Deck; reshuffle. [Virtual Block 4] Krayt Dragon Bones and your Force drains here are canceled.");
        setLocationLightSideGameText("If a ferocious creature here, characters and creatures cannot move from here, and, once during opponent's control phase, if opponent controls an adjacent site, lose 1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.TATOOINE);
        addMayNotBePartOfSystem(Title.Bespin);
        addMayNotBePartOfSystem(Title.Dagobah);
        addMayNotBePartOfSystem(Title.Endor);
        addMayNotBePartOfSystem(Title.Hoth);
    }

    private static Filter block4KraytDragonBones() {
        return Filters.and(Filters.Krayt_Dragon_Bones, Icon.LEGACY_BLOCK_4);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__EXECUTION_ARENA_PIT__DEPLOY_DARK_WATERS;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Title.Dark_Waters)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Dark Waters here from Reserve Deck");
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Dark_Waters, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, block4KraytDragonBones())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // [Virtual Block 4] Krayt Dragon Bones is canceled.
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, block4KraytDragonBones())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, block4KraytDragonBones(), Title.Krayt_Dragon_Bones);
            actions.add(action);
        }

        // Your Force drains here are canceled.
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerOnDarkSideOfLocation, self)
                && GameConditions.canCancelForceDrain(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            action.setActionMsg("Cancel Force drain at " + com.gempukku.swccgo.logic.GameUtils.getCardLink(self));
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition ferociousCreatureHere = new AtCondition(self, Filters.creature, self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.or(Filters.character, Filters.creature), ferociousCreatureHere, self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        if (TriggerConditions.isStartOfOpponentsPhase(game, effectResult, Phase.CONTROL, playerOnLightSideOfLocation)
                && GameConditions.canSpot(game, self, Filters.and(Filters.creature, Filters.at(self)))
                && GameConditions.controls(game, opponent, Filters.adjacentSite(self))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force");
            action.setActionMsg("Make " + playerOnLightSideOfLocation + " lose 1 Force");
            action.appendEffect(
                    new LoseForceEffect(action, playerOnLightSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
