package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Imperial
 * Title: ISB Sector Commander
 */
public class Card1273_007 extends AbstractImperial {
    public Card1273_007() {
        super(Side.DARK, 3, 2, 2, 2, 3, "ISB Sector Commander", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("ISB leader.");
        setGameText("Adds 2 to power of anything he pilots. Once per game, when deployed, may retrieve up to two Imperials (or one into hand). While present at a site you control, Effects with 'Celebration' in title at related system are suspended and, once per turn, may use 1 Force to deploy a trooper from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    private static Condition presentAtSiteYouControl(PhysicalCard self) {
        return new PresentAtCondition(self, Filters.and(Filters.site, Filters.controls(self.getOwner())));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter celebrationsAtRelatedSystem = Filters.and(Filters.Effect, Filters.titleContains("Celebration"),
                Filters.attachedTo(Filters.relatedSystem(self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new SuspendsCardModifier(self, celebrationsAtRelatedSystem, presentAtSiteYouControl(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ISB_SECTOR_COMMANDER__RETRIEVE_IMPERIALS;

        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

            final OptionalGameTextTriggerAction retrieveTwo = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            retrieveTwo.setText("Retrieve up to two Imperials");
            retrieveTwo.setActionMsg("Retrieve up to two Imperials");
            retrieveTwo.appendUsage(
                    new OncePerGameEffect(retrieveTwo));
            retrieveTwo.appendEffect(
                    new RetrieveCardsEffect(retrieveTwo, playerId, 2, true, Filters.Imperial));
            actions.add(retrieveTwo);

            final OptionalGameTextTriggerAction retrieveIntoHand = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            retrieveIntoHand.setText("Retrieve an Imperial into hand");
            retrieveIntoHand.setActionMsg("Retrieve an Imperial into hand");
            retrieveIntoHand.appendUsage(
                    new OncePerGameEffect(retrieveIntoHand));
            retrieveIntoHand.appendEffect(
                    new RetrieveCardIntoHandEffect(retrieveIntoHand, playerId, Filters.Imperial));
            actions.add(retrieveIntoHand);
            return actions;
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ISB_SECTOR_COMMANDER__DEPLOY_TROOPER;

        if (presentAtSiteYouControl(self).isFulfilled(game.getGameState(), game.getModifiersQuerying())
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy trooper from Reserve Deck");
            action.setActionMsg("Deploy a trooper from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.trooper, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
