package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.List;

/** Commando Training & K'lor'slug, final Legacy Light master p23. */
public class Card1261_079 extends AbstractNormalEffect {
    private static final Filter QUALIFIED_REBEL = Filters.and(Filters.Rebel, Filters.not(Filters.Jedi),
            Filters.not(Filters.character_with_permanent_character_weapon));
    private static final Filter DEFERRABLE_CARD = Filters.or(Filters.character,
            Filters.and(Filters.starship, Filters.not(Icon.INDEPENDENT)));

    public Card1261_079() {
        super(Side.LIGHT, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Commando Training & K'lor'slug",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent(Title.Commando_Training, Uniqueness.UNIQUE),
                new TitleComponent(Title.Klorslug, Uniqueness.UNIQUE));
        setGameText("Deploy on table. If your character or non-[Independent Starship] starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). Rebels (except Jedi or [Permanent Weapon] characters) ignore location deploytment restrictions (and 'power -1') in their game text, apply their Endor deployment modifiers at any location, and are immune to Bad Feeling Have I. Your Interrupts that retrieve exactly 1 Force (except Corellian Retort) are not stacked on Do They Have A Code Clearance?. Cannot be canceled. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotBeCanceledModifier(self, self));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, QUALIFIED_REBEL),
                new IgnoresOwnPowerMinusOneInGameTextModifier(self, QUALIFIED_REBEL),
                new ApplyOwnLocationDeploymentModifiersAtAnyLocationModifier(self, QUALIFIED_REBEL, Filters.Endor_location),
                new RetrievalSourceMayNotBeStackedOnModifier(self, Filters.and(Filters.your(self), Filters.Interrupt,
                        Filters.not(Filters.title("Corellian Retort"))), 1, Filters.Do_They_Have_A_Code_Clearance, null),
                new ImmuneToTitleModifier(self, QUALIFIED_REBEL, Title.Bad_Feeling_Have_I),
                new ImmuneToDeployCostModifiersToLocationModifier(self, QUALIFIED_REBEL,
                        Filters.title(Title.Bad_Feeling_Have_I), Filters.location));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        var battle = game.getGameState().getBattleState();
        if (battle == null || battle.isCanceled() || battle.isReachedDamageSegment()
                || !TriggerConditions.isAboutToBeLost(game, result,
                    Filters.and(Filters.your(self), Filters.participatingInBattle, DEFERRABLE_CARD))) return List.of();
        var loss = (AboutToLoseCardFromTableResult) result;
        var card = loss.getCardToBeLost();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Defer the loss of " + GameUtils.getFullName(card) + " until the end of battle");
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                loss.getPreventableCardEffect().preventEffectOnCard(card);
                int key = self.getPermanentCardId();
                var data = self.getForRemainderOfGameData().get(key);
                if (data == null) {
                    data = new ForRemainderOfGameData(new ArrayList<PhysicalCard>());
                    self.getForRemainderOfGameData().put(key, data);
                    action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action, endBattleLosses(key)));
                }
                if (!data.getPhysicalCards().contains(card)) {
                    data.getPhysicalCards().add(card);
                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                            new ForfeitValueToUseWhenForfeitedModifier(self, card, 0), null));
                }
            }
        });
        return List.of(action);
    }

    private AbstractActionProxy endBattleLosses(int key) {
        return new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult result) {
                if (!TriggerConditions.battleEnded(game, result) && !TriggerConditions.battleCanceled(game, result)) return List.of();
                var source = game.findCardByPermanentId(key);
                if (source == null) return List.of();
                var data = source.getForRemainderOfGameData().get(key);
                if (data == null) return List.of();
                var pending = data.getPhysicalCards();
                pending.removeIf(card -> !Filters.onTable.accepts(game, card));
                if (pending.isEmpty()) return List.of();
                var action = new RequiredGameTextTriggerAction(source, source.getCardId(), GameTextActionId.OTHER_CARD_ACTION_2);
                action.setRepeatableTrigger(true);
                action.setText("Lose a card whose battle loss was deferred");
                action.appendTargeting(new ChooseCardOnTableEffect(action, game.getGameState().getCurrentPlayerId(),
                        "Choose a card whose battle loss was deferred", pending) {
                    @Override
                    protected void cardSelected(PhysicalCard card) {
                        pending.remove(card);
                        action.appendEffect(new LoseCardFromTableEffect(action, card, true));
                    }
                    @Override protected boolean getUseShortcut() { return true; }
                });
                return List.of(action);
            }
        };
    }
}
