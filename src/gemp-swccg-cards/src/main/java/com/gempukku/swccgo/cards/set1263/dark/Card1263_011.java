package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.List;

/** Legacy Block 6.3: The Emperor Is Coming Here? */
public class Card1263_011 extends AbstractNormalEffect {
    public Card1263_011() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Emperor Is Coming Here?", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLore("Though reluctant to leave Coruscant, Emperor Palpatine occasionally finds it necessary to personally put lagging Imperial operations back on schedule.");
        setGameText("Deploy on table. Once per game, if Emperor on table, may deploy a Royal Guard from Reserve Deck; reshuffle. You may play Force Lightning as follows: 'USED: During battle, target a character present with Emperor. Draw destiny. If destiny +3 > ability, target loses immunity to attrition and is power -2.' (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__THE_EMPEROR_IS_COMING_HERE__DEPLOY_ROYAL_GUARD;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canSpot(game, self, Filters.Emperor)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Deploy a Royal Guard from Reserve Deck");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Royal_Guard, true));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new LegacyMayPlayInterruptAsModifier(self, Filters.and(Filters.your(self), Filters.Force_Lightning),
                CardSubtype.USED, new LegacyMayPlayInterruptAsModifier.PlayMode() {
            @Override
            public PlayInterruptAction createAction(String playerId, SwccgGame game, PhysicalCard source,
                                                    PhysicalCard interrupt) {
                return forceLightning(playerId, game, interrupt);
            }
        }));
    }

    private static PlayInterruptAction forceLightning(String playerId, SwccgGame game, PhysicalCard interrupt) {
        var eligible = Filters.and(Filters.character, Filters.presentWith(interrupt, Filters.Emperor));
        if (!GameConditions.isDuringBattle(game) || !GameConditions.canTarget(game, interrupt, eligible)) return null;
        var action = new PlayInterruptAction(game, interrupt, CardSubtype.USED);
        action.setText("Force Lightning: Draw destiny to reduce power and cancel immunity to attrition");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose character present with Emperor", eligible) {
            @Override
            protected void cardTargeted(int group, PhysicalCard target) {
                action.addAnimationGroup(target);
                action.allowResponses("Target " + GameUtils.getCardLink(target) + " with Force Lightning",
                        new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        var finalTarget = action.getPrimaryTargetCard(group);
                        action.appendEffect(new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return List.of(finalTarget);
                            }

                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> cards, List<Float> draws,
                                                        Float total) {
                                if (total != null && total + 3 > game.getModifiersQuerying()
                                        .getAbility(game.getGameState(), finalTarget)) {
                                    action.appendEffect(new CancelImmunityToAttritionUntilEndOfBattleEffect(action, finalTarget,
                                            "Cancels " + GameUtils.getCardLink(finalTarget) + "'s immunity to attrition"));
                                    action.appendEffect(new ModifyPowerUntilEndOfBattleEffect(action, finalTarget, -2));
                                }
                            }
                        });
                    }
                });
            }
        });
        return action;
    }
}
