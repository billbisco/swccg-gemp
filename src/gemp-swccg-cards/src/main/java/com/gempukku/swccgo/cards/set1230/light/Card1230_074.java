package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Lieutenant Blount
 */
public class Card1230_074 extends AbstractRebel {
    public Card1230_074() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Lieutenant_Blount, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wingman of Colonel Cracken. Spy and scout. Former agent of the Imperial Security Bureau. Defected and joined Rebel Intelligence. Seasoned combat veteran.");
        setGameText("Adds 2 to power of anything he pilots. Double Agent is a Used Interrupt. Once per game: may take Double Agent into hand from Reserve Deck; reshuffle. OR if you just targeted with Double Agent, may add 2 to your total destiny. OR may target opponent's bounty hunter with Double Agent instead of their spy.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.SPY, Keyword.SCOUT);
        addPersona(Persona.BLOUNT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new UsedInterruptModifier(self, Filters.Double_Agent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Double_Agent, ModifyGameTextType.LEGACY__BLOUNT_V__DOUBLE_AGENT_TARGETS_BOUNTY_HUNTER));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BLOUNT_V__TAKE_DOUBLE_AGENT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Double Agent into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Double_Agent, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BLOUNT_V__ADD_DESTINY;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Double_Agent, Filters.cardBeingPlayedTargeting(self, Filters.any)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 2 to total destiny");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new ModifyDestinyEffect(action, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
