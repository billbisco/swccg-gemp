package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Final Legacy master list, page 34; full card supplies unchanged destiny 5. */
public class Card1261_095 extends AbstractUsedOrLostInterrupt {
    public Card1261_095() {
        super(Side.LIGHT, 5, Title.I_Dont_Need_Their_Scum_Either, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Of all the scum and villainy Lando had dealt with (pirates, smugglers, con-artists, thieves, swindlers, politicians and Imperial lackeys), he hated bounty hunters the most.");
        setGameText("USED: Cancel Lando System?. OR If Lando piloting Falcon, it draws two battle destiny if not able to otherwise. LOST: Retrieve your just lost gambler (except Dash) into hand. OR Place a copy of Eject! Eject! deployed on table in your Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Lando_System))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        Filter falcon = Filters.and(Filters.Falcon, Filters.participatingInBattle, Filters.hasPiloting(self, Filters.Lando));
        if (GameConditions.isDuringBattleWithParticipant(game, falcon)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Falcon draws two battle destiny if unable to otherwise");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Falcon", falcon) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                    new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, action.getPrimaryTargetCard(targetGroupId), 2), null));
                        }
                    });
                }
            });
            actions.add(action);
        }
        Filter eject = Filters.title(Title.Eject_Eject);
        if (GameConditions.canTarget(game, self, eject)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place Eject! Eject! in Used Pile");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Eject! Eject!", eject) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if (TriggerConditions.justLost(game, result, Filters.and(Filters.your(self), Filters.gambler, Filters.not(Filters.Dash)))) {
            PhysicalCard gambler = ((LostFromTableResult) result).getCard();
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Retrieve " + GameUtils.getFullName(gambler) + " into hand");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action, playerId, gambler, false, true));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
