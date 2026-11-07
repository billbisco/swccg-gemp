package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Jabba, This Is Your Last Chance!, final Light master page 25. */
public class Card1261_080 extends AbstractStartingEffect {
    private static final GameTextActionId PLAY_SHIELD = GameTextActionId.LEGACY__JABBA_LAST_CHANCE__PLAY_SHIELD;

    public Card1261_080() {
        super(Side.LIGHT, 5, "Jabba, This Is Your Last Chance!", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains or 'swing' more than once per battle. Cloud City Celebration is canceled.");
        setLore("With a quick motion of his hand, Luke signaled Artoo and dramatically changed the situation.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition qmcOrIo = new OnTableCondition(self, Filters.or(Filters.Quiet_Mining_Colony, Filters.Independent_Operation));
        return List.of(new CancelForceDrainBonusesFromCardModifier(self, Filters.lightsaber, qmcOrIo),
                new MayFireOnlyOncePerBattleModifier(self,
                        Filters.or(Filters.lightsaber, Filters.hasPermanentWeapon(Filters.lightsaber)), qmcOrIo));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, result, self)) {
            RequiredGameTextTriggerAction stack = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            stack.skipInitialMessageAndAnimation();
            stack.appendEffect(new StackCardsFromOutsideDeckEffect(stack, self.getOwner(), 5, 5, self, Filters.any));
            return List.of(stack);
        }
        if (TriggerConditions.isTableChanged(game, result) && GameConditions.canTargetToCancel(game, self, Filters.Cloud_City_Celebration)) {
            RequiredGameTextTriggerAction cancel = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(cancel, Filters.Cloud_City_Celebration, Title.Cloud_City_Celebration);
            return List.of(cancel);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Cloud_City_Celebration)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter shields = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
        if (!GameConditions.isTwicePerGame(game, self, PLAY_SHIELD)
                || !GameConditions.hasStackedCards(game, self, shields)) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, PLAY_SHIELD);
        action.setText("Play a Defensive Shield");
        action.appendUsage(new TwicePerGameEffect(action));
        action.appendTargeting(new ChooseStackedCardEffect(action, playerId, self, shields) {
            @Override
            protected void cardSelected(PhysicalCard card) {
                action.appendEffect(new PlayStackedCardEffect(action, self, card));
            }
        });
        return List.of(action);
    }
}
