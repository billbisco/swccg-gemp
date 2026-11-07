package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.1 Breached Defenses & Molator. */
public class Card1261_029 extends AbstractNormalEffect {
    private static final GameTextActionId SEARCH = GameTextActionId.LEGACY__BREACHED_DEFENSES_MOLATOR__TAKE_EFFECT;
    private static final Filter VIRTUAL_CROSSFIRE = Filters.and(Filters.title(Title.Crossfire), new Filter() {
        @Override
        public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
            return card.getBlueprint().hasVirtualSuffix();
        }
    });
    private static final Filter LOCATION_EFFECT = Filters.and(Filters.Effect,
            Filters.deploys_on_location,
            Filters.not(Filters.title(Title.Expand_The_Empire)));

    public Card1261_029() {
        super(Side.DARK, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Breached_Defenses + " & " + Title.Molator,
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent(Title.Breached_Defenses, Uniqueness.UNIQUE),
                new TitleComponent(Title.Molator, Uniqueness.UNIQUE));
        setGameText("Deploy on table. Once per game, unless [Virtual] Crossfire on table (or out of play), may use 3 Force to take an Effect that deploys on a location (except Expand The Empire) into hand from Reserve Deck; reshuffle. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Your non-Dark Jedi characters without [Permanent Weapon] or [Maintenance] are immune to Goo Nee Tay. May not be canceled. (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                     PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isOncePerGame(game, self, SEARCH)
                || GameConditions.canSpot(game, self, SpotOverride.INCLUDE_SUSPENDED, VIRTUAL_CROSSFIRE)
                || GameConditions.isOutOfPlay(game, VIRTUAL_CROSSFIRE)
                || !GameConditions.canUseForce(game, playerId, 3)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, SEARCH))
            return null;

        var action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, SEARCH);
        action.setText("Take location Effect into hand from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendCost(new UseForceEffect(action, playerId, 3));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, LOCATION_EFFECT, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotBeCanceledModifier(self, self));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter eligible = Filters.and(Filters.your(self), Filters.character,
                Filters.not(Filters.or(Filters.Dark_Jedi, Filters.icon(Icon.PERMANENT_WEAPON), Filters.icon(Icon.MAINTENANCE))));
        return List.of(new ImmuneToTitleModifier(self, eligible, Title.Goo_Nee_Tay),
                new ImmuneToDeployCostModifiersToLocationModifier(self, eligible, Filters.title(Title.Goo_Nee_Tay), Filters.any));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                    PhysicalCard self, int sourceId) {
        var battle = game.getGameState().getBattleState();
        if (battle == null || battle.isCanceled() || battle.isReachedDamageSegment())
            return null;

        Filter eligible = Filters.and(Filters.your(self), Filters.participatingInBattle,
                Filters.or(Filters.character, Filters.and(Filters.starship, Filters.not(Filters.icon(Icon.INDEPENDENT)))));
        if (!TriggerConditions.isAboutToBeLost(game, result, eligible))
            return null;
        var loss = (AboutToLoseCardFromTableResult) result;
        var departing = loss.getCardToBeLost();
        if (loss.getPreventableCardEffect() == null)
            return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Defer loss of " + GameUtils.getFullName(departing) + " until battle ends");
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                loss.getPreventableCardEffect().preventEffectOnCard(departing);
                action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                        new ForfeitValueToUseWhenForfeitedModifier(self, departing, 0), null));

                // As with the original Molator, a resolved deferral survives the Effect leaving play.
                var ledger = self.getForRemainderOfGameData();
                if (!ledger.containsKey(sourceId)) {
                    ledger.put(sourceId, new ForRemainderOfGameData(new ArrayList<PhysicalCard>()));
                    action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action,
                            new DeferredBattleLoss(self.getPermanentCardId(), sourceId)));
                }
                var pending = ledger.get(sourceId).getPhysicalCards();
                if (!pending.contains(departing))
                    pending.add(departing);
            }
        });
        return List.of(action);
    }

    private static class DeferredBattleLoss extends AbstractActionProxy {
        private final int sourcePermanentId;
        private final int sourceId;

        DeferredBattleLoss(int sourcePermanentId, int sourceId) {
            this.sourcePermanentId = sourcePermanentId;
            this.sourceId = sourceId;
        }

        @Override
        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult result) {
            if (!TriggerConditions.battleEnded(game, result) && !TriggerConditions.battleCanceled(game, result))
                return null;
            var source = game.findCardByPermanentId(sourcePermanentId);
            if (source == null || !source.getForRemainderOfGameData().containsKey(sourceId))
                return null;
            var pending = source.getForRemainderOfGameData().get(sourceId).getPhysicalCards();
            pending.removeIf(card -> !Filters.onTable.accepts(game, card));
            if (pending.isEmpty())
                return null;

            var action = new RequiredGameTextTriggerAction(source, sourceId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setRepeatableTrigger(true);
            action.setText("Lose a card whose battle loss was deferred");
            action.appendTargeting(new ChooseCardOnTableEffect(action, game.getGameState().getCurrentPlayerId(),
                    "Choose card whose loss was deferred", pending) {
                @Override
                protected void cardSelected(PhysicalCard card) {
                    pending.remove(card);
                    action.addAnimationGroup(card);
                    action.appendEffect(new LoseCardFromTableEffect(action, card, true));
                }

                @Override
                protected boolean getUseShortcut() {
                    return true;
                }
            });
            return List.of(action);
        }
    }
}
