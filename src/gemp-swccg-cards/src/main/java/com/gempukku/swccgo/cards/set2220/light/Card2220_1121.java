package com.gempukku.swccgo.cards.set2220.light;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndStackEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Evacuation Control
 * PC-E1 version of 1220_121 (PC card list / AR 2.0, 2012): "may take a card here into hand"; same play as the other texts. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1121 extends AbstractNormalEffect {
    public Card2220_1121() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Evacuation_Control, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Give the evacuation code signal...and get to your transports!'");
        setGameText("Deploy on table; shuffle your Reserve Deck, peek at top three cards, and stack them face-up here. During battle, may take a card here into hand to prevent all battle destiny draws from being modified or canceled (each player may draw no more than one battle destiny). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_121", "PC-E1",
                "PC website card list (by 2012-07-21) and AR 2.0 Appendix A (by 2012-08-27); superseded by the Virtual Cards Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndStackEffect(action, playerId, 3, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take stacked card into hand");
            action.setActionMsg("Prevent all battle destiny draws from being modified or canceled");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendCost(
                    new TakeStackedCardIntoHandEffect(action, playerId, self));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotModifyBattleDestinyModifier(self), "Prevents all battle destiny draws from being modified"));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotCancelBattleDestinyModifier(self), "Prevents all battle destiny draws from being canceled"));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, playerId, 1));
            action.appendEffect(
                    new DrawsNoMoreThanBattleDestinyEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
