package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.1 Tarkin's Orders (V). */
public class Card1261_049 extends AbstractUsedOrLostInterrupt {
    private static final GameTextActionId SEARCH = GameTextActionId.LEGACY__TARKINS_ORDERS__DEPLOY_OR_RETRIEVE;
    private static final Filter TARGETS = Filters.or(Filters.and(Filters.system, Filters.title(Title.Eriadu)),
            Filters.and(Filters.Imperial, Filters.abilityEqualTo(3), Filters.not(Filters.Veers)));

    public Card1261_049() {
        super(Side.DARK, 4, "Tarkin's Orders", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Put all sections on alert!'");
        setGameText("USED: If a battle just initiated at a site, target your Imperial of ability = 3 defending. Target's forfeit cannot be reduced, and draws one battle destiny if not able to otherwise. LOST: Use 1 Force to deploy from Reserve Deck (then reshuffle), or retrieve, Eriadu or an Imperial of ability = 3 (except Veers). (Immune to Sense.)");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                        EffectResult result, PhysicalCard self) {
        Filter defender = Filters.and(Filters.your(self), Filters.Imperial, Filters.abilityEqualTo(3),
                Filters.defendingBattle, Filters.canBeTargetedBy(self));
        if (!TriggerConditions.battleInitiatedAt(game, result, game.getOpponent(playerId), Filters.site)
                || !GameConditions.canTarget(game, self, defender))
            return null;

        var action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Protect defending Imperial and enable battle destiny");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose defending Imperial", defender) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard target = action.getPrimaryTargetCard(targetGroupId);
                        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                new MayNotHaveForfeitValueReducedModifier(self, target),
                                GameUtils.getCardLink(target) + "'s forfeit cannot be reduced"));
                        action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, target, 1),
                                GameUtils.getCardLink(target) + " draws one battle destiny if unable otherwise"));
                    }
                });
            }
        });
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1))
            return null;

        List<PlayInterruptAction> actions = new ArrayList<>();
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, SEARCH, false)) {
            var deploy = paidLostAction(game, self, playerId, "Deploy Eriadu or an eligible Imperial from Reserve Deck");
            deploy.allowResponses(new RespondablePlayCardEffect(deploy) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    deploy.appendEffect(new DeployCardFromReserveDeckEffect(deploy, TARGETS, true));
                }
            });
            actions.add(deploy);
        }

        if (GameConditions.canSearchLostPile(game, playerId, self, SEARCH)
                && Filters.canSpot(game.getGameState().getLostPile(playerId), game, TARGETS)) {
            var retrieve = paidLostAction(game, self, playerId, "Retrieve Eriadu or an eligible Imperial");
            retrieve.allowResponses(new RespondablePlayCardEffect(retrieve) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    retrieve.appendEffect(new RetrieveCardEffect(retrieve, playerId, TARGETS));
                }
            });
            actions.add(retrieve);
        }
        return actions;
    }

    private PlayInterruptAction paidLostAction(SwccgGame game, PhysicalCard self, String playerId, String text) {
        var action = new PlayInterruptAction(game, self, SEARCH, CardSubtype.LOST);
        action.setText(text);
        action.setImmuneTo(Title.Sense);
        action.appendCost(new UseForceEffect(action, playerId, 1));
        return action;
    }
}
