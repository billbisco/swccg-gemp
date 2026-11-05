package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeBlownAwayModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: A Stunning Move / A Valuable Hostage
 */
public class Card1273_020 extends AbstractObjective {
    public static final String PRINTED_GAMETEXT = "A Stunning Move: Deploy Palpatine's Quarters, Private Platform, and Insidious Prisoner. For remainder of game, Sidious and Imperials are lost. Once per turn, may deploy a Blockade Flagship site from Reserve Deck; reshuffle. Blockade Flagship may not be 'blown away.' While this side up, Insidious Prisoner only follows opponent's characters when moving to Palpatine's Quarters. Once per game, may deploy (for -2 Force) Cyborg Commander or The Count to a site you occupy from Reserve Deck; reshuffle. Flip this card if Insidious Prisoner is on Blockade Flagship. "
            + "A Valuable Hostage: Immediately take into hand from Reserve Deck; reshuffle (or retrieve into hand) a [Presence] droid. While this side up, immunity to attrition of opponent's Jedi, starships, and vehicles is limited to < 5. During your control phase, opponent loses 1 Force for each battleground you control. Once during your turn, may search your Force Pile and reveal any one card; reshuffle. Opponent may lose 2 Force to place that card on bottom of your Used Pile; otherwise, take it into hand. Flip this card if Insidious Prisoner not at a Blockade Flagship site.";

    static final Filter BLOCKADE_FLAGSHIP_SITE = Filters.siteOfStarshipOrVehicle(Persona.BLOCKADE_FLAGSHIP, true);

    public Card1273_020() {
        super(Side.DARK, 0, Title.A_Stunning_Move, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(PRINTED_GAMETEXT);
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
    }

    static Filter remainderLostFilter() {
        return Filters.or(Filters.Sidious, CardType.IMPERIAL);
    }

    static boolean isAtBlockadeFlagshipSite(SwccgGame game, PhysicalCard insidiousPrisoner) {
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), insidiousPrisoner);
        return location != null && BLOCKADE_FLAGSHIP_SITE.accepts(game, location);
    }

    static RequiredGameTextTriggerAction makeRemainderLostAction(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Collection<PhysicalCard> toLose = Filters.filterActive(game, self, remainderLostFilter());
        if (toLose.isEmpty()) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Make Sidious and Imperials lost");
        action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");
        action.appendEffect(new LoseCardsFromTableEffect(action, toLose));
        return action;
    }

    /**
     * "Once per turn, may [T] a Blockade Flagship site": part of the remainder-of-game text, so both sides offer it.
     */
    static TopLevelGameTextAction deployBlockadeFlagshipSiteAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_STUNNING_MOVE__DEPLOY_BLOCKADE_FLAGSHIP_SITE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Blockade Flagship site from Reserve Deck");
            action.setActionMsg("Deploy a Blockade Flagship site from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, BLOCKADE_FLAGSHIP_SITE, true));
            return action;
        }
        return null;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(getDeployCardFromReserveDeckEffect(action, Card1273_013.PALPATINES_QUARTERS, "Choose Palpatine's Quarters to deploy"));
        action.appendRequiredEffect(getDeployCardFromReserveDeckEffect(action, Filters.Private_Platform, "Choose Private Platform to deploy"));
        action.appendRequiredEffect(getDeployCardToTargetFromReserveDeckEffect(action, Filters.Insidious_Prisoner, Card1273_013.PALPATINES_QUARTERS, "Choose Insidious Prisoner to deploy on Palpatine's Quarters"));
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final int permCardId = self.getPermanentCardId();
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard objective = game.findCardByPermanentId(permCardId);
                                if (TriggerConditions.isTableChanged(game, effectResult)) {
                                    RequiredGameTextTriggerAction loseAction = makeRemainderLostAction(game, objective, gameTextSourceCardId);
                                    if (loseAction != null) {
                                        actions.add(loseAction);
                                    }
                                }
                                return actions;
                            }
                        }));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeBlownAwayModifier(self, Filters.Blockade_Flagship), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        TopLevelGameTextAction deploySite = deployBlockadeFlagshipSiteAction(playerId, game, self, gameTextSourceCardId);
        if (deploySite != null) {
            actions.add(deploySite);
        }

        // Cyborg Commander and The Count are Grievous's and Dooku's printed titles on the Virtual Block 7 sheets
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_STUNNING_MOVE__DEPLOY_GRIEVOUS_OR_DOOKU;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Grievous or Dooku from Reserve Deck");
            action.setActionMsg("Deploy Grievous or Dooku (for -2 Force) to a site you occupy from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Grievous, Filters.Dooku),
                            Filters.and(Filters.site, Filters.occupies(playerId)), -2, true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard insidiousPrisoner = Filters.findFirstActive(game, self, Filters.Insidious_Prisoner);
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && insidiousPrisoner != null
                && isAtBlockadeFlagshipSite(game, insidiousPrisoner)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
