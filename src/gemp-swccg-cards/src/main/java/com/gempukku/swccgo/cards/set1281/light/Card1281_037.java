package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.StackCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CancelCardOnTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInForcePileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInReserveDeckFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.ReturnedCardToHandFromTableResult;
import com.gempukku.swccgo.logic.timing.results.StackedFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Luck
 */
public class Card1281_037 extends AbstractNormalEffect {
    public Card1281_037() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Scoundrel's Luck", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("'You said you wanted to be around when I made a mistake, well, this could be it, sweetheart.'");
        setGameText("Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here. Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("Infiltration");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.title("Infiltration"), Filters.title("Unlikely Allies"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeFlippedModifier(self, twoNonDroidScoundrelsAtBattlegrounds(game, self),
                Filters.Hunt_Down_And_Destroy_The_Jedi));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        Filter scoundrelEffect = Filters.and(Icon.SCOUNDREL, Filters.Effect);

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            GameTextActionId stackId = GameTextActionId.LEGACY__SCOUNDRELS_LUCK__STACK_SCOUNDREL_CARDS_FROM_RESERVE_DECK;
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, stackId);
            action.setPerformingPlayer(playerId);
            action.setText("Stack three [Scoundrel] cards from Reserve Deck");
            action.appendEffect(new StackCardsFromReserveDeckEffect(action, playerId, 3, 3, self, true, Filters.and(Icon.SCOUNDREL)));
            actions.add(action);
        }

        if (TriggerConditions.leavesTable(game, effectResult, scoundrelEffect)) {
            PhysicalCard leftTable = getCardThatLeftTable(effectResult);
            if (leftTable != null && leftTable.getStackedOn() != self) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(leftTable) + " here");
                stackScoundrelEffectHere(action, leftTable, self);
                actions.add(action);
            }
        }

        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)) {
            Collection<PhysicalCard> onTable = Filters.filterActive(game, self, scoundrelEffect);
            if (!onTable.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack all [Scoundrel] Effects here");
                action.appendEffect(new StackCardsFromTableEffect(action, onTable, self, true));
                actions.add(action);
            }
        }

        if (TriggerConditions.isTableChanged(game, effectResult)
                && twoNonDroidScoundrelsAtBattlegrounds(game, self).isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            PhysicalCard theirFire = Filters.findFirstActive(game, self, Filters.Their_Fire_Has_Gone_Out_Of_The_Universe);
            if (theirFire != null && GameConditions.canBeFlipped(game, theirFire)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(theirFire));
                action.setActionMsg("Flip " + GameUtils.getCardLink(theirFire));
                action.appendEffect(new FlipCardEffect(action, theirFire));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter scoundrelEffect = Filters.and(Icon.SCOUNDREL, Filters.Effect);

        GameTextActionId deployFromHere = GameTextActionId.LEGACY__SCOUNDRELS_LUCK__DEPLOY_SCOUNDREL_EFFECT;
        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, deployFromHere)
                && GameConditions.hasStackedCards(game, self, Filters.and(scoundrelEffect, Filters.deployable(self, null, false, 0)))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployFromHere);
            action.setText("Deploy a [Scoundrel] Effect from here");
            action.setActionMsg("Deploy a [Scoundrel] Effect stacked on " + GameUtils.getCardLink(self));
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployStackedCardEffect(action, self, scoundrelEffect, false));
            actions.add(action);
        }

        GameTextActionId weaponId = GameTextActionId.LEGACY__SCOUNDRELS_LUCK__DEPLOY_CHARACTER_WEAPON_FROM_LOST_PILE;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, weaponId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, weaponId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, weaponId);
            action.setText("Deploy character weapon from Lost Pile");
            action.setActionMsg("Place a card from Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, Filters.any, false));
            action.appendEffect(new DeployCardFromLostPileEffect(action, Filters.and(Filters.character_weapon, Filters.except(Filters.Concussion_Grenade)), false));
            actions.add(action);
        }

        return actions;
    }

    private Condition twoNonDroidScoundrelsAtBattlegrounds(final SwccgGame game, final PhysicalCard self) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return Filters.countActive(game, self, Filters.and(Filters.scoundrel, Filters.not(Filters.droid), Filters.presentAt(Filters.battleground))) >= 2;
            }
        };
    }

    private PhysicalCard getCardThatLeftTable(EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)
            return ((LostFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)
            return ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE)
            return ((CancelCardOnTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE)
            return ((StackedFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_TABLE)
            return ((ReturnedCardToHandFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_RESERVE_DECK_FROM_TABLE)
            return ((PutCardInReserveDeckFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_FORCE_PILE_FROM_TABLE)
            return ((PutCardInForcePileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE)
            return ((PutCardInUsedPileFromTableResult) effectResult).getCard();
        if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE)
            return ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard();
        return null;
    }

    private void stackScoundrelEffectHere(Action action, PhysicalCard card, PhysicalCard self) {
        Zone zone = GameUtils.getZoneFromZoneTop(card.getZone());
        if (zone == Zone.LOST_PILE) {
            action.appendEffect(new StackOneCardFromLostPileEffect(action, card, self, true, false, true));
        }
        else if (zone == Zone.HAND) {
            action.appendEffect(new StackOneCardFromHandEffect(action, card, self, true));
        }
        else if (zone == Zone.FORCE_PILE) {
            action.appendEffect(new StackOneCardFromForcePileEffect(action, card, self, true));
        }
        else if (zone == Zone.OUT_OF_PLAY || zone == Zone.STACKED || zone == Zone.STACKED_FACE_DOWN) {
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            game.getGameState().removeCardsFromZone(Collections.singleton(card));
                            game.getGameState().stackCard(card, self, true, false, false);
                        }
                    });
        }
        else {
            action.appendEffect(new StackOneCardFromPileEffect(action, self.getOwner(), card, self, true, false, false));
        }
    }
}
