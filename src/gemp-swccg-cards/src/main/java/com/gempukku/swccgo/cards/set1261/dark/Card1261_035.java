package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/** Legacy Block 6.1 He's No Jedi.
 * Text: PC-E3, the Virtual Cards Master List (2013-04-27; the PC card image, PC-E2, came first), which repeats the release-sheet words (no 'swing' limit); the
 * PC card list (Holotable data) text (PC-E1, 2010) is version 2261_1035 (Bill 2026-11-11: every publication is a version).
 */
public class Card1261_035 extends AbstractStartingEffect {
    public Card1261_035() {
        super(Side.DARK, 5, "He's No Jedi", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'He's no Jedi.'");
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force drains. Cloud City Occupation is canceled.");
        addIcons(Icon.JABBAS_PALACE);
    }

    /** Master List (PC-E3) and 2009 sheet: no swing clause; the card list (PC-E1, 2261_1035) adds "or 'swing' more than once per battle". */
    protected boolean limitsLightsaberSwings() {
        return false;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var actions = new ArrayList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.justDeployed(game, result, self)) {
            var setup = new RequiredGameTextTriggerAction(self, sourceId);
            setup.skipInitialMessageAndAnimation();
            setup.appendEffect(new StackCardsFromOutsideDeckEffect(setup, self.getOwner(), 5, 5, self, Filters.any));
            actions.add(setup);
        }
        if (TriggerConditions.isTableChanged(game, result)) {
            if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)
                    && GameConditions.canSpot(game, self, Filters.This_Deal_Is_Getting_Worse_All_The_Time)) {
                var action = new RequiredGameTextTriggerAction(self, sourceId);
                action.appendEffect(new SetForRemainderOfGameDataEffect(action, self, new ForRemainderOfGameData()));
                // Lightsaber cards and characters whose permanent weapon is a lightsaber (a permanent
                // weapon's Force drain bonus and firing limit are tracked on the character card).
                Filter lightsabers = Filters.or(Filters.lightsaber, Filters.hasPermanentWeapon(Filters.lightsaber));
                action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                        new CancelForceDrainBonusesFromCardModifier(self, lightsabers),
                        "Lightsabers may not add to Force drains for the remainder of the game"));
                if (limitsLightsaberSwings())
                    action.appendEffect(new AddUntilEndOfGameModifierEffect(action,
                            new MayFireOnlyOncePerBattleModifier(self, lightsabers),
                            "Lightsabers may not 'swing' more than once per battle for the remainder of the game"));
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Cloud_City_Occupation)) {
                var action = new RequiredGameTextTriggerAction(self, sourceId);
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Cloud_City_Occupation, Title.Cloud_City_Occupation);
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Cloud_City_Occupation)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect))
            return null;
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__HES_NO_JEDI__PLAY_SHIELD;
        var filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
        if (!GameConditions.isTwicePerGame(game, self, id) || !GameConditions.hasStackedCards(game, self, filter))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Play a Defensive Shield");
        action.appendUsage(new TwicePerGameEffect(action));
        action.appendTargeting(new ChooseStackedCardEffect(action, playerId, self, filter) {
            @Override
            protected void cardSelected(PhysicalCard selectedCard) {
                action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
            }
        });
        return List.of(action);
    }
}
