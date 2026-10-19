package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.cards.actions.PlayCharacterAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Rescue On Geonosis
 */
public class Card1291_067 extends AbstractMission {
    public Card1291_067() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Rescue On Geonosis", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on Begun, The Clone War Has. Once per turn, may take Amidala into hand from Reserve Deck; reshuffle. Petranaki Arena is a prison. May deploy Amidala imprisoned there (for free) from hand or Lost Pile and, unless this Mission complete, Amidala lost if not imprisoned there and she may not be transferred while imprisoned. If you just initiated a Force drain at Petranaki Arena, release a captive there (Mission complete).\n{Reward}: If Obi-Wan is present with Anakin or Amidala, attrition against opponent is +2.\n{Turn the Tide}: If opponent controls Petranaki Arena (or if Amidala not on table), place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter obiPresentWith = Filters.and(Filters.ObiWan, Filters.or(Filters.presentWith(self, Filters.Anakin), Filters.presentWith(self, Filters.Amidala)));
        Condition reward = new AndCondition(new InPlayDataSetCondition(self),
                new com.gempukku.swccgo.cards.conditions.OnTableCondition(self, obiPresentWith));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.Geonosis_Petranaki_Arena, Keyword.PRISON));
        modifiers.add(new AttritionModifier(self, reward, 2, opponent));
        modifiers.add(new MayNotBeTransferredModifier(self, Filters.and(Filters.Amidala, Filters.imprisonedIn(Filters.Geonosis_Petranaki_Arena)),
                new InPlayDataNotSetCondition(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId takeId = GameTextActionId.LEGACY__RESCUE_ON_GEONOSIS__TAKE_AMIDALA;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, takeId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, takeId);
            action.setText("Take Amidala into hand from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Amidala, true));
            actions.add(action);
        }
        GameTextActionId imprisonId = GameTextActionId.LEGACY__RESCUE_ON_GEONOSIS__DEPLOY_AMIDALA_IMPRISONED;
        if (GameConditions.canSpotLocation(game, Filters.Geonosis_Petranaki_Arena)) {
            if (GameConditions.hasInHand(game, playerId, Filters.Amidala)) {
                TopLevelGameTextAction fromHand = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
                fromHand.setText("Deploy Amidala imprisoned from hand");
                fromHand.appendEffect(new PassthruEffect(fromHand) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        PhysicalCard amidala = null;
                        for (PhysicalCard card : game.getGameState().getHand(playerId)) {
                            if (Filters.Amidala.accepts(game, card)) {
                                amidala = card;
                                break;
                            }
                        }
                        if (amidala == null) {
                            return;
                        }
                        PlayCardAction play = new PlayCharacterAction(game, self, amidala, true, 0,
                                DeployAsCaptiveOption.deployAsImprisonedCaptive(), null, Filters.Geonosis_Petranaki_Arena);
                        fromHand.appendEffect(new StackActionEffect(fromHand, play));
                    }
                });
                actions.add(fromHand);
            }
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, imprisonId, Persona.AMIDALA)) {
                TopLevelGameTextAction fromLost = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, imprisonId);
                fromLost.setText("Deploy Amidala imprisoned from Lost Pile");
                fromLost.appendEffect(new DeployCardFromPileEffect(fromLost, playerId, Zone.LOST_PILE, Filters.Amidala, Filters.Geonosis_Petranaki_Arena, null, null, null, true, null, 0, null, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), DeployAsCaptiveOption.deployAsImprisonedCaptive(), false, false));
                actions.add(fromLost);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        Collection<PhysicalCard> captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                Filters.and(Filters.captive, Filters.at(Filters.Geonosis_Petranaki_Arena)));
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Geonosis_Petranaki_Arena)
                && !captives.isEmpty()) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Release a captive (Mission complete)");
            action.appendEffect(new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(true)));
            action.appendEffect(
                    new ChooseCardOnTableEffect(action, playerId, "Choose captive to release", captives) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(new ReleaseCaptivesEffect(action, Collections.singleton(selectedCard)));
                        }
                    });
            actions.add(action);
        }
        if (!TriggerConditions.isTableChanged(game, effectResult)) {
            return actions.isEmpty() ? null : actions;
        }
        String opponent = game.getOpponent(self.getOwner());
        boolean complete = self.getWhileInPlayData() != null;
        PhysicalCard amidala = Filters.findFirstFromAllOnTable(game, Filters.Amidala);
        if (!complete && amidala != null
                && !Filters.imprisonedIn(Filters.Geonosis_Petranaki_Arena).accepts(game, amidala)) {
            RequiredGameTextTriggerAction lose = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            lose.setSingletonTrigger(true);
            lose.setText("Amidala lost (not imprisoned at Petranaki Arena)");
            lose.appendEffect(new LoseCardFromTableEffect(lose, amidala));
            actions.add(lose);
        }
        boolean amidalaOnTable = GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.Amidala);
        if (GameConditions.controls(game, opponent, Filters.Geonosis_Petranaki_Arena)
                || (complete && !amidalaOnTable)) {
            RequiredGameTextTriggerAction ttt = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            ttt.setSingletonTrigger(true);
            ttt.setText("Place in Used Pile");
            ttt.appendEffect(new PlaceCardInUsedPileFromTableEffect(ttt, self));
            actions.add(ttt);
        }
        return actions.isEmpty() ? null : actions;
    }
}
