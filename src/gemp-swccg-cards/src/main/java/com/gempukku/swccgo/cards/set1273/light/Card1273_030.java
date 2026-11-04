package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFireWeaponCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Wes Janson, Rogue Veteran
 */
public class Card1273_030 extends AbstractRebel {
    public Card1273_030() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Wes Janson, Rogue Veteran", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Served for many years as a gunner with friend Jek Porkins in the Tierfon Yellow Aces. Expert marksmanship helped the Alliance earn some of its earliest victories.");
        setGameText("Adds 2 to the power of anything he pilots. Once per turn, may deploy from Reserve Deck (or deploy from hand) Disruptor Pistol for free on Wes; reshuffle. May fire Disruptor Pistol for 1 Force. Power and forfeit +1 while with Hobbie or Wedge. If about to leave table, place cards on Wes in owner's Used Pile.");
        addPersona(Persona.WES);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER);
    }

    private static Filter disruptorPistol() {
        return Filters.and(Filters.title("Disruptor Pistol"), Filters.weapon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withHobbieOrWedge = new WithCondition(self, Filters.or(Filters.Hobbie, Filters.Wedge));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DefinedByGameTextFireWeaponCostModifier(self, disruptorPistol(), Filters.sameCardId(self), 1));
        modifiers.add(new PowerModifier(self, withHobbieOrWedge, 1));
        modifiers.add(new ForfeitModifier(self, withHobbieOrWedge, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WES_JANSON_ROGUE_VETERAN__DEPLOY_DISRUPTOR_PISTOL;
        Filter onWes = Filters.sameCardId(self);

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Disruptor Pistol from Reserve Deck");
                action.setActionMsg("Deploy Disruptor Pistol on " + GameUtils.getCardLink(self) + " from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(action, disruptorPistol(), onWes, true, true));
                actions.add(action);
            }
            if (GameConditions.hasInHand(game, playerId, disruptorPistol())) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Disruptor Pistol from hand");
                action.setActionMsg("Deploy Disruptor Pistol on " + GameUtils.getCardLink(self) + " from hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardToTargetFromHandEffect(action, playerId, disruptorPistol(), onWes, true));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // If about to leave table, place cards on Wes in owner's Used Pile
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            final Collection<PhysicalCard> cardsOnWes = Filters.filterAllOnTable(game, Filters.attachedTo(self));
            if (!cardsOnWes.isEmpty()) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place cards on " + GameUtils.getFullName(self) + " in Used Pile");
                action.setActionMsg("Place cards on " + GameUtils.getCardLink(self) + " in owner's Used Pile");
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                for (PhysicalCard card : cardsOnWes) {
                                    result.getPreventableCardEffect().preventEffectOnCard(card);
                                }
                                action.appendEffect(
                                        new PlaceCardsInUsedPileFromTableEffect(action, cardsOnWes));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
